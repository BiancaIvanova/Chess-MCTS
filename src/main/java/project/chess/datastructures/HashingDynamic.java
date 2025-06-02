package project.chess.datastructures;

public class HashingDynamic<K, V> implements IHashDynamic<K, V>
{
    // Thresholds for resizing the hashtable
    private static final double LOAD_FACTOR_THRESHOLD = 0.75;
    private static final double SHRINK_THRESHOLD = 0.25;
    private static final int INITIAL_SIZE = 11;

    // Keeps track of the current size and capacity of the hashtable
    private int size = 0;
    private int currentCapacity = INITIAL_SIZE;

    // Array of Element
    @SuppressWarnings("unchecked")
    private Element<K, V>[] table = new Element[INITIAL_SIZE];

    public V[] asArray()
    {
        @SuppressWarnings("unchecked")
        V[] result = (V[]) new Object[size]; // not type-safe, but is common practice

        int index = 0;

        for (Element<K, V> element : table)
        {
            if (element != null && !element.Deleted())
            {
                result[index++] = element.Value();
            }
        }

        return result;
    }

    private void resize(int newCapacity)
    {
        Element<K, V>[] oldTable = table;
        currentCapacity = newCapacity;

        // Reset the contents of the table
        @SuppressWarnings("unchecked")
        Element<K, V>[] newTable = (Element<K, V>[]) new Element[newCapacity];
        table = newTable;
        size = 0;

        // Copy all the elements into the new, bigger table
        for (Element<K, V> element : oldTable)
        {
            if (element != null && !element.Deleted())
            {
                add(element.Key(), element.Value());
            }
        }

    }

    public int hash(K key)
    {
        double A = (Math.sqrt(5) - 1) / 2;      // Golden ratio constant

        int hashCode = key.hashCode();
        int positiveHash = hashCode & 0x7FFFFFFF;

        double result = positiveHash * A;                // Multiply key by A

        // Use fractional part and scale by table size
        return (int) (currentCapacity * (result - Math.floor(result)));
    }

    @Override
    public void add(K key, V value)
    {
        // Validate the input value and check if the value is null
        if (value == null)
        {
            throw new NullPointerException("Value cannot be null");
        }

        // Resize if the load factor is above the threshold
        if ((double) size / currentCapacity >= LOAD_FACTOR_THRESHOLD)
        {
            resize(currentCapacity * 2);
        }

        // Hash the key using the hash function
        int hashedKey = hash(key);

        int originalHash = hashedKey;

        while (table[hashedKey] != null && !table[hashedKey].Deleted())
        {
            // Check if the key has already been added
            if (key.equals(table[hashedKey].Key()))
            {
                throw new IllegalArgumentException("Key already added");
            }

            // Linear search to find the next empty slot
            hashedKey = (hashedKey + 1) % currentCapacity;

            // Checks if the hashtable is full
            if (hashedKey == originalHash)
            {
                throw new UnsupportedOperationException("Hash table is full");
            }
        }

        table[hashedKey] = new Element<K, V>(key, value);
        size++;
    }

    @Override
    public V item(K key)
    {
        // Hash the key using the hash function
        int hashedKey = hash(key);
        int originalHash = hashedKey;
        V output = null;
        boolean found = false;

        while (table[hashedKey] != null && !found)
        {
            // Check if the current location is empty
            if (!table[hashedKey].Deleted() && key.equals(table[hashedKey].Key()))
            {
                output = table[hashedKey].Value();
                found = true;
            }
            else
            {
                hashedKey = (hashedKey + 1) % currentCapacity;

                // Stop if we have looped back to the start, hence the array is full
                if (hashedKey == originalHash)
                {
                    break;
                }
            }
        }

        // If the key wasn't found after linear search
        if (!found)
        {
            throw new IllegalArgumentException("Key not found: " + key);
        }

        return output;
    }

    @Override
    public void delete(K key)
    {
        // Hash the key using the hash function
        int hashedKey = hash(key);
        int originalHash = hashedKey;

        while (table[hashedKey] != null)
        {
            if (!table[hashedKey].Deleted() && key.equals(table[hashedKey].Key()))
            {
                table[hashedKey].SetDeleted(true);
                size--;

                // Shrink the table if the load factor falls below the threshold
                if ((double) size / currentCapacity <= SHRINK_THRESHOLD && currentCapacity > INITIAL_SIZE) {
                    resize(currentCapacity / 2);
                }
                return;
            }

            hashedKey = (hashedKey + 1) % currentCapacity;

            if (hashedKey == originalHash)
            {
                break;
            }
        }

        throw new IllegalArgumentException("Key not found: " + key);
    }

    @Override
    public boolean contains(K key)
    {
        // Hash the key using the hash function
        int hashedKey = hash(key);
        int originalHash = hashedKey;

        while (table[hashedKey] != null)
        {
            if (!table[hashedKey].Deleted() && table[hashedKey].Key() == key)
            {
                return true;
            }

            hashedKey = (hashedKey + 1) % currentCapacity;

            if (hashedKey == originalHash)
            {
                // Return false if the key has not been found in the hash table
                return false;
            }
        }
        return false;
    }

    @Override
    public int length() { return size; }

    @Override
    public boolean isEmpty() { return size == 0; }
}
