package uk.co.jcox.advancedfarming.container;

import net.minecraft.client.gui.screens.inventory.FurnaceScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import uk.co.jcox.advancedfarming.be.WoodGeneratorBE;
import uk.co.jcox.advancedfarming.block.WoodGeneratorBlock;
import uk.co.jcox.advancedfarming.setup.Registration;

public class WoodGeneratorContainer extends AbstractContainerMenu {

    private BlockEntity tile;
    private Player player;
    private IItemHandler playerInv;
    private ContainerData dataAccess;

    public WoodGeneratorContainer(int windowId, BlockPos pos, Inventory inventory, Player player, ContainerData dataAccess) {
        super(Registration.WOOD_GENERATOR_CONTAINER.get(), windowId);
        this.player = player;
        this.tile = player.getCommandSenderWorld().getBlockEntity(pos);
        this.playerInv = new InvWrapper(inventory);
        this.dataAccess = dataAccess;

        this.addDataSlots(dataAccess);

        tile.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(h -> {
            addSlot(new SlotItemHandler(h, 0, 16, 27));
        });

        int index = 9;

        for(int i = 0; i < 3; i++) { //Horizontal
            for(int j =0; j < 9; j++) { //Vertical
                addSlot(new SlotItemHandler(playerInv, index, j * 18 + 5, (i * 18) + 80));
                index++;
            }
        }

        int hotbarEndIndex = 9;
        for(int i = 0; i < hotbarEndIndex; i++) {
            addSlot(new SlotItemHandler(playerInv, i, i * 18 + 5, 145));
        }
    }

    public int getEnergy() {
        return this.dataAccess.get(WoodGeneratorBE.GET_ENERGY);
    }

    public WoodGeneratorContainer(int windowId, BlockPos pos, Inventory inventory, Player player) {
        this(windowId, pos, inventory, player, new SimpleContainerData(1));
    }

    @Override
    public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
        return null;
    }

    @Override
    public boolean stillValid(Player p_38874_) {
        return true;
    }
}
