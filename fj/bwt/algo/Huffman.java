package fj.bwt.algo;

import fj.bwt.bit.BitWriter;
import fj.bwt.bit.BitReader;
import fj.bwt.*;
import java.util.ArrayList;
import java.io.OutputStream;
import java.io.IOException;

/**
 *  Class for dealing with huffman trees (incl. coding and decoding).
 *
 *@author     Frank Jennings fermatjen@yahoo.com
 *@created    March 27, 2004
 */
public class Huffman {

    /**
     *  Inner class representing a tree node.
     *
     *@author     Administrator
     *@created    March 27, 2004
     */
    private class HuffmanNode {

        /**
         *  Holds the value this node stands for. This value is only meaningful
         *  if the node has no children.
         */
        private int _value = 0;
        /**
         *  Holds the weight of this node.
         */
        private long _weight = 0;
        /**
         *  Holds the left child.
         */
        private HuffmanNode _leftChild = null;
        /**
         *  Holds the right child.
         */
        private HuffmanNode _rightChild = null;

        /**
         *  Constructs a new node.
         */
        private HuffmanNode() {
        }

        /**
         *  Constructs a new node with the specified value and weight and no
         *  children.
         *
         *@param  value   the value that the node stands for.
         *@param  weight  the weight of the node.
         */
        private HuffmanNode(int value, long weight) {
            _value = value;
            _weight = weight;
        }

        /**
         *  Combines two nodes to a new node.
         *
         *@param  left   the left child.
         *@param  right  the right child.
         */
        private HuffmanNode(HuffmanNode left, HuffmanNode right) {
            _weight = left._weight + right._weight;
            _leftChild = left;
            _rightChild = right;
        }
    }
    /**
     *  Holds a huffman tree.
     */
    private byte[][] _huffmanCodes = null;
    /**
     *  Remembers the weights.
     */
    private long[] _weights = null;
    /**
     *  Remembers the constructed tree.
     */
    private HuffmanNode _huffmanTree;
    /**
     *  Helper array.
     */
    private static int[] _helper = {(int) 0x8000, 0x4000, 0x2000, 0x1000, 0x0800, 0x0400, 0x0200, 0x0100,
        0x0080, 0x0040, 0x0020, 0x0010, 0x0008, 0x0004, 0x0002, 0x0001};

    /**
     *  Constructs a new <code>Huffman</code> object.
     */
    public Huffman() {
    }

    /**
     *  Helper function for determining the huffman codes.
     *
     *@param  rootNode    the root node of the tree (part) to get the codes of.
     *@param  codeBuffer  the current code (modified recursively).
     *@param  codeLength  the current length of the code (modified recursively).
     */
    private void getTreeCodes(HuffmanNode rootNode, byte[] codeBuffer, int codeLength) {
        if (rootNode._leftChild != null) {
            // if left child is null, right child is also null (due to the tree construction algorithm)
            codeBuffer[codeLength] = 0;
            getTreeCodes(rootNode._leftChild, codeBuffer, codeLength + 1);
            codeBuffer[codeLength] = 1;
            getTreeCodes(rootNode._rightChild, codeBuffer, codeLength + 1);
        } else {
            // --> debug info
            //System.out.println(value);
            // <--
            byte[] code = new byte[codeLength];
            System.arraycopy(codeBuffer, 0, code, 0, codeLength);
            _huffmanCodes[rootNode._value] = code;
        }
    }

    /**
     *  Builds a huffman tree.
     *
     *@param  weights  an array of 256 weights (one for each byte).
     */
    public void buildTree(long[] weights) {
        int i;
        int nodeCount;
        ArrayList nodes = new ArrayList();
        HuffmanNode node1;
        HuffmanNode node2;
        HuffmanNode currentNode;
        long currentWeight;

        // check if there are too many weights
        if (weights.length > 65536) {
            throw new IllegalArgumentException();
        }

        // build the initial nodes and add it to the hashtable
        _huffmanCodes = new byte[weights.length][];
        for (i = 0; i < weights.length; ++i) {
            if (weights[i] > 0) {
                //System.out.println("value [" + this + "]: " + i);
                currentNode = new HuffmanNode(i, weights[i]);
                nodes.add(currentNode);
            }
        }

        // build the tree
        while (nodes.size() > 1) {
            // find the two nodes with the lowest weights
            node1 = null;
            node2 = null;
            for (i = 0; i < nodes.size(); ++i) {
                currentNode = (HuffmanNode) nodes.get(i);
                if (node1 == null) {
                    node1 = currentNode;
                } else if (node2 == null) {
                    node2 = currentNode;
                } else {
                    currentWeight = currentNode._weight;
                    if (currentWeight < node1._weight || currentWeight < node2._weight) {
                        if (node1._weight > node2._weight) {
                            node1 = currentNode;
                        } else {
                            node2 = currentNode;
                        }
                    }
                }
            }
            nodes.remove(node1);
            nodes.remove(node2);
            nodes.add(new HuffmanNode(node1, node2));
        //System.out.println("node added");
        }

        // remember the original weights and the tree
        _huffmanTree = (HuffmanNode) nodes.get(0);
        _weights = weights;

        // construct the codes
        byte[] codeBuffer = new byte[_huffmanCodes.length];
        getTreeCodes(_huffmanTree, codeBuffer, 0);
    }

    /**
     *  Debug print.
     *
     *@return    Description of the Return Value
     */
    public long countBytes() {
        /*
         *  for (int i = 0; i < 512; ++i) {
         *  if (_weights[i] > 0) {
         *  System.out.print("byte: " + i + "(" + _weights[i] + ")\tcode: ");
         *  for (int j = 0; j < _huffmanCodes[i].length; ++j) {
         *  System.out.print(_huffmanCodes[i][j]);
         *  }
         *  System.out.print("\n");
         *  }
         *  }
         */
        long totalLength = 0;
        long numDifferent = 0;
        for (int i = 0; i < _weights.length; ++i) {
            if (_weights[i] > 0) {
                totalLength += _weights[i] * _huffmanCodes[i].length;
                totalLength += 11;
                numDifferent += 1;
            }
        }
        if (totalLength % 8 != 0) {
            totalLength += 8;
        }

        //System.out.println(numDifferent);
        return totalLength / 8;
    }

    /**
     *  Helper method to determine the number of bits needed for the values.
     *  Primitive, but intuitive ...
     *
     *@return    The valueBitCount value
     */
    private int getValueBitCount() {
        if (_huffmanCodes.length > 32768) {
            return 16;
        }
        if (_huffmanCodes.length > 16384) {
            return 15;
        }
        if (_huffmanCodes.length > 8192) {
            return 14;
        }
        if (_huffmanCodes.length > 4096) {
            return 13;
        }
        if (_huffmanCodes.length > 2048) {
            return 12;
        }
        if (_huffmanCodes.length > 1024) {
            return 11;
        }
        if (_huffmanCodes.length > 512) {
            return 10;
        }
        if (_huffmanCodes.length > 256) {
            return 9;
        }
        if (_huffmanCodes.length > 128) {
            return 8;
        }
        if (_huffmanCodes.length > 64) {
            return 7;
        }
        if (_huffmanCodes.length > 32) {
            return 6;
        }
        if (_huffmanCodes.length > 16) {
            return 5;
        }
        if (_huffmanCodes.length > 8) {
            return 4;
        }
        if (_huffmanCodes.length > 4) {
            return 3;
        }
        if (_huffmanCodes.length > 2) {
            return 2;
        }
        return 1;
    }

    /**
     *  Helper method to recursivly write out the tree.
     *
     *@param  output           Description of the Parameter
     *@param  root             Description of the Parameter
     *@exception  IOException  Description of the Exception
     */
    private void writeTreeHelper(BitWriter output, HuffmanNode root) throws IOException {
        if (root._leftChild != null) {
            // right child is also present
            output.write(0);
            writeTreeHelper(output, root._leftChild);
            writeTreeHelper(output, root._rightChild);
        } else {
            output.write(1);
            int bitCount = getValueBitCount();
            byte[] buffer = new byte[bitCount];
            int helperIndex;
            for (helperIndex = 16 - bitCount; helperIndex < 16; ++helperIndex) {
                if ((root._value & _helper[helperIndex]) != 0) {
                    output.write(1);
                } else {
                    output.write(0);
                }
            }
        }
    }

    /**
     *  Writes the tree to the specified <code>BitWriter</code>.
     *
     *@param  output           a <code>BitWriter</code> instance.
     *@exception  IOException  thrown in case of an I/O problem.
     */
    public void writeTree(BitWriter output) throws IOException {
        // write the number of bits needed per value
        int bitCount = getValueBitCount();
        //System.out.println("bitCount: " + bitCount);
        if (bitCount == 16) {
            bitCount = 0;
        }
        int helperIndex;
        for (helperIndex = 16 - 4; helperIndex < 16; ++helperIndex) {
            //System.out.println(_helper[helperIndex]);
            //System.out.println(~_helper[helperIndex]);
            //System.out.println(bitCount & (~_helper[helperIndex]));
            if ((bitCount & _helper[helperIndex]) != 0) {
                output.write(1);
            } else {
                output.write(0);
            }
        }

        // write the tree structure and the values
        writeTreeHelper(output, _huffmanTree);
    }

    /**
     *  Helper function for recursively reading the huffman tree.
     *
     *@param  input            the <code>BitReader</code> to read from.
     *@return                  the <code>HuffmanNode</code> of the partial tree
     *      that is currently read.
     *@exception  IOException  Description of the Exception
     */
    public HuffmanNode readTreeHelper(BitReader input) throws IOException {
        try {
            HuffmanNode result = new HuffmanNode();
            byte leafIndicator = input.read();
            if (leafIndicator == 0) {
                result._leftChild = readTreeHelper(input);
                result._rightChild = readTreeHelper(input);
            } else {
                int bitCount = getValueBitCount();
                int value = 0;
                for (int i = 0; i < bitCount; ++i) {
                    value <<= 1;
                    value |= (int) input.read();
                }
                result._value = value;
            }

            return result;
        } catch (StackOverflowError error) {
            System.out.println("STACK OVER FLOW!");
            System.exit(0);
        }
        return null;
    }

    /**
     *  Reads the tree from the specified <code>BitReader</code>.
     *
     *@param  input            the <code>BitReader</code> to read from.
     *@exception  IOException  thrown if something goes wrong while reading.
     */
    public void readTree(BitReader input) throws IOException {
        // read the number of bits per value
        int bitCount = 0;
        byte temp;
        int i;
        for (i = 0; i < 4; ++i) {
            bitCount <<= 1;
            // not necessary the first time, but doesn't do any harm
            temp = input.read();
            bitCount |= (int) temp;
        // simple cast ist enough, since only 4 Bits are allowed for the count
        }
        int codesCount = 1 << bitCount;
        _huffmanCodes = new byte[codesCount][];

        // build the tree and the codes
        _huffmanTree = readTreeHelper(input);
        byte[] codeBuffer = new byte[_huffmanCodes.length];
        getTreeCodes(_huffmanTree, codeBuffer, 0);
    }

    /**
     *  Writes an encoded input value to the specified <code>BitWriter</code>.
     *
     *@param  index            Description of the Parameter
     *@param  output           the <code>BitWriter</code> to write to.
     *@exception  IOException  thrown if something goes wrong while writing the
     *      code.
     */
    public void encode(int index, BitWriter output) throws IOException {
        output.write(_huffmanCodes[index]);
    }

    /**
     *  Reads an encoded input value from the specified <code>BitReader</code>
     *
     *@param  input            the <code>BitReader</code> to read from.
     *@return                  the value read.
     *@exception  IOException  thrown if something goes wrong while reading the
     *      value.
     */
    public int decode(BitReader input) throws IOException {
        HuffmanNode currentNode = _huffmanTree;
        byte bit;

        // read bits until a value is found
        while (currentNode._leftChild != null) {
            bit = input.read();
            if (bit == 0) {
                currentNode = currentNode._leftChild;
            } else {
                currentNode = currentNode._rightChild;
            }
        }

        return currentNode._value;
    }
}
