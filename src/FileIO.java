import java.io.*;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;

public class FileIO
{
   private int numLayers;
   private int numInAct;
   private int numHidAct;
   private int numOutAct;

   public FileIO(int numLayers, int numInAct, int numHidAct, int numOutAct) {
      this.numLayers = numLayers;
      this.numInAct = numInAct;
      this.numHidAct = numHidAct;
      this.numOutAct = numOutAct;
   }

   public boolean loadWeights(double[][] kjWeights, double[][] jiWeights, String fileName)
         throws FileNotFoundException
   {
      int i;
      int j;
      int k;
      int layers;
      int inAct;
      int hidAct;
      int outAct;

      Scanner scanner = new Scanner(new File(fileName));
      scanner.useDelimiter(Pattern.compile("\\p{javaWhitespace}*:*"));

/**
 * Reads the number of network layers from the file and compares it to the user set number of layers
 */
      if (scanner.hasNext("Network layers:"))
      {
         scanner.next("Network layers:");
         if (scanner.hasNext())
         {
            layers = 0;
            try
            {
               layers = Integer.parseInt(scanner.next().trim());
            }
            catch (NumberFormatException e)
            {
               System.out.println("Incorrectly formatted \"Network layers\" configuration.");
               System.exit(1);
            }
            if (layers != numLayers)
            {
               System.out.println("Error: \"Network layers\" mismatch. File " + fileName +
                     " configured for " + layers + " layers.");
               System.exit(1);
            }
         }
         else
         {
            System.out.println("Missing \"Network layers\" configuration.");
            System.exit(1);
         }
      }
      else
      {
         System.out.println("Missing \"Network layers\" tag.");
         System.exit(1);
      }
      scanner.reset();

/**
* Reads the network configuration from the file and compares it to the user set configuration
*/
      if (scanner.hasNext("Network configuration:"))
      {
         scanner.next("Network configuration:");
         if (scanner.hasNextInt())
         {
            inAct = scanner.nextInt();
            if (inAct != numInAct) {
               System.out.println("Error: Network configuration mismatch. File " + fileName +
                           " configured for " + inAct + " input activations");
               System.exit(1);
            }
            else
            {
               System.out.println("Missing input activations configuration");
               System.exit(1);
            }
            if (scanner.hasNextInt())
            {
               hidAct = scanner.nextInt();
            }
            else
            {
               System.out.println("Missing hidden activations configuration");
               System.exit(1);
            }
            if (scanner.hasNextInt())
            {
               outAct = scanner.nextInt();
            }
            else
            {
               System.out.println("Missing output activations configuration");
               System.exit(1);
            }

//            if (configMismatch)
//            {
//               System.out.println("Error: Network configuration mismatch. Weights configured for " + inAct + "-" + hidAct + "-" + outAct + ".");
//               System.exit(1);
//            }
         }
         else
         {
            System.out.println("Incorrectly formatted \"Network configuration\".");
            System.exit(1);
         }
      }
      else
      {
         System.out.println("Missing \"Network configuration\".");
         System.exit(1);
      }
      scanner.reset();

/**
 * Reads the network configuration from the file and compares it to the user set configuration
 */


/**
 * Reads the weights for the kj connectivity layer from the file
 */
      for (k = 0; k < numInAct; k++)
      {
         for (j = 0; j < numHidAct; j++)
         {
            if (scanner.hasNextDouble())
            {
               kjWeights[k][j] = scanner.nextDouble();
            }
            else
            {
               System.out.println("Error in weights at file at k = " + k + ", j = " + j);
               System.exit(1);
            }
         }
      }

/**
 * Reads the weights for the ji connectivity layer from the file
 */
      for (j = 0; j < numHidAct; j++)
      {
         for (i = 0; i < numOutAct; i++)
         {
            if (scanner.hasNextDouble())
            {
               jiWeights[j][i] = scanner.nextDouble();
            }
            else
            {
               System.out.println("Error in weights at file at j = " + j + ", i = " + i);
               System.exit(1);
            }
         } //for (i = 0; i < numOutAct; i++)
      } //for (j = 0; j < numHidAct; j++)

      System.out.println("Loaded weights successfully.");
      return true;
   }

   public boolean saveWeights(double[][] kjWeights, double[][] jiWeights, String fileName) throws IOException
   {
      int i;
      int j;
      int k;
      BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
      writer.write("Network layers: " + numLayers + "\n");
      writer.write("Network configuration: " + Integer.toString(numInAct) + " " +
            Integer.toString(numHidAct) + " " + Integer.toString(numOutAct) + "\n");

      for (k = 0; k < numInAct; k++)
      {
         for (j = 0; j < numHidAct; j++)
         {
            writer.write("kj(" + k + ", " + j + "): " + Double.toString(kjWeights[k][j]) + "\n");
         }
      }

      for (j = 0; j < numHidAct; j++)
      {
         for (i = 0; i < numOutAct; i++)
         {
            writer.write("ji(" + j + ", " + i + "): " + Double.toString(jiWeights[j][i]) + "\n");
         }
      }

      writer.flush();
      writer.close();
      System.out.println("Saved weights successfully.");
      return true;
   }
}
