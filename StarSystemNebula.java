import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.Random;

public class StarSystemNebula {
    private double[][] noiseLayer1;
    private double[][] noiseLayer2;
    private Color nebulaColor;
    private int width;
    private int height;
    private double offsetX = 0;
    private double offsetY = 0;
    private final int GRID_SIZE = 64; // Size of the noise grid
    private final double SCALE = 120.0; // Scale of the noise
    
    public StarSystemNebula(int width, int height, long seed) {
        this.width = width * 5;  // 5 screens wide
        this.height = height * 5; // 5 screens high
        Random rand = new Random(seed);
        
        // Generate a random color for the nebula
        nebulaColor = Color.hsb(
            rand.nextDouble() * 360, // Random hue
            0.5 + rand.nextDouble() * 0.3, // Saturation between 0.5 and 0.8
            0.4 + rand.nextDouble() * 0.3  // Brightness between 0.4 and 0.7
        );
        
        // Generate two layers of noise
        noiseLayer1 = generatePerlinNoise(GRID_SIZE, GRID_SIZE, seed);
        noiseLayer2 = generatePerlinNoise(GRID_SIZE, GRID_SIZE, seed + 1);
    }
    
    public void setOffset(double x, double y) {
        this.offsetX = x;
        this.offsetY = y;
    }
    
    public void draw(GraphicsContext gc) {
        // Draw both layers with different speeds for parallax effect
        drawNoiseLayer(gc, noiseLayer1, 0.7, 1.0); // Layer 1 moves slower
        drawNoiseLayer(gc, noiseLayer2, 1.0, 0.6); // Layer 2 moves faster and is more transparent
    }
    
    private void drawNoiseLayer(GraphicsContext gc, double[][] noise, double parallaxFactor, double alpha) {
        int screenWidth = (int) gc.getCanvas().getWidth();
        int screenHeight = (int) gc.getCanvas().getHeight();

        // Calculate effective offsets
        double effectiveOffsetX = offsetX * parallaxFactor;
        double effectiveOffsetY = offsetY * parallaxFactor;

        for (int x = 0; x < screenWidth; x += 4) {  // Sample every 4 pixels for optimization
            for (int y = 0; y < screenHeight; y += 4) {
                // Map screen coordinates to noise grid
                double noiseX = (x + effectiveOffsetX) / SCALE;
                double noiseY = (y + effectiveOffsetY) / SCALE;

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
                    Color color = nebulaColor.deriveColor(0, 1, 1,
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