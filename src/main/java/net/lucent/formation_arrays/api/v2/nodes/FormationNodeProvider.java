package net.lucent.formation_arrays.api.v2.nodes;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.Set;

/**
 * An interface that defines how something like a NodeManager would provide information
 * About Formation Nodes,
 * Does not directly provide them, but lets you get information about what node types a block position contains
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

}
