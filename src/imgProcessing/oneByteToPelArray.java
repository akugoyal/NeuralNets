package imgProcessing;

import imgProcessing.PelArray.PelArray;

import java.io.*;

public class oneByteToPelArray
{
   public static void main(String[] args) throws IOException
   {
      String inPath = "/Users/akulgoyal/Desktop/NeuralNets/imgs/5F.txt";
      BufferedReader br = new BufferedReader(new FileReader(inPath));
      DataOutputStream out = new DataOutputStream(new FileOutputStream("/Users/akulgoyal/Desktop" +
            "/NeuralNets/bins/1A.bin"));

      int height = Integer.parseInt(br.readLine());
      int width = Integer.parseInt(br.readLine());
      int[][] img = new int[height][width];

      for (int i = 0; i < height; i++) {
         for (int j = 0; j < width; j++) {
            img[i][j] = Integer.parseInt(br.readLine());
            if (img[i][j] > Integer.parseInt(args[0])) {
               img[i][j] = 255;
            }
         }
      }

      PelArray pel = new PelArray(img);
//      pel = pel.onesComplimentImage();

      int[][] moddedImg = pel.getPelArray();
      for (int i = 0; i < height; i++) {
         for (int j = 0; j < width; j++) {
            out.writeByte(moddedImg[i][j]);
         }
      }

      out.close();
   }
}
