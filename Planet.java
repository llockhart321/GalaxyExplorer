import javafx.scene.paint.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
public class Planet {
   // Variable for the color of the planet
   private Color color;
   // Variables distance away from the central star and the normal-circle-degree position in relation to the central star
   private double distance, position;
   // Variables for the size of the planet and the speed at which it travels
   private int radius, speed, size;
   // Constructor
   public Planet (Color color, double distance, double position, int radius, int speed) {
      this.color = color;
      this.distance = distance;
      this.position = position;
      this.radius = radius;
      this.speed = speed;
   }
   // Method to continually update the position
   private void updatePosition() {
      position += Math.toRadians(speed/distance); // Increment position by speed (converted to radians)
        if (position > 2 * Math.PI) {
            position -= 2 * Math.PI; // Ensure the position stays within 0 to 2π
        }
   }
   // Methods to return the x and y variabels relative to the position of the central star
   public double getRelativeX(double relativex0) {
      return 200 + distance * Math.cos(position); // Calculate X coordinate
   }
   
   public double getRelativeY(double relativex0) {
      return 200 + distance * Math.sin(position); // Calculate Y coordinate
   }
   
   // Method to draw the planet
   public void drawMe(GraphicsContext gc, double playerOffsetX, double cameraOffsetY) {
      updatePosition();
      double x = getRelativeX(500) - radius; // Adjust for the planet's radius
      double y = getRelativeY(500) - radius; // Adjust for the planet's radius

      gc.setFill(color); // Set the fill color
      gc.fillOval(x, y, radius * 2, radius * 2); // Draw the planet as a circle
      
   }
}