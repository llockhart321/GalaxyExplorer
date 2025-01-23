import javafx.animation.*;
import javafx.scene.canvas.*;
import javafx.scene.paint.*;

public class AnimationHandler extends AnimationTimer {
   // Private instance variables
   private GraphicsContext gc;
   private Canvas canvas;
   private int x = 0;
   private StarSystem system;
   
   public AnimationHandler(GraphicsContext gc) { 
      this.gc = gc;
   } 
   public void handle(long currentTimeInNanoSeconds) {
      system =  Player.getInstance().getSystem();
      // Clear the canvas each time
      gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
      system.drawMe(gc, Camera.getInstance().getCameraOffsetX(), Camera.getInstance().getCameraOffsetY());
      system.collisionCheck(gc);
      // Player draws itself
      Player.getInstance().drawMe(gc);
      // PlayerMovementState will check if the player has moved
      PlayerMovementState.getInstance().move();
      // Camera will check if the camera needs to pan based on the Player's position
      Camera.getInstance().checkPlayerPosition();
   }
}