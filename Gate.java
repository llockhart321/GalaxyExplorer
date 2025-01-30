import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
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

    public Gate(double direction, int targetSystem, double x, double y, StarSystem system) {
        this.direction = direction;
        this.targetSystem = targetSystem;
        this.system = system;
        this.x = x;
        this.y = y;
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

    public void drawMe(GraphicsContext gc, double cameraOffsetX, double cameraOffsetY) {
        // Draw at screen position (world position - camera offset)
        double screenX = x - cameraOffsetX;
        double screenY = y - cameraOffsetY;

        // Draw the gate
        gc.setFill(Color.PURPLE);
        gc.fillOval(screenX, screenY, sizeX, sizeY);

        // Draw bounds for debugging (can remove this in production)
        gc.setStroke(Color.RED);
        gc.strokeRect(screenX, screenY, sizeX, sizeY);
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
   
   public void activate(GraphicsContext gc) {
    StarSystem newSys;
    
    //load new system
    if (StarSystemCache.get(targetSystem) == null) {
        //create this new system.
        newSys = new StarSystem(gc, targetSystem);
        StarSystemCache.add(newSys);
        GalaxyMap.getInstance().discoverChunkFromSystemCoords(newSys.getxLoc(), newSys.getyLoc());
    } else {
        newSys = StarSystemCache.get(targetSystem);
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
            if (foundSafeSpot) break;
        }
    }
    
    // If we still haven't found a safe spot, move far away from center
    if (!foundSafeSpot) {
        player.setX(800);
        player.setY(800);
    }


}

   
}