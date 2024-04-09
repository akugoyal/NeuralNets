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
 *
 * @param kjWeights the weights from input to hidden layer
 * @param jiWeights the weights from hidden to output layer
 */
   public void saveWeights(double[][] mkWeights, double[][] kjWeights, double[][] jiWeights)
   {
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

      for (m = 0; m < numInAct; m++)
      {
         for (k = 0; k < numHid1Act; k++)
         {
            try
            {
               out.writeDouble(mkWeights[m][k]);
            }
            catch (IOException e)
            {
               Util.exit("Error writing mkWeights[" + m + "][" + k + "]", fileName);
            }
         } //for (j = 0; j < numHidAct; j++)
      } //for (k = 0; k < numInAct; k++)

      for (k = 0; k < numHid1Act; k++)
      {
         for (j = 0; j < numHid2Act; j++)
         {
            try
            {
               out.writeDouble(kjWeights[k][j]);
            }
            catch (IOException e)
            {
               Util.exit("Error writing kjWeights[" + k + "][" + j + "]", fileName);
            }
         } //for (j = 0; j < numHidAct; j++)
      } //for (k = 0; k < numInAct; k++)

      for (j = 0; j < numHid2Act; j++)
      {
         for (i = 0; i < numOutAct; i++)
         {
            try
            {
               out.writeDouble(jiWeights[j][i]);
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
 *
 * @param kjWeights the array to store the weights from input to hidden layer
 * @param jiWeights the array to store the weights from hidden to output layer
 */
   public void loadWeights(double[][] mkWeights, double[][] kjWeights, double[][] jiWeights)
   {
      int inActsRead;
      int hid1ActsRead;
      int hid2ActsRead;
      int outActsRead;
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

      for (m = 0; m < numInAct; m++)
      {
         for (k = 0; k < numHid1Act; k++)
         {
            try
            {
               mkWeights[m][k] = in.readDouble();
            }
            catch (IOException e)
            {
               Util.exit("Error reading mkWeights[" + m + "][" + k + "]", fileName);
            }
         } //for (j = 0; j < numHidAct; j++)
      } //for (k = 0; k < numInAct; k++)

      for (k = 0; k < numHid1Act; k++)
      {
         for (j = 0; j < numHid2Act; j++)
         {
            try
            {
               kjWeights[k][j] = in.readDouble();
            }
            catch (IOException e)
            {
               Util.exit("Error reading kjWeights[" + k + "][" + j + "]", fileName);
            }
         } //for (j = 0; j < numHidAct; j++)
      } //for (k = 0; k < numInAct; k++)

      for (j = 0; j < numHid2Act; j++)
      {
         for (i = 0; i < numOutAct; i++)
         {
            try
            {
               jiWeights[j][i] = in.readDouble();
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
