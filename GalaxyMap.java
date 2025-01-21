// Galaxy map singleton class
public class GalaxyMap {
   // Class instance
   private GalaxyMap instance;
   // Public method to get private instance
   public GalaxyMap getInstance() {
      // Ensure instance is instantiated
      if (instance == null) {
         instance = new GalaxyMap();
      }
      // Return instance
      return instance;
   }
   // Private constructor
   private GalaxyMap() {
      instance = new GalaxyMap();
   }
   // Method to draw the map onscreen
   public void drawMe() {
      // Enter code here ...
   }
}