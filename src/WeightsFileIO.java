import java.io.*;

/**
 * Class to read and write weights to a file. Expects the weights file to be a binary file
 * adhering to the following format:
 *
 * The first three integers are the number of input, hidden, and output activations, respectively.
 * The remaining data are the kjWeights and the jiWeights weights written in row major order.
 *
 * Author: Akul Goyal
 * Date of Creation: 03/19/2024
 */
public class WeightsFileIO
{
   public static final int MK_LAYER = 0;
   public static final int KJ_LAYER = 1;
   public static final int JI_LAYER = 2;
   private int numInAct;
   private int numHid1Act;
   private int numHid2Act;
   private int numOutAct;
   private DataOutputStream out;
   private DataInputStream in;
   private String fileName;

/**
 * Constructor to initialize the expected number of input, hidden, and output activations and
 * the file name.
 *
 * @param numInAct  the expected number of input activations
 * @param numHid1Act the expected number of hidden activations
 * @param numOutAct the expected number of output activations
 * @param fileName  the name of the file to read/write weights
 */
   public WeightsFileIO(int numInAct, int numHid1Act, int numHid2Act, int numOutAct,
                        String fileName)
   {
      this.numInAct = numInAct;
      this.numHid1Act = numHid1Act;
      this.numHid2Act = numHid2Act;
      this.numOutAct = numOutAct;
      this.fileName = fileName;
   } //public WeightsFileIO(int numInAct, int numHidAct, int numOutAct, String fileName)

/**
 * Method to save the weights to the binary file in a format that is compatible with the
 * loadWeights method.
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
         out.writeInt(numInAct);
         out.writeInt(numHid1Act);
         out.writeInt(numHid2Act);
         out.writeInt(numOutAct);
      }
      catch (IOException e)
      {
         Util.exit("Error writing network configuration", fileName);
      }

      n = MK_LAYER;
      for (m = 0; m < numInAct; m++)
      {
         for (k = 0; k < numHid1Act; k++)
         {
            try
            {
               out.writeDouble(w[n][m][k]);
            }
            catch (IOException e)
            {
               Util.exit("Error writing mkWeights[" + m + "][" + k + "]", fileName);
            }
         } //for (j = 0; j < numHidAct; j++)
      } //for (k = 0; k < numInAct; k++)

      n = KJ_LAYER;
      for (k = 0; k < numHid1Act; k++)
      {
         for (j = 0; j < numHid2Act; j++)
         {
            try
            {
               out.writeDouble(w[n][k][j]);
            }
            catch (IOException e)
            {
               Util.exit("Error writing kjWeights[" + k + "][" + j + "]", fileName);
            }
         } //for (j = 0; j < numHidAct; j++)
      } //for (k = 0; k < numInAct; k++)

      n = JI_LAYER;
      for (j = 0; j < numHid2Act; j++)
      {
         for (i = 0; i < numOutAct; i++)
         {
            try
            {
               out.writeDouble(w[n][j][i]);
            }
            catch (IOException e)
            {
               Util.exit("Error writing jiWeights[" + j + "][" + i + "]", fileName);
            }
         } //for (i = 0; i < numOutAct; i++)
      } //for (j = 0; j < numHidAct; j++)

      try
      {
         out.close();
      }
      catch (IOException e)
      {
         Util.exit("Error closing output stream", fileName);
      }
   } //public void saveWeights(double[][] kjWeights, double[][] jiWeights)

/**
 * Method to load the weights from the binary file. The weights are loaded into the provided
 * arrays.
 */
   public void loadWeights(double[][][] w)
   {
      int inActsRead;
      int hid1ActsRead;
      int hid2ActsRead;
      int outActsRead;
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
         inActsRead = in.readInt();
         hid1ActsRead = in.readInt();
         hid2ActsRead = in.readInt();
         outActsRead = in.readInt();
         if (inActsRead != numInAct || hid1ActsRead != numHid1Act || hid2ActsRead != numHid2Act || outActsRead != numOutAct)
         {
            Util.exit("Network config doesn't match weights config from file", fileName);
         }
      } //try
      catch (IOException e)
      {
         Util.exit("Error reading config", fileName);
      }

      n = MK_LAYER;
      for (m = 0; m < numInAct; m++)
      {
         for (k = 0; k < numHid1Act; k++)
         {
            try
            {
               w[n][m][k] = in.readDouble();
            }
            catch (IOException e)
            {
               Util.exit("Error reading mkWeights[" + m + "][" + k + "]", fileName);
            }
         } //for (j = 0; j < numHidAct; j++)
      } //for (k = 0; k < numInAct; k++)

      n = KJ_LAYER;
      for (k = 0; k < numHid1Act; k++)
      {
         for (j = 0; j < numHid2Act; j++)
         {
            try
            {
               w[n][k][j] = in.readDouble();
            }
            catch (IOException e)
            {
               Util.exit("Error reading kjWeights[" + k + "][" + j + "]", fileName);
            }
         } //for (j = 0; j < numHidAct; j++)
      } //for (k = 0; k < numInAct; k++)

      n = JI_LAYER;
      for (j = 0; j < numHid2Act; j++)
      {
         for (i = 0; i < numOutAct; i++)
         {
            try
            {
               w[n][j][i] = in.readDouble();
            }
            catch (IOException e)
            {
               Util.exit("Error reading jiWeights[" + j + "][" + i + "]", fileName);
            }
         } //for (i = 0; i < numOutAct; i++)
      } //for (j = 0; j < numHidAct; j++)

      try
      {
         in.close();
      }
      catch (IOException e)
      {
         Util.exit("Error closing input stream", fileName);
      }
   } //public void loadWeights(double[][] kjWeights, double[][] jiWeights)
} //public class WeightsFileIO
