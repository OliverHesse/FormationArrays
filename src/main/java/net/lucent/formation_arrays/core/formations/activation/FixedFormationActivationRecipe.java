package net.lucent.formation_arrays.core.formations.activation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.lucent.formation_arrays.api.nodes.FormationNodeType;
import net.lucent.formation_arrays.api.nodes.Node;
import net.lucent.formation_arrays.api.nodes.NodeManager;
import net.lucent.formation_arrays.core.formations.util.BlockPosUtil;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A recipe used to determine if a formation can be created
 * @param nodes the nodes and types relative to node centre to create the formation
 * @param canRotate true -> any rotation around y is allowed, false -> no rotations checked
 *
 * TODO get rotation working (for now i just doing fixed)
 */
public record FixedFormationActivationRecipe(List<Node> nodes, boolean canRotate) implements FormationActivationRecipe{

    public static final Codec<FixedFormationActivationRecipe> CODEC = RecordCodecBuilder.create(
            instance->instance.group(
                    Node.CODEC.listOf().fieldOf("nodes").forGetter(FixedFormationActivationRecipe::nodes),
                    Codec.BOOL.fieldOf("can_rotate").forGetter(FixedFormationActivationRecipe::canRotate)
            ).apply(instance, FixedFormationActivationRecipe::new)
    );
    @Override
    public boolean tryActive(NodeManager nodeManager, BlockPos pos, FormationNodeType type) {

        Node pivotNode = getNode(type);
        if (pivotNode == null) return false;

        int rotations = canRotate ? 4 : 1;

        for (int rotation = 0; rotation < rotations; rotation++) {
            if (matchesRotation(nodeManager, pos, pivotNode, rotation)) {
                return true;
            }
        }
        return false;
    }
    private boolean matchesRotation(NodeManager nodeManager, BlockPos pos, Node pivotNode, int rotation) {
        for (Node potentialNode : nodes) {

            BlockPos relativePos = potentialNode.pos().subtract(pivotNode.pos());
            BlockPos rotatedPos = BlockPosUtil.rotateAboutY(relativePos, rotation);

            BlockPos potentialPos = pos.offset(rotatedPos);
            for (FormationNodeType potentialType : potentialNode.types()) {
                if (!nodeManager.hasNodeType(potentialPos, potentialType)) {
                    return false;
                }
            }
        }
        return true;
    }
    @Override
    public Set<BlockPos> getGlobalNodePositions(NodeManager nodeManager, BlockPos pos, FormationNodeType type) {
        Node pivot = getNode(type);
        if (pivot == null) return Set.of();
        int rotations = canRotate ? 4 : 1;

        for (int rotation = 0; rotation < rotations; rotation++) {
            if (matchesRotation(nodeManager, pos, pivot, rotation)) {
                return getGlobalNodePositions(pos, pivot, rotation);
            }
        }
        return Set.of();
    }
    private Set<BlockPos> getGlobalNodePositions(BlockPos pos, Node pivotNode, int rotation) {
        Set<BlockPos> positions = new HashSet<>();

        for (Node node : nodes) {
            BlockPos relativePos = node.pos().subtract(pivotNode.pos());
            BlockPos rotatedPos = BlockPosUtil.rotateAboutY(relativePos, rotation);

            positions.add(pos.offset(rotatedPos));
        }

        return positions;
    }


    @Override
    public List<Node> getGlobalNodes(NodeManager nodeManager, BlockPos pos, FormationNodeType type) {
        Node pivotNode = getNode(type);
        if (pivotNode == null) return List.of();

        int rotations = canRotate ? 4 : 1;

        for (int rotation = 0; rotation < rotations; rotation++) {
            if (matchesRotation(nodeManager, pos, pivotNode, rotation)) {
                return getGlobalNodes(pos, pivotNode, rotation);
            }
        }

        return List.of();
    }
    private List<Node> getGlobalNodes(BlockPos pos, Node pivotNode, int rotation) {
        List<Node> newNodes = new ArrayList<>();
        for (Node node : nodes) {
            BlockPos relativePos = node.pos().subtract(pivotNode.pos());
            BlockPos rotatedPos = BlockPosUtil.rotateAboutY(relativePos, rotation);
            newNodes.add(new Node(pos.offset(rotatedPos), node.types()));
        }
        return newNodes;
    }
    @Override
    public Set<FormationNodeType> getUniqueTypes() {
        Set<FormationNodeType> types = new HashSet<>();
        for(Node node: nodes) types.addAll(node.types());
        return types;
    }

    @Override
    public Node getNode(List<Node> globalPositionedNodes, BlockPos pos) {
        if (globalPositionedNodes.isEmpty()) {
            return null;
        }
        BlockPos center = getCenterFromNodes(globalPositionedNodes);

        BlockPos globalPos = center.offset(pos);

        for (Node node : globalPositionedNodes) {
            if (node.pos().equals(globalPos)) {
                return node;
            }
        }
        return null;
    }

    @Override
    public BlockPos getCenterFromPositions(List<BlockPos> globalNodePositions) {
        return globalNodePositions.isEmpty() ? BlockPos.ZERO : globalNodePositions.getFirst().subtract(nodes.getFirst().pos());
    }

    @Override
    public BlockPos getCenterFromNodes(List<Node> globalPositionedNodes) {
        return globalPositionedNodes.isEmpty() ? BlockPos.ZERO : globalPositionedNodes.getFirst().pos().subtract(nodes.getFirst().pos());
    }

    @Override
    public double getAmplification(List<Node> nodes) {
        return 1;
    }

    public Node getNode(FormationNodeType type){
        for(Node node : nodes){
            if(node.types().contains(type)) return node;
        }
        return null;
    }
    public Node getNode(BlockPos pos){
        for(Node node : nodes){
            if(node.pos().equals(pos)) return node;
        }
        return null;
    }
    public int getIndexOf(BlockPos pos){
        Node node  = getNode(pos);
        return node == null ? -1 : nodes.indexOf(node);
    }
}
