import javafx.scene.paint.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.Random;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Affine;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.canvas.Canvas;
import java.util.*;



import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.canvas.Canvas;
import java.util.*;

public class GalaxyMap {
    private static GraphicsContext gc;
    private static AnimationHandler ah;
    private static boolean isOpen = false;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 450;

    // View properties
    private double viewX = 0;
    private double viewY = 0;
    private double scale = 1.0;
    private double minScale = 0.5;
    private double maxScale = 6.0;

    // Mouse tracking for pan
    private double lastMouseX;
    private double lastMouseY;
    private boolean isPanning = false;

    // Chunk management
    private static final int CHUNK_SIZE = 200;
    private Map<ChunkCoord, Chunk> loadedChunks = new HashMap<>();
    private static Set<ChunkCoord> discoveredChunks = new HashSet<>();
    //private static final Random random = new Random(42); // Fixed seed for consistency

    // Starting chunk coordinates
    private static final ChunkCoord STARTING_CHUNK = new ChunkCoord(0, 0);

    // Singleton instance
    private static GalaxyMap instance;
    private StarSystemData currentSystem; // Track current system for zoom centering

    private static final Random random = new Random(System.currentTimeMillis());

    private static class ChunkCoord {
        final int x, y;

        ChunkCoord(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ChunkCoord)) return false;
            ChunkCoord that = (ChunkCoord) o;
            return x == that.x && y == that.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    private static class Chunk {
        List<StarSystemData> systems = new ArrayList<>();
        ChunkCoord chunkCoord;

        Chunk(ChunkCoord coord) {
            this.chunkCoord = coord;

            // Use a new random seed based on chunk coordinates and current time
            Random chunkRandom = new Random(
                    System.currentTimeMillis() +
                            coord.x * 10000L +
                            coord.y * 1000L
            );

            // Random number of systems per chunk, between 2 and 5
            int systemCount = chunkRandom.nextInt(4) + 2;

            for (int i = 0; i < systemCount; i++) {
                // Add some randomization to position but keep it away from edges
                int padding = CHUNK_SIZE / 4;
                double x = coord.x * CHUNK_SIZE + padding + chunkRandom.nextDouble() * (CHUNK_SIZE - 2 * padding);
                double y = coord.y * CHUNK_SIZE + padding + chunkRandom.nextDouble() * (CHUNK_SIZE - 2 * padding);
                systems.add(new StarSystemData(x, y, generateSystemId(coord, i)));
            }
        }
    }

    // Method to prepare inter-chunk connections
    private void prepareChunkConnections() {
        // Directions to check: top-left, top, top-right, left, right, bottom-left, bottom, bottom-right
        int[][] directions = {
                {-1, -1}, {0, -1}, {1, -1},
                {-1, 0},           {1, 0},
                {-1, 1}, {0, 1}, {1, 1}
        };

        // For each discovered chunk
        for (ChunkCoord currentChunkCoord : discoveredChunks) {
            Chunk currentChunk = loadedChunks.get(currentChunkCoord);

            // Check neighboring chunks
            for (int[] dir : directions) {
                ChunkCoord neighborChunkCoord = new ChunkCoord(
                        currentChunkCoord.x + dir[0],
                        currentChunkCoord.y + dir[1]
                );

                // Skip if neighbor chunk is not discovered
                if (!discoveredChunks.contains(neighborChunkCoord)) continue;

                Chunk neighborChunk = loadedChunks.get(neighborChunkCoord);

                // Determine edge for connection
                String edge = determineEdge(dir);

                // Find nearest systems to connect
                StarSystemData currentSystem = findSystemNearestToEdge(currentChunk, getOppositeEdge(edge));
                StarSystemData neighborSystem = findSystemNearestToEdge(neighborChunk, edge);

                // Create connection (you'll need to implement the actual gate creation)
                // This is a placeholder for your actual gate creation logic
                createChunkConnection(currentSystem, neighborSystem);
            }
        }
    }

    private static class StarSystemData {
        final double x, y;
        final int id;
        boolean visited = false;

        StarSystemData(double x, double y, int id) {
            this.x = x;
            this.y = y;
            this.id = id;
        }
    }

    // Private constructor for singleton
    private GalaxyMap() {
        // Seed the initial chunk discovery with the current time
        long seed = System.currentTimeMillis();
        Random initialRandom = new Random(seed);

        discoveredChunks.add(STARTING_CHUNK);
        loadedChunks.put(STARTING_CHUNK, new Chunk(STARTING_CHUNK));

        // Find and set initial current system
        Chunk startChunk = loadedChunks.get(STARTING_CHUNK);
        if (!startChunk.systems.isEmpty()) {
            // Randomly select a system from the starting chunk
            currentSystem = startChunk.systems.get(
                    initialRandom.nextInt(startChunk.systems.size())
            );
        }
    }

    public static GalaxyMap getInstance() {
        if (instance == null) {
            instance = new GalaxyMap();
        }
        return instance;
    }

    private static int generateSystemId(ChunkCoord coord, int systemIndex) {
        // Use a combination of chunk coordinates and system index
        // This ensures unique IDs across different chunk layouts
        return Math.abs((coord.x * 10000 + coord.y) * 100 + systemIndex);
    }

    public void initializeChunkConnections() {
        // Call this method whenever a new system is set or discovered
        prepareChunkConnections();
        generateChunkConnections();
    }

    // Get chunk coordinates from system coordinates
    private ChunkCoord getChunkCoordFromSystem(double x, double y) {
        int chunkX = (int) Math.floor(x / CHUNK_SIZE);
        int chunkY = (int) Math.floor(y / CHUNK_SIZE);
        return new ChunkCoord(chunkX, chunkY);
    }

    // Draw method
    private void draw() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        gc.save();
        gc.translate(WIDTH/2, HEIGHT/2);
        gc.scale(scale, scale);
        gc.translate(viewX, viewY);

        // Draw chunk boundaries (for debugging)
        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(1.0 / scale);
        for (ChunkCoord coord : discoveredChunks) {
            double x = coord.x * CHUNK_SIZE;
            double y = coord.y * CHUNK_SIZE;

            gc.strokeRect(x, y, CHUNK_SIZE, CHUNK_SIZE);
        }

        // Draw connections and systems
        for (ChunkCoord coord : discoveredChunks) {
            Chunk chunk = loadedChunks.get(coord);
            if (chunk == null) continue;

            // Draw connections between systems within the same chunk
            for (int i = 0; i < chunk.systems.size(); i++) {
                StarSystemData systemA = chunk.systems.get(i);

                for (int j = i + 1; j < chunk.systems.size(); j++) {
                    StarSystemData systemB = chunk.systems.get(j);

                    // Determine connection color
                    if (systemA.visited && systemB.visited) {
                        gc.setStroke(Color.WHITE);
                    } else {
                        gc.setStroke(Color.GRAY);
                    }

                    // Draw connection line
                    gc.setLineWidth(1.0 / scale);
                    gc.strokeLine(systemA.x, systemA.y, systemB.x, systemB.y);
                }
            }

            // Draw systems
            for (StarSystemData system : chunk.systems) {
                double dotSize = 6;

                // Determine system color and visited status
                if (currentSystem != null && system.id == currentSystem.id) {
                    // Current system
                    gc.setFill(Color.PURPLE);
                    system.visited = true;
                } else if (StarSystemCache.getInstance().get(system.id) != null) {
                    // System has been visited
                    gc.setFill(Color.WHITE);
                    system.visited = true;
                } else {
                    // Undiscovered system
                    gc.setFill(Color.GRAY);
                }

                // Draw system dot
                gc.fillOval(
                        system.x - dotSize/2,
                        system.y - dotSize/2,
                        dotSize,
                        dotSize
                );

                // Draw system ID
                gc.setFill(Color.CYAN);
                gc.setFont(javafx.scene.text.Font.font(8.0 / scale)); // Adjust font size based on zoom
                gc.fillText(
                        String.valueOf(system.id),
                        system.x + dotSize,
                        system.y - dotSize
                );
            }
        }

        gc.restore();
    }

    // Helper method to find the system closest to a chunk edge
    private StarSystemData findSystemNearestToEdge(Chunk chunk, String edge) {
        StarSystemData nearestSystem = null;
        double minDistance = Double.MAX_VALUE;

        for (StarSystemData system : chunk.systems) {
            double distance = 0;
            switch (edge) {
                case "left":
                    distance = system.x;
                    break;
                case "right":
                    distance = Math.abs(system.x - (chunk.chunkCoord.x + 1) * CHUNK_SIZE);
                    break;
                case "top":
                    distance = system.y;
                    break;
                case "bottom":
                    distance = Math.abs(system.y - (chunk.chunkCoord.y + 1) * CHUNK_SIZE);
                    break;
            }

            if (distance < minDistance) {
                minDistance = distance;
                nearestSystem = system;
            }
        }

        return nearestSystem;
    }


    // Method to discover a new chunk when visiting a system
    public void discoverChunkFromSystemCoords(double x, double y) {
        ChunkCoord newChunk = getChunkCoordFromSystem(x, y);
        if (!discoveredChunks.contains(newChunk)) {
            discoveredChunks.add(newChunk);
            loadedChunks.put(newChunk, new Chunk(newChunk));
            //draw();
        }
    }

    // Event handling methods remain the same as in your original code
    public void mapAction() {
        if (!isOpen) {
            isOpen = true;
            ah.stop();
            setupEventHandlers();
            draw();
        } else {
            isOpen = false;
            scale = 1;
            viewX = 0;
            viewY = 0;
            ah.start();
        }
    }

    public void set(GraphicsContext gc, AnimationHandler ah) {
        this.gc = gc;
        this.ah = ah;
    }

    private void zoom(double factor) {
        double newScale = scale * factor;

        // Enforce zoom limits
        if (newScale < minScale) {
            factor = minScale / scale;
            newScale = minScale;
        } else if (newScale > maxScale) {
            factor = maxScale / scale;
            newScale = maxScale;
        }

        if (currentSystem != null) {
            // Convert current system coordinates to screen space
            double screenX = (currentSystem.x + viewX) * scale + WIDTH/2;
            double screenY = (currentSystem.y + viewY) * scale + HEIGHT/2;

            scale = newScale;

            // Adjust view to keep current system centered
            double newScreenX = (currentSystem.x + viewX) * scale + WIDTH/2;
            double newScreenY = (currentSystem.y + viewY) * scale + HEIGHT/2;

            viewX -= (newScreenX - screenX) / scale;
            viewY -= (newScreenY - screenY) / scale;
        } else {
            scale = newScale;
        }

        draw();
    }

    // Modified setupEventHandlers to use new zoom method
    private void setupEventHandlers() {
        Canvas canvas = gc.getCanvas();

        // Mouse press for starting pan
        canvas.setOnMousePressed(e -> {
            if (isOpen) {
                lastMouseX = e.getX();
                lastMouseY = e.getY();
                isPanning = true;
            }
        });

        // Mouse drag for panning
        canvas.setOnMouseDragged(e -> {
            if (isOpen && isPanning) {
                double deltaX = e.getX() - lastMouseX;
                double deltaY = e.getY() - lastMouseY;

                viewX += deltaX / scale;
                viewY += deltaY / scale;

                lastMouseX = e.getX();
                lastMouseY = e.getY();

                draw();
            }
        });

        // Mouse release to end pan
        canvas.setOnMouseReleased(e -> {
            if (isOpen) {
                isPanning = false;
            }
        });

        // Scroll for zoom
        canvas.setOnScroll(e -> {
            if (isOpen) {
                double zoomFactor;
                if (e.isInertia()) {
                    // Handle trackpad gesture
                    zoomFactor = 0.5 - e.getDeltaY() * 0.001;
                } else {
                    // Handle mouse wheel
                    zoomFactor = e.getDeltaY() > 0 ? 1.1 : 0.9;
                }
                zoom(zoomFactor);
            }
        });
    }

    // Method to update current system
    public void setCurrentSystem(int systemId) {
        // Find the system with the given ID
        for (Chunk chunk : loadedChunks.values()) {
            for (StarSystemData system : chunk.systems) {
                if (system.id == systemId) {
                    currentSystem = system;

                    // Mark the system as visited
                    system.visited = true;

                    // Check and load neighboring chunks
                    checkAndLoadNeighboringChunks();

                    // Trigger chunk connection generation
                    initializeChunkConnections();

                    // Redraw the map
                    //draw();
                    return;
                }
            }
        }
    }



    private String determineEdge(int[] direction) {
        if (direction[0] == -1 && direction[1] == 0) return "left";
        if (direction[0] == 1 && direction[1] == 0) return "right";
        if (direction[0] == 0 && direction[1] == -1) return "top";
        if (direction[0] == 0 && direction[1] == 1) return "bottom";
        // Diagonal cases
        if (direction[0] == -1 && direction[1] == -1) return "top-left";
        if (direction[0] == 1 && direction[1] == -1) return "top-right";
        if (direction[0] == -1 && direction[1] == 1) return "bottom-left";
        if (direction[0] == 1 && direction[1] == 1) return "bottom-right";
        return "unknown";
    }

    private String getOppositeEdge(String edge) {
        switch (edge) {
            case "left": return "right";
            case "right": return "left";
            case "top": return "bottom";
            case "bottom": return "top";
            case "top-left": return "bottom-right";
            case "top-right": return "bottom-left";
            case "bottom-left": return "top-right";
            case "bottom-right": return "top-left";
            default: return "unknown";
        }
    }

    private void createChunkConnection(StarSystemData systemA, StarSystemData systemB) {
        // Ensure systems are from different chunks
        ChunkCoord chunkA = getChunkCoordFromSystem(systemA.x, systemA.y);
        ChunkCoord chunkB = getChunkCoordFromSystem(systemB.x, systemB.y);

        // Verify they are actually from different chunks
        if (chunkA.equals(chunkB)) {
            return; // No need to create connection within same chunk
        }

        // Get or create star systems for these system data points
        StarSystem starSystemA = StarSystemCache.getInstance().get(systemA.id);
        StarSystem starSystemB = StarSystemCache.getInstance().get(systemB.id);

        // If star systems don't exist, create them
        if (starSystemA == null) {
            starSystemA = new StarSystem(gc, systemA.id);
            StarSystemCache.getInstance().add(starSystemA);
            starSystemA.setxLoc(systemA.x);
            starSystemA.setyLoc(systemA.y);
        }

        if (starSystemB == null) {
            starSystemB = new StarSystem(gc, systemB.id);
            StarSystemCache.getInstance().add(starSystemB);
            starSystemB.setxLoc(systemB.x);
            starSystemB.setyLoc(systemB.y);
        }

        /*
        // Create gates in both systems pointing to each other
        // Use a fixed position near the edge of the system for the gate
        Gate gateToB = new Gate(
                0,  // direction (can be refined later)
                systemB.id,
                systemA.x % CHUNK_SIZE + 50,  // Offset from system's left edge
                systemA.y % CHUNK_SIZE + 50,  // Offset from system's top edge
                starSystemA,
                270
        );

        Gate gateToA = new Gate(
                0,  // direction (can be refined later)
                systemA.id,
                systemB.x % CHUNK_SIZE + 50,  // Offset from system's left edge
                systemB.y % CHUNK_SIZE + 50,  // Offset from system's top edge
                starSystemB,
                270
        );



        // Add gates to respective star systems
        starSystemA.addGate(gateToB);
        starSystemB.addGate(gateToA);

         */


        System.out.println("Created chunk connection between System " + systemA.id + " and System " + systemB.id);
    }
    public void checkAndLoadNeighboringChunks() {
        if (currentSystem == null) return;

        // Get current chunk coordinates
        ChunkCoord currentChunkCoord = getChunkCoordFromSystem(currentSystem.x, currentSystem.y);

        // Directions to check: top-left, top, top-right, left, right, bottom-left, bottom, bottom-right
        int[][] directions = {
                {-1, -1}, {0, -1}, {1, -1},
                {-1, 0},           {1, 0},
                {-1, 1}, {0, 1}, {1, 1}
        };

        // Check and load neighboring chunks
        for (int[] dir : directions) {
            ChunkCoord neighborChunkCoord = new ChunkCoord(
                    currentChunkCoord.x + dir[0],
                    currentChunkCoord.y + dir[1]
            );

            // Only load if not already discovered
            if (!discoveredChunks.contains(neighborChunkCoord)) {
                // Create and add new chunk
                Chunk newChunk = new Chunk(neighborChunkCoord);
                discoveredChunks.add(neighborChunkCoord);
                loadedChunks.put(neighborChunkCoord, newChunk);
            }
        }

        initializeChunkConnections();

        // trigger a redraw of the map
        //draw();
    }

    private void generateChunkConnections() {
        // Iterate through discovered chunks
        for (ChunkCoord currentChunkCoord : discoveredChunks) {
            Chunk currentChunk = loadedChunks.get(currentChunkCoord);

            // Directions to check: top-left, top, top-right, left, right, bottom-left, bottom, bottom-right
            int[][] directions = {
                    {-1, -1}, {0, -1}, {1, -1},
                    {-1, 0},           {1, 0},
                    {-1, 1}, {0, 1}, {1, 1}
            };

            for (int[] dir : directions) {
                ChunkCoord neighborChunkCoord = new ChunkCoord(
                        currentChunkCoord.x + dir[0],
                        currentChunkCoord.y + dir[1]
                );

                // Check if neighbor chunk is discovered
                if (discoveredChunks.contains(neighborChunkCoord)) {
                    Chunk neighborChunk = loadedChunks.get(neighborChunkCoord);

                    // Find systems near the shared edge
                    StarSystemData currentEdgeSystem = findSystemNearestToEdge(currentChunk, getEdgeForDirection(dir));
                    StarSystemData neighborEdgeSystem = findSystemNearestToEdge(neighborChunk, getOppositeEdge(getEdgeForDirection(dir)));

                    // Create a gate between these systems
                    if (currentEdgeSystem != null && neighborEdgeSystem != null) {
                        createInterChunkGate(currentEdgeSystem, neighborEdgeSystem);
                    }
                }
            }
        }
    }

    private void createInterChunkGate(StarSystemData fromSystem, StarSystemData toSystem) {
        // This method would create a gate in the fromSystem's star system
        // that leads to the toSystem's star system
        StarSystem fromStarSystem = StarSystemCache.getInstance().get(fromSystem.id);

        if (false){//fromStarSystem != null) {
            // Create a gate in the fromStarSystem that leads to toSystem

            Gate interChunkGate = new Gate(
                    0,  // direction (can be refined)
                    toSystem.id,  // target system ID
                    fromSystem.x % CHUNK_SIZE,  // x position within the system
                    fromSystem.y % CHUNK_SIZE,  // y position within the system
                    fromStarSystem,  // parent star system
                    270
            );



            // Add the gate to the star system
            fromStarSystem.addGate(interChunkGate);
        }
    }

    private String getEdgeForDirection(int[] direction) {
        if (direction[0] == -1 && direction[1] == 0) return "left";
        if (direction[0] == 1 && direction[1] == 0) return "right";
        if (direction[0] == 0 && direction[1] == -1) return "top";
        if (direction[0] == 0 && direction[1] == 1) return "bottom";
        // Add more specific handling for diagonal directions if needed
        return "unknown";
    }
}