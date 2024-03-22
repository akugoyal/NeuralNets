import java.math.BigDecimal;

/**
 * Utility class for common operations. Supports conversion of strings to booleans, integers, and
 * doubles. It also provides a method to exit the program with an error message and a method to
 * add a new line to the end of a string.
 *
 * Author: Akul Goyal
 * Date of Creation: 03/19/2024
 */
public abstract class Util
{

/**
 * Exits the program with an error message.
 *
 * @param msg      The error message.
 * @param fileName The name of the file that caused the error.
 */
   public static void exit(String msg, String fileName)
   {
      System.out.println("File \"" + fileName + "\" - " + msg);
      System.exit(1);
   }

/**
 * Converts a string to a boolean.
 *
 * @param s The string to convert.
 * @return The boolean value of the string.
 */
   public static boolean toBoolean(String s)
   {
      return Boolean.parseBoolean(s);
   }

/**
 * Converts a string to an integer.
 *
 * @param s The string to convert.
 * @return The integer value of the string.
 * @throws NumberFormatException If the string is not a valid integer.
 */
   public static int toInt(String s) throws NumberFormatException
   {
      return new BigDecimal(s).intValue();
   }

/**
 * Converts a string to a double.
 *
 * @param s The string to convert.
 * @return The double value of the string.
 * @throws NumberFormatException If the string is not a valid double.
 */
   public static double toDouble(String s) throws NumberFormatException
   {
      return new BigDecimal(s).doubleValue();
   }

/**
 * Adds a new line to the end of a string.
 *
 * @param s The string to add a new line to.
 * @return The string with a new line at the end.
 */
   public static String newLine(String s)
   {
      return s + "\n";
   }
}
