package net.lucent.formation_arrays.api.v2.nodes.accessor;

import net.lucent.formation_arrays.api.v2.nodes.FormationNode;
import net.lucent.formation_arrays.api.v2.nodes.FormationNodeType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public interface FormationNodeReference {


    boolean isLoaded();
    FormationNode get(Level level);
    FormationNodeHolder getHolder();
    BlockPos getPos();
    Identifier getDimension();
    FormationNodeType getNodeType(Level level);

    record Loaded(FormationNodeHolder holder) implements FormationNodeReference{

        @Override
        public boolean isLoaded() {
            return true;
        }

        @Override
        public FormationNode get(Level level) {
            return holder.getNode(level);
        }

        @Override
        public FormationNodeHolder getHolder() {
            return holder;
        }

        @Override
        public BlockPos getPos() {
            return getHolder().getPosition();
        }

        @Override
        public FormationNodeType getNodeType(Level level) {
            return get(level).getType();
        }

        @Override
        public Identifier getDimension() {
            return null;
        }
    }
    record Unloaded(Identifier dimension,BlockPos pos, FormationNodeType type) implements FormationNodeReference{

        @Override
        public boolean isLoaded() {
            return false;
        }

        @Override
        public FormationNode get(Level level) {
            return null;
        }

        @Override
        public FormationNodeHolder getHolder() {
            return null;
        }

        @Override
        public BlockPos getPos() {
            return pos;
        }

        @Override
        public FormationNodeType getNodeType(Level level) {
            return type;
        }

        @Override
        public Identifier getDimension() {
            return dimension;
        }
    }
}
