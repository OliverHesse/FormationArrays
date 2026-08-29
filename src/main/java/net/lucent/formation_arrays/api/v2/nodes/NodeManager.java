package net.lucent.formation_arrays.api.v2.nodes;

import net.lucent.formation_arrays.api.v2.nodes.accessor.FormationNodeReference;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
//TODO add descriptions to each
public interface NodeManager {

    void clearNodesAt(BlockPos pos);
    void unloadNodes(BlockPos pos);

    void addNode(Entity entity);
    void addNode(BlockPos pos);

    void updateNode(BlockPos pos);
    void updateNode(Entity entity);

    void removeNode(Entity entity);
    void removeNode(BlockPos pos);

    Collection<BlockPos> getAllNodeLocations();

    boolean hasNodeType(BlockPos pos, FormationNodeType type);
    Set<FormationNodeType> getTypes(BlockPos pos);
    boolean isLoaded(BlockPos pos);

}
