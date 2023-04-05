package uk.co.jcox.advancedfarming.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import uk.co.jcox.advancedfarming.setup.Registration;

import static uk.co.jcox.advancedfarming.AdvancedFarming.MODID;

public class AFBlockTagProvider extends BlockTagsProvider {

    public AFBlockTagProvider(DataGenerator generator, ExistingFileHelper helper) {
        super(generator, MODID, helper);
    }

    @Override
    public void addTags() {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(Registration.PLANT_VESSEL.get());

        tag(BlockTags.NEEDS_STONE_TOOL)
                .add(Registration.PLANT_VESSEL.get());
    }

}
