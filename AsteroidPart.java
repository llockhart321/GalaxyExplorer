import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;
import javafx.geometry.Point2D;
import java.util.*;
import javafx.scene.shape.Circle;


class AsteroidPart {
    private Point2D position;
    private Point2D velocity;
    private double radius;
    private Color color;
    private int groupId;
    private Circle bounds;
    


    public AsteroidPart(Point2D position, double radius, int groupId) {
        this.position = position;
        this.velocity = new Point2D(0, 0);
        this.radius = radius;
        this.color = Color.BURLYWOOD;
        this.groupId = groupId;
        bounds = new Circle(getPosition().getX(), getPosition().getY(), radius);

    }

    public Point2D getPosition() { return position; }
    public void setPosition(Point2D position) { this.position = position; }
    public Point2D getVelocity() { return velocity; }
    public void setVelocity(Point2D velocity) { this.velocity = velocity; }
    public double getRadius() { return radius; }
    public int getGroupId() { return groupId; }
    public void setGroupId(int groupId) { this.groupId = groupId; }
    
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
    
    
}