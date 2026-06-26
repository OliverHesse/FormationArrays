package net.lucent.formation_arrays.api.formations;

import net.lucent.formation_arrays.api.nodes.NodeGraph;

public interface Formation {


    FormationType getType();
    NodeGraph getGraph();
}
