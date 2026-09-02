package net.lucent.formation_arrays.core.formations;

import net.lucent.formation_arrays.core.formations.manager.ClientDimensionFormationManger;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ClientFormationManagerHolder {

    private static final Map<Identifier, ClientDimensionFormationManger> managers = new HashMap<>();


    public static  Collection<ClientDimensionFormationManger> getAllManagers(){
        return managers.values();
    }

    public static ClientDimensionFormationManger get(Level level){
        return managers.get(level.dimension().identifier());
    }
    public static ClientDimensionFormationManger getOrCreate(Level level){
        return managers.computeIfAbsent(level.dimension().identifier(),key->new ClientDimensionFormationManger(level));
    }
    public static void remove(Level level){
        managers.remove(level.dimension().identifier());
    }

    public static void clear(){
        managers.clear();
    }
}
