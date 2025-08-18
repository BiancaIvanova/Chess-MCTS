package project.chess.datastructure;

/**
 * Generic n-ary tree class, using {@link TreeNode}
 * @param <T> The type of values stored in the tree nodes.
 */

public class Tree<T>
{
    private TreeNode<T> root;
    // Total number of nodes currently in the tree
    private int length = 0;

    public boolean isEmpty() { return root == null; }

    /**
     * Sets the root node of the tree.
     * @param value The value to store in the root node.
     * @throws IllegalStateException If the tree already has a root node.
     */
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

    /**
     * Adds a child node with the given value.
     * @param parent The parent node to add the child to.
     * @param value The value for the new child node.
     * @throws IllegalArgumentException If the parent node is null.
     */
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

    /**
     * Displays the tree using pre-order traversal.
     */
    public void displayPreOrder()
    {
        displayPreOrderRecursive(root);
    }

    private void displayPreOrderRecursive(TreeNode<T> node) {
        if (node == null) return;

        // Process the root node
        System.out.print(node.getValue() + " ");

        // Recursively process all children
        for (TreeNode<T> childVal : node.getChildren().asIterable()) {
            TreeNode<T> child = (TreeNode<T>) childVal;
            displayPreOrderRecursive(child);
        }
    }

    /**
     * Displays the tree using post-order traversal.
     */
    public void displayPostOrder()
    {
        displayPostOrderRecursive(root);
    }

    private void displayPostOrderRecursive(TreeNode<T> node)
    {
        if (node == null) { return; }

        // Recursively process all children
        for (TreeNode<T> child : node.getChildren().asIterable())
        {
            displayPostOrderRecursive(child);
        }

        // Process the root node
        System.out.print(node.getValue() + " ");
    }

    /**
     * Displays the tree using breadth-first traversal.
     */
    public void displayLevelOrder() {
        if (root == null) return;

        LinkedList<TreeNode<T>> queue = new LinkedList<>();
        queue.append(root);

        while (!queue.isEmpty()) {
            TreeNode<T> node = queue.pop();
            System.out.print(node.getValue() + " ");

            // Add all children to queue
            for (TreeNode<T> child : node.getChildren().asIterable()) {
                queue.append(child);
            }
        }
        System.out.println();
    }

    /**
     * Converts the tree to an array using pre-order traversal.
     * @return An array containing all node values.
     */
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

    /**
     * Displays the tree structure with visual formatting.
     * @param showNulls whether to show null markers for leaf nodes
     */
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

        // Print the current node with a tree branch
        System.out.println(indent + (isLast ? "└── " : "├── ") + node.getValue());

        // Ensure chilren are aligned correctly
        String newIndent = indent + (isLast ? "    " : "│   ");
        var children = node.getChildren();

        // Recursively display all children
        for (int i = 0; i < children.size(); i++)
        {
            boolean childIsLast = (i == children.size() - 1);
            displayTreeRecursive(children.get(i), newIndent, childIsLast, showNulls);
        }

        // If showNulls is true, print a null marker for leaf nodes
        if (children.isEmpty() && showNulls)
        {
            System.out.println(newIndent + "└── null");
        }
    }
}
