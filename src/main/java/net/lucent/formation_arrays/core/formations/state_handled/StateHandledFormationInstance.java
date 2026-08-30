package net.lucent.formation_arrays.core.formations.state_handled;

import net.lucent.formation_arrays.api.v2.CoreRegistries;
import net.lucent.formation_arrays.api.v2.formations.Formation;
import net.lucent.formation_arrays.api.v2.formations.FormationInstance;
import net.lucent.formation_arrays.api.v2.formations.FormationRuntimeData;
import net.lucent.formation_arrays.api.v2.nodes.NodeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

import java.util.Set;

public class StateHandledFormationInstance<T extends FormationRuntimeData,S extends FormationStateHandler> implements FormationInstance {
    private final StateHandledFormation<T,S> formation;
    private final T runtimeData;
    private final S handler;
    private boolean wasActiveLastTick = false;

    public StateHandledFormationInstance(StateHandledFormation<T, S> formation, T runtimeData, S handler) {
        this.formation = formation;
        this.runtimeData = runtimeData;
        this.handler = handler;
    }

    @Override
    public void tick(Level level){
        if(wasActiveLastTick && !handler.isActive(level)){
            //TODO deactivate
        }else if(!wasActiveLastTick && handler.isActive(level)){
            //TODO activate
        }
        wasActiveLastTick = handler.isActive(level);
        formation.tick(runtimeData,handler,level);
    }

    @Override
    public void destroy(Level level) {
        formation.destroy(runtimeData,handler,level);
    }

    @Override
    public Set<BlockPos> getListenedNodePositions() {
        return Set.of();
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
        return handler.isValid(level);
    }

    //important!! since this is saved so we can retrieve the proper instance
    public Identifier getFormationId(RegistryAccess access){
        return CoreRegistries.FORMATIONS.get(access).getKey(formation);
    }
}
