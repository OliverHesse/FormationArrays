package net.lucent.formation_arrays.api.v2.formations;

import net.lucent.formation_arrays.api.v2.nodes.FormationNodeType;
import net.lucent.formation_arrays.api.v2.nodes.NodeManager;
import net.lucent.formation_arrays.core.formations.state_handled.FormationStateHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Set;

public interface Formation<T extends FormationRuntimeData,S extends FormationInstance>{

    FormationType getType();

    //returns a set of node types required to activate
    Set<FormationType> activationNodes();

    /**
     * when an activation node type is detected, tests to see if we can activate the formation
     * @param nodeManager the mangar handling nodes
     * @param pos the position of the node
     * @param type the type we detected being added
     * @return true -> create a formation, false -> do not create a formation
     */
    boolean tryActive(NodeManager nodeManager, BlockPos pos, FormationNodeType type);

    /**
     * Creates a fresh instance from a given node and node type
     * @param nodeManager the mangar handling nodes
     * @param pos the position of the node
     * @param type the type we detected being added
     * @return a new formation instance
     */
    S createFormationInstance(NodeManager nodeManager, BlockPos pos, FormationNodeType type);
    S loadFormationInstance(ValueInput input);
    void writeFormationInstance(ValueOutput output,S instance);

    FormationRuntimeData createRuntimeData();
    FormationRuntimeData loadRuntimeData(ValueInput input);
    void writeRuntimeData(ValueOutput output,T runtimeData);


}
