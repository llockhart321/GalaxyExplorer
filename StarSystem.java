import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.*;
import javafx.scene.shape.Rectangle;


public class StarSystem {
   private List <Planet> planets;
   private List <Gate> gates;
   private ArrayList<Double> starX;
   private ArrayList<Double> starY;
   private ArrayList<Double> starRadius;
   Planet planet;
   GraphicsContext gc;
   private int ID;
   private static int idCounter = 0;
   private StarSystemNebula nebula;

   public StarSystem(GraphicsContext gc, int potentialID) {
      if(potentialID == -1){     
         this.ID = idCounter++;
      }else{
         ID = potentialID;
         idCounter = ID+1;
      }
      
      starX = new ArrayList<Double>();
      starY = new ArrayList<Double>();
      starRadius = new ArrayList<Double>();
      //not the most effecient i dont think but well see if it matters
      this.gc=gc;
      
      Random rand = new Random();
      int numPlanets = rand.nextInt(5) + 1;  // Random number between 1 and 5
      
      // Set number of gates from an outside input
      //until then it is 2 ;)
      int numGates = 2;
      
      planets = new ArrayList<>();
      gates = new ArrayList<>();
      nebula = new StarSystemNebula(800, 450, ID); //for the parallax clouds
      
      for (int i = 0; i < numPlanets; i++) {
         planets.add(new Planet(Color.BLUE, 0, 0, 0, 0));
      }
      
      //add gates
       for (int i = 0; i < numGates; i++) {
                     //still need to get accurate next system. rn im just doing +1
                              // this rand allows for gates to spawn in orbit path. this needs to be fixed.
         gates.add(new Gate( 0, ID+1, rand.nextInt(700), rand.nextInt(400)));
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

   public void collisionCheck(GraphicsContext gc){
      //check if player collides with gate
      for (int i = 0; i<gates.size(); i++){
          if (Player.getBounds().getBoundsInParent().intersects(gates.get(i).getBounds().getBoundsInParent())) {
                 
                  gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
                  gates.get(i).activate(gc);
          }
          
       }
   }


   public int getID(){
      return this.ID;
   }
   
   public void drawMe(GraphicsContext gc) {
        // Set the background to black
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, 1000,1000);
        
        
        //draw nebula
        nebula.setOffset(Player.getInstance().getX(), Player.getInstance().getY());
        nebula.draw(gc);
        
        gc.setFill(Color.WHITE);
        for(int i=0; i<starX.size(); i++)
        {
            gc.fillOval(starX.get(i), starY.get(i), starRadius.get(i), starRadius.get(i));
        }
        

      
        
        // draw gates
        for (int i = 0; i<gates.size(); i++){
            gates.get(i).drawMe(gc);
        }
        
        

        planet.drawMe(gc);
        
        
    }   

}
