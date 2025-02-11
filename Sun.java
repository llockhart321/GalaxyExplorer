import javafx.scene.paint.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Circle;
import javafx.scene.effect.BlendMode;

public class Sun {
    public static final double WORLD_CENTER_X = 2000;
    public static final double WORLD_CENTER_Y = 1125;
    
    private final Color centerColor = Color.WHITE;
    private final Color[] gradientColors = {
        Color.rgb(255, 51, 153, 0.8),  // Hot pink
        Color.rgb(255, 0, 128, 0.6),   // Magenta
        Color.rgb(255, 0, 255, 0.4)    // Purple
    };
    private int radius;
    private Circle bounds;
    private double pulsePhase = 0;
    
    public Sun() {
        this.radius = 300;
        this.bounds = new Circle(WORLD_CENTER_X, WORLD_CENTER_Y, radius);
    }
    
    public void handleCollision(Player player) {
        // Get centers of both objects
        Circle playerBounds = Player.getBounds();
        double playerCenterX = player.getX() + playerBounds.getRadius();
        double playerCenterY = player.getY() + playerBounds.getRadius();

        // Calculate vector from planet to player
        double dx = playerCenterX - bounds.getCenterX();
        double dy = playerCenterY - bounds.getCenterY();

        // Calculate current distance
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance == 0) {
            // If centers are exactly the same, push player right
            player.setX(player.getX() + playerBounds.getRadius() + bounds.getRadius());
            return;
        }

        // Calculate minimum allowed distance
        double minDistance = bounds.getRadius() + playerBounds.getRadius();

        if (distance < minDistance) {
            // Normalize the direction vector
            double nx = dx / distance;
            double ny = dy / distance;

            // Calculate how far to push the player out
            double pushDistance = minDistance - distance;

            // Set player's new position to be exactly at the minimum distance
            player.setX(player.getX() + (nx * pushDistance));
            player.setY(player.getY() + (ny * pushDistance));

            // Reset player movement state to prevent sticking
            PlayerMovementState.getInstance().stopLeft();
            PlayerMovementState.getInstance().stopRight();
            PlayerMovementState.getInstance().stopUp();
            PlayerMovementState.getInstance().stopDown();
        }
    }

    
    public void drawMe(GraphicsContext gc, double cameraOffsetX, double cameraOffsetY) {
        double screenX = bounds.getCenterX() - radius - cameraOffsetX;
        double screenY = bounds.getCenterY() - radius - cameraOffsetY;
        
        // Pulsing effect
        pulsePhase += 0.05;
        double pulseFactor = 1.0 + Math.sin(pulsePhase) * 0.1;
        
        // Draw outer glows
        for (int i = 3; i >= 0; i--) {
            double glowRadius = radius * (1 + i * 0.2) * pulseFactor;
            gc.setFill(gradientColors[i % gradientColors.length]);
            gc.fillOval(
                screenX - (glowRadius - radius),
                screenY - (glowRadius - radius),
                glowRadius * 2,
                glowRadius * 2
            );
        }
        
        // Draw core
        gc.setFill(centerColor);
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