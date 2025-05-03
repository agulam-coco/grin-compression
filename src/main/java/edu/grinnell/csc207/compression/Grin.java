package edu.grinnell.csc207.compression;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The driver for the Grin compression program.
 */
public class Grin {

    public static final int MAGIC_NUMBER = 0x736;
    public static final boolean DEBUG = false;

    /**
     * Decodes the .grin file denoted by infile and writes the output to the
     * .grin file denoted by outfile.
     *
     * @param infile the file to decode
     * @param outfile the file to ouptut to
     * @throws java.io.IOException
     */
    public static void decode(String infile, String outfile) {
        try {
            BitInputStream in = new BitInputStream(infile);
            BitOutputStream out = new BitOutputStream(outfile, DEBUG);

            //read the maigic number from infile
            int magicNumber = in.readBits(Integer.SIZE);

            if (!isMagicNumber(magicNumber)) {
                throw new IllegalArgumentException("File is not valid grin file");
            }

            HuffmanTree tree = new HuffmanTree(in);
            tree.decode(in, out);

            in.close();
            out.close();

        } catch (IOException e) {
            System.out.println("Invlaid file path provided");
            e.printStackTrace();
        }

    }

    /**
     * Checks if inputed number is the GRIN magic number
     *
     * @param magicNumber number to be checked
     * @return true or false
     */
    private static boolean isMagicNumber(int magicNumber) {
        return magicNumber == MAGIC_NUMBER;
    }

    /**
     * Creates a mapping from 8-bit sequences to number-of-occurrences of those
     * sequences in the given file. To do this, read the file using a
     * BitInputStream, consuming 8 bits at a time.
     *
     * @param file the file to read
     * @return a freqency map for the given file
     */
    public static Map<Short, Integer> createFrequencyMap(String file) {
        try {
            Map<Short, Integer> map = new HashMap<>();
            BitInputStream in = new BitInputStream(file);

            while (in.hasBits()) {
                short data = (short) in.readBits(8);

                //get data in file and populate hash map
                map.put(data, map.getOrDefault(data, 0) + 1);

                // Add EOF marker with frequency 1
            }

            //Add EOF
            map.put((short) 256, 1);

            return map;
        } catch (IOException e) {
            System.out.println("Invlaid file path provided");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Encodes the given file denoted by infile and writes the output to the
     * .grin file denoted by outfile.
     *
     * @param infile the file to encode.
     * @param outfile the file to write the output to.
     */
    public static void encode(String infile, String outfile) {
        Map<Short, Integer> map = createFrequencyMap(infile);
        try {
            BitInputStream in = new BitInputStream(infile);
            BitOutputStream out = new BitOutputStream(outfile, DEBUG);

            HuffmanTree tree = new HuffmanTree(map);

            //write magicnumber, serialize then encode payloade
            out.writeBits(MAGIC_NUMBER, 32);
            tree.serialize(out);
            tree.encode(in, out);

            in.close();
            out.close();

        } catch (IOException e) {
            System.out.println("Invlaid file path provided");
            e.printStackTrace();

        }
    }

    /**
     * Entry point for the Grin program.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException(
"Invalid number of arguments. Usage: java Grin <encode|decode> <infile> <outfile>"
            );
        }

        String command = args[0];
        String infile = args[1];
        String outfile = args[2];

        switch (command.toLowerCase()) {
            case "encode" ->
                encode(infile, outfile);
            case "decode" ->
                decode(infile, outfile);
            default ->
                throw new IllegalArgumentException(
                        "Invalid command '" + command + "'. Expected 'encode' or 'decode'."
                );
        }
    }

}
