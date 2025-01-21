// Aggregate interface
interface Aggregate<T> {
    Iterator<T> createIterator();
}