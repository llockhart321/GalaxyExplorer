import java.util.*;
// Star system iterator
class StarSystemIterator implements Iterator<StarSystem> {
    private int currentIndex = 0;
    private List<StarSystem> systems;
   
    public StarSystemIterator(List<StarSystem> systems) {
        this.systems = systems;
    }

    @Override
    public boolean hasNext() {
        return currentIndex < systems.size();
    }

    @Override
    public StarSystem next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return systems.get(currentIndex++);
    }
}
