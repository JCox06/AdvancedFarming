package uk.co.jcox.advancedfarming.be;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.jcox.advancedfarming.container.WoodGeneratorContainer;
import uk.co.jcox.advancedfarming.setup.Registration;

import java.util.concurrent.atomic.AtomicInteger;

import static uk.co.jcox.advancedfarming.AdvancedFarming.MODID;

public class WoodGeneratorBE extends BlockEntity implements MenuProvider {

    public static final int POWER_GEN = 2;
    public static final int POWER_SEND = 100;
    public static final int POWER_CAPACITY = 600;

    public static final int GET_ENERGY = 0;

    public static final int ENERGY_FROM_WOOD = 25;

    public static final ResourceLocation SCREEN_NAME = new ResourceLocation(MODID, "screen.wood_generator");

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            if (index == GET_ENERGY) {
                return WoodGeneratorBE.this.energy.getEnergyStored();
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {

        }

        @Override
        public int getCount() {
            return 1;
        }
    };

    private int counter = 0;

    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot,ItemStack stack) {
            return stack.is(ItemTags.LOGS);
        }
    };

    private final EnergyStorage energy = new EnergyStorage(POWER_CAPACITY);

    private LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> energy);
    private LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> inventory);


    public WoodGeneratorBE(BlockPos p_155229_, BlockState p_155230_) {
        super(Registration.WOOD_GENERATOR_BE.get(), p_155229_, p_155230_);
    }

    public void tickServer() {
        if (counter > 0) {
            energy.receiveEnergy(POWER_GEN, false);
            counter--;
            setChanged();
        }

        if (counter <= 0) {
            ItemStack stack = inventory.extractItem(0, 1, false);
            if(! stack.getItem().equals(Items.AIR)) {
                counter = ENERGY_FROM_WOOD;
                setChanged();
            }
        }

        BlockState blockstate = level.getBlockState(worldPosition);
        if(blockstate.getValue(BlockStateProperties.POWERED) != counter > 0) {
            level.setBlock(worldPosition, blockstate.setValue(BlockStateProperties.POWERED, counter > 0), Block.UPDATE_ALL);
        }

        sendOutPower();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        itemHandler.invalidate();
        energyHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("Inventory", inventory.serializeNBT());
        tag.put("Energy", energy.serializeNBT());
        tag.putInt("Counter", counter);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if(tag.contains("Inventory")) {
            inventory.deserializeNBT(tag.getCompound("Inventory"));
        }

        if(tag.contains("Energy")) {
            energy.deserializeNBT(tag.get("Energy"));
        }

        if(tag.contains("Counter")) {
            counter = tag.getInt("Counter");
        }
    }

    private void sendOutPower() {
        AtomicInteger capacity = new AtomicInteger(energy.getEnergyStored());
        if (capacity.get() > 0) {
            for(Direction direction : Direction.values()) {
                BlockEntity be = level.getBlockEntity(worldPosition.relative(direction));
                if (be != null) {
                    be.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).map(handler -> {
                        if(handler.canReceive()) {
                            int received = handler.receiveEnergy(Math.min(POWER_SEND, capacity.get()), false);
                            capacity.addAndGet(-received);
                            energy.extractEnergy(received, false);
                            setChanged();
                            return capacity.get() > 0;
                        } else {
                            return true;
                        }
                    });
                }
            }
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(SCREEN_NAME.getPath());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player plr) {
        return new WoodGeneratorContainer(id, this.getBlockPos(), inv, plr, dataAccess);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {

        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandler.cast();
        }

        if(cap == ForgeCapabilities.ENERGY) {
            return energyHandler.cast();
        }

        return super.getCapability(cap);
    }
}
