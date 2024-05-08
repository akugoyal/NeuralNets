package imgProcessing;

import imgProcessing.PelArray.PelArray;

import java.io.*;

public class oneByteToPelArrayAll
{
   public static void main(String[] args) throws IOException
   {
      if (args.length < 4)
      {
         System.out.println("Missing one or more arguments: path to file containing input paths, " +
               "path to output directory, threshold value, gray/color");
      }
      else
      {
         if (!args[1].endsWith("/"))
         {
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
         while ((line = br.readLine()) != null)
         {
            System.out.println("\n" + line);
            fileName = extractFileName(line);
            DataInputStream in = new DataInputStream(new FileInputStream(line));

            String headerPath = args[1] + fileName + ".txt";
            BufferedWriter headerOut = new BufferedWriter(new FileWriter(headerPath));

            width = in.readInt();
            height = in.readInt();
            System.out.println(width + "x" + height);
            img = new int[height][width];

            for (int i = 0; i < height; i++)
            {
               for (int j = 0; j < width; j++)
               {
                  img[i][j] = in.readUnsignedByte();

                  if (img[i][j] > thresh)
                  {
                     img[i][j] = 255;
                  }
               }
            }

            pel = new PelArray(img);
//
//            pel = pel.onesComplimentImage();
//
//            pel = pel.pad(500, 500, 500, 500, pel.getPelArray()[5][50]);
//            moddedImg = pel.getPelArray();
//            width = moddedImg[0].length;
//            height = moddedImg.length;
//
//            xCOM = pel.getXcom();
//            yCOM = pel.getYcom();
//            System.out.println("Center of Mass: (" + xCOM + ", " + yCOM + ")");
//
//            xTL = Math.max(0, xCOM - xOFF);
//            yTL = Math.max(0, yCOM - yOFF);
//            xBR = Math.min(xCOM + xOFF, width - 1);
//            yBR = Math.min(yCOM + yOFF, height - 1);
//            pel = pel.crop(xTL, yTL, xBR, yBR);
//            pel = pel.offsetColors(rOFF, gOFF, bOFF);
//
//
//            ratio = ((double) xOFF * 2) / (yOFF * 2);
//            desiredWidth = (int) (ratio * desiredHeight + 0.5); //Rounds the number
//            System.out.println("Scaling to: " + desiredWidth + " x " + desiredHeight);
//            pel = pel.scale(desiredWidth, desiredHeight);

            moddedImg = pel.getPelArray();
            width = moddedImg[0].length;
            height = moddedImg.length;
            DataOutputStream out =
                  new DataOutputStream(new FileOutputStream(args[1] + fileName + ".bin"));
            for (int i = 0; i < height; i++)
            {
               for (int j = 0; j < width; j++)
               {
                  out.writeByte(moddedImg[i][j]);
               }
            }

            headerOut.write(width + "\n");
            headerOut.write(height + "\n");
            headerOut.write(args[3] + "\n");

            out.close();
            headerOut.close();

            System.out.println();
         }
         br.close();
      }
   }

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
}
