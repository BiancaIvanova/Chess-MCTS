package project.chess.datastructure;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * A generic doubly linked list implementation using {@link ListNode}
 * @param <T> the type of elements stored in the list
 */
public class LinkedList<T>
{
    private ListNode<T> head;
    private ListNode<T> tail;
    private int size;

    public LinkedList()
    {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public boolean isEmpty() { return this.size == 0; }

    public int size() { return size; }

    /**
     * Appends a value to the end of the list.
     * @param value the value to append
     */
    public void append(T value)
    {
        ListNode<T> newNode = new ListNode<>(value, tail, null);

        // If the list is empty, head and tail are the same node
        if (tail != null) { tail.setNext(newNode); }
        else { head = newNode; }

        tail = newNode;
        size++;
    }

    /**
     * Removes the first occurrence of the specified value.
     * @param value The value to remove.
     * @return True if the value was found and removed, false otherwise.
     */
    public boolean remove(T value)
    {
        ListNode<T> current = head;

        while (current != null)
        {
            if (current.getValue().equals(value))
            {
                // Update the previous node's next pointer (or head if no previous)
                if (current.getPrevious() != null) current.getPrevious().setNext(current.getNext());
                else head = current.getNext();

                // Update the next node's previous pointer (or tail if no next)
                if (current.getNext() != null) current.getNext().setPrevious(current.getPrevious());
                else tail = current.getPrevious();

                size--;
                return true;
            }

            current = current.getNext();
        }

        return false;
    }

    /**
     * Removes and returns the first element (from the head).
     * @return The value of the removed element.
     * @throws IllegalStateException If the list is empty.
     */
    public T pop()
    {
        if (head == null) throw new IllegalStateException("Cannot pop from empty list");

        T value = head.getValue();
        head = head.getNext();

        if (head != null) head.setPrevious(null);
        else tail = null; // List is now empty

        size--;

        return value;
    }

    /**
     * Removes and returns the element at the specified index.
     * @param index The index of the element to remove.
     * @return The value of the removed element.
     * @throws IndexOutOfBoundsException If the index is out of range.
     */
    public T pop(int index)
    {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();

        // Traverse to the node at the specified index
        ListNode<T> current = head;
        for (int i = 0; i < index; i++)
        {
            current = current.getNext();
        }

        // Update the previous node's next pointer (or head if no previous)
        if (current.getPrevious() != null) current.getPrevious().setNext(current.getNext());
        else head = current.getNext();

        // Update the next node's previous pointer (or tail if no next)
        if (current.getNext() != null) current.getNext().setPrevious(current.getPrevious());
        else tail = current.getPrevious();

        size--;
        return current.getValue();
    }

    /**
     * Inserts a value at the specified index.
     * @param value The value to insert.
     * @param index The position to insert at.
     * @throws IndexOutOfBoundsException If index is out of range.
     */
    public void insert(T value, int index)
    {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();

        if (index == 0)
        {
            // Insert at the head
            ListNode<T> newNode = new ListNode<>(value, null, head);
            if (head != null) head.setPrevious(newNode);
            head = newNode;
            if (tail == null) tail = newNode;
        }
        else if (index == size - 1)
        {
            // Insert at the tail
            append(value);
            return;
        }
        else
        {
            // Insert in the middle
            ListNode<T> current = head;

            for (int i = 0; i < index; i++)
            {
                current = current.getNext();
            }

            // Insert the new node before the current one
            ListNode<T> newNode = new ListNode<>(value, current.getPrevious(), current);
            current.getPrevious().setNext(newNode);
            current.setPrevious(newNode);
        }

        size++;
    }

    /**
     * Returns the index of the first occurrence of the specified value.
     * @param value The value to search for.
     * @return The index of the value.
     * @throws IllegalArgumentException If value is not found.
     */
    public int index(T value)
    {
        ListNode<T> current = head;
        int index = 0;

        while (current != null)
        {
            if (current.getValue().equals(value)) return index;
            current = current.getNext();
            index++;
        }

        throw new IllegalArgumentException("Value not found");
    }

    /**
     * Returns the element at the specified index.
     * @param index The index of the element to retrieve.
     * @return The value at the specified index.
     * @throws IndexOutOfBoundsException If the index is out of range.
     */
    public T get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        ListNode<T> current = head;

        for (int i = 0; i < index; i++) {
            current = current.getNext();
        }

        return current.getValue();
    }

    /**
     * Converts the list to an array.
     * @return an array containing all elements in order
     */
    @SuppressWarnings("unchecked")
    public T[] asArray()
    {
        T[] array = (T[]) new Object[size];
        ListNode<T> current = head;

        int i = 0;
        while (current != null)
        {
            array[i++] = current.getValue();
            current = current.getNext();
        }

        return array;
    }

    /**
     * Returns an iterable for use in enhanced for-loops.
     * @return an Iterable that traverses the list from head to tail.
     */
    public Iterable<T> asIterable() {
        return new Iterable<T>() {
            @Override
            public @NotNull Iterator<T> iterator() {
                return new Iterator<T>() {
                    private ListNode<T> current = head;

                    @Override
                    public boolean hasNext() {
                        return current != null;
                    }

                    @Override
                    public T next() {
                        T val = current.getValue();
                        current = current.getNext();
                        return val;
                    }
                };
            }
        };
    }
}
