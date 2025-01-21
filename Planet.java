import javafx.scene.paint.*;
public class Planet {
   // Variable for the color of the planet
   private Color color;
   // Variables distance away from the central star and the normal-circle-degree position in relation to the central star
   private double distance, position;
   // Variables for the size of the planet and the speed at which it travels
   private int radius, speed;
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
      // Complicated calculations here
   }
   // Methods to return the x and y variabels relative to the position of the central star
   public double getRelativeX(double relativex0) {
      // Very complex math here ...
      return 0;
   }
   public double getRelativeY(double relativex0) {
      // Very complex math here ...
      return 0;
   }
   // Method to draw the planet
   public void drawMe() {
      // Draw code here ...
   }
}