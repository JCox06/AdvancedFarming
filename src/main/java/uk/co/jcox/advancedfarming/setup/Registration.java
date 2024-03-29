package uk.co.jcox.advancedfarming.setup;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import uk.co.jcox.advancedfarming.be.PlantVesselBE;
import uk.co.jcox.advancedfarming.be.WoodGeneratorBE;
import uk.co.jcox.advancedfarming.block.ManureBlock;
import uk.co.jcox.advancedfarming.block.PlantVesselBlock;
import uk.co.jcox.advancedfarming.block.WoodGeneratorBlock;
import uk.co.jcox.advancedfarming.container.WoodGeneratorContainer;

import static uk.co.jcox.advancedfarming.AdvancedFarming.MODID;

public class Registration {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);

    public static final Item.Properties ITEM_PROPERTIES = new Item.Properties().tab(CommonSetup.ITEM_GROUP);

    public static void init(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        CONTAINERS.register(bus);
    }

    private static <B extends Block> RegistryObject<Item> fromBlock(RegistryObject<B> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), ITEM_PROPERTIES));
    }

    //ForgeNamespace Tags
    public static final TagKey<Block> STEM_GROWN_BLOCK = BlockTags.create(new ResourceLocation("forge", "stem_grown"));

    //PlantVessel
    public static final RegistryObject<Block> PLANT_VESSEL = BLOCKS.register("plant_vessel", PlantVesselBlock::new);
    public static final RegistryObject<Item> PLANT_VESSEL_ITEM = fromBlock(PLANT_VESSEL);
    public static final RegistryObject<BlockEntityType<PlantVesselBE>> PLANT_VESSEL_BE = BLOCK_ENTITIES.register("plant_vessel",
            () -> BlockEntityType.Builder.of(PlantVesselBE::new, PLANT_VESSEL.get()).build(null));

    //Wood Generator
    public static final RegistryObject<Block> WOOD_GENERATOR_BLOCK = BLOCKS.register("wood_generator", WoodGeneratorBlock::new);
    public static final RegistryObject<Item> WOOD_GENERATOR_ITEM = fromBlock(WOOD_GENERATOR_BLOCK);
    public static final RegistryObject<BlockEntityType<WoodGeneratorBE>> WOOD_GENERATOR_BE = BLOCK_ENTITIES.register("wood_generator",
            () -> BlockEntityType.Builder.of(WoodGeneratorBE::new, WOOD_GENERATOR_BLOCK.get()).build(null));
    public static final RegistryObject<MenuType<WoodGeneratorContainer>> WOOD_GENERATOR_CONTAINER = CONTAINERS.register("wood_generator", () ->
            IForgeMenuType.create((windowId, inv, data) -> new WoodGeneratorContainer(windowId, data.readBlockPos(), inv, inv.player)));


    //Manure
    public static final RegistryObject<Block> MANURE_BLOCK = BLOCKS.register("manure", () -> new ManureBlock());
    public static final RegistryObject<Item> MANURE_ITEM = fromBlock(MANURE_BLOCK);
}
