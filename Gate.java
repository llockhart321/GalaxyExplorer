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
   // Direction variable to say which way the gate will point to using normal circular degrees
   private double direction;
   // Object that the gate leads to
   private int targetSystem;
   
   // which system the gate is in
   private int systemID;
   
   //location. dteremend from random;
   private double x, y;
  
   private double sizeX = 10;
   private double sizeY = 50;
   private Rectangle bounds;
   
   
   
   // Constructor for each gate
   public Gate (double direction, int targetSystem, double x, double y) {
      this.direction = direction;
      this.targetSystem = targetSystem;
      this.systemID = systemID;
      this.x = x;
      this.y = y;
      this.bounds = new Rectangle(this.x, this.y, sizeX, sizeY);
   }
   
   //get the id of the solar system the gate is in
   public int getSystemID(){
      return systemID;
   }
   
   // Method to draw the gate using the direciton variable
   public void drawMe(GraphicsContext gc, double cameraOffsetX, double cameraOffsetY) {
      // Enter drawing code here...
      
      gc.setFill(Color.PURPLE);
      gc.fillOval(x,y,sizeX,sizeY);
      
   }
   
   public Rectangle getBounds(){
      return this.bounds;
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