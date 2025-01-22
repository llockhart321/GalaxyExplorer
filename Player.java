import javafx.scene.shape.Rectangle;


// Player singleton class
public class Player {
   // Class instance
   private static Player instance;
   // Coordinates
   private double x, y;
   // for collision
   private static Rectangle bounds = new Rectangle(100, 100, 30, 30); 
   
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
      x = -99;
      y = -99;
   }
   public static Rectangle getBounds(){
      return bounds;
   }
   // Handle player movements
   public void moveLeftBy(double deltaX) { x += deltaX; }
   public void moveRightBy(double deltaX) { x -= deltaX; }
   public void moveDownBy(double deltaY) { y += deltaY; }
   public void moveUpBy(double deltaY) { y -= deltaY; }
   // Getters and Setters
   public double getX() { return x; }
   public double getY() { return y; }
   public void setX(double x) { this.x = x; }
   public void setY(double y) { this.y = y; }
}