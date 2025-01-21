import javafx.scene.paint.*;
import java.util.*;
public class StarSystem {
   private List <Planet> planets;
   private List <Gate> gates;

   public StarSystem() {
      Random rand = new Random();
      int numPlanets = rand.nextInt(5) + 1;  // Random number between 1 and 5
      // Set number of gates from an outside input
      
      planets = new ArrayList<>();
      gates = new ArrayList<>();
      
      for (int i = 0; i < numPlanets; i++) {
         planets.add(new Planet(Color.BLUE, 0, 0, 0, 0));
      }
      
      // Fill the gates arraylist
   }
}
