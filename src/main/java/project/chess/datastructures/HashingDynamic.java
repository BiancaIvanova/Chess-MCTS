package project.chess.datastructures;

import java.util.Iterator;
import java.util.function.Function;

/**
 * A dynamically resizing hash table.
 *
 * @details
 * - Uses {@link Element} objects to store key-value pairs and {@code deleted} flags
 * - Implements {@link IHashDynamic} and {@link Iterable}
 * - Supports custom hash functions via {@link #setCustomHash(Function)}, else defaults
 *   to Fibonacci hashing
 *
 * @param <K> The type of keys.
 * @param <V> The type of mapped values.
 */

public class HashingDynamic<K, V> implements IHashDynamic<K, V>, Iterable<Element<K, V>>
{
    // Thresholds for resizing the hashtable
    private static final double LOAD_FACTOR_THRESHOLD = 0.75;
    private static final double SHRINK_THRESHOLD = 0.25;
    private static final int INITIAL_SIZE = 11;

    // Keeps track of the current size and capacity of the hashtable
    private int size = 0;
    private int currentCapacity = INITIAL_SIZE;

    // Defines any custom hash functions used
    private Function<K, Integer> customHashFunction = null;
    private boolean customHash = false;

    // Array of Element
    @SuppressWarnings("unchecked")
    private Element<K, V>[] table = new Element[INITIAL_SIZE];

    // Hashes the key with the specified hash function

    /**
     * Hashes the key with the specified hash function.
     *
     * @param key The key to hash.
     * @return A non-negative integer representing the hashed index within the current capacity.
     */
    @Override
    public int hash(K key)
    {
        if (customHash)
        {
            // Use custom hash function if it has been set
            return customHashFunction.apply(key) % currentCapacity;
        }
        else
        {
            // Else use the following default hash function
            double A = (Math.sqrt(5) - 1) / 2;      // Golden ratio constant

            int hashCode = key.hashCode();
            int positiveHash = hashCode & 0x7FFFFFFF;

            double result = positiveHash * A;                // Multiply key by A

            // Use fractional part and scale by table size
            return (int) (currentCapacity * (result - Math.floor(result)));
        }
    }

    /**
     * Sets a custom hash function.
     *
     * @param hashFunction A function that maps keys of type {@code K} to hash codes.
     * @throws IllegalArgumentException If {@code hashFunction} is {@code null}.
     */
    public void setCustomHash(Function<K, Integer> hashFunction)
    {
        if (hashFunction == null)
        {
            throw new IllegalArgumentException("Specified hash function cannot be null");
        }
        this.customHashFunction = hashFunction;
        this.customHash = true;
    }

    /**
     * Overloaded method that resets the hash function to the default implementation.
     */
    public void setCustomHash() { this.customHash = false; }

    /**
     * Returns the value found at a specific key.
     *
     * @param key The key whose associated value is to be returned.
     * @return The value associated with the specified key.
     * @throws IllegalArgumentException If the key is not found in the table.
     */
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

    /**
     * Adds a new item to the hashtable, and resizes if necessary.
     *
     * @param key The key associated with the value.
     * @param value The value to be added to the table.
     * @throws NullPointerException If the value passed is {@code null}.
     * @throws IllegalArgumentException If the key already exists in the table.
     * @throws UnsupportedOperationException If the table is full and cannot insert.
     */
    @Override
    public void add(K key, V value)
    {
        // Validate the input value and check if the value is null
        if (value == null)
        {
            throw new NullPointerException("getValue cannot be null");
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

    /**
     * Deletes the requested item from the hashtable, and downsizes if necessary.
     *
     * @param key The key to be deleted.
     * @throws IllegalArgumentException If the key is not found in the table.
     */
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

    /**
     * Verifies if the requested item is present in the hashtable.
     *
     * @param key The key to be located.
     * @return A boolean value indicating whether the requested key is found in the hashtable.
     */
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

    /**
     * Returns an iterator of Element
     */
    @Override
    public Iterator<Element<K, V>> iterator()
    {
        return new Iterator<Element<K, V>>()
        {
            private int index = 0;

            @Override
            public boolean hasNext()
            {
                while (index < currentCapacity && (table[index] == null || table[index].Deleted()))
                {
                    index++;
                }
                return index < currentCapacity;
            }

            @Override
            public Element<K, V> next()
            {
                return table[index++];
            }
        };
    }

    @Override
    public Iterable<K> keys()
    {
        // The boilerplate Iterator implementation was found here:
        // https://www.geeksforgeeks.org/java-implementing-iterator-and-iterable-interface/
        return new Iterable<K>() {
            @Override
            public Iterator<K> iterator() {
                return new Iterator<K>() {
                    private int index = 0;

                    @Override
                    public boolean hasNext() {
                        while (index < currentCapacity && (table[index] == null || table[index].Deleted())) {
                            index++;
                        }
                        return index < currentCapacity;
                    }

                    @Override
                    public K next() {
                        return table[index++].Key();
                    }
                };
            }
        };
    }

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

    /**
     * Copies over all the elements into a new size of hashtable.
     *
     * @param newCapacity The new capacity of the hashtable, as an integer representing the number of items.
     */
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

    @Override
    public int length() { return size; }

    @Override
    public boolean isEmpty() { return size == 0; }
}
