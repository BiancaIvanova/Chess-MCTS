package project.chess.datastructures;

import java.util.ArrayList;
import java.util.List;

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

    public T Value() { return value; }

    public Node<T> Parent() { return parent; }

    public void Parent(Node<T> p) { parent = p; }

    public List<Node<T>> Children() { return children; }

    public void AddChild(Node<T> child)
    {
        child.Parent(this);
        children.add(child);
    }

    public boolean RemoveChild(Node<T> child)  { return children.remove(child); }

    @Override
    public String toString() { return value.toString(); }

}
