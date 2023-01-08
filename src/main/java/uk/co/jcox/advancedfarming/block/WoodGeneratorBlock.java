package uk.co.jcox.advancedfarming.block;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import uk.co.jcox.advancedfarming.be.WoodGeneratorBE;

import static uk.co.jcox.advancedfarming.AdvancedFarming.MODID;

public class WoodGeneratorBlock extends Block implements EntityBlock {

    private static final ResourceLocation SCREEN_LABEL = new ResourceLocation(MODID, "screen.wood_generator");

    public WoodGeneratorBlock() {
        super(Properties.of(Material.METAL)
                .lightLevel(state -> state.getValue(BlockStateProperties.POWERED) ? 10: 0));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WoodGeneratorBE(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState p_153213_, BlockEntityType<T> p_153214_) {
        if(level.isClientSide()) {
            return null;
        }

        return (lvl, pos, blockState, tile) -> {
            if(tile instanceof WoodGeneratorBE entity) {
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.POWERED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(BlockStateProperties.POWERED, false);
    }

    @SuppressWarnings("depreciation")
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        //Opening container is only done on the server
        if(! (level.isClientSide)) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(blockEntity instanceof WoodGeneratorBE be) {
                NetworkHooks.openScreen((ServerPlayer) player, be, blockEntity.getBlockPos());
            } else {
                throw new IllegalStateException();
            }
        }

        return InteractionResult.SUCCESS;
    }
}
