import java.util.*;
// Concrete Aggregate
public class Galaxy implements Aggregate<StarSystem> {
    private List<StarSystem> systems;

    public Galaxy(List<StarSystem> systems) {
        this.systems = systems;
    }

    @Override
    public Iterator<StarSystem> createIterator() {
        return new StarSystemIterator(systems);
    }
}