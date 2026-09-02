package net.lucent.formation_arrays.core.nodes;

import net.lucent.formation_arrays.FormationArrays;
import net.lucent.formation_arrays.util.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.Collection;

@EventBusSubscriber(modid = FormationArrays.MOD_ID)
public class NodeManagerEventHandler {

    @SubscribeEvent
    public static void onLoad(ChunkEvent.Load event) {
        if(!(event.getChunk().getLevel() instanceof ServerLevel serverLevel)) return;


        //if there was a pos in this chunk, clear all unloaded nodes

        DimensionNodeManager nodeManager = DimensionNodeManager.getNodeManger(serverLevel);
        ChunkPos chunkPos = event.getChunk().getPos();


        for(BlockPos pos : nodeManager.getAllNodeLocations()){
            if(chunkPos.contains(pos)) {
                nodeManager.loadNode(pos);
            };
        }

        Collection<Block> potentialNodeProviders = BlockUtil.getNodeTypeProviders();
        //discover nodes in this chunk
        for(int i = 0; i<event.getChunk().getSectionsCount(); i++){
            LevelChunkSection section = event.getChunk().getSection(i);
            if(!hasPotentialNodeProviders(potentialNodeProviders,section)) continue;

            discoverNodeProviders(potentialNodeProviders,event.getChunk(),section,i);
        }

    }
    @SubscribeEvent
    public static void onUnload(ChunkEvent.Unload event){
        if(!(event.getChunk().getLevel() instanceof  ServerLevel serverLevel)) return;

        //if a pos is in this chunk unload nodes at that pos

        DimensionNodeManager nodeManager = DimensionNodeManager.getNodeManger(serverLevel);
        ChunkPos chunkPos = event.getChunk().getPos();
        for(BlockPos pos : nodeManager.getAllNodeLocations()){
            if(chunkPos.contains(pos)) {

                nodeManager.unloadNode(pos);

            };
        }

    }
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event){
        event.getServer().getAllLevels().forEach(NodeManagerEventHandler::processServer);
    }

    public static void processServer(ServerLevel level){
       DimensionNodeManager.getNodeManger(level).calculateCachedTypes();
    }

    public static boolean hasPotentialNodeProviders(Collection<Block> blocks, LevelChunkSection section){
        return section.maybeHas(state->blocks.contains(state.getBlock()));
    }

    public static void discoverNodeProviders(Collection<Block> nodeTypeProviders,ChunkAccess chunk, LevelChunkSection section, int sectionId){
        SectionPos sectionPos = SectionPos.of(chunk.getPos(),chunk.getSectionYFromSectionIndex(sectionId));

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {

                    BlockState state = section.getBlockState(x,y,z);
                    if (nodeTypeProviders.contains(state.getBlock())) {

                        BlockPos globalPos = new BlockPos(
                                sectionPos.minBlockX() + x,
                                sectionPos.minBlockY() + y,
                                sectionPos.minBlockZ() + z
                        );


                        DimensionNodeManager.getNodeManger((ServerLevel) chunk.getLevel()).addNodeProvider(globalPos);
                    }
                }
            }
        }
    }
}
