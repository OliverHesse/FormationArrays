package net.lucent.formation_arrays.api.v2.nodes.accessor;


import net.lucent.formation_arrays.api.v2.nodes.FormationNode;
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
        Identifier getDimension();
        record EntityFormationNodeHolder(
                Entity entity) implements FormationNodeHolder {

            @Override
            public FormationNode getNode(Level level) {
                return null;//TODO
            }

            @Override
            public BlockPos getPosition() {
                return entity.blockPosition();
            }

            @Override
            public Identifier getDimension() {
                return entity.level().dimension().identifier();
            }
        }
        //todo potentially no need to store the block, note also store dimension id for hashing
        record BlockFormationNodeHolder(BlockPos pos, Identifier dimension) implements FormationNodeHolder {
            @Override
            public FormationNode getNode(Level level) {

                return null;//TODO
            }

            @Override
            public BlockPos getPosition() {
                return pos;
            }

            @Override
            public Identifier getDimension() {
                return dimension;
            }
        }
}
