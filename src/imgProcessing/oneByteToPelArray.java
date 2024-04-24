package imgProcessing;

import imgProcessing.PelArray.PelArray;

import java.io.*;

public class oneByteToPelArray
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
         int thresh = Integer.parseInt(args[2]);
//         BufferedReader br = new BufferedReader(new FileReader(args[0]));
         String line = args[0]; //$

//         while ((line = br.readLine()) != null)
//         {
            DataInputStream in = new DataInputStream(new FileInputStream(line));
            DataOutputStream out = new DataOutputStream(new FileOutputStream(args[1]));

            String headerPath = args[1].substring(0, args[1].indexOf(".")) + ".txt";
            BufferedWriter headerOut = new BufferedWriter(new FileWriter(headerPath));

            int width = in.readInt();
            int height = in.readInt();
            int[][] img = new int[height][width];

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

            PelArray pel = new PelArray(img);
            pel = pel.onesComplimentImage();
            int x = pel.getXcom();
            int y = pel.getYcom();
            System.out.println(x);
            System.out.println(y);

            int xOff = 770;
            int yOff = 900;
            int xL = Math.max(0, x - xOff);
            int yL = Math.max(0, y - yOff);
            int xR = Math.min(x + xOff, width - 1);
            int yR = Math.min(y + yOff, height - 1);
            pel = pel.crop(xL, yL, xR, yR);
            pel = pel.offsetColors(-100, -100, -100);


            double ratio = ((double) xOff * 2) / (yOff * 2);
            System.out.println(ratio);
            int desiredHeight = 100;
            int desiredWidth = (int) (ratio * desiredHeight + 0.5); //Rounds the number
            System.out.println(desiredWidth);
            pel = pel.scale(desiredWidth, desiredHeight);

            int[][] moddedImg = pel.getPelArray();
            width = moddedImg[0].length;
            height = moddedImg.length;
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
//         }
//         br.close();
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
   }
}
