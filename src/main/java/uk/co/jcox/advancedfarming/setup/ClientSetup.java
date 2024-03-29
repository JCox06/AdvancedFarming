package uk.co.jcox.advancedfarming.setup;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import uk.co.jcox.advancedfarming.AdvancedFarming;
import uk.co.jcox.advancedfarming.client.render.gui.WoodGeneratorScreen;
import uk.co.jcox.advancedfarming.client.render.block.PlantVesselTileRenderer;

@Mod.EventBusSubscriber(modid = AdvancedFarming.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    public static void init(FMLClientSetupEvent event) {
        MenuScreens.register(Registration.WOOD_GENERATOR_CONTAINER.get(), WoodGeneratorScreen::new);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(Registration.PLANT_VESSEL_BE.get(), PlantVesselTileRenderer::new);
    }
}
