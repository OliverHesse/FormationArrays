package net.lucent.formation_arrays.test;

import net.lucent.formation_arrays.FormationArrays;
import net.lucent.formation_arrays.api.formations.FormationType;
import net.lucent.formation_arrays.api.nodes.FormationNodeType;
import net.lucent.formation_arrays.api.nodes.NodeManager;
import net.lucent.formation_arrays.core.formations.activation.FormationActivationRecipe;
import net.lucent.formation_arrays.core.formations.state_handled.SimpleFormationStateHandler;
import net.lucent.formation_arrays.core.formations.state_handled.StateHandledFormation;
import net.lucent.formation_arrays.core.formations.state_handled.StateHandledFormationInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Optional;
import java.util.Set;

public record DebugTestFormation(FormationActivationRecipe recipe,Optional<BlockPos> controlNode) implements StateHandledFormation<EmptyRuntimeData, SimpleFormationStateHandler> {

    @Override
    public void activate(EmptyRuntimeData runtimeData, SimpleFormationStateHandler state, Level level) {
        FormationArrays.LOGGER.debug("DEBUG FORMATION ACTIVATED");
    }

    @Override
    public void deactivate(EmptyRuntimeData runtimeData, SimpleFormationStateHandler state, Level level) {
        FormationArrays.LOGGER.debug("DEBUG FORMATION DEACTIVATED");
    }

    @Override
    public void destroy(EmptyRuntimeData runtimeData, SimpleFormationStateHandler state, Level level) {
        FormationArrays.LOGGER.debug("DEBUG FORMATION DESTROYED");
    }

    @Override
    public void tick(EmptyRuntimeData runtimeData, SimpleFormationStateHandler state, Level level) {
        FormationArrays.LOGGER.debug("DEBUG FORMATION TICKED");
    }

    @Override
    public FormationType getType() {
        return null; //TODO
    }

    @Override
    public Set<FormationNodeType> activationNodes() {
        return recipe.getUniqueTypes();
    }

    @Override
    public boolean tryActive(NodeManager nodeManager, BlockPos pos, FormationNodeType type) {
        return recipe().tryActive(nodeManager,pos,type);
    }

    @Override
    public Set<BlockPos> getRequiredActivationNodes(NodeManager nodeManager, BlockPos pos, FormationNodeType type) {
        return recipe.getGlobalNodePositions(nodeManager,pos,type);
    }

    @Override
    public StateHandledFormationInstance<EmptyRuntimeData, SimpleFormationStateHandler> createFormationInstance(NodeManager nodeManager, BlockPos pos, FormationNodeType type) {

        int controlIndex = -1;
        if(controlNode.isPresent()) controlIndex = recipe.getIndexOf(controlNode.get());
        SimpleFormationStateHandler stateHandler = SimpleFormationStateHandler.of(recipe.getGlobalNodes(nodeManager,pos,type),controlIndex);

        return new StateHandledFormationInstance<>(this,createRuntimeData(),stateHandler);
    }

    @Override
    public StateHandledFormationInstance<EmptyRuntimeData, SimpleFormationStateHandler> loadFormationInstance(ValueInput input) {
        return null; //TODO
    }

    @Override
    public void writeFormationInstance(ValueOutput output, StateHandledFormationInstance<EmptyRuntimeData, SimpleFormationStateHandler> instance) {
        //TODO
    }

    @Override
    public EmptyRuntimeData createRuntimeData() {
        return new EmptyRuntimeData();
    }

    @Override
    public EmptyRuntimeData loadRuntimeData(ValueInput input) {
        return createRuntimeData();
    }

    @Override
    public void writeRuntimeData(ValueOutput output, EmptyRuntimeData runtimeData) {

    }
}
