package fj.bwt.algo;

import java.io.*;
import java.util.ArrayList;

/**
 *  Description of the Class
 *
 *@author     Frank Jennings fermatjen@yahoo.com
 *@created    March 27, 2004
 */
public class RLE {

    /**
     *  Description of the Class
     *
     *@author     Administrator
     *@created    March 27, 2004
     */
    private static class Block {

        private int _length;
        private byte _type;
        private byte _value;

        /**
         *  Constructor for the Block object
         *
         *@param  length  Description of the Parameter
         *@param  type    Description of the Parameter
         *@param  value   Description of the Parameter
         */
        private Block(int length, byte type, byte value) {
            _length = length;
            _type = type;
            _value = value;
        }
    }

    /**
     *  Description of the Method
     *
     *@param  input  Description of the Parameter
     *@return        Description of the Return Value
     */
    public static byte[] encode(byte[] input) {
        ArrayList blocks = new ArrayList();
        int size = 0;
        int numSame = 0;
        byte currentByte = 0;
        boolean rleFound;
        int rleIndex = 0;
        int i;
        int j;
        int k;
        int x;
        Block block;

        for (i = 0; i < input.length;) {
            rleFound = false;
            for (j = i; j <= input.length; ++j) {
                if (j == i) {
                    currentByte = input[j];
                    numSame = 1;
                } else if (j == input.length) {
                    if (numSame >= 100) {
                        rleIndex = j - numSame;
                        rleFound = true;
                    }
                } else {
                    if (input[j] == currentByte) {
                        ++numSame;
                    } else {
                        if (numSame >= 100) {
                            rleIndex = j - numSame;
                            rleFound = true;
                            break;
                        } else {
                            currentByte = input[j];
                            numSame = 1;
                        }
                    }
                }
            }

            if (rleFound) {
                if (rleIndex == i) {
                    blocks.add(new Block(numSame, (byte) 1, currentByte));
                    size += 6;
                } else {
                    blocks.add(new Block(rleIndex - i, (byte) 0, (byte) 0));
                    blocks.add(new Block(numSame, (byte) 1, currentByte));
                    size += 11 + rleIndex - i;
                }
                i = rleIndex + numSame;
            } else {
                blocks.add(new Block(input.length - i, (byte) 0, (byte) 0));
                size += 5 + input.length - i;
                break;
            }
        }

        byte[] result = new byte[size];
        j = 0;
        k = 0;
        for (i = 0; i < blocks.size(); ++i) {
            block = (Block) blocks.get(i);
            size = block._length;
            result[j++] = block._type;
            if (block._type == 1) {
                result[j++] = block._value;
                k += size;
            }
            result[j++] = (byte) (size >>> 24);
            result[j++] = (byte) (size >>> 16);
            result[j++] = (byte) (size >>> 8);
            result[j++] = (byte) size;
            if (block._type == 0) {
                for (x = 0; x < size; ++x) {
                    result[j++] = input[k++];
                }
            }
        }

        return result;
    }

    /**
     *  Description of the Method
     *
     *@param  input  Description of the Parameter
     *@return        Description of the Return Value
     */
    public static byte[] decode(byte[] input) {
        int size;
        int totalSize = 0;
        byte type;
        byte value = 0;
        int i;
        int j;
        int k;

        for (i = 0; i < input.length;) {
            type = input[i++];
            if (type == 1) {
                ++i;
            }
            size = 0;
            size = (((int) input[i++]) & 0xFF) << 24;
            size |= (((int) input[i++]) & 0xFF) << 16;
            size |= (((int) input[i++]) & 0xFF) << 8;
            size |= (((int) input[i++]) & 0xFF);
            totalSize += size;
            if (type == 0) {
                i += size;
            }
        }

        byte[] result = new byte[totalSize];
        k = 0;
        for (i = 0; i < input.length;) {
            type = input[i++];
            if (type == 1) {
                value = input[i++];
            }
            size = 0;
            size = (((int) input[i++]) & 0xFF) << 24;
            size |= (((int) input[i++]) & 0xFF) << 16;
            size |= (((int) input[i++]) & 0xFF) << 8;
            size |= (((int) input[i++]) & 0xFF);
            if (type == 0) {
                for (j = 0; j < size; ++j) {
                    result[k++] = input[i++];
                }
            } else {
                for (j = 0; j < size; ++j) {
                    result[k++] = value;
                }
            }
        }

        return result;
    }
}
