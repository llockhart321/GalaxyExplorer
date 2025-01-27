// Singleton camera that holds the map (system) and player
public class Camera {
   // Private variables for isntance, map, and player
   private static Camera instance;
   private StarSystem ss;
   private Player p;
   // Important location variables
   private double mapCenterOffsetX, mapCenterOffsetY, playerX, playerY;
   private int leftBound = 200;
   private int rightBound = 600;
   private int upBound = 120;
   private int downBound = 330;
   // Public getInstance that takes in the current map
   public static Camera getInstance(StarSystem ss) {
      if (instance == null) {
         instance = new Camera(ss);
      }
      return instance;
   }
   // Private constructor with public map passed through and getting Player instance
   private Camera(StarSystem ss) {
      this.ss = ss;
      p = Player.getInstance();
   }
   public void update() {
      playerX = p.getX();
      playerY = p.getY();
      if (playerX > rightBound && p.isMovingRight()) {
         mapCenterOffsetX = playerX - rightBound;
      } else if (playerX < leftBound && p.isMovingLeft()) {
         mapCenterOffsetX = playerX - leftBound;
      }
      if (playerY > downBound && p.isMovingDown()) {
         mapCenterOffsetY = playerY - downBound;
      } else if (playerY < upBound && p.isMovingUp()) {
         mapCenterOffsetY = playerY - upBound;
      }
   }
   public double getMapCenterOffsetX() {
      return mapCenterOffsetX;
   }

   public double getMapCenterOffsetY() {
      return mapCenterOffsetY;
   }
}