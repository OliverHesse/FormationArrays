package net.lucent.formation_arrays.api.v2.nodes.accessor;

import net.lucent.formation_arrays.api.v2.nodes.FormationNode;
import net.lucent.formation_arrays.api.v2.nodes.FormationNodeType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
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
    record Unloaded(BlockPos pos, FormationNodeType type) implements FormationNodeReference{

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
            return List.of(type);
        }

        @Override
        public boolean isType(FormationNodeType type, Level level) {
            return type == this.type;
        }

    }
}
