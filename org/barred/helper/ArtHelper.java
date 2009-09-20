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

import java.util.*;

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


