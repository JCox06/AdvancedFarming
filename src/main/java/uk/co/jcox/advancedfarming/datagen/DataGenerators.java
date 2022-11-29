package uk.co.jcox.advancedfarming.datagen;


import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import uk.co.jcox.advancedfarming.AdvancedFarming;

@Mod.EventBusSubscriber(modid = AdvancedFarming.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();

        AFBlockTagProvider BTProvider = new AFBlockTagProvider(generator, fileHelper);
        generator.addProvider(event.includeServer(), BTProvider);
        generator.addProvider(event.includeServer(), new AFItemTagProvider(generator, BTProvider, fileHelper));
    }
}
