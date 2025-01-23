// Camera class to handle as the player moves around the galaxy
public class Camera {
   double cameraOffsetX, cameraOffsetY;
   public Camera() {
      cameraOffsetX = 0;
      cameraOffsetY = 0;
   }
   public void setCameraOffsetX(double cameraOffsetX) { this.cameraOffsetX = cameraOffsetX; }
   public void setCameraOffsetY(double cameraOffsetY) { this.cameraOffsetY = cameraOffsetY; }
   public double getCameraOffsetX() { return cameraOffsetX; }
   public double getCameraOffsetY() { return cameraOffsetY; }
}