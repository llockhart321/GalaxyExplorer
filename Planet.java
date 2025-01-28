import javafx.scene.paint.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
public class Planet {
   // Variable for the color of the planet
   private Color color;
   // Variables distance away from the central star and the normal-circle-degree position in relation to the central star
   private double distance, position;
   // Variables for the size of the planet and the speed at which it travels
   private int radius, speed, size;

   //bounds for collision
   private Circle bounds;
   // Constructor
   public Planet (Color color, double distance, double position, int radius, int speed) {
      this.color = color;
      this.distance = distance;
      this.position = position;
      this.radius = radius;
      this.speed = speed;
       double x = getRelativeX(500) - radius; // Adjust for the planet's radius
       double y = getRelativeY(500) - radius; // Adjust for the planet's radius
       this.bounds = new Circle(x, y, radius);
       //x y rad

   }
   // Method to continually update the position
   private void updatePosition() {
       position += Math.toRadians(speed / distance); // Increment position by speed (converted to radians)
       if (position > 2 * Math.PI) {
           position -= 2 * Math.PI; // Ensure the position stays within 0 to 2π
       }

       // Update the bounds of the Circle
       double x = getRelativeX(500); // Calculate current center X
       double y = getRelativeY(500); // Calculate current center Y
       bounds.setCenterX(x); // Update the center X of the bounds
       bounds.setCenterY(y); // Update the center Y of the bounds
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
       updatePosition(); // Update the position before drawing

       double x = bounds.getCenterX() - radius; // Adjust for the planet's radius
       double y = bounds.getCenterY() - radius; // Adjust for the planet's radius

       // Draw the planet
       gc.setFill(color);
       gc.fillOval(x, y, radius * 2, radius * 2);

       // Draw the bounds for debugging purposes
       gc.setStroke(Color.BLACK);
       gc.strokeOval(bounds.getCenterX() - bounds.getRadius(), bounds.getCenterY() - bounds.getRadius(), bounds.getRadius() * 2, bounds.getRadius() * 2);
   }

    // Add these methods to your Planet class
   public boolean isCollidingWith(Player player) {

       if (Player.getBounds().getBoundsInParent().intersects(this.bounds.getBoundsInParent())) {
           return true;
       }
       else{
           return false;
       }

   }
   
   public void handleCollision(Player player) {
       // Get positions
       double playerCenterX = player.getX() + Player.getBounds().getWidth() / 2;
       double playerCenterY = player.getY() + Player.getBounds().getHeight() / 2;
       double planetCenterX = getRelativeX(0);
       double planetCenterY = getRelativeY(0);
       
       // Calculate vector from planet to player
       double dx = playerCenterX - planetCenterX;
       double dy = playerCenterY - planetCenterY;
       
       // Calculate current distance
       double currentDistance = Math.sqrt(dx * dx + dy * dy);
       
       // Calculate minimum allowed distance
       double minDistance = radius + (Player.getBounds().getWidth() / 2);
       
       if (currentDistance < minDistance && currentDistance > 0) {
           // Normalize the direction vector
           double nx = dx / currentDistance;
           double ny = dy / currentDistance;
           
           // Calculate how far to push the player out
           double pushDistance = minDistance - currentDistance;
           
           // Set player's new position to be exactly at the minimum distance
           double newX = player.getX() + (nx * pushDistance);
           double newY = player.getY() + (ny * pushDistance);
           
           // Update player position
           player.setX(newX);
           player.setY(newY);
           
           // Stop movement in collision direction
           player.setLeftright(0);
           player.setUpdown(0);
       }
   }
}