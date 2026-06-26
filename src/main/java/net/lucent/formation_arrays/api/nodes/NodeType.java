package net.lucent.formation_arrays.api.nodes;

import net.minecraft.network.chat.Component;

/**
 * Used to differentiate different nodes
 * meant to be quite empty, as most logic should be handled by the type carrier rather than the type itself
 */
public interface NodeType {

    Component getName();

}
