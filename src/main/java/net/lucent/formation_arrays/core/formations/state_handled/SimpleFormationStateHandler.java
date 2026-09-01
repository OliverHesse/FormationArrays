package net.lucent.formation_arrays.core.formations.state_handled;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.lucent.formation_arrays.api.nodes.FormationNodeType;
import net.lucent.formation_arrays.api.nodes.Node;
import net.lucent.formation_arrays.api.nodes.NodeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public record SimpleFormationStateHandler(List<Node> nodes,Node controlNode) implements FormationStateHandler {
    public static Codec<SimpleFormationStateHandler> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Node.CODEC.listOf().fieldOf("nodes").forGetter(SimpleFormationStateHandler::nodes),
                    Node.CODEC.optionalFieldOf("control_node").forGetter(val -> Optional.ofNullable(val.controlNode))
                    ).apply(instance,(nodes,control)->new SimpleFormationStateHandler(nodes,control.orElse(null)))
            );
    public static SimpleFormationStateHandler of(List<Node> nodes, int controlNode){
        return controlNode == -1 ? new SimpleFormationStateHandler(nodes,null) : new SimpleFormationStateHandler(nodes, nodes.get(controlNode));
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
            if(node == controlNode) continue;
            if(!isNodeValid(nodeManager,node)) return false;
        }
        return true;
    }

    @Override
    public Set<BlockPos> getListenedNodePositions() {
        return nodes.stream().map(Node::pos).collect(Collectors.toSet());
    }
}
