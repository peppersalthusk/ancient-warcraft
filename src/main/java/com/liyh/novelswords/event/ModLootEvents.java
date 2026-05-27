package com.liyh.AncientWarcraft.event;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.liyh.AncientWarcraft.AncientWarcraft;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;

@Mod.EventBusSubscriber(modid = AncientWarcraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ModLootEvents {
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

    private ModLootEvents() {}

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        ResourceLocation id = event.getName();
        if (BASTION_TABLES.contains(id)) {
            event.setTable(patchLootTable(event.getTable(), true));
        } else if (EXTRA_TABLES.contains(id)) {
            event.setTable(patchLootTable(event.getTable(), false));
        }
    }

    private static LootTable patchLootTable(LootTable table, boolean bastionStyle) {
        JsonElement encoded = LootTable.DIRECT_CODEC.encodeStart(JsonOps.INSTANCE, table).getOrThrow();
        JsonObject root = encoded.getAsJsonObject();
        if (!root.has("pools")) {
            return table;
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

        return LootTable.DIRECT_CODEC.parse(JsonOps.INSTANCE, root).getOrThrow();
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
