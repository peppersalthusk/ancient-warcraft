package com.liyh.AncientWarcraft.event;

import com.liyh.AncientWarcraft.AncientWarcraft;
import com.liyh.AncientWarcraft.dispenser.SoulChargeDispenseBehavior;
import com.liyh.AncientWarcraft.init.ModRegistries;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.common.util.MutableHashedLinkedMap;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = AncientWarcraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModEventHandlers {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final CreativeModeTab.TabVisibility TAB_VISIBILITY =
            CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS;

    private ModEventHandlers() {}

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("{} common setup", AncientWarcraft.MOD_ID);
        event.enqueueWork(() -> DispenserBlock.registerBehavior(
                ModRegistries.SOUL_CHARGE.get(), new SoulChargeDispenseBehavior()));
    }

    @SubscribeEvent
    public static void buildCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> entries = event.getEntries();

        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            insertAfter(entries, Items.STONE_SWORD, ModRegistries.MAGMA_SWORD.get());
            insertAfter(entries, Items.DIAMOND_SWORD, ModRegistries.BLAZE_SWORD.get());
            insertAfter(entries, ModRegistries.BLAZE_SWORD.get(), ModRegistries.SOUL_BLADE.get());
            insertAfter(entries, ModRegistries.SOUL_BLADE.get(), ModRegistries.ENCAPSULATED_SOUL.get());
            insertAfter(entries, ModRegistries.ENCAPSULATED_SOUL.get(), ModRegistries.SWORD_OF_CENTURION.get());
        }
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            insertAfter(entries, Items.FIRE_CHARGE, ModRegistries.SOUL_CHARGE.get());
            insertAfter(entries, ModRegistries.SOUL_CHARGE.get(), ModRegistries.PURIFIED_SOUL.get());
            insertAfter(entries, ModRegistries.PURIFIED_SOUL.get(), ModRegistries.ENERGY_CELL.get());
            insertAfter(entries, ModRegistries.ENERGY_CELL.get(), ModRegistries.ANCIENT_CORE.get());
            insertAfter(entries, Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, ModRegistries.SOUL_UPGRADE_SMITHING_TEMPLATE.get());
        }
    }

    private static void insertAfter(
            MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> entries,
            Item anchorItem,
            ItemStack modStack) {
        entries.putAfter(findReferenceStack(entries, anchorItem), modStack, TAB_VISIBILITY);
    }

    private static void insertAfter(
            MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> entries,
            Item anchorItem,
            Item modItem) {
        insertAfter(entries, anchorItem, new ItemStack(modItem));
    }

    private static ItemStack findReferenceStack(
            MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> entries, Item item) {
        for (var entry : entries) {
            if (entry.getKey().is(item)) {
                return entry.getKey();
            }
        }
        return new ItemStack(item);
    }
}
