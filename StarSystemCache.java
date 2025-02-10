import java.util.*;
import javafx.scene.canvas.GraphicsContext;

public class StarSystemCache {
    // Singleton pattern
    private static StarSystemCache instance;

    // Main cache to store star systems
    private Map<Integer, StarSystem> systemCache = new HashMap<>();

    // Tracks the next available system ID
    private int nextSystemId = 0;

    // Private constructor for singleton
    private StarSystemCache() {}

    // Singleton access method
    public static synchronized StarSystemCache getInstance() {
        if (instance == null) {
            instance = new StarSystemCache();
        }
        return instance;
    }

    // Generate a new unique system ID
    public synchronized int generateNextSystemId() {
        return nextSystemId++;
    }

    // Reset the ID counter (useful for starting a new game)
    public synchronized void resetIdCounter() {
        nextSystemId = 0;
        systemCache.clear();
    }

    // Add a star system to the cache
    public void add(StarSystem system) {
        // If no ID is set, generate one
        if (system.getID() == -1) {
            system.setID(generateNextSystemId());
        }

        // Ensure the ID is tracked
        if (system.getID() >= nextSystemId) {
            nextSystemId = system.getID() + 1;
        }

        // Add to cache
        systemCache.put(system.getID(), system);
    }

    // Retrieve a star system by ID
    public StarSystem get(int id) {
        return systemCache.get(id);
    }

    // Check if a system exists in the cache
    public boolean contains(int id) {
        return systemCache.containsKey(id);
    }

    // Create a new star system and add it to the cache
    public int createSystem(GraphicsContext gc) {
        StarSystem newSystem = new StarSystem(gc, -1);
        add(newSystem);
        return newSystem.getID();
    }

    // Create a new star system with a specific location
    public StarSystem createSystem(GraphicsContext gc, double x, double y) {
        StarSystem newSystem = new StarSystem(gc, -1);
        newSystem.setxLoc(x);
        newSystem.setyLoc(y);
        add(newSystem);
        return newSystem;
    }

    // Debug method to print all cached systems
    public void printCache() {
        System.out.println("Cached Star Systems:");
        for (Map.Entry<Integer, StarSystem> entry : systemCache.entrySet()) {
            StarSystem system = entry.getValue();
            System.out.println("ID: " + entry.getKey() +
                    ", Location: (" + system.getxLoc() + ", " + system.getyLoc() + ")");
        }
    }

    // Get all cached systems
    public Collection<StarSystem> getAllSystems() {
        return systemCache.values();
    }

    // Remove a system from the cache
    public void remove(int id) {
        systemCache.remove(id);
    }
}