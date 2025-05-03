package edu.grinnell.csc207.compression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * A HuffmanTree derives a space-efficient coding of a collection of byte
 * values.
 *
 * The huffman tree encodes values in the range 0--255 which would normally take
 * 8 bits. However, we also need to encode a special EOF character to denote the
 * end of a .grin file. Thus, we need 9 bits to store each byte value. This is
 * fine for file writing (modulo the need to write in byte chunks to the file),
 * but Java does not have a 9-bit data type. Instead, we use the next larger
 * primitive integral type, short, to store our byte values.
 */
public class HuffmanTree {

    private Node head;
    private Map<String, Short> huffMap;
    private Map<Short, String> reverseHuffMap;

    /**
     * Constructs a new HuffmanTree from a frequency map.
     *
     * @param freqs a map from 9-bit values to frequencies.
     */
    public HuffmanTree(Map<Short, Integer> freqs) {

        PriorityQueue queue = new PriorityQueue();

        //add all data into priority queue
        for (short key : freqs.keySet()) {
            Node newNode = new Node(key, freqs.get(key));
            queue.add(newNode);
        }

        //do until the size of the queue is 1
        while (queue.size() != 1) {
            Node first = (Node) queue.pop();
            Node second = (Node) queue.pop();

            Node newNode = new Node(null, (int) first.getValue() + (int) second.getValue(), first, second);

            //add new node to queue
            queue.add(newNode);
        }

        //last item in queue is head of the tree
        head = (Node) queue.pop();

        //generate huffcodes map
        huffMap = new HashMap<>();
        reverseHuffMap = new HashMap<>();

        generateHuffmanCodes(head, "");
    }

    /**
     * Constructs a new HuffmanTree from the given file.
     *
     * @param in the input file (as a BitInputStream)
     */
    public HuffmanTree(BitInputStream in) {
        head = buildTreeRecursive(in);

        //generate huffcodes maps
        huffMap = new HashMap<>();
        reverseHuffMap = new HashMap<>();
        generateHuffmanCodes(head, "");
    }

    /**
     * Recursive function to build huffman tree from the preorder representation
     * in file
     *
     * @param in the BitInputStream
     * @return a node in a recursive manner
     */
    private Node buildTreeRecursive(BitInputStream in) {
        short newBit = (short) in.readBit();

        //leaf node
        if (newBit == 0) {
            short key = (short) in.readBits(9);

            //create new node with empty value fields
            return new Node(key, null);

        } //not a leaf node
        else {
            Node left = buildTreeRecursive(in);
            Node right = buildTreeRecursive(in);

            //return empty node with left and right
            return new Node(null, null, left, right);
        }
    }

    /**
     * Writes this HuffmanTree to the given file as a stream of bits in a
     * serialized format.
     *
     * @param out the output file as a BitOutputStream
     */
    public void serialize(BitOutputStream out) {
        writeTreeRecursive(out, head);

    }

    private void writeTreeRecursive(BitOutputStream out, Node curr) {
        //is leaf node
        if (curr.left == null && curr.right == null) {
            out.writeBit(0);
            out.writeBits((short) curr.getKey(), 9);
        } else {
            out.writeBit(1);
            writeTreeRecursive(out, curr.left);
            writeTreeRecursive(out, curr.right);
        }
    }

    /**
     * Encodes the file given as a stream of bits into a compressed format using
     * this Huffman tree. The encoded values are written, bit-by-bit to the
     * given BitOuputStream.
     *
     * @param in the file to compress.
     * @param out the file to write the compressed output to.
     */
    public void encode(BitInputStream in, BitOutputStream out) {
        while (in.hasBits()) {
            int read = in.readBits(8);
            //EOF encountered
            if (read == -1) {
                break;
            }
            short character = (short) read;

            // Get Huffman code for symbol
            String code = reverseHuffMap.get(character);
            for (char c : code.toCharArray()) {
                out.writeBit(c == '1' ? 1 : 0);
            }
        }

        // Write EOF marker
        String eofCode = reverseHuffMap.get((short) 256);
        for (char c : eofCode.toCharArray()) {
            out.writeBit(c == '1' ? 1 : 0);
        }
    }

    //Credit:https://stackoverflow.com/a/2904266
    private void generateHuffmanCodes(Node curr, String currentCode) {
        //is leaf node
        if (curr.left == null && curr.right == null) {
            short symbol = (short) curr.getKey();

            huffMap.put(currentCode, symbol);
            reverseHuffMap.put(symbol, currentCode);

        } //is internal
        else {
            generateHuffmanCodes(curr.left, currentCode + "0");
            generateHuffmanCodes(curr.right, currentCode + "1");
        }
    }

    /**
     * Decodes a stream of huffman codes from a file given as a stream of bits
     * into their uncompressed form, saving the results to the given output
     * stream. Note that the EOF character is not written to out because it is
     * not a valid 8-bit chunk (it is 9 bits).
     *
     * @param in the file to decompress.
     * @param out the file to write the decompressed output to.
     */
    public void decode(BitInputStream in, BitOutputStream out) {

        Node current = head;

        while (in.hasBits()) {
            int bit = in.readBit();
            if (bit == -1) {
                break;
            }

            //walk tree and decode
            current = (bit == 0) ? current.left : current.right;

            // Reached a leaf node
            if (current.left == null && current.right == null) {
                short character = (short) current.getKey();

                // EOF check
                if (character == 256) {
                    break;
                }

                // wrtie symbol
                out.writeBits(character, 8);

                // reset
                current = head;
            }
        }
    }
}
