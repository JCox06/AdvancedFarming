package uk.co.jcox.advancedfarming.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ManureBlock extends Block {

    public static VoxelShape shape = Shapes.box(0, 0, 0, 1, 0.2, 1);

    public ManureBlock() {
        super(BlockBehaviour.Properties.of(Material.DIRT).noCollission());

    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext context) {
        return shape;
    }

    @Override  
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        entity.makeStuckInBlock(state, new Vec3(0.8f, 1.0f, 0.8f));
    }
}
