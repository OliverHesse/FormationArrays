package net.lucent.formation_arrays.core.formations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import net.lucent.formation_arrays.api.formations.StateHandler;
import net.lucent.formation_arrays.api.formations.util.SerializerHandler;
import net.lucent.formation_arrays.api.formations.util.SyncHandler;
import net.lucent.formation_arrays.api.nodes.FormationNodeType;
import net.lucent.formation_arrays.api.nodes.Node;
import net.lucent.formation_arrays.api.nodes.NodeManager;
import net.lucent.formation_arrays.core.formations.activation.FormationActivationRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public record SimpleStateHandler(List<Node> nodes, Node controlNode) implements StateHandler {
    public static Codec<SimpleStateHandler> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Node.CODEC.listOf().fieldOf("nodes").forGetter(SimpleStateHandler::nodes),
                    Codec.INT.optionalFieldOf("control_node").forGetter(SimpleStateHandler::getControlNode)
            ).apply(instance,(nodes,control)->SimpleStateHandler.of(nodes,control.orElse(-1)))
    );

    public static StreamCodec<ByteBuf, SimpleStateHandler> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    public static SerializerHandler<SimpleStateHandler> SERIALIZER = new SerializerHandler.CodecSerializerHandler<>(CODEC);
    public static SyncHandler<SimpleStateHandler> SYNC_HANDLER = new SyncHandler.StreamCodecSyncHandler<>(STREAM_CODEC);


    public static SimpleStateHandler of(List<Node> nodes, int controlNode){
        return controlNode == -1 ? new SimpleStateHandler(nodes,null) : new SimpleStateHandler(nodes, nodes.get(controlNode));
    }
    public static SimpleStateHandler of(FormationActivationRecipe recipe,List<Node> globalNodes, BlockPos controlNode){
        return controlNode == null? new SimpleStateHandler(globalNodes,null) :  new SimpleStateHandler(globalNodes,recipe.getNode(globalNodes,controlNode));

    }
    private Optional<Integer> getControlNode(){
        if(controlNode == null) return Optional.empty();
        int index = nodes.indexOf(controlNode);
        return index == -1 ? Optional.empty() : Optional.of(index);
    }
    public boolean isNodeValid(NodeManager nodeManager,Node node){
        for(FormationNodeType type : node.types()){
            if(!nodeManager.hasNodeType(node.pos(),type)) return false;

        }
        return true;
    }
    @Override
    public boolean isActive(Level level, NodeManager nodeManager) {
        if(controlNode != null && !isNodeValid(nodeManager,controlNode)) return false;

        for(Node node : nodes){
            if(nodeManager.isLoaded(node.pos())) return true;
        }
        return false;
    }

    @Override
    public boolean isValid(Level level, NodeManager nodeManager) {
        for(Node node : nodes){
            if(controlNode != null && node.pos().equals(controlNode.pos())) continue;
            if(!isNodeValid(nodeManager,node)) return false;
        }
        return true;
    }

    @Override
    public Set<BlockPos> getListenedNodePositions() {
        return nodes.stream().map(Node::pos).collect(Collectors.toSet());
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



}