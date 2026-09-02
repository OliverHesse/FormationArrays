package net.lucent.formation_arrays.core.formations.state_handled;

import io.netty.buffer.ByteBuf;
import net.lucent.formation_arrays.api.formations.Formation;
import net.lucent.formation_arrays.api.formations.FormationInstance;
import net.lucent.formation_arrays.api.formations.FormationRuntimeData;
import net.lucent.formation_arrays.api.nodes.FormationNodeType;
import net.lucent.formation_arrays.api.nodes.NodeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Set;

public class StateHandledFormationInstance<T extends FormationRuntimeData,S extends FormationStateHandler> implements FormationInstance {
    private final StateHandledFormation<T,S> formation;
    public final T runtimeData;
    public final S handler;
    private boolean wasActiveLastTick = false;

    public StateHandledFormationInstance(StateHandledFormation<T, S> formation, T runtimeData, S handler) {
        this.formation = formation;
        this.runtimeData = runtimeData;
        this.handler = handler;
    }


    public boolean wasActive(){
        return wasActiveLastTick;
    }
    @Override
    public boolean tick(Level level,NodeManager manager){
        boolean active = handler.isActive(level,manager);
        if(wasActiveLastTick && !active){
            formation.deactivate(runtimeData,level);
            wasActiveLastTick = false;
            return true;
        }else if(!wasActiveLastTick && active){
            formation.activate(runtimeData,level);
            formation.tick(runtimeData,level);
            wasActiveLastTick = true;
            return true;
        }

        return active && formation.tick(runtimeData, level);

    }

    @Override
    public void created(Level level, NodeManager manager) {

    }

    @Override
    public void destroyed(Level level,NodeManager manager) {
        formation.destroy(runtimeData,level);
    }

    @Override
    public Formation<?, ?> getFormation() {
        return formation;
    }


    @Override
    public Set<BlockPos> getListenedNodePositions() {
        return handler.getListenedNodePositions();
    }

    @Override
    public void nodeTypesChanged(Level level, NodeManager nodeManager, BlockPos pos) {

    }

    @Override
    public void nodeLoaded(Level level, NodeManager nodeManager, BlockPos pos) {

    }

    @Override
    public void nodeUnloaded(Level level, NodeManager nodeManager, BlockPos pos) {

    }

    @Override
    public boolean isValid(Level level, NodeManager nodeManager) {
        return handler.isValid(level,nodeManager);
    }

    @Override
    public void write(ValueOutput output, RegistryAccess access) {
        formation.writeFormationInstance(output,this,access);
    }

    @Override
    public void encode(ByteBuf buf, RegistryAccess access) {
        formation.encodeFormationInstance(buf,access,this);
    }

    @Override
    public void decode(ByteBuf buf, RegistryAccess access) {
        //not a client instance so does nothing
    }
}
