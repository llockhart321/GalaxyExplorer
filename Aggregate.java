// Aggregate interface
import java.util.Iterator;

interface Aggregate<T> {
    Iterator<T> createIterator();
}