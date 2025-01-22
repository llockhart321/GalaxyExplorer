import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.*;




public class Gate {
   // Direction variable to say which way the gate will point to using normal circular degrees
   private double direction;
   // Object that the gate leads to
   private Gate to;
   
   // which system the gate is in
   private int systemID;
   
   //location. dteremend from random;
   private double x=10;
   private double y=10;
   
   //bounds / shape of gate
   Rectangle bounds = new Rectangle(100, 100, 50, 50); // size to be determined :)
   //bounds.setFill(Color.GRAY);
   
   
   // Constructor for each gate
   public Gate (double direction, Gate to, double x, double y) {
      this.direction = direction;
      this.to = to;
      this.systemID = systemID;
   }
   
   //get the id of the solar system the gate is in
   public int getSystemID(){
      return systemID;
   }
   
   // Method to draw the gate using the direciton variable
   public void drawMe(GraphicsContext gc) {
      // Enter drawing code here...
      
      gc.setFill(Color.PURPLE);
      gc.fillOval(x,y,10,50);
      
   }
   
   public Rectangle getBounds(){
      return bounds;
   }
}