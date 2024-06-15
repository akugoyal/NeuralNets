package imgProcessing;

/**
 * bin2bmpAll.java
 *
 * Author Akul Goyal
 * Date Created: April 23, 2024
 * Date Modified: June 14, 2024
 *
 * This code reads in a list of binary file paths which represent either gray scale (1 byte)
 * or BGR color (3 bytes) images and converts each one into a BMP file.
 *
 *
 * Usage: java bin2bmpAll input_paths_file output_directory
 *
 * If two arguments are not passed, then a usage message is presented to the user.
 *
 * Each binary image file must have a corresponding header file with the same base name
 * and a ".txt" extension. The header file must contain the following information:
 *
 * Line 1: Width of the image (integer)
 * Line 2: Height of the image (integer)
 * Line 3: Type of the image (string, either "gray" for grayscale or "BGR" for BGR color)
 *
 * Example header file content for a BGR image:
 * 640
 * 480
 * BGR
 *
 * Example header file content for a grayscale image:
 * 640
 * 480
 * gray
 *
 * Relies on the BGR2BMP class to convert the binary image to a BMP file. BGR2BMP must be in the
 * same directory as this class, else change its location at the line marked with "CHANGE THIS".
 */

import java.io.*;

public class bin2bmpAll
{
   /**
    * Reads in a text file containing paths to binary image files and converts each one to a BMP
    * file. Reads the header file (path determined from binary file name) for each binary image to
    * determine the width, height, and type of image (gray or BGR). Gives a command line call to
    * the BGR2BMP class to convert each binary image to a BMP file, saved in the output directory.
    *
    * @param args - two arguments: path to file containing input paths, path to output directory
    * @throws IOException - if an I/O error occurs
    * @throws InterruptedException - if a thread is interrupted
    */
   public static void main(String[] args) throws IOException, InterruptedException
   {
      // Check if the number of arguments is correct
      if (args.length != 2)
      {
         System.out.println("Usage: java bin2bmpAll input_paths_file output_directory");
      }
      else
      {
         // Ensure the output directory path ends with a '/'
         if (!args[1].endsWith("/"))
         {
            args[1] = args[1] + "/";
         }

         // Read the input paths file
         BufferedReader br = new BufferedReader(new FileReader(args[0]));
         String fileName;
         int width;
         int height;
         String type;
         String cmd = "java BGR2BMP"; // Command to run the conversion utility - CHANGE THIS
         Runtime r;
         Process p;
         String outFile;

         String line;
         while ((line = br.readLine()) != null)
         {
            System.out.println(line);

            // Extract the file name from the path
            fileName = extractFileName(line);

            // Construct the output file path
            outFile = args[1] + fileName + ".bmp";

            // Construct the header file path and read the image properties
            String headerPath = line.substring(0, line.indexOf(".")) + ".txt";
            BufferedReader headerIn = new BufferedReader(new FileReader(headerPath));

            width = Integer.parseInt(headerIn.readLine()); // Read the width
            height = Integer.parseInt(headerIn.readLine()); // Read the height
            type = headerIn.readLine().trim(); // Read the image type (e.g., BGR, gray)

            // Execute the command to convert the binary file to BMP format
            r = Runtime.getRuntime();
            p = r.exec(cmd + " " + type + " " + width + " " + height + " " + line + " " + outFile);

            // Read and print the output from the conversion process
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String ln;
            while ((ln = reader.readLine()) != null)
            {
               System.out.println(ln);
            }
            p.waitFor(); // Wait for the process to complete
         } //while ((line = br.readLine()) != null)

         br.close(); // Close the BufferedReader
      } //if (args.length != 2)...else
   } //public static void main(String[] args) throws IOException, InterruptedException

   /**
    * Extracts the file name, without the extension, from a given path.
    *
    * @param path The full path to the file
    * @return The file name extracted from the path
    */
   public static String extractFileName(String path)
   {
      String res = path.substring(path.indexOf("/") + 1);

      int ind;
      while ((ind = res.indexOf("/")) != -1)
      {
         res = res.substring(ind + 1);
      }

      return res.substring(0, res.indexOf("."));
   } //public static String extractFileName(String path)
} //public class bin2bmpAll
