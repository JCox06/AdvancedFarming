package uk.co.jcox.advancedfarming.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PlantVesselBlock extends Block {

    private static final VoxelShape VOXEL_SHAPE = Shapes.box(0.1, 0.1, 0.1, 0.9, 0.8, 0.9);

    public PlantVesselBlock() {
        super(BlockBehaviour.Properties.of(Material.GLASS).requiresCorrectToolForDrops());
    }


    @Override
    public VoxelShape getOcclusionShape(BlockState p_60578_, BlockGetter p_60579_, BlockPos p_60580_) {
        return VOXEL_SHAPE;
    }
}
