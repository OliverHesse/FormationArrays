package net.lucent.formation_arrays.api.nodes.type_provider;

import net.lucent.formation_arrays.api.nodes.FormationNodeType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.Collection;

public sealed interface NodeTypeProviderReference permits
        NodeTypeProviderReference.Loaded,
        NodeTypeProviderReference.Unloaded {

    BlockPos getPos();
    Collection<FormationNodeType> getNodeTypes(Level level);
    boolean isType(Level level,FormationNodeType type);
    boolean isLoaded();

    static Loaded of(BlockPos pos){
        return new Loaded(new NodeTypeProviderHolder.BlockNodeTypeProviderHolder(pos));
    }
    static Loaded of(Entity entity){
        return new Loaded(new NodeTypeProviderHolder.EntityNodeTypeProviderHolder(entity));
    }
    static Unloaded of(BlockPos pos,Collection<FormationNodeType> types){
        return new Unloaded(pos,types);
    }
    record Loaded(NodeTypeProviderHolder holder) implements NodeTypeProviderReference{

        @Override
        public BlockPos getPos() {
            return holder.getPos();
        }

        @Override
        public Collection<FormationNodeType> getNodeTypes(Level level) {
            return holder.getNodeTypes(level);
        }

        @Override
        public boolean isType(Level level,FormationNodeType type) {
            return holder.isType(level,type);
        }

        @Override
        public boolean isLoaded() {
            return true;
        }
    }

    record Unloaded(BlockPos pos,Collection<FormationNodeType> types) implements NodeTypeProviderReference{

        @Override
        public BlockPos getPos() {
            return pos;
        }

        @Override
        public Collection<FormationNodeType> getNodeTypes(Level level) {
            return types;
        }

        @Override
        public boolean isType(Level level, FormationNodeType type) {
            return types.contains(type);
        }

        @Override
        public boolean isLoaded() {
            return false;
        }
    }
}
