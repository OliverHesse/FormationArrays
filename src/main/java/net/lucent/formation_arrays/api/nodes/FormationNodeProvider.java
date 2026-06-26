package net.lucent.formation_arrays.api.nodes;

import net.lucent.formation_arrays.capabilities.CoreCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public sealed interface FormationNodeProvider permits
        FormationNodeProvider.EntityFormationNodeProvider,
        FormationNodeProvider.BlockFormationNodeProvider

{

    FormationNode getNode(Level level);


    record EntityFormationNodeProvider(Entity entity) implements FormationNodeProvider{

        @Override
        public FormationNode getNode(Level level) {
            return entity.getCapability(CoreCapabilities.ENTITY_FORMATION_NODE);
        }
    }

    record BlockFormationNodeProvider(Block block,BlockPos pos) implements FormationNodeProvider{
        @Override
        public FormationNode getNode(Level level) {

            return level.getCapability(CoreCapabilities.BLOCK_FORMATION_NODE,pos);
        }
    }

}
