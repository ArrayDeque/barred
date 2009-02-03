package fj.bwt.helper;

/**
 *  Description of the Class
 *
 *@author     Frank Jennings fermatjen@yahoo.com
 *@created    March 27, 2004
 */
public class StringHelper {

    /**
     *  Helper method for replacing the file seperator
     *  char based on the OS
     *
     *@param  tsize  Description of the Parameter
     *@return        The block value
     */
    public static String cleanString(String input, char ol, char ne) {
        return input.replace(ol, ne);
    }
}
