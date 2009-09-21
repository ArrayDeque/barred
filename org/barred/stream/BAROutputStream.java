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

        
	//STAGE1: PERFORMING RLE
	byte[] a2 = new byte[a1.length];
	a2 = RLE.encode(a1);
	//a2 has the target array

        //STAGE2: PERFORMING BWT
        a1 = new byte[a2.length];
        BWTHelper bwth = BWT.encode(a2, a1);
        int index = bwth.getPindex();
        boolean suc = bwth.getSuccess();
        //a1 has the target array.


        BWTHelper status = new BWTHelper();

        if (!suc) {
            //do not read a2. It is corrupted.
            index = -1;
            a2 = a1;
            status.setIsBWT(false);
        } else {
            status.setIsBWT(true);
        }

	//STAGE3: PERFORMING MTF
        MTF.encode(a1, a2);
        //a2 has the target array.

        //STAGE4: PERFORMING RLE
        a1 = RLE.encode(a2);
	//a1 has the target array.

        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.putInt(index);

        //Writing the Primary Index as is
        out.write(buf.array());

        //STAGE5: PERFORMING ENT

        int len = (3 * a1.length);
        ByteBuffer tout = ByteBuffer.allocate((len));
        
        BBHelper fin = new ENT().encode(a1, tout);
        int size = len - (fin.getRemaining());
        

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
