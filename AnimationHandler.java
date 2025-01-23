import javafx.animation.*;
import javafx.scene.canvas.*;
import javafx.scene.paint.*;

public class AnimationHandler extends AnimationTimer {
   private GraphicsContext gc;
   private Canvas canvas;
   private int x = 0;
   private StarSystem system;
   private Camera c;
   
   public AnimationHandler(GraphicsContext gc) { 
      this.gc = gc;
      c = new Camera();
     
   }
   public void handle(long currentTimeInNanoSeconds) {
      system =  Player.getInstance().getSystem();
      gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
      system.drawMe(gc, c.getCameraOffsetX(), c.getCameraOffsetY());
      system.collisionCheck(gc);
      Player.getInstance().drawMe(gc);
      PlayerMovementState.getInstance().move();
   }
}