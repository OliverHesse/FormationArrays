package net.lucent.formation_arrays.api.nodes.accessor;


import net.lucent.formation_arrays.api.nodes.FormationNode;
import net.lucent.formation_arrays.capabilities.CoreCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;

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
                ChunkPos chunkPos = ChunkPos.containing(pos);

                ChunkAccess access = level.getChunkSource().getChunk(chunkPos.x(), chunkPos.z(), ChunkStatus.FULL,false);
                if(access == null) return null;
                BlockState state = access.getBlockState(pos);
                return CoreCapabilities.BLOCK_FORMATION_NODE.getCapability(level,pos,state,null,null);
            }

            @Override
            public BlockPos getPosition() {
                return pos;
            }

        }
}
