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

import java.io.File;
import java.util.ArrayList;


public class DirHelper {

    private static int rlength = 0;
    private static ArrayList <String> alist = null;

    public static ArtHelper getArtifacts(String source) {
        File _helper = new File(source);
        rlength = (_helper.getPath()).length();
        //rlength++;
        alist = new ArrayList <String> ();

        getArt(source);

        ArtHelper ahelp = new ArtHelper();
        ahelp.setSeperator(File.separatorChar);
        ahelp.setAlist(alist);
        return ahelp;
    }

    private static void getArt(String source) {
        try {
            File f1 = new File(source);
            if (f1.exists()) {
                if (f1.isDirectory()) {
                    String[] lis = f1.list();

                    for (int i = 0; i < lis.length; i++) {
                        File te = null;
                        try {
                            te = new File(f1, lis[i]);
                        } catch (Exception e) {
                            System.out.println("Error:" + e);
                        }
                        if (te.isDirectory()) {
                            try {
                                getArt(te.toString());
                            } catch (Exception e) {
                                System.out.println("Error:" + e);
                            }
                        } else {
                            String fname = te.getPath();
                            fname = (fname).substring(rlength, fname.length());
                            alist.add(fname);
                        }
                    }
                } else {
                    alist.add(f1.getPath());

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}