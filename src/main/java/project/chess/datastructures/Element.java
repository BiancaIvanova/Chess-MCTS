package project.chess.datastructures;

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


