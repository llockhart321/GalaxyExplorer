import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Point2D;



public class StarSystem {
   private static final int GRID_WIDTH = 4000;
   private static final int GRID_HEIGHT = 2250;
   private static final int GRID_CELL_SIZE = 450; 
   private List <Planet> planets;
   private List<Asteroid> asteroids;
   private List <Gate> gates;
   private ArrayList<Double> starX;
   private ArrayList<Double> starY;
   private ArrayList<Double> starRadius;
   Planet planet;
   GraphicsContext gc;
   private int ID;
   private static int idCounter = 0;
   private StarSystemNebula nebula;
   private Sun sun;
   


   // this is the location of the star systejm in the map.
   private double xLoc;
   private double yLoc;


   public StarSystem(GraphicsContext gc, int potentialID) {
      if(potentialID == -1){     
         this.ID = idCounter++;
      }else{
         ID = potentialID;
         idCounter = ID+1;
      }
      
      starX = new ArrayList<Double>();
      starY = new ArrayList<Double>();
      starRadius = new ArrayList<Double>();
      //not the most effecient i dont think but well see if it matters
      this.gc=gc;
      
      Random rand = new Random();
      int numPlanets = rand.nextInt(5) + 1;  // Random number between 1 and 5
      int numAsteroids = rand.nextInt(5) + 1; // Random number between 1 and 5
      
      // Set number of gates from an outside input
      //until then it is 2 ;)
      int numGates = 2;
      
      this.planets = new ArrayList<>();
      this.asteroids = new ArrayList<>();
      this.gates = new ArrayList<>();
      nebula = new StarSystemNebula(800, 450, ID); //for the parallax clouds
      
      for (int i = 0; i < numPlanets; i++) { //add a random number of planets to the array
         int minRadius = 20;
         int maxRadius = 100;
         int minDistance = 500;
         int maxDistance = 1500; //setting mins and maxes for random parameters for planets 
       
         
         Color color = Color.color(Math.random(), Math.random(), Math.random());
        
         int radius = rand.nextInt(maxRadius - minRadius + 1) + minRadius; //getting a random radius and distance for each planet 
         int distance = rand.nextInt(maxDistance - minDistance + 1) + minDistance;
         this.planets.add(new Planet(color, distance, 30, radius, 30));
         System.out.println("new planet");
      }
      
              // Generate asteroids
      int maxAstDist = 800;
      int minAstDist = 400;
      
      int asteroidDistance = rand.nextInt(maxAstDist - minAstDist + 1) + minAstDist; // Fixed distance for all asteroids
      for (int i = 0; i < numAsteroids; i++) {
         int minRadius = 3;
         int maxRadius = 8;
         int radius = rand.nextInt(maxRadius - minRadius + 1) + minRadius;
         int speed = rand.nextInt(50) + 50; // Random speed between 50 and 100
         double position = rand.nextDouble() * 2 * Math.PI; // Random initial position
      
         this.asteroids.add(new Asteroid((double)asteroidDistance, position, (double)radius, speed));
         System.out.println("new asteroid");
      }
      

      //add gates
      for (int i = 0; i < numGates; i++) {
          //still need to get accurate next system. rn im just doing +1
          // this rand allows for gates to spawn in orbit path. this needs to be fixed.


          //this.gates.add(new Gate( 0, ID+1, rand.nextInt(700), rand.nextInt(400), this, 90));
          //maybe get target systems for sstar system cache

          //Random rand = new Random();
          // First gate connects to previous system (ID - 1)
          if (ID > 0) {
              this.gates.add(new Gate(0, ID - 1, rand.nextInt(700), rand.nextInt(400), this, 90));

          }
          // Second gate connects to next system (ID + 1)
          this.gates.add(new Gate(0, ID + 1, rand.nextInt(700), rand.nextInt(400), this, 90));
      }
      int numStars = 100000; // Number of stars to draw
      for (int i = 0; i < numStars; i++) {
          // Generate random positions within a 4000x2250 space
          double x = (rand.nextDouble() * 40000) - 20000;
          double y = (rand.nextDouble() * 22500) - 11250;
          double radius = rand.nextDouble() * 2 + 1; // Random size for stars (1-3 pixels)
          
          starX.add(x);
          starY.add(y);
          starRadius.add(radius);
      }
        
       // planet = new Planet(Color.BLUE, 500, 30, 10, 100);
      // Create the sun
    sun = new Sun(); // Add this line and create a Sun field in the class
    
    /*for (int i = 0; i < numPlanets; i++) {
        int minRadius = 80;
        int maxRadius = 200;
        int minDistance = 10000;
        int maxDistance = 1000000;
        
        Color color = Color.color(Math.random(), Math.random(), Math.random());
        
        int radius = rand.nextInt(maxRadius - minRadius + 1) + minRadius;
        int distance = rand.nextInt(maxDistance) + minDistance;
        this.planets.add(new Planet(color, distance, 30, radius, 1));
    }
      */
      // Fill the gates arraylist
   
      
   }
   public Point2D getValidGateSpawn(String generalArea) {
        // Define the grid boundaries based on input
        int xStart = 0, xEnd = 0, yStart = 0, yEnd = 0;

        switch (generalArea) {
            case "UR": // Upper right
               xStart = GRID_WIDTH / 2;
               xEnd = GRID_WIDTH;
               yStart = 0;
               yEnd = GRID_HEIGHT / 2;
               break;
           case "UM": // Upper middle
               xStart = GRID_WIDTH / 4;
               xEnd = (3 * GRID_WIDTH) / 4;
               yStart = 0;
               yEnd = GRID_HEIGHT / 2;
               break;
           case "UL": // Upper left
               xStart = 0;
               xEnd = GRID_WIDTH / 2;
               yStart = 0;
               yEnd = GRID_HEIGHT / 2;
               break;
           case "MR": // Middle right
               xStart = GRID_WIDTH / 2;
               xEnd = GRID_WIDTH;
               yStart = GRID_HEIGHT / 4;
               yEnd = (3 * GRID_HEIGHT) / 4;
               break;
           case "ML": // Middle left
               xStart = 0;
               xEnd = GRID_WIDTH / 2;
               yStart = GRID_HEIGHT / 4;
               yEnd = (3 * GRID_HEIGHT) / 4;
               break;
           case "LR": // Lower right
               xStart = GRID_WIDTH / 2;
               xEnd = GRID_WIDTH;
               yStart = GRID_HEIGHT / 2;
               yEnd = GRID_HEIGHT;
               break;
           case "LM": // Lower middle
               xStart = GRID_WIDTH / 4;
               xEnd = (3 * GRID_WIDTH) / 4;
               yStart = GRID_HEIGHT / 2;
               yEnd = GRID_HEIGHT;
               break;
           case "LL": // Lower left
               xStart = 0;
               xEnd = GRID_WIDTH / 2;
               yStart = GRID_HEIGHT / 2;
               yEnd = GRID_HEIGHT;
               break;
            default:
                System.out.println("Invalid area input");
                return null;
        }

        Random rand = new Random();

        // Try random points within the specified area until a valid one is found
        for (int attempts = 0; attempts < 1000; attempts++) {
            double x = xStart + rand.nextDouble() * (xEnd - xStart);
            double y = yStart + rand.nextDouble() * (yEnd - yStart);
            Point2D candidatePoint = new Point2D(x, y);

            // Check for collisions with the sun and planets
            if (!isOverlappingSun(candidatePoint) && !isOverlappingPlanet(candidatePoint)) {
                return candidatePoint;
            }
        }

        System.out.println("No valid gate location found after multiple attempts.");
        return null;
    }

    private boolean isOverlappingSun(Point2D point) {
        double distanceToSun = point.distance(Sun.WORLD_CENTER_X, Sun.WORLD_CENTER_Y);
        return distanceToSun < sun.getRadius();
    }

    private boolean isOverlappingPlanet(Point2D point) {
        for (Planet planet : planets) {
            double distanceToPlanet = point.distance(planet.getX(), planet.getY());
            if (distanceToPlanet < planet.getRadius()) {
                return true;
            }
        }
        return false;
    }
   
   
   public List<Planet> getPlanets() {
       return this.planets;
   }
   public List<Gate> getGates() {
       return this.gates;
   }
   public void debugRenderSunCoordinates(GraphicsContext gc, double cameraOffsetX, double cameraOffsetY) {
    System.out.println("Sun Center X: " + Sun.WORLD_CENTER_X);
    System.out.println("Sun Center Y: " + Sun.WORLD_CENTER_Y);
}
   
   public void checkPlayerPlanetCollisions(Player player) {
       // Check against each planet
       for (Planet planet : this.planets) {
           if (planet.isCollidingWith(player)) {
               planet.handleCollision(player);
               //System.out.println("collision check");
               // Break here if you only want to handle one collision at a time
               //break;
           }
       }
   }

    public void collisionCheck(GraphicsContext gc) {
        Player player = Player.getInstance();
        for (Gate gate : this.gates) {
            if (gate.isCollidingWith(player)) {
                gate.activate(gc);
                return;
            }
        }
    }
    public void checkPlayerAsteroidCollisions(Player player) {
        for (Asteroid asteroid : asteroids) {
            asteroid.checkPlayerCollision(player);
        }
    }


   public int getID(){
      return this.ID;
   }

    public void setID(int ID) {
        this.ID = ID;
    }

    public boolean checkMissileCollisions(Point2D missilePos, double missileRadius) {
       Iterator<Asteroid> iter = asteroids.iterator();
       while (iter.hasNext()) {
           Asteroid asteroid = iter.next();
           if (asteroid.checkMissileCollision(missilePos, missileRadius)) {
               if (asteroid.isEmpty()) {
                   iter.remove();
               }
               return true;
           }
       }
       return false;
   }
   
   public void drawMe(GraphicsContext gc) {
      double cameraOffsetX = Camera.getInstance(this).getMapCenterOffsetX();
      double cameraOffsetY = Camera.getInstance(this).getMapCenterOffsetY();
      // Set the background to black
      gc.setFill(Color.BLACK);
      gc.fillRect(0, 0, 1000,1000);
      
      

       // Check planet-planet collisions
       for (int i = 0; i < planets.size(); i++) {
           for (int j = i + 1; j < planets.size(); j++) {
               planets.get(i).checkPlanetCollision(planets.get(j));
           }
       }
       // Check planet-asteroid collisions
       for (Planet planet : planets) {
           for (Asteroid asteroid : asteroids) {
               //planet.checkAsteroidCollision(asteroid);
           }
       }

       checkPlayerAsteroidCollisions(Player.getInstance());

       //draw nebula
      nebula.setOffset(cameraOffsetX, cameraOffsetY);
      nebula.draw(gc);
      
      gc.setFill(Color.WHITE);
      for (int i = 0; i < starX.size(); i++) {
          gc.fillOval(starX.get(i) - cameraOffsetX, starY.get(i) - cameraOffsetY, starRadius.get(i), starRadius.get(i));
      }
      
       //draw the sun
       sun.drawMe(gc, cameraOffsetX, cameraOffsetY);

      



        // draw gates
      for (int i = 0; i<gates.size(); i++){
         gates.get(i).drawMe(gc, cameraOffsetX, cameraOffsetY);
      }



        //planet.drawMe(gc);

      for (int i = 0; i < planets.size(); i++) {
         planets.get(i).drawMe(gc, cameraOffsetX, cameraOffsetY);
      }
      
         // Draw asteroids
      for (Asteroid asteroid : asteroids) {
         asteroid.drawMe(gc, cameraOffsetX, cameraOffsetY);
      }
      Player.getInstance().drawMe(gc, cameraOffsetX, cameraOffsetY);
      
      //for debug
      //debugRenderSunCoordinates(gc, cameraOffsetX, cameraOffsetY);
   }


    public boolean checkMissilePlanetCollisions(Point2D missilePos, double missileRadius) {
        for (Planet planet : planets) {
            // Get planet center and radius
            double planetX = planet.getRelativeX(500);
            double planetY = planet.getRelativeY(500);
            double planetRadius = planet.getBounds().getRadius();

            // Calculate distance between missile and planet center
            double dx = missilePos.getX() - planetX;
            double dy = missilePos.getY() - planetY;
            double distance = Math.sqrt(dx * dx + dy * dy);

            // Check if missile touches planet
            if (distance < (planetRadius + missileRadius)) {
                return true; // Collision detected
            }
        }
        return false;
    }

   // getters and setters for maptivities
   public double getxLoc() {
      return xLoc;
   }

   public double getyLoc() {
      return yLoc;
   }

   public void setxLoc(double xLoc) {
      this.xLoc = xLoc;
   }

   public void setyLoc(double yLoc) {
      this.yLoc = yLoc;
   }

    public void addGate(Gate gate) {
        if (this.gates == null) {
            this.gates = new ArrayList<>();
        }
        this.gates.add(gate);
    }
}
