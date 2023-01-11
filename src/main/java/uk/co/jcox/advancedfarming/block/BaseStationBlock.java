package uk.co.jcox.advancedfarming.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import uk.co.jcox.advancedfarming.be.BaseStationBE;
import uk.co.jcox.advancedfarming.setup.Registration;

public class BaseStationBlock extends Block implements EntityBlock {

    public static VoxelShape fullShape = Shapes.box(0, 0, 0, 1, 2, 1);

    public BaseStationBlock() {
        super(Properties.of(Material.METAL).requiresCorrectToolForDrops());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BaseStationBE(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if(level.isClientSide()) {
            return null;
        }

        return (lvl, pos, blockState, tile) -> {
            if(tile instanceof BaseStationBE entity) {
                entity.tickServer();
            }
        };
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {


        if(level.isClientSide) {
            return InteractionResult.FAIL;
        }

        ItemStack currentItem = player.getItemInHand(hand);

        if(currentItem.getItem().equals(Registration.PLANT_VESSEL_ITEM.get())) {
            currentItem.setCount(currentItem.getCount() - 1);
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof BaseStationBE baseStation) {
                baseStation.isIncubatorPresent(true);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }


    @Override
    public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext context) {
        BlockEntity be = get.getBlockEntity(pos);


        if (be instanceof BaseStationBE baseStation) {
            if (baseStation.hasIncubator()) {
                return fullShape;
            }
        }

        return Shapes.block();
    }

}
