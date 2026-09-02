package net.lucent.formation_arrays.util;

import net.lucent.formation_arrays.api.CoreDataMaps;
import net.lucent.formation_arrays.capabilities.CoreCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.neoforged.neoforge.registries.datamaps.DataMapEntry;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

import java.util.Set;
import java.util.stream.Collectors;

public class BlockUtil {


    public static BlockState getBlockNoForceLoad(Level level, BlockPos pos){
        ChunkPos chunkPos = ChunkPos.containing(pos);

        ChunkAccess access = level.getChunkSource().getChunk(chunkPos.x(), chunkPos.z(), ChunkStatus.FULL,false);

        if(access == null) return null;
        return access.getBlockState(pos);
    }

    public static ResourceKey<Block> getBlockResourceKey(Level level, BlockPos pos){
        BlockState state = getBlockNoForceLoad(level,pos);
        return state == null ? null : BuiltInRegistries.BLOCK.getResourceKey(state.getBlock()).orElse(null);
    }
    public static <T> T getDataMapEntry(DataMapType<Block,T> dataMapType, Level level, BlockPos pos){
        ResourceKey<Block> key = getBlockResourceKey(level,pos);
        return key == null ? null : BuiltInRegistries.BLOCK.getData(dataMapType,key);
    }
    //used to loop through chunks easier
    public static Set<Block> getNodeTypeProviders(){
        return BuiltInRegistries.BLOCK.getDataMap(CoreDataMaps.BLOCK_NODE_TYPE_PROVIDER).keySet().stream().map(
                BuiltInRegistries.BLOCK::getValue
        ).collect(Collectors.toSet());
    }
    public static boolean isNodeTypeProvider(Level level,BlockPos pos){
        return getDataMapEntry(CoreDataMaps.BLOCK_NODE_TYPE_PROVIDER,level,pos) != null;
    }

}
