package uk.co.jcox.advancedfarming.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import static uk.co.jcox.advancedfarming.AdvancedFarming.MODID;

public class AFItemTagProvider extends ItemTagsProvider {

    public AFItemTagProvider(DataGenerator generator, BlockTagsProvider blockTags, ExistingFileHelper helper) {
        super(generator, blockTags, MODID, helper);
    }

    @Override
    public void addTags() {

    }
}
