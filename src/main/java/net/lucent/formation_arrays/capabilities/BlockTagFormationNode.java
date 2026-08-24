package net.lucent.formation_arrays.capabilities;

import net.lucent.formation_arrays.api.v1.nodes.FormationNode;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class BlockTagFormationNode implements FormationNode {
    private final Set<Identifier> types;
    public BlockTagFormationNode(Level level, BlockPos pos, BlockState state, BlockEntity entity){


        HashSet<Identifier> tags = new HashSet<>();
        state.tags().forEach(
                tag-> {
                    if(tag.location().getPath().contains("formation_node/")) tags.add(
                            Identifier.fromNamespaceAndPath(
                                    tag.location().getNamespace(),
                                    tag.location().getPath().replace("formation_node/",""))
                    );
                }
        );
        this.types =tags;

    }

    @Override
    public boolean isOfType(Identifier nodeType) {
        return types.contains(nodeType);
    }

    @Override
    public Collection<Identifier> getNodeTypes() {
        return types;
    }
}
