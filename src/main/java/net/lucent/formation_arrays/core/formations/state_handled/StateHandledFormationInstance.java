package net.lucent.formation_arrays.core.formations.state_handled;

import net.lucent.formation_arrays.api.formations.Formation;
import net.lucent.formation_arrays.api.formations.FormationInstance;
import net.lucent.formation_arrays.api.formations.FormationRuntimeData;
import net.lucent.formation_arrays.api.nodes.FormationNodeType;
import net.lucent.formation_arrays.api.nodes.NodeManager;
import net.minecraft.core.BlockPos;
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
    public void tick(Level level,NodeManager manager){
        boolean active = handler.isActive(level,manager);
        if(wasActiveLastTick && !active){
            formation.deactivate(runtimeData,handler,level);
        }else if(!wasActiveLastTick && active){
            formation.activate(runtimeData,handler,level);
        }

        wasActiveLastTick = active;
        if(active) formation.tick(runtimeData,handler,level);
    }

    @Override
    public void created(Level level, NodeManager manager) {

    }

    @Override
    public void destroyed(Level level,NodeManager manager) {
        formation.destroy(runtimeData,handler,level);
    }

    @Override
    public Formation<?, ?> getFormation() {
        return formation;
    }

    @Override
    public Set<BlockPos> getListenedNodePositions(NodeManager manager, BlockPos pos, FormationNodeType type) {
        return formation.getRequiredActivationNodes(manager,pos,type);
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


}
