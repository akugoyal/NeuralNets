import java.io.*;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * Class to read and write weights to a file. Expects the weights file to be a binary file
 * adhering to the following format:
 *
 * The first three integers are the number of input, hidden, and output activations, respectively.
 * The remaining data are the kjWeights and the jiWeights weights written in row major order.
 *
 * Table of Contents:
 * 1. WeightsFileIO(int[] numActsInLayers, String fileName)
 * 2. void saveWeights(double[][][] w)
 * 3. void loadWeights(double[][][] w)
 *
 * Author: Akul Goyal
 * Date of Creation: 03/19/2024
 */
public class WeightsFileIO
{
   private Config config;
   private int[] numActsInLayers;
   private DataOutputStream out;
   private DataInputStream in;
   private String fileName;

   /**
    * Constructor to initialize the expected number of activations in each layer and the file name.
    *
    * @param fileName        the name of the file to read/write weights
    * @param config          the Config object representing the network configuration
    */
   public WeightsFileIO(String fileName, Config config)
   {
      this.numActsInLayers = config.numActsInLayers;
      this.fileName = fileName;
      this.config = config;
   } //public WeightsFileIO(int[] numActsInLayers, String fileName, Config config)

   /**
    * Method to save the weights to the binary file in a format that is compatible with the
    * loadWeights method.
    *
    * @param w the weights to save
    */
   public void saveWeights(double[][][] w)
   {
      int n;
      int k;
      int j;

      ByteArrayOutputStream b = new ByteArrayOutputStream();
      out = new DataOutputStream(b);
      DataOutputStream out1 = null;

      try
      {
         out1 = new DataOutputStream(new FileOutputStream(fileName));
      }
      catch (FileNotFoundException e)
      {
         Util.exit("Failed to write to weights file", fileName);
      }

      try
      {
         for (n = config.INPUT_LAYER; n <= config.OUTPUT_LAYER; n++)
         {
            out.writeInt(numActsInLayers[n]);
         }
      }
      catch (IOException e)
      {
         Util.exit("Error writing network configuration", fileName);
      }

      for (n = config.INPUT_LAYER; n <= config.LAST_HIDDEN_LAYER; n++)
      {
         for (k = 0; k < numActsInLayers[n]; k++)
         {
            for (j = 0; j < numActsInLayers[n + 1]; j++)
            {
               try
               {
                  out.writeDouble(w[n][k][j]);
               }
               catch (IOException e)
               {
                  Util.exit("Error writing weights[" + n + "][" + k + "][" + j + "]", fileName);
               }
            } //for (j = 0; j < numActsInLayers[n + 1]; j++)
         } //for (k = 0; k < numActsInLayers[n]; k++)
      } //for (n = config.INPUT_LAYER; n <= config.LAST_HIDDEN_LAYER; n++)

      try
      {
         byte[] bArr = b.toByteArray();
         out.close();
         b.close();
         out1.write(bArr);
         out1.close();
      }
      catch (IOException e)
      {
         Util.exit("Error closing output stream", fileName);
      }
   } //public void saveWeights(double[][][] w)

   /**
    * Method to load the weights from the binary file. The weights are loaded into the provided
    * array.
    *
    * @param w the array to load the weights into
    */
   public void loadWeights(double[][][] w)
   {
      int[] layersRead;
      int n;
      int k;
      int j;

      try
      {
         in = new DataInputStream(new FileInputStream(fileName));
      }
      catch (FileNotFoundException e)
      {
         Util.exit("Failed to open weights file", fileName);
      }

      try
      {
         layersRead = new int[config.numActLayers];
         for (n = config.INPUT_LAYER; n <= config.OUTPUT_LAYER; n++)
         {
            layersRead[n] = in.readInt();
         }
         if (!Arrays.equals(layersRead, numActsInLayers))
         {
            Util.exit("Network config doesn't match weights config from file", fileName);
         }
      } //try
      catch (IOException e)
      {
         Util.exit("Error reading config", fileName);
      }

      for (n = config.INPUT_LAYER; n <= config.LAST_HIDDEN_LAYER; n++)
      {
         for (k = 0; k < numActsInLayers[n]; k++)
         {
            for (j = 0; j < numActsInLayers[n + 1]; j++)
            {
               try
               {
                  w[n][k][j] = in.readDouble();
               }
               catch (IOException e)
               {
                  Util.exit("Error reading mkWeights[" + k + "][" + j + "]", fileName);
               }
            } //for (j = 0; j < numActsInLayers[n + 1]; j++)
         } //for (k = 0; k < numActsInLayers[n]; k++)
      } //for (n = config.INPUT_LAYER; n <= config.LAST_HIDDEN_LAYER; n++)

      try
      {
         in.close();
      }
      catch (IOException e)
      {
         Util.exit("Error closing input stream", fileName);
      }
   } //public void loadWeights(double[][][] w)
} //public class WeightsFileIO
