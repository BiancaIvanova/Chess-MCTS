package project.chess.datastructures;

import java.util.ArrayList;
import project.chess.datastructures.LinkedList;
import java.util.Queue;

/**
 * Generic n-ary tree class, using {@link TreeNode}
 *
 * @param <T> the type of values stored in the tree nodes.
 */

/*
Used to generate a tree of all possible moves from the current board state.
 */

public class Tree<T>
{
    private TreeNode<T> root;
    private int length = 0;

    public boolean isEmpty() { return root == null; }

    public void setRoot(T value)
    {
        if (root == null)
        {
            root = new TreeNode<>(value);
            length++;
        }
        else
        {
            throw new IllegalStateException("Root already exists.");
        }
    }

    public TreeNode<T> getRoot() { return root; }

    public void addChild(TreeNode<T> parent, T value)
    {
        if (parent == null)
        {
            throw new IllegalArgumentException("getParent node cannot be null.");
        }

        TreeNode<T> child = new TreeNode<>(value);
        parent.addChild(child);
        length++;
    }

    public void displayPreOrder()
    {
        displayPreOrderRecursive(root);
    }

    private void displayPreOrderRecursive(TreeNode<T> node) {
        if (node == null) return;

        System.out.print(node.getValue() + " ");
        for (TreeNode<T> childVal : node.getChildren().asIterable()) {
            TreeNode<T> child = (TreeNode<T>) childVal;
            displayPreOrderRecursive(child);
        }
    }

    public void displayPostOrder()
    {
        displayPostOrderRecursive(root);
    }

    private void displayPostOrderRecursive(TreeNode<T> node)
    {
        if (node == null) { return; }

        for (TreeNode<T> child : node.getChildren().asIterable())
        {
            displayPostOrderRecursive(child);
        }
        System.out.print(node.getValue() + " ");
    }

    // Level order traversal
    public void displayLevelOrder() {
        if (root == null) return;

        LinkedList<TreeNode<T>> queue = new LinkedList<>();
        queue.append(root);

        while (!queue.isEmpty()) {
            TreeNode<T> node = queue.pop();
            System.out.print(node.getValue() + " ");

            for (TreeNode<T> childVal : node.getChildren().asIterable()) {
                TreeNode<T> child = (TreeNode<T>) childVal;
                queue.append(child);
            }
        }
        System.out.println();
    }

    public T[] asArray()
    {
        LinkedList<T> list = new LinkedList<>();
        flattenTree(root, list);
        return list.asArray();
    }

    private void flattenTree(TreeNode<T> node, LinkedList<T> list)
    {
        if (node == null) { return; }

        list.append(node.getValue());
        for (TreeNode<T> child : node.getChildren().asIterable())
        {
            flattenTree(child, list);
        }
    }

    public void displayTreeStructure(boolean showNulls)
    {
        displayTreeRecursive(root, "", true, showNulls);
    }

    public void displayTreeStructure()
    {
        displayTreeStructure(true);
    }

    private void displayTreeRecursive(TreeNode<T> node, String indent, boolean isLast, boolean showNulls)
    {
        if (node == null) return;

        System.out.println(indent + (isLast ? "└── " : "├── ") + node.getValue());

        String newIndent = indent + (isLast ? "    " : "│   ");
        var children = node.getChildren();

        for (int i = 0; i < children.size(); i++)
        {
            boolean childIsLast = (i == children.size() - 1);
            displayTreeRecursive(children.get(i), newIndent, childIsLast, showNulls);
        }

        if (children.isEmpty() && showNulls)
        {
            System.out.println(newIndent + "└── null");
        }
    }
}
