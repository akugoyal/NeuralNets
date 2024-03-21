import java.io.*;
import java.util.Arrays;

public class TruthTableFileIO
{
   private int numInputs;
   private int numOutputs;
   private int numTrainingCases;
   private int numConfigParams;
   private int lnNumber;
   private String ln;
   private DataOutputStream out;
   private DataInputStream in;
   private String fileName;

   public TruthTableFileIO(int numInputs, int numOutputs, int numCases, String fileName)
   {
      this.numInputs = numInputs;
      this.numOutputs = numOutputs;
      this.fileName = fileName;
      this.numTrainingCases = numCases;
      this.numConfigParams = 3;
      this.lnNumber = 0;
   }

   public void loadTruthTable(double[][] truthTableInputs, double[][] truthTableOutputs)
   {
      int numInRead;
      int numOutRead;
      int numCasesRead;
      int caseIter;
      int inIter;
      int outIter;
      int blanksFound;
      String[] read;

      try
      {
         in = new DataInputStream(new FileInputStream(fileName));
      }
      catch (FileNotFoundException e)
      {
         Util.exit("Failed to open truth table file", fileName);
      }

      try
      {
         lnNumber++;
         ln = in.readLine();
         if (ln == null) {
            Util.exit("Empty truth table file", fileName);
         } else {
            while (ln.isBlank()) {
               ln = in.readLine();
            }
         }
         read = ln.split("-");
         if (read.length != this.numConfigParams)
         {
            Util.exit("Expected " + this.numConfigParams + " config params. Found " + read.length, fileName);
         }
         else
         {
            try
            {
               numCasesRead = Util.toInt(read[0]);
               numInRead = Util.toInt(read[1]);
               numOutRead = Util.toInt(read[2]);
               if (numCasesRead != this.numTrainingCases || numInRead != this.numInputs || numOutRead != this.numOutputs)
               {
                  Util.exit("Network config doesn't match truth table config from file. ",
                        fileName);
               }
            }
            catch (NumberFormatException e)
            {
               Util.exit("Incorrectly formatted integer in parsed configuration: " + Arrays.toString(read), fileName);
            }
         }
      }
      catch (EOFException e)
      {
         Util.exit("Missing one or more truth table configuration tags: \"Number of training " +
               "cases\", \"Number of inputs\", \"Number of outputs\"", fileName);
      }
      catch (IOException e)
      {
         Util.exit("Error reading line " + lnNumber, fileName);
      }

      blanksFound = 0;
      for (caseIter = 0; caseIter - blanksFound < this.numTrainingCases; caseIter++)
      {
         try
         {
            ln = in.readLine();
            if (ln == null) {
               Util.exit("Truth table file missing inputs.", fileName);
            }
            if (ln.isBlank())
            {
               blanksFound++;
               continue;
            }
            read = ln.split("\s+");
            if (read.length != this.numInputs)
            {
               Util.exit("Expected " + this.numInputs + " truth table inputs on line. Found " + read.length + ".\nLine: " + Arrays.toString(read), fileName);
            }

            for (inIter = 0; inIter < this.numInputs; inIter++)
            {
               truthTableInputs[caseIter - blanksFound][inIter] = Util.toDouble(read[inIter]);
            }
         }
         catch (EOFException e)
         {
            Util.exit("Reached end of truth table file too early", fileName);
         }
         catch (NumberFormatException e)
         {
            Util.exit("Incorrectly formatted input in truth table file", fileName);
         }
         catch (IOException e)
         {
            Util.exit("Encountered IOException in truth table file", fileName);
         }
      }

      blanksFound = 0;
      for (caseIter = 0; caseIter - blanksFound < this.numTrainingCases; caseIter++)
      {
         try
         {
            ln = in.readLine();
            if (ln == null) {
               Util.exit("Truth table file missing outputs.", fileName);
            }
            if (ln.isBlank())
            {
               blanksFound++;
               continue;
            }
            read = ln.split("\s+");
            if (read.length != this.numOutputs)
            {
               Util.exit("Expected " + this.numOutputs + " truth table outputs on line. Found " + read.length + ".\nLine: " + Arrays.toString(read), fileName);
            }

            for (outIter = 0; outIter < this.numOutputs; outIter++)
            {
               truthTableOutputs[caseIter - blanksFound][outIter] = Util.toDouble(read[outIter]);
            }
         }
         catch (EOFException e)
         {
            Util.exit("Reached end of truth table file too early", fileName);
         }
         catch (NumberFormatException e)
         {
            Util.exit("Incorrectly formatted output in truth table file", fileName);
         }
         catch (IOException e)
         {
            Util.exit("Encountered IOException in truth table file", fileName);
         }
      }
   }

   public void saveTruthTable(double[][] truthTableInputs, double[][] truthTableOutputs)
   {
      int numCases;
      int numInputs;
      int numOutputs;
      int caseIter;

      try
      {
         out = new DataOutputStream(new FileOutputStream(fileName));
      }
      catch (FileNotFoundException e)
      {
         Util.exit("Failed to write to truth table file", fileName);
      }

      numCases = truthTableInputs.length;
      numInputs = truthTableInputs[0].length;
      numOutputs = truthTableOutputs[0].length;
      if (numCases != this.numTrainingCases || numCases != truthTableOutputs.length || numInputs != this.numInputs || numOutputs != this.numOutputs)
      {
         Util.exit("Saving truth table - Expected input table of size " + this.numTrainingCases + "x" + this.numInputs + " and output table of size " + this.numTrainingCases + "x" + this.numOutputs + ". Received input table of size " + truthTableInputs.length + "x" + truthTableInputs[0].length + " and output table of size " + truthTableOutputs.length + "x" + truthTableOutputs[0].length + ".", fileName);
      }

      try
      {
         out.writeUTF(formatTableConfig());
      }
      catch (IOException e)
      {
         Util.exit("Error saving truth table configuration to file", fileName);
      }

      for (caseIter = 0; caseIter < this.numTrainingCases; caseIter++)
      {
         try
         {
            out.writeUTF(formatRow(truthTableInputs[caseIter], this.numInputs));
         }
         catch (IOException e)
         {
            Util.exit("Error saving truth table input case " + caseIter + " to file", fileName);
         }
      }

      for (caseIter = 0; caseIter < this.numTrainingCases; caseIter++)
      {
         try
         {
            out.writeUTF(formatRow(truthTableOutputs[caseIter], this.numOutputs));
         }
         catch (IOException e)
         {
            Util.exit("Error saving truth table output case " + caseIter + " to file", fileName);
         }
      }
   }

   public String formatTableConfig()
   {
      return this.numTrainingCases + "-" + this.numInputs + "-" + this.numOutputs;
   }

   public String formatRow(double[] arr, int len)
   {
      String res;
      int iter;

      res = "";

      for (iter = 0; iter < len; iter++)
      {
         res += arr[iter] + " ";
      }

      return res;
   }
}
