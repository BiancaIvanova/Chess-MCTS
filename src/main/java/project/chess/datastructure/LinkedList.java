package project.chess.datastructure;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

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

    public void append(T value)
    {
        ListNode<T> newNode = new ListNode<>(value, tail, null);

        if (tail != null) { tail.setNext(newNode); }
        else { head = newNode; }

        tail = newNode;
        size++;
    }

    public boolean remove(T value)
    {
        ListNode<T> current = head;

        while (current != null)
        {
            if (current.getValue().equals(value))
            {
                if (current.getPrevious() != null) current.getPrevious().setNext(current.getNext());
                else head = current.getNext();

                if (current.getNext() != null) current.getNext().setPrevious(current.getPrevious());
                else tail = current.getPrevious();

                size--;
                return true;
            }

            current = current.getNext();
        }

        return false;
    }

    public T pop()
    {
        if (head == null) throw new IllegalStateException("Cannot pop from empty list");

        T value = head.getValue();
        head = head.getNext();

        if (head != null) head.setPrevious(null);
        else tail = null;

        size--;

        return value;
    }

    public T pop(int index)
    {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();

        ListNode<T> current = head;
        for (int i = 0; i < index; i++)
        {
            current = current.getNext();
        }

        if (current.getPrevious() != null) current.getPrevious().setNext(current.getNext());
        else head = current.getNext();

        if (current.getNext() != null) current.getNext().setPrevious(current.getPrevious());
        else tail = current.getPrevious();

        size--;
        return current.getValue();
    }

    public void insert(T value, int index)
    {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();

        if (index == 0)
        {
            ListNode<T> newNode = new ListNode<>(value, null, head);
            if (head != null) head.setPrevious(newNode);
            head = newNode;
            if (tail == null) tail = newNode;
        }
        else if (index == size - 1)
        {
            append(value);
            return;
        }
        else
        {
            ListNode<T> current = head;

            for (int i = 0; i < index; i++)
            {
                current = current.getNext();
            }

            ListNode<T> newNode = new ListNode<>(value, current.getPrevious(), current);
            current.getPrevious().setNext(newNode);
            current.setPrevious(newNode);
        }

        size++;
    }

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

    public T get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        ListNode<T> current = head;

        for (int i = 0; i < index; i++) {
            current = current.getNext();
        }

        return current.getValue();
    }

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
