package fj.bwt.algo;

import fj.bwt.helper.BWTHelper;
import fj.bwt.*;
import java.util.*;

/**
 *  BWT module for transforming the input bytes in such a way that
 *  similar contexts are placed close to each other as far as possible
 *  for moving to the front using MTF encoder.
 *
 *@author     Frank Jennings fermatjen@yahoo.com
 *@created    March 25, 2004
 */
public class BWT {

    private static byte[] in;
    private static int[] pos;
    private static int ind,  swapPos,  slength,  cind;
    private static byte pb,  cb;
    private static boolean con,  isSwap,  success;

    /**
     *  Swap
     *
     *
     *
     */
    private static void swap(int index1, int index2) {
        int temp = pos[index1];
        pos[index1] = pos[index2];
        pos[index2] = temp;
    }

    /**
     *  Description of the Method
     *
     *@param  pStart   Description of the Parameter
     *@param  pLength  Description of the Parameter
     *@param  cind     Description of the Parameter
     */
    public static void qsort(int pStart, int pLength, int cind) {

        int a;
        int b;
        int c;
        int d;

        if (pLength <= 1) {
            return;
        }

        isSwap = true;

        if (isSwap) {
            ind = pStart + (int) (Math.random() * (double) pLength) % pLength;
            swap(pStart, ind);
            pb = in[(pos[pStart] + cind) % in.length];

            a = pStart + 1;
            b = a;
            c = pStart + pLength - 1;
            d = c;

            while (b <= c) {
                do {
                    cb = in[(pos[b] + cind) % in.length];
                    if (b <= c && cb <= pb) {
                        if (cb == pb) {
                            swap(a, b);
                            ++a;
                        }
                        ++b;
                    }
                } while (b <= c && cb <= pb);
                do {
                    cb = in[(pos[c] + cind) % in.length];
                    if (b <= c && cb >= pb) {
                        if (cb == pb) {
                            swap(c, d);
                            --d;
                        }
                        --c;
                    }
                } while (b <= c && cb >= pb);
                if (b <= c) {
                    swap(b, c);
                    ++b;
                    --c;
                }
            }
            slength = a - pStart;
            slength = (slength <= (b - a) ? slength : b - a);
            for (swapPos = 0; swapPos < slength; ++swapPos) {
                swap(pStart + swapPos, b - slength + swapPos);
            }

            slength = pLength - (d + 1 - pStart);
            slength = (slength <= (d - c) ? slength : d - c);
            for (swapPos = 0; swapPos < slength; ++swapPos) {
                swap(b + swapPos, pStart + pLength - slength + swapPos);
            }

            cind = cind;
            try {
                qsort(pStart, b - a, cind);
                if (cind < (in.length - 1)) {
                    qsort(pStart + b - a, (a - pStart) + (pLength - (d + 1 - pStart)), cind + 1);
                }
                qsort(pStart + pLength - (d - c), d - c, cind);
            } catch (StackOverflowError error) {
                success = false;
                return;
            }
        }


    }

    /**
     *  Description of the Method
     *
     *@param  input   Description of the Parameter
     *@param  output  Description of the Parameter
     *@return         Description of the Return Value
     */
    public static BWTHelper encode(byte[] input, byte[] output) {
        int[] pointers = new int[input.length];
        int i;
        int ret = 0;
        for (i = 0; i < input.length; ++i) {
            pointers[i] = i;
        }
        in = input;
        pos = pointers;

        success = true;

        qsort(0, input.length, 0);


        for (i = 0; i < input.length; ++i) {
            output[i] = input[(pointers[i] + input.length - 1) % input.length];
            if (pointers[i] == 0) {
                ret = i;
            }
        }


        BWTHelper bwth = new BWTHelper();
        bwth.setPindex(ret);
        bwth.setSuccess(success);
        return bwth;
    }

    /**
     *  Description of the Method
     *
     *@param  input   Description of the Parameter
     *@param  index   Description of the Parameter
     *@param  output  Description of the Parameter
     */
    public static void decode(byte[] input, int index, byte[] output) {
        int i;
        int j;
        int li;
        int sc;
        byte nb;

        byte[] firstCol = new byte[input.length];
        System.arraycopy(input, 0, firstCol, 0, input.length);
        Arrays.sort(firstCol);

        int[] count = new int[256];
        int[] byteStart = new int[256];
        int[] shortcut = new int[input.length];
        for (i = 0; i < 256; ++i) {
            count[i] = 0;
            byteStart[i] = -1;
        }
        for (i = 0; i < input.length; ++i) {
            sc = (input[i] >= 0 ? (int) input[i] : (int) input[i] + 256);
            shortcut[i] = count[sc];
            count[sc] += 1;
            sc = (firstCol[i] >= 0 ? (int) firstCol[i] : (int) firstCol[i] + 256);
            if (byteStart[sc] == -1) {
                byteStart[sc] = i;
            }
        }

        li = index;
        for (i = 0; i < input.length; ++i) {
            nb = input[li];
            output[input.length - i - 1] = nb;
            sc = (nb >= 0 ? (int) nb : (int) nb + 256);
            li = byteStart[sc] + shortcut[li];
        }
    }
}