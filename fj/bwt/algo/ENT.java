package fj.bwt.algo;

import fj.bwt.bit.BitWriter;
import fj.bwt.bit.BitReader;
import fj.bwt.*;
import fj.bwt.helper.BBHelper;
import java.io.*;
import java.nio.*;

/**
 *  Description of the Class
 *
 *@author     Frank Jennings fermatjen@yahoo.com
 *@created    March 27, 2004
 */
public class ENT {

    private byte[] result = null;

    /**
     *  Description of the Method
     *
     *@param  input          Description of the Parameter
     *@param  output         Description of the Parameter
     *@return                Description of the Return Value
     *@exception  Exception  Description of the Exception
     */
    public BBHelper encode(byte[] input, ByteBuffer output) throws Exception {
        long[] count = new long[512];
        long[] count2 = new long[256];
        int i;
        int index;
        int null_count;
        boolean isUsed = false;

        BitWriter bitWriter = new BitWriter(output);
        for (i = 0; i < 512; ++i) {
            count[i] = 0;
        }
        for (i = 0; i < 256; ++i) {
            count2[i] = 0;
        }
        null_count = 0;

        for (i = 0; i < input.length; ++i) {
            if (input[i] == 0) {
                if (null_count < 255) {
                    ++null_count;
                } else {
                    count[256] += 1;
                    null_count = 1;
                }
            } else {
                index = (input[i] >= 0 ? (int) input[i] : 256 + (int) input[i]);
                if (null_count > 0) {
                    count[256 + null_count] += 1;
                    null_count = 0;
                    count2[index] += 1;
                    isUsed = true;
                } else {
                    count[index] += 1;
                }
            }
        }
        if (null_count > 0) {
            count[256 + null_count] += 1;
        }

        Huffman huffman = new Huffman();
        huffman.buildTree(count);
        Huffman huffman2 = null;
        if (isUsed) {
            huffman2 = new Huffman();
            huffman2.buildTree(count2);
        }

        try {

            int length = input.length;
            ByteBuffer buf = ByteBuffer.allocate(4);
            buf.putInt(length);
            output.put(buf.array());

            huffman.writeTree(bitWriter);
            if (isUsed) {
                bitWriter.write(1);
                huffman2.writeTree(bitWriter);
            } else {
                bitWriter.write(0);
            }

            null_count = 0;
            for (i = 0; i < input.length; ++i) {
                if (input[i] == 0) {
                    if (null_count < 255) {
                        ++null_count;
                    } else {
                        huffman.encode(256, bitWriter);
                        null_count = 1;
                    }
                } else {
                    index = (input[i] >= 0 ? (int) input[i] : 256 + (int) input[i]);
                    if (null_count > 0) {
                        huffman.encode(256 + null_count, bitWriter);
                        null_count = 0;
                        huffman2.encode(index, bitWriter);
                    } else {
                        huffman.encode(index, bitWriter);
                    }
                }
            }
            if (null_count > 0) {
                huffman.encode(256 + null_count, bitWriter);
            }


            bitWriter.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }

        BBHelper bb = new BBHelper();
        bb.setRemaining(output.remaining());
        bb.setBuffer(output);


        return bb;
    }

    /**
     *  Description of the Method
     *
     *@param  input            Description of the Parameter
     *@return                  Description of the Return Value
     *@exception  IOException  Description of the Exception
     */
    public byte[] decode(InputStream input) throws IOException {


        BitReader bitReader = new BitReader(input);
        int length = 0;

        boolean useTree2;
        int value;
        int null_count;
        int i;
        int j;

        try {

            byte[] lengthBytes = new byte[4];
            input.read(lengthBytes);
            length = (((int) lengthBytes[0]) & 0xFF) << 24;
            length |= (((int) lengthBytes[1]) & 0xFF) << 16;
            length |= (((int) lengthBytes[2]) & 0xFF) << 8;
            length |= (((int) lengthBytes[3]) & 0xFF);
            result = null;

            result = new byte[length];

            Huffman huffman = new Huffman();
            Huffman huffman2 = null;
            huffman.readTree(bitReader);
            if (bitReader.read() != 0) {
                huffman2 = new Huffman();
                huffman2.readTree(bitReader);
            }

            useTree2 = false;
            i = 0;
            while (i < length) {
                if (useTree2) {
                    value = huffman2.decode(bitReader);
                    useTree2 = false;
                    result[i] = (byte) value;
                    ++i;
                } else {
                    value = huffman.decode(bitReader);
                    if (value < 256) {
                        result[i] = (byte) value;
                        ++i;
                    } else {
                        if (value == 256) {
                            null_count = 255;
                        } else {
                            null_count = value - 256;
                            useTree2 = true;
                        }
                        for (j = 0; j < null_count; ++j) {
                            result[i] = 0;
                            ++i;
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
