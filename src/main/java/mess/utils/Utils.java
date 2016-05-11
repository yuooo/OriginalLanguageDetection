package mess.utils;

/**
 * Created by jessicahoffmann on 02/05/2016.
 */
public class Utils {
    private static long m_start_time = 0;
    private static long m_end_time = 0;

    public static void T() {
        m_start_time = System.currentTimeMillis();
    }

    public static void pT() {
        m_end_time = System.currentTimeMillis();
        System.out.println("Total execution time: " + (m_end_time - m_start_time)/1000 + "s.\n" );
    }

    public static void pT(String s) {
        m_end_time = System.currentTimeMillis();
        System.out.println(s + " " + (m_end_time - m_start_time)/1000 + "s.\n");
    }


}
