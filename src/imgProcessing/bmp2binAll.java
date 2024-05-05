package imgProcessing;

import java.io.*;

public class bmp2binAll
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
         BufferedReader in = new BufferedReader(new FileReader(args[0]));

         String outPath = args[1];
         if (!outPath.endsWith("/"))
         {
            outPath += "/";
         }

         String cmdStart = "java imgProcessing/BMP2OneByte";

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
            p.waitFor();
         }
      } //if (args.length < 1)...else
   } //public static void main(String[] args) throws IOException, InterruptedException

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
