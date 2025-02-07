import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import java.util.List;
import javafx.stage.*;
import java.util.Random;





public class Gate {
   private double direction;
   private int targetSystem;
   private StarSystem system;
   private double x, y;
   private double sizeX = 10;
   private double sizeY = 50;
   private Rectangle bounds;
   private int ticker = 0;
   private double pulseSize = 1;
   private int rotation;

   public Gate(double direction, int targetSystem, double x, double y, StarSystem system, int rotation) {
      this.direction = direction;
      this.targetSystem = targetSystem;
      this.system = system;
      this.x = x;
      this.y = y;
      this.rotation = rotation;
      updateBounds();
   }

   private void updateBounds() {
        // Update rectangle bounds to match current position
      if (bounds == null) {
         bounds = new Rectangle(x, y, sizeX, sizeY);
      } else {
         bounds.setX(x);
         bounds.setY(y);
         bounds.setWidth(sizeX);
         bounds.setHeight(sizeY);
      }
   }
    /*
   public void drawMe(GraphicsContext gc, double cameraOffsetX, double cameraOffsetY) {
    // Draw at screen position (world position - camera offset)
      double screenX = x - cameraOffsetX;
      double screenY = y - cameraOffsetY;
      ticker++;
      gc.setFill(Color.rgb(173, 216, 230, 0.6));
      double[] xAr = {x, x + 30, x + 60, x + 60, x};
      double[] yAr = {y, y + 40, y, y + 50, y + 50};
      pulseSize = 1 + Math.sin(ticker / 30.0) * 0.2;
      gc.save();
      double centerX = x + 30;
      double centerY = y + 25;
      gc.translate(centerX, centerY);
      gc.rotate(rotation);
      gc.translate(-centerX, -centerY);
      gc.setFill(Color.rgb(173, 216, 230, 0.5 + Math.abs(Math.sin(ticker / 30.0)) * 0.4));
      gc.fillPolygon(xAr, yAr, 5);
      gc.setFill(Color.rgb(173, 216, 230, 0.8));
      for (int i = 0; i < 3; i++) {
         double offsetX = Math.sin(ticker / 20.0 + i) * 3;
         double offsetY = Math.cos(ticker / 20.0 + i) * 3;
         gc.fillPolygon(new double[]{x + offsetX, x + 30 + offsetX, x + 60 + offsetX, x + 60, x + offsetX},
                    new double[]{y + offsetY, y + 40 + offsetY, y + offsetY, y + 50 + offsetY, y + 50 + offsetY}, 5);
      }
      for (int i = 0; i <= ticker / 100; i++) {
         if (i % 10 == 0) {
            drawRays(gc, ticker / 100 - i);
         }
      }
      if (ticker % 500 == 0) {
         gc.setFill(Color.color(Math.random(), Math.random(), Math.random(), 0.8));
         gc.fillPolygon(xAr, yAr, 5);
      }
      gc.restore();
      // Draw bounds for debugging (can remove this in production)
      gc.setStroke(Color.RED);
      gc.strokeRect(screenX, screenY, sizeX, sizeY);
      // Display target system ID
      gc.setFill(Color.CYAN);
      gc.setFont(javafx.scene.text.Font.font(10));
      gc.fillText(
                "To System: " + targetSystem,
                screenX,
                screenY - 10
         );
   }


   private void drawRays(GraphicsContext gc, int size) {
      if (size < 60) {
         double r = Math.sin(ticker / 30.0 + size * 0.1) * 0.5 + 0.5;
         double g = Math.cos(ticker / 30.0 + size * 0.1) * 0.5 + 0.5;
         double b = Math.sin(ticker / 40.0 + size * 0.1) * 0.5 + 0.5;
         gc.setStroke(Color.color(r, g, b));
         gc.setLineWidth(2);
         gc.strokeLine(x + size, y, x + 30, y + 40 - size * 0.5);
         gc.strokeLine(x + 30, y + 40 - size * 0.5, x + 60 - size, y);
      }
   }

     */



   public void drawMe(GraphicsContext gc, double cameraOffsetX, double cameraOffsetY) {
      // Calculate screen position
      double screenX = x - cameraOffsetX;
      double screenY = y - cameraOffsetY;
      ticker++;

      gc.setFill(Color.rgb(173, 216, 230, 0.6));
      // Update polygon coordinates to use screen position
      double[] xAr = {screenX, screenX + 30, screenX + 60, screenX + 60, screenX};
      double[] yAr = {screenY, screenY + 40, screenY, screenY + 50, screenY + 50};

      pulseSize = 1 + Math.sin(ticker / 30.0) * 0.2;

      gc.save();
      // Update rotation center to use screen coordinates
      double centerX = screenX + 30;
      double centerY = screenY + 25;
      gc.translate(centerX, centerY);
      gc.rotate(rotation);
      gc.translate(-centerX, -centerY);

      gc.setFill(Color.rgb(173, 216, 230, 0.5 + Math.abs(Math.sin(ticker / 30.0)) * 0.4));
      gc.fillPolygon(xAr, yAr, 5);

      gc.setFill(Color.rgb(173, 216, 230, 0.8));
      for (int i = 0; i < 3; i++) {
         double offsetX = Math.sin(ticker / 20.0 + i) * 3;
         double offsetY = Math.cos(ticker / 20.0 + i) * 3;
         gc.fillPolygon(
                 new double[]{screenX + offsetX, screenX + 30 + offsetX, screenX + 60 + offsetX, screenX + 60, screenX + offsetX},
                 new double[]{screenY + offsetY, screenY + 40 + offsetY, screenY + offsetY, screenY + 50 + offsetY, screenY + 50 + offsetY},
                 5
         );
      }

      for (int i = 0; i <= ticker / 100; i++) {
         if (i % 10 == 0) {
            drawRays(gc, ticker / 100 - i, screenX, screenY);  // Pass screen coordinates to drawRays
         }
      }

      if (ticker % 500 == 0) {
         gc.setFill(Color.color(Math.random(), Math.random(), Math.random(), 0.8));
         gc.fillPolygon(xAr, yAr, 5);
      }

      gc.restore();

      // Debug rectangle (already using screen coordinates)
      gc.setStroke(Color.RED);
      gc.strokeRect(screenX, screenY, sizeX, sizeY);

      // Label (already using screen coordinates)
      gc.setFill(Color.CYAN);
      gc.setFont(javafx.scene.text.Font.font(10));
      gc.fillText(
              "To System: " + targetSystem,
              screenX,
              screenY - 10
      );
   }

   private void drawRays(GraphicsContext gc, int size, double screenX, double screenY) {
      if (size < 60) {
         double r = Math.sin(ticker / 30.0 + size * 0.1) * 0.5 + 0.5;
         double g = Math.cos(ticker / 30.0 + size * 0.1) * 0.5 + 0.5;
         double b = Math.sin(ticker / 40.0 + size * 0.1) * 0.5 + 0.5;
         gc.setStroke(Color.color(r, g, b));
         gc.setLineWidth(2);
         gc.strokeLine(screenX + size, screenY, screenX + 30, screenY + 40 - size * 0.5);
         gc.strokeLine(screenX + 30, screenY + 40 - size * 0.5, screenX + 60 - size, screenY);
      }
   }



   public boolean isCollidingWith(Player player) {
        // Create a temporary rectangle for the player's current position
      Rectangle playerRect = new Rectangle(
                player.getX(),
                player.getY(),
                Player.getBounds().getRadius() * 2,  // width is diameter
                Player.getBounds().getRadius() * 2   // height is diameter
         );
   
      return bounds.intersects(playerRect.getBoundsInLocal());
   }

   public Rectangle getBounds() {
      updateBounds(); // Ensure bounds are current
      return bounds;
   }

   public StarSystem getSystem() {
      return system;
   }
   
   /*public void activate(GraphicsContext gc){
   
      StarSystem newSys;
      
      //load new system
      if (StarSystemCache.get(targetSystem) == null){
         //create this new system.
         newSys = new StarSystem(gc, targetSystem);
         StarSystemCache.add( newSys );
         //GalaxyMap.getInstance().discoverChunkFromSystemCoords(systemX, systemY);
      }
      else{
         newSys = StarSystemCache.get(targetSystem);
      }
      Player.getInstance().setSystem(newSys);
      Player player = Player.getInstance();
      //bring platyer to new spawn point
      player.setX(0);
      player.setY(0);
      
   }*/
   
   /*public void activate(GraphicsContext gc) {
      StarSystem newSys;
    
    //load new system
      if (StarSystemCache.getInstance().get(targetSystem) == null) {
        //create this new system.
         newSys = new StarSystem(gc, targetSystem);
         StarSystemCache.getInstance().add(newSys);
         GalaxyMap.getInstance().discoverChunkFromSystemCoords(newSys.getxLoc(), newSys.getyLoc());
      } else {
         newSys = StarSystemCache.getInstance().get(targetSystem);
      }
    
      Player player = Player.getInstance();
      player.setSystem(newSys);
   
   
   
    // Try several spawn positions until we find one that doesn't collide
      double[][] spawnPoints = {
         {0, 0},      // Original spawn
         {200, 200},  // Alternative 1
         {400, 400},  // Alternative 2
         {600, 200},  // Alternative 3
         {200, 600}   // Alternative 4
         };
    
      boolean foundSafeSpot = false;
      for (double[] point : spawnPoints) {
         player.setX(point[0]);
         player.setY(point[1]);
        
         boolean isColliding = false;
         for (Planet planet : newSys.getPlanets()) {
            if (planet.isCollidingWith(player)) {
               isColliding = true;
               break;
            }
         }
        
         if (!isColliding) {
            foundSafeSpot = true;
            break;
         }
      }
    
    // If no safe spots found in our list, try to find one
      if (!foundSafeSpot) {
         for (int x = 0; x < 800; x += 100) {
            for (int y = 0; y < 600; y += 100) {
               player.setX(x);
               player.setY(y);
                
               boolean isColliding = false;
               for (Planet planet : newSys.getPlanets()) {
                  if (planet.isCollidingWith(player)) {
                     isColliding = true;
                     break;
                  }
               }
                
               if (!isColliding) {
                  foundSafeSpot = true;
                  break;
               }
            }
            if (foundSafeSpot) 
               break;
         }
      }
    
    // If we still haven't found a safe spot, move far away from center
      if (!foundSafeSpot) {
         player.setX(800);
         player.setY(800);
      }
   
   
   }*/
   
     public void activate(GraphicsContext gc) {
    StarSystem newSys;
    StarSystem oldSys = this.system;  // Store the original system
    
    // Load new system
    if (StarSystemCache.getInstance().get(targetSystem) == null) {
        // Create this new system
        newSys = new StarSystem(gc, targetSystem);
        StarSystemCache.getInstance().add(newSys);
        GalaxyMap.getInstance().discoverChunkFromSystemCoords(newSys.getxLoc(), newSys.getyLoc());
    } else {
        newSys = StarSystemCache.getInstance().get(targetSystem);
    }
    
    Player player = Player.getInstance();
    
    // Find the corresponding gate in the new system
    Gate destinationGate = null;
    List<Gate> systemGates = newSys.getGates();
    
    // Debug print
    System.out.println("Looking for gate connecting from system " + targetSystem + " back to system " + oldSys.getID());
    System.out.println("Number of gates in new system: " + systemGates.size());
    
    for (Gate gate : systemGates) {
        System.out.println("Checking gate with target: " + gate.targetSystem);
        if (gate.targetSystem == oldSys.getID()) {
            destinationGate = gate;
            System.out.println("Found matching gate!");
            break;
        }
    }
    
    if (destinationGate != null) {
        // Calculate spawn position in front of the destination gate
        double spawnOffsetDistance = 70; // Distance to spawn from the gate
        
        // Calculate spawn position based on gate's rotation
        double spawnX = destinationGate.x;
        double spawnY = destinationGate.y;
        
        System.out.println("Destination gate position: " + spawnX + ", " + spawnY);
        System.out.println("Gate rotation: " + destinationGate.rotation);
        
        // Adjust spawn position based on rotation
        switch (destinationGate.rotation) {
            case 0: // Gate facing right
                spawnX += spawnOffsetDistance;
                break;
            case 90: // Gate facing down
                spawnY += spawnOffsetDistance;
                break;
            case 180: // Gate facing left
                spawnX -= spawnOffsetDistance;
                break;
            case 270: // Gate facing up
                spawnY -= spawnOffsetDistance;
                break;
        }
        
        // Set player position
        player.setX(spawnX);
        player.setY(spawnY);
        System.out.println("Set player position to: " + spawnX + ", " + spawnY);
        
        // Check if spawn position is safe
        boolean isColliding = false;
        for (Planet planet : newSys.getPlanets()) {
            if (planet.isCollidingWith(player)) {
                isColliding = true;
                break;
            }
        }
        
        // If collision detected, try alternative positions
        if (isColliding) {
            System.out.println("Initial spawn position unsafe, trying alternatives");
            double[][] offsets = {
                {spawnOffsetDistance * 1.5, 0},
                {-spawnOffsetDistance * 1.5, 0},
                {0, spawnOffsetDistance * 1.5},
                {0, -spawnOffsetDistance * 1.5}
            };
            
            for (double[] offset : offsets) {
                player.setX(destinationGate.x + offset[0]);
                player.setY(destinationGate.y + offset[1]);
                
                isColliding = false;
                for (Planet planet : newSys.getPlanets()) {
                    if (planet.isCollidingWith(player)) {
                        isColliding = true;
                        break;
                    }
                }
                
                if (!isColliding) {
                    System.out.println("Found safe alternative position");
                    break;
                }
            }
            
            // If still no safe spot found, use the original fallback position
            if (isColliding) {
                System.out.println("No safe position found, using fallback");
                player.setX(800);
                player.setY(800);
            }
        }
    } else {
        System.out.println("No matching gate found in new system");
        // Fallback spawn position if destination gate not found
        player.setX(800);
        player.setY(800);
    }
    
    // Set the system after positioning the player
    player.setSystem(newSys);
}
   
}