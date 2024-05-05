package imgProcessing;

import java.io.*;

public class bin2scaledBin
{
   public static void main(String[] args) throws IOException
   {
      if (args.length < 1)
      {
         System.out.println("Missing one or more arguments: path to file containing input paths, " +
               "path to output directory");
      }
      else
      {
//         if (!args[1].endsWith("/"))
//         {
//            args[1] = args[1] + "/";
//         }

         BufferedReader br = new BufferedReader(new FileReader(args[0]));

         byte[][] img;
         String fileName;



         String line;
         while ((line = br.readLine()) != null)
         {
            System.out.println("\n" + line);
            fileName = extractFileName(line);
            DataInputStream in = new DataInputStream(new FileInputStream(line));
//            DataOutputStream out =
//                  new DataOutputStream(new FileOutputStream(args[1] + fileName + ".bin"));

            img = new byte[100][83];

            for (int i = 0; i < 100; i++)
            {
               for (int j = 0; j < 83; j++)
               {
                  img[i][j] = in.readByte();
                  System.out.println(img[i][j]);
               }
            }

            in.close();
//            out.close();
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
