package uk.co.jcox.advancedfarming.block;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.material.Material;
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
}
