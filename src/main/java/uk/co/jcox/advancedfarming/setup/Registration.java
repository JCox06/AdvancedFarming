package uk.co.jcox.advancedfarming.setup;

import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import uk.co.jcox.advancedfarming.be.BaseStationBE;
import uk.co.jcox.advancedfarming.be.PlantVesselBE;
import uk.co.jcox.advancedfarming.be.WoodGeneratorBE;
import uk.co.jcox.advancedfarming.block.BaseStationBlock;
import uk.co.jcox.advancedfarming.block.PlantVesselBlock;
import uk.co.jcox.advancedfarming.block.WoodGeneratorBlock;
import uk.co.jcox.advancedfarming.container.WoodGeneratorContainer;

import java.util.List;

import static uk.co.jcox.advancedfarming.AdvancedFarming.MODID;

public class Registration {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);
    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES = DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, MODID);
    public static final DeferredRegister<PlacedFeature> PLACED_FEATURES = DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, MODID);

    public static final Item.Properties ITEM_PROPERTIES = new Item.Properties().tab(CommonSetup.ITEM_GROUP);

    public static void init(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        CONTAINERS.register(bus);
        CONFIGURED_FEATURES.register(bus);
        PLACED_FEATURES.register(bus);
    }

    private static <B extends Block> RegistryObject<Item> fromBlock(RegistryObject<B> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), ITEM_PROPERTIES));
    }


    //Phosphate block
    public static final RegistryObject<Block> PHOSPHATE_ROCK_BLOCK = BLOCKS.register("phosphate_rock", () ->
            new Block(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> PHOSPHATE_ROCK_ITEM = fromBlock(PHOSPHATE_ROCK_BLOCK);
    public static final RegistryObject<Item> RAW_PHOSPHATE = ITEMS.register("raw_phosphate", () -> new Item(ITEM_PROPERTIES));

    //BaseStation
    public static final RegistryObject<Block> BASE_STATION_BLOCK = BLOCKS.register("base_station", BaseStationBlock::new);
    public static final RegistryObject<Item> BASE_STATION_ITEM = fromBlock(BASE_STATION_BLOCK);
    public static final RegistryObject<BlockEntityType<BaseStationBE>> BASE_STATION_BE = BLOCK_ENTITIES.register("base_station",
            () -> BlockEntityType.Builder.of(BaseStationBE::new, BASE_STATION_BLOCK.get()).build(null));


    //Crop Receptacle
    public static final RegistryObject<Block> PLANT_VESSEL_BLOCK = BLOCKS.register("plant_vessel", PlantVesselBlock::new);
    public static final RegistryObject<Item> PLANT_VESSEL_ITEM = fromBlock(PLANT_VESSEL_BLOCK);
    public static final RegistryObject<BlockEntityType<PlantVesselBE>> PLANT_VESSEL_BE = BLOCK_ENTITIES.register("plant_vessel",
            () -> BlockEntityType.Builder.of(PlantVesselBE::new, PLANT_VESSEL_BLOCK.get()).build(null));

    //Wood Generator
    public static final RegistryObject<Block> WOOD_GENERATOR_BLOCK = BLOCKS.register("wood_generator", WoodGeneratorBlock::new);
    public static final RegistryObject<Item> WOOD_GENERATOR_ITEM = fromBlock(WOOD_GENERATOR_BLOCK);
    public static final RegistryObject<BlockEntityType<WoodGeneratorBE>> WOOD_GENERATOR_BE = BLOCK_ENTITIES.register("wood_generator",
            () -> BlockEntityType.Builder.of(WoodGeneratorBE::new, WOOD_GENERATOR_BLOCK.get()).build(null));
    public static final RegistryObject<MenuType<WoodGeneratorContainer>> WOOD_GENERATOR_CONTAINER = CONTAINERS.register("wood_generator", () ->
            IForgeMenuType.create((windowId, inv, data) -> new WoodGeneratorContainer(windowId, data.readBlockPos(), inv, inv.player)));


    //Items
    public static final RegistryObject<Item> FERTILIZER = ITEMS.register("fertilizer", () -> new Item(ITEM_PROPERTIES));

   //Configured features

    public static final RegistryObject<ConfiguredFeature<?, ?>> PHOSPHATE_ROCK_BLOCK_CONFIGURED_FEATURE = CONFIGURED_FEATURES.register("phosphate_rock", () ->
            //Vein Size
            new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(OreFeatures.STONE_ORE_REPLACEABLES, PHOSPHATE_ROCK_BLOCK.get().defaultBlockState(), 12)));

    public static final RegistryObject<PlacedFeature> POSHPHATE_ROCK_BLOCK_PLACED_FEATURE = PLACED_FEATURES.register("phosphate_rock", () ->
            //                                                                                                                                    Per chunk
           new PlacedFeature(PHOSPHATE_ROCK_BLOCK_CONFIGURED_FEATURE.getHolder().get(), List.of(CountPlacement.of(20), InSquarePlacement.spread(), HeightRangePlacement.triangle(VerticalAnchor.bottom(), VerticalAnchor.top()))));

}
