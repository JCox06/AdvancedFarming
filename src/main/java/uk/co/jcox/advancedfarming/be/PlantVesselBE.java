package uk.co.jcox.advancedfarming.be;

import net.minecraft.core.BlockPos;
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

    public static final String NBT_INCUBATING_BLOCK = "Crop";

    public static final ModelProperty<BlockState> INCUBATING_BLOCK = new ModelProperty<>();

    private BlockState incubatingBlock;

    private int counter;

    private final ItemStackHandler input = createInventory();
    private final ItemStackHandler output = createInventory();
    private final EnergyStorage energy = new EnergyStorage(30);

    private final LazyOptional<IItemHandler> inputHandler = LazyOptional.of(() -> input);
    private final LazyOptional<IItemHandler> outputHandler = LazyOptional.of(() -> output);
    private final LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> energy);
    private final LazyOptional<IItemHandler> combinedItemHandler = LazyOptional.of(this::createCombinedItemHandler);

    public PlantVesselBE(BlockPos pos, BlockState state) {
        super(Registration.PLANT_VESSEL_BE.get(), pos, state);
    }

    public void tickServer() {

    }

    private ItemStackHandler createInventory() {
        return new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return stack.is(Tags.Items.CROPS);
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
        tag.put("Inventory", input.serializeNBT());
        tag.put("Energy", energy.serializeNBT());
        tag.putInt("Counter", counter);

        saveClientData(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        //Server
        if (tag.contains("Inventory")) {
            input.deserializeNBT(tag.getCompound("Inventory"));
        }

        if (tag.contains("Energy")) {
            energy.deserializeNBT(tag.get("Energy"));
        }

        counter = tag.getInt("Counter");

        loadClientData(tag);

    }

    private void loadClientData(CompoundTag tag) {
        //Client [todo] there is an issue here, crop and block are the same thing!
        if (tag.contains(NBT_INCUBATING_BLOCK)) {
            incubatingBlock = NbtUtils.readBlockState(tag.getCompound(NBT_INCUBATING_BLOCK));
        }
    }

    private void saveClientData(CompoundTag tag) {
        //Client data
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
    public void setIncubatingBlock(BlockState state) {
        this.incubatingBlock = state;
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }
}
