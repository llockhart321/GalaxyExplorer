import javafx.animation.*;
import javafx.scene.canvas.*;
import javafx.scene.paint.*;

public class AnimationHandler extends AnimationTimer {
   private GraphicsContext gc;
   private int x = 0;
   public AnimationHandler(GraphicsContext gc) { this.gc = gc; }
   public void handle(long currentTimeInNanoSeconds) {
      gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
      gc.setFill(Color.WHITE);
      gc.fillRect(x, 50, 50, 50);
      x++;
   }
}