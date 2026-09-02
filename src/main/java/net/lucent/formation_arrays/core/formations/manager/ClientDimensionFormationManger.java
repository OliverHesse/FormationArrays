package net.lucent.formation_arrays.core.formations.manager;

import net.lucent.formation_arrays.api.formations.FormationInstance;
import net.lucent.formation_arrays.core.formations.PlacedFormation;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class ClientDimensionFormationManger {

    private final Level level;


    private final Map<Long, PlacedFormation> clientFormations = new HashMap<>();

    public ClientDimensionFormationManger(Level level){
        this.level = level;
    }

    boolean hasFormation(long id) {return clientFormations.containsKey(id);}

    public FormationInstance getInstance(long id){return clientFormations.get(id).instance();}

    public void addFormation(PlacedFormation formation){
        clientFormations.put(formation.id(),formation);
    }
    public void removeFormation(long id){
        if(!hasFormation(id)) return;

        clientFormations.remove(id).instance().destroyed(level,null);
    }


    public void tick(){
        for(PlacedFormation placedFormation: clientFormations.values())placedFormation.instance().tick(level,null);
    }

}
