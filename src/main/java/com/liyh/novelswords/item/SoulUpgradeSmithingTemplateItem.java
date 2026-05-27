package com.liyh.AncientWarcraft.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.flag.FeatureFlag;

public class SoulUpgradeSmithingTemplateItem extends SmithingTemplateItem {
    private static final SmithingTemplateItem NETHERITE_TEMPLATE =
            (SmithingTemplateItem) Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE;

    public SoulUpgradeSmithingTemplateItem() {
        super(
                Component.translatable("upgrade.ancient_warcraft.soul_upgrade.applies_to"),
                Component.translatable("upgrade.ancient_warcraft.soul_upgrade.ingredients"),
                Component.translatable("upgrade.ancient_warcraft.soul_upgrade.description"),
                Component.translatable("upgrade.ancient_warcraft.soul_upgrade.base"),
                Component.translatable("upgrade.ancient_warcraft.soul_upgrade.additions"),
                NETHERITE_TEMPLATE.getBaseSlotEmptyIcons(),
                NETHERITE_TEMPLATE.getAdditionalSlotEmptyIcons(),
                new FeatureFlag[0]);
    }
}
