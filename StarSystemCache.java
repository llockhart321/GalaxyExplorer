import java.util.*;

//  Cache for Star Systems
class StarSystemCache {
    private static Map<Integer, StarSystem> systemCache = new HashMap<>();
 
    public static void add(StarSystem system) {


        //system.setxLoc();

        systemCache.put(system.getID(), system);

    }

    
    public static StarSystem get(int id) {
    
        if(systemCache.get(id) == null){
            return null;
        } 
        else{
            return systemCache.get(id);
        }
    }


   // print for debug
    public static void printCache() {
        for (Map.Entry<Integer, StarSystem> entry : systemCache.entrySet()) {
            System.out.println(entry.getValue());
        }
    }
}