package net.lucent.formation_arrays.capabilities;


import net.lucent.formation_arrays.api.v2.nodes.FormationNode;
import net.lucent.formation_arrays.api.v2.nodes.FormationNodeType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class BlockTagFormationNode implements FormationNode {
    private final Set<FormationNodeType> types;
    public BlockTagFormationNode(Level level, BlockPos pos, BlockState state, BlockEntity entity){


        HashSet<FormationNodeType> tags = new HashSet<>();
        state.tags().forEach(
                tag-> {
                    if(tag.location().getPath().contains("formation_node/")) tags.add(
                            new FormationNodeType(  Identifier.fromNamespaceAndPath(
                                    tag.location().getNamespace(),
                                    tag.location().getPath().replace("formation_node/","")))
                    );
                }
        );
        this.types =tags;

    }



    @Override
    public Collection<FormationNodeType> getTypes() {
        return types;
    }

    @Override
    public boolean isType(FormationNodeType type) {
        return types.contains(type);
    }
}
