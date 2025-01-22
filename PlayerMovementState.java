public class PlayerMovementState {
   private static PlayerMovementState instance;

   private double deltaX = 0;
   private double deltaY = 0;
   private double slowRate = 0.0005;
   private double speed = 1.0; 
   
   private boolean floatRight = false;
   private boolean floatLeft = false;
   private boolean floatDown = false;
   private boolean floatUp = false;
   
   public void moveRight() {deltaX = speed; }
   public void moveLeft() { deltaX = speed * -1; }
   public void moveDown() { deltaY = speed; }
   public void moveUp() { deltaY = speed * -1; }
   
   public static PlayerMovementState getInstance() {
      if (instance == null) {
         instance = new PlayerMovementState();
      }
      return instance;
   }
   
   public void stopRight() { floatRight = true; }
   public void stopLeft() { floatLeft = true; }
   public void stopDown() { floatDown = true; }
   public void stopUp() { floatUp = true; }
   
   public void move() {
      if (floatRight) {
         floatLeft = false;
         if (deltaX >= 0) {
            deltaX -= slowRate;
         } else {
            deltaX = 0;
            floatRight = false;
         }
      } else if (floatLeft) {
         floatRight = false;
         if (deltaX <= 0) {
            deltaX += slowRate;
         } else {
            deltaX = 0;
            floatLeft = false;
         }
      }
      if (floatDown) {
         floatUp = false;
         if (deltaY >= 0) {
            deltaY -= slowRate;
         } else {
            deltaY = 0;
            floatDown = false;
         }
      } else if (floatUp) {
         floatDown = false;
         if (deltaY <= 0) {
            deltaY += slowRate;
         } else {
            deltaY = 0;
            floatUp = false;
         }
      }
      Player.getInstance().moveXBy(deltaX);
      Player.getInstance().moveYBy(deltaY);
   }
}