import javafx.animation.*;
import javafx.scene.canvas.*;
import javafx.scene.paint.*;
import javafx.scene.text.Font;

public class AnimationHandler extends AnimationTimer {
   // Private instance variables
   private GraphicsContext gc;
   private Canvas canvas;
   private int x = 0;
   private StarSystem system;
   private Camera c;
   private final long startTime; // Store when the handler starts


   public AnimationHandler(GraphicsContext gc) { 
      this.gc = gc;
      this.gc = gc;
      this.startTime = System.nanoTime(); // Capture start time
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
      c.update(gc);

      if (currentTimeInNanoSeconds - startTime < 4_000_000_000L) {

         gc.setFont(new Font("Arial", 10));


         gc.setFill(Color.web("ff4f00"));

         gc.fillText("POINT AND CLICK TO SHOOT\nPRESS M TO ACTIVATE MAP\nPRESS N TO ACTIVATE MINIMAP", 5, 420);
      }

   }
   
}