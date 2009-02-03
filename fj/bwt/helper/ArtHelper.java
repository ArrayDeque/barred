package fj.bwt.helper;

import java.util.*;

/**
 *  Description of the Class
 *
 *@author     Frank Jennings fermatjen@yahoo.com
 *@created    March 27, 2004
 */
public class ArtHelper {

    private char seperator = '\\';
    private ArrayList alist = null;

    /**
     *  Sets the remaining attribute of the BBHelper object
     *
     *@param  size  The new remaining value
     */
    public void setSeperator(char sep) {
        seperator = sep;
    }

    /**
     *  Gets the remaining attribute of the BBHelper object
     *
     *@return    The remaining value
     */
    public char getSeperator() {
        return seperator;
    }

    /**
     *  Sets the buffer attribute of the BBHelper object
     *
     *@param  buf  The new buffer value
     */
    public void setAlist(ArrayList list) {
        alist = list;
    }

    /**
     *  Gets the buffer attribute of the BBHelper object
     *
     *@return    The buffer value
     */
    public ArrayList getAlist() {
        return alist;
    }
}


