package fj.bwt.helper;

import java.security.*;

/**
 *  Description of the Class
 *
 *@author     Frank Jennings fermatjen@yahoo.com
 *@created    March 27, 2004
 */
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
