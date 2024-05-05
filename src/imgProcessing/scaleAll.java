package imgProcessing;

import imgProcessing.PelArray.PelArray;

import java.io.*;

public class scaleAll
{
   public static void main(String[] args) throws IOException
   {
      if (args.length < 2)
      {
         System.out.println("Missing one or more arguments: path to file containing input paths, " +
               "path to output directory");
      }
      if (!args[1].endsWith("/"))
      {
         args[1] = args[1] + "/";
      }
      BufferedReader br = new BufferedReader(new FileReader(args[0]));
      String line;
      String fileName;
      int width;
      int height;
      int[][] img;

      while ((line = br.readLine()) != null)
      {
         System.out.println("\n" + line);
         fileName = extractFileName(line);
         DataInputStream in = new DataInputStream(new FileInputStream(line));

         width = 83;
         height = 100;
         img = new int[height][width];

         for (int i = 0; i < height; i++)
         {
            for (int j = 0; j < width; j++)
            {
               img[i][j] = in.readUnsignedByte();
            }
         }
         in.close();

         DataOutputStream out =
               new DataOutputStream(new FileOutputStream(args[1] + fileName + ".bin"));
         for (int i = 0; i < height; i++)
         {
            for (int j = 0; j < width; j++)
            {
               out.writeDouble(img[i][j] / 255.0);
            }
         }

         out.close();

         System.out.println();
      }

      br.close();
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
