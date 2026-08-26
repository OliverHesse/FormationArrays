package net.lucent.formation_arrays.api.v2;

import net.lucent.formation_arrays.api.v2.nodes.FormationNode;
import net.lucent.formation_arrays.api.v2.nodes.FormationNodeProvider;
import net.lucent.formation_arrays.api.v2.nodes.FormationNodeType;
import net.lucent.formation_arrays.api.v2.nodes.accessor.FormationNodeReference;
import net.lucent.formation_arrays.capabilities.CoreCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

import java.util.*;

/*
    TODO for now will be in api, but in the future will be moved out of api
 */
public class NodeManager{

    //TODO split these into loaded and unloaded, then the combined methods can combine the results

    private record DimensionBlockPos(Identifier dimension, BlockPos pos){
        public static DimensionBlockPos of(Level level,BlockPos pos){
            return new DimensionBlockPos(level.dimension().identifier(),pos);
        }
        private static DimensionBlockPos of(FormationNodeReference reference){
            return new DimensionBlockPos(reference.getDimension(),reference.getPos());
        }
    }
    private final Map<DimensionBlockPos, Set<FormationNodeReference>> nodes = new HashMap<>();

    private final Set<FormationNodeReference> unloadedNodes = new HashSet<>();





    private FormationNodeReference createUnloadedRef(FormationNodeReference reference,Level level){
        return new FormationNodeReference.Unloaded(reference.getDimension(),reference.getPos(),reference.getNodeType(level));
    }

    public void unloadNode(FormationNodeReference reference,Level level){
        DimensionBlockPos pos = DimensionBlockPos.of(reference);
        if(!nodes.containsKey(pos)) return;

        nodes.get(pos).remove(reference);

        FormationNodeReference unloadedReference = createUnloadedRef(reference,level);
        nodes.get(pos).add(unloadedReference);
        unloadedNodes.add(unloadedReference);
    }
}
