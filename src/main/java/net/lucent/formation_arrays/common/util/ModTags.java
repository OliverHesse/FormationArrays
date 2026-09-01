package net.lucent.formation_arrays.common.util;

import net.lucent.formation_arrays.FormationArrays;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
//TODO remove any and all tag stuff
public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> FORMATION_NODE = createTag("formation_node");

        public static final TagKey<Block> MAGIC_NODE = createTag("formation_node/magic_node");
        public static final TagKey<Block> DIAMOND_NODE = createTag("formation_node/diamond_node");
        public static final TagKey<Block> GOLD_NODE = createTag("formation_node/gold_node");
        public static final TagKey<Block> IRON_NODE = createTag("formation_node/iron_node");

        private static TagKey<Block> createCommonTag(String path) {
            return BlockTags.create(Identifier.fromNamespaceAndPath("c", path));

        }

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(Identifier.fromNamespaceAndPath(FormationArrays.MOD_ID, name));
        }
    }


}
