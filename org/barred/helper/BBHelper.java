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

import java.nio.ByteBuffer;

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


