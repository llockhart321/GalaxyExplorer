import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;
import javafx.geometry.Point2D;
import java.util.*;

class AsteroidPart {
    private Point2D position;
    private Point2D velocity;
    private double radius;
    private Color color;
    private int groupId;

    public AsteroidPart(Point2D position, double radius, int groupId) {
        this.position = position;
        this.velocity = new Point2D(0, 0);
        this.radius = radius;
        this.color = Color.BURLYWOOD;
        this.groupId = groupId;
    }

    public Point2D getPosition() { return position; }
    public void setPosition(Point2D position) { this.position = position; }
    public Point2D getVelocity() { return velocity; }
    public void setVelocity(Point2D velocity) { this.velocity = velocity; }
    public double getRadius() { return radius; }
    public int getGroupId() { return groupId; }
    public void setGroupId(int groupId) { this.groupId = groupId; }
}