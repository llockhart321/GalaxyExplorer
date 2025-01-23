public class PlayerMovementState {
   private static PlayerMovementState instance;
   private double deltaX = 0;
   private double deltaY = 0;
   private double slowRate = 0.010;
   private double speed = 1.0; 
   private boolean movingRight = false;
   private boolean movingLeft = false;
   private boolean movingDown = false;
   private boolean movingUp = false;
   public void moveRight() {
       movingRight = true;
       movingLeft = false;
       deltaX = speed;
   }
   public void moveLeft() {
       movingLeft = true;
       movingRight = false;
       deltaX = -speed;
   }
   public void moveDown() {
       movingDown = true;
       movingUp = false;
       deltaY = speed;
   }
   public void moveUp() {
       movingUp = true;
       movingDown = false;
       deltaY = -speed;
   }
   public static PlayerMovementState getInstance() {
      if (instance == null) { instance = new PlayerMovementState(); }
      return instance;
   }
   public void stopRight() { 
       movingRight = false; 
   }
   public void stopLeft() { 
       movingLeft = false; 
   }
   public void stopDown() { 
       movingDown = false; 
   }
   public void stopUp() { 
       movingUp = false; 
   }
   public void move() {
      if (movingRight) {
         if (deltaX < 0) {
            deltaX = 0;
         }
         deltaX += slowRate;
      } else if (movingLeft) {
         if (deltaX > 0) {
            deltaX = 0;
         }
         deltaX -= slowRate;
      } else {
         if (deltaX > 0) {
            deltaX -= slowRate;
            if (deltaX < 0) deltaX = 0;
         } else if (deltaX < 0) {
            deltaX += slowRate;
            if (deltaX > 0) deltaX = 0;
         }
      }
      if (movingDown) {
         if (deltaY < 0) {
            deltaY = 0;
         }
         deltaY += slowRate;
      } else if (movingUp) {
         if (deltaY > 0) {
            deltaY = 0;
         }
         deltaY -= slowRate;
      } else {
         if (deltaY > 0) {
            deltaY -= slowRate;
            if (deltaY < 0) deltaY = 0;
         } else if (deltaY < 0) {
            deltaY += slowRate;
            if (deltaY > 0) deltaY = 0;
         }
      }
      Player.getInstance().moveXBy(deltaX);
      Player.getInstance().moveYBy(deltaY);
   }
}
