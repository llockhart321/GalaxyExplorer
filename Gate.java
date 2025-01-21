public class Gate {
   // Direction variable to say which way the gate will point to using normal circular degrees
   private double direction;
   // Object that the gate leads to
   private Gate to;
   // Constructor for each gate
   public Gate (double direction, Gate to) {
      this.direction = direction;
      this.to = to;
   }
   // Method to draw the gate using the direciton variable
   public void drawMe() {
      // Enter drawing code here...
   }
}