package net.lucent.formation_arrays.api.nodes.type_provider.block;

import net.lucent.formation_arrays.api.nodes.FormationNodeType;
import net.lucent.formation_arrays.api.nodes.type_provider.NodeTypeProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;

import java.util.Collection;

/**
 * A Contextless provider, everytime it is queried it is provided the context
 */
public interface BlockNodeTypeProvider extends NodeTypeProvider {

    BlockNodeTypeFactory getType();

    @Override
    default Collection<FormationNodeType> getTypes(Level level,BlockPos pos){
        return getTypes(level,getState(level,pos),pos);
    }
    @Override
    default boolean isType(Level level,FormationNodeType type,BlockPos pos){
        return isType(level,getState(level,pos),pos,type);
    }

    Collection<FormationNodeType> getTypes(Level level,BlockState state,BlockPos pos);
    boolean isType(Level level,BlockState state,BlockPos pos,FormationNodeType type);

    default BlockState getState(Level level,BlockPos pos){
        ChunkPos chunkPos = ChunkPos.containing(pos);

        ChunkAccess access = level.getChunkSource().getChunk(chunkPos.x(), chunkPos.z(), ChunkStatus.FULL,false);
        if(access == null) return null;
        return access.getBlockState(pos);
    }

}
