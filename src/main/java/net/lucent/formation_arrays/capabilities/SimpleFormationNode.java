package net.lucent.formation_arrays.capabilities;

import net.lucent.formation_arrays.api.v1.nodes.FormationNode;
import net.minecraft.resources.Identifier;

import java.util.Collection;
import java.util.List;

public class SimpleFormationNode implements FormationNode {
    @Override
    public boolean isOfType(Identifier type) {
        return false;
    }

    @Override
    public Collection<Identifier> getNodeTypes() {
        return List.of();
    }
}
