package uk.co.jcox.advancedfarming.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import uk.co.jcox.advancedfarming.be.PlantVesselBE;

public class PlantVesselTileRenderer implements BlockEntityRenderer<PlantVesselBE> {

    private final BlockEntityRendererProvider.Context context;
    private final BlockRenderDispatcher dispatcher;

    public PlantVesselTileRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
        this.dispatcher = this.context.getBlockRenderDispatcher();
    }

    @Override
    public void render(PlantVesselBE tile, float ticks, PoseStack matrixStack, MultiBufferSource buffer,
                       int combinedOverlay, int packedLight) {

        BlockState state = tile.getIncubatingState();

        if (state != null) {
            matrixStack.pushPose();
            matrixStack.scale(0.5f, 0.5f, 0.5f);
            matrixStack.translate(0.5, 0.2, 0.5);
            this.dispatcher.renderSingleBlock(state, matrixStack, buffer, combinedOverlay, packedLight, ModelData.EMPTY, RenderType.cutout());
            matrixStack.popPose();
        }
    }
}
