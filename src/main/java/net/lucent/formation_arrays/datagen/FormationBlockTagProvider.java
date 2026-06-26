package net.lucent.formation_arrays.datagen;

import net.lucent.formation_arrays.FormationArrays;
import net.lucent.formation_arrays.common.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.references.BlockIds;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTypeIds;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class FormationBlockTagProvider extends BlockTagsProvider {
    public FormationBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, FormationArrays.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {



        tag(ModTags.Blocks.FORMATION_NODE)
                .addTag(ModTags.Blocks.MAGIC_NODE);

        tag(ModTags.Blocks.MAGIC_NODE)
                .add(BuiltInRegistries.BLOCK.getResourceKey(Blocks.DRAGON_EGG).get());
    }
}
