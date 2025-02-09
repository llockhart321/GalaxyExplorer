import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.*;
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
import javafx.util.Duration;

import java.util.*;
import java.util.stream.Collectors;
import java.util.ArrayDeque;
import java.util.Deque;

public class GalaxyMap {

    // Singleton instance
    private static GalaxyMap instance;

    private  GraphicsContext gc;
    private  AnimationHandler ah;


    private  boolean isOpen = false;
    private  boolean validDebug = true;

    private  final int WIDTH = 800;
    private  final int HEIGHT = 450;
    private double viewX = 0;
    private double viewY = 0;
    private double scale = 1.0;
    private double minScale = 0.5;
    private double maxScale = 6.0;
    private double lastMouseX;
    private double lastMouseY;
    private boolean isPanning = false;

    //random for deciding how many star systems in a chunk
    private  final Random random = new Random();




    private boolean debugMode = false;



    private int CHUNK_SIZE = 50;



    // all the chunks and their star systems
    private int currentSystem = 0; // Track current system for zoom centering

    //private  Map<Point, List<Integer>> chunks = new HashMap<>();
    private Map<Point, Set<Integer>> chunks = new HashMap<>();


    //the star systems and whether or not we have visited them or not
    //private final Map<Integer, Boolean> visitedSS = new HashMap<>();

    //star systems and their on map locations
    //private final Map<Integer, Point2D.Double> starSystemPositions = new HashMap<>();

    //vars for innerclass drwawring
    private  Map<Integer, SystemData> systemData = new HashMap<>();
    private Queue<Integer> processingQueue = new LinkedList<>();




    private GalaxyMap() {

    }
    
    public static GalaxyMap getInstance() {
        if (instance == null) {
            instance = new GalaxyMap();
        }
        return instance;
    }

    //called from main to set gc and animationh
    public void set(GraphicsContext gc, AnimationHandler ah) {
        this.gc = gc;
        this.ah = ah;
    }


    //create the next chunk of star systems. is called by main to create inital starsystems
    public  void createChunk(int chunkLocX, int chunkLocY){


        //get chunks coords.
        //Point chunk = determineChunkLoc();
        Point chunk = new Point(chunkLocX, chunkLocY);

        //create chunk in chunks list
        //chunks.putIfAbsent(chunk, new ArrayList<>());
        chunks.putIfAbsent(chunk, new HashSet<>());



        //bounds for how many, maybe should get more specific in future
        int ssCount = random.nextInt(3,5);

        //get ss coordinates to draw in map to make spiral
        Deque<Point2D.Double> coords = determineStarSystemCoords(ssCount);

        // add ss' to chunk
        for(int i=0; i<ssCount; i++) {
            //first create system
            int newSys = StarSystemCache.getInstance().createSystem(gc);
            //then add system to chunks list
            Set<Integer> starSystems = chunks.computeIfAbsent(chunk, k -> new HashSet<>());
            starSystems.add(newSys);
            //chunks.computeIfAbsent(chunk, k -> new ArrayList<>()).add(newSys);


            SystemData sysData = new SystemData(newSys, coords.pop());
            systemData.put(newSys, sysData);



        }


        // add gates to all the star systems we have just created
        //List<Integer> allStarSystems = chunks.getOrDefault(chunk, Collections.emptyList());
        Set<Integer> allStarSystemsSet = chunks.getOrDefault(chunk, Collections.emptySet());

        System.out.println("Star systems in chunk " + chunk + ": " + chunks.getOrDefault(chunk, Collections.emptySet()));

        List<Integer> allStarSystems = new ArrayList<>(allStarSystemsSet);
        //assign gates
        for(int i=0; i<allStarSystems.size(); i++){

            //chunks.getOrDefault(new Point(0, 0), Collections.emptyList())
            //find possible connections for current ss

            //first add all gates for local group
            // call method to get general area for each gate
            // make a map :( of allstarsystems and their general area string

            //add gates to each ss
            assignGates(allStarSystems.get(i), allStarSystems);

            // find ss' on edges to go to other chunks
            // make the gates to go to other edges.



        }


        handleNeighbors();

    }

    private void handleNeighbors(){

        // create 3x3 chunk grid around currentstarsystem
        createSurroundingChunks();

        Point chunk = getSystemChunk(currentSystem);
        int chunkLocX = (int)chunk.getX();
        int chunkLocY = (int)chunk.getY();


        Set<Integer> allStarSystemsSet = chunks.getOrDefault(chunk, Collections.emptySet());
        List<Integer> allStarSystems = new ArrayList<>(allStarSystemsSet);


        List<Integer> targetSys = new ArrayList<>();

        if(getSystemChunk(currentSystem) == chunk){

            //find left neighbor connection
            targetSys = new ArrayList<>(List.of(findSystemNearestToEdge(new Point(chunkLocX-1, chunkLocY), "left").id));
            SystemData left =findSystemNearestToEdge(chunk, "left");
            assignGates(allStarSystems.get(left.id), targetSys);

            //find right neighbor connection
            targetSys = new ArrayList<>(List.of(findSystemNearestToEdge(new Point(chunkLocX+1, chunkLocY), "right").id));
            SystemData right =findSystemNearestToEdge(chunk, "right");
            assignGates(allStarSystems.get(right.id), targetSys);

            //find bottom neighbor connection
            targetSys = new ArrayList<>(List.of(findSystemNearestToEdge(new Point(chunkLocX, chunkLocY+1), "bottom").id));
            SystemData bottom =findSystemNearestToEdge(chunk, "bottom");
            assignGates(allStarSystems.get(bottom.id), targetSys);

            //find top neighbor connection
            targetSys = new ArrayList<>(List.of(findSystemNearestToEdge(new Point(chunkLocX, chunkLocY-1), "top").id));
            SystemData top =findSystemNearestToEdge(chunk, "top");
            assignGates(allStarSystems.get(top.id), targetSys);

        }
    }

    private Point getSystemChunk(int id) {
        // Find the chunk that contains currentSystem
        Point currentChunk = null;
        for (Map.Entry<Point, Set<Integer>> entry : chunks.entrySet()) {
            if (entry.getValue().contains(id)) {
                currentChunk = entry.getKey();
                break;
            }
        }
        return currentChunk;
    }

    public void createSurroundingChunks() {



        Point currentChunk = getSystemChunk(currentSystem);
        if (currentChunk == null) {
            System.out.println("System not found in any chunk.");
            return;
        }

        int cx = currentChunk.x;
        int cy = currentChunk.y;

        // Iterate through the 3x3 grid around the current chunk
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int newX = cx + dx;
                int newY = cy + dy;
                Point newChunk = new Point(newX, newY);

                // Check if the chunk already exists
                if (!chunks.containsKey(newChunk)) {
                    createChunk(newX, newY);
                }
            }
        }
    }



    private SystemData findSystemNearestToEdge(Point chunkCoord, String edge) {
        Set<Integer> systemIds = chunks.getOrDefault(chunkCoord, Collections.emptySet());
        SystemData nearestSystem = null;
        double minDistance = Double.MAX_VALUE;

        for (int systemId : systemIds) {
            SystemData system = systemData.get(systemId);
            if (system == null) continue;

            double distance = 0;
            switch (edge) {
                case "left":
                    distance = system.position.x;
                    break;
                case "right":
                    distance = Math.abs(system.position.x - (chunkCoord.x + 1) ); // ommitted * chunksize
                    break;
                case "top":
                    distance = system.position.y;
                    break;
                case "bottom":
                    distance = Math.abs(system.position.y - (chunkCoord.y + 1) );
                    break;
            }

            if (distance < minDistance) {
                minDistance = distance;
                nearestSystem = system;
            }
        }

        return nearestSystem;
    }







    private  Deque<Point2D.Double> determineStarSystemCoords(int ssCount){

        //wizard level code to find spiral locations ;)

        Deque< Point2D.Double> coords = new ArrayDeque<>();

        for(int i=0; i<ssCount; i++) {

            Point2D.Double position = new Point2D.Double(random.nextDouble(100,200), random.nextDouble(100,200));
            coords.push(position);

        }
        return coords;
    }




    // build gates for a star system. used when chunks are create and new chunks discovered
    private  void assignGates(int ssID, List<Integer>targetIDs){

        List<Gate> systemGates = StarSystemCache.getInstance().get(ssID).getGates();

        //create gate
        for(int i=0; i<targetIDs.size(); i++){

            //make sure gate doesnt go to self
            if(ssID != targetIDs.get(i)){
                // make sure gate doesnt already exist
                boolean exists = false;
                for (Gate gate : systemGates) {
                    if (gate.getTargetSystem() == targetIDs.get(i)) {
                        exists = true;
                        break;
                    }
                }
                // if ssid doesnt already have gate to target
                if(!exists) {

                    int direction = 0;
                    int targetSystem = targetIDs.get(i);
                    double x = random.nextDouble(50, 700);
                    double y = random.nextDouble(20, 300);
                    //getValidGateSpawn(string generalArea)
                    StarSystemCache.getInstance().get(ssID).addGate(new Gate(direction, targetSystem, x, y, ssID));
                    systemData.get(ssID).addConnection(targetSystem);
                }
            }
        }
    }


    // when player goes to a new system map need to be updated
    public void setCurrentSystem(int systemId) {

        // Mark the system as visited if not already
        // set current system.
        currentSystem = systemId;
        systemData.get(currentSystem).visited=true;
        // check if we need to load neighboring chunk

    }
    





    // this is called to open the map
    public void mapAction() {
        if (!isOpen) {
            isOpen = true;
            ah.stop();
            setupEventHandlers();
            draw();
        } else {
            isOpen = false;
            closeMap();
            scale = 1;
            viewX = 0;
            viewY = 0;
            ah.start();
        }
    }

    public void closeMap() {
        isOpen = false;
        debugMode = false;

        /*
        isOpen = false;
        debugDrawMode = false;  // Disable debug drawing mode
        if (expansionTimeline != null) {
            expansionTimeline.stop();
        }
        isDebugExpanding = false;

         */
    }




    //draw
    private void draw() {
        // Clear background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        // Update screen positions for all systems that will be drawn
        systemData.values().forEach(sys -> {
            sys.updateScreenPosition(viewX, viewY, scale);
        });

        // Draw connections
        gc.setLineWidth(1.0 * scale);
        for (SystemData systemA : systemData.values()) {
            // Skip if system is not in view or not within one hop (in normal mode)
            if (!systemA.isInView) continue;
            if (!debugMode && !isOneHopFromVisited(systemA)) continue;

            for (int connectedId : systemA.connections) {
                SystemData systemB = systemData.get(connectedId);
                if (systemB == null || !systemB.isInView) continue;

                // In normal mode, only draw connections to systems within one hop
                if (!debugMode && !isOneHopFromVisited(systemB)) continue;

                // Set connection color based on visited status of both systems
                if (systemA.visited && systemB.visited) {
                    gc.setStroke(Color.WHITE);  // Both systems visited = white connection
                } else {
                    gc.setStroke(Color.GRAY);   // At least one system unvisited = gray connection
                }

                gc.strokeLine(systemA.screenX, systemA.screenY,
                        systemB.screenX, systemB.screenY);
            }
        }

        // Draw systems
        double dotSize = 6.0 * scale;
        for (SystemData system : systemData.values()) {
            // Skip if system is not in view or not within one hop (in normal mode)
            if (!system.isInView) continue;
            if (!debugMode && !isOneHopFromVisited(system)) continue;

            // Set system color
            if (system.id == currentSystem) {
                gc.setFill(Color.PURPLE);
            } else if (system.visited) {
                gc.setFill(Color.WHITE);
            } else {
                gc.setFill(Color.GRAY);
            }

            gc.fillOval(
                    system.screenX - dotSize/2,
                    system.screenY - dotSize/2,
                    dotSize,
                    dotSize
            );

            // Draw system ID
            gc.setFill(Color.CYAN);
            gc.setFont(javafx.scene.text.Font.font(8.0 * scale));
            gc.fillText(
                    String.valueOf(system.id),
                    system.screenX + dotSize,
                    system.screenY - dotSize
            );
        }
    }


    private boolean isOneHopFromVisited(SystemData system) {
        // If the system itself is visited, return true
        if (system.visited) return true;

        // Check if any directly connected system is visited (one hop)
        return system.connections.stream()
                .map(id -> systemData.get(id))
                .anyMatch(connected -> connected != null && connected.visited);
    }

    private void zoom(double factor) {
    // Calculate new scale while respecting bounds
    double newScale = scale * factor;
    if (newScale < minScale) {
        newScale = minScale;
    } else if (newScale > maxScale) {
        newScale = maxScale;
    }
    
    // Only adjust view if we have a current system
    if (currentSystem != 0) {
        SystemData currentSysData = systemData.get(currentSystem);
        if (currentSysData != null) {
            // Get the current system's position
            Point2D.Double sysPos = currentSysData.position;
            
            // Calculate the system's screen position before zoom
            double oldScreenX = (sysPos.x * scale) - (viewX * scale) + (WIDTH / 2);
            double oldScreenY = (sysPos.y * scale) - (viewY * scale) + (HEIGHT / 2);
            
            // Update scale
            scale = newScale;
            
            // Calculate the system's screen position after zoom
            double newScreenX = (sysPos.x * scale) - (viewX * scale) + (WIDTH / 2);
            double newScreenY = (sysPos.y * scale) - (viewY * scale) + (HEIGHT / 2);
            
            // Adjust view position to maintain system position
            viewX += (newScreenX - oldScreenX) * scale;
            viewY += (newScreenY - oldScreenY) * scale;
        }
    } else {
        // If no current system, just update scale
        scale = newScale;
    }
    
    // Redraw the map
    draw();
}


    //setup event handlers to use zoom
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

                // Invert the deltas for natural panning
                viewX -= deltaX / scale;  // Changed from += to -=
                viewY -= deltaY / scale;  // Changed from += to -=

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
                    zoomFactor = 1.0 + e.getDeltaY() * 0.000001;  // Changed from 0.5 - to 1.0 +
                } else {
                    // Handle mouse wheel
                    zoomFactor = e.getDeltaY() > 0 ? 0.9 : 1.1;  // Swapped 1.1 and 0.9
                }
                zoom(zoomFactor);
            }
        });

    }







    private boolean isConnectedToVisited(SystemData system) {
        // If the system itself is visited, return true
        if (system.visited) return true;

        // Check if any connected system is visited
        return system.connections.stream()
                .map(id -> systemData.get(id))
                .anyMatch(connected -> connected != null && connected.visited);
    }

    //inner class to make effeeicent drawing i hope

    private class SystemData {
        private final int id;                      // Star system ID
        private final Point2D.Double position;     // Position on map
        private final Set<Integer> connections;    // Connected system IDs

        // States for map visualization
        private boolean visited;           // If player has been here
        private boolean mapProcessed;      // If system has been processed for map display
        private boolean visibleThisFrame;  // If system should be drawn this frame

        // Cached render data to avoid recalculations
        private double screenX;           // Transformed X for current view
        private double screenY;           // Transformed Y for current view
        private boolean isInView;         // If system is in current viewport

        public SystemData(int id, Point2D.Double position) {
            this.id = id;
            this.position = position;
            this.connections = new HashSet<>();
            this.visited = false;
            this.mapProcessed = false;
            this.visibleThisFrame = false;
        }

        // Update screen position based on current view transform
        public void updateScreenPosition(double viewX, double viewY, double scale) {
            screenX = (position.x - viewX) * scale + (WIDTH / 2.0);
            screenY = (position.y - viewY) * scale + (HEIGHT / 2.0);

            // Check if system is in current viewport
            isInView = screenX >= -50 && screenX <= WIDTH + 50 &&
                    screenY >= -50 && screenY <= HEIGHT + 50;
        }

        // Add a connection to another system
        public void addConnection(int targetId) {
            connections.add(targetId);
        }


    }


    //debug mode



    public void debug() {

        debugMode = !debugMode;
        draw();
        /*
        if (!isOpen || !validDebug) return;
        validDebug = false;

        System.out.println("Debug started");
        debugDrawMode = true;  // Enable debug drawing mode

        if (expansionTimeline != null) {
            expansionTimeline.stop();
        }

        expansionQueue.clear();
        queuedChunks.clear();
        isDebugExpanding = true;
        currentWave = 0;

        Point currentChunk = getSystemChunk(currentSystem);
        if (currentChunk == null) return;

        addExpansionWave(currentChunk, currentWave);
        startDebugExpansion();

        // Force a redraw to show all systems immediately
        draw();

         */
    }



    private Point getChunkFromLocation(Point location) {
        // Assuming chunk size is consistent with your implementation
        return new Point(
                (int)Math.floor(location.x / CHUNK_SIZE),
                (int)Math.floor(location.y / CHUNK_SIZE)
        );
    }

}
