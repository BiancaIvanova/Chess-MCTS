package project.chess.datastructures;

/**
 * Interface defining a dynamically resizing hash table.
 * See {@link HashingDynamic} for details.
 */

public interface IHashDynamic<K, V> extends Iterable<Element<K, V>>
{
    int hash(K key);
    V item(K key);
    void add(K key, V value);
    void delete(K key);
    boolean contains(K key);
    V[] asArray();
    int length();
    boolean isEmpty();
    Iterable<K> keys();
}
