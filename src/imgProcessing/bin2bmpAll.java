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
               "path to output directory, scale factor");
      }
      else
      {
         if (!args[1].endsWith("/"))
         {
            args[1] = args[1] + "/";
         }

         BufferedReader br = new BufferedReader(new FileReader(args[0]));
         String fileName;
         int width;
         int height;
         String type;
         String cmd = "java imgProcessing/BGR2BMP";
         Runtime r;
         Process p;
         String outFile;

         String line;
         while ((line = br.readLine()) != null)
         {
            System.out.println(line);

            fileName = extractFileName(line);

            outFile = args[1] + fileName + ".bmp";

            String headerPath = line.substring(0, line.indexOf(".")) + ".txt";
            BufferedReader headerIn = new BufferedReader(new FileReader(headerPath));

            width = Integer.parseInt(headerIn.readLine());
            height = Integer.parseInt(headerIn.readLine());
            type = headerIn.readLine().trim();

            r = Runtime.getRuntime();
            p =
                  r.exec(cmd + " " + type + " " + width + " " + height + " " + line + " " + outFile);

            BufferedReader reader =
                  new BufferedReader(new InputStreamReader(p.getInputStream()));
            String ln;
            while ((ln = reader.readLine()) != null)
            {
               System.out.print(ln + "\n");
            }
            p.waitFor();

            headerIn.close();
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
