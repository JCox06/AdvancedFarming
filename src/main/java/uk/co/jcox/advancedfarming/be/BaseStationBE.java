package uk.co.jcox.advancedfarming.be;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.jcox.advancedfarming.block.BaseStation;
import uk.co.jcox.advancedfarming.setup.Registration;

public class BaseStationBE extends BlockEntity {

    public static final int POWER_CAPACITY = 600;
    public static final int POWER_SEND = 150;
    public static final int POWER_REQUIRED = 50;

    private int counter;

    private EnergyStorage energy = new EnergyStorage(POWER_CAPACITY);

    private LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> energy);

    public BaseStationBE(BlockPos pos, BlockState state) {
        super(Registration.BASE_STATION_BE.get(), pos, state);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
    }

    @Override
    public void load(CompoundTag tag) {
        if(tag.contains("Counter")) {
            counter = tag.getInt("Counter");
        }

        if(tag.contains("Energy")) {
            energy.deserializeNBT(tag.get("Energy"));
        }
        super.load(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putInt("Counter", counter);
        tag.put("Energy", energy.serializeNBT());
    }

    public void tickServer() {
        BlockState blockstate = level.getBlockState(worldPosition);
        if (energy.getEnergyStored() >= POWER_REQUIRED) {
            level.setBlock(worldPosition, blockstate.setValue(BaseStation.POWER_STATE, 1), Block.UPDATE_ALL);
        } else if (energy.getEnergyStored() < POWER_REQUIRED) {
            level.setBlock(worldPosition, blockstate.setValue(BaseStation.POWER_STATE, 0), Block.UPDATE_ALL);
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ENERGY) {
            return energyHandler.cast();
        }
        return super.getCapability(cap, side);
    }
}
