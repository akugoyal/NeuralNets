import java.math.BigDecimal;

public abstract class Util
{
   public static void exit(String msg) {
      System.out.println(msg);
      System.exit(1);
   }

   public static void exit(String msg, String fileName) {
      exit("File \"" + fileName + "\" - " + msg);
   }

   public static boolean toBoolean(String s)
   {
      return Boolean.parseBoolean(s);
   }

   public static int toInt(String s) throws NumberFormatException
   {
      return new BigDecimal(s).intValue();
   }

   public static double toDouble(String s) throws NumberFormatException
   {
      return new BigDecimal(s).doubleValue();
   }

   public static String newLine(String s) {
      return s + "\n";
   }
}
