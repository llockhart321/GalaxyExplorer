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