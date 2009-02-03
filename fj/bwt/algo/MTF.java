package fj.bwt.algo;

import java.io.*;

/**
 *  Move To Front Encoder as a second stage from
 *  The BWT.
 *
 *@author     Frank Jennings fermatjen@yahoo.com
 *@created    March 25, 2004
 */
public class MTF {

    /**
     *  Encodes the input byte array into the output byte array
     *
     *@param  input   byte array to be MTF encoded.
     *@param  output  MTF encoded byte array.
     */
    public static void encode(byte[] input, byte[] output) {
        byte[] values = new byte[256];
        int i;
        int j;
        byte cb;

        for (i = 0; i < 256; ++i) {
            values[i] = (byte) (i);
        }

        for (i = 0; i < input.length; ++i) {
            cb = input[i];
            for (j = 0; j < 256; ++j) {
                if (values[j] == cb) {
                    output[i] = (byte) (j);
                    if (j != 0) {
                        System.arraycopy(values, 0, values, 1, j);
                        values[0] = cb;
                    }
                    break;
                }
            }
        }
    }

    /**
     *  Decodes an MTF encoded Byte Array
     *
     *@param  input   The byte array to be MTF decoded
     *@param  output  The MTF decoded byte array
     */
    public static void decode(byte[] input, byte[] output) {
        byte[] values = new byte[256];
        int i;
        int j;
        byte cb;
        int position;

        for (i = 0; i < 256; ++i) {
            values[i] = (byte) (i);
        }

        for (i = 0; i < input.length; ++i) {
            cb = input[i];
            position = (cb >= 0 ? (int) cb : (int) cb + 256);
            cb = values[position];
            output[i] = cb;
            if (position != 0) {
                System.arraycopy(values, 0, values, 1, position);
                values[0] = cb;
            }
        }
    }
}