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
   public void checkPlayerPosition(double playerX, double playerY) {
      if (playerX + cameraOffsetX <= 270) {
         System.out.println("Pan left");
         while (playerX + cameraOffsetX <= 270) {
         }
      }
      if (playerX + cameraOffsetX >= 530) {
         while (playerX + cameraOffsetX >= 530) {
         }
      }
      if (playerY + cameraOffsetY <= 150) {
         System.out.println("Pan up");
         while (playerY + cameraOffsetY <= 150) {
         }
      }
      if (playerY + cameraOffsetY >= 300) {
         System.out.println("Pan down");
         while (playerY + cameraOffsetY >= 300) {
         }
      }
   }
   // Method to reset the camera to 0 when a new level is started
   public void reset() {
      cameraOffsetX = 0;
      cameraOffsetY = 0;
   }
}