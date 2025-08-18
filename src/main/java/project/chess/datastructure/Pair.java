package project.chess.datastructure;

/**
 * Generic container class that holds a pair of related values.
 * Instances of the {@code Pair} class are immutable.
 *
 * @param <K> the type of the first element.
 * @param <V> the type of the second element.
 */

public class Pair<K, V>
{
    private final K key;
    private final V value;

    public Pair(K key, V value)
    {
        this.key = key;
        this.value = value;
    }

    public K getKey() { return key; }

    public V getValue() { return value; }
}