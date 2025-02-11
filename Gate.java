import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.List;

import javafx.scene.transform.Rotate;
import javafx.stage.*;
import java.util.Random;





public class Gate {
   private int direction;
   private int targetSystem;
   private int system;
   private double x, y;
   private double sizeX = 60;
   private double sizeY = 15;
   private Rectangle activateBounds;
   private Rectangle bottomBounds;
   private Rectangle leftBounds;
   private Rectangle rightBounds;

   private int ticker = 0;
   private double pulseSize = 1;


   public Gate(int direction, int targetSystem, double x, double y, int system) {
      this.direction = direction;
      this.targetSystem = targetSystem;
      this.system = system;
      this.x = x;
      this.y = y;

      updateBounds();
   }

   private void updateBounds() {
        // Update rectangle bounds to match current position
      if (activateBounds == null) {
          activateBounds = new Rectangle(x, y+30, sizeX, sizeY);
          activateBounds = rotateRectangle(activateBounds, direction);
      } else {
          activateBounds.setX(x);
          activateBounds.setY(y+30);
          activateBounds.setWidth(sizeX);
          activateBounds.setHeight(sizeY);
          activateBounds = rotateRectangle(activateBounds, direction);
      }
       if (bottomBounds == null) {
           bottomBounds = new Rectangle(x, y+40, sizeX, sizeY-10);
           bottomBounds = rotateRectangle(bottomBounds, direction);
       } else {
           bottomBounds.setX(x);
           bottomBounds.setY(y+40);
           bottomBounds.setWidth(sizeX);
           bottomBounds.setHeight(sizeY);
           bottomBounds = rotateRectangle(bottomBounds, direction);
       }

   }

    public static Rectangle rotateRectangle(Rectangle rect, double angle) {
        // Calculate the center of the rectangle
        double centerX = rect.getX() + rect.getWidth() / 2;
        double centerY = rect.getY() + rect.getHeight() / 2;

        // Rotate the rectangle by the given angle around its center
        Rotate rotate = new Rotate(angle, centerX, centerY);
        rect.getTransforms().add(rotate);

        return rect;
    }


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
      gc.rotate(direction);
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
   
      return activateBounds.intersects(playerRect.getBoundsInLocal());
   }

   public Rectangle getBounds() {
      updateBounds(); // Ensure bounds are current
      return activateBounds;
   }

   public int getSystem() {
      return system;
   }
   

   
    /*public void activate(GraphicsContext gc) {
        StarSystem newSys;
        int oldSys = this.system;  // Store the original system

        // Load new system
        if (StarSystemCache.getInstance().get(targetSystem) == null) {
            // Create this new system
            //newSys = new StarSystem(gc, targetSystem);
            //StarSystemCache.getInstance().add(newSys);
            //GalaxyMap.getInstance().discoverChunkFromSystemCoords(newSys.getxLoc(), newSys.getyLoc());
            //GalaxyMap.getInstance().createChunk();
        } else {
            newSys = StarSystemCache.getInstance().get(targetSystem);
        }

        //skip the if bc thats not how we do it now
        newSys = StarSystemCache.getInstance().get(targetSystem);


            Player player = Player.getInstance();

        // Find the corresponding gate in the new system
        Gate destinationGate = null;
        List<Gate> systemGates = newSys.getGates();

        // Debug print
        //System.out.println("Looking for gate connecting from system " + targetSystem + " back to system " + oldSys);
        //System.out.println("Number of gates in new system: " + systemGates.size());

        for (Gate gate : systemGates) {
            //System.out.println("Checking gate with target: " + gate.targetSystem);
            if (gate.targetSystem == oldSys) {
                destinationGate = gate;
                //System.out.println("Found matching gate!");
                break;
            }
        }

        if (destinationGate != null) {
            // Calculate spawn position in front of the destination gate
            double spawnOffsetDistance = 100; // Distance to spawn from the gate

            // Calculate spawn position based on gate's rotation
            double spawnX = destinationGate.x;
            double spawnY = destinationGate.y;

            //System.out.println("Destination gate position: " + spawnX + ", " + spawnY);
            //System.out.println("Gate rotation: " + destinationGate.direction);

            // Adjust spawn position based on rotation
            switch (destinationGate.direction) {
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
            //System.out.println("Set player position to: " + spawnX + ", " + spawnY);

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
    }*/
    
    public void activate(GraphicsContext gc) {
    StarSystem newSys;
    int oldSys = this.system;

    // Get the new system from cache
    newSys = StarSystemCache.getInstance().get(targetSystem);
    Player player = Player.getInstance();

    // Find the corresponding gate in the new system
    Gate destinationGate = null;
    List<Gate> systemGates = newSys.getGates();

    for (Gate gate : systemGates) {
        if (gate.targetSystem == oldSys) {
            destinationGate = gate;
            break;
        }
    }

    if (destinationGate != null) {
        // Calculate spawn position in front of the destination gate
        double spawnOffsetDistance = 60; // Increased distance to prevent immediate re-triggering
        double spawnX = destinationGate.x;
        double spawnY = destinationGate.y;

        // Convert rotation to radians for precise positioning
        double angleInRadians = Math.toRadians(destinationGate.direction);
        
        // Calculate offset using trigonometry for more precise positioning
        spawnX += spawnOffsetDistance * Math.cos(angleInRadians);
        spawnY += spawnOffsetDistance * Math.sin(angleInRadians);

        // Set initial player position
        double radians = Math.toRadians(direction);
        double dx = Math.cos(radians) * 5;
        double dy = Math.sin(radians) * 5;
        
        player.setX(spawnX+=dx);
        player.setY(spawnY+=dy);

        // Check if spawn position is safe
        boolean isColliding = false;
        for (Planet planet : newSys.getPlanets()) {
            if (planet.isCollidingWith(player)) {
                isColliding = true;
                break;
            }
        }

        // If collision detected, try alternative positions with increasing distances
        if (isColliding) {
            //System.out.println("Initial spawn position unsafe, trying alternatives");
            double[] distances = {150, 200, 250}; // Try increasingly larger distances
            
            for (double distance : distances) {
                spawnX = destinationGate.x + (distance * Math.cos(angleInRadians));
                spawnY = destinationGate.y + (distance * Math.sin(angleInRadians));
                
                player.setX(spawnX);
                player.setY(spawnY);

                isColliding = false;
                for (Planet planet : newSys.getPlanets()) {
                    if (planet.isCollidingWith(player)) {
                        isColliding = true;
                        break;
                    }
                }

                if (!isColliding) {
                    //System.out.println("Found safe position at distance: " + distance);
                    break;
                }
            }

            // If still no safe spot found, use a fallback position
            if (isColliding) {
                //System.out.println("No safe position found, using fallback");
                player.setX(800);
                player.setY(800);
            }
        }
    } else {
        //System.out.println("No matching gate found in new system");
        player.setX(800);
        player.setY(800);
    }

    // Set the system after positioning the player
    player.setSystem(newSys);
}

    public int getTargetSystem(){
        return targetSystem;
    }


   
}