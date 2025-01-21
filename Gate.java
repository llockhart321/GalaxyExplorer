import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;



public class Gate {
   // Direction variable to say which way the gate will point to using normal circular degrees
   private double direction;
   // Object that the gate leads to
   private Gate to;
   
   // which system the gate is in
   private int systemID;
   
   //bounds / shape of gate
   Rectangle bounds = new Rectangle(100, 100, 50, 50); // size to be determined :)
   
   
   // Constructor for each gate
   public Gate (double direction, Gate to) {
      this.direction = direction;
      this.to = to;
      this.systemID = systemID;
   }
   
   //get the id of the solar system the gate is in
   public int getSystemID(){
      return systemID;
   }
   
   // Method to draw the gate using the direciton variable
   public void drawMe() {
      // Enter drawing code here...
      bounds.setFill(Color.GRAY);
   }
}