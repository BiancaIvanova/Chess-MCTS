package project.chess.datastructures;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a generic node in an n-ary tree.
 * This class is used in {@link Tree} to build an n-ary tree structure.
 *
 * @param <T> the type of value stored in the node.
 */

public class Node<T>
{
    private T value;
    private Node<T> parent;
    private List<Node<T>> children;

    public Node(T value)
    {
        this.value = value;
        this.children = new ArrayList<>();
    }

    public T getValue() { return value; }

    public Node<T> getParent() { return parent; }

    public void setParent(Node<T> p) { parent = p; }

    public List<Node<T>> getChildren() { return children; }

    public void addChild(Node<T> child)
    {
        child.setParent(this);
        children.add(child);
    }

    public boolean removeChild(Node<T> child)  { return children.remove(child); }

    @Override
    public String toString() { return value.toString(); }

}
