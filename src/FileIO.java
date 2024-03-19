import java.io.*;
import java.util.Scanner;
import java.util.regex.Pattern;

public class FileIO
{
   private int numLayers;
   private int numInAct;
   private int numHidAct;
   private int numOutAct;
   private DataOutputStream out;
   private DataInputStream in;
   private String fileName;

   public FileIO(int numLayers, int numInAct, int numHidAct, int numOutAct, String fileName)
   {
      this.numLayers = numLayers;
      this.numInAct = numInAct;
      this.numHidAct = numHidAct;
      this.numOutAct = numOutAct;
      this.fileName = fileName;

      try
      {
         out = new DataOutputStream(new FileOutputStream(fileName));
         in = new DataInputStream(new FileInputStream(fileName));
      }
      catch (FileNotFoundException e)
      {
         e.printStackTrace();
      }
   }

   public boolean loadWeights(double[][] kjWeights, double[][] jiWeights, String fileName)
         throws FileNotFoundException
   {
      int layers;
      int inAct;
      int hidAct;
      int outAct;
      String read;
      Scanner scanner;

/**
 * Reads the number of network layers from the file and compares it to the user set number of layers
 */
      scanner = new Scanner(new File(fileName));
      scanner.useDelimiter(Pattern.compile("(\\p{javaWhitespace}|:|\\(|\\)|,)+"));

      if (scanner.hasNext("NetworkLayers"))
      {
         scanner.next("NetworkLayers");
         if (scanner.hasNext())
         {
            layers = 0;
            try
            {
               layers = Integer.parseInt(scanner.next().trim());
            }
            catch (NumberFormatException e)
            {
               System.out.println("Error: Incorrectly formatted \"NetworkLayers\" configuration.");
               System.exit(1);
            }
            if (layers != numLayers)
            {
               System.out.println("Error: \"NetworkLayers\" mismatch. File " + fileName +
                     " configured for " + layers + " layers.");
               System.exit(1);
            }
         }
         else
         {
            System.out.println("Error: Missing \"NetworkLayers\" configuration.");
            System.exit(1);
         }
      }
      else
      {
         System.out.println("Error: Missing \"NetworkLayers\" tag.");
         System.exit(1);
      }

/**
 * Reads the network configuration from the file and compares it to the user set configuration
 */
      if (scanner.hasNext("NetworkConfiguration"))
      {
         scanner.next("NetworkConfiguration");
         if (scanner.hasNext())
         {
            try
            {
               inAct = Integer.parseInt(scanner.next().trim());
               if (inAct != numInAct)
               {
                  System.out.println("Error: Network configuration mismatch. File " + fileName +
                        " configured for " + inAct + " input activations");
                  System.exit(1);
               }
            }
            catch (NumberFormatException e)
            {
               System.out.println("Error: Incorrectly formatted \"NetworkConfiguration\".");
               System.exit(1);
            }
            if (scanner.hasNext())
            {
               try
               {
                  hidAct = Integer.parseInt(scanner.next().trim());
                  if (hidAct != numHidAct)
                  {
                     System.out.println("Error: Network configuration mismatch. File " + fileName +
                           " configured for " + hidAct + " hidden activations");
                     System.exit(1);
                  }
               }
               catch (NumberFormatException e)
               {
                  System.out.println("Error: Incorrectly formatted \"NetworkConfiguration\".");
                  System.exit(1);
               }
            }
            else
            {
               System.out.println("Error: Missing hidden activations configuration");
               System.exit(1);
            }
            if (scanner.hasNext())
            {
               try
               {
                  outAct = Integer.parseInt(scanner.next().trim());
                  if (outAct != numOutAct)
                  {
                     System.out.println("Error: Network configuration mismatch. File " + fileName +
                           " configured for " + outAct + " output activations");
                     System.exit(1);
                  }
               }
               catch (NumberFormatException e)
               {
                  System.out.println("Error: Incorrectly formatted \"NetworkConfiguration\".");
                  System.exit(1);
               }
            }
            else
            {
               System.out.println("Error: Missing output activations configuration");
               System.exit(1);
            }
         }
         else
         {
            System.out.println("Error: Missing input activations configuration");
            System.exit(1);
         }
      }
      else
      {
         System.out.println("Error: Missing \"NetworkConfiguration\" tag.");
         System.exit(1);
      }

/**
 * Reads the weights from file and stores them
 */
      while (scanner.hasNext())
      {
         read = scanner.next();
         if (read.startsWith("kj"))
         {
            if (readWeight(kjWeights, scanner))
            {
               System.out.println("Error: Incorrectly formatted kj weight.");
               System.exit(1);
            }
         }

         if (read.startsWith("ji"))
         {
            if (readWeight(jiWeights, scanner))
            {
               System.out.println("Error: Incorrectly formatted ji weight.");
               System.exit(1);
            }
         }
      }

      System.out.println("Loaded weights successfully.");
      return true;
   }

   private boolean readWeight(double[][] weights, Scanner scanner)
   {
      boolean badFormat;
      int x;
      int y;
      badFormat = false;
      if (scanner.hasNextInt())
      {
         x = scanner.nextInt();
         if (scanner.hasNextInt())
         {
            y = scanner.nextInt();
            if (scanner.hasNext())
            {
               try
               {
                  weights[x][y] = Double.parseDouble(scanner.next().trim());
               }
               catch (NumberFormatException e)
               {
                  badFormat = true;
               }
            }
            else
            {
               badFormat = true;
            }
         }
         else
         {
            badFormat = true;
         }
      }
      else
      {
         badFormat = true;
      }
      return badFormat;
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

   public boolean mySaveWeights(double[][] kjWeights, double[][] jiWeights)
   {
      int k;
      int j;
      int i;

      try
      {
         out.writeInt(numInAct);
         out.writeInt(numHidAct);
         out.writeInt(numOutAct);
      }
      catch (IOException e)
      {
         Exit.exit("Error writing network configuration to weights file: " + fileName);
      }

      for (k = 0; k < numInAct; k++)
      {
         for (j = 0; j < numHidAct; j++)
         {
            try
            {
               out.writeDouble(kjWeights[k][j]);
            }
            catch (IOException e)
            {
               Exit.exit("Error writing kjWeights[" + k + "][" + j + "] to weights file: " + fileName);
            }
         }
      }

      for (j = 0; j < numHidAct; j++)
      {
         for (i = 0; i < numOutAct; i++)
         {
            try
            {
               out.writeDouble(jiWeights[j][i]);
            }
            catch (IOException e)
            {
               Exit.exit("Error writing jiWeights[" + j + "][" + i + "] to weights file: " + fileName);
            }
         }
      }

      return true;
   }

   public boolean myLoadWeights(double[][] kjWeights, double[][] jiWeights)
   {
      int inActsRead;
      int hidActsRead;
      int outActsRead;
      int k;
      int j;
      int i;

      try
      {
         inActsRead = in.readInt();
         hidActsRead = in.readInt();
         outActsRead = in.readInt();
         if (inActsRead != numInAct || hidActsRead != numHidAct || outActsRead != numOutAct) {
            Exit.exit("Network config doesn't match weights config in weights file: " + fileName);
         }
      } catch (IOException e) {
         Exit.exit("Error reading config from weights file: " + fileName);
      }

      for (k = 0; k < numInAct; k++) {
         for (j = 0; j < numHidAct; j++) {
            try
            {
               kjWeights[k][j] = in.readDouble();
            }
            catch (IOException e)
            {
               Exit.exit("Error reading kjWeights[" + k + "][" + j + "] from weights file: " + fileName);
            }
         }
      }

      for (j = 0; j < numHidAct; j++) {
         for (i = 0; i < numOutAct; i++) {
            try
            {
               jiWeights[j][i] = in.readDouble();
            }
            catch (IOException e)
            {
               Exit.exit("Error reading jiWeights[" + j + "][" + i + "] from weights file: " + fileName);
            }
         }
      }

      return true;
   }
}
