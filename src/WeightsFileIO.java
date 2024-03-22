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
   private int numHidAct;
   private int numOutAct;
   private DataOutputStream out;
   private DataInputStream in;
   private String fileName;

/**
 * Constructor to initialize the expected number of input, hidden, and output activations and
 * the file name.
 *
 * @param numInAct  the expected number of input activations
 * @param numHidAct the expected number of hidden activations
 * @param numOutAct the expected number of output activations
 * @param fileName  the name of the file to read/write weights
 */
   public WeightsFileIO(int numInAct, int numHidAct, int numOutAct, String fileName)
   {
      this.numInAct = numInAct;
      this.numHidAct = numHidAct;
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
   public void saveWeights(double[][] kjWeights, double[][] jiWeights)
   {
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
         out.writeInt(numHidAct);
         out.writeInt(numOutAct);
      }
      catch (IOException e)
      {
         Util.exit("Error writing network configuration", fileName);
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
               Util.exit("Error writing kjWeights[" + k + "][" + j + "]", fileName);
            }
         } //for (j = 0; j < numHidAct; j++)
      } //for (k = 0; k < numInAct; k++)

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
   public void loadWeights(double[][] kjWeights, double[][] jiWeights)
   {
      int inActsRead;
      int hidActsRead;
      int outActsRead;
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
         hidActsRead = in.readInt();
         outActsRead = in.readInt();
         if (inActsRead != numInAct || hidActsRead != numHidAct || outActsRead != numOutAct)
         {
            Util.exit("Network config doesn't match weights config from file", fileName);
         }
      } //try
      catch (IOException e)
      {
         Util.exit("Error reading config", fileName);
      }

      for (k = 0; k < numInAct; k++)
      {
         for (j = 0; j < numHidAct; j++)
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

      for (j = 0; j < numHidAct; j++)
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
}
