public class PlayerMovementState {
   //Player movement booleans
   private boolean moveRightActive = false;
   private boolean moveLeftActive = false;
   private boolean moveDownActive = false;
   private boolean moveUpActive = false;
   
   private boolean moveRightPassive = false;
   private boolean moveLeftPassive = false;
   private boolean moveDownPassive = false;
   private boolean moveUpPassive = false;
   
   public void moveRight() {
      moveRightActive = true;
      moveLeftActive = false;
      moveDownActive = false;
      moveUpActive = false;
      moveRightPassive = false;
      moveLeftPassive = false;
      moveDownPassive = false;
      moveUpPassive = false;
   }
   public void moveLeft() {
      moveRightActive = false;
      moveLeftActive = true;
      moveDownActive = false;
      moveUpActive = false;
      moveRightPassive = false;
      moveLeftPassive = false;
      moveDownPassive = false;
      moveUpPassive = false;
   }
   public void moveDown() {
      moveRightActive = false;
      moveLeftActive = false;
      moveDownActive = true;
      moveUpActive = false;
      moveRightPassive = false;
      moveLeftPassive = false;
      moveDownPassive = false;
      moveUpPassive = false;
   }
   public void moveUp() {
      moveRightActive = false;
      moveLeftActive = false;
      moveDownActive = false;
      moveUpActive = true;
      moveRightPassive = false;
      moveLeftPassive = false;
      moveDownPassive = false;
      moveUpPassive = false;
   }
   public void floatRight() {
      moveRightActive = false;
      moveLeftActive = false;
      moveDownActive = false;
      moveUpActive = false;
      moveRightPassive = true;
      moveLeftPassive = false;
      moveDownPassive = false;
      moveUpPassive = false;
   }
   public void floatLeft() {
      moveRightActive = false;
      moveLeftActive = false;
      moveDownActive = false;
      moveUpActive = false;
      moveRightPassive = false;
      moveLeftPassive = true;
      moveDownPassive = false;
      moveUpPassive = false;
   }
   public void floatDown() {
      moveRightActive = false;
      moveLeftActive = false;
      moveDownActive = false;
      moveUpActive = false;
      moveRightPassive = false;
      moveLeftPassive = false;
      moveDownPassive = true;
      moveUpPassive = false;
   }
   public void floatUp() {
      moveRightActive = false;
      moveLeftActive = false;
      moveDownActive = false;
      moveUpActive = false;
      moveRightPassive = false;
      moveLeftPassive = false;
      moveDownPassive = false;
      moveUpPassive = true;
   }
}