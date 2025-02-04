// Class to handle when a movement key or map key is pressed
import javafx.event.*;
import javafx.scene.input.*;
public class KeyListenerDown implements EventHandler<KeyEvent> {
   private static PlayerMovementState pms;
   public KeyListenerDown() {
      pms = PlayerMovementState.getInstance();
   }
   public void handle(KeyEvent event) {
      if (event.getCode() == KeyCode.D) {
         pms.moveRight();
      }
      if (event.getCode() == KeyCode.A) {
         pms.moveLeft();
      }
      if (event.getCode() == KeyCode.S) {
         pms.moveDown();
      }
      if (event.getCode() == KeyCode.W) {
         pms.moveUp();
      }
      if (event.getCode() == KeyCode.M) {
         // Display map
         Main.mapState();
         GalaxyMap.getInstance().mapAction();


      }
   }
}