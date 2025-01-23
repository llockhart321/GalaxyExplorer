import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.*;
import java.util.Random;





public class Gate {
   // Direction variable to say which way the gate will point to using normal circular degrees
   private double direction;
   // Object that the gate leads to
   private int targetSystem;
   
   // which system the gate is in
   private int systemID;
   
   //location. dteremend from random;
   private double x, y;
  
   private double sizeX = 10;
   private double sizeY = 50;
   private Rectangle bounds;
   
   
   
   // Constructor for each gate
   public Gate (double direction, int targetSystem, double x, double y) {
      this.direction = direction;
      this.targetSystem = targetSystem;
      this.systemID = systemID;
      this.x = x;
      this.y = y;
      this.bounds = new Rectangle(this.x, this.y, sizeX, sizeY);
   }
   
   //get the id of the solar system the gate is in
   public int getSystemID(){
      return systemID;
   }
   
   // Method to draw the gate using the direciton variable
   public void drawMe(GraphicsContext gc, double offsetX, double offsetY) {
      // Enter drawing code here...
      
      gc.setFill(Color.PURPLE);
      gc.fillOval(x - offsetX,y - offsetY,sizeX,sizeY);
      
   }
   
   public Rectangle getBounds(){
      return this.bounds;
   }
   
   public void activate(GraphicsContext gc){
   
      StarSystem newSys;
      
      //load new system
      if (StarSystemCache.get(targetSystem) == null){
         //create this new system.
         newSys = new StarSystem(gc, targetSystem);
         StarSystemCache.add( newSys );
      }
      else{
         newSys = StarSystemCache.get(targetSystem);
      }
      Player.getInstance().setSystem(newSys);
      // Reset the camera angle
      Camera.getInstance().reset();
      Player player = Player.getInstance();
      //bring platyer to new spawn point
      player.setX(0);
      player.setY(0);
      
   }
   
}