package fj.bwt.stream;

import fj.bwt.helper.BWTHelper;
import fj.bwt.algo.MTF;
import fj.bwt.algo.ENT;
import fj.bwt.algo.RLE;
import fj.bwt.algo.BWT;
import fj.bwt.helper.BBHelper;
import fj.bwt.*;
import java.io.*;
import java.nio.*;

/**
 *  Description of the Class
 *
 *@author     Frank Jennings fermatjen@yahoo.com
 *@created    March 25, 2004
 */
public class BAROutputStream {

    private OutputStream out = null;
    private boolean mBlock = false;

    /**
     *  Constructor for the BAROutputStream object
     *
     *@param  out  The output stream where the compressed byte array will be
     *      written.
     */
    public BAROutputStream(OutputStream out) {
        this.out = out;
        this.mBlock = mBlock;
    }

    /**
     *  The input byte array is compressed and written to the output stream.
     *
     *@param  a1               The input byte array, which will be compressed.
     *@exception  Exception    Description of the Exception
     */
    public BWTHelper write(byte[] a1) throws Exception {

        byte[] a2 = null;
        byte[] a3 = null;

        //System.out.print("Performing RLE .....");
        a2 = RLE.encode(a1);
        //System.out.print("[DONE]\n");

        //System.out.print("Performing BWT .....");
        a1 = new byte[a2.length];
        BWTHelper bwth = BWT.encode(a2, a1);
        int index = bwth.getPindex();
        boolean suc = bwth.getSuccess();

        BWTHelper status = new BWTHelper();

        if (!suc) {
            //do not read a1. It is corrupted.
            index = -1;
            a1 = a2;
            status.setIsBWT(false);
        } else {
            status.setIsBWT(true);
        }

        //System.out.print("[DONE]\n");

        //System.out.print("Performing MTF .....");
        MTF.encode(a1, a2);
        //System.out.print("[DONE]\n");

        //System.out.print("Performing RLE .....");
        //a3 = RLE.encode(a2);
        //System.out.print("[DONE]\n");

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
