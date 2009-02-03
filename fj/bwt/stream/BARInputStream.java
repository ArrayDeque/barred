package fj.bwt.stream;

import fj.bwt.algo.MTF;
import fj.bwt.algo.ENT;
import fj.bwt.algo.RLE;
import fj.bwt.algo.BWT;
import fj.bwt.*;
import java.io.*;
import java.nio.*;

/**
 *  BAR Archiver for InputStream. Bar reads compressed bytes
 *  using this stream object.
 *
 *@author     Frank Jennings fermatjen@yahoo.com
 *@created    March 27, 2004
 */
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
        //a2 = RLE.decode(a1);
        //System.out.print("[DONE]\n");

        a2 = new byte[a1.length];
        //System.out.print("Performing MTF .....");
        MTF.decode(a1, a2);
        //System.out.print("[DONE]\n");

        //System.out.print("Performing RBWT ....");
        //a2 = new byte[a1.length];
        if (!isRaw) {
            BWT.decode(a2, index, a1);
        } else {
            a1 = a2;
        }
        //System.out.print("[DONE]\n");

        //System.out.print("Performing RLE .....");
        //a1 = new byte[a2.length];
        a2 = RLE.decode(a1);
        //System.out.print("[DONE]\n");

        //System.out.print("Writing file .......");
        //outputStream.write(a2);
        //System.out.print("[DONE]\n");
        return a2;
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