package project.chess.datastructure;

/**
 * Represents a generic key-value pair stored in a hash table.
 * This class is used in {@link HashTable}.
 *
 * @param <K> the type of the key.
 * @param <V> the type of the value.
 */

class Element<K, V>
{
    private K key;
    private V value;
    private boolean deleted;

    public Element(K key, V value)
    {
        this.key = key;
        this.value = value;
        this.deleted = false;
    }

    public K Key() { return this.key; }

    public V Value() { return this.value; }

    public boolean Deleted() { return this.deleted; }

    public void SetDeleted(boolean status) { this.deleted = status; };
}


