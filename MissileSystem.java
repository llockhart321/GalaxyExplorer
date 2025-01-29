import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.geometry.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

public class MissileSystem {
    private static MissileSystem instance;
    private ArrayList<Missile> missiles = new ArrayList<>();
    private long lastShotTime = 0;
    private static final long SHOT_COOLDOWN = 250; // Milliseconds between shots

    public static MissileSystem getInstance() {
        if (instance == null) {
            instance = new MissileSystem();
        }
        return instance;
    }

    private MissileSystem() {}

    public void shoot(double mouseX, double mouseY) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime < SHOT_COOLDOWN) {
            return;
        }
        
        Player player = Player.getInstance();
        double playerCenterX = player.getX() + 40 / 2;
        double playerCenterY = player.getY() + 40 / 2;

        // Add camera offset to mouse position since it's in screen coordinates
        Camera camera = Camera.getInstance(player.getSystem());
        mouseX += camera.getMapCenterOffsetX();
        mouseY += camera.getMapCenterOffsetY();

        // Calculate direction from player to mouse
        double dirX = mouseX - playerCenterX;
        double dirY = mouseY - playerCenterY;

        missiles.add(new Missile(playerCenterX, playerCenterY, dirX, dirY));
        lastShotTime = currentTime;
    }

    public void update(GraphicsContext gc, double cameraOffsetX, double cameraOffsetY) {
        Iterator<Missile> iter = missiles.iterator();
        while (iter.hasNext()) {
            Missile missile = iter.next();
            missile.update();

            // Check for asteroid collisions
            StarSystem system = Player.getInstance().getSystem();
            boolean hitAsteroid = checkAsteroidCollisions(missile, system);
            
            if (hitAsteroid || !missile.isActive()) {
                iter.remove();
            } else {
                missile.drawMe(gc, cameraOffsetX, cameraOffsetY);
            }
        }
    }

    private boolean checkAsteroidCollisions(Missile missile, StarSystem system) {
        return system.checkMissileCollisions(missile.getPosition(), missile.getRadius());
    }
}