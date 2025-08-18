package project.chess.datastructure;

/**
 * Interface defining a dynamically resizing hash table.
 * See {@link HashTable} for details.
 */

public interface IHashTable<K, V> extends Iterable<Element<K, V>>
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
