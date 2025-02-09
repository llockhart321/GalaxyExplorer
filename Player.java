import javafx.scene.shape.*;
import javafx.scene.canvas.*;
import javafx.scene.paint.*;
import javafx.scene.effect.*;

public class Player {
    private static Player instance;
    private static StarSystem currentSystem;
    private double x, y;
    private static double radius = 20;
    private static Circle bounds = new Circle(radius);
    private int leftright = 0;
    private int updown = 0;
    private Color primaryColor = Color.rgb(255, 51, 153);  // Hot pink
    private Color glowColor = Color.rgb(0, 255, 255, 0.5); // Cyan glow
    
    public static Player getInstance() {
        if (instance == null) instance = new Player();
        return instance;
    }
    
    private Player() {
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
        double drawX = x - cameraOffsetX;
        double drawY = y - cameraOffsetY;
        
        // Draw glow effect
        gc.setFill(glowColor);
        gc.fillOval(drawX - 2, drawY - 2, radius * 2 + 4, radius * 2 + 4);
        
        // Draw main ship
        gc.setFill(primaryColor);
        gc.fillOval(drawX, drawY, radius * 2, radius * 2);
        
        // Draw geometric details
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokePolygon(
            new double[]{drawX + radius, drawX + radius * 2, drawX},
            new double[]{drawY, drawY + radius, drawY + radius},
            3
        );
    }

   public StarSystem getSystem() {
      return currentSystem;
   }
   public void setSystem(StarSystem sys) {
      currentSystem = sys;
      GalaxyMap.getInstance().setCurrentSystem(currentSystem.getID());

      // gate stops player, maybe we can add a slight movement in out direction
      PlayerMovementState.getInstance().stop();
   }


}
