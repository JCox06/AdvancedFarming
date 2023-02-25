package uk.co.jcox.advancedfarming.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

public class BaseStationTopBlock extends Block {


    public BaseStationTopBlock() {
        super(BlockBehaviour.Properties.of(Material.AIR).noOcclusion());
    }

}
