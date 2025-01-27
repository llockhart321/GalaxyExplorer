// Signleton Camera class to handle as the player moves around the galaxy
public class Camera {
   // doubles to handle how much from the initial start the camera will need to draw
   double cameraOffsetX, cameraOffsetY;
   // Instance
   private static Camera instance;
   // Constructor will need to start at 0, 0 for each StarSystem
   public static Camera getInstance() {
      if (instance == null) {
         instance = new Camera();
      }
      return instance;
   }
   private Camera() {
      cameraOffsetX = 0;
      cameraOffsetY = 0;
   }
   // Getters and Setters for the camera offset variables
   public void setCameraOffsetX(double cameraOffsetX) { this.cameraOffsetX = cameraOffsetX; }
   public void setCameraOffsetY(double cameraOffsetY) { this.cameraOffsetY = cameraOffsetY; }
   public double getCameraOffsetX() { return cameraOffsetX; }
   public double getCameraOffsetY() { return cameraOffsetY; }
   // Method called every tick to check if the camera needs to pan from last tick
   public void checkPlayerPosition() {
      if (Player.getInstance().getX() + cameraOffsetX <= 270 && Player.getInstance().isMovingLeft()) {
         System.out.println("Pan left");
         double playerRelativeX = Player.getInstance().getX() - 270;
      }
      if (Player.getInstance().getX() + cameraOffsetX >= 530 && Player.getInstance().isMovingRight()) {
         System.out.println("Pan right");
         double playerRelativeX = Player.getInstance().getX() - 530;
      }
      if (Player.getInstance().getY() + cameraOffsetY <= 150 && Player.getInstance().isMovingUp()) {
         System.out.println("Pan up");
         double playerRelativeY = Player.getInstance().getY() - 150;
      }
      if (Player.getInstance().getY() + cameraOffsetY >= 300 && Player.getInstance().isMovingDown()) {
         System.out.println("Pan down");
         double playerRelativeY = Player.getInstance().getY() - 300;
      }
   }
   // Method to reset the camera to 0 when a new level is started
   public void reset() {
      cameraOffsetX = 0;
      cameraOffsetY = 0;
   }
}