package fj.bwt.helper;

import fj.bwt.helper.ArtHelper;
import java.io.*;
import java.util.*;

public class DirHelper {

    private static int rlength = 0;
    private static ArrayList alist = null;

    public static ArtHelper getArtifacts(String source) {
        File _helper = new File(source);
        rlength = (_helper.getPath()).length();
        //rlength++;
        alist = new ArrayList();

        getArt(source);

        ArtHelper ahelp = new ArtHelper();
        ahelp.setSeperator(_helper.separatorChar);
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