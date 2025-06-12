package project.chess.datastructures;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Tree<T>
{
    private Node<T> root;
    private int length = 0;

    public boolean isEmpty() { return root == null; }

    public void setRoot(T value)
    {
        if (root == null)
        {
            root = new Node<>(value);
            length++;
        }
        else
        {
            throw new IllegalStateException("Root already exists.");
        }
    }

    public Node<T> getRoot() { return root; }

    public void addChild(Node<T> parent, T value)
    {
        if (parent == null)
        {
            throw new IllegalArgumentException("getParent node cannot be null.");
        }

        Node<T> child = new Node<>(value);
        parent.addChild(child);
        length++;
    }

    public void displayPreOrder()
    {
        displayPreOrderRecursive(root);
    }

    private void displayPreOrderRecursive(Node<T> node)
    {
        if (node == null) { return; }

        System.out.print(node.getValue() + " ");
        for (Node<T> child : node.getChildren())
        {
            displayPreOrderRecursive(child);
        }
    }

    public void displayPostOrder()
    {
        displayPostOrderRecursive(root);
    }

    private void displayPostOrderRecursive(Node<T> node)
    {
        if (node == null) { return; }

        for (Node<T> child : node.getChildren())
        {
            displayPostOrderRecursive(child);
        }
        System.out.print(node.getValue() + " ");
    }

    // Level order traversal
    public void displayLevelOrder()
    {
        if (root == null) { return; }

        Queue<Node<T>> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty())
        {
            Node<T> node = queue.poll();
            System.out.print(node.getValue() + " ");
            queue.addAll(node.getChildren());
        }
    }

    public ArrayList<T> asArray()
    {
        ArrayList<T> list = new ArrayList<>();
        flattenTree(root, list);
        return list;
    }

    private void flattenTree(Node<T> node, ArrayList<T> list)
    {
        if (node == null) { return; }

        list.add(node.getValue());
        for (Node<T> child : node.getChildren())
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

    private void displayTreeRecursive(Node<T> node, String indent, boolean isLast, boolean showNulls)
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
