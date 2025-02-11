import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;
import javafx.geometry.Point2D;
import java.util.*;
public class Asteroid {
    private List<AsteroidPart> parts;
    private double centerDistance;
    private double orbitalPosition;
    private double orbitalSpeed;
    private boolean isIntact;
    private static final double ORBITAL_FORCE = 0.1;
    private static final double IMPACT_FORCE = 5.0;
    public Asteroid(double distance, double position, double baseRadius, int speed) {
        this.centerDistance = distance;
        this.orbitalPosition = position;
        this.orbitalSpeed = speed;
        this.isIntact = true;
        this.parts = new ArrayList<>();
        // Create 3-5 overlapping parts
        Random rand = new Random();
        int numParts = rand.nextInt(3) + 3;
        for (int i = 0; i < numParts; i++) {
            double angle = 2 * Math.PI * i / numParts;
            double partRadius = baseRadius * (0.6 + rand.nextDouble() * 0.4);
            double offset = baseRadius * 0.7; // Overlap distance
            Point2D partPos = new Point2D(
                    Math.cos(angle) * offset,
                    Math.sin(angle) * offset
            );
            parts.add(new AsteroidPart(partPos, partRadius, 0));
        }
    }
    public boolean checkMissileCollision(Point2D missilePos, double missileRadius) {
        Point2D asteroidCenter = new Point2D(getRelativeX(), getRelativeY());
        for (Iterator<AsteroidPart> iterator = parts.iterator(); iterator.hasNext(); ) {
            AsteroidPart part = iterator.next();
            Point2D partWorldPos = asteroidCenter.add(part.getPosition());
            if (partWorldPos.distance(missilePos) < (part.getRadius() + missileRadius)) {
                // Calculate impact direction
                Point2D impactDir = partWorldPos.subtract(missilePos).normalize();
                // Remove the hit part
                iterator.remove();
                // If asteroid was intact, apply impact force and disable orbital motion
                if (isIntact) {
                    isIntact = false;
                    for (AsteroidPart remainingPart : parts) {
                        Point2D partPos = remainingPart.getPosition();
                        double distance = partPos.distance(part.getPosition());
                        double forceFactor = 0.5 / (1.0 + distance);
                        // Calculate force direction away from the impact
                        Point2D forceDir = partPos.subtract(part.getPosition()).normalize();
                        Point2D impulse = forceDir.multiply(IMPACT_FORCE * forceFactor);
                        remainingPart.setVelocity(remainingPart.getVelocity().add(impulse));
                    }
                }
                // Recalculate groups after impact
                updateGroups();
                return true;
            }
        }
        return false;
    }
    public void applyCollisionForce(double force){
    }
    private void updateGroups() {
        if (parts.isEmpty()) return;
        // Reset all group IDs
        for (AsteroidPart part : parts) {
            part.setGroupId(-1);
        }
        // Assign new groups using flood fill
        int currentGroup = 0;
        for (AsteroidPart part : parts) {
            if (part.getGroupId() == -1) {
                assignGroup(part, currentGroup++);
            }
        }
    }
    private void assignGroup(AsteroidPart part, int groupId) {
        part.setGroupId(groupId);
        for (AsteroidPart other : parts) {
            if (other.getGroupId() == -1 && isConnected(part, other)) {
                assignGroup(other, groupId);
            }
        }
    }
    private boolean isConnected(AsteroidPart part1, AsteroidPart part2) {
        return part1.getPosition().distance(part2.getPosition()) <
                (part1.getRadius() + part2.getRadius()) * 0.8; // 0.8 for overlap threshold
    }
    private void updatePosition() {
    if (isIntact) {
        // Update orbital position for intact asteroids
        orbitalPosition += Math.toRadians(orbitalSpeed / centerDistance);
        if (orbitalPosition > 2 * Math.PI) {
            orbitalPosition -= 2 * Math.PI;
        }
    } else {
        // Update individual part positions with collision checking
        Point2D asteroidCenter = new Point2D(getRelativeX(), getRelativeY());
        for (AsteroidPart part : parts) {
            Point2D pos = part.getPosition();
            Point2D vel = part.getVelocity();
            Point2D newPos = pos.add(vel);
            
            // Get the world position of the part
            Point2D worldPos = asteroidCenter.add(newPos);
            
            // Check collision with sun
            Point2D sunCenter = new Point2D(Sun.WORLD_CENTER_X, Sun.WORLD_CENTER_Y);
            double distToSun = worldPos.distance(sunCenter);
            if (distToSun < (300 + part.getRadius())) { // Sun radius (300) + part radius
                // Calculate bounce direction from sun
                Point2D bounceDir = worldPos.subtract(sunCenter).normalize();
                // Reflect velocity off sun surface
                vel = reflect(vel, bounceDir);
                part.setVelocity(vel.multiply(0.8)); // Reduce velocity after bounce
                // Move part to just outside sun
                double correctDist = 300 + part.getRadius();
                Point2D correctedPos = sunCenter.add(bounceDir.multiply(correctDist));
                newPos = correctedPos.subtract(asteroidCenter);
            }
            
            // Check collision with planets
            StarSystem system = Player.getInstance().getSystem();
            for (Planet planet : system.getPlanets()) {
                Point2D planetCenter = new Point2D(planet.getRelativeX(500), planet.getRelativeY(500));
                double distToPlanet = worldPos.distance(planetCenter);
                if (distToPlanet < (planet.getRadius() + part.getRadius())) {
                    // Calculate bounce direction from planet
                    Point2D bounceDir = worldPos.subtract(planetCenter).normalize();
                    // Reflect velocity off planet surface
                    vel = reflect(vel, bounceDir);
                    part.setVelocity(vel.multiply(0.8)); // Reduce velocity after bounce
                    // Move part to just outside planet
                    double correctDist = planet.getRadius() + part.getRadius();
                    Point2D correctedPos = planetCenter.add(bounceDir.multiply(correctDist));
                    newPos = correctedPos.subtract(asteroidCenter);
                }
            }
            
            // Update position
            part.setPosition(newPos);
        }
    }
}

// Helper method to reflect a velocity vector off a surface normal
private Point2D reflect(Point2D velocity, Point2D normal) {
    double dot = velocity.dotProduct(normal);
    return velocity.subtract(normal.multiply(2 * dot));
}
    public double getRelativeX() {
        return Sun.WORLD_CENTER_X + centerDistance * Math.cos(orbitalPosition);
    }
    
    public double getRelativeY() {
        return Sun.WORLD_CENTER_Y + centerDistance * Math.sin(orbitalPosition);
    }
    public void drawMe(GraphicsContext gc, double cameraOffsetX, double cameraOffsetY) {
        updatePosition();
        Point2D center = new Point2D(getRelativeX() - cameraOffsetX, getRelativeY() - cameraOffsetY);
        // Draw each part
        for (AsteroidPart part : parts) {
            Point2D partPos = center.add(part.getPosition());
            // Vary color slightly based on group ID for visualization
            Color partColor = isIntact ? Color.BURLYWOOD :
                    Color.hsb(30 + part.getGroupId() * 20, 0.3, 0.6);
            gc.setFill(partColor);
            gc.fillOval(
                    partPos.getX() - part.getRadius(),
                    partPos.getY() - part.getRadius(),
                    part.getRadius() * 2,
                    part.getRadius() * 2
            );
        }
    }
    public boolean isEmpty() {
        return parts.isEmpty();
    }
    public void checkPlayerCollision(Player player) {
        Point2D asteroidCenter = new Point2D(getRelativeX(), getRelativeY());
        Point2D playerCenter = new Point2D(
                player.getX() + Player.getBounds().getRadius(),
                player.getY() + Player.getBounds().getRadius()
        );
        
        // Check if any part of the asteroid collides with the player
        for (AsteroidPart part : parts) {
            Point2D partWorldPos = asteroidCenter.add(part.getPosition());
            double collisionDist = part.getRadius() + Player.getBounds().getRadius();
            if (partWorldPos.distance(playerCenter) < collisionDist) {
                // Calculate push direction
                Point2D pushDir = partWorldPos.subtract(playerCenter).normalize();
                
                // Calculate overlap distance
                double overlap = collisionDist - partWorldPos.distance(playerCenter);
                
                // Push player out of the asteroid part
                player.setX(player.getX() - pushDir.getX() * overlap);
                player.setY(player.getY() - pushDir.getY() * overlap);
                
                // Stop player movement
                PlayerMovementState.getInstance().stop();
                
                break; // Exit after first collision
            }
        }
    }    public void applyCollisionForce(Point2D force) {
        isIntact = false;
        for (AsteroidPart part : parts) {
            Point2D currentVel = part.getVelocity();
            part.setVelocity(currentVel.add(force));
        }
    }
 }