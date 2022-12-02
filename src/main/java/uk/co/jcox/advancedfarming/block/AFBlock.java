package uk.co.jcox.advancedfarming.block;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AFBlock extends Block {

    public final ResourceLocation basicTooltip;
    public final ResourceLocation detailedTooltip;

    protected AFBlock(Properties properties, ResourceLocation tbasic, ResourceLocation tdetailed) {
        super(properties);
        this.basicTooltip = tbasic;
        this.detailedTooltip = tdetailed;
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter getter, List<Component> list, TooltipFlag tooltip) {
        super.appendHoverText(stack, getter, list, tooltip);

        if(Screen.hasShiftDown()) {
            list.add(Component.translatable(detailedTooltip.getPath()));
        } else {
            list.add(Component.translatable(basicTooltip.getPath()));
        }
    }
}
