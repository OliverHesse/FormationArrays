package net.lucent.formation_arrays.api.v2.formations;

public interface FormationInstance {
    Formation getType();
    //the definition used to create this instance
    FormationDefinition getDefinition();
}
