import javafx.scene.paint.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.Random;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Affine;

public class GalaxyMap {
    private static GraphicsContext gc;
    private static AnimationHandler ah;
    private static boolean isOpen = false;

    private double viewX = 0;
    private double viewY = 0;
    private double scale = 1.0;
    private double minScale = 0.5;
    private double maxScale = 6.0;
    
    // Mouse tracking for pan
    private double lastMouseX;
    private double lastMouseY;
    private boolean isPanning = false;
    
    private static final int WIDTH = 800;
    private static final int HEIGHT = 450;
    private static GalaxyMap instance;
    
    // Public method to get private instance
    public static GalaxyMap getInstance() {
        if (instance == null) {
            instance = new GalaxyMap();
        }
        return instance;
    }
    
    // Private constructor
    private GalaxyMap() {}
    
    // Method to draw the map onscreen
    public void mapAction() {
        if (!isOpen) {
            // open map
            isOpen = true;
            ah.stop();
            setupEventHandlers();
            draw();
        } else {
            // close map
            isOpen = false;
            scale = 1;
            viewX = 0;
            viewY = 0;
            ah.start();
        }
    }
    
    private void setupEventHandlers() {
        Canvas canvas = gc.getCanvas();
        
        // Mouse press for starting pan
        canvas.setOnMousePressed(e -> {
            if (isOpen) {
                lastMouseX = e.getX();
                lastMouseY = e.getY();
                isPanning = true;
            }
        });
        
        // Mouse drag for panning
        canvas.setOnMouseDragged(e -> {
            if (isOpen && isPanning) {
                double deltaX = e.getX() - lastMouseX;
                double deltaY = e.getY() - lastMouseY;
                
                viewX += deltaX / scale;
                viewY += deltaY / scale;
                
                lastMouseX = e.getX();
                lastMouseY = e.getY();
                
                draw();
            }
        });
        
        // Mouse release to end pan
        canvas.setOnMouseReleased(e -> {
            if (isOpen) {
                isPanning = false;
            }
        });
        
        // Scroll/gesture for zoom
        canvas.setOnScroll(e -> {
            if (isOpen) {
                double zoomFactor;
                if (e.isInertia()) {
                    // Handle trackpad gesture
                    zoomFactor = 0.5 - e.getDeltaY() * 0.001;
                    
                } else {
                    // Handle mouse wheel
                    zoomFactor = e.getDeltaY() > 0 ? 1.1 : 0.9;
                }
                
                zoom(zoomFactor, e.getX(), e.getY());
            }
        });
    }
    
    private void zoom(double factor, double mouseX, double mouseY) {
        double newScale = scale * factor;
        
        // Enforce zoom limits
        if (newScale < minScale) {
            factor = minScale / scale;
            newScale = minScale;
        } else if (newScale > maxScale) {
            factor = maxScale / scale;
            newScale = maxScale;
        }
        
        // Convert mouse coordinates to world space before zoom
        double worldX = (mouseX - WIDTH/2) / scale - viewX;
        double worldY = (mouseY - HEIGHT/2) / scale - viewY;

        scale = newScale;
        
        // Convert the same world coordinates back to screen space after zoom
        double newScreenX = (worldX * scale + WIDTH/2);
        double newScreenY = (worldY * scale + HEIGHT/2);
        
        // Adjust view to maintain mouse position
        viewX -= (newScreenX - mouseX) / scale;
        viewY -= (newScreenY - mouseY) / scale;
        
        draw();
    }    
    private void draw() {

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);
        
        // Save the current transform
        gc.save();
        
        // Apply view transformation
        gc.translate(WIDTH/2, HEIGHT/2);
        gc.scale(scale, scale);
        gc.translate(viewX, viewY);
        gc.translate(-WIDTH/2, -HEIGHT/2);

        drawGalaxy();
        
        // Restore the transform
        gc.restore();
    }
    
    
    //shout out to cluade for cooking a spiral galaxy
     public void drawGalaxy() {
        double centerX = 400;
        double centerY = 225;
        
        // Set the spiral color and make both arms thick
        gc.setStroke(Color.rgb(60, 60, 120));
        gc.setLineWidth(15 / scale);  // Adjust line width based on zoom
        gc.setGlobalAlpha(0.6);
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineJoin(StrokeLineJoin.ROUND);
        
        // Draw two spiral arms with longer reach
        for (int arm = 0; arm < 2; arm++) {
            double angleOffset = arm * Math.PI;
            gc.beginPath();
            boolean first = true;
            
            
            for (double t = 0; t < 16; t += 0.05) { 
               
                double r = 20 * Math.exp(0.18 * t); 
                double angle = t + angleOffset;
                
                double x = centerX + r * Math.cos(angle);
                double y = centerY + r * Math.sin(angle);
                
                if (first) {
                    gc.moveTo(x, y);
                    first = false;
                } else {
                    gc.lineTo(x, y);
                }
            }
            
            gc.stroke();
        }
        
        // Draw the core
        gc.setFill(Color.rgb(60, 60, 120));
        double coreSize = 40 / scale;
        gc.fillOval(centerX - coreSize/2, centerY - coreSize/2, coreSize, coreSize);
        
        // Reset graphics context
        gc.setGlobalAlpha(1.0);
        gc.setLineWidth(1.0);
    }

        
    public void set(GraphicsContext gc, AnimationHandler ah) {
        this.gc = gc;
        this.ah = ah;
    }
}