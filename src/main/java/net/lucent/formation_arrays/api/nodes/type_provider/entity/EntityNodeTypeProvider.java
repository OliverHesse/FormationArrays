package net.lucent.formation_arrays.api.nodes.type_provider.entity;

import net.lucent.formation_arrays.api.nodes.type_provider.NodeTypeProvider;
import net.minecraft.world.entity.Entity;

public interface EntityNodeTypeProvider extends NodeTypeProvider {
    Entity getEntity();

}
