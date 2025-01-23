
import javafx.scene.shape.Rectangle;
import javafx.scene.canvas.*;
import javafx.scene.paint. *;

// Player singleton class
public class Player {
   // Class instance
   private static Player instance;
   
   private static StarSystem currentSystem;
   // Coordinates
   private double x, y;
   //size
   private static double sizeX = 40;
   private static double sizeY = 40;
   // for collision
   private static Rectangle bounds = new Rectangle(0, 0, sizeX, sizeY);
   
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
      x = 480;
      y = 200;
      bounds.setX(x);
      bounds.setY(y);
      
   }
   public static Rectangle getBounds(){
      return bounds;
   }
   // Handle player movements
   public void moveXBy(double deltaX) { x += deltaX; this.bounds.setX(x);}
   public void moveYBy(double deltaY) { y += deltaY; this.bounds.setY(y);}
   // Getters and Setters
   public double getX() { return x; }
   public double getY() { return y; }
   public void setX(double x) { 
      this.x = x;
      this.bounds.setX(x);
   }
   public void setY(double y) { 
      this.y = y; 
      this.bounds.setY(y);      
   }
   
   public void drawMe(GraphicsContext gc) {
      gc.setFill(Color.LIGHTBLUE);
      gc.fillOval(x, y, sizeX, sizeY);
   }
   
   public StarSystem getSystem(){
      return currentSystem;
   }
   public void setSystem(StarSystem sys){
      currentSystem = sys;
   }
}