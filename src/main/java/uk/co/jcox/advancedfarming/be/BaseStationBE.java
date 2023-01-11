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

public class BaseStationBE extends BlockEntity {

    private int counter;

    //NBT TAGS
    private static final String NBT_INCUBATING_BLOCK = "Crop";
    private static final String NBT_INCUBATOR_PRESENT = "Incubator";
    private static final String NBT_CROP_AGE = "Age";
    private static final String NBT_INVENTORY_INPUT = "IInventory";
    private static final String NBT_INVENTORY_OUTPUT = "OInventory";
    private static final String NBT_ENERGY = "Energy";
    private static final String NBT_TICKER = "Counter";

    //Model properties for baked model
    public static final ModelProperty<BlockState> INCUBATING_BLOCK = new ModelProperty<>();
    public static final ModelProperty<Boolean> INCUBATOR_PRESENT = new ModelProperty<>();
    public static final ModelProperty<Integer> AGE = new ModelProperty<>();

    private final EnergyStorage energy = new EnergyStorage(30);
    private final ItemStackHandler input = createInputInventory(5);
    private final ItemStackHandler output = createOutputInventory(5);

    private final LazyOptional<IItemHandler> inputHandler = LazyOptional.of(() -> input);
    private final LazyOptional<IItemHandler> outputHandler = LazyOptional.of(() -> output);
    private final LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> energy);
    private final LazyOptional<IItemHandler> combinedItemHandler = LazyOptional.of(this::createCombinedItemHandler);

    //Model Properties values
    private BlockState incubatingBlock;
    private boolean incubatorPresent;
    private int age;

    public BaseStationBE(BlockPos pos, BlockState state) {
        super(Registration.BASE_STATION_BE.get(), pos, state);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        this.inputHandler.invalidate();
        this.outputHandler.invalidate();
        this.energyHandler.invalidate();
        this.combinedItemHandler.invalidate();
    }


    public void tickServer() {

    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if(tag.contains(NBT_TICKER)) {
            this.counter = tag.getInt(NBT_TICKER);
        }

        if(tag.contains(NBT_ENERGY)) {
            this.energy.deserializeNBT(tag.get(NBT_ENERGY));
        }

        if(tag.contains(NBT_INVENTORY_INPUT)) {
            this.input.deserializeNBT(tag.getCompound(NBT_INVENTORY_INPUT));
        }

        if(tag.contains(NBT_INVENTORY_OUTPUT)) {
            this.input.deserializeNBT(tag.getCompound(NBT_INVENTORY_OUTPUT));
        }

        loadClientData(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put(NBT_INVENTORY_INPUT, this.input.serializeNBT());
        tag.put(NBT_INVENTORY_OUTPUT, this.input.serializeNBT());
        tag.put(NBT_ENERGY, energy.serializeNBT());
        tag.putInt(NBT_TICKER, counter);

        saveClientData(tag);
    }


    private void loadClientData(CompoundTag tag) {
        if(tag.contains(NBT_INCUBATING_BLOCK)) {
            this.incubatingBlock = NbtUtils.readBlockState(tag.getCompound(NBT_INCUBATING_BLOCK));
        }

        if(tag.contains(NBT_INCUBATOR_PRESENT)) {
            this.incubatorPresent = tag.getBoolean(NBT_INCUBATOR_PRESENT);
        }

        if(tag.contains(NBT_CROP_AGE)) {
            this.age = tag.getInt(NBT_CROP_AGE);
        }
    }

    private void saveClientData(CompoundTag tag) {
        if (this.incubatingBlock != null) {
            tag.put(NBT_INCUBATING_BLOCK, NbtUtils.writeBlockState(this.incubatingBlock));
        }
        tag.putBoolean(NBT_INCUBATOR_PRESENT, this.incubatorPresent);
        tag.putInt(NBT_CROP_AGE, this.age);
    }



    //The following methods are called when a player receives a new chunk
    //The following method is called server side, and prepares the data
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveClientData(tag);
        return tag;
    }

    //This is called on the client
    @Override
    public void handleUpdateTag(CompoundTag tag) {
        if (tag != null) {
            loadClientData(tag);
        }
    }


    //The following set of the methods happen when a bock update happens
    //The following method is called serverside
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    //The following method is called on the client
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        BlockState oldIncubatorBlock = this.incubatingBlock;
        boolean oldIncubatorPresent = this.incubatorPresent;
        CompoundTag tag = packet.getTag();
        handleUpdateTag(tag);

        if(!(Objects.equals(incubatingBlock, oldIncubatorBlock)) || oldIncubatorPresent != this.incubatorPresent) {
            requestModelDataUpdate();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public ModelData getModelData() {
        return ModelData.builder()
                .with(INCUBATING_BLOCK, this.incubatingBlock)
                .with(INCUBATOR_PRESENT, this.incubatorPresent)
                .with(AGE, this.age)
                .build();
    }


    private void setIncubatingBlock(BlockState blockstate) {
        this.incubatingBlock = blockstate;
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    public void isIncubatorPresent(boolean isPresent) {
        this.incubatorPresent = isPresent;
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    private ItemStackHandler createInputInventory(int size) {
        return new ItemStackHandler(size) {
            @Override
            protected void onContentsChanged(int slot) {
                counter = 0;
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return stack.is(Tags.Items.SEEDS) || stack.is(Tags.Items.CROPS);
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


    public boolean hasIncubator() {
        return this.incubatorPresent;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {

        if (side == null) {
            return this.combinedItemHandler.cast();
        }

        if(cap == ForgeCapabilities.ENERGY) {
            return energyHandler.cast();
        }
        return super.getCapability(cap, side);
    }
}
