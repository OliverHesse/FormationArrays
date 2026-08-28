package net.lucent.formation_arrays.api.v1.nodes;

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
            return null;
        }
    }

    record BlockFormationNodeProvider(Block block,BlockPos pos) implements FormationNodeProvider{
        @Override
        public FormationNode getNode(Level level) {

            return null;
        }
    }

}
