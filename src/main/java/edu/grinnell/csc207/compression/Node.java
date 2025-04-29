/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.grinnell.csc207.compression;

/**
 *
 * @author jason
 */
 /**
     * Nodes which the Huffman tree uses
     *
     * @param <T>
     */
    public class Node<T, U> implements Comparable<Node> {

        public T key;
        public U value;
        public Node left;
        public Node right;

        /**
         * Constructor for new node
         *
         * @param value to be put in node
         * @param left of the node
         * @param right of the node
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
         * @param value to be put in the node
         */
        public Node(T key, U value) {
            this(key, value, null, null);
        }

        @Override
        /**
         * Return the difference of the two nodes
         */
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