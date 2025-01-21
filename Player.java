// Player singleton class
public class Player {
   // Class instance
   private Player instance;
   // Coordinates
   private double x, y;
   // Public method to get private instance
   public Player getInstance() {
      // Ensure instance is instantiated
      if (instance == null) {
         instance = new Player();
      }
      // Return instance
      return instance;
   }
   // Private constructor
   private Player() {
      instance = new Player();
      // Set player off screen
      x = -99;
      y = -99;
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