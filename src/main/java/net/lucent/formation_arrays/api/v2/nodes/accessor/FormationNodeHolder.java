package net.lucent.formation_arrays.api.v2.nodes.accessor;


import net.lucent.formation_arrays.api.v2.nodes.FormationNode;
import net.lucent.formation_arrays.api.v2.nodes.FormationNodeType;
import net.lucent.formation_arrays.capabilities.CoreCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public sealed interface FormationNodeHolder permits
        FormationNodeHolder.EntityFormationNodeHolder,
        FormationNodeHolder.BlockFormationNodeHolder

    {

        FormationNode getNode(Level level);
        BlockPos getPosition();

        record EntityFormationNodeHolder(
                Entity entity) implements FormationNodeHolder {

            @Override
            public FormationNode getNode(Level level) {
                return entity.getCapability(CoreCapabilities.ENTITY_FORMATION_NODE);
            }

            @Override
            public BlockPos getPosition() {
                return entity.blockPosition();
            }



        }
        //todo potentially no need to store the block, note also store dimension id for hashing
        record BlockFormationNodeHolder(BlockPos pos) implements FormationNodeHolder {
            @Override
            public FormationNode getNode(Level level) {

                return level.getCapability(CoreCapabilities.BLOCK_FORMATION_NODE,pos);
            }

            @Override
            public BlockPos getPosition() {
                return pos;
            }

        }
}
