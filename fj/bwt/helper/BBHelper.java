package fj.bwt.helper;

import java.nio.*;

/**
 *  Description of the Class
 *
 *@author     Frank Jennings fermatjen@yahoo.com
 *@created    March 27, 2004
 */
public class BBHelper {

    private int remaining = -1;
    private ByteBuffer buffer = null;

    /**
     *  Sets the remaining attribute of the BBHelper object
     *
     *@param  size  The new remaining value
     */
    public void setRemaining(int size) {
        remaining = size;
    }

    /**
     *  Gets the remaining attribute of the BBHelper object
     *
     *@return    The remaining value
     */
    public int getRemaining() {
        return remaining;
    }

    /**
     *  Sets the buffer attribute of the BBHelper object
     *
     *@param  buf  The new buffer value
     */
    public void setBuffer(ByteBuffer buf) {
        buffer = buf;
    }

    /**
     *  Gets the buffer attribute of the BBHelper object
     *
     *@return    The buffer value
     */
    public ByteBuffer getBuffer() {
        return buffer;
    }
}


