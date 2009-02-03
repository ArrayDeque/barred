package fj.bwt.block;

/**
 *  Description of the Class
 *
 *@author     Frank Jennings fermatjen@yahoo.com
 *@created    March 27, 2004
 */
public class BlockMan {

    //The default block size
    private final static int _block_size = 1000000;

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
