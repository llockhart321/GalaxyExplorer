import javafx.scene.canvas.*;
import javafx.scene.paint. *;
// Player singleton class
public class Player {
   // Class instance
   private static Player instance;
   // Coordinates
   private double x, y;
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
      // Set player off screen
      x = 40;
      y = 40;
   }
   // Handle player movements
   public void moveXBy(double deltaX) { x += deltaX; }
   public void moveYBy(double deltaY) { y += deltaY; }
   // Getters and Setters
   public double getX() { return x; }
   public double getY() { return y; }
   public void setX(double x) { this.x = x; }
   public void setY(double y) { this.y = y; }
   
   public void drawMe(GraphicsContext gc) {
      gc.setFill(Color.LIGHTBLUE);
      gc.fillOval(x, y, 40, 40);
   }
}