package project.chess.datastructures;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a generic node in an n-ary tree.
 * This class is used in {@link Tree} to build an n-ary tree structure.
 *
 * @param <T> the type of value stored in the node.
 */

public class TreeNode<T>
{
    private T value;
    private TreeNode<T> parent;
    private List<TreeNode<T>> children;

    public TreeNode(T value)
    {
        this.value = value;
        this.children = new ArrayList<>();
    }

    public T getValue() { return value; }

    public TreeNode<T> getParent() { return parent; }

    public void setParent(TreeNode<T> p) { parent = p; }

    public List<TreeNode<T>> getChildren() { return children; }

    public void addChild(TreeNode<T> child)
    {
        child.setParent(this);
        children.add(child);
    }

    public boolean removeChild(TreeNode<T> child)  { return children.remove(child); }

    @Override
    public String toString() { return value.toString(); }

}
