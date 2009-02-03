package fj.bwt.bit;

import java.io.InputStream;
import java.io.IOException;
import java.io.EOFException;

/**
 *  Class for reading individual bits from an input stream.
 *
 *@author     Frank Jennings fermatjen@yahoo.com
 *@created    March 27, 2004
 */
public class BitReader {

    /**
     *  Holds the input stream that is read from.
     */
    private InputStream _input;
    /**
     *  Buffers the bits to read.
     */
    private byte[] _buffer;
    /**
     *  Holds the current position in the buffer (in bits).
     */
    private int _bufferPos;
    /**
     *  Flag indicating if the buffer has been filled.
     */
    private boolean _bufferFilled;
    /**
     *  Helper array.
     */
    private static byte[] helper = {(byte) 0x80, 0x40, 0x20, 0x10, 0x08, 0x04, 0x02, 0x01};

    /**
     *  Constructs a new <code>BitReader</code> object.
     *
     *@param  input  Description of the Parameter
     */
    public BitReader(InputStream input) {
        _input = input;
        _buffer = new byte[256];
        _bufferPos = 0;
        _bufferFilled = false;
    }

    /**
     *  Read a bit from the input stream.
     *
     *@return                  the next bit in the stream (0 or 1).
     *@exception  IOException  thrown in case of an I/O problem (including
     *      end-of-file).
     */
    public byte read() throws IOException {
        // fill the buffer (if necessary)
        if (!_bufferFilled) {
            int bytesRead = _input.read(_buffer, 0, 256);
            if (bytesRead == 0) {
                throw new EOFException();
            }
            _bufferFilled = true;
        }

        // read the bit
        byte temp = helper[_bufferPos % 8];
        byte result;
        if ((_buffer[_bufferPos / 8] & temp) != 0) {
            result = 1;
        } else {
            result = 0;
        }
        ++_bufferPos;
        if (_bufferPos / 8 == _buffer.length) {
            _bufferFilled = false;
            _bufferPos = 0;
        }

        return result;
    }
}
