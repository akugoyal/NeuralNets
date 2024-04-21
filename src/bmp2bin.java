import java.io.*;

public class bmp2bin
{
   public static void main(String[] args) throws IOException, InterruptedException
   {

      String[] alpha = new String[] {"A", "B", "C", "D", "E", "F"};
      String start = "java BMP2OneByte ";
      String inPath = "/Users/akulgoyal/Downloads/bmps/";
      String outPath = "/Users/akulgoyal/Desktop/NeuralNets/imgs/";
      for (int i = 1; i < 6; i++)
      {
         for (int j = 0; j < 6; j++)
         {
            Runtime r = Runtime.getRuntime();
            String in = inPath + i + alpha[j] + ".bmp ";
            String out = outPath + i + alpha[j] + ".bin";
            Process p = r.exec(start + in + out);

            BufferedReader reader =
                  new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while((line = reader.readLine()) != null) {
               System.out.print(line + "\n");
            }

            p.waitFor();
         }
      }
   }
}
