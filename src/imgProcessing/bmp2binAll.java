package imgProcessing;

/**
 *
 * bmp2binAll.java
 *
 * This code reads in a list of BMP file paths and converts each one into a binary file.
 *
 * Usage: java bmp2binAll input_paths_file output_directory
 *
 * If two arguments are not passed, then a usage message is presented to the user.
 *
 * The program expects each BMP image file to be converted to a corresponding binary file.
 *
 * Example:
 * If you have a BMP file named "image1.bmp", it will be converted to "image1.bin" in the specified output directory.
 *
 * Relies on BMP2OneByte.java to perform the conversion. The BMP2OneByte class should be in the
 * same directory as this class, else change its location at the line marked with "CHANGE THIS".
 *
 * Author: Akul Goyal
 * Date Created: April 18, 2024
 * Date Modified: June 14, 2024
 */

import java.io.*;

public class bmp2binAll
{
   /**
    * The main method that drives the conversion process from BMP to binary files.
    *
    * @param args Command line arguments:
    *             args[0] - Path to the file containing input BMP file paths
    *             args[1] - Path to the output directory where binary files will be saved
    * @throws IOException If an I/O error occurs during file operations
    * @throws InterruptedException If the process is interrupted during execution
    */
   public static void main(String[] args) throws IOException, InterruptedException
   {
      // Check if the number of arguments is correct
      if (args.length != 2)
      {
         System.out.println("Usage: java bmp2binAll input_paths_file output_directory");
      }
      else
      {
         // Read the input paths file
         BufferedReader in = new BufferedReader(new FileReader(args[0]));

         // Ensure the output directory path ends with a '/'
         String outPath = args[1];
         if (!outPath.endsWith("/"))
         {
            outPath += "/";
         }

         // Command to run the conversion utility
         String cmdStart = "java BMP2OneByte"; // CHANGE THIS

         String line;
         String outFile;
         Process p = null;

         while ((line = in.readLine()) != null)
         {
            line = line.trim();
            System.out.println("\n" + line);
            Runtime r = Runtime.getRuntime();
            outFile = outPath + extractFileName(line) + ".bin";
            p = r.exec(cmdStart + " " +  line + " " + outFile);

            // Read and print the output from the conversion process
            BufferedReader reader =
                  new BufferedReader(new InputStreamReader(p.getInputStream()));

            String ln;
            while ((ln = reader.readLine()) != null)
            {
               System.out.print(ln + "\n");
            }
         } //while ((line = in.readLine()) != null)

         if (p != null)
         {
            p.waitFor(); // Wait for the process to complete
         }
      } //if (args.length != 2)...else
   } //public static void main(String[] args)

   /**
    * Extracts the file name without extension from a given path.
    *
    * @param line The full path to the file
    * @return The file name without the extension
    */
   public static String extractFileName(String line)
   {
      String res = line.substring(line.indexOf("/") + 1);

      int ind;
      while ((ind = res.indexOf("/")) != -1)
      {
         res = res.substring(ind + 1);
      }

      return res.substring(0, res.indexOf("."));
   } //public static String extractFileName(String line)
} //public class bmp2binAll