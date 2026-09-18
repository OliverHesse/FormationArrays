package net.lucent.formation_arrays.test.v2;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.lucent.formation_arrays.FormationArrays;
import net.lucent.formation_arrays.api.formations.Formation;
import net.lucent.formation_arrays.api.formations.FormationType;
import net.lucent.formation_arrays.api.formations.StateHandler;
import net.lucent.formation_arrays.api.formations.util.SerializerHandler;
import net.lucent.formation_arrays.api.formations.util.SyncHandler;
import net.lucent.formation_arrays.api.nodes.FormationNodeType;
import net.lucent.formation_arrays.api.nodes.Node;
import net.lucent.formation_arrays.api.nodes.NodeManager;
import net.lucent.formation_arrays.core.formations.SimpleStateHandler;
import net.lucent.formation_arrays.core.formations.activation.FixedFormationActivationRecipe;
import net.lucent.formation_arrays.test.TestRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public record DebugFormation(FixedFormationActivationRecipe recipe, Optional<BlockPos> controlNode) implements Formation<DebugRuntimeData, SimpleStateHandler> {

    public static class Type extends FormationType{

        @Override
        public MapCodec<? extends Formation<?, ?>> definitionCodec() {
            return RecordCodecBuilder.<DebugFormation>mapCodec(instance->instance.group(
                            FixedFormationActivationRecipe.CODEC.fieldOf("activation_nodes").forGetter(DebugFormation::recipe),
                            BlockPos.CODEC.optionalFieldOf("control_node").forGetter(DebugFormation::controlNode)
                    ).apply(instance,DebugFormation::new)
            );
        }
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
        return recipe.tryActive(nodeManager,pos,type);
    }

    @Override
    public Set<BlockPos> getRequiredActivationNodes(NodeManager nodeManager, BlockPos pos, FormationNodeType type) {
        return recipe.getGlobalNodePositions(nodeManager,pos,type);
    }

    @Override
    public DebugRuntimeData createRuntimeData(StateHandler handler) {
        return new DebugRuntimeData();
    }

    @Override
    public SerializerHandler<DebugRuntimeData> runtimeDataSerializer() {
        return DebugRuntimeData.SERIALIZER;
    }

    @Override
    public SyncHandler<DebugRuntimeData> runtimeDataSyncHandler() {
        return DebugRuntimeData.SYNC_HANDLER;
    }

    @Override
    public SimpleStateHandler createStateHandler(NodeManager nodeManager, BlockPos pos, FormationNodeType type) {
        List< Node> nodes = recipe.getGlobalNodes(nodeManager,pos,type);

        return SimpleStateHandler.of(recipe,nodes,controlNode.orElse(null));
    }

    @Override
    public SerializerHandler<SimpleStateHandler> stateHandlerSerializer() {
        return SimpleStateHandler.SERIALIZER;
    }

    @Override
    public SyncHandler<SimpleStateHandler> stateHandlerSyncHandler() {
        return SimpleStateHandler.SYNC_HANDLER;
    }

    @Override
    public void activate(DebugRuntimeData runtimeData, SimpleStateHandler stateHandler, Level level) {
        FormationArrays.LOGGER.debug("DEBUG FORMATION ACTIVATED ON ({})",(level.isClientSide() ? "Client" :"Server"));
    }

    @Override
    public void deactivate(DebugRuntimeData runtimeData, SimpleStateHandler stateHandler, Level level) {
        FormationArrays.LOGGER.debug("DEBUG FORMATION DEACTIVATED ON ({})",(level.isClientSide() ? "Client" :"Server"));
    }

    @Override
    public void destroy(DebugRuntimeData runtimeData, SimpleStateHandler stateHandler, Level level) {
        FormationArrays.LOGGER.debug("DEBUG FORMATION DESTROYED ON ({})",(level.isClientSide() ? "Client" :"Server"));
    }

    @Override
    public boolean tick(DebugRuntimeData runtimeData, SimpleStateHandler stateHandler, Level level) {
        FormationArrays.LOGGER.debug("DEBUG FORMATION TICKED ON ({}) ({})",(level.isClientSide() ? "Client" :"Server"), runtimeData.ticks);
        runtimeData.ticks ++;

        return false;
    }
}
