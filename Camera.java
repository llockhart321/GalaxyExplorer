public class Camera {
   private static Camera instance;
   private StarSystem ss;
   private Player p;
   public static Camera getInstance(StarSystem ss) {
      if (instance == null) {
         instance = new Camera(ss);
      }
      return instance;
   }
   private Camera(StarSystem ss) {
      this.ss = ss;
      p = Player.getInstance();
   }
   
}