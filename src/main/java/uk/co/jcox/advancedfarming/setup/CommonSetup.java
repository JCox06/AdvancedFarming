package uk.co.jcox.advancedfarming.setup;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class CommonSetup {

    public static final String TAB_NAME = "Advanced Farming";

    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab(TAB_NAME) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Registration.BASE_STATION_BLOCK.get());
        }
    };
}
