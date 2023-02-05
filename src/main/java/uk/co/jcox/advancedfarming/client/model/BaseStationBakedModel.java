package uk.co.jcox.advancedfarming.client.model;

import com.mojang.math.Matrix4f;
import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.model.QuadTransformers;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import uk.co.jcox.advancedfarming.block.BaseStationBlock;
import uk.co.jcox.advancedfarming.setup.Registration;
import uk.co.jcox.advancedfarming.be.BaseStationBE;
import uk.co.jcox.advancedfarming.util.ClientTools;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static java.lang.Boolean.TRUE;
import static uk.co.jcox.advancedfarming.util.ClientTools.v;

public class BaseStationBakedModel implements IDynamicBakedModel {

    private final ModelState modelState;
    private final Function<Material, TextureAtlasSprite> spriteGetter;
    private List<BakedQuad> quadCache;
    private final ItemOverrides overrides;
    private final ItemTransforms itemTransforms;


    public BaseStationBakedModel(ModelState modelState, Function<Material, TextureAtlasSprite> spriteGetter, ItemOverrides overrides, ItemTransforms itemTransforms) {
        this.modelState = modelState;
        this.spriteGetter = spriteGetter;
        this.overrides = overrides;
        this.itemTransforms = itemTransforms;

        generateQuadCache();
    }


    private void generateQuadCache() {
        quadCache = generateQuads();
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand, @Nonnull ModelData extraData, @Nullable RenderType layer) {


        boolean incubatorPresent = TRUE == extraData.get(BaseStationBE.INCUBATOR_PRESENT);

        if (state == null) {
            return quadCache;
        }

        if ((side != null || (layer == null))) {
            return Collections.emptyList();
        }

        if (layer.equals(RenderType.solid())) {
            return quadCache;
        }

        if (layer.equals(RenderType.translucent())) {

            List<BakedQuad> quads = new ArrayList();

            if(incubatorPresent) {
                BlockState plantVesselState = Registration.PLANT_VESSEL_BLOCK.get().defaultBlockState();
                Transformation translation = new Transformation(Matrix4f.createTranslateMatrix(0.0f, 1.0f, 0.0f));
                quads.addAll(getQuadsOfExternalBlock(plantVesselState, rand, layer, QuadTransformers.applying(translation)));
            }

            quads.addAll(quadCache);
            return quads;
        }

        if (layer.equals(RenderType.cutout())) {
            BlockState cropState = Blocks.WHEAT.defaultBlockState().setValue(CropBlock.AGE, 7);
            Transformation translation = new Transformation(Matrix4f.createTranslateMatrix(8f/16,2 + 2f/16, 8f/16));
            Transformation scale = new Transformation(Matrix4f.createScaleMatrix(0.5f, 0.5f, 0.5f));
            return getQuadsOfExternalBlock(cropState, rand, layer, QuadTransformers.applying(scale.compose(translation)));
        }

        return Collections.emptyList();
    }

    private List<BakedQuad> generateQuads() {
        Transformation rotation = modelState.getRotation();
        TextureAtlasSprite textureBaseStationSide = spriteGetter.apply(BaseStationModelLoader.MATERIAL_BASE_STATION_SIDE);
        TextureAtlasSprite textureBaseStationTop = spriteGetter.apply(BaseStationModelLoader.MATERIAL_BASE_STATION_TOP);

        var quads = new ArrayList<BakedQuad>();

        float a = 1;
        float b = 0;

        //Quads for the base station
        quads.add(ClientTools.createQuad(v(a, a, a), v(a, a, b), v(b, a, b), v(b, a, a), rotation, textureBaseStationTop));      // Top side
        quads.add(ClientTools.createQuad(v(a, b, a), v(a, b, b), v(b, b, b), v(b, b, a), rotation, textureBaseStationSide));      // Inside texture
        quads.add(ClientTools.createQuad(v(a, a, a), v(a, b, a), v(a, b, b), v(a, a, b), rotation, textureBaseStationSide));
        quads.add(ClientTools.createQuad(v(b, a, b), v(b, b, b), v(b, b, a), v(b, a, a), rotation, textureBaseStationSide));
        quads.add(ClientTools.createQuad(v(a, a, b), v(a, b, b), v(b, b, b), v(b, a, b), rotation, textureBaseStationSide));
        quads.add(ClientTools.createQuad(v(b, a, a), v(b, b, a), v(a, b, a), v(a, a, a), rotation, textureBaseStationSide));
        return quads;
    }


    private List<BakedQuad> getQuadsOfExternalBlock(@Nullable BlockState blockState, @NotNull RandomSource rand, RenderType layer, IQuadTransformer transformer) {
        List<BakedQuad> blockStateQuads = new ArrayList<>();



        if (blockState != null && !(blockState.getBlock() instanceof BaseStationBlock)) {
            if (layer == null || getRenderTypes(blockState, rand, ModelData.EMPTY).contains(layer)) {

                BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getBlockModel(blockState);

                try {
                    List<BakedQuad> modelQuads = model.getQuads(blockState, null, rand, ModelData.EMPTY, layer);
                    for (BakedQuad quad : modelQuads) {
                        blockStateQuads.add(transformer.process(quad));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return blockStateQuads;
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
        return this.spriteGetter.apply(BaseStationModelLoader.MATERIAL_BASE_STATION_SIDE);
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