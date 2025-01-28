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
    private static final Random random = new Random(42); // Fixed seed for consistency

    // Starting chunk coordinates
    private static final ChunkCoord STARTING_CHUNK = new ChunkCoord(0, 0);

    // Singleton instance
    private static GalaxyMap instance;
    private StarSystemData currentSystem; // Track current system for zoom centering


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

        Chunk(ChunkCoord coord) {
            // Generate 2-5 systems per chunk
            int systemCount = random.nextInt(4) + 2; // Random number between 2 and 5
            for (int i = 0; i < systemCount; i++) {
                // Add some randomization to position but keep it away from edges
                int padding = CHUNK_SIZE / 4;
                double x = coord.x * CHUNK_SIZE + padding + random.nextDouble() * (CHUNK_SIZE - 2 * padding);
                double y = coord.y * CHUNK_SIZE + padding + random.nextDouble() * (CHUNK_SIZE - 2 * padding);
                systems.add(new StarSystemData(x, y, generateSystemId(coord, i)));
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
        // Initialize with just the starting chunk
        discoveredChunks.add(STARTING_CHUNK);
        loadedChunks.put(STARTING_CHUNK, new Chunk(STARTING_CHUNK));

        // Find and set initial current system
        Chunk startChunk = loadedChunks.get(STARTING_CHUNK);
        if (!startChunk.systems.isEmpty()) {
            currentSystem = startChunk.systems.get(0);
        }
    }

    public static GalaxyMap getInstance() {
        if (instance == null) {
            instance = new GalaxyMap();
        }
        return instance;
    }

    private static int generateSystemId(ChunkCoord coord, int systemIndex) {
        return (coord.x * 10000 + coord.y) * 100 + systemIndex;
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

        // Only draw systems from discovered chunks
        for (ChunkCoord coord : discoveredChunks) {
            Chunk chunk = loadedChunks.get(coord);
            if (chunk == null) {
                chunk = new Chunk(coord);
                loadedChunks.put(coord, chunk);
            }

            for (StarSystemData system : chunk.systems) {
                double dotSize = 6;

                if (currentSystem != null && system.id == currentSystem.id) {
                    // Current system
                    gc.setFill(Color.PURPLE);
                } else if (StarSystemCache.get(system.id) != null) {
                    // System has been visited
                    gc.setFill(Color.WHITE);
                    system.visited = true;
                } else {
                    // Undiscovered system
                    gc.setFill(Color.GRAY);
                }

                gc.fillOval(
                        system.x - dotSize/2,
                        system.y - dotSize/2,
                        dotSize,
                        dotSize
                );
            }
        }

        gc.restore();
    }
    // Method to discover a new chunk when visiting a system
    public void discoverChunkFromSystemCoords(double x, double y) {
        ChunkCoord newChunk = getChunkCoordFromSystem(x, y);
        if (!discoveredChunks.contains(newChunk)) {
            discoveredChunks.add(newChunk);
            loadedChunks.put(newChunk, new Chunk(newChunk));
            draw();
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
    public  void setCurrentSystem(int systemId) {
        // Find the system with the given ID
        for (Chunk chunk : loadedChunks.values()) {
            for (StarSystemData system : chunk.systems) {
                if (system.id == systemId) {
                    currentSystem = system;
                    draw();
                    return;
                }
            }
        }
    }
}