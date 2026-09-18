package net.lucent.formation_arrays.core.formations.client;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ClientDimensionFormationManagerHolder {
    private static final Map<Identifier, ClientDimensionFormationManager> managers = new HashMap<>();


    public static Collection<ClientDimensionFormationManager> getAllManagers(){
        return managers.values();
    }

    public static ClientDimensionFormationManager get(Level level){
        return managers.get(level.dimension().identifier());
    }
    public static ClientDimensionFormationManager getOrCreate(Level level){
        return managers.computeIfAbsent(level.dimension().identifier(),key->new ClientDimensionFormationManager(level));
    }
    public static void remove(Level level){
        managers.remove(level.dimension().identifier());
    }

    public static void clear(){
        managers.clear();
    }
}
