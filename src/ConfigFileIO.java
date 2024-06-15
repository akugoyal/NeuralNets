import activationFunctions.*;
import java.io.*;

/**
 * This is a class to read and write configuration files for an N-layer neural network. The
 * configuration file is a text file that contains the parameters for the neural network. The file
 * is expected to contain key:value pairs, with each pair on its own line. Keys are not
 * case-sensitive, can be padded with whitespaces, and can appear in any order. Blank lines between
 * key:value pairs are allowed. The accepted keys are:
 *
 * Network Configuration:     The number of input, hidden, and output nodes in the network,
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
 * Weights File:              The file to load/save the weights from/to.
 * Decimal Precision:         The number of decimal places to round the weights to when saving.
 * Activation Function:       The activation function to use. Supports the linear, hyperbolic
 *                            tangent, sigmoid, ReLU, leaky ReLU, randomized ReLU, and Gaussian
 *                            functions. For the linear and leaky ReLU functions, additional
 *                            arguments are required after the function name as comma separated
 *                            values. For the linear function, the slope and y-intercept are
 *                            required. For the leaky ReLU function, the slope is required.
 * Save Weights Interval:     The interval at which to save weights to file during training. If
 *                            this is greater than 0, weights will be saved at the end, regardless
 *                            of network mode.
 * ETA Interval:              The interval at which to print an ETA to the end of training. This
 *                            is only used when the network is in training mode.
 *
 * Keys which do not match the above list will be ignored. Lines beginning with a '#' are treated
 * as comments and are ignored. An example configuration file may look like the following:
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
 * Weights File: weights.txt
 * Run Case Number: 0
 * Keep Alive Interval: 100
 * Decimal Precision: 17
 * Activation Function: Linear, 0.1, 0.5
 * Save Weights Interval: 1e3
 * ETA Interval: 0
 *
 *
 * Table of Contents:
 * 1. ConfigFileIO(String fileName, String defaultWeightsFile, String defaultTruthTableFile)
 * 2. ConfigFileIO(String fileName, Config config, String defaultWeightsFile,
 *                String defaultTruthTableFile)
 * 3. public Config loadConfig()
 * 4. public boolean readLine()
 * 5. public void parseNetworkConfig(String ln)
 * 6. public void saveConfig()
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
 * If the configuration file does not specify a parameter, the default value specified in the
 * Config class is used. This method will throw an exception if it encounters an error in parsing
 * the configuration file.
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
               read = ln.split("[:,]");
               if (read.length < 2)
               {
                  Util.exit("Missing key or value in config file. Line " + lnNumber +
                        ": \n\t" + ln, fileName);
               }
               int KEY = 0;
               read[KEY] = read[KEY].trim().toLowerCase();
               int VALUE = 1;
               read[VALUE] = read[VALUE].trim();

               switch (read[KEY])
               {
                  case "network configuration":
                     parseNetworkConfig(read[VALUE]);
                     break;
                  case "network mode":
                     try
                     {
                        config.networkMode = Util.toInt(read[VALUE]);
                        break;
                     }
                     catch (NumberFormatException e)
                     {
                        Util.exit("Poorly formatted integer for Network Mode: " + read[VALUE],
                              fileName);
                     }
                  case "number of cases":
                     try
                     {
                        config.numCases = Util.toInt(read[VALUE]);
                        if (config.numCases == 0)
                        {
                           Util.exit("Invalid: \"Number of Cases\" parameter is 0. Read - "
                                       + ln, fileName);
                        }
                        break;
                     } //try
                     catch (NumberFormatException e)
                     {
                        Util.exit("Poorly formatted integer for Number of Training Cases: "
                              + read[VALUE], fileName);
                     }
                  case "max training iterations":
                     try
                     {
                        config.maxIters = Util.toInt(read[VALUE]);
                        break;
                     }
                     catch (NumberFormatException e)
                     {
                        Util.exit("Poorly formatted integer for Max Training Iterations: "
                                    + read[VALUE], fileName);
                     }
                  case "lambda":
                     try
                     {
                        config.lambda = Util.toDouble(read[VALUE]);
                        break;
                     }
                     catch (NumberFormatException e)
                     {
                        Util.exit("Poorly formatted double for lambda: " + read[VALUE], fileName);
                     }
                  case "error threshold":
                     try
                     {
                        config.errThreshold = Util.toDouble(read[VALUE]);
                        break;
                     }
                     catch (NumberFormatException e)
                     {
                        Util.exit("Poorly formatted double for error threshold: "
                              + read[VALUE], fileName);
                     }
                  case "random range lower bound":
                     try
                     {
                        config.lowRand = Util.toDouble(read[VALUE]);
                        break;
                     }
                     catch (NumberFormatException e)
                     {
                        Util.exit("Poorly formatted double for random number range lower" +
                              " bound: " + read[VALUE], fileName);
                     }
                  case "random range upper bound":
                     try
                     {
                        config.highRand = Util.toDouble(read[VALUE]);
                        break;
                     }
                     catch (NumberFormatException e)
                     {
                        Util.exit("Poorly formatted double for random number range upper" +
                              " bound: " + read[VALUE], fileName);
                     }
                  case "truth table file":
                     config.truthTableFile = read[VALUE];
                     break;
                  case "load weights":
                     config.loadWeights = Util.toBoolean(read[VALUE]);
                     break;
                  case "weights file in":
                     config.weightsFileIn = read[VALUE];
                     break;
                  case "weights file out":
                     config.weightsFileOut = read[VALUE];
                     break;
                  case "run case number":
                     try
                     {
                        config.runCaseNum = Util.toInt(read[VALUE]);
                        if (config.runCaseNum > config.numCases)
                        {
                           Util.exit("Case " + config.runCaseNum + " in config file exceeds" +
                                 " total number of cases: " + config.numCases, fileName);
                        }
                        break;
                     } //try
                     catch (NumberFormatException e)
                     {
                        Util.exit("Poorly formatted double for Run Case Number: "
                              + read[VALUE], fileName);
                     }
                  case "keep alive interval":
                     try
                     {
                        config.keepAliveInterval = Util.toInt(read[VALUE]);
                        break;
                     }
                     catch (NumberFormatException e)
                     {
                        Util.exit("Poorly formatted integer for Keep Alive Interval:" +
                                    " " + read[VALUE], fileName);
                     }
                  case "decimal precision":
                     try
                     {
                        config.decimalPrecision = Util.toInt(read[VALUE]);
                        break;
                     }
                     catch (NumberFormatException e)
                     {
                        Util.exit("Poorly formatted integer for Decimal Precision: "
                                    + read[VALUE], fileName);
                     }
                  case "activation function":
                     int ARG_0 = 2;
                     int ARG_1 = 3;
                     switch (read[VALUE].toLowerCase())
                     {
                        case "sigmoid":
                           config.activationFunction = new Sigmoid();
                           break;
                        case "tanh", "hyperbolic tangent":
                           config.activationFunction = new Tanh();
                           break;
                        case "relu":
                           config.activationFunction = new ReLU();
                           break;
                        case "linear":
                           double m = 0.0;
                           double b = 0.0;
                           try
                           {
                              if (read.length > ARG_0)
                              {
                                 m = Util.toDouble(read[ARG_0].trim());
                              }
                              else
                              {
                                 Util.exit("Missing argument for Linear activation function, m",
                                       fileName);
                              }
                           }
                           catch (NumberFormatException e)
                           {
                              Util.exit("Poorly formatted double for Linear activation " +
                                    "function, m: " + read[ARG_0].trim(), fileName);
                           }

                           try
                           {
                              if (read.length > ARG_1)
                              {
                                 b = Util.toDouble(read[ARG_1].trim());
                              }
                              else
                              {
                                 Util.exit("Missing argument for Linear activation function, b",
                                       fileName);
                              }
                           }
                           catch (NumberFormatException e)
                           {
                              Util.exit("Poorly formatted double for Linear activation " +
                                    "function, b: " + read[ARG_1].trim(), fileName);
                           }

                           config.activationFunction = new Linear(m, b);
                           break;
                        case "leaky relu":
                           double alpha = 0.0;

                           try
                           {
                              if (read.length > ARG_0)
                              {
                                 alpha = Util.toDouble(read[ARG_0].trim());
                              }
                              else
                              {
                                 Util.exit("Missing argument for Leaky ReLU activation function, " +
                                       "alpha", fileName);
                              }
                           }
                           catch (NumberFormatException e)
                           {
                              Util.exit("Poorly formatted double for Leaky ReLU activation " +
                                    "function, alpha: " + read[ARG_0].trim(), fileName);
                           }

                           config.activationFunction = new LeakyReLU(alpha);
                           break;
                        case "randomized relu", "rrelu":
                           config.activationFunction = new RReLU();
                           break;
                        case "gaussian":
                           config.activationFunction = new Gaussian();
                           break;
                        default:
                           Util.exit("Invalid activation function: " + read[VALUE], fileName);
                     }
                     break;
                  case "save weights interval":
                     try
                     {
                        config.saveWeightsInterval = Util.toInt(read[VALUE]);
                        break;
                     }
                     catch (NumberFormatException e)
                     {
                        Util.exit("Poorly formatted integer for Save Weights Interval: " +
                              read[VALUE], fileName);
                     }
                  case "eta interval":
                        try
                        {
                           config.etaInterval = Util.toInt(read[VALUE]);
                           break;
                        }
                        catch (NumberFormatException e)
                        {
                           Util.exit("Poorly formatted integer for ETA Interval: " +
                                 read[VALUE], fileName);
                        }
                  default:
                     Util.exit("Invalid configuration parameter \"" + read[KEY] + "\"",
                           fileName);
               } //switch (read[KEY])
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
      boolean success = false;

      try
      {
         ln = in.readLine();
         lnNumber++;

         success = ln != null && !ln.toLowerCase().trim().equals("eof");
      } //try
      catch (EOFException e)
      {
         success = false;
      }
      catch (IOException e)
      {
         Util.exit("Error reading line " + lnNumber, fileName);
      }

      return success;
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
      config.numActLayers = read.length;

      if (config.numActLayers < 3)
      {
         Util.exit("Network requires a minimum of three activation layers", fileName);
      }
      else
      {
         config.OUTPUT_LAYER = config.numActLayers - 1;
         config.LAST_HIDDEN_LAYER = config.OUTPUT_LAYER - 1;
         config.numActsInLayers = new int[config.numActLayers];

         for (n = 0; n <= config.OUTPUT_LAYER; n++)
         {
            config.numActsInLayers[n] = Util.toInt(read[n].trim());

            if (config.numActsInLayers[n] == 0)
            {
               Util.exit("Invalid network configuration parameters. Parsed: " + ln, fileName);
            }
         }
      } //if (config.numLayers < 3)...else
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
         out.writeUTF(Util.newLine("Network configuration: " +
               Util.formatConfiguration(config.numActsInLayers, config.numActLayers)));
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
         out.writeUTF(Util.newLine("Weights file In: " + config.weightsFileIn));
         out.writeUTF(Util.newLine("Weights file Out: " + config.weightsFileOut));
         out.writeUTF(Util.newLine("Run case Number: " + config.runCaseNum));
         out.writeUTF(Util.newLine("Keep Alive Interval: " + config.keepAliveInterval));
         out.writeUTF(Util.newLine("Save Weights Interval: " + config.saveWeightsInterval));
         out.writeUTF(Util.newLine("Decimal precision: " + config.decimalPrecision));
         out.writeUTF(Util.newLine("Activation Function: " + config.activationFunction.toString()));

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
