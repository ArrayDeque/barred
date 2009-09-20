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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Digest {

    /**
     *  Helper method for replacing the file seperator
     *  char based on the OS
     *
     *@param  tsize  Description of the Parameter
     *@return        The block value
     */

    public static byte[] getDigest(byte[] input) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("MD5 Digest Algorithm Not Initialized.");
        }
        md5.update(input);
        return md5.digest();

    }

    public static boolean verifyDigest(byte[] key1, byte[] key2) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("MD5 Digest Algorithm Not Initialized.");
        }
        return md5.isEqual(key1, key2);
    }
}
