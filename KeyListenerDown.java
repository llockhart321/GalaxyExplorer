// Class to handle when a movement key or map key is pressed
import javafx.event.*;
import javafx.scene.input.*;
public class KeyListenerDown implements EventHandler<KeyEvent> {
   private static Player p;
   public KeyListenerDown() {
      p = Player.getInstance();
   }
   public void handle(KeyEvent event) {
      if (event.getCode() == KeyCode.D) {
         // Player right
      }
      if (event.getCode() == KeyCode.A) {
         // Player left
      }
      if (event.getCode() == KeyCode.S) {
         // Player down
      }
      if (event.getCode() == KeyCode.D) {
         // Player up
      }
      if (event.getCode() == KeyCode.M) {
         // Display map
      }
   }
}