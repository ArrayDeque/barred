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

package org.barred.block;

public class BlockMan {

    //The default block size
    private final static int _block_size = 1024*20000;

    /**
     *  Gets the block attribute of the BlockMan class
     *
     *@param  tsize  Description of the Parameter
     *@return        The block value
     */
    public static int getBlock(int tsize) {

        if (tsize > _block_size) {
            return _block_size;
        }
        if (tsize <= _block_size) {
            return tsize;
        }
        return _block_size;

        
    }
}
