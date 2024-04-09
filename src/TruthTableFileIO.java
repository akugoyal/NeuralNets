import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Class to handle reading and writing truth tables to and from files. Expects the truth table
 * file to be a text file adhering to the following format:
 *
 * The file configuration will appear as three dash-separated integers on the first line of the
 * file, with the first integer representing the number of training cases, the second integer
 * representing the number of inputs, and the third integer representing the number of outputs.
 *
 * The next section of the file will contain the input values for each training case, with each
 * line containing space separated values for each input. The number of values on each line
 * should match the number of inputs specified in the file configuration.
 *
 * The final section of the file will contain the output values for each training case, with each
 * line containing space separated values for each output. The number of values on each line
 * should match the number of outputs specified in the file configuration.
 *
 * Author: Akul Goyal
 * Date of Creation: 03/19/2024
 */
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

/**
 * Constructor for the TruthTableFileIO class. Initializes the expected number of inputs,
 * outputs, cases, and the file to read/write the truth table from/to.
 *
 * @param numInputs  the expected number of inputs in the truth table.
 * @param numOutputs the expected number of outputs in the truth table.
 * @param numCases   the expected number of cases in the truth table.
 * @param fileName   the file to read/write the truth table from/to.
 */
   public TruthTableFileIO(int numInputs, int numOutputs, int numCases, String fileName)
   {
      this.numInputs = numInputs;
      this.numOutputs = numOutputs;
      this.fileName = fileName;
      this.numTrainingCases = numCases;
      this.lnNumber = 0;
/**
 * Refers to the three dash-separated integers representing the file configuration.
 */
      this.numConfigParams = 3;
   } //public TruthTableFileIO(int numInputs, int numOutputs, int numCases, String fileName)

/**
 * Loads the truth table from a file and stores the input and output values in the provided
 * arrays. If the file does not adhere to the expected format, the program will exit.
 *
 * @param truthTableInputs  the array to store the input values of the truth table.
 */
   public void loadTruthTableInputs(double[][] truthTableInputs)
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

         if (ln == null)
         {
            Util.exit("Empty truth table file", fileName);
         }
         else
         {
            while (ln.isBlank())
            {
               ln = in.readLine();
            }
         }

         read = ln.split("[-x]");

         if (read.length != this.numConfigParams)
         {
            Util.exit("Expected " + this.numConfigParams + " config params. Found " +
                  read.length, fileName);
         }
         else
         {
            try
            {
               numCasesRead = Util.toInt(read[0]);
               numInRead = Util.toInt(read[1]);
               numOutRead = Util.toInt(read[2]);
               if (numCasesRead != this.numTrainingCases || numInRead != this.numInputs ||
                     numOutRead != this.numOutputs)
               {
                  Util.exit("Network config doesn't match truth table config from file. ",
                        fileName);
               }
            } //try
            catch (NumberFormatException e)
            {
               Util.exit("Incorrectly formatted integer in parsed configuration: " +
                     Arrays.toString(read), fileName);
            }
         } //if (read.length != this.numConfigParams)...else
      } //try
      catch (EOFException e)
      {
         Util.exit("Missing one or more truth table configuration tags: \"Number of training "
               + "cases\", \"Number of inputs\", \"Number of outputs\"", fileName);
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
            ln = in.readLine().trim();

            if (ln == null)
            {
               Util.exit("Truth table file missing inputs.", fileName);
            }
            if (ln.isBlank())
            {
               blanksFound++;
               continue;
            }

            if (ln.startsWith("?")) {
               readFromFile(truthTableInputs, caseIter - blanksFound, ln.substring(1), numInputs);
            }
            else
            {

               read = ln.split("\s+");

               if (read.length != this.numInputs)
               {
                  Util.exit("Expected " + this.numInputs + " truth table inputs on line. " +
                        "Found " + read.length + ".\nLine: " + Arrays.toString(read), fileName);
               }

               for (inIter = 0; inIter < this.numInputs; inIter++)
               {
                  truthTableInputs[caseIter - blanksFound][inIter] = Util.toDouble(read[inIter]);
               }
            }
         } //try
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
      } //for (caseIter = 0; caseIter - blanksFound < this.numTrainingCases; caseIter++)
   } //public void loadTruthTable(double[][] truthTableInputs, double[][] truthTableOutputs)

   public void loadTruthTable(double[][] truthTableInputs, double[][] truthTableOutputs) {
      int blanksFound;
      int caseIter;
      String[] read;
      int outIter;

      loadTruthTableInputs(truthTableInputs);

      blanksFound = 0;
      for (caseIter = 0; caseIter - blanksFound < this.numTrainingCases; caseIter++)
      {
         try
         {
            ln = in.readLine();

            if (ln == null)
            {
               Util.exit("Truth table file missing outputs.", fileName);
            }
            if (ln.isBlank())
            {
               blanksFound++;
               continue;
            }

            if (ln.startsWith("?")) {
               readFromFile(truthTableOutputs, caseIter - blanksFound, ln.substring(1), numOutputs);
            } else
            {
               read = ln.split("\s+");

               if (read.length != this.numOutputs)
               {
                  Util.exit("Expected " + this.numOutputs + " truth table outputs on line. " +
                        "Found " + read.length + ".\nLine: " + Arrays.toString(read), fileName);
               }

               for (outIter = 0; outIter < this.numOutputs; outIter++)
               {
                  try
                  {
                     truthTableOutputs[caseIter - blanksFound][outIter] = Util.toDouble(read[outIter]);
                  }
                  catch (NumberFormatException e)
                  {
                     Util.exit("Incorrectly formatted output in truth table file", fileName);
                  }
               } //for (outIter = 0; outIter < this.numOutputs; outIter++)
            }
         } //try
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
      } //for (caseIter = 0; caseIter - blanksFound < this.numTrainingCases; caseIter++)

      try
      {
         in.close();
      }
      catch (IOException e)
      {
         Util.exit("Error closing input stream", fileName);
      }
   }

   public void readFromFile(double[][] truthTable, int caseIter, String file, int numElements)
   {
      String ln;
      String[] read;
      int iter;

      try
      {
         BufferedReader inputReader = new BufferedReader(new FileReader(file));
         ln = inputReader.readLine();
         if (ln == null) {
            Util.exit("Empty file when reading from truth table Case #" + caseIter, file);
         }
         if (ln.isBlank()) {
            Util.exit("Empty line when reading from truth table Case #" + caseIter, file);
         }

         read = ln.trim().split("\s+");
         if (read.length > numElements) {
            System.out.println("Found " + read.length + " elements in file \"" + file + "\". " +
                  "Using only the first " + numElements + " elements.");
         }
         for (iter = 0; iter < numElements; iter++) {
            truthTable[caseIter][iter] = Util.toDouble(read[iter]);
         }

      } catch (FileNotFoundException e) {
         Util.exit("Could not find file referenced in truth table Case #" + caseIter, file);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

/**
 * Saves the truth table to a file in a format compatible with the loadTruthTable method.
 *
 * @param truthTableInputs  the array containing the input values of the truth table.
 * @param truthTableOutputs the array containing the output values of the truth table.
 */
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
      if (numCases != this.numTrainingCases || numCases != truthTableOutputs.length ||
            numInputs != this.numInputs || numOutputs != this.numOutputs)
      {
         Util.exit("Saving truth table - Expected input table of size " +
               this.numTrainingCases + "x" + this.numInputs + " and output table of size " +
               this.numTrainingCases + "x" + this.numOutputs + ". Received input table of size " +
               truthTableInputs.length + "x" + truthTableInputs[0].length + " and output table" +
               " of size " + truthTableOutputs.length + "x" + truthTableOutputs[0].length + ".",
               fileName);
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
            Util.exit("Error saving truth table input case " + caseIter + " to file",
                  fileName);
         }
      } //for (caseIter = 0; caseIter < this.numTrainingCases; caseIter++)

      for (caseIter = 0; caseIter < this.numTrainingCases; caseIter++)
      {
         try
         {
            out.writeUTF(formatRow(truthTableOutputs[caseIter], this.numOutputs));
         }
         catch (IOException e)
         {
            Util.exit("Error saving truth table output case " + caseIter + " to file",
                  fileName);
         }
      } //for (caseIter = 0; caseIter < this.numTrainingCases; caseIter++)

      try
      {
         out.close();
      }
      catch (IOException e)
      {
         Util.exit("Error closing output stream", fileName);
      }
   } //public void saveTruthTable(double[][] truthTableInputs, double[][] truthTableOutputs)

/**
 * Formats the configuration of the truth table to be dash separated values.
 *
 * @return The formatted configuration.
 */
   public String formatTableConfig()
   {
      return this.numTrainingCases + "-" + this.numInputs + "-" + this.numOutputs;
   }

/**
 * Formats a row of the truth table to be a set of space separated values.
 *
 * @param arr The row of the truth table to format.
 * @param len The number of values in the row.
 * @return The formatted row.
 */
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
   } //public String formatRow(double[] arr, int len)
} //public class TruthTableFileIO
