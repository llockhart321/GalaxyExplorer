import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.Random;

public class StarSystemNebula {
   private double[][] noiseLayer1;
   private double[][] noiseLayer2;
   private Color[] retroColors;
   private int width;
   private int height;
   private double offsetX = 0;
   private double offsetY = 0;
   private final int GRID_SIZE = 32;
   private final double SCALE = 180.0;
    
   public StarSystemNebula(int width, int height, long seed) {
      this.width = width * 5;
      this.height = height * 5;
        
        // Center the nebula on player spawn (380, 220)
      this.offsetX = 380;
      this.offsetY = 220;
        
      retroColors = new Color[]{
            Color.rgb(255, 51, 153, 0.4),
            Color.rgb(0, 255, 255, 0.3)
         };
      noiseLayer1 = generatePerlinNoise(GRID_SIZE, GRID_SIZE, seed);
      noiseLayer2 = generatePerlinNoise(GRID_SIZE, GRID_SIZE, seed + 1);
   }

   
   public void setOffset(double x, double y) {
    // Simply store the raw offset values
      this.offsetX = x;
      this.offsetY = y;
   }

   private double wrapValue(double value, double max) {
      return value - max * Math.floor(value / max);
   }
   
   public void draw(GraphicsContext gc) {
        // Draw simplified grid
      if (offsetX % 40 == 0 && offsetY % 40 == 0) {
         drawGrid(gc);
      }
        
        // Draw nebula with larger steps
      drawNoiseLayer(gc, noiseLayer1, 0.7, 1.0, retroColors[0]);
      drawNoiseLayer(gc, noiseLayer2, 1.0, 0.6, retroColors[1]);
   }
    
   private void drawGrid(GraphicsContext gc) {
      gc.setStroke(Color.rgb(255, 255, 255, 0.15));
      gc.setLineWidth(1);
      double step = 80; // Increased grid size
        
      for (double x = 0; x < gc.getCanvas().getWidth(); x += step) {
         gc.strokeLine(x, 0, x, gc.getCanvas().getHeight());
      }
      for (double y = 0; y < gc.getCanvas().getHeight(); y += step) {
         gc.strokeLine(0, y, gc.getCanvas().getWidth(), y);
      }
   }
   
   private void drawNoiseLayer(GraphicsContext gc, double[][] noise, double parallaxFactor, double alpha, Color baseColor) {
      int screenWidth = (int) gc.getCanvas().getWidth();
      int screenHeight = (int) gc.getCanvas().getHeight();
   
      // Calculate effective offsets
      double effectiveOffsetX = offsetX * parallaxFactor;
      double effectiveOffsetY = offsetY * parallaxFactor;
   
      for (int x = 0; x < screenWidth; x += 4) {  // Sample every 4 pixels for optimization
         for (int y = 0; y < screenHeight; y += 4) {
            // Map screen coordinates to noise grid
            double noiseX = wrapValue(x + effectiveOffsetX, SCALE * GRID_SIZE) / SCALE;
            double noiseY = wrapValue(y + effectiveOffsetY, SCALE * GRID_SIZE) / SCALE;
         
            // Sample noise using bilinear interpolation
            double noiseValue = sampleNoise(noise, noiseX, noiseY);
         
            // Calculate fade-out based on distance from screen center
            double centerX = screenWidth / 2.0;
            double centerY = screenHeight / 2.0;
            double distanceFromCenter = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
            double fadeRadius = Math.max(width, height) * 0.5; // Adjust for larger nebula
            double fadeOut = Math.max(0, 1 - (distanceFromCenter / fadeRadius));
         
            // Apply color with noise value and fade-out effect
            if (noiseValue > 0.2) {  // Threshold to define visible clouds
               Color color = baseColor.deriveColor(0, 1, 1,
                  noiseValue * fadeOut * alpha * 0.4); // Adjust alpha for visibility
               gc.setFill(color);
               gc.fillRect(x, y, 4, 4);  // Draw 4x4 blocks for optimization
            }
         }
      }
   }
   
   private double[][] generatePerlinNoise(int width, int height, long seed) {
      Random rand = new Random(seed);
      double[][] noise = new double[width][height];
      
      // Generate random gradients
      double[][] gradients = new double[width][height];
      for (int i = 0; i < width; i++) {
         for (int j = 0; j < height; j++) {
            gradients[i][j] = rand.nextDouble() * 2 * Math.PI;
         }
      }
      
      // Generate noise using value noise with gradient interpolation
      for (int i = 0; i < width; i++) {
         for (int j = 0; j < height; j++) {
            noise[i][j] = generateNoisePoint(i, j, gradients);
         }
      }
      
      return noise;
   }
   
   private double generateNoisePoint(int x, int y, double[][] gradients) {
      double total = 0;
      double frequency = 1;
      double amplitude = 1;
      double maxValue = 0;
      
      // Add multiple octaves of noise
      for (int i = 0; i < 4; i++) {
         total += interpolatedNoise(x * frequency, y * frequency, gradients) * amplitude;
         maxValue += amplitude;
         amplitude *= 0.5;
         frequency *= 2;
      }
      
      return total / maxValue;
   }
   
   private double interpolatedNoise(double x, double y, double[][] gradients) {
      int intX = (int) x;
      int intY = (int) y;
      double fracX = x - intX;
      double fracY = y - intY;
      
      // Get values at corners of cell
      double v1 = smoothNoise(intX, intY, gradients);
      double v2 = smoothNoise(intX + 1, intY, gradients);
      double v3 = smoothNoise(intX, intY + 1, gradients);
      double v4 = smoothNoise(intX + 1, intY + 1, gradients);
      
      // Interpolate between values
      double i1 = interpolate(v1, v2, fracX);
      double i2 = interpolate(v3, v4, fracX);
      
      return interpolate(i1, i2, fracY);
   }
   
   private double smoothNoise(int x, int y, double[][] gradients) {
      x = Math.floorMod(x, gradients.length);
      y = Math.floorMod(y, gradients[0].length);
      return gradients[x][y];
   }
   
   private double interpolate(double a, double b, double x) {
      // Smooth step interpolation
      x = x * x * (3 - 2 * x);
      return a + (b - a) * x;
   }
   
   private double sampleNoise(double[][] noise, double x, double y) {
      // Bilinear interpolation for smooth sampling
      int x1 = Math.floorMod((int) x, noise.length);
      int y1 = Math.floorMod((int) y, noise[0].length);
      int x2 = Math.floorMod(x1 + 1, noise.length);
      int y2 = Math.floorMod(y1 + 1, noise[0].length);
      
      double fracX = x - (int) x;
      double fracY = y - (int) y;
      
      double v1 = noise[x1][y1];
      double v2 = noise[x2][y1];
      double v3 = noise[x1][y2];
      double v4 = noise[x2][y2];
      
      double i1 = interpolate(v1, v2, fracX);
      double i2 = interpolate(v3, v4, fracX);
      
      return interpolate(i1, i2, fracY);
   }
}