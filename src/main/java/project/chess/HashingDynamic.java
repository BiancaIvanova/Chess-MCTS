package hashing;

public class HashingDynamic implements IHashDynamic
{
    // Thresholds for resizing the hashtable
    private static final double LOAD_FACTOR_THRESHOLD = 0.75;
    private static final double SHRINK_THRESHOLD = 0.25;
    private static final int INITIAL_SIZE = 11;

    // Keeps track of the current size and capacity of the hashtable
    private int size = 0;
    private int currentCapacity = INITIAL_SIZE;

    // Array of Element
    private Element[] table = new Element[INITIAL_SIZE];

    public static void main(String[] args)
    {
        HashingDynamic hashTable = new HashingDynamic();
    }

    public String[] asArray()
    {
        String[] result = new String[INITIAL_SIZE];

        for (int i = 0; i < INITIAL_SIZE; i++)
        {
            if (table[i] != null && !table[i].Deleted())
            {
                result[i] = table[i].Value();
            }
            else
            {
                result[i] = null;
            }
        }

        return result;
    }

    private void resize(int newCapacity)
    {
        Element[] oldTable = table;
        currentCapacity = newCapacity;

        // Reset the contents of the table
        table = new Element[newCapacity];
        size = 0;

        // Copy all the elements into the new, bigger table
        for (Element element : oldTable)
        {
            if (element != null && !element.Deleted())
            {
                add(element.Key(), element.Value());
            }
        }

    }

    public int hash(int key)
    {
        double A = (Math.sqrt(5) - 1) / 2;      // Golden ratio constant
        double result = key * A;                // Multiply key by A
        // Use fractional part and scale by table size
        return (int) (currentCapacity * (result - Math.floor(result)));
    }

    @Override
    public void add(int Key, String Value)
    {
        // Validate the input value, and check if the value is null
        if (Value == null) {
            throw new NullPointerException("Value cannot be null");
        }

        // Resize if the load factor is above the threshold
        if ((double) size / currentCapacity >= LOAD_FACTOR_THRESHOLD)
        {
            resize(currentCapacity * 2);
        }

        // Hash the key using the hash function
        int hashedKey = hash(Key);

        int originalHash = hashedKey;

        while (table[hashedKey] != null && !table[hashedKey].Deleted())
        {
            // Check if key has already been added
            if (Key == table[hashedKey].Key())
            {
                throw new IllegalArgumentException("Key already added");
            }

            // Linear search to find the next empty slot
            hashedKey = (hashedKey + 1) % INITIAL_SIZE;

            // Checks if hashtable is full
            if (hashedKey == originalHash)
            {
                throw new UnsupportedOperationException("Hash table is full");
            }
        }

        table[hashedKey] = new Element(Key, Value);
        size++;
    }

    @Override
    public String item(int Key)
    {
        // Hash the key using the hash function
        int hashedKey = hash(Key);
        int originalHash = hashedKey;
        String output = null;
        boolean found = false;

        while (table[hashedKey] != null && !found)
        {
            // Check if the current location is empty
            if (!table[hashedKey].Deleted() && table[hashedKey].Key() == Key)
            {
                output = table[hashedKey].Value();
                found = true;
            }
            else
            {
                hashedKey = (hashedKey + 1) % INITIAL_SIZE;

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
            throw new IllegalArgumentException("Key not found: " + Key);
        }

        return output;
    }

    @Override
    public void delete(int Key)
    {
        // Hash the key using the hash function
        int hashedKey = hash(Key);
        int originalHash = hashedKey;

        while (table[hashedKey] != null)
        {
            if (!table[hashedKey].Deleted() && table[hashedKey].Key() == Key)
            {
                table[hashedKey].SetDeleted(true);
                size--;

                // Shrink the table if the load factor falls below the threshold
                if ((double) size / currentCapacity <= SHRINK_THRESHOLD && currentCapacity > INITIAL_SIZE) {
                    resize(currentCapacity / 2);
                }
                return;
            }

            hashedKey = (hashedKey + 1) % INITIAL_SIZE;

            if (hashedKey == originalHash)
            {
                break;
            }
        }

        throw new IllegalArgumentException("Key not found: " + Key);
    }

    @Override
    public boolean contains(int Key)
    {
        // Hash the key using the hash function
        int hashedKey = hash(Key);
        int originalHash = hashedKey;

        while (table[hashedKey] != null)
        {
            if (!table[hashedKey].Deleted() && table[hashedKey].Key() == Key)
            {
                return true;
            }

            hashedKey = (hashedKey + 1) % INITIAL_SIZE;

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
