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



    private int CHUNK_SIZE = 100;



    // all the chunks and their star systems
    private int currentSystem = 0; // Track current system for zoom centering

    //private  Map<Point, List<Integer>> chunks = new HashMap<>();

    List<Integer> origin = new ArrayList<>();
    private Point currentChunk;

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

        if(chunks.containsKey(chunk)){
            return;
        }

        chunks.putIfAbsent(chunk, new HashSet<>());




        //bounds for how many, maybe should get more specific in future
        int ssCount = random.nextInt(3,5);

        //get ss coordinates to draw in map to make spiral
        Deque<Point2D.Double> coords = determineStarSystemCoords(ssCount, chunk);

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
        for(Integer systemId : allStarSystems){
            assignGates(systemId, allStarSystems);
        }

            //chunks.getOrDefault(new Point(0, 0), Collections.emptyList())
            //find possible connections for current ss

            //first add all gates for local group
            // call method to get general area for each gate
            // make a map :( of allstarsystems and their general area string

            //add gates to each ss
            //assignGates(allStarSystems.get(i), allStarSystems);

            // find ss' on edges to go to other chunks
            // make the gates to go to other edges.






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


            // the issue VVVV
            ///assignGates(allStarSystems.get(left.id), targetSys);

            assignGates(left.id, targetSys);


            System.out.println("teststerte3");
            // now give gate to target going to left.id
            //origin = new ArrayList<>(List.of(allStarSystems.get(left.id)));
            origin = new ArrayList<>(List.of(left.id));

            System.out.println("teststerte");
            assignGates(targetSys.get(0), origin);
            System.out.println("teststerte2");


            //find right neighbor connection
            targetSys = new ArrayList<>(List.of(findSystemNearestToEdge(new Point(chunkLocX+1, chunkLocY), "right").id));
            SystemData right =findSystemNearestToEdge(chunk, "right");
            //assignGates(allStarSystems.get(right.id), targetSys);
            assignGates(right.id, targetSys);
            // now give gate to target going to origin.id
            origin = new ArrayList<>(List.of(right.id));
            assignGates(targetSys.get(0), origin);

            //find bottom neighbor connection
            targetSys = new ArrayList<>(List.of(findSystemNearestToEdge(new Point(chunkLocX, chunkLocY+1), "bottom").id));
            SystemData bottom =findSystemNearestToEdge(chunk, "bottom");
            //assignGates(allStarSystems.get(bottom.id), targetSys);
            assignGates(bottom.id, targetSys);
            // now give gate to target going to origin.id
            origin = new ArrayList<>(List.of(bottom.id));
            assignGates(targetSys.get(0), origin);

            //find top neighbor connection
            targetSys = new ArrayList<>(List.of(findSystemNearestToEdge(new Point(chunkLocX, chunkLocY-1), "top").id));
            SystemData top =findSystemNearestToEdge(chunk, "top");
            //assignGates(allStarSystems.get(top.id), targetSys);
            assignGates(top.id, targetSys);
            // now give gate to target going to origin.id
            origin = new ArrayList<>(List.of(top.id));

            assignGates(targetSys.get(0), origin);

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







    /*private  Deque<Point2D.Double> determineStarSystemCoords(int ssCount){

        //wizard level code to find spiral locations ;)

        Deque< Point2D.Double> coords = new ArrayDeque<>();

        for(int i=0; i<ssCount; i++) {

            Point2D.Double position = new Point2D.Double(random.nextDouble(100,200), random.nextDouble(100,200));
            coords.push(position);

        }
        return coords;
    }*/
    private Deque<Point2D.Double> determineStarSystemCoords(int ssCount, Point chunk) {
    Deque<Point2D.Double> coords = new ArrayDeque<>();
    
    // Get the chunk bounds (assuming we're working in a single chunk)
    double minX = 0;
    double maxX = 800;  // Match the WIDTH constant
    double minY = 0;
    double maxY = 450;  // Match the HEIGHT constant
    
    // Center point for the spiral
    double centerX = (maxX + minX) / 2.0;
    double centerY = (maxY + minY) / 2.0;
    
    // Spiral parameters
    double initialRadius = 50;  // Starting radius
    double growthFactor = 0.3;  // How quickly the spiral expands
    double angleStep = 2 * Math.PI / ssCount;  // Even distribution of points
    
    for (int i = 0; i < ssCount; i++) {
        // Calculate angle and radius
        double angle = i * angleStep;
        double radius = initialRadius + (growthFactor * angle);
        
        // Calculate base position
        double x = centerX + (radius * Math.cos(angle));
        double y = centerY + (radius * Math.sin(angle));
        
        // Add some random variation
        double randomRadius = random.nextDouble(0, 20);
        double randomAngle = random.nextDouble(0, 2 * Math.PI);
        x += randomRadius * Math.cos(randomAngle);
        y += randomRadius * Math.sin(randomAngle);
        
        // Ensure points stay within bounds
        x = Math.max(minX + 50, Math.min(x, maxX - 50));
        y = Math.max(minY + 50, Math.min(y, maxY - 50));

        x+=chunk.getX()*CHUNK_SIZE;
        y+=chunk.getY()*CHUNK_SIZE;
        coords.add(new Point2D.Double(x, y));
    }
    
    return coords;
}    // build gates for a star system. used when chunks are create and new chunks discovered
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


                    int targetSystem = targetIDs.get(i);
                    int direction = (int)calculateGateDirection(ssID, targetSystem);
                    //double x = random.nextDouble(50, 700);
                    //double y = random.nextDouble(20, 300);
                    StarSystem target = StarSystemCache.getInstance().get(ssID);
                    String generalArea = getSystemChunkPosition(ssID,getSystemChunk(ssID) );
                    System.out.println("gate "+ssID+" is near "+generalArea);
                    javafx.geometry.Point2D coords = target.getValidGateSpawn(generalArea);
                    target.addGate(new Gate(direction, targetSystem, coords.getX(), coords.getY(), ssID));
                    systemData.get(ssID).addConnection(targetSystem);
                }
            }
        }
    }


    /**
    does not work :(
     */
    public String getSystemChunkPosition(int ssID, Point chunkLoc) {
        SystemData system = systemData.get(ssID);
        if (system == null) {
            return "";
        }

        // Calculate position relative to chunk's origin
        double chunkStartX = chunkLoc.x * CHUNK_SIZE;
        double chunkStartY = chunkLoc.y * CHUNK_SIZE;

        // Get relative position within the chunk
        double relX = system.position.x - chunkStartX;
        double relY = system.position.y - chunkStartY;


        // Define thirds of the chunk
        double thirdSize = CHUNK_SIZE / 3.0;

        // Determine horizontal position
        String horizontal;
        if (relX < thirdSize) {
            horizontal = "L";
        } else if (relX < 2 * thirdSize) {
            horizontal = "M";
        } else {
            horizontal = "R";
        }

        // Determine vertical position
        String vertical;
        if (relY < thirdSize) {
            vertical = "U";
        } else if (relY < 2 * thirdSize) {
            vertical = "M";
        } else {
            vertical = "L";
        }

        // If it's in the middle-middle, shift it to middle-right
        if (horizontal.equals("M") && vertical.equals("M")) {
            horizontal = "R";
        }

        String position = vertical + horizontal;
        System.out.println("Calculated position: " + position + " for system " + ssID + " in chunk " + chunkLoc);
        return position;
    }


    public double calculateGateDirection(int sourceId, int targetId) {
        SystemData sourceSystem = systemData.get(sourceId);
        SystemData targetSystem = systemData.get(targetId);

        if (sourceSystem == null || targetSystem == null) {
            return 0.0;
        }

        // Get the vector from source to target
        double dx = targetSystem.position.x - sourceSystem.position.x;
        double dy = targetSystem.position.y - sourceSystem.position.y;

        // Calculate angle in radians, convert to degrees
        // Math.atan2 returns angle in range -π to π
        double angle = Math.toDegrees(Math.atan2(dy, dx));

        // Add 90 degrees because gates are drawn pointing up by default
        // Then add 180 because we want the back of the gate to face the target
        angle += 270;

        // Normalize to 0-360 range
        angle = angle % 360;
        if (angle < 0) {
            angle += 360;
        }

        System.out.println("Gate from system " + sourceId + " to " + targetId +
                " rotated " + angle + " degrees");

        return angle;
    }


    // when player goes to a new system map need to be updated
    public void setCurrentSystem(int systemId) {

        // Mark the system as visited if not already
        // set current system.
        currentSystem = systemId;
        systemData.get(currentSystem).visited=true;


        currentSystem = systemId;
        systemData.get(currentSystem).visited=true;
        // check if we need to load neighboring chunk
        if(systemId ==0){
            currentChunk = new Point(0,0);
        }
        else{
            if(currentChunk != getSystemChunk(currentSystem)){
                // do the stuff
                //currentChunk = getSystemChunk(currentSystem);
                handleNeighbors();
            }
        }
        currentChunk = getSystemChunk(currentSystem);

        testPrint();

    }
    





    // this is called to open the map
    public void mapAction() {
        if (!isOpen) {
            isOpen = true;
            PlayerMovementState.getInstance().stop();
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
        isSpawning = false;
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
    // Draw grid background
    gc.setFill(Color.rgb(0, 0, 20));
    gc.fillRect(0, 0, WIDTH, HEIGHT);
    drawGrid();
    
    systemData.values().forEach(sys -> sys.updateScreenPosition(viewX, viewY, scale));
    
    // Draw connections with glow
    gc.setLineWidth(2.0 * scale);
    for (SystemData systemA : systemData.values()) {
        if (!systemA.isInView || (!debugMode && !isOneHopFromVisited(systemA))) continue;
        
        for (int connectedId : systemA.connections) {
            SystemData systemB = systemData.get(connectedId);
            if (systemB == null || !systemB.isInView || !shouldDrawConnection(systemA, systemB)) continue;
            
            // Glow effect
            gc.setStroke(Color.rgb(0, 255, 255, 0.3));
            gc.setLineWidth(4.0 * scale);
            gc.strokeLine(systemA.screenX, systemA.screenY, systemB.screenX, systemB.screenY);
            
            // Main line
            gc.setStroke(systemA.visited && systemB.visited ? 
                Color.rgb(255, 51, 153) : // Hot pink
                Color.rgb(128, 0, 255));   // Purple
            gc.setLineWidth(2.0 * scale);
            gc.strokeLine(systemA.screenX, systemA.screenY, systemB.screenX, systemB.screenY);
        }
    }
    
    // Draw systems
    double dotSize = 8.0 * scale;
    for (SystemData system : systemData.values()) {
        if (!system.isInView || (!debugMode && !isOneHopFromVisited(system))) continue;
        
        // Glow effect
        gc.setFill(Color.rgb(0, 255, 255, 0.3));
        gc.fillOval(
            system.screenX - (dotSize * 1.5)/2,
            system.screenY - (dotSize * 1.5)/2,
            dotSize * 1.5,
            dotSize * 1.5
        );
        
        // Main dot
        Color systemColor = system.id == currentSystem ? 
            Color.rgb(255, 51, 153) : // Current system: Hot pink
            system.visited ? 
                Color.rgb(0, 255, 255) : // Visited: Cyan
                Color.rgb(128, 0, 255);   // Unvisited: Purple
        
        gc.setFill(systemColor);
        gc.fillOval(
            system.screenX - dotSize/2,
            system.screenY - dotSize/2,
            dotSize,
            dotSize
        );
        
        // System ID with glow
        gc.setFill(Color.rgb(0, 255, 255, 0.5));
        gc.setFont(javafx.scene.text.Font.font("Arial", 10.0 * scale));
        gc.fillText(
            String.valueOf(system.id),
            system.screenX + dotSize,
            system.screenY - dotSize
        );
        gc.setFill(Color.WHITE);
        gc.fillText(
            String.valueOf(system.id),
            system.screenX + dotSize,
            system.screenY - dotSize
        );
    }
}

private void drawGrid() {
    gc.setStroke(Color.rgb(128, 128, 255, 0.2));
    gc.setLineWidth(1);
    double gridSize = 50 * scale;
    
    for (double x = 0; x < WIDTH; x += gridSize) {
        gc.strokeLine(x, 0, x, HEIGHT);
    }
    for (double y = 0; y < HEIGHT; y += gridSize) {
        gc.strokeLine(0, y, WIDTH, y);
    }
}

    private boolean shouldDrawConnection(SystemData systemA, SystemData systemB) {
        // Always draw connections between visited systems
        if (systemA.visited && systemB.visited) return true;

        // Draw connection if one system is visited and the other is unvisited
        return (systemA.visited && !systemB.visited) || (!systemA.visited && systemB.visited);
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
            isInView = screenX >= -400 && screenX <= WIDTH + 400 &&
                    screenY >= -400 && screenY <= HEIGHT + 400;
            //isInView = true;



        }

        // Add a connection to another system
        public void addConnection(int targetId) {
            connections.add(targetId);
        }


    }


    //debug mode

    private int debugChunksSpawned = 0;
    private int debugSpawnAmount = 50; //260; // Total chunks to spawn
    private int spawnEachTickAmount = 2; // How many to spawn per tick
    private int x = 0, y = 0;
    private int dx = 1, dy = 0;
    private int stepSize = 1;
    private int stepsTaken = 0;
    private int directionChanges = 0;
    private boolean isSpawning = false;
    private Timeline debugUpdater;
    private long lastTickTime = System.nanoTime();
    private final long MAX_TICK_TIME_NS = 4_000_000_000L; // 5000ms (5 seconds) in nanoseconds


    public void debug() {

        if(!isOpen){
            isSpawning = false;
            debugMode = false;
            return;
        }

        debugMode = !debugMode;
        draw();

        if (!isSpawning) {
            lastTickTime = System.nanoTime();
            debugChunksSpawned = 0;
            x = 0;
            y = 0;
            dx = 1;
            dy = 0;
            stepSize = 1;
            stepsTaken = 0;
            directionChanges = 0;
            isSpawning = true;

            // Start a repeating task to update debug spawning
            debugUpdater = new Timeline(new KeyFrame(Duration.millis(100), e -> {
                updateDebugSpawning();
                draw(); // Force a redraw every tick
            }));
            debugUpdater.setCycleCount(Timeline.INDEFINITE);
            debugUpdater.play();
        }
        else{
            isSpawning = false;
        }

    }


    public void updateDebugSpawning() {
        if (!isSpawning) return; // Stop if spawning was disabled externally

        long startTime = System.nanoTime();

        // If the last tick was too slow, stop spawning
        long elapsed = startTime - lastTickTime;
        if (elapsed > MAX_TICK_TIME_NS) {
            System.out.println("Tick took too long (" + (elapsed / 1_000_000) + "ms), stopping spawning.");
            isSpawning = false; // Prevent further spawning
            return;
        }

        int spawnLimit = spawnEachTickAmount;

        // Adjust spawn rate dynamically (increase if fast, decrease if slow)
        if (elapsed < 16_000_000) { // Less than 16ms (60 FPS)
            spawnLimit *= 2; // Speed up if fast
        } else if (elapsed > 33_000_000) { // More than 33ms (30 FPS)
            spawnLimit = Math.max(1, spawnLimit / 2); // Slow down if lagging
        }

        // Spawn chunks
        for (int i = 0; i < spawnLimit && debugChunksSpawned < debugSpawnAmount; i++) {
            createChunk(x, y);
            x += dx;
            y += dy;
            stepsTaken++;
            debugChunksSpawned++;

            if (stepsTaken == stepSize) {
                stepsTaken = 0;
                directionChanges++;
                int temp = dx;
                dx = -dy;
                dy = temp;
                if (directionChanges % 2 == 0) stepSize++;
            }
        }

        lastTickTime = System.nanoTime();
    }







    private Point getChunkFromLocation(Point location) {
        // Assuming chunk size is consistent with your implementation
        return new Point(
                (int)Math.floor(location.x / CHUNK_SIZE),
                (int)Math.floor(location.y / CHUNK_SIZE)
        );
    }


    // test print out some impoertant info of current sys
    private void testPrint(){

        System.out.print("\ncurrently in "+currentSystem);


        List<Gate> gates = StarSystemCache.getInstance().get(currentSystem).getGates();
        System.out.println(" ");
        int gateCount = gates.size();
        System.out.print("which has gates going to: ");
        for(Gate g : gates){
            System.out.print(" "+g.getTargetSystem()+", ");
        }
        System.out.println(' ');
        Point chunk = getSystemChunk(currentSystem);
        int chunkLocX = (int)chunk.getX();
        int chunkLocY = (int)chunk.getY();

        System.out.println("l1");

        int leftTarget = findSystemNearestToEdge(new Point(chunkLocX-1, chunkLocY), "left").id;
        System.out.println("l2");

        int left =findSystemNearestToEdge(chunk, "left").id;
        System.out.println("left ss "+left+" should go to "+leftTarget);

        System.out.println("r1");
        int rightTarget = findSystemNearestToEdge(new Point(chunkLocX+1, chunkLocY), "right").id;
        System.out.println("r2");

        int right =findSystemNearestToEdge(chunk, "right").id;
        System.out.println("right ss "+right+" should go to "+rightTarget);

        System.out.println("b1");
        int bottomTarget = findSystemNearestToEdge(new Point(chunkLocX, chunkLocY+1), "bottom").id;
        System.out.println("b2");

        int bottom =findSystemNearestToEdge(chunk, "bottom").id;
        System.out.println("bottom ss "+bottom+" should go to "+bottomTarget);

        System.out.println("bouttatopt");
        int topTarget = findSystemNearestToEdge(new Point(chunkLocX, chunkLocY-1), "top").id;
        System.out.println("bouttatop");
        int top =findSystemNearestToEdge(chunk, "top").id;
        System.out.println("success!");

        System.out.println("top ss "+top+" should go to "+topTarget);


        System.out.println("all chunks"+chunks);
        System.out.println("all systems"+systemData);


    }




}
