package net.lucent.formation_arrays.api.nodes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

import java.util.Collection;
import java.util.Set;
//TODO add descriptions to each
public interface NodeManager {

    void loadNode(BlockPos pos);
    void unloadNode(BlockPos pos);

    void addNodeProvider(Entity entity);
    void addNodeProvider(BlockPos pos);

    void updateNodeProvider(BlockPos pos);
    void updateNodeProvider(Entity entity);

    void removeNodeProvider(Entity entity);
    void removeNodeProvider(BlockPos pos);

    Collection<BlockPos> getAllNodeLocations();

    boolean hasNodeType(BlockPos pos, FormationNodeType type);
    Set<FormationNodeType> getTypes(BlockPos pos);
    boolean isLoaded(BlockPos pos);

}
