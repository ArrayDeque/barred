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

public class ByteDisp {

    private static String disp = new String();

    public static String convert(long by) {

        Long lo = new Long(by);
        float byt = lo.floatValue();

        float temp;
        Float con = null;

        if (by < 1024) {
            disp = "" + by + " Bytes";
        } else {
            if (by < 1048576) {
                temp = ((byt / 1024) * 10);
                con = new Float(temp);
                int yu = con.intValue();
                Integer in = new Integer(yu);
                float op = in.floatValue();
                op = op / 10;
                disp = "" + op + " KB";
            } else {
                temp = ((byt / 1048576) * 10);
                con = new Float(temp);
                int yu = con.intValue();
                Integer in = new Integer(yu);
                float op = in.floatValue();
                op = op / 10;
                disp = "" + op + " MB";
            }
        }
        return disp;
    }
}
