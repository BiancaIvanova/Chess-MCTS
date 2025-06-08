package project.chess.datastructures;

public interface IHashDynamic<K, V>
{
    int hash(K key);
    V item(K key);
    void add(K key, V value);
    void delete(K key);
    boolean contains(K key);
    V[] asArray();
    int length();
    boolean isEmpty();
}
