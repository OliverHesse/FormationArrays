package net.lucent.formation_arrays.api.formations;

import io.netty.buffer.ByteBuf;
import net.lucent.formation_arrays.api.formations.util.SerializerHandler;
import net.lucent.formation_arrays.api.formations.util.SyncHandler;
import net.lucent.formation_arrays.api.nodes.FormationNodeType;
import net.lucent.formation_arrays.api.nodes.NodeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;

import java.util.Set;

public interface Formation<T extends RuntimeData,S extends StateHandler>{



    default FormationInstance<T,S> createFormationInstance(NodeManager nodeManager, BlockPos pos, FormationNodeType type){
        S handler = createStateHandler(nodeManager,pos,type);
        T data = createRuntimeData(handler);
        return createFormationInstance(data,handler);
    }

    default FormationInstance<T,S> createFormationInstance(T runtimeData,S stateHandler){
        return new FormationInstance<>(this,runtimeData,stateHandler);
    }
    default FormationInstance.ClientFormationInstance<T,S> createFormationInstance(ByteBuf buf, RegistryAccess access){
        return new FormationInstance.ClientFormationInstance<>(this, runtimeDataSyncHandler().decode(buf,access),stateHandlerSyncHandler().decode(buf,access));
    }
    default FormationInstance<T,S> createFormationInstance(ValueInput runtimeInput,ValueInput stateHandlerInput,RegistryAccess access){
        return new FormationInstance<>(this, runtimeDataSerializer().read(runtimeInput,access), stateHandlerSerializer().read(stateHandlerInput,access));
    }

    FormationType getType();

    //returns a set of node types required to activate
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


    T createRuntimeData(StateHandler handler);
    SerializerHandler<T> runtimeDataSerializer();
    SyncHandler<T> runtimeDataSyncHandler();

    S createStateHandler(NodeManager nodeManager, BlockPos pos, FormationNodeType type);
    SerializerHandler<S> stateHandlerSerializer();
    SyncHandler<S> stateHandlerSyncHandler();


    void activate(T runtimeData,S stateHandler, Level level);
    void deactivate(T runtimeData,S stateHandler, Level level);
    void destroy(T runtimeData,S stateHandler, Level level);

    /**
     * @param runtimeData the current runtime data for this formation instance
     * @param stateHandler the current state handler for this formation instance
     * @param level the level it was ticked in
     * @return true -> dirty so trigger sync, false -> not dirty no sync
     */
    boolean tick(T runtimeData,S stateHandler,  Level level);
}
