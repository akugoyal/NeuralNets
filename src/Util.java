public class Util
{
   public static void exit(String msg, int x) {
      System.out.println(msg);
      System.exit(1);
   }

   public static void exit(String msg, String fileName) {
      exit("\"" + fileName + "\" - " + msg);
   }

   public static boolean toBoolean(String s)
   {
      return Boolean.parseBoolean(s);
   }

   public static int toInt(String s) throws NumberFormatException
   {
      return Integer.parseInt(s);
   }

   public static double toDouble(String s) throws NumberFormatException
   {
      return Double.parseDouble(s);
   }
}
