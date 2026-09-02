package net.lucent.formation_arrays.api.nodes.type_provider;

import net.lucent.formation_arrays.api.CoreDataMaps;
import net.lucent.formation_arrays.api.nodes.FormationNodeType;
import net.lucent.formation_arrays.util.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.List;

public sealed interface NodeTypeProviderHolder  permits
        NodeTypeProviderHolder.EntityNodeTypeProviderHolder,
        NodeTypeProviderHolder.BlockNodeTypeProviderHolder

{
    NodeTypeProvider getTypeProvider(Level level);
    BlockPos getPos();


    default Collection<FormationNodeType> getNodeTypes(Level level){
        NodeTypeProvider provider = getTypeProvider(level);
        return provider == null ? List.of() : provider.getTypes(level,getPos());
    }
    default boolean isType(Level level,FormationNodeType type){
        NodeTypeProvider provider = getTypeProvider(level);
        return provider != null && provider.isType(level, type,getPos());
    }


    record EntityNodeTypeProviderHolder(
            Entity entity) implements NodeTypeProviderHolder {


        @Override
        public NodeTypeProvider getTypeProvider(Level level) {
            return null;//TODO
        }

        @Override
        public BlockPos getPos() {
            return entity.blockPosition();
        }
    }

    record BlockNodeTypeProviderHolder(BlockPos pos) implements NodeTypeProviderHolder {

        @Override
        public NodeTypeProvider getTypeProvider(Level level) {
            return BlockUtil.getDataMapEntry(CoreDataMaps.BLOCK_NODE_TYPE_PROVIDER,level,getPos());
        }

        @Override
        public BlockPos getPos() {
            return pos;
        }
    }
}
