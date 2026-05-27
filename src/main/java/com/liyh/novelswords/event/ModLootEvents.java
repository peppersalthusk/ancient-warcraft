package com.liyh.AncientWarcraft.event;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.liyh.AncientWarcraft.AncientWarcraft;
import com.liyh.AncientWarcraft.util.RegistryAccessHelper;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;

import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = AncientWarcraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ModLootEvents {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final FileToIdConverter LOOT_TABLE_LISTER = new FileToIdConverter("loot_table", ".json");

    private static final String NETHERITE_UPGRADE_TEMPLATE = "minecraft:netherite_upgrade_smithing_template";
    private static final String SOUL_UPGRADE_TEMPLATE = AncientWarcraft.MOD_ID + ":soul_upgrade_smithing_template";

    private static final Set<ResourceLocation> BASTION_TABLES = Set.of(
            ResourceLocation.withDefaultNamespace("chests/bastion_treasure"),
            ResourceLocation.withDefaultNamespace("chests/bastion_bridge"),
            ResourceLocation.withDefaultNamespace("chests/bastion_hoglin_stable"),
            ResourceLocation.withDefaultNamespace("chests/bastion_other"));

    private static final Set<ResourceLocation> EXTRA_TABLES = Set.of(
            ResourceLocation.withDefaultNamespace("chests/ancient_city"),
            ResourceLocation.withDefaultNamespace("chests/nether_bridge"));

    private static final Map<ResourceLocation, Boolean> PENDING_PATCHES = new ConcurrentHashMap<>();

    private ModLootEvents() {}

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        ResourceLocation id = event.getName();
        boolean bastionStyle = BASTION_TABLES.contains(id);
        if (!bastionStyle && !EXTRA_TABLES.contains(id)) {
            return;
        }

        Optional<ResourceManager> resourceManager = resolveResourceManager();
        if (resourceManager.isEmpty()) {
            PENDING_PATCHES.put(id, bastionStyle);
            return;
        }

        Optional<RegistryAccess> access = RegistryAccessHelper.resolve();
        if (access.isEmpty()) {
            PENDING_PATCHES.put(id, bastionStyle);
            return;
        }

        tryPatchAndApply(event, id, resourceManager.get(), access.get(), bastionStyle);
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        if (PENDING_PATCHES.isEmpty()) {
            return;
        }

        MinecraftServer server = event.getServer();
        RegistryAccess access = server.registryAccess();
        ResourceManager resourceManager = server.getResourceManager();

        for (Map.Entry<ResourceLocation, Boolean> entry : PENDING_PATCHES.entrySet()) {
            ResourceLocation id = entry.getKey();
            try {
                Optional<LootTable> patched = tryPatchLootTable(id, resourceManager, access, entry.getValue());
                if (patched.isPresent()) {
                    injectLootTable(server, id, patched.get());
                }
            } catch (Exception exception) {
                LOGGER.warn("Deferred loot patch failed for {}", id, exception);
            }
        }
        PENDING_PATCHES.clear();
    }

    private static void tryPatchAndApply(
            LootTableLoadEvent event,
            ResourceLocation id,
            ResourceManager resourceManager,
            RegistryAccess access,
            boolean bastionStyle) {
        try {
            Optional<LootTable> patched = tryPatchLootTable(id, resourceManager, access, bastionStyle);
            patched.ifPresent(event::setTable);
        } catch (Exception exception) {
            LOGGER.warn("Loot patch failed for {}, keeping vanilla table", id, exception);
            PENDING_PATCHES.put(id, bastionStyle);
        }
    }

    private static Optional<LootTable> tryPatchLootTable(
            ResourceLocation id,
            ResourceManager resourceManager,
            RegistryAccess access,
            boolean bastionStyle) {
        Optional<JsonObject> root = loadLootTableJson(id, resourceManager);
        if (root.isEmpty()) {
            return Optional.empty();
        }
        patchLootJson(root.get(), bastionStyle);
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, access);
        return LootTable.DIRECT_CODEC.parse(ops, root.get()).result();
    }

    private static void injectLootTable(MinecraftServer server, ResourceLocation id, LootTable table) {
        Registry<LootTable> registry = server.registryAccess().registryOrThrow(Registries.LOOT_TABLE);
        ResourceKey<LootTable> key = ResourceKey.create(Registries.LOOT_TABLE, id);
        registry.getHolder(key).ifPresent(holder -> bindLootTableHolder(holder, table, id));
    }

    private static void bindLootTableHolder(Holder<LootTable> holder, LootTable table, ResourceLocation id) {
        if (!(holder instanceof Holder.Reference<LootTable> reference)) {
            return;
        }
        try {
            Method bindValue = Holder.Reference.class.getDeclaredMethod("bindValue", Object.class);
            bindValue.setAccessible(true);
            bindValue.invoke(reference, table);
        } catch (ReflectiveOperationException exception) {
            LOGGER.warn("Unable to inject loot table {}", id, exception);
        }
    }

    private static Optional<ResourceManager> resolveResourceManager() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            return Optional.of(server.getResourceManager());
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft != null) {
            return Optional.of(minecraft.getResourceManager());
        }
        return Optional.empty();
    }

    private static Optional<JsonObject> loadLootTableJson(ResourceLocation tableId, ResourceManager resourceManager) {
        ResourceLocation fileId = LOOT_TABLE_LISTER.idToFile(tableId);
        Optional<Resource> resource = resourceManager.getResource(fileId);
        if (resource.isEmpty()) {
            LOGGER.warn("Loot table json not found: {}", fileId);
            return Optional.empty();
        }
        try (InputStreamReader reader = new InputStreamReader(resource.get().open(), StandardCharsets.UTF_8)) {
            return Optional.of(JsonParser.parseReader(reader).getAsJsonObject());
        } catch (Exception exception) {
            LOGGER.warn("Failed to read loot table json {}", fileId, exception);
            return Optional.empty();
        }
    }

    private static void patchLootJson(JsonObject root, boolean bastionStyle) {
        if (!root.has("pools")) {
            return;
        }

        JsonArray pools = root.getAsJsonArray("pools");
        boolean foundNetherite = false;

        for (JsonElement poolElement : pools) {
            JsonObject pool = poolElement.getAsJsonObject();
            if (!pool.has("entries")) {
                continue;
            }

            JsonArray entries = pool.getAsJsonArray("entries");
            if (containsNetheriteTemplate(entries)) {
                foundNetherite = true;
                if (!poolHasSoulTemplate(entries)) {
                    for (JsonElement entryElement : entries) {
                        JsonObject entry = entryElement.getAsJsonObject();
                        if (isItemEntry(entry, NETHERITE_UPGRADE_TEMPLATE)) {
                            entries.add(createSoulTemplateEntry(copyWeight(entry)));
                            break;
                        }
                    }
                }

                if (isGuaranteedSingleNetheritePool(pool, entries)) {
                    pools.add(createGuaranteedSoulTemplatePool(pool));
                }
            }
        }

        if (!foundNetherite && !bastionStyle) {
            pools.add(createWeightedSoulTemplatePool());
        }
    }

    private static boolean containsNetheriteTemplate(JsonArray entries) {
        for (JsonElement entryElement : entries) {
            if (isItemEntry(entryElement.getAsJsonObject(), NETHERITE_UPGRADE_TEMPLATE)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isGuaranteedSingleNetheritePool(JsonObject pool, JsonArray entries) {
        if (entries.size() != 1) {
            return false;
        }
        if (!isItemEntry(entries.get(0).getAsJsonObject(), NETHERITE_UPGRADE_TEMPLATE)) {
            return false;
        }
        JsonElement rolls = pool.get("rolls");
        return rolls != null && rolls.isJsonPrimitive() && rolls.getAsJsonPrimitive().isNumber() && rolls.getAsDouble() >= 1.0;
    }

    private static JsonObject createGuaranteedSoulTemplatePool(JsonObject netheritePool) {
        JsonObject pool = new JsonObject();
        if (netheritePool.has("rolls")) {
            pool.add("rolls", netheritePool.get("rolls").deepCopy());
        } else {
            pool.addProperty("rolls", 1.0);
        }
        pool.addProperty("bonus_rolls", 0.0);
        JsonArray entries = new JsonArray();
        entries.add(createSoulTemplateEntry(1));
        pool.add("entries", entries);
        return pool;
    }

    private static boolean poolHasSoulTemplate(JsonArray entries) {
        for (JsonElement entryElement : entries) {
            if (isItemEntry(entryElement.getAsJsonObject(), SOUL_UPGRADE_TEMPLATE)) {
                return true;
            }
        }
        return false;
    }

    private static JsonObject createSoulTemplateEntry(int weight) {
        JsonObject entry = new JsonObject();
        entry.addProperty("type", "minecraft:item");
        entry.addProperty("name", SOUL_UPGRADE_TEMPLATE);
        if (weight != 1) {
            entry.addProperty("weight", weight);
        }
        return entry;
    }

    private static JsonObject createWeightedSoulTemplatePool() {
        JsonObject pool = new JsonObject();
        pool.addProperty("rolls", 1.0);
        pool.addProperty("bonus_rolls", 0.0);
        JsonArray entries = new JsonArray();
        JsonObject empty = new JsonObject();
        empty.addProperty("type", "minecraft:empty");
        empty.addProperty("weight", 9);
        entries.add(empty);
        entries.add(createSoulTemplateEntry(1));
        pool.add("entries", entries);
        return pool;
    }

    private static int copyWeight(JsonObject entry) {
        if (entry.has("weight")) {
            return entry.get("weight").getAsInt();
        }
        return 1;
    }

    private static boolean isItemEntry(JsonObject entry, String itemId) {
        return entry.has("type")
                && "minecraft:item".equals(entry.get("type").getAsString())
                && entry.has("name")
                && itemId.equals(entry.get("name").getAsString());
    }
}
