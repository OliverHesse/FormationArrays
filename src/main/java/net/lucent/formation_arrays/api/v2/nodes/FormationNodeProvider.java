package net.lucent.formation_arrays.api.v2.nodes;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.Set;

/**
 * An interface that defines how something like a NodeManager would provide FormationNodes
 * Does not control adding or removing nodes
 */
public interface FormationNodeProvider {

    /**
     * @param level the level we want to check
     * @param pos the position we want to check
     * @param type the formation node type we want to check
     * @return true -> contains that node type. false -> does not contain that type
     */
    boolean hasNodeType(Level level, BlockPos pos, FormationNodeType type);

    /**
     * @param level the level we want to check
     * @param pos the position we want to check
     * @return all Formation Node Types in a position
     */
    Collection<FormationNodeType> getTypes(Level level, BlockPos pos);

    /**
     *
     * @param level the level we want to check
     * @param pos the position we want to check
     * @param type the formation node type we want
     * @return a formation node of type in that block pos
     */
    FormationNode getNode(Level level,BlockPos pos,FormationNodeType type);

    /**
     * @param level the level we want to check
     * @param pos the position we want to check
     * @return all nodes in that block pos
     */
    Collection<FormationNode> getNodes(Level level,BlockPos pos);
}
