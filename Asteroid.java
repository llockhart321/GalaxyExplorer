import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;

public class Asteroid {
    private Color color;
    private double distance, position;
    private int radius;
    private int speed = 100;

    public Asteroid(double distance, double position, int radius, int speed) {
        this.color = Color.BURLYWOOD; // Asteroids are brown
        this.distance = distance;
        this.position = position;
        this.radius = radius;
    }

    private void updatePosition() {
        position += Math.toRadians(speed / distance); // Increment position by speed (converted to radians)
        if (position > 2 * Math.PI) {
            position -= 2 * Math.PI; // Ensure the position stays within 0 to 2π
        }
    }

    public double getRelativeX() {
        return 200 + distance * Math.cos(position); // Calculate X coordinate
    }

    public double getRelativeY() {
        return 200 + distance * Math.sin(position); // Calculate Y coordinate
    }

    public void drawMe(GraphicsContext gc, double cameraOffsetX, double cameraOffsetY) {
        updatePosition();
        double x = getRelativeX() - radius; // Adjust for the asteroid's radius
        double y = getRelativeY() - radius; // Adjust for the asteroid's radius

        gc.setFill(color); // Set the fill color
        gc.fillOval(x, y, radius * 2, radius * 2); // Draw the asteroid as a circle
    }
}