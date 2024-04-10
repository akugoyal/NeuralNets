import java.io.*;
import java.util.Arrays;

/**
 * Class to read and write configuration files for the neural network. The configuration file is
 * a text file that contains the parameters for the neural network. The file is expected to
 * contain key:value pairs, with each pair on its own line. Keys are not case-sensitive, can be
 * padded with whitespaces, and can appear in any order. Blank lines between key:value pairs are
 * allowed. The accepted keys are:
 *
 * Network Configuration:     The number of input, hidden, and output neurons in the network,
 *                            separated by dashes.
 * Network Mode:              The mode of the network. 0 for training, 1 for running the entire
 *                            truth table, 2 for running a single case.
 * Number of Cases:           The number of cases in the truth table.
 * Max Training Iterations:   The maximum number of iterations for training.
 * Lambda:                    The learning rate.
 * Error Threshold:           The error threshold for training.
 * Keep Alive Interval:       The interval to print status updates to the console during training.
 * Random Range Lower Bound:  The lower bound for the random number range, which may be used to
 *                            randomize the weights.
 * Random Range Upper Bound:  The upper bound for the random number range, which may be used to
 *                            randomize the weights.
 * Run Case Number:           The case number to run in the truth table. Used when networkMode is 2.
 * Truth Table File:          The file containing the truth table.
 * Load Weights:              Whether to load the weights from a file.
 * Save Weights:              Whether to save the weights to a file.
 * Weights File:              The file to load/save the weights from/to.
 * Decimal Precision:         The number of decimal places to round the weights to when saving.
 *
 *
 * An example configuration file may look like the following:
 *
 * Network Configuration: 2-2-1
 * Network Mode: 0
 * Number of Cases: 4
 * Max Training Iterations: 10000
 * Lambda: 0.1
 * Error Threshold: 0.01
 * Random Range Lower Bound: -0.5
 * Random Range Upper Bound: 0.5
 * Truth Table File: truth_table.txt
 * Load Weights: false
 * Save Weights: false
 * Weights File: weights.txt
 * Run Case Number: 0
 * Keep Alive Interval: 100
 * Decimal Precision: 17
 *
 * Author: Akul Goyal
 * Date of Creation: 03/19/2024
 */
public class ConfigFileIO
{
   private DataOutputStream out;
   private DataInputStream in;
   private String fileName;
   private String ln;
   private int lnNumber;
   private Config config;

/**
 * Constructor for the ConfigFileIO class. Creates a new Config object with the default parameter
 * values.
 *
 * @param fileName              the name of the configuration file
 * @param defaultWeightsFile    the default file to load/save the weights from/to
 * @param defaultTruthTableFile the default file containing the truth table
 */
   public ConfigFileIO(String fileName, String defaultWeightsFile, String defaultTruthTableFile)
   {
      this.fileName = fileName;
      lnNumber = 1;
      config = new Config(defaultWeightsFile, defaultTruthTableFile);
   }

/**
 * Constructor for the ConfigFileIO class. Creates a config object with the given parameter
 * values.
 *
 * @param fileName              the name of the configuration file
 * @param config                the Config object to use
 * @param defaultWeightsFile    the default file to load/save the weights from/to
 * @param defaultTruthTableFile the default file containing the truth table
 */
   public ConfigFileIO(String fileName, Config config, String defaultWeightsFile,
                       String defaultTruthTableFile)
   {
      this(fileName, defaultWeightsFile, defaultTruthTableFile);
      this.config = config;
   }

/**
 * Loads the configuration from the file and returns a Config object with the parameters set.
 * If the configuration file does not specify a parameter, the default value is used. This
 * method will exit the program if it encounters an error in parsing the configuration file.
 *
 * @return a Config object with the parameters set from the configuration file
 */
   public Config loadConfig()
   {
      String[] read;

      try //Create input stream for the file
      {
         in = new DataInputStream(new FileInputStream(fileName));
      }
      catch (FileNotFoundException e)
      {
         Util.exit("Failed to open config file", fileName);
      }

      while (readLine())
      {
         if (!ln.trim().startsWith("#") && !ln.isBlank())
         {
            if (ln.contains(":"))
            {
               read = ln.split(":");
               if (read.length < 2)
               {
                  Util.exit("Missing key or value in config file. Line: \n\t" + ln, fileName);
               }
               read[0] = read[0].trim().toLowerCase();
               read[1] = read[1].trim();

               switch (read[0])
               {
                  case "network configuration":
                     parseNetworkConfig(read[1]);
                     break;
                  case "network mode":
                     try
                     {
                        config.networkMode = Util.toInt(read[1]);
                        break;
                     }
                     catch (NumberFormatException e)
                     {
                        Util.exit("Poorly formatted integer for Network Mode: " + read[1],
                              fileName);
                     }
                  case "number of cases":
                     try
                     {
                        config.numCases = Util.toInt(read[1]);
                        if (config.numCases == 0)
                        {
                           Util.exit("Invalid: \"Number of Cases\" parameter is 0." + ln, fileName);
                        }
                        break;
                     }
                     catch (NumberFormatException e)
                     {
                        Util.exit("Poorly formatted integer for Number of Training Cases: " + read[1], fileName);
                     }
                  case "max training iterations":
                     try
                     {
                        config.maxIters = Util.toInt(read[1]);
                        break;
                     }
                     catch (NumberFormatException e)
                     {
                        Util.exit("Poorly formatted integer for Max Training Iterations: " + read[1],
                              fileName);
                     }
                  case "lambda":
                     try
                     {
                        config.lambda = Util.toDouble(read[1]);
                        break;
                     }
                     catch (NumberFormatException e)
                     {
                        Util.exit("Poorly formatted double for lambda: " + read[1], fileName);
                     }
                  case "error threshold":
                     try
                     {
                        config.errThreshold = Util.toDouble(read[1]);
                        break;
                     }
                     catch (NumberFormatException e)
                     {
                        Util.exit("Poorly formatted double for error threshold: " + read[1], fileName);
                     }
                  case "random range lower bound":
                     try
                     {
                        config.lowRand = Util.toDouble(read[1]);
                        break;
                     }
                     catch (NumberFormatException e)
                     {
                        Util.exit("Poorly formatted double for random number range lower bound: " + read[1], fileName);
                     }
                  case "random range upper bound":
                     try
                     {
                        config.highRand = Util.toDouble(read[1]);
                        break;
                     }
                     catch (NumberFormatException e)
                     {
                        Util.exit("Poorly formatted double for random number range upper bound: " + read[1], fileName);
                     }
                  case "truth table file":
                     config.truthTableFile = read[1];
                     break;
                  case "load weights":
                     config.loadWeights = Util.toBoolean(read[1]);
                     break;
                  case "save weights":
                     config.saveWeights = Util.toBoolean(read[1]);
                     break;
                  case "weights file":
                     config.weightsFile = read[1];
                     break;
                  case "run case number":
                     try
                     {
                        config.runCaseNum = Util.toInt(read[1]);
                        if (config.runCaseNum > config.numCases)
                        {
                           Util.exit("Case " + config.runCaseNum + " in config file exceeds total " +
                                 "number of cases: " + config.numCases, fileName);
                        }
                        break;
                     } //try
                     catch (NumberFormatException e)
                     {
                        Util.exit("Poorly formatted double for Run Case Number: " + read[1], fileName);
                     }
                  case "keep alive interval":
                     try
                     {
                        config.keepAliveInterval = Util.toInt(read[1]);
                        break;
                     }
                     catch (NumberFormatException e)
                     {
                        Util.exit("Poorly formatted integer for Keep Alive Interval: " + read[1],
                              fileName);
                     }
                  case "decimal precision":
                     try {
                        config.decimalPrecision = Util.toInt(read[1]);
                        break;
                     }
                     catch (NumberFormatException e)
                     {
                        Util.exit("Poorly formatted integer for Decimal Precision: " + read[1],
                              fileName);
                     }
                  default:
                     Util.exit("Invalid configuration parameter \"" + read[0] + "\"", fileName);
               } //switch (read[0])
            } //if (ln.contains(":"))
            else
            {
               System.out.println("File \"" + fileName + "\" - Ignoring garbage line: " + ln);
            }
         } //if (!ln.trim().startsWith("#") && !ln.isBlank())
      } //while (readLine())

      try
      {
         in.close();
      }
      catch (IOException e)
      {
         Util.exit("Error closing input stream", fileName);
      }

      return config;
   } //public Config loadConfig()

/**
 * Reads a line from the input stream and increments the line number. If the line is null or
 * is an EOF, the method returns false. Otherwise, it returns true.
 *
 * @return true if the end of the file has not been reached, false otherwise
 */
   public boolean readLine()
   {
      try
      {
         ln = in.readLine();
         lnNumber++;

         return ln != null && !ln.toLowerCase().trim().equals("eof");
      } //try
      catch (EOFException e)
      {
         return false;
      }
      catch (IOException e)
      {
         Util.exit("Error reading line " + lnNumber, fileName);
      }

      return false;
   } //public boolean readLine()

/**
 * Parses the network configuration from the given string and sets the parameters in the
 * Config object.
 *
 * @param ln the string containing the network configuration
 */
   public void parseNetworkConfig(String ln)
   {
      int n;
      String[] read;

      read = ln.split("-");
      config.numActsInLayers = new int[config.numLayers];

      if (read.length > 3)
      {
         n = Config.INPUT_LAYER;
         config.numActsInLayers[n] = Util.toInt(read[n].trim());
         n = Config.HIDDEN_LAYER1;
         config.numActsInLayers[n] = Util.toInt(read[n].trim());
         n = Config.HIDDEN_LAYER2;
         config.numActsInLayers[n] = Util.toInt(read[n].trim());
         n = Config.OUTPUT_LAYER;
         config.numActsInLayers[n] = Util.toInt(read[n].trim());
      }
      else
      {
         Util.exit("Missing network configuration parameters. Parsed: " + ln, fileName);
      }

      if (Arrays.asList(config.numActsInLayers).contains(0))
      {
         Util.exit("Invalid network configuration parameters. Parsed: " + ln, fileName);
      }
   } //public void parseNetworkConfig(String ln)

/**
 * Saves the configuration to the file in a format compatible with the loadConfig() method.
 * This method will exit the program if it encounters an error in writing to the configuration
 * file.
 */
   public void saveConfig()
   {
      try
      {
         out = new DataOutputStream(new FileOutputStream(fileName));
      }
      catch (FileNotFoundException e)
      {
         Util.exit("Failed to write to config file", fileName);
      }

      try
      {
         out.writeUTF(Util.newLine("Network configuration: " + Util.formatConfiguration(config.numActsInLayers,
               config.numLayers)));
         out.writeUTF(Util.newLine(""));
         out.writeUTF(Util.newLine("Network mode: " + config.networkMode));
         out.writeUTF(Util.newLine("Number of training cases: " + config.numCases));
         out.writeUTF(Util.newLine("Max training iterations: " + config.maxIters));
         out.writeUTF(Util.newLine("Lambda: " + config.lambda));
         out.writeUTF(Util.newLine("Error threshold: " + config.errThreshold));
         out.writeUTF(Util.newLine("Truth table file: " + config.truthTableFile));
         out.writeUTF(Util.newLine(""));
         out.writeUTF(Util.newLine("Random range lower bound: " + config.lowRand));
         out.writeUTF(Util.newLine("Random range upper bound: " + config.highRand));
         out.writeUTF(Util.newLine(""));
         out.writeUTF(Util.newLine("Load weights: " + config.loadWeights));
         out.writeUTF(Util.newLine("Save weights: " + config.saveWeights));
         out.writeUTF(Util.newLine("Weights file: " + config.weightsFile));
      } //try
      catch (IOException e)
      {
         Util.exit("Error saving config", fileName);
      }

      try
      {
         out.close();
      }
      catch (IOException e)
      {
         Util.exit("Error closing output stream", fileName);
      }
   } //public void saveConfig()
} //public class ConfigFileIO
