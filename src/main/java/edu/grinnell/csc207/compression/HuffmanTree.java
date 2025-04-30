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

    /**
     * Constructs a new HuffmanTree from a frequency map.
     *
     * @param freqs a map from 9-bit values to frequencies.
     */
    public HuffmanTree(Map<Short, Integer> freqs) {

        //include EOF with frequency of 1
        freqs.put((short) 256, 1);

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

            Node newNode = new Node(null, (int) first.getKey() + (int) second.getKey(), first, second);

            //add new node to queue
            queue.add(newNode);
        }

        //last item in queue is head of the tree
        head = (Node) queue.pop();

        //generate huffcodes map
        huffMap = new HashMap<>();
        generateHuffmanCodes(head, "");
    }

    /**
     * Constructs a new HuffmanTree from the given file.
     *
     * @param in the input file (as a BitInputStream)
     */
    public HuffmanTree(BitInputStream in) {
        head = buildTreeRecursive(in);

        //generate huffcodes map
        huffMap = new HashMap<>();
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

            //create new nodee with empty value fields
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
            //write left
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
        String currentByteBuffer = "";
        while (in.hasBits()) {
            short read = (short) in.readBit();
            currentByteBuffer += read;

            //write buffer contents to out
            if (huffMap.containsValue(Short.valueOf(currentByteBuffer))) {
                String key = getKeyByValue(huffMap, Short.valueOf(currentByteBuffer));

                out.writeBits(Integer.parseInt(key), key.length()); // ie base 2,

                //clear buffer to restart
                currentByteBuffer = "";
            }
        }
    }

    private void generateHuffmanCodes(Node curr, String currentCode) {
        //is leaf node
        if (curr.left == null && curr.right == null) {
            huffMap.put(currentCode, (short) curr.getKey());
        } //is internal
        else {
            generateHuffmanCodes(curr.left, currentCode + "0");
            generateHuffmanCodes(curr.right, currentCode + "1");
        }
    }

    //Credit:https://stackoverflow.com/a/2904266
    /**
     * Gets the key of a specific value in a 1 to 1 hashmap
     *
     * @param <T>
     * @param <E>
     * @param map
     * @param value
     * @return
     */
    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
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
        String currentByteBuffer = "";

        while (in.hasBits()) {
            short read = (short) in.readBit();
            currentByteBuffer += read;

            //write buffer contents to out
            if (huffMap.containsKey(currentByteBuffer)) {
                out.writeBits(huffMap.get(currentByteBuffer), currentByteBuffer.length());

                //clear buffer to restart
                currentByteBuffer = "";
            }

        }
    }
}
