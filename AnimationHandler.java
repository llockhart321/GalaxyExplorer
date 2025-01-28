import javafx.animation.*;
import javafx.scene.canvas.*;
import javafx.scene.paint.*;

public class AnimationHandler extends AnimationTimer {
   // Private instance variables
   private GraphicsContext gc;
   private Canvas canvas;
   private int x = 0;
   private StarSystem system;
   private Camera c;
   
   public AnimationHandler(GraphicsContext gc) { 
      this.gc = gc;
   } 
   public void handle(long currentTimeInNanoSeconds) {
      c = Camera.getInstance(system);
      system =  Player.getInstance().getSystem();
      // Clear the canvas each time
      gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
      
      // Check for planet collisions before moving the player
      system.checkPlayerPlanetCollisions(Player.getInstance());
      system.drawMe(gc);
      system.collisionCheck(gc);
      //System.out.println(system+" " +Player.getInstance());

      
      //missiles update
      MissileSystem.getInstance().update(gc, c.getMapCenterOffsetX(), c.getMapCenterOffsetY());
      // PlayerMovementState will check if the player has moved
      PlayerMovementState.getInstance().move();
      // Update camera
      c.update();
   }
   
}