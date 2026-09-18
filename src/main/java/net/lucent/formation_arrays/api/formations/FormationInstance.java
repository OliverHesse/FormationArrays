package net.lucent.formation_arrays.api.formations;

import io.netty.buffer.ByteBuf;
import net.lucent.formation_arrays.api.CoreRegistries;
import net.lucent.formation_arrays.api.nodes.NodeManager;

import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.zic.zenithlib.network.ByteBufHelpers;

import java.util.Set;

public class FormationInstance<T extends RuntimeData,S extends StateHandler>{



    private final Formation<T,S> formation;
    public T runtimeData;
    public S stateHandler;
    boolean wasActiveLastTick = false;

    public FormationInstance(Formation<T,S> formation,T runtimeData,S stateHandler){
        this.formation = formation;
        this.runtimeData = runtimeData;
        this.stateHandler = stateHandler;
    }

    //mainly used when loading formations
    public boolean isMalformed(){
        return formation == null || runtimeData == null || stateHandler == null;
    }
    public static FormationInstance<?,?> malformed(){
        return new FormationInstance<>(null,null,null);
    }

    /**
     * @param level the level it was ticked in
     * @return true -> dirty so trigger sync, false -> not dirty no sync
     */
    public boolean tick(Level level, NodeManager nodeManager){
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

    public Formation<T, S> getFormation() {
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

    public Identifier getFormationKey(RegistryAccess access){
        return CoreRegistries.FORMATIONS.get(access).getKey(formation);
    }
    public static Formation<?,?> getFormation(Identifier key,RegistryAccess access){
        return CoreRegistries.FORMATIONS.get(access).containsKey(key) ? CoreRegistries.FORMATIONS.get(access).getValue(key) : null;
    }

    public void encode(ByteBuf buf, RegistryAccess access){
        buf.writeBoolean(wasActiveLastTick);
        ByteBufHelpers.encodeIdentifier(getFormationKey(access),buf);
        formation.runtimeDataSyncHandler().encode(runtimeData,buf,access);
        formation.stateHandlerSyncHandler().encode(stateHandler,buf,access);
    }
    public void decode(ByteBuf buf,RegistryAccess access){

    }
    //if no formation instance exists use this
    public static FormationInstance<?,?> initialDecode(ByteBuf buf,RegistryAccess access){
        boolean active = buf.readBoolean();
        Formation<?,?> formation = getFormation(ByteBufHelpers.decodeIdentifier(buf),access);
        if(formation == null) return null;
        ClientFormationInstance<?,?> instance = formation.createFormationInstance(buf,access);
        if(instance == null) return null;
        instance.active = true;
        return instance;
    }

    public void write(ValueOutput output, RegistryAccess access) {
        output.putString("formation",getFormationKey(access).toString());
        formation.runtimeDataSerializer().write(runtimeData,output.child("runtime_data"),access);
        formation.stateHandlerSerializer().write(stateHandler,output.child("state_handler"),access);
    }

    public static FormationInstance<?,?> load(ValueInput input,RegistryAccess access){
        Formation<?,?> formation = getFormation(Identifier.parse(input.getStringOr("formation","none")),access);
        if(formation == null) return malformed();
        FormationInstance<?,?> instance = formation.createFormationInstance(input.childOrEmpty("runtime_data"),input.childOrEmpty("state_handler"),access);
        return instance == null ? malformed() : instance;
    }


    public static class ClientFormationInstance<T extends RuntimeData,S extends StateHandler> extends FormationInstance<T,S>{

        private boolean active = false;
        public ClientFormationInstance(Formation<T, S> formation, T runtimeData, S stateHandler) {
            super(formation, runtimeData, stateHandler);
        }
        @Override
        public boolean tick(Level level, NodeManager nodeManager){

            if (active != wasActiveLastTick) {
                if (active) {
                    getFormation().activate(runtimeData, stateHandler, level);
                    getFormation().tick(runtimeData, stateHandler, level);
                } else {
                    getFormation().deactivate(runtimeData, stateHandler, level);
                }

                wasActiveLastTick = active;
                return true;
            }

            return active && getFormation().tick(runtimeData, stateHandler, level);
        }

        @Override
        public void decode(ByteBuf buf, RegistryAccess access) {
            active = buf.readBoolean();
            Identifier formation = ByteBufHelpers.decodeIdentifier(buf);
            runtimeData = getFormation().runtimeDataSyncHandler().decode(buf,access);
            stateHandler = getFormation().stateHandlerSyncHandler().decode(buf,access);
        }
    }
}
