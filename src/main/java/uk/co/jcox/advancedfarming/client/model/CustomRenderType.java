package uk.co.jcox.advancedfarming.client.model;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.client.event.ScreenEvent;

public class CustomRenderType extends RenderType {

    public CustomRenderType(String p_173178_, VertexFormat p_173179_, VertexFormat.Mode p_173180_, int p_173181_,
                            boolean p_173182_, boolean p_173183_, Runnable p_173184_, Runnable p_173185_) {
        super(p_173178_, p_173179_, p_173180_, p_173181_, p_173182_, p_173183_, p_173184_, p_173185_);
    }

    public static CompositeState addState(ShaderStateShard shard) {
        return CompositeState.builder()
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setShaderState(shard)
                .setTextureState(BLOCK_SHEET_MIPPED)
                .setTransparencyState(RenderStateShard.ADDITIVE_TRANSPARENCY)
                .setOutputState(RenderStateShard.TRANSLUCENT_TARGET)
                .createCompositeState(true);
    }


    public static final RenderType ADD = create("translucent",
            DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 2097152, true, true, addState(RenderStateShard.RENDERTYPE_TRANSLUCENT_SHADER));
}
