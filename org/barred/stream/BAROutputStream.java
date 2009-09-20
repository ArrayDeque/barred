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

package org.barred.stream;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import org.barred.helper.BWTHelper;
import org.barred.algo.MTF;
import org.barred.algo.ENT;
import org.barred.algo.RLE;
import org.barred.algo.BWT;
import org.barred.helper.BBHelper;


public class BAROutputStream {

    private OutputStream out = null;

    /**
     *  Constructor for the BAROutputStream object
     *
     *@param  out  The output stream where the compressed byte array will be
     *      written.
     */
    public BAROutputStream(OutputStream out) {
        this.out = out;
    }

    /**
     *  The input byte array is compressed and written to the output stream.
     *
     *@param  a1               The input byte array, which will be compressed.
     *@exception  Exception    Description of the Exception
     */
    public BWTHelper write(byte[] a1) throws Exception {

        byte[] a2 = null;

        //System.out.print("Performing RLE .....");
        //a2 = RLE.encode(a1);
        //System.out.print("[DONE]\n");

        //System.out.print("Performing BWT .....");
        a2 = new byte[a1.length];
        BWTHelper bwth = BWT.encode(a1, a2);
        int index = bwth.getPindex();
        boolean suc = bwth.getSuccess();
        //a1 is the input array.
        //a2 has the bwt array.

        BWTHelper status = new BWTHelper();

        if (!suc) {
            //do not read a2. It is corrupted.
            index = -1;
            a2 = a1;
            status.setIsBWT(false);
        } else {
            status.setIsBWT(true);
        }

        //System.out.print("[DONE]\n");

        //System.out.print("Performing MTF .....");
        MTF.encode(a2, a1);
        //System.out.print("[DONE]\n");
        //a1 is the MTF Array
        //a2 has the bwt array.

        //System.out.print("Performing RLE .....");
        a2 = RLE.encode(a1);
        //System.out.print("[DONE]\n");
        //a2 has the RLE array.

        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.putInt(index);

        //Writing the Primary Index as is
        out.write(buf.array());

        //System.out.print("Performing ENT .....");

        int len = (3 * a2.length);
        ByteBuffer tout = ByteBuffer.allocate((len));
        //System.out.println("a2length:"+len);

        BBHelper fin = new ENT().encode(a2, tout);
        int size = len - (fin.getRemaining());
        //System.out.println("Chunk Size: "+size);


        byte fArray[] = fin.getBuffer().array();

        //if(mBlock){
        //Writing the chunk length
        buf = ByteBuffer.allocate(4);
        buf.putInt(size);
        out.write(buf.array());
        //}

        for (int i = 0; i < size; i++) {
            out.write(fArray[i]);
        }


        status.setBytesOut(size);
        return status;
    //System.out.print("[DONE]\n");
    }

    /**
     *  Description of the Method
     *
     *@exception  IOException  Description of the Exception
     */
    public void close() throws IOException {
        out.close();
    }
}
