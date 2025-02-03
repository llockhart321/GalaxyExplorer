import javafx.application.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.*;


public class Main extends Application {
   public void start(Stage primaryStage) {
      // Create a FlowPane
      FlowPane root = new FlowPane();
      
      // Create a Canvas and GraphicsContext
      MovingCanvas c = new MovingCanvas();
      root.getChildren().add(c);
      GraphicsContext gc = c.getGraphicsContext2D();
         
      // Create a scene
      Scene scene = new Scene(root, 800, 450, Color.BLACK);
   
      // Set the title
      primaryStage.setTitle("GalExplorer");
      
      // Key Listeners
      scene.setOnKeyPressed(new KeyListenerDown());
      scene.setOnKeyReleased(new KeyListenerUp());
      scene.setOnMouseClicked(event -> {
          MissileSystem.getInstance().shoot(event.getX(), event.getY());
      });
      
      //set player start system
      StarSystem startSystem = new StarSystem(gc, -1);
      StarSystemCache.getInstance().add(startSystem);

      
      
      
      // Set up Animation
      AnimationHandler ah = new AnimationHandler(gc);
      
      //get map ready
      GalaxyMap.getInstance().set(gc, ah);
      Player.getInstance().setSystem(startSystem);
      
      ah.start();
   
      // Set the scene and display the stage
      primaryStage.setScene(scene);
      primaryStage.show();
   }

   public static void main(String[] args) {
      launch(args);
   }
}
