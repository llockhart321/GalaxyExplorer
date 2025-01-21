import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.*;

public class StarSystem {
   private List <Planet> planets;
   private List <Gate> gates;
   private ArrayList<Double> starX;
   private ArrayList<Double> starY;
   private ArrayList<Double> starRadius;
   Planet planet;

   public StarSystem() {
      starX = new ArrayList<Double>();
      starY = new ArrayList<Double>();
      starRadius = new ArrayList<Double>();
      
      Random rand = new Random();
      int numPlanets = rand.nextInt(5) + 1;  // Random number between 1 and 5
      // Set number of gates from an outside input
      
      planets = new ArrayList<>();
      gates = new ArrayList<>();
      
      for (int i = 0; i < numPlanets; i++) {
         planets.add(new Planet(Color.BLUE, 0, 0, 0, 0));
      }
      
        int numStars = 100; // Number of stars to draw
        for (int i = 0; i < numStars; i++) {
            double x = rand.nextDouble() * 1000;
            double y = rand.nextDouble() * 1000;
            double radius = rand.nextDouble() * 2 + 1; // Random size for stars (1-3 pixels)
            
            starX.add(x);
            starY.add(y);
            starRadius.add(radius);
        }
        
        planet = new Planet(Color.BLUE, 500, 30, 10, 100);
        
      
      // Fill the gates arraylist
   }
   

   
   public void drawMe(GraphicsContext gc) {
        // Set the background to black
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, 1000,1000);
        gc.setFill(Color.WHITE);

        for(int i=0; i<starX.size(); i++)
        {
            gc.fillOval(starX.get(i), starY.get(i), starRadius.get(i), starRadius.get(i));
        }
        
        planet.drawMe(gc);
        
        // Optionally: Draw planets or gates (if implemented)
        // This example focuses on the starry background.
    }   
}
