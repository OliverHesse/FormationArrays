package net.lucent.formation_arrays.test;

import net.lucent.formation_arrays.FormationArrays;
import net.lucent.formation_arrays.api.formations.FormationType;
import net.lucent.formation_arrays.api.nodes.FormationNodeType;
import net.lucent.formation_arrays.api.nodes.NodeManager;
import net.lucent.formation_arrays.core.formations.MalformedFormationInstance;
import net.lucent.formation_arrays.core.formations.activation.FormationActivationRecipe;
import net.lucent.formation_arrays.core.formations.state_handled.SimpleFormationStateHandler;
import net.lucent.formation_arrays.core.formations.state_handled.StateHandledFormation;
import net.lucent.formation_arrays.core.formations.state_handled.StateHandledFormationInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Optional;
import java.util.Set;

public record DebugTestFormation(FormationActivationRecipe recipe,Optional<BlockPos> controlNode) implements StateHandledFormation<TickCounterRuntimeData, SimpleFormationStateHandler> {

    @Override
    public void activate(TickCounterRuntimeData runtimeData, SimpleFormationStateHandler state, Level level) {
        FormationArrays.LOGGER.debug("DEBUG FORMATION ACTIVATED");
    }

    @Override
    public void deactivate(TickCounterRuntimeData runtimeData, SimpleFormationStateHandler state, Level level) {
        FormationArrays.LOGGER.debug("DEBUG FORMATION DEACTIVATED");
    }

    @Override
    public void destroy(TickCounterRuntimeData runtimeData, SimpleFormationStateHandler state, Level level) {
        FormationArrays.LOGGER.debug("DEBUG FORMATION DESTROYED");
    }

    @Override
    public void tick(TickCounterRuntimeData runtimeData, SimpleFormationStateHandler state, Level level) {
        FormationArrays.LOGGER.debug("DEBUG FORMATION TICKED ({})", runtimeData.ticks);
        runtimeData.ticks ++;
    }

    @Override
    public FormationType getType() {
        return TestRegistries.DEBUG_FORMATION_TYPE.get();
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
    public StateHandledFormationInstance<TickCounterRuntimeData, SimpleFormationStateHandler> createFormationInstance(NodeManager nodeManager, BlockPos pos, FormationNodeType type) {

        int controlIndex = -1;
        if(controlNode.isPresent()) controlIndex = recipe.getIndexOf(controlNode.get());
        SimpleFormationStateHandler stateHandler = SimpleFormationStateHandler.of(recipe.getGlobalNodes(nodeManager,pos,type),controlIndex);

        return new StateHandledFormationInstance<>(this,createRuntimeData(),stateHandler);
    }

    @Override
    public StateHandledFormationInstance<TickCounterRuntimeData, SimpleFormationStateHandler> loadFormationInstance(ValueInput input) {
        Optional<SimpleFormationStateHandler> optionalStateHandler = input.read("state",SimpleFormationStateHandler.CODEC);
        return optionalStateHandler.map(simpleFormationStateHandler -> new StateHandledFormationInstance<>(this, loadRuntimeData(input.childOrEmpty("data")), simpleFormationStateHandler)).orElse(null);

    }

    @Override
    public void writeFormationInstance(ValueOutput output, StateHandledFormationInstance<TickCounterRuntimeData, SimpleFormationStateHandler> instance, RegistryAccess access) {
       output.store("state",SimpleFormationStateHandler.CODEC,instance.handler);
       writeRuntimeData(output.child("data"),instance.runtimeData,access);
    }

    @Override
    public TickCounterRuntimeData createRuntimeData() {
        return new TickCounterRuntimeData();
    }

    @Override
    public TickCounterRuntimeData loadRuntimeData(ValueInput input) {
        TickCounterRuntimeData data = createRuntimeData();
        data.read(input);
        return data;
    }

    @Override
    public void writeRuntimeData(ValueOutput output, TickCounterRuntimeData runtimeData, RegistryAccess access) {
        runtimeData.write(output);
    }
}
