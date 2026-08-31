package net.lucent.formation_arrays.core.formations.activation;

import net.lucent.formation_arrays.api.CoreRegistries;
import net.lucent.formation_arrays.api.formations.Formation;
import net.lucent.formation_arrays.api.nodes.FormationNodeType;
import net.minecraft.core.RegistryAccess;

import java.util.*;

/**
 * maps node types to all formations that have that type as an activation node type
 */
public class FormationActivationHelper {

    private static final Map<FormationNodeType, Set<Formation<?,?>>> typeToFormations = new HashMap<>();

    public static void init(RegistryAccess access){
        CoreRegistries.FORMATIONS.get(access).stream().forEach(FormationActivationHelper::addFormation);
    }
    public static void clear(){
        typeToFormations.clear();
    }


    private static void addFormation(Formation<?,?> formation){
        for(FormationNodeType type : formation.activationNodes()) typeToFormations.computeIfAbsent(type,key->new HashSet<>()).add(formation);
    }


    public static Collection<Formation<?,?>> getFormations(FormationNodeType type){
        return Set.copyOf(typeToFormations.getOrDefault(type,new HashSet<>()));
    }
}
