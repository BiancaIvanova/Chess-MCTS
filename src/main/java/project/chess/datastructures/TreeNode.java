package project.chess.datastructures;

/**
 * Represents a generic node in an n-ary tree using a custom linked list.
 *
 * @param <T> the type of value stored in the node.
 */

public class TreeNode<T>
{
    private T value;
    private TreeNode<T> parent;
    private LinkedList<TreeNode<T>> children;

    public TreeNode(T value)
    {
        this.value = value;
        this.children = new LinkedList<>();
    }

    public T getValue() { return value; }

    public TreeNode<T> getParent() { return parent; }

    public void setParent(TreeNode<T> p) { parent = p; }

    public LinkedList<TreeNode<T>> getChildren() { return children; }

    public void addChild(TreeNode<T> child)
    {
        child.setParent(this);
        children.append(child);
    }

    public boolean removeChild(TreeNode<T> child)
    {
        return children.remove(child);
    }

    @Override
    public String toString() { return value.toString(); }
}
