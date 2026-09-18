package net.lucent.formation_arrays.api.formations.v2;

import io.netty.buffer.ByteBuf;
import net.lucent.formation_arrays.api.formations.v2.util.SerializerHandler;
import net.lucent.formation_arrays.api.formations.v2.util.SyncHandler;
import net.lucent.formation_arrays.api.nodes.FormationNodeType;
import net.lucent.formation_arrays.api.nodes.NodeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;

public interface Formation<T extends RuntimeData,S extends StateHandler>{



    default FormationInstance<T,S> createFormationInstance(NodeManager nodeManager, BlockPos pos, FormationNodeType type){
        S handler = createStateHandler(nodeManager,pos,type);
        T data = createRuntimeData(handler);
        return createFormationInstance(data,handler);
    }

    default FormationInstance<T,S> createFormationInstance(T runtimeData,S stateHandler){
        return new FormationInstance<>(this,runtimeData,stateHandler);
    }
    default FormationInstance<T,S> createFormationInstance(ByteBuf buf,RegistryAccess access){
        return new FormationInstance<>(this, runtimeDataSyncHandler().decode(buf,access),stateHandlerSyncHandler().decode(buf,access));
    }
    default FormationInstance<T,S> createFormationInstance(ValueInput runtimeInput,ValueInput stateHandlerInput,RegistryAccess access){
        return new FormationInstance<>(this, runtimeDataSerializer().read(runtimeInput,access), stateHandlerSerializer().read(stateHandlerInput,access));
    }

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
