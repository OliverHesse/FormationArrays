package net.lucent.formation_arrays.api.v2.formations;

import net.lucent.formation_arrays.api.v1.nodes.NodeGraph;

public interface FormationDefinition {

    Formation getType();


    NodeGraph getGraph();
}
