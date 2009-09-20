/******************************************************************************
 *
 *
 * Barred File Archiver.
 *
 * Copyright (C) 2009 by Frank Jennings (fermatjen@yahoo.com).
 *
 * Permission to use, copy, modify, and distribute this software and its
 * documentation under the terms of the GNU General Public License is hereby
 * granted. No representations are made about the suitability of this software
 * for any purpose. It is provided "as is" without express or implied warranty.
 * See the GNU General Public License for more details.
 *
 *
 * @author     Frank Jennings fermatjen@yahoo.com
 * @created    September 20, 2009
 */

package org.barred.helper;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import org.barred.algo.ENT;

public class BWTHelper {

    private int pindex = -1;
    private boolean success = true;
    private boolean isBWT = true;
    private int _bytesOut = 0;

    /**
     *  Sets the remaining attribute of the BBHelper object
     *
     *@param  size  The new remaining value
     */
    public void setPindex(int pi) {
        pindex = pi;
    }

    public void setIsBWT(boolean bwt) {
        isBWT = bwt;
    }

    public void setBytesOut(int bo) {
        _bytesOut = bo;
    }

    public boolean getIsBWT() {
        return isBWT;
    }

    public int getBytesOut() {
        return _bytesOut;
    }

    /**
     *  Gets the remaining attribute of the BBHelper object
     *
     *@return    The remaining value
     */
    public int getPindex() {
        return pindex;
    }

    /**
     *  Sets the buffer attribute of the BBHelper object
     *
     *@param  buf  The new buffer value
     */
    public void setSuccess(boolean suc) {
        success = suc;
    }

    /**
     *  Gets the buffer attribute of the BBHelper object
     *
     *@return    The buffer value
     */
    public boolean getSuccess() {
        return success;
    }

    public static byte[] compressBytes(byte[] input) {
        //System.out.println("C:"+new String(input));


        int len = (2 * input.length);
        ByteBuffer tout = ByteBuffer.allocate((len));
        //System.out.println("a2length:"+len);
        BBHelper fin = null;
        try {
            fin = new ENT().encode(input, tout);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int size = len - (fin.getRemaining());
        //System.out.println("Chunk Size: "+size);

        //System.out.println("D:"+new String(fin.getBuffer().array()));
        return fin.getBuffer().array();
    }

    public static byte[] decompressBytes(byte[] input) {

        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(input);
            return new ENT().decode(bis);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;


    }
}


