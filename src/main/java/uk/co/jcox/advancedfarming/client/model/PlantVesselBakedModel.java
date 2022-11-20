package uk.co.jcox.advancedfarming.client.model;

import com.mojang.math.Matrix4f;
import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.model.QuadTransformers;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import uk.co.jcox.advancedfarming.block.PlantVessel;
import uk.co.jcox.advancedfarming.util.ClientTools;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static uk.co.jcox.advancedfarming.util.ClientTools.v;

public class PlantVesselBakedModel implements IDynamicBakedModel {

    private final ModelState modelState;
    private final Function<Material, TextureAtlasSprite> spriteGetter;
    private List<BakedQuad> quads = new ArrayList<>();
    private final ItemOverrides overrides;
    private final ItemTransforms itemTransforms;


    public PlantVesselBakedModel(ModelState modelState, Function<Material, TextureAtlasSprite> spriteGetter, ItemOverrides overrides, ItemTransforms itemTransforms) {
        this.modelState = modelState;
        this.spriteGetter = spriteGetter;
        this.overrides = overrides;
        this.itemTransforms = itemTransforms;

        generateQuadCache();
    }


    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand, @Nonnull ModelData extraData, @Nullable RenderType layer) {

        if (state == null) {
            return new ArrayList<>(quads);
        }

        if ((side != null || (layer == null))) {
            return Collections.emptyList();
        }

        if (layer.equals(RenderType.translucent())) {
            return new ArrayList<>(quads);
        }

        if (layer.equals(RenderType.cutout())) {
            return getQuadsOfIncubatingBlock(state, rand, extraData, layer);
        }

        return Collections.emptyList();

//        if (layer != null) {
//            return getQuadsOfIncubatingBlock(state, rand, extraData, layer);
//        }
//
//        return Collections.emptyList();
    }

    private void generateQuadCache() {
        Transformation rotation = modelState.getRotation();

        TextureAtlasSprite textureSide = spriteGetter.apply(PlantVesselModelLoader.MATERIAL_SIDE);
        TextureAtlasSprite textureBase = spriteGetter.apply(PlantVesselModelLoader.MATERIAL_BOTTOM);
        TextureAtlasSprite textureOutsideBase = spriteGetter.apply(PlantVesselModelLoader.MATERIAL_BASE);
        TextureAtlasSprite textureBottomBase = spriteGetter.apply(PlantVesselModelLoader.MATERIAL_BOTTOM_BASE);

        float l = 2f / 16f;
        float r = 14f / 16f;

        float a = 1;
        float b = 0;

        //Base
        var quads = new ArrayList<BakedQuad>();
        quads.add(ClientTools.createQuad(v(r, r, r), v(r, r, l), v(l, r, l), v(l, r, r), rotation, textureSide));      // Top side
        quads.add(ClientTools.createQuad(v(a, l, a), v(a, l, b), v(b, l, b), v(b, l, a), rotation, textureBase));      // Inside texture
        quads.add(ClientTools.createQuad(v(r, r, r), v(r, l, r), v(r, l, l), v(r, r, l), rotation, textureSide));
        quads.add(ClientTools.createQuad(v(l, r, l), v(l, l, l), v(l, l, r), v(l, r, r), rotation, textureSide));
        quads.add(ClientTools.createQuad(v(r, r, l), v(r, l, l), v(l, l, l), v(l, r, l), rotation, textureSide));
        quads.add(ClientTools.createQuad(v(l, r, r), v(l, l, r), v(r, l, r), v(r, r, r), rotation, textureSide));


        quads.add(ClientTools.createQuad(v(b, b, b), v(a, b, b), v(a, b, a), v(b, b, a), rotation, textureOutsideBase));       //Outside

        quads.add(ClientTools.createQuad(v(b, l, b), v(b, b, b), v(b, b, a), v(b, l, a), rotation, textureBottomBase));
        quads.add(ClientTools.createQuad(v(a, l, b), v(a, b, b), v(b, b, b), v(b, l, b), rotation, textureBottomBase));
        quads.add(ClientTools.createQuad(v(b, l, a), v(b, b, a), v(a, b, a), v(a, l, a), rotation, textureBottomBase));
        quads.add(ClientTools.createQuad(v(a, l, a), v(a, b, a), v(a, b, b), v(a, l, b), rotation, textureBottomBase));

        this.quads = quads;
    }


    private List<BakedQuad> getQuadsOfIncubatingBlock(@Nullable BlockState state, @NotNull RandomSource rand, @Nullable ModelData extraData, RenderType layer) {
        List<BakedQuad> incubatingBlockQuads = new ArrayList<>();

//        BlockState crop = Blocks.WHEAT.defaultBlockState().setValue(CropBlock.AGE, 7);
        BlockState crop = Blocks.SWEET_BERRY_BUSH.defaultBlockState();


        if (crop != null && !(crop.getBlock() instanceof PlantVessel)) {
            if (layer == null || getRenderTypes(crop, rand, ModelData.EMPTY).contains(layer)) {

                BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getBlockModel(crop);

                try {
                    Transformation translate = new Transformation(Matrix4f.createScaleMatrix(0.5f, 0.5f, 0.5f));
                    translate = translate.compose(new Transformation(Matrix4f.createTranslateMatrix(8f/16, 5/16f, 8f /16)));
                    IQuadTransformer transformer = QuadTransformers.applying(translate);

                    //Get the quad of every side, transform it, and add it to the list of quads to render
                    //Don't give direction to force all sides of crop to render
                    List<BakedQuad> modelQuads = model.getQuads(crop, null, rand, ModelData.EMPTY, layer);
                    for (BakedQuad quad : modelQuads) {
                        incubatingBlockQuads.add(transformer.process(quad));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return incubatingBlockQuads;
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
        return ChunkRenderTypeSet.of(RenderType.translucent(), RenderType.cutout());
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this.spriteGetter.apply(PlantVesselModelLoader.MATERIAL_SIDE);
    }

    @Override
    public ItemOverrides getOverrides() {
        return this.overrides;
    }

    @Override
    public ItemTransforms getTransforms() {
        return this.itemTransforms;
    }
}