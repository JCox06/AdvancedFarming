package uk.co.jcox.advancedfarming.be;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.jcox.advancedfarming.setup.Registration;

import java.util.Objects;

public class PlantVesselBE extends BlockEntity {

    private static final String NBT_INCUBATING_BLOCK = "Crop";
    private static final String NBT_INVENTORY_INPUT = "IInventory";
    private static final String NBT_INVENTORY_OUTPUT = "OInventory";
    private static final String NBT_ENERGY = "Energy";
    private static final String NBT_TICKER = "Counter";

    private static final String NBT_COMPOUND_CLIENT = "Client";

    public static final ModelProperty<BlockState> INCUBATING_BLOCK = new ModelProperty<>();
    public static final ModelProperty<Integer> AGE = new ModelProperty<>();

    private BlockState incubatingBlock;
    private int age;
    private int counter;

    private boolean isStemCrop;
    private boolean isVegetableBlock;
    private boolean isCropBlock;

    private final ItemStackHandler input = createInputInventory(1);
    private final ItemStackHandler output = createOutputInventory(1);
    private final EnergyStorage energy = new EnergyStorage(30);

    private final LazyOptional<IItemHandler> inputHandler = LazyOptional.of(() -> input);
    private final LazyOptional<IItemHandler> outputHandler = LazyOptional.of(() -> output);
    private final LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> energy);
    private final LazyOptional<IItemHandler> combinedItemHandler = LazyOptional.of(this::createCombinedItemHandler);

    public PlantVesselBE(BlockPos pos, BlockState state) {
        super(Registration.PLANT_VESSEL_BE.get(), pos, state);
    }

    private ItemStackHandler createInputInventory(int size) {
        return new ItemStackHandler(size) {
            @Override
            protected void onContentsChanged(int slot) {
                Block block = Block.byItem(getStackInSlot(slot).getItem());
                if (!block.equals(Blocks.AIR)) {
                    setIncubatingBlock(block.defaultBlockState());
                }

                //todo check if block is a stem block, vegetable, or a standard crop block and update the booleans accordingly

                counter = 0;
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return stack.is(Tags.Items.CROPS);
            }
        };

    }

    private ItemStackHandler createOutputInventory(int size) {
        return new ItemStackHandler(size) {
            @Override
            protected void onContentsChanged(int slot) {
                counter = 0;
                setChanged();
            }
        };

    }

    private IItemHandler createCombinedItemHandler() {
        return new CombinedInvWrapper(input, output) {
            @Override
            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                return ItemStack.EMPTY;
            }

            @Override
            public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                return stack;
            }
        };
    }
    public void tickServer() {

    }


    private void manageBlockState() {

    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        inputHandler.invalidate();
        outputHandler.invalidate();
        combinedItemHandler.invalidate();
        energyHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        //Server data
        tag.put(NBT_INVENTORY_INPUT, input.serializeNBT());
        tag.put(NBT_INVENTORY_OUTPUT, output.serializeNBT());
        tag.put(NBT_ENERGY, energy.serializeNBT());
        tag.putInt(NBT_TICKER, counter);

        saveClientData(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        //Server
        if (tag.contains(NBT_INVENTORY_INPUT)) {
            input.deserializeNBT(tag.getCompound(NBT_INVENTORY_INPUT));
        }

        if (tag.contains(NBT_INVENTORY_OUTPUT)) {
            output.deserializeNBT(tag.getCompound(NBT_INVENTORY_OUTPUT));
        }

        if (tag.contains(NBT_ENERGY)) {
            energy.deserializeNBT(tag.get(NBT_ENERGY));
        }

        if (tag.contains(NBT_TICKER)) {
            counter = tag.getInt(NBT_TICKER);
        }

        loadClientData(tag);

    }

    private void loadClientData(CompoundTag tag) {
        if (tag.contains(NBT_INCUBATING_BLOCK)) {
            incubatingBlock = NbtUtils.readBlockState(tag.getCompound(NBT_INCUBATING_BLOCK));
        }
    }

    private void saveClientData(CompoundTag tag) {
        if (incubatingBlock != null) {
            tag.put(NBT_INCUBATING_BLOCK, NbtUtils.writeBlockState(incubatingBlock));
        }
    }


    //These pair of methods are called when the client gets a new chunk
    //This is called server side and makes data for the client
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveClientData(tag);
        return tag;
    }

    //This is called client side
    @Override
    public void handleUpdateTag(CompoundTag tag) {
        if (tag != null) {
            loadClientData(tag);
        }
    }


    //This is called when a block update happens
    //The method calls blockUpdateTag and will update the client based on information stored in the NBT tag
    //This method is called server side
    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    //This method is called client side
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        BlockState oldIncubatorBlock = incubatingBlock;
        CompoundTag tag = pkt.getTag();
        handleUpdateTag(tag);

        if (!(Objects.equals(incubatingBlock, oldIncubatorBlock))) {
            requestModelDataUpdate();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public ModelData getModelData() {
        return ModelData.builder()
                .with(INCUBATING_BLOCK, incubatingBlock)
                .build();
    }


    //Should be called on the server only
    private void setIncubatingBlock(BlockState state) {
        this.incubatingBlock = state;
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (ForgeCapabilities.ITEM_HANDLER == cap) {
            if (side == null) {
                return combinedItemHandler.cast();
            }
            if (side == Direction.UP) {
                return inputHandler.cast();
            }
            if (side == Direction.DOWN) {
                return outputHandler.cast();
            }
        }
        if (cap == ForgeCapabilities.ENERGY) {
            return energyHandler.cast();
        }

        return super.getCapability(cap, side);
    }
}
