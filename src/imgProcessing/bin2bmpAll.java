package imgProcessing;

import imgProcessing.PelArray.PelArray;

import java.io.*;

public class bin2bmpAll
{
   public static void main(String[] args) throws IOException, InterruptedException
   {
      if (args.length < 2)
      {
         System.out.println("Missing one or more arguments: path to file containing input paths, " +
               "path to output directory");
      }
      else
      {
         String headerPath = args[0].substring(0, args[0].indexOf(".")) + ".txt";
         BufferedReader headerIn = new BufferedReader(new FileReader(headerPath));

         int width = Integer.parseInt(headerIn.readLine());
         int height = Integer.parseInt(headerIn.readLine());
         String type = headerIn.readLine().trim();

         String cmd = "java imgProcessing/BGR2BMP";

         Runtime r = Runtime.getRuntime();
         Process p =
               r.exec(cmd + " " + type + " " + width + " " + height + " " + args[0] + " " + args[1]);

         p.waitFor();

         headerIn.close();
      }
   }
}
