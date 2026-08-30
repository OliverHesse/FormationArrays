package net.lucent.formation_arrays.api.v2.nodes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

import java.util.Collection;
import java.util.Set;
//TODO add descriptions to each
public interface NodeManager {

    void loadNodes(BlockPos pos);
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
