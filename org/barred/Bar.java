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
package org.barred;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.barred.helper.ByteDisp;
import org.barred.block.BlockMan;
import org.barred.helper.Digest;
import org.barred.helper.ZipHandler;
import org.barred.helper.DirHelper;
import org.barred.helper.StringHelper;
import org.barred.helper.BWTHelper;
import org.barred.stream.BAROutputStream;
import org.barred.stream.BARInputStream;
import org.barred.helper.ArtHelper;

public class Bar {

    private static boolean isNeg = false;
    private static boolean isD = false;
    private static boolean protectA = false;
    private static boolean view = false;
    private static boolean verify = false;
    private static boolean find = false;
    private static boolean add = false;
    private static float[] bpsA = null;
    private static boolean isComment = false;
    private static boolean eSingle = false;
    private static boolean isBwt = true;
    private static int blockSize = 10;
    private static boolean modeLock = false;

    /**
     *  Appears on typing Help
     */
    private static void display() {
        System.out.println("\nBARRED 2.0_SVN_Sep21_09");
        System.out.println("  Copyright (C) 2009 by Frank Jennings (fermatjen@yahoo.com).\r\n");
        System.out.println("  Usage: -c/x/v/i/a <inputfile> <outputfile> [-secure].\n");
        System.out.println("        -a Add Files/DIR specified by <inputfile> to <outputfile>.");
        System.out.println("        -v View the content in the <inputfile> archive.");
        System.out.println("        -c Compress the specified <inputfile> and save as <outputfile>.");
        System.out.println("        -cc Add a comment to the archive and compress it.");
        System.out.println("        -x Decompress the specified <inputfile> and save as <outputfile>.");
        System.out.println("        -xf Decompress the specified file.");
        System.out.println("        -i Perform integrity check in DIR specified by <ifile>\n");
        System.out.println("        -b (Advanced) Manually setting the block size (1-n) MB (Default is 10).\n");
        System.out.println("	View Archive: <o-o>");
        System.out.println("        e-g. -v mp3s.bar\n");
        System.out.println("	Check Archive: <*-*>");
        System.out.println("        e-g. -i mp3s.bar\n");
        System.out.println("	Search File: <O-O>");
        System.out.println("        e-g. -f mp3s.bar bill.mp3\n");
        System.out.println("	Add Dir to Archive: <++>");        
        System.out.println("        e-g. -a new_mp3s/ mp3s.bar\n");
        System.out.println("	Compressing: >101<");
        System.out.println("        e-g. -c test.mpg test.bar");
        System.out.println("        e-g. -c -b 3 test.mpg test.bar");        
        System.out.println("        e-g. -c /home/afj/mp3s /home/backup/mp3s.bar");
        System.out.println("        e-g. -cc /home/afj/mp3s /home/backup/mp3s.bar\n");
        System.out.println("	De-compressing: <01010>");
        System.out.println("        e-g. -x test.bar test.mpg");
        System.out.println("        e-g. -x /home/backup/mp3s.bar /home/afj/mp3s");
        System.out.println("        e-g. -xf billy.mp3 mp3s.bar\n");
        System.out.println("	Securing Content (Optional): {$$$}");
        System.out.println("        e-g. -c payroll.doc payroll.bar -secure");
        System.out.println("        e-g. -c /home/afj/personal personal.bar -secure\n");
        System.exit(0);
    }

    /**
     *  Prints stats after compression routine
     *
     *@param  orig  Description of the Parameter
     *@param  comp  Description of the Parameter
     *@return       Description of the Return Value
     */
    private static void stats(float orig, float comp, boolean file) {

        if (file) {
            System.out.println("Average File Stats:");

        } else {

            System.out.println("Dir. Stats:");

        }

        System.out.println("  Orig. Size : " + (int) orig + " Bytes " + "(" + ByteDisp.convert((long) orig) + ")");
        System.out.println("  Comp. Size : " + (int) comp + " Bytes " + "(" + ByteDisp.convert((long) comp) + ")");
        System.out.println("  Comp. Ratio: " + (((orig - comp) * 100) / orig) + " %");

        if (!file) {
            System.out.println("Aveg. bps  : " + averageBps() + " bps");
        }

        if (isNeg) {
            if (isD) {
                System.out.println("One/more files had minimum entropy. So negative compression occured.\n This message can be ignored. Your Dir. content is safe.");
            } else {
                System.out.println("WARNING! The file, which you are compressing has minimum entropy. Could not be compressed further.\n So negative compression occured. Please delete the bar file. This file is already compressed!");
            }
        }
    }

    private static float cratio(float orig, float comp) {
        return (((orig - comp) * 100) / orig);
    }

    private static float averageBps() {
        float total = 0;
        for (int i = 0; i < bpsA.length; i++) {
            total = total + bpsA[i];
        }
        return (total / bpsA.length);
    }

    /**
     *  Program Start
     *
     *@param  ar             Description of the Parameter
     *@exception  Exception  Description of the Exception
     */
    public static void main(String ar[]) throws Exception {
        try {
            String mode = new String();
            String ifile = new String();
            String ofile = new String();
            String pass = new String();

            int arLength = ar.length;

            try {
                if (arLength != 0) {

                    if (ar[0].indexOf("help") != -1) {
                        display();
                    }

                    //check for overriding block size
                    for (int i = 0; i < arLength; i++) {
                        if (ar[i].equalsIgnoreCase("-b")) {
                            if (i == arLength) {
                                display();
                                System.exit(0);
                            }

                            blockSize = Integer.parseInt(ar[i + 1]);

                            
                            System.out.println(" WARNING: Block size specified manually - "+blockSize);
                           
                            break;
                        }
                    }

                    //check for compression
                    for (int i = 0; i < arLength; i++) {
                        if (ar[i].equalsIgnoreCase("-c")) {
                            if (i == arLength) {
                                display();
                                System.exit(0);
                            }
                            mode = "compress";
                            modeLock = true;
                            ifile = ar[i + 1];
                            ofile = ar[i + 2];
                            break;
                        }
                    }

                    //check for decompression
                    for (int i = 0; i < arLength; i++) {
                        if (ar[i].equalsIgnoreCase("-x")) {
                            if (i == arLength) {
                                display();
                                System.exit(0);
                            }
                            mode = "decompress";
                            modeLock = true;
                            ifile = ar[i + 1];

                            if (!ifile.endsWith("zip")) {
                                ofile = ar[i + 2];
                            } else {

                                if (arLength > 2) {
                                    System.out.println("Cannot specify output file for formats other than bar. Files will be extracted in bar_ext Dir.");
                                    System.exit(0);
                                }

                                ofile = "bar_ext";
                                File ini = new File(ofile);
                                ini.mkdir();
                            }
                            break;
                        }
                    }

                    //check for view mode
                    for (int i = 0; i < arLength; i++) {
                        if (ar[i].equalsIgnoreCase("-v")) {
                            if (i == arLength) {
                                display();
                                System.exit(0);
                            }
                            mode = "view";
                            modeLock = true;
                            ifile = ar[i + 1];
                            view = true;
                            break;
                        }
                    }

                    //check for verify mode
                    for (int i = 0; i < arLength; i++) {
                        if (ar[i].equalsIgnoreCase("-i")) {
                            if (i == arLength) {
                                display();
                                System.exit(0);
                            }
                            mode = "verify";
                            modeLock = true;
                            ifile = ar[i + 1];
                            verify = true;
                            break;
                        }
                    }

                    //check for find mode
                    for (int i = 0; i < arLength; i++) {
                        if (ar[i].equalsIgnoreCase("-f")) {
                            if (i == arLength) {
                                display();
                                System.exit(0);
                            }
                            mode = "find";
                            modeLock = true;
                            ifile = ar[i + 1];
                            ofile = ar[i + 2];
                            find = true;
                            break;
                        }
                    }

                    //check for add mode
                    for (int i = 0; i < arLength; i++) {
                        if (ar[i].equalsIgnoreCase("-a")) {
                            if (i == arLength) {
                                display();
                                System.exit(0);
                            }
                            mode = "add";
                            modeLock = true;
                            ifile = ar[i + 1];
                            ofile = ar[i + 2];
                            add = true;
                            break;
                        }
                    }

                    //check for compress with comment mode
                    for (int i = 0; i < arLength; i++) {
                        if (ar[i].equalsIgnoreCase("-cc")) {
                            if (i == arLength) {
                                display();
                                System.exit(0);
                            }
                            mode = "compress";
                            modeLock = true;
                            isComment = true;
                            ifile = ar[i + 1];
                            ofile = ar[i + 2];
                            break;
                        }
                    }

                    //check for file decompress mode
                    for (int i = 0; i < arLength; i++) {
                        if (ar[i].equalsIgnoreCase("-xf")) {
                            if (i == arLength) {
                                display();
                                System.exit(0);
                            }
                            mode = "decompress";
                            modeLock = true;
                            eSingle = true;
                            ifile = ar[i + 2];
                            ofile = ar[i + 1];
                            break;
                        }
                    }

                    if (!modeLock) {
                        display();
                        System.exit(0);
                    }


                } else {
                    display();
                    System.exit(0);
                }

                //check for secure mode
                for (int i = 0; i < arLength; i++) {
                    if (ar[i].equalsIgnoreCase("-secure")) {
                        if (i == arLength) {
                            display();
                            System.exit(0);
                        }
                        if (mode.equals("decompress") || mode.equals("view") || mode.equals("verify")) {
                            System.out.println(" -secure option should be used for compression only.");
                            System.exit(0);
                        }
                        //System.out.println("Protecting...");
                        protectA = true;
                        break;
                    }
                }
 
            } catch (Exception e) {
                System.out.println(e);
                display();
                System.exit(0);
            }


            if (mode.equals("compress") || mode.equals("add")) {
                boolean isDir = false;
                /* Handle file types here */

                System.out.println(" Block Size: "+blockSize+" MB");

                boolean isZip = false;
                boolean isGZip = false;
                FileOutputStream zips = null;

                if (ofile.endsWith("zip")) {
                    //isZip = true;
                    zips = new FileOutputStream(ofile);
                }

                if (add) {
                    File te = new File(ofile);
                    if (!te.exists()) {
                        System.out.println("The output file is invalid!. It should be a Bar compressed file");
                        System.exit(0);
                    }
                    FileInputStream tSec = new FileInputStream(te);
                    int fb = tSec.read();
                    tSec.close();
                    if (fb == 10 || fb == 9) {
                        System.out.println("The archive: " + ofile + " is secured. Permission Denied");
                        System.exit(0);
                    }
                    if (fb == 1) {
                        System.out.println(ofile + " does not have DIR. content. Permission Denied");
                        System.exit(0);
                    }
                    isDir = true;

                }

                //first get the password
                if (protectA) {

                    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                    System.out.println(" Please enter the passphrase:");
                    pass = in.readLine();
                    System.out.println(" Please re-enter the passphrase:");
                    String conf = in.readLine();

                    if (!pass.equals(conf)) {
                        System.out.println(" Passphrase Mismatch.");
                        System.exit(0);
                    }
                }


                File te = new File(ifile);

                if (!te.exists()) {
                    System.out.println("Invalid Input File!");
                    System.exit(0);
                }

                //check for directory

                //boolean mBlock=false;


                char fsep = '\\';

                if (te.isDirectory()) {
                    isDir = true;
                    isD = true;
                    //This switch introduced regression for a single block file in a DIR..so removing.
                    //mBlock=true;
                }

                ArtHelper arts = DirHelper.getArtifacts(ifile);
                fsep = arts.getSeperator();
                ArrayList alist = arts.getAlist();
                bpsA = new float[alist.size()];

                if (!(ofile).endsWith(".bar")) {
                    System.out.println("Output file should have extension .bar");
                    System.exit(0);
                }



                File check = new File(ofile);
                if (!add) {
                    if (check.exists()) {
                        check.delete();
                    }
                }
                if (check.isDirectory()) {
                    System.out.println("Output file is a Directory. Try again.\n Enter a valid file name such as /home/afj/test.bar.");
                    System.exit(0);
                }
                FileOutputStream fout = null;


                if (!isZip) {
                    fout = new FileOutputStream(ofile, true);
                }





                BAROutputStream bos = new BAROutputStream(fout);
                //System.out.println();

                int sosize = 0;
                int sosize_f = 0;

                //write MBlock info.
                ByteBuffer buf = ByteBuffer.allocate(1);
                if (!add && !isZip) {
                    if (isDir) {
                        //Add header ID, seperator char, artifact length
                        if (!protectA) {
                            if (isComment) {
                                buf.put((byte) 20);
                                fout.write(buf.array());
                            } else {
                                buf.put((byte) 2);
                                fout.write(buf.array());
                            }
                        } //secure dir
                        else {
                            if (isComment) {
                                buf.put((byte) 100);
                                fout.write(buf.array());
                            } else {
                                buf.put((byte) 10);
                                fout.write(buf.array());
                            }
                        }

                        String comment = null;

                        if (isComment) {
                            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                            System.out.println("Please enter the archive comment:");
                            comment = in.readLine();
                            byte[] com = comment.getBytes();
                            short clen = (short) com.length;
                            ByteBuffer buf0 = ByteBuffer.allocate(2);
                            buf0.putShort(clen);
                            //writing commment length
                            fout.write(buf0.array());
                            //writing comment
                            fout.write(com);
                        }

                        buf = ByteBuffer.allocate(1);
                        //Add File Seperator
                        buf.put((byte) fsep);
                        fout.write(buf.array());

                    } else {
                        if (isComment) {
                            System.out.println("Invalid Comment Switch. Comment can be added only for Dir. content");
                            System.exit(0);
                        }
                        //Header for non Dir
                        if (!protectA) {
                            buf.put((byte) 1);
                            fout.write(buf.array());
                        } //secure file
                        else {
                            buf.put((byte) 9);
                            fout.write(buf.array());
                        }
                    }

                    //add password info if any
                    if (protectA) {
                        //System.out.println("Sending to digest:"+pass);
                        byte[] dig = Digest.getDigest(pass.getBytes());
                        //System.out.println("Storing digest:"+dig);

                        byte _digLength = (byte) dig.length;
                        fout.write(_digLength);
                        fout.write(dig);
                    }
                }//if not add and zip mode

                //System.out.println("Processing...\n");
                int bpsInd = 0;


                boolean initOut = false;

                long startTime = System.currentTimeMillis();
                //System.out.println("MEM:" + (Runtime.getRuntime().maxMemory()) / 1024000);


                for (int i = 0; i < alist.size(); i++) {


                    if (isZip) {
                        if (!isDir) {
                            System.out.println("Adding ZIP Entry: " + alist.get(i));
                            ZipHandler.initO(zips);
                            ZipHandler.writeFile(null, (String) alist.get(i));
                            continue;
                        } else {
                            File abs = null;
                            if (!initOut) {
                                ZipHandler.initO(zips);
                                initOut = true;
                            }

                            System.out.println("Adding ZIP Entry: " + alist.get(i));
                            try {
                                abs = new File(ifile, (String) alist.get(i));
                                ZipHandler.writeFile((String) alist.get(i), abs.getPath());
                            } catch (Exception e) {
                                System.out.println("Error:" + e);
                                System.exit(0);
                            }
                            continue;
                        }
                    }


                    FileInputStream in = null;

                    if (((String) alist.get(i)).endsWith(".bar")) {
                        System.out.println("Skipping.." + alist.get(i));
                    } else {



                        if (isDir) {
                            File abs = null;
                            try {
                                abs = new File(ifile, (String) alist.get(i));
                                in = new FileInputStream(abs);
                            } catch (Exception e) {
                                System.out.println("Error:" + e);
                                System.exit(0);
                            }
                        } else {
                            in = new FileInputStream(ifile);
                        }

                        int size = in.available();
                        if (size == 0) {
                            System.out.println("Skipping: " + alist.get(i) + " 0 Byte!");
                            continue;
                        }
                        if (!isDir) {
                            System.out.println("Compressing: " + alist.get(i) + " \n(" + size + ") - " + ByteDisp.convert((long) size));
                        }

                        int asize = 0;

                        sosize = sosize + size;



                        int bsize = (BlockMan.getBlock(size)) * blockSize;

                        //long dbs = (bsize / (1024 * 1000));

                        //System.out.println(" Block size: " + dbs + " MB");

                        //if(bsize==size && !isDir){
                        //mBlock=false;
                        //}
                        int start = 0;
                        int end = bsize;

                        if (size < bsize) {
                            end = size;
                        }

                        //System.out.println("Size:"+size+" bsize:"+bsize+" for "+alist.get(i));

                        long parts = (size / bsize) + 1;
                        if (size == bsize) {
                            parts = 1;
                        }

                        int part = 0;


                        if (isDir) {

                            //add segInfo
                            buf = ByteBuffer.allocate(1);
                            buf.put((byte) 1);
                            fout.write(buf.array());

                            byte[] alength = ((String) alist.get(i)).getBytes();
                            buf = ByteBuffer.allocate(2);


                            //System.out.println("Art Length:"+alength.length+" : "+clength.length);

                            buf.putShort((short) alength.length);
                            fout.write(buf.array());

                            buf = ByteBuffer.wrap(alength);

                            //write artifact
                            buf.put(alength);
                            fout.write(buf.array());
                        }


                        boolean isFirst = true;
                        boolean isBWT = true;

                        asize = 1;

                        System.out.println(" Reading " + alist.get(i) + " as " + parts + " block(s).");

                        while (true) {
                            if (end > size && start < size) {
                                end = size;
                            }
                            if (start >= size) {
                                //System.out.println();
                                break;
                            }
                            int fsize = end - start;
                            if (fsize < bsize) {
                                if (!isFirst) {
                                    fsize++;
                                }
                            }
                            part++;

                            //add segInfo
                            buf = ByteBuffer.allocate(1);
                            buf.put((byte) 0);
                            fout.write(buf.array());
                            asize++;
                            //if(! isDir){
                            System.out.print("[" + part + "/" + parts + "]");
                            //}
                            byte[] input = new byte[fsize];
                            in.read(input);
                            BWTHelper bwt = bos.write(input);
                            int _bsize = bwt.getBytesOut();
                            isBWT = bwt.getIsBWT();
                            isBwt = isBWT;
                            //PI + Chunk Size
                            asize = asize + 8;

                            asize = asize + _bsize;
                            start = end + 1;
                            end = end + bsize;
                            isFirst = false;
                        }
                        System.out.println();

                        float t1 = (float) asize;
                        float t2 = (float) size;
                        bpsA[bpsInd] = (float) ((t1 * 8) / t2);

                        sosize_f = sosize_f + asize;

                        if (isDir) {
                            if (isBWT) {
                                System.out.println("    + " + alist.get(i) + " @ " + asize + " : " + size + " -> CR: [" + cratio((float) size, (float) asize) + " %] - [" + bpsA[bpsInd] + " bps]");

                                //System.out.println("+ " + alist.get(i)+" @ "+ByteDisp.convert((long)asize)+" -> "+ByteDisp.convert((long)size)+" with CR: ["+cratio((float)size,(float)asize)+" %]");
                            } else {
                                System.out.println("    + " + alist.get(i) + " @ " + asize + " : " + size + " -> CR: [" + cratio((float) size, (float) asize) + " %] - [" + bpsA[bpsInd] + " bps] - [No BWT]");
                            }
                            if (cratio((float) size, (float) asize) < 0.0) {
                                isNeg = true;
                            }
                        }

                        bpsInd++;


                    }



                }//for

                long endTime = System.currentTimeMillis();

                long duration = (endTime - startTime) / 1000;

                System.out.println(" Processed in " + duration + " secs.");

                if (!add) {
                    System.out.println("File saved as : " + ofile);
                } else {
                    System.out.println("Added :" + ifile + " to " + ofile);
                }


                if (isZip) {
                    ZipHandler.closeOut();
                } else {
                    fout.close();
                }


                File test = new File(ofile);

                if (cratio((float) sosize, (float) test.length()) < 0.0) {
                    isNeg = true;
                }
                if (!add && !isZip) {
                    stats(sosize, (float) sosize_f, true);
                    stats(sosize, (float) test.length(), false);
                }


            } else if (mode.equals("decompress") || mode.equals("view") || mode.equals("verify") || mode.equals("find")) {


                /*
                 *  -----------------------Decompressing------------------------
                 */

                if (ifile.endsWith("zip")) {

                    if (mode.equals("decompress")) {
                        System.out.println("Reading ZIP Stream: " + ifile);
                        File ou = new File(System.getProperty("user.dir"), ofile);
                        ou.mkdir();
                        //System.out.println("Ofile: "+ou.getPath());
                        ZipHandler.copyFile(ifile, ou.getPath());

                    } else if (mode.equals("view")) {
                        System.out.println("Reading ZIP Stream: " + ifile + "");

                        File ou = new File(System.getProperty("user.dir"), ofile);
                        ou.mkdir();
                        ZipHandler.viewArc(ifile);
                    }

                    System.out.println("Done!");
                    System.exit(0);

                }

                if (!view && !verify && !find) {
                    if (eSingle) {
                        System.out.println("Searching File: " + ofile);
                    } else {
                        System.out.println("De-compressing File: " + ifile);
                    }
                } else {
                    System.out.println("Archive Content for: " + ifile);
                }
                FileInputStream fin = null;


                File test = new File(ifile);
                if (!test.exists() || test.isDirectory()) {
                    System.out.println("Not a valid input file. Input file should be a Barred compressed file.");
                    System.exit(0);
                }
                try {
                    fin = new FileInputStream(ifile);
                } catch (Exception e) {
                    System.out.println("Invalid File!");
                    System.exit(0);
                }

                //boolean mBlock=false;
                boolean isDir = false;
                char fsep = '\\';
                byte[] dig = null;

                int fb = fin.read();
                //System.out.println("" + fb);

                if (fb == 2) {
                    isDir = true;
                    fsep = (char) fin.read();
                }
                //check for secure dir
                if (fb == 10) {
                    isDir = true;
                    fsep = (char) fin.read();
                    int _diglen = (byte) fin.read();
                    dig = new byte[_diglen];
                    fin.read(dig);
                    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                    System.out.println("Archive Protected. Enter the passphrase:");
                    pass = br.readLine();

                    if (!Digest.verifyDigest(Digest.getDigest(pass.getBytes()), dig)) {
                        System.out.println("Invalid passphrase!");
                        System.exit(0);
                    }
                }
                //check for secure file
                if (fb == 9) {
                    int _diglen = (byte) fin.read();
                    dig = new byte[_diglen];
                    fin.read(dig);
                    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                    System.out.println("File Protected. Enter the passphrase:");
                    pass = br.readLine();
                    if (!Digest.verifyDigest(Digest.getDigest(pass.getBytes()), dig)) {
                        //System.out.println("dig:"+dig+" pass:"+pass+"  "+Digest.getDigest(pass.getBytes()));

                        System.out.println("Invalid passphrase!");
                        System.exit(0);
                    }

                }
                if (fb == 20) {
                    byte len[] = new byte[2];
                    fin.read(len);
                    ByteBuffer bu0 = ByteBuffer.wrap(len);
                    short clen = bu0.getShort();
                    byte com[] = new byte[clen];
                    fin.read(com);
                    System.out.println("\n\".." + new String(com) + "..\"");
                    isDir = true;
                    fsep = (char) fin.read();
                }
                if (fb == 100) {
                    byte len[] = new byte[2];
                    fin.read(len);
                    ByteBuffer bu0 = ByteBuffer.wrap(len);
                    short clen = bu0.getShort();
                    byte com[] = new byte[clen];
                    fin.read(com);

                    isDir = true;
                    fsep = (char) fin.read();
                    int _diglen = (byte) fin.read();
                    dig = new byte[_diglen];
                    fin.read(dig);
                    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                    System.out.println("Archive Protected. Enter the passphrase:");
                    pass = br.readLine();
                    if (!Digest.verifyDigest(Digest.getDigest(pass.getBytes()), dig)) {
                        System.out.println("Invalid passphrase!");
                        System.exit(0);
                    }

                    System.out.println("\n\".." + new String(com) + "..\"");
                }

                //System.out.println("a1");
                if (isDir) {
                    //System.out.println("a12");
                    if (!view && !verify && !eSingle && !find) {
                        test = new File(ofile);
                        test.mkdirs();
                        if (!test.isDirectory()) {
                            System.out.println("\r\nEnter a valid output directory");
                            System.exit(0);
                        }
                    }
                }
                if (!isDir) {
                    if (view) {
                        System.out.println("\r\nThe Barred file does not have DIR. information. No files to view! (You are trying to view a compressed file.)");
                        System.exit(0);
                    }
                    if (verify) {
                        System.out.println("\r\nThe Barred file does not have DIR. information. No files to check! (You are trying to verify a compressed file.)");
                        System.exit(0);
                    }
                }

                boolean act = false;

                if (view || find) {
                    act = true;
                }

                BARInputStream bin = new BARInputStream(fin, act);

                FileOutputStream fos = null;
                //System.out.println("a2");
                if (!isDir) {
                    if (!view && !verify) {
                        File check = new File(ofile);
                        if (check.exists()) {
                            check.delete();
                        }
                        fos = new FileOutputStream(check, true);
                    }
                }

                int aIndex = 0;

                int aSize = 0;
                boolean aSizeFirst = true;
                boolean eSingleDone = false;

                System.out.print("Wait");

                while (fin.available() != 0) {
                    if (!view && !verify) {
                        System.out.print(".");
                    }

                    //read here the seg info
                    boolean _nextArt = false;

                    int _segid = fin.read();
                    //System.out.println("SEG ID:"+_segid);
                    if (_segid == 1) {
                        _nextArt = true;
                        //System.out.println("ART Start:");
                    }
                    //else{
                    //System.out.println("NExt BLOCK:");
                    //}



                    //if dir read the artifact length
                    if (isDir && _nextArt) {
                        //System.out.println("a3");
                        //Reading Artifact Length
                        byte[] _rlengthB = new byte[2];
                        fin.read(_rlengthB);
                        ByteBuffer buf = ByteBuffer.wrap(_rlengthB);
                        short rlength = buf.getShort();

                        //System.out.println("a4");

                        //System.out.println(rlength);
                        //Reading Artifact
                        byte[] _artifact = new byte[rlength];
                        fin.read(_artifact);

                        //System.out.println("a5");
                        String artifact = new String(_artifact);
                        //System.out.println("Artifact: "+artifact);
                        //clean the string before writing
                        String cleaned = StringHelper.cleanString(artifact, fsep, File.separatorChar);
                        if (view) {
                            System.out.println("\r\n" + cleaned);
                            aIndex++;
                        }
                        if (verify) {
                            System.out.println("\r\nChecking..." + cleaned + " - ");
                            aIndex++;
                        }
                        if (find) {
                            //System.out.println("Cleaned: "+cleaned+" : "+ofile);
                            if ((cleaned.indexOf(ofile) != -1)) {
                                System.out.println("    " + cleaned);
                                aIndex++;
                            }
                        }

                        if (!view && !verify && !eSingle && !find) {
                            File _art = new File(ofile, cleaned);

                            File parent = new File(_art.getParent());
                            if (!parent.exists()) {
                                //System.out.println("Making dir for "+parent.getPath());
                                parent.mkdirs();
                            }
                            //System.out.println(_art.getParent());
                            fos = new FileOutputStream(_art);
                        }

                        if (eSingle) {
                            if (cleaned.equals(ofile)) {
                                File ini = new File("bar_ext");
                                ini.mkdir();
                                File esin = new File(ini, ofile);
                                File parent = new File(esin.getParent());
                                if (!parent.exists()) {
                                    //System.out.println("Making dir for "+parent.getPath());
                                    parent.mkdirs();
                                }
                                fos = new FileOutputStream(esin);
                                eSingleDone = true;
                            }
                        }

                        aSize = 0;
                        int _dupId = fin.read();
                    }

                    //System.out.println("a9");
                    //System.out.println("Input to BIS");
                    byte byt[] = null;
                    try {
                        byt = bin.read();
                    } catch (Exception e) {
                        System.out.println("\r\nThe Archive is corrupted!");
                        System.exit(0);
                    }

                    if (verify) {
                        aSize = aSize + byt.length;
                        System.out.println("\r\nB: " + aSize + " Bytes " + "(" + ByteDisp.convert((long) aSize) + ")");
                    }

                    //System.out.println("OP from  BIS");
                    if (!view && !verify && !find) {
                        if (eSingle) {
                            if (eSingleDone) {
                                fos.write(byt);
                                fos.close();
                                System.out.println("\r\nDone. " + ofile + " is extracted into BARRED temp. dir. bar_ext.");
                                System.exit(0);
                            }
                        } else {
                            fos.write(byt);
                        }
                    }


                    //if(isDir){
                    //fos.close();
                    //}
                }
                if (view || verify || find) {
                    System.out.println("\r\nTotal Files: " + aIndex);
                }


                fin.close();
                if (!isDir && !view && !find) {
                    fos.close();
                }
                if (!view && !verify && !eSingle && !find) {
                    System.out.println("\r\nSaved as: " + ofile);
                }
                if (eSingle && !eSingleDone) {
                    System.out.println("\r\nThe specified file: " + ofile + " is not in the archive " + ifile + ". Please use -v and view the archive content. Note down the prefix/suffix, if any and try again.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
