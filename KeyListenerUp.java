import javafx.event.*;
import javafx.scene.input.*;
public class KeyListenerUp implements EventHandler<KeyEvent> {
   private static PlayerMovementState pms;
   public KeyListenerUp() {
      pms = PlayerMovementState.getInstance();
   }
   public void handle(KeyEvent event) {
      if (event.getCode() == KeyCode.D) {
         pms.stopRight();
      }
      if (event.getCode() == KeyCode.A) {
         pms.stopLeft();
      }
      if (event.getCode() == KeyCode.S) {
         pms.stopDown();
      }
      if (event.getCode() == KeyCode.W) {
         pms.stopUp();
      }
   }
}