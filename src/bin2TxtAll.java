import java.io.*;

public class bin2TxtAll
{
   public static void main(String[] args) throws IOException
   {
      if (args.length < 3)
      {
         System.out.println("Missing one or more arguments: path to file containing input paths, " +
               "path to output file, num bytes");
      }

      BufferedReader br = new BufferedReader(new FileReader(args[0]));
      String line;
      String fileName;
      BufferedWriter out = new BufferedWriter(new FileWriter(args[1]));

      while ((line = br.readLine()) != null)
      {
         DataInputStream in = new DataInputStream(new FileInputStream(line));

         for (int i = 0; i < Integer.parseInt(args[2]); i++)
         {
            int b = in.readUnsignedByte();
            out.write(b + "\n");
         }

         in.close();
      }

      out.close();

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
