package net.lucent.formation_arrays.api.v2.formations;

import net.lucent.formation_arrays.api.v2.nodes.FormationNodeType;
import net.lucent.formation_arrays.api.v2.nodes.NodeManager;
import net.minecraft.core.BlockPos;

import java.util.Set;

public interface FormationRecipe {


    /**
     * when a recipe type is detected, will try to test if we can create a formation using that type
     * @param nodeManager the mangar handling nodes
     * @param pos the position of the node
     * @param type the type we detected being added
     * @return true -> create a formation, false -> do not create a formation
     */
    boolean test(NodeManager nodeManager, BlockPos pos,FormationNodeType type);


    //the node types used in this recipe
    Set<FormationNodeType> getTypes();
}
