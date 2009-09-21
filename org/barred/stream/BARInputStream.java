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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.barred.algo.MTF;
import org.barred.algo.ENT;
import org.barred.algo.RLE;
import org.barred.algo.BWT;


public class BARInputStream {

    private InputStream in = null;
    private boolean forge = false;

    /**
     *  Constructor for the BARInputStream object
     *
     *@param  in  The input stream from which byetes
     * will be read.
     */
    public BARInputStream(InputStream in, boolean forge) {
        this.in = in;
        this.forge = forge;

    }

    /**
     *  The read method for reading compressed bytes from the stream.
     * This method also decompresses the bytes before returning.
     *
     *@return                 The decompressed Byte Array
     *@exception  IOException  Thrown upon file error
     */
    public byte[] read() throws IOException {




        byte[] a1 = null;
        byte[] a2 = null;
        byte[] ind = new byte[4];

        //Reading chunks' PI
        //System.out.println("reading PI");

        //System.out.println("Available:"+in.available());
        in.read(ind);
        ByteBuffer buf = ByteBuffer.wrap(ind);
        int index = buf.getInt();
        boolean isRaw = false;

        //check for corrupted block
        if (index == -1) {
            //System.out.println("Found Raw Bytes.");
            isRaw = true;
        }


        //System.out.println("PI:"+index);
        byte[] chunk = null;


        //if(mBlock){
        //Reading chunks' Size
        //System.out.println("PI:"+index);
        in.read(ind);
        buf = ByteBuffer.wrap(ind);
        int cSize = buf.getInt();
        //System.out.println("Chunk Size:"+cSize);
        chunk = new byte[cSize];
        in.read(chunk);
        //}

        if (forge) {
            return null;
        }


        //System.out.println("INDEX:"+index);
        //System.out.print("Performing ENT .....");

        ByteArrayInputStream bis = new ByteArrayInputStream(chunk);
        a1 = new ENT().decode(bis);
        //System.out.print("[DONE]\n");

        //System.out.print("Performing RLE .....");
        a2 = RLE.decode(a1);
        //System.out.print("[DONE]\n");
        //a2 has the RLE array

        a1 = new byte[a2.length];
        //System.out.print("Performing MTF .....");
        MTF.decode(a2, a1);
        //System.out.print("[DONE]\n");
        //a1 has the MTF array

        //System.out.print("Performing RBWT ....");
        a2 = new byte[a1.length];
        if (!isRaw) {
            BWT.decode(a1, index, a2);
        } else {
            a2 = a1;
        }
        //a2 has the BWT array
        //System.out.print("[DONE]\n");

        //System.out.print("Performing RLE .....");
        a1 = new byte[a2.length];
        a1 = RLE.decode(a2);
        //System.out.print("[DONE]\n");

        //System.out.print("Writing file .......");
        //outputStream.write(a2);
        //System.out.print("[DONE]\n");
        return a1;
    }

    /**
     *  Closes the InputStream
     *
     *@exception  IOException  Thrown on File Stream closing.
     */
    public void close() throws IOException {
        in.close();
    }
}
