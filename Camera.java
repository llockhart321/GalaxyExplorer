import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Camera {
   private static Camera instance;
   private StarSystem ss;
   private Player p;

   // World bounds (accounting for player circle radius)
   //private final int WORLD_WIDTH = 4000;
   //private final int WORLD_HEIGHT = 2250;

   // Screen bounds - accounting for player radius for smoother movement
   private final int LEFT_BOUND = 200;
   private final int RIGHT_BOUND = 600;
   private final int UP_BOUND = 80;
   private final int DOWN_BOUND = 370;

   // Camera position
   private double mapCenterOffsetX = 0;
   private double mapCenterOffsetY = 0;
  // private VisualEffects effects = VisualEffects.getInstance();


   public static Camera getInstance(StarSystem ss) {
      if (instance == null) {
         instance = new Camera(ss);
      }
      instance.ss = ss;
      return instance;
   }

   private Camera(StarSystem ss) {
      this.ss = ss;
      p = Player.getInstance();
   }

   public void update(GraphicsContext gc) {
      double playerCenterX = p.getX() + Player.getBounds().getRadius();
      double playerCenterY = p.getY() + Player.getBounds().getRadius();

      // Prevent player from leaving world bounds
      double playerRadius = Player.getBounds().getRadius();
      //double maxPlayerX = WORLD_WIDTH - (playerRadius * 2);
      //double maxPlayerY = WORLD_HEIGHT - (playerRadius * 2);

      //double[] effectOffsets = effects.getEffectOffsets(playerCenterX, playerCenterY, WORLD_WIDTH, WORLD_HEIGHT);
      //mapCenterOffsetX += effectOffsets[0];
      //amapCenterOffsetY += effectOffsets[1];




      // Update camera based on player position
      double playerX = p.getX();
      double playerY = p.getY();

      // Update X offset, accounting for player radius in boundary checks
      if ((playerX + playerRadius * 2) - mapCenterOffsetX > RIGHT_BOUND) {
         mapCenterOffsetX = (playerX + playerRadius * 2) - RIGHT_BOUND;
      } else if (playerX - mapCenterOffsetX < LEFT_BOUND) {
         mapCenterOffsetX = playerX - LEFT_BOUND;
      }

      // Update Y offset, accounting for player radius in boundary checks
      if ((playerY + playerRadius * 2) - mapCenterOffsetY > DOWN_BOUND) {
         mapCenterOffsetY = (playerY + playerRadius * 2) - DOWN_BOUND;
      } else if (playerY - mapCenterOffsetY < UP_BOUND) {
         mapCenterOffsetY = playerY - UP_BOUND;
      }

      // Clamp camera position
      //mapCenterOffsetX = clamp(mapCenterOffsetX, 0, WORLD_WIDTH - 800);
      //mapCenterOffsetY = clamp(mapCenterOffsetY, 0, WORLD_HEIGHT - 450);


      //gc.setStroke(Color.RED);
      //int width = RIGHT_BOUND - LEFT_BOUND;
      //int height = DOWN_BOUND - UP_BOUND;

      //gc.strokeRect(LEFT_BOUND, UP_BOUND, width, height);
   }

   private double clamp(double value, double min, double max) {
      return Math.max(min, Math.min(max, value));
   }

   public double getMapCenterOffsetX() {
      return mapCenterOffsetX;
   }

   public double getMapCenterOffsetY() {
      return mapCenterOffsetY;
   }
}