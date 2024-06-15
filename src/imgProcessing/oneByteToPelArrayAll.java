package imgProcessing;

import imgProcessing.PelArray.PelArray;

import java.io.*;

/**
 * The oneByteToPelArrayAll class processes images by reading byte data from input files,
 * performing various operations (e.g., thresholding, padding, cropping, color offsetting, scaling),
 * and writing the modified data to output files.
 *
 * Usage: java oneByteToPelArrayAll input_paths_file output_directory threshold_value color_mode
 *
 * The processed images are saved as binary files in the output directory, and the dimensions and
 * color mode of each image are saved in a separate text file.
 *
 * Author: Akul Goyal
 * Date Created: April 22, 2024
 * Date Modified: June 14, 2024
 */
public class oneByteToPelArrayAll {

   /**
    * The main method processes images according to the provided command-line arguments.
    * It reads paths from an input file, processes each image, and writes the results to an output
    * directory.
    *
    * @param args the command-line arguments:
    *             - args[0]: path to the file containing input paths
    *             - args[1]: path to the output directory
    *             - args[2]: threshold value (or "x" for no threshold)
    *             - args[3]: color mode (gray/color)
    * @throws IOException if an I/O error occurs
    */
   public static void main(String[] args) throws IOException {
      if (args.length != 4) {
         System.out.println("Usage: java oneByteToPelArrayAll input_paths_file output_directory " +
               "threshold_value color_mode");
      } else {
         if (!args[1].endsWith("/")) {
            args[1] = args[1] + "/";
         }

         int thresh = ("x".equals(args[2].trim())) ? Integer.MAX_VALUE : Integer.parseInt(args[2]);
         BufferedReader br = new BufferedReader(new FileReader(args[0]));

         int width;
         int height;
         int xCOM;
         int yCOM;
         int xOFF = 600;
         int yOFF = 700;
         int padding = 500;
         int padColorRow = 5;
         int padColorCol = 5;
         int xTL;
         int yTL;
         int xBR;
         int yBR;
         int rOFF = -50;
         int gOFF = -50;
         int bOFF = -50;
         double ratio;
         int[][] img;
         int[][] moddedImg;
         int desiredHeight = 100;
         int desiredWidth;
         PelArray pel;
         String fileName;

         String line;
         while ((line = br.readLine()) != null) {
            System.out.println("\n" + line);
            fileName = extractFileName(line);
            DataInputStream in = new DataInputStream(new FileInputStream(line));

            String headerPath = args[1] + fileName + ".txt";
            BufferedWriter headerOut = new BufferedWriter(new FileWriter(headerPath));

            width = in.readInt();
            height = in.readInt();
            System.out.println(width + "x" + height);
            img = new int[height][width];

            for (int i = 0; i < height; i++) {
               for (int j = 0; j < width; j++) {
                  img[i][j] = in.readUnsignedByte();

                  if (img[i][j] > thresh) {
                     img[i][j] = 255;
                  }
               }
            }

            pel = new PelArray(img);

            pel = pel.onesComplimentImage();

            pel = pel.pad(padding, padding, padding, padding, pel.getPelArray()[padColorRow][padColorCol]);
            moddedImg = pel.getPelArray();
            width = moddedImg[0].length;
            height = moddedImg.length;

            xCOM = pel.getXcom();
            yCOM = pel.getYcom();
            System.out.println("Center of Mass: (" + xCOM + ", " + yCOM + ")");

            xTL = Math.max(0, xCOM - xOFF);
            yTL = Math.max(0, yCOM - yOFF);
            xBR = Math.min(xCOM + xOFF, width - 1);
            yBR = Math.min(yCOM + yOFF, height - 1);
            pel = pel.crop(xTL, yTL, xBR, yBR);
            pel = pel.offsetColors(rOFF, gOFF, bOFF);

            ratio = ((double) xOFF * 2) / (yOFF * 2);
            desiredWidth = (int) (ratio * desiredHeight + 0.5); // Rounds the number
            System.out.println("Scaling to: " + desiredWidth + " x " + desiredHeight);
            pel = pel.scale(desiredWidth, desiredHeight);

            moddedImg = pel.getPelArray();
            width = moddedImg[0].length;
            height = moddedImg.length;
            DataOutputStream out = new DataOutputStream(new FileOutputStream(args[1] + fileName + ".bin"));
            for (int i = 0; i < height; i++) {
               for (int j = 0; j < width; j++) {
                  out.writeByte(moddedImg[i][j]);
               }
            }

            headerOut.write(width + "\n");
            headerOut.write(height + "\n");
            headerOut.write(args[3] + "\n");

            out.close();
            headerOut.close();

            System.out.println();
         } //while ((line = br.readLine()) != null)
         br.close();
      } //if (args.length != 4)...else
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
} //public class oneByteToPelArrayAll