package uk.co.jcox.advancedfarming.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static uk.co.jcox.advancedfarming.AdvancedFarming.MODID;

public class BaseStationModelLoader implements IGeometryLoader<BaseStationModelLoader.PlantVesselModelGeometry> {

    public static final ResourceLocation PLANT_VESSEL_LOADER = new ResourceLocation(MODID, "vesselloader");

    //Base station
    public static final ResourceLocation BASE_STATION_SIDE = new ResourceLocation(MODID, "block/base_station_side");
    public static final ResourceLocation BASE_STATION_TOP = new ResourceLocation(MODID, "block/base_station_top");

    public static final Material MATERIAL_BASE_STATION_SIDE = ForgeHooksClient.getBlockMaterial(BASE_STATION_SIDE);
    public static final Material MATERIAL_BASE_STATION_TOP = ForgeHooksClient.getBlockMaterial(BASE_STATION_TOP);

    @Override
    public PlantVesselModelGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
        return new PlantVesselModelGeometry();
    }

    public static class PlantVesselModelGeometry implements IUnbakedGeometry<PlantVesselModelGeometry> {
        @Override
        public BakedModel bake(IGeometryBakingContext context, ModelBakery bakery, Function<Material,
                TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
            return new BaseStationBakedModel(modelState, spriteGetter, overrides, context.getTransforms());
        }

        @Override
        public Collection<Material> getMaterials(IGeometryBakingContext context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
            return List.of(MATERIAL_BASE_STATION_SIDE, MATERIAL_BASE_STATION_TOP);
        }

    }
}