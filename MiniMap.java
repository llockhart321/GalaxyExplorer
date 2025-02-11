import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class MiniMap {


    private static MiniMap instance;

    private GraphicsContext gc;
    private  AnimationHandler ah;
    private  boolean isOpen = false;
    private  final int WIDTH = 400;
    private  final int HEIGHT = 225;





    public static MiniMap getInstance() {
        if (instance == null) {
            instance = new MiniMap();
        }
        return instance;
    }

    //called from main to set gc and animationh
    public void set(GraphicsContext gc, AnimationHandler ah) {
        this.gc = gc;
        this.ah = ah;
    }




    public void mapAction() {
        if (!isOpen) {
            isOpen = true;
            PlayerMovementState.getInstance().stop();
            ah.stop();
            draw();
        } else {
            isOpen = false;
            closeMap();
            ah.start();
        }
    }

    public void closeMap() {
        isOpen = false;
    }


    private void draw() {
        gc.setGlobalAlpha(0.7); // Slightly opaque background
        gc.setFill(Color.BLACK);
        gc.fillRect(200, 112.5, 400, 225); // Minimap at (200,112.5), size 400x225

        gc.setGlobalAlpha(1.0); // Reset opacity for objects

        // Draw gridlines for retro-wave look
        gc.setStroke(Color.DARKMAGENTA);
        for (int i = 0; i <= 400; i += 40) {
            gc.strokeLine(200 + i, 112.5, 200 + i, 112.5 + 225);
        }
        for (int j = 0; j <= 225; j += 40) {
            gc.strokeLine(200, 112.5 + j, 200 + 400, 112.5 + j);
        }

        // Sun always at the minimap center
        double sunX = 200 + 400 / 2;
        double sunY = 112.5 + 225 / 2;
        gc.setFill(Color.YELLOW);
        gc.fillOval(sunX - 30, sunY - 30, 60, 60);

        // Get player's position
        double worldX = Player.getInstance().getX();
        double worldY = Player.getInstance().getY();

        // Scale position to minimap
        double playerX = worldX / 10;
        double playerY = worldY / 10;

        // Clamp player within minimap edges
        playerX = Math.max(0, Math.min(400, playerX));
        playerY = Math.max(0, Math.min(225, playerY));

        gc.setFill(Color.HOTPINK);
        gc.fillOval(200 + playerX - 5, 112.5 + playerY - 5, 10, 10);
    }




}
