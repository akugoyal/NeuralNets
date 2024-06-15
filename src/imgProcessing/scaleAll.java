package imgProcessing;

import java.io.*;

/**
 * The scaleAll class reads image data from binary input files, scales the image values, and writes
 * the scaled data to binary output files.
 *
 * Usage: java scaleAll list_of_input_files output_directory
 *
 * Search for $$$ to adjust the width and height of the images and the scale factor.
 *
 * Author: Akul Goyal
 * Date Created: April 22, 2024
 * Date Modified: June 14, 2024
 */
public class scaleAll {

   /**
    * The main method processes images according to the provided command-line arguments.
    * It reads paths from an input file, scales the image values, and writes the results to an
    * output directory.
    *
    * @param args the command-line arguments:
    *             - args[0]: path to the file containing input paths
    *             - args[1]: path to the output directory
    * @throws IOException if an I/O error occurs
    */
   public static void main(String[] args) throws IOException {
      if (args.length != 2) {
         System.out.println("Usage: java scaleAll list_of_input_files output_directory");
      }
      if (!args[1].endsWith("/")) {
         args[1] = args[1] + "/";
      }
      BufferedReader br = new BufferedReader(new FileReader(args[0]));
      String line;
      String fileName;
      int width = 83; //$$$
      int height = 120; //$$$
      double scaleFactor = 255.0; //$$$
      int[][] img;

      while ((line = br.readLine()) != null) {
         System.out.println("\n" + line);
         fileName = extractFileName(line);
         DataInputStream in = new DataInputStream(new FileInputStream(line));

         img = new int[height][width];

         for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
               img[i][j] = in.readUnsignedByte();
            }
         }
         in.close();

         DataOutputStream out = new DataOutputStream(new FileOutputStream(args[1] + fileName + ".bin"));
         for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
               out.writeDouble(img[i][j] / scaleFactor);
            }
         }

         out.close();

         System.out.println();
      } //while ((line = br.readLine()) != null)

      br.close();
   } //public static void main(String[] args)

   /**
    * Extracts the file name from a given file path.
    *
    * @param line the file path
    * @return the extracted file name without the extension
    */
   public static String extractFileName(String line) {
      String res = line.substring(line.indexOf("/") + 1);

      int ind;
      while ((ind = res.indexOf("/")) != -1) {
         res = res.substring(ind + 1);
      }

      return res.substring(0, res.indexOf("."));
   } //public static String extractFileName(String line)
} //public class scaleAll