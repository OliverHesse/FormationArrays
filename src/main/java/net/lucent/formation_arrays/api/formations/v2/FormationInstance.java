package net.lucent.formation_arrays.api.formations.v2;

import io.netty.buffer.ByteBuf;
import net.lucent.formation_arrays.api.CoreRegistries;
import net.lucent.formation_arrays.api.nodes.NodeManager;

import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.zic.zenithlib.network.ByteBufHelpers;

import java.util.Set;

public class FormationInstance<T extends RuntimeData,S extends StateHandler>{
    private final Formation<T,S> formation;
    public T runtimeData;
    public S stateHandler;
    private boolean wasActiveLastTick = false;

    public FormationInstance(Formation<T,S> formation,T runtimeData,S stateHandler){
        this.formation = formation;
        this.runtimeData = runtimeData;
        this.stateHandler = stateHandler;
    }

    /**
     * @param runtimeData the current runtime data for this formation instance
     * @param stateHandler the current state handler for this formation instance
     * @param level the level it was ticked in
     * @return true -> dirty so trigger sync, false -> not dirty no sync
     */
    public boolean tick(T runtimeData, S stateHandler, Level level, NodeManager nodeManager){
        boolean active = stateHandler.isActive(level, nodeManager);

        if (active != wasActiveLastTick) {
            if (active) {
                formation.activate(runtimeData, stateHandler, level);
                formation.tick(runtimeData, stateHandler, level);
            } else {
                formation.deactivate(runtimeData, stateHandler, level);
            }

            wasActiveLastTick = active;
            return true;
        }

        return active && formation.tick(runtimeData, stateHandler, level);
    }



    public void destroyed(Level level,NodeManager manager) {
        formation.destroy(runtimeData,stateHandler,level);
    }

    public Formation<?, ?> getFormation() {
        return formation;
    }

    public Set<BlockPos> getListenedNodePositions() {
        return stateHandler.getListenedNodePositions();
    }
    public boolean isValid(Level level, NodeManager nodeManager) {
        return stateHandler.isValid(level,nodeManager);
    }

    //Not sure what to do with these, might just hook them into state handler
    public void nodeTypesChanged(Level level, NodeManager nodeManager, BlockPos pos) {
        stateHandler.nodeTypesChanged(level,nodeManager,pos);
    }
    public void nodeLoaded(Level level, NodeManager nodeManager, BlockPos pos) {
        stateHandler.nodeLoaded(level,nodeManager,pos);
    }
    public void nodeUnloaded(Level level, NodeManager nodeManager, BlockPos pos) {
        stateHandler.nodeUnloaded(level,nodeManager,pos);
    }

    public void encode(ByteBuf buf, RegistryAccess access){
        ByteBufHelpers.encodeIdentifier(getFormationKey(access),buf);
        formation.runtimeDataSyncHandler().encode(runtimeData,buf,access);
        formation.stateHandlerSyncHandler().encode(stateHandler,buf,access);
    }
    public Identifier getFormationKey(RegistryAccess access){
        //TODO update when i have changed registries to use new v2
        return Identifier.parse("none");
    }
    public void decode(ByteBuf buf,RegistryAccess access){
        //TODO
    }
    //if no formation instance exists use this
    public static FormationInstance<?,?> initialDecode(ByteBuf buf,RegistryAccess access){
        return  null;//TODO
    }
}
