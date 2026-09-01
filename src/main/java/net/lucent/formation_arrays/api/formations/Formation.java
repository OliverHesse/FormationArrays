package net.lucent.formation_arrays.api.formations;

import net.lucent.formation_arrays.api.nodes.FormationNodeType;
import net.lucent.formation_arrays.api.nodes.NodeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Set;

public interface Formation<T extends FormationRuntimeData,S extends FormationInstance>{

    FormationType getType();

    /**
     * returns a set of node types required to activate
     */
    Set<FormationNodeType> activationNodes();

    /**
     * when an activation node type is detected, tests to see if we can activate the formation
     * @param nodeManager the mangar handling nodes
     * @param pos the position of the node
     * @param type the type we detected being added
     * @return true -> create a formation, false -> do not create a formation
     */
    boolean tryActive(NodeManager nodeManager, BlockPos pos, FormationNodeType type);

    /**
     * given an activation node, return the set of positions of all required nodes, this will be used
     * to uniquely identify a formation, and ensure a duplicate formation is not created
     * (NOTE: in its current implementation it does not check for type of the nodes, so if a formation supports orientation
     * it will still trigger as a duplicate)
     * @param nodeManager the mangar handling nodes
     * @param pos the position of the node
     * @param type the type we detected being added
     * @return a set of required activation node positions
     */
    Set<BlockPos> getRequiredActivationNodes(NodeManager nodeManager, BlockPos pos, FormationNodeType type);

    /**
     * Creates a fresh instance from a given node and node type
     * @param nodeManager the mangar handling nodes
     * @param pos the position of the node
     * @param type the type we detected being added
     * @return a new formation instance
     */
    S createFormationInstance(NodeManager nodeManager, BlockPos pos, FormationNodeType type);
    S loadFormationInstance(ValueInput input);
    void writeFormationInstance(ValueOutput output, S instance, RegistryAccess access);

    T createRuntimeData();
    T loadRuntimeData(ValueInput input);
    void writeRuntimeData(ValueOutput output,T runtimeData, RegistryAccess access);


}
