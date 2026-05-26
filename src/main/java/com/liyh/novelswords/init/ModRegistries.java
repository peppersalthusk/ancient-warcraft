package com.liyh.novelswords.init;

import com.liyh.novelswords.NovelSwords;
import com.liyh.novelswords.item.MagmaSwordItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModRegistries {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, NovelSwords.MOD_ID);

    public static final RegistryObject<MagmaSwordItem> MAGMA_SWORD =
            ITEMS.register("magma_sword", MagmaSwordItem::new);

    private ModRegistries() {}

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }
}
