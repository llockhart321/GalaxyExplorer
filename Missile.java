import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.geometry.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

public class Missile {
    private Point2D position;
    private Point2D velocity;
    private static final double SPEED = 10.0;
    private static final double RADIUS = 3.0;
    private boolean active = true;

    public Missile(double startX, double startY, double directionX, double directionY) {
        position = new Point2D(startX, startY);
        Point2D direction = new Point2D(directionX, directionY).normalize();
        velocity = direction.multiply(SPEED);
    }

    public void update() {
        position = position.add(velocity);
    }

    public void drawMe(GraphicsContext gc, double cameraOffsetX, double cameraOffsetY) {
        gc.setFill(Color.RED);
        gc.fillOval(
            position.getX() - RADIUS - cameraOffsetX,
            position.getY() - RADIUS - cameraOffsetY,
            RADIUS * 2,
            RADIUS * 2
        );
    }

    public Point2D getPosition() { return position; }
    public double getRadius() { return RADIUS; }
    public boolean isActive() { return active; }
    public void deactivate() { active = false; }
}