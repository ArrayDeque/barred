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

package org.barred.bit;

import java.io.IOException;
import java.nio.ByteBuffer;

public class BitWriter {

    /**
     *  Holds the output stream that is written to.
     */
    private ByteBuffer _output;
    /**
     *  Buffers the bits to write.
     */
    private byte[] _buffer;
    /**
     *  Holds the current position in the buffer (in bits).
     */
    private int _bufferPos;
    /**
     *  Helper array.
     */
    private static byte[] helper = {(byte) 0x80, 0x40, 0x20, 0x10, 0x08, 0x04, 0x02, 0x01};

    /**
     *  Constructs a new <code>BitWriter</code> object.
     *
     *@param  output  Description of the Parameter
     */
    public BitWriter(ByteBuffer output) {
        _output = output;
        _buffer = new byte[256];
        _bufferPos = 0;
    }

    /**
     *  Flushes the buffer (necessary after writing all bits).
     *
     *@exception  IOException  thrown in case of an I/O problem.
     */
    public void flush() throws IOException {
        int length = _bufferPos / 8;
        if (_bufferPos % 8 != 0) {
            ++length;
        }
        //System.out.println("buffer:"+_buffer.length+" length:"+length);
        try {
            _output.put(_buffer, 0, length);
        } catch (Exception e) {
            System.out.println("An Error Occured: Not enough scratch buffer!.");

        }
        //_output.clear();
        _bufferPos = 0;
    //System.out.println("");
    }

    /**
     *  Write the specified bit to the output stream. The int passed must only
     *  contain 0 and 1 which are transformed to a bit.
     *
     *@param  bit              the bit to write.
     *@exception  IOException  thrown in case of an I/O problem.
     */
    public void write(int bit) throws IOException {
        byte temp = helper[_bufferPos % 8];
        if (bit == 0) {
            _buffer[_bufferPos / 8] &= (~temp);
        } else {
            _buffer[_bufferPos / 8] |= temp;
        }
        ++_bufferPos;
        if (_bufferPos / 8 == _buffer.length) {
            flush();
        // also resets _bufferPos
        }
    //System.out.print(bit);
    }

    /**
     *  Write the specified bits to the output stream. The byte array must only
     *  contain 0 and 1 which are transformed to bits.
     *
     *@param  bits             the bits to write.
     *@exception  IOException  thrown in case of an I/O problem.
     */
    public void write(byte[] bits) throws IOException {
        for (int i = 0; i < bits.length; ++i) {
            write(bits[i]);
        }
    }
}
