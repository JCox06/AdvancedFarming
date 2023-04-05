package uk.co.jcox.advancedfarming.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import uk.co.jcox.advancedfarming.be.PlantVesselBE;

public class PlantVesselBlock extends Block implements EntityBlock {

    private VoxelShape VOXEL_SHAPE = Shapes.box(0.1, 0.1, 0.1, 0.9, 0.8, 0.9);

    public PlantVesselBlock() {
        super(Properties.of(Material.METAL).requiresCorrectToolForDrops());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PlantVesselBE(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return (lvl, pos, blockState, tile) -> {
            if (tile instanceof PlantVesselBE entity) {
                entity.tickServer();
            }
        };
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_) {
        return this.VOXEL_SHAPE;
    }
}
