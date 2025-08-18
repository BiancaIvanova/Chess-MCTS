package project.chess.datastructure;

/**
 * Represents a node in a doubly linked list.
 * @param <T> The type of value stored in this node.
 */

public class ListNode<T>
{
    private T value;
    private ListNode<T> previous;
    private ListNode<T> next;

    public ListNode(T value, ListNode<T> previous, ListNode<T> next)
    {
        this.value = value;
        this.previous = previous;
        this.next = next;
    }

    public T getValue() { return value; }

    public void setValue(T value) { this.value = value; }

    public ListNode<T> getPrevious() { return previous; }

    public void setPrevious(ListNode<T> previous) { this.previous = previous; }

    public ListNode<T> getNext() { return next; }

    public void setNext(ListNode<T> next) { this.next = next; }

    @Override
    public String toString() {
        return value.toString();
    }
}
