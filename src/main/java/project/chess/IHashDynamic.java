package project.chess;

public interface IHashDynamic<K, V>
{
    int hash(K key);
    void add(K key, V value);
    void delete(K key);
    V item(K key);
    boolean contains(K key);
    int length();
    boolean isEmpty();    
}
