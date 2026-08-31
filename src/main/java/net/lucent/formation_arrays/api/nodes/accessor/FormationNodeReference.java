package net.lucent.formation_arrays.api.nodes.accessor;

import net.lucent.formation_arrays.api.nodes.FormationNode;
import net.lucent.formation_arrays.api.nodes.FormationNodeType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.List;

public interface FormationNodeReference {


    boolean isLoaded();
    FormationNode get(Level level);
    FormationNodeHolder getHolder();
    BlockPos getPos();
    Collection<FormationNodeType> getNodeTypes(Level level);
    boolean isType(FormationNodeType type,Level level);

    static Loaded of(BlockPos pos){
        return new Loaded(new FormationNodeHolder.BlockFormationNodeHolder(pos));
    }
    static Loaded of(Entity entity){
        return new Loaded(new FormationNodeHolder.EntityFormationNodeHolder(entity));
    }
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
        public Collection<FormationNodeType> getNodeTypes(Level level) {
            return get(level).getTypes();
        }

        @Override
        public boolean isType(FormationNodeType type, Level level) {
            return holder.getNode(level) != null && holder.getNode(level).isType(type);
        }




    }
    record Unloaded(BlockPos pos, List<FormationNodeType> types) implements FormationNodeReference{

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
        public Collection<FormationNodeType> getNodeTypes(Level level) {
            return types;
        }

        @Override
        public boolean isType(FormationNodeType type, Level level) {
            return types.contains(type);
        }

    }
}
