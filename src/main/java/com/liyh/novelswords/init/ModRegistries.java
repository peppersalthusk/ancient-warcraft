package com.liyh.AncientWarcraft.init;

import com.liyh.AncientWarcraft.AncientWarcraft;
import com.liyh.AncientWarcraft.entity.SoulFireball;
import com.liyh.AncientWarcraft.item.BlazeSwordItem;
import com.liyh.AncientWarcraft.item.MagmaSwordItem;
import com.liyh.AncientWarcraft.item.SoulBladeItem;
import com.liyh.AncientWarcraft.item.SoulChargeItem;
import com.liyh.AncientWarcraft.item.SoulUpgradeSmithingTemplateItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModRegistries {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, AncientWarcraft.MOD_ID);

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, AncientWarcraft.MOD_ID);

    public static final RegistryObject<MagmaSwordItem> MAGMA_SWORD =
            ITEMS.register("magma_sword", MagmaSwordItem::new);

    public static final RegistryObject<BlazeSwordItem> BLAZE_SWORD =
            ITEMS.register("blaze_sword", BlazeSwordItem::new);

    public static final RegistryObject<SoulBladeItem> SOUL_BLADE =
            ITEMS.register("soul_blade", SoulBladeItem::new);

    public static final RegistryObject<SoulChargeItem> SOUL_CHARGE =
            ITEMS.register("soul_charge", SoulChargeItem::new);

    public static final RegistryObject<Item> PURIFIED_SOUL =
            ITEMS.register("purified_soul", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ENERGY_CELL =
            ITEMS.register("energy_cell", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ANCIENT_CORE =
            ITEMS.register("ancient_core", () -> new Item(new Item.Properties().stacksTo(4)));

    public static final RegistryObject<SoulUpgradeSmithingTemplateItem> SOUL_UPGRADE_SMITHING_TEMPLATE =
            ITEMS.register("soul_upgrade_smithing_template", SoulUpgradeSmithingTemplateItem::new);

    public static final RegistryObject<EntityType<SoulFireball>> SOUL_FIREBALL = ENTITIES.register(
            "soul_fireball",
            () -> EntityType.Builder.<SoulFireball>of(SoulFireball::new, MobCategory.MISC)
                    .sized(0.3125F, 0.3125F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("soul_fireball"));

    private ModRegistries() {}

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
        ENTITIES.register(modBus);
    }
}
