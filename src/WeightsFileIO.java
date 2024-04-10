import java.io.*;
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
   private int[] numActsInLayers;
   private DataOutputStream out;
   private DataInputStream in;
   private String fileName;

/**
 * Constructor to initialize the expected number of activations in each layer and the file name.
 *
 * @param numActsInLayers the number of activations in each layer
 * @param fileName        the name of the file to read/write weights
 */
   public WeightsFileIO(int[] numActsInLayers, String fileName)
   {
      this.numActsInLayers = numActsInLayers;
      this.fileName = fileName;
   } //public WeightsFileIO(int[] numActsInLayers, String fileName)

/**
 * Method to save the weights to the binary file in a format that is compatible with the
 * loadWeights method.
 *
 * @param w the weights to save
 */
   public void saveWeights(double[][][] w)
   {
      int n;
      int m;
      int k;
      int j;
      int i;

      try
      {
         out = new DataOutputStream(new FileOutputStream(fileName));
      }
      catch (FileNotFoundException e)
      {
         Util.exit("Failed to write to weights file", fileName);
      }

      try
      {
         n = Config.INPUT_LAYER;
         out.writeInt(numActsInLayers[n]);
         n = Config.HIDDEN_LAYER1;
         out.writeInt(numActsInLayers[n]);
         n = Config.HIDDEN_LAYER2;
         out.writeInt(numActsInLayers[n]);
         n = Config.OUTPUT_LAYER;
         out.writeInt(numActsInLayers[n]);
      }
      catch (IOException e)
      {
         Util.exit("Error writing network configuration", fileName);
      }

      n = Config.INPUT_LAYER;
      for (m = 0; m < numActsInLayers[n]; m++)
      {
         for (k = 0; k < numActsInLayers[n + 1]; k++)
         {
            try
            {
               out.writeDouble(w[n][m][k]);
            }
            catch (IOException e)
            {
               Util.exit("Error writing weights[" + n + "][" + m + "][" + k + "]", fileName);
            }
         } //for (k = 0; k < numActsInLayers[n + 1]; k++)
      } //for (m = 0; m < numActsInLayers[n]; m++)

      n = Config.HIDDEN_LAYER1;
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

      n = Config.HIDDEN_LAYER2;
      for (j = 0; j < numActsInLayers[n]; j++)
      {
         for (i = 0; i < numActsInLayers[n + 1]; i++)
         {
            try
            {
               out.writeDouble(w[n][j][i]);
            }
            catch (IOException e)
            {
               Util.exit("Error writing weights[" + n + "][" + j + "][" + i + "]", fileName);
            }
         } //for (i = 0; i < numActsInLayers[n + 1]; i++)
      } //for (j = 0; j < numActsInLayers[n]; j++)

      try
      {
         out.close();
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
      int m;
      int k;
      int j;
      int i;

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
         layersRead = new int[]{in.readInt(), in.readInt(), in.readInt(), in.readInt()};
         if (!Arrays.equals(layersRead, numActsInLayers))
         {
            Util.exit("Network config doesn't match weights config from file", fileName);
         }
      } //try
      catch (IOException e)
      {
         Util.exit("Error reading config", fileName);
      }

      n = Config.INPUT_LAYER;
      for (m = 0; m < numActsInLayers[n]; m++)
      {
         for (k = 0; k < numActsInLayers[n + 1]; k++)
         {
            try
            {
               w[n][m][k] = in.readDouble();
            }
            catch (IOException e)
            {
               Util.exit("Error reading mkWeights[" + m + "][" + k + "]", fileName);
            }
         } //for (k = 0; k < numActsInLayers[n + 1]; k++)
      } //for (m = 0; m < numActsInLayers[n]; m++)

      n = Config.HIDDEN_LAYER1;
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
               Util.exit("Error reading kjWeights[" + k + "][" + j + "]", fileName);
            }
         } //for (j = 0; j < numActsInLayers[n + 1]; j++)
      } //for (k = 0; k < numActsInLayers[n]; k++)

      n = Config.HIDDEN_LAYER2;
      for (j = 0; j < numActsInLayers[n]; j++)
      {
         for (i = 0; i < numActsInLayers[n + 1]; i++)
         {
            try
            {
               w[n][j][i] = in.readDouble();
            }
            catch (IOException e)
            {
               Util.exit("Error reading jiWeights[" + j + "][" + i + "]", fileName);
            }
         } //for (i = 0; i < numActsInLayers[n + 1]; i++)
      } //for (j = 0; j < numActsInLayers[n]; j++)

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
