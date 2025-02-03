import javafx.scene.shape.*;
import javafx.scene.canvas.*;
import javafx.scene.paint.*;

// Player singleton class
public class Player {
   // Class instance
   private static Player instance;
   private static StarSystem currentSystem;
   // Coordinates
   private double x, y;
   // Size
   private static double radius = 20;
   // For collision
   private static Circle bounds = new Circle(radius);
   // Ints for player movement for Camera
   private int leftright = 0;
   private int updown = 0;

   // Public method to get private instance
   public static Player getInstance() {
      // Ensure instance is instantiated
      if (instance == null) {
         instance = new Player();
      }
      // Return instance
      return instance;
   }

   // Private constructor
   private Player() {
      // Set player on screen
      x = 380;
      y = 220;
      bounds.setCenterX(x + radius);
      bounds.setCenterY(y + radius);
   }

   public static Circle getBounds() {
      return bounds;
   }

   // Handle player movements
   public void moveXBy(double deltaX) {
      x += deltaX;
      bounds.setCenterX(x + radius);
   }

   public void moveYBy(double deltaY) {
      y += deltaY;
      bounds.setCenterY(y + radius);
   }

   // Getters and Setters
   public double getX() {
      return x;
   }
   public double getY() {
      return y;
   }
   public void setX(double x) {
      this.x = x;
      bounds.setCenterX(x + radius);
   }
   public void setY(double y) {
      this.y = y;
      bounds.setCenterY(y + radius);
   }

   // Set and get player movements
   public void setLeftright(int leftright) { this.leftright = leftright; }
   public void setUpdown(int updown) { this.updown = updown; }
   public boolean isMovingRight() {
      return leftright == 1;
   }
   public boolean isMovingLeft() {
      return leftright == -1;
   }
   public boolean isMovingUp() {
      return updown == -1;
   }
   public boolean isMovingDown() {
      return updown == 1;
   }

   public void drawMe(GraphicsContext gc, double cameraOffsetX, double cameraOffsetY) {
      gc.setFill(Color.LIGHTBLUE);
      gc.fillOval(x - cameraOffsetX, y - cameraOffsetY, radius * 2, radius * 2);

      gc.setStroke(Color.BLACK);
      gc.strokeOval(bounds.getCenterX() - radius - cameraOffsetX,
              bounds.getCenterY() - radius - cameraOffsetY,
              radius * 2, radius * 2);
   }

   public StarSystem getSystem() {
      return currentSystem;
   }
   public void setSystem(StarSystem sys) {
      currentSystem = sys;
      GalaxyMap.getInstance().setCurrentSystem(currentSystem.getID());
   }
}
