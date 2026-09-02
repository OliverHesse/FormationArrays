package net.lucent.formation_arrays.datagen;

import net.lucent.formation_arrays.FormationArrays;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class FormationBlockTagProvider extends BlockTagsProvider {
    public FormationBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, FormationArrays.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {



    }
}
