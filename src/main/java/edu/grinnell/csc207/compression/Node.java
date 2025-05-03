package edu.grinnell.csc207.compression;

/**
 * Nodes which the Huffman tree uses
 *
 * @param <T> The key type
 * @param <U> The value type 
 */
public class Node<T, U> implements Comparable<Node> {

    public T key;
    public U value;
    public Node left;
    public Node right;

    /**
     * Constructor for new node
     *
     * @param key the key of the node
     * @param value the value to be put in node
     * @param left left child of the node
     * @param right right child of the node
     */
    public Node(T key, U value, Node left, Node right) {
        this.key = key;
        this.value = value;
        this.left = left;
        this.right = right;
    }

    /**
     * Calls default constructor
     *
     * @param key the key of the node
     * @param value the value to be put in the node
     */
    public Node(T key, U value) {
        this(key, value, null, null);
    }

    /**
     * Return the difference of the two nodes
     *
     * @param other the other Node to compare
     * @return difference of this node's value and the other node's value
     */
    @Override
    public int compareTo(Node other) {
        return ((int) this.value - (int) other.value);
    }

    /**
     * Returns the key from the Node
     *
     * @return key
     */
    public T getKey() {
        return key;
    }

    /**
     * Returns the value from the Node
     *
     * @return value
     */
    public U getValue() {
        return value;
    }
}
