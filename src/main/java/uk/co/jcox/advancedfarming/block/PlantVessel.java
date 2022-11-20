package uk.co.jcox.advancedfarming.block;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;
import uk.co.jcox.advancedfarming.be.PlantVesselBE;

import static uk.co.jcox.advancedfarming.AdvancedFarming.MODID;

public class PlantVessel extends AbstractAFBlock implements EntityBlock {

    private final static ResourceLocation BASIC_TOOLTIP = new ResourceLocation(MODID, "tooltip.basic_plant_vessel");
    private final static ResourceLocation DETAILED_TOOLTIP = new ResourceLocation(MODID, "tooltip.detailed_plant_vessel");


    public PlantVessel() {
        super(BlockBehaviour.Properties.of(Material.GLASS).sound(SoundType.GLASS).noOcclusion().requiresCorrectToolForDrops(), BASIC_TOOLTIP, DETAILED_TOOLTIP);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PlantVesselBE(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if(level.isClientSide()) {
            return null;
        }

        return (lvl, pos, blockState, tile) -> {
            if(tile instanceof PlantVesselBE entity) {
                entity.tickServer();
            }
        };
    }

    @Nullable
    @Override
    public <T extends BlockEntity> GameEventListener getListener(ServerLevel p_221121_, T p_221122_) {
        return EntityBlock.super.getListener(p_221121_, p_221122_);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (! level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof PlantVesselBE ve) {
                ve.setIncubatingBlock(Blocks.GRASS_BLOCK.defaultBlockState());
            }
        }

       return InteractionResult.SUCCESS;
    }
}
