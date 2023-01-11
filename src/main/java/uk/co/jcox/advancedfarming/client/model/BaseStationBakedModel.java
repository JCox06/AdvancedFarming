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
import uk.co.jcox.advancedfarming.be.BaseStationBE;
import uk.co.jcox.advancedfarming.block.BaseStationBlock;
import uk.co.jcox.advancedfarming.util.ClientTools;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

import static java.lang.Boolean.TRUE;
import static uk.co.jcox.advancedfarming.util.ClientTools.v;

public class BaseStationBakedModel implements IDynamicBakedModel {

    private final ModelState modelState;
    private final Function<Material, TextureAtlasSprite> spriteGetter;
    private Map<Boolean, List<BakedQuad>> quadCache = new HashMap<>();
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
        this.quadCache.put(true, generateQuads(true));
        this.quadCache.put(false, generateQuads(false));
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand, @Nonnull ModelData extraData, @Nullable RenderType layer) {


        boolean incubatorPresent = TRUE == extraData.get(BaseStationBE.INCUBATOR_PRESENT);

        if (state == null) {
            return new ArrayList<>(quadCache.get(false));
        }

        if ((side != null || (layer == null))) {
            return Collections.emptyList();
        }

        if (layer.equals(RenderType.translucent())) {
            return new ArrayList<>(quadCache.get(incubatorPresent));
        }

        if (layer.equals(RenderType.cutout())) {
            return getQuadsOfIncubatingBlock(state, rand, extraData, layer);
        }

        return Collections.emptyList();
    }

    private List<BakedQuad> generateQuads(boolean isIncubatorPresent) {
        Transformation rotation = modelState.getRotation();

        TextureAtlasSprite textureSide = spriteGetter.apply(BaseStationModelLoader.MATERIAL_SIDE);
        TextureAtlasSprite textureBase = spriteGetter.apply(BaseStationModelLoader.MATERIAL_BOTTOM);
        TextureAtlasSprite textureOutsideBase = spriteGetter.apply(BaseStationModelLoader.MATERIAL_BASE);
        TextureAtlasSprite textureBottomBase = spriteGetter.apply(BaseStationModelLoader.MATERIAL_BOTTOM_BASE);

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


        if(isIncubatorPresent) {
            float l = (2f / 16f);
            float r = (14f / 16f);

            //Base
            quads.add(ClientTools.createQuad(v(r, r + 1, r), v(r, r + 1, l), v(l, r + 1, l), v(l, r + 1, r), rotation, textureSide));      // Top side
            quads.add(ClientTools.createQuad(v(a, l + 1, a), v(a, l + 1, b), v(b, l + 1, b), v(b, l + 1, a), rotation, textureBase));      // Inside texture
            quads.add(ClientTools.createQuad(v(r, r + 1, r), v(r, l + 1, r), v(r, l + 1, l), v(r, r + 1, l), rotation, textureSide));
            quads.add(ClientTools.createQuad(v(l, r + 1, l), v(l, l + 1, l), v(l, l + 1, r), v(l, r + 1, r), rotation, textureSide));
            quads.add(ClientTools.createQuad(v(r, r + 1, l), v(r, l + 1, l), v(l, l + 1, l), v(l, r + 1, l), rotation, textureSide));
            quads.add(ClientTools.createQuad(v(l, r + 1, r), v(l, l + 1, r), v(r, l + 1, r), v(r, r + 1, r), rotation, textureSide));


            quads.add(ClientTools.createQuad(v(b, b + 1, b), v(a, b + 1, b), v(a, b + 1, a), v(b, b + 1, a), rotation, textureOutsideBase));       //Outside

            quads.add(ClientTools.createQuad(v(b, l + 1, b), v(b, b + 1, b), v(b, b + 1, a), v(b, l + 1, a), rotation, textureBottomBase));
            quads.add(ClientTools.createQuad(v(a, l + 1, b), v(a, b + 1, b), v(b, b + 1, b), v(b, l + 1, b), rotation, textureBottomBase));
            quads.add(ClientTools.createQuad(v(b, l + 1, a), v(b, b + 1, a), v(a, b + 1, a), v(a, l + 1, a), rotation, textureBottomBase));
            quads.add(ClientTools.createQuad(v(a, l + 1, a), v(a, b + 1, a), v(a, b + 1, b), v(a, l + 1, b), rotation, textureBottomBase));

        }
        return quads;
    }


    private List<BakedQuad> getQuadsOfIncubatingBlock(@Nullable BlockState state, @NotNull RandomSource rand, @Nullable ModelData extraData, RenderType layer) {
        List<BakedQuad> incubatingBlockQuads = new ArrayList<>();

        BlockState crop = Blocks.WHEAT.defaultBlockState().setValue(CropBlock.AGE, 7);
//        BlockState crop = extraData.get(PlantVesselBE.INCUBATING_BLOCK);


        if (crop != null && !(crop.getBlock() instanceof BaseStationBlock)) {
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
        return this.spriteGetter.apply(BaseStationModelLoader.MATERIAL_SIDE);
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