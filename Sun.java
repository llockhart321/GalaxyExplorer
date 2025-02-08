import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Circle;

public class Sun {
    public static final double WORLD_CENTER_X = 2000; // Center of 4000 wide world
    public static final double WORLD_CENTER_Y = 1125; // Center of 2250 tall world
    
    private Color color;
    private int radius;
    private Circle bounds;

    public Sun() {
        this.color = Color.YELLOW;
        this.radius = 300; // Adjust size as needed
        
        // Create bounds at the center of the world
        this.bounds = new Circle(WORLD_CENTER_X, WORLD_CENTER_Y, radius);
    }

    public void drawMe(GraphicsContext gc, double cameraOffsetX, double cameraOffsetY) {
        // Calculate screen position
        double screenX = bounds.getCenterX() - radius - cameraOffsetX;
        double screenY = bounds.getCenterY() - radius - cameraOffsetY;

        // Draw the sun
        gc.setFill(color);
        gc.fillOval(screenX, screenY, radius * 2, radius * 2);
    }

    public double getCenterX() {
        return bounds.getCenterX();
    }

    public double getCenterY() {
        return bounds.getCenterY();
    }

    public int getRadius() {
        return radius;
    }
}