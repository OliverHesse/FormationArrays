package net.lucent.formation_arrays.core.formations.client;

import net.lucent.formation_arrays.api.formations.FormationInstance;
import net.lucent.formation_arrays.core.formations.PlacedFormation;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class ClientDimensionFormationManager {
    private final Level level;


    private final Map<Long, PlacedFormation> clientFormations = new HashMap<>();

    public ClientDimensionFormationManager(Level level){
        this.level = level;
    }

    public boolean hasFormation(long id) {return clientFormations.containsKey(id);}

    public FormationInstance<?,?> getInstance(long id){return clientFormations.get(id).instance();}

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
