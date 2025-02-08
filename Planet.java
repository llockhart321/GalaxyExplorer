import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;

public class Planet {
    private Color color;
    private Color glowColor;
    private double distance, position;
    private int radius, speed;
    private Circle bounds;
    private double[] trailX = new double[15];
    private double[] trailY = new double[15];
    private int trailIndex = 0;
    
    public Planet(Color color, double distance, double position, int radius, int speed) {
        this.color = color;
        this.glowColor = color.deriveColor(0, 1, 1, 0.3);
        this.distance = distance;
        this.position = position;
        this.radius = radius;
        this.speed = speed;
        this.bounds = new Circle(getRelativeX(500) - radius, getRelativeY(500) - radius, radius);
        
        // Initialize trail arrays
        for (int i = 0; i < trailX.length; i++) {
            trailX[i] = getRelativeX(500);
            trailY[i] = getRelativeY(500);
        }
    }
    
    private void updatePosition() {
        position += Math.toRadians(speed / distance);
        if (position > 2 * Math.PI) position -= 2 * Math.PI;
        
        // Update trail
        trailX[trailIndex] = getRelativeX(500);
        trailY[trailIndex] = getRelativeY(500);
        trailIndex = (trailIndex + 1) % trailX.length;
        
        bounds.setCenterX(getRelativeX(500));
        bounds.setCenterY(getRelativeY(500));
    }

    public double getRelativeX(double relativex0) {
        return Sun.WORLD_CENTER_X + distance * Math.cos(position);
    }

    public double getRelativeY(double relativex0) {
        return Sun.WORLD_CENTER_Y + distance * Math.sin(position);
    }

    public void drawMe(GraphicsContext gc, double cameraOffsetX, double cameraOffsetY) {
        updatePosition();
        
        // Draw orbital trail
        for (int i = 0; i < trailX.length - 1; i++) {
            int nextI = (i + 1) % trailX.length;
            gc.setStroke(color.deriveColor(0, 1, 1, 0.2 * (double)i / trailX.length));
            gc.setLineWidth(2);
            gc.strokeLine(
                trailX[i] - cameraOffsetX,
                trailY[i] - cameraOffsetY,
                trailX[nextI] - cameraOffsetX,
                trailY[nextI] - cameraOffsetY
            );
        }
        
        double screenX = bounds.getCenterX() - radius - cameraOffsetX;
        double screenY = bounds.getCenterY() - radius - cameraOffsetY;
        
        // Draw glow
        gc.setFill(glowColor);
        gc.fillOval(screenX - 4, screenY - 4, (radius * 2) + 8, (radius * 2) + 8);
        
        // Draw planet
        gc.setFill(color);
        gc.fillOval(screenX, screenY, radius * 2, radius * 2);
        
        // Draw highlight
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        gc.strokeArc(screenX + radius/2, screenY + radius/2, radius, radius, -45, 90, javafx.scene.shape.ArcType.OPEN);
    }


    public boolean isCollidingWith(Player player) {
        // Get the actual positions of both circles in world space
        Circle playerBounds = Player.getBounds();

        // Create temporary circles at the actual world positions for collision check
        Circle tempPlayerCircle = new Circle(
                player.getX() + playerBounds.getRadius(),  // center X = position + radius
                player.getY() + playerBounds.getRadius(),  // center Y = position + radius
                playerBounds.getRadius()
        );

        // Check actual geometric collision between circles
        double dx = tempPlayerCircle.getCenterX() - bounds.getCenterX();
        double dy = tempPlayerCircle.getCenterY() - bounds.getCenterY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        return distance < (bounds.getRadius() + tempPlayerCircle.getRadius());
    }

    // Getter methods for collision
    public double getX() {
        return bounds.getCenterX();
    }

    public double getY() {
        return bounds.getCenterY();
    }

    public double getRadius() {
        return bounds.getRadius();
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

    public void checkAsteroidCollision(Asteroid asteroid) {
        Point2D planetCenter = new Point2D(bounds.getCenterX(), bounds.getCenterY());
        Point2D asteroidCenter = new Point2D(asteroid.getRelativeX(), asteroid.getRelativeY());

        double collisionDist = bounds.getRadius() + 20; // approximate asteroid size
        if (planetCenter.distance(asteroidCenter) < collisionDist) {
            // Calculate bounce direction
            Point2D bounceDir = asteroidCenter.subtract(planetCenter).normalize();

            // Break apart the asteroid and apply force away from planet
            asteroid.applyCollisionForce(bounceDir.multiply(0.1));
        }
    }

    public void checkPlanetCollision(Planet other) {
        double dx = other.bounds.getCenterX() - this.bounds.getCenterX();
        double dy = other.bounds.getCenterY() - this.bounds.getCenterY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        double minDistance = this.bounds.getRadius() + other.bounds.getRadius();

        if (distance < minDistance && distance > 0) {
            // Calculate overlap
            double overlap = minDistance - distance;

            // Normalize direction vector
            double nx = dx / distance;
            double ny = dy / distance;

            // Adjust positions (move each planet half the overlap)
            double adjustX = nx * (overlap * 0.5);
            double adjustY = ny * (overlap * 0.5);

            // Adjust orbital positions instead of direct positions
            this.adjustOrbit(-adjustX, -adjustY);
            other.adjustOrbit(adjustX, adjustY);
        }
    }

    private void adjustOrbit(double dx, double dy) {
        // Convert cartesian adjustment to polar
        double currentX = distance * Math.cos(position);
        double currentY = distance * Math.sin(position);

        // Apply small adjustment
        currentX += dx * 0.1; // Reduced effect for gentler movement
        currentY += dy * 0.1;

        // Convert back to polar coordinates
        distance = Math.sqrt(currentX * currentX + currentY * currentY);
        position = Math.atan2(currentY, currentX);

        // Update bounds
        bounds.setCenterX(getRelativeX(500));
        bounds.setCenterY(getRelativeY(500));
    }

    public Circle getBounds() {
        return bounds;
    }
}