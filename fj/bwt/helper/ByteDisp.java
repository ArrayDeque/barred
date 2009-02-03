package fj.bwt.helper;

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
