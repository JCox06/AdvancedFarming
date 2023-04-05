package uk.co.jcox.advancedfarming.setup;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommonSetup {

    public static final String TAB_NAME = "Advanced Farming";

    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab(TAB_NAME) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Registration.PLANT_VESSEL.get());
        }
    };

    public static void init(FMLCommonSetupEvent event) {

    }
}
