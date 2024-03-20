import java.io.*;

public class WeightsFileIO
{
   private int numInAct;
   private int numHidAct;
   private int numOutAct;
   private DataOutputStream out;
   private DataInputStream in;
   private String fileName;

   public WeightsFileIO(int numInAct, int numHidAct, int numOutAct, String fileName)
   {
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
         Util.exit("Failed to find file", fileName);
      }
   }

   public boolean saveWeights(double[][] kjWeights, double[][] jiWeights)
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
               Util.exit("Error writing jiWeights[" + j + "][" + i + "]", fileName);
            }
         }
      }

      return true;
   }

   public boolean loadWeights(double[][] kjWeights, double[][] jiWeights)
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
            Util.exit("Network config doesn't match weights config from file", fileName);
         }
      } catch (IOException e) {
         Util.exit("Error reading config", fileName);
      }

      for (k = 0; k < numInAct; k++) {
         for (j = 0; j < numHidAct; j++) {
            try
            {
               kjWeights[k][j] = in.readDouble();
            }
            catch (IOException e)
            {
               Util.exit("Error reading kjWeights[" + k + "][" + j + "]", fileName);
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
               Util.exit("Error reading jiWeights[" + j + "][" + i + "]", fileName);
            }
         }
      }

      return true;
   }
}
