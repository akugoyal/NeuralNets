import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * This class is a fully connected, N-layer feedforward neural network. The network can be run in
 * training, run-all, or run single mode. In training mode, the network uses a gradient descent
 * algorithm with backpropagation to calculate the delta weights, until either the maximum
 * number of iterations is reached or the average error is under the error threshold. In run-all
 * mode, the network runs each input in the truth table through the network and prints the value
 * of the output node(s). In run single mode the network runs only the input specified in the
 * configuration file.
 *
 * The network reads the configuration parameters from a file, using a ConfigFileIO object to
 * read/write from/to the file and storing the network configuration in a Config object.
 *
 * Table of Contents:
 * 1. setConfig(String[] args)
 * 2. echoConfig()
 * 3. allocateMemory()
 * 4. randomize(double low, double high)
 * 5. populateArrays()
 * 6. randomizeWeights()
 * 7. runSingleCase()
 * 8. runAll()
 * 9. runDuringTrain(int caseNum)
 * 10. activationFunction(double x)
 * 11. activationFunctionPrime(double x)
 * 12. runError(int caseNum)
 * 13. reportFull()
 * 14. formatDoubleArray(double[] arr, int len)
 * 15. formatTime(double seconds)
 * 16. main(String[] args)
 *
 * Author: Akul Goyal
 * Date of Creation: 01/30/2024
 */

public class Main
{
   /**
    * Default files for reading and writing the configuration, weights, and truth table files.
    */
   public static final String DEFAULT_CONFIG_FILE = "defaultConfig.txt";
   public static final String DEFAULT_WEIGHTS_FILE = "weights.bin";
   public static final String DEFAULT_TRUTH_TABLE_FILE = "defaultTruthTable.txt";

   /**
    * Constants for the network mode
    */
   public static final int TRAINING = 0;
   public static final int RUN_ALL = 1;
   public static final int RUN_SINGLE = 2;

   /**
    * Constants and variables for tracking the elapsed time since the start of the program.
    */
   public static final double NANO_PER_SEC = 1.0e9;        //Nanoseconds per second
   public static final double MILLIS_PER_SEC = 1000.0;     //Milliseconds per second
   public static final double SEC_PER_MIN = 60.0;          //Seconds per minute
   public static final double MIN_PER_HR = 60.0;           //Minutes per hour
   public static final double HR_PER_DAY = 24.0;           //Hours per day
   public static final double DAYS_PER_WK = 7.0;           //Days per week
   public static double initTime;                          //Time at the start of the program
   public static DateTimeFormatter dtf;
   public static LocalDateTime now;

   /**
    * Variables for the IO objects for the configuration, weights, and truth table files.
    */
   public static ConfigFileIO configFileIO;              //Object for reading/writing the config file
   public static WeightsFileIO weightsFileIOLoader;      //Object for reading/writing the input weights file
   public static WeightsFileIO weightsFileIOSaver;       //Object for reading/writing the output weights file
   public static TruthTableFileIO truthTableFileIO;      //Object for reading/writing the truth table file
   public static Config config;                          //Network configuration object

   /**
    * Basic variables for the fundamental network functionality. Used in all modes.
    */
   public static double[][] a;                          //Activation values for each node
   public static double[][][] w;                        //Weights between each layer
   public static double[][] truthTableInputs;           //Inputs for each case in the truth table

   /**
    * Variables used during training mode only
    */
   public static int trainIterations;                   //Number of iterations done during training
   public static double[][] truthTableOutputs;          //Expected outputs for each case in the truth table
   public static double[][] theta;                      //Theta values for each node
   public static double[][] psi;                        //Psi values for each node
   public static double error;                          //Average error for the network

   /**
    * Variables for formatting.
    */
   public static DecimalFormat df;
   public static DecimalFormat df1;

   /**
    * Loads the configuration from the file passed as the first command line argument. If no file is
    * passed, the default configuration file is used. The configuration is stored in a Config object.
    * It also creates objects which can read and write the weights and truth table files, but doesn't
    * load or save either yet.
    */
   public static void setConfig(String[] args)
   {

      String configFile = DEFAULT_CONFIG_FILE;

/**
 * Record the time at the start of the program.
 */
      {
         initTime = System.nanoTime();
         dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss");
         now = LocalDateTime.now();
      }

/**
 * Load the configuration from file.
 */
      if (args.length > 0)
      {
         configFile = args[0];
      }

      configFileIO = new ConfigFileIO(configFile, DEFAULT_WEIGHTS_FILE, DEFAULT_TRUTH_TABLE_FILE);
      config = configFileIO.loadConfig();

      df1 = new DecimalFormat("#".repeat(config.decimalPrecision) + "." +
            "0".repeat(config.decimalPrecision) + "E0");
      df1.setRoundingMode(java.math.RoundingMode.FLOOR);

      df = new DecimalFormat("#".repeat(config.decimalPrecision) + "0." +
            "0".repeat(config.decimalPrecision));
      df.setRoundingMode(java.math.RoundingMode.FLOOR);

      truthTableFileIO = new TruthTableFileIO(config.numActsInLayers[config.INPUT_LAYER],
            config.numActsInLayers[config.OUTPUT_LAYER],
            config.numCases, config.networkMode, config.truthTableFile);

      if (config.loadWeights)
      {
         weightsFileIOLoader = new WeightsFileIO(config.weightsFileIn, config);
      }
      if (config.saveWeightsInterval > 0)
      {
         weightsFileIOSaver = new WeightsFileIO(config.weightsFileOut, config);
      }
   } //public static void setConfig(String[] args)

   /**
    * Prints the configuration parameters.
    */
   public static void echoConfig()
   {
      System.out.println("\n====================================================================" +
            "=====================================================");
      System.out.println("======================================================================" +
            "===================================================");
      System.out.println("======================================================================" +
            "===================================================");

      System.out.println("Network configuration: " + Util.formatConfiguration(
            config.numActsInLayers, config.numActLayers));
      System.out.println("Activation function: " + config.activationFunction.toString());
      if (!config.activationFunction.BOUNDED)
      {
         System.out.println("WARNING: Activation function is unbounded. May result in NaN values.");
      }

      System.out.println();

      if (config.networkMode == TRAINING)
      {
         System.out.println("Network is in mode: " + config.networkMode + " (Training)\n");
         System.out.println("Number of training cases: " + config.numCases);
         System.out.println("Max training iterations: " + config.maxIters);
         System.out.println("Lambda value: " + config.lambda);
         System.out.println("Error threshold: " + config.errThreshold);

         if (config.keepAliveInterval > 0)
         {
            System.out.println("Keep alive interval: " + config.keepAliveInterval);
         }
         else
         {
            System.out.println("Keep alive interval: Disabled");
         }

         if (config.saveWeightsInterval > 0)
         {
            System.out.println("Save weights interval: " + config.saveWeightsInterval);
         }
         else
         {
            System.out.println("Save weights interval: Disabled");
         }

         if (config.etaInterval > 0)
         {
            System.out.println("ETA interval: " + config.etaInterval);
         }
         else
         {
            System.out.println("ETA interval: Disabled");
         }

         System.out.println("\nLoading truth table from file: " + config.truthTableFile);
      } //if (config.networkMode == TRAINING)
      else if (config.networkMode == RUN_ALL)
      {
         System.out.println("Network is in mode: " + config.networkMode + " (Run All)\n");
         System.out.println("Loading inputs from file: " + config.truthTableFile);
      }
      else
      {
         System.out.println("Network is in mode: " + config.networkMode + " (Run Single)");
         System.out.println("Running case number: " + config.runCaseNum + "\n");
         System.out.println("Loading inputs from file: " + config.truthTableFile);
      }

      System.out.println();

      if (config.loadWeights)
      {
         System.out.println("Loading weights from file: " + config.weightsFileIn);
      }
      else
      {
         System.out.println("Randomizing weights with range: " + config.lowRand + " to " +
               config.highRand);
      }

      if (config.saveWeightsInterval > 0)
      {
         System.out.println("Saving weights to file: " + config.weightsFileOut);
      }


      System.out.println("\nStarting time: " + dtf.format(now));
      System.out.println("----------------------------------------------------------------------" +
            "------------------------------");
   } //public static void echoConfig()

   /**
    * Initializes the activation, weights, and truth table arrays. Allocates space on the heap for the
    * arrays and variables used.
    */
   public static void allocateMemory()
   {
      int n;
/**
 * Allocates the following memory only if the network is in training mode.
 */
      if (config.networkMode == TRAINING)
      {
         psi = new double[config.numActLayers][];
         for (n = config.OUTPUT_LAYER; n > config.FIRST_HIDDEN_LAYER; n--)
         {
            psi[n] = new double[config.numActsInLayers[n]];
         }

         error = config.errThreshold + 1.0;
         trainIterations = 0;

         truthTableOutputs = new double[config.numCases][config.numActsInLayers[config.OUTPUT_LAYER]];

         theta = new double[config.numActLayers][];
         for (n = config.LAST_HIDDEN_LAYER; n >= config.FIRST_HIDDEN_LAYER; n--)
         {
            theta[n] = new double[config.numActsInLayers[n]];
         }
      } //if (config.networkMode == TRAINING)

/**
 * The following memory is always allocated.
 */
      a = new double[config.numActLayers][];
      for (n = config.OUTPUT_LAYER; n >= config.INPUT_LAYER; n--)
      {
         a[n] = new double[config.numActsInLayers[n]];
      }

      w = new double[config.numActLayers - 1][][];
      for (n = config.LAST_HIDDEN_LAYER; n >= config.INPUT_LAYER; n--)
      {
         w[n] = new double[config.numActsInLayers[n]][config.numActsInLayers[n + 1]];
      }

      truthTableInputs = new double[config.numCases][config.numActsInLayers[config.INPUT_LAYER]];
   } //public static void allocateMemory()

   /**
    * Uses Math.random() to return a random number between low (inclusive) and high (exclusive).
    *
    * @param low  the lower bound for the random number range
    * @param high the higher bound for the random number range
    * @return a random double between low and high
    */
   public static double randomize(double low, double high)
   {
      return (Math.random() * (high - low)) + low;
   } //public static double randomize(double low, double high)

   /**
    * Populates the weights from file or randomly, depending on the value of the loadWeights
    * boolean. Also, populates the truth table from the specified file.
    */
   public static void populateArrays()
   {
      if (config.networkMode != TRAINING)
      {
         truthTableFileIO.loadTruthTableInputs(truthTableInputs);

         if (config.networkMode == RUN_ALL)
         {
            a[config.INPUT_LAYER] = truthTableInputs[0];
         }
         else
         {
            a[config.INPUT_LAYER] = truthTableInputs[config.runCaseNum];
         }
      } //if (config.networkMode != TRAINING)
      else {
         truthTableFileIO.loadTruthTable(truthTableInputs, truthTableOutputs);
      }

      if (config.loadWeights)
      {
         weightsFileIOLoader.loadWeights(w);
      } //if (config.loadWeights)
      else
      {
         randomizeWeights();
      }
   } //public static void populateArrays()

   /**
    * Populates the weights with random values between the lowRand and highRand values specified in
    * the configuration file.
    */
   public static void randomizeWeights()
   {
      int n;
      int k;
      int j;

      for (n = config.LAST_HIDDEN_LAYER; n >= config.INPUT_LAYER; n--)
      {
         for (j = 0; j < config.numActsInLayers[n + 1]; j++)
         {
            for (k = 0; k < config.numActsInLayers[n]; k++)
            {
               w[n][k][j] = randomize(config.lowRand, config.highRand);
            }
         }
      } //for (n = config.LAST_HIDDEN_LAYER; n >= config.INPUT_LAYER; n--)
   } //public static void randomizeWeights()

   /**
    * Runs the network on the input given by the input activations array, a. Each hidden node is the
    * activation function of the sum of the dot products of each of the previous activations and the
    * corresponding weight between the previous activation and the current activation. This
    * method does not save the theta values because they are not needed after the network runs.
    */
   public static void runSingleCase()
   {
      int n;
      int k;
      int j;
      double thetaAccumulator;

      for (n = config.FIRST_HIDDEN_LAYER; n <= config.OUTPUT_LAYER; n++)
      {
         for (j = 0; j < config.numActsInLayers[n]; j++)
         {
            thetaAccumulator = 0.0;
            for (k = 0; k < config.numActsInLayers[n - 1]; k++)
            {
               thetaAccumulator += a[n - 1][k] * w[n - 1][k][j];
            }
            a[n][j] = activationFunction(thetaAccumulator);
         }
      } //for (n = config.FIRST_HIDDEN_LAYER; n <= config.OUTPUT_LAYER; n++)
   } //public static void runSingleCase()

   /**
    * If the network is in the training or run all modes, it runs the network on all the inputs
    * given by the truth table. If the network is in run single mode, it runs the network on the
    * input specified in the configuration file.
    */
   public static void runAll()
   {
      int caseIter;

      if (config.networkMode == RUN_ALL || config.networkMode == TRAINING)
      {
         for (caseIter = 0; caseIter < config.numCases; caseIter++)
         {
            a[config.INPUT_LAYER] = truthTableInputs[caseIter];
            runSingleCase();
         }
      } //if (config.networkMode == RUN_ALL || config.networkMode == TRAINING)
      else
      {
         a[config.INPUT_LAYER] = truthTableInputs[config.runCaseNum];
         runSingleCase();
      }
   } //public static void runAll()

   /**
    * Runs the network the same as the runSingleCase() method, but also saves the theta values for
    * the hidden layer and saves the psi values for the output layer.
    */
   public static void runDuringTrain(int caseNum)
   {
      int n;
      int k;
      int j;
      double thetaI;
      double omegaI;
      double Ti;

      for (n = config.FIRST_HIDDEN_LAYER; n <= config.LAST_HIDDEN_LAYER; n++)
      {
         for (j = 0; j < config.numActsInLayers[n]; j++)
         {
            theta[n][j] = 0.0;
            for (k = 0; k < config.numActsInLayers[n - 1]; k++)
            {
               theta[n][j] += a[n - 1][k] * w[n - 1][k][j];
            }
            a[n][j] = activationFunction(theta[n][j]);
         } //for (j = 0; j < config.numActsInLayers[n]; j++)
      } //for (n = config.FIRST_HIDDEN_LAYER; n <= config.LAST_HIDDEN_LAYER; n++)

      n = config.OUTPUT_LAYER;
      for (j = 0; j < config.numActsInLayers[n]; j++)
      {
         thetaI = 0.0;
         for (k = 0; k < config.numActsInLayers[n - 1]; k++)
         {
            thetaI += a[n - 1][k] * w[n - 1][k][j];
         }
         a[n][j] = activationFunction(thetaI);

         Ti = truthTableOutputs[caseNum][j];
         omegaI = Ti - a[n][j];
         psi[n][j] = omegaI * activationFunctionPrime(thetaI);
      } //for (j = 0; j < config.numActsInLayers[n]; j++)
   } //public static void runDuringTrain(int caseNum)

   /**
    * Activation function for the network.
    * @param x the input to the activation function
    * @return the output of the activation function
    */
   public static double activationFunction(double x)
   {
      return config.activationFunction.f(x);
   } //public static double activationFunction(double x)

   /**
    * Derivative of the activation function for the network.
    * @param x the input to the derivative of the activation function
    * @return the output of the derivative
    */
   public static double activationFunctionPrime(double x)
   {
      return config.activationFunction.fPrime(x);
   } //public static double activationFunctionPrime(double x)

   /**
    * Calculates the error for the given case number by first running the network on the current
    * input activations and then comparing it to the expected values in the truth table for the
    * given case number. The error is half the sum of the squares of the differences between the
    * expected output and the actual output.
    *
    * @param caseNum the case number to calculate the error for
    * @return the average error
    */
   public static double runError(int caseNum)
   {
      int n;
      int i;
      double errorAccumulator;
      double Ti;

      runSingleCase();

      errorAccumulator = 0.0;
      n = config.OUTPUT_LAYER;
      for (i = 0; i < config.numActsInLayers[n]; i++)
      {
         Ti = truthTableOutputs[caseNum][i];
         errorAccumulator += (Ti - a[n][i]) * (Ti - a[n][i]);
      }

      return 0.5 * errorAccumulator;
   } //public static double runError(int caseNum)

   /**
    * Prints a report at the end of either training or running. For training, it prints the
    * reason for ending training, the number of iterations, the average error, and the truth
    * table with outputs. If the network is in the run-all mode, it runs every truth table case
    * and reports each case. If the network is in the run single mode, it just reports the specified
    * case. It also prints the time elapsed since the start of the program.
    */
   public static void reportFull()
   {
      int n;
      int caseIter;

      System.out.println("----------------------------------------------------------------------" +
            "------------------------------");

      if (config.networkMode == TRAINING || config.networkMode == RUN_ALL)
      {
         if (config.networkMode == TRAINING)
         {
            if (trainIterations >= config.maxIters)
            {
               System.out.println("Ended training due to max iterations reached.");
            }
            else if (error < config.errThreshold)
            {
               System.out.println("Ended training due to reaching error threshold.");
            }

            System.out.println("Reached " + trainIterations + " iterations.");
            System.out.println("Reached " + error + " average error.");
         } //if (config.networkMode == TRAINING)

         n = config.INPUT_LAYER;
         for (caseIter = 0; caseIter < config.numCases; caseIter++)
         {
            a[n] = truthTableInputs[caseIter];
            runSingleCase();
            reportSingleCase(caseIter);
         }
      } //if (config.networkMode == TRAINING || config.networkMode == RUN_ALL)
      else if (config.networkMode == RUN_SINGLE)
      {
         reportSingleCase(config.runCaseNum);
      }

      System.out.print("\nElapsed Time: " + formatTime((System.nanoTime() - initTime)
            / NANO_PER_SEC));
      now = LocalDateTime.now();
      System.out.println("Ended at: " + dtf.format(now) + "\n");
   } //public static void reportFull()

   /**
    * Formats the given double array into a string with all the doubles formatted to the given
    * precision.
    *
    * @param arr the double array to format
    * @param len the length of the array
    * @return the formatted string
    */
   public static String formatDoubleArray(double[] arr, int len)
   {
      int iter;
      StringBuilder res;

      res = new StringBuilder();
      res.append("[");

      for (iter = 0; iter < len; iter++)
      {
         res.append(df.format(arr[iter]));
         if (iter < len - 1)
         {
            res.append(", ");
         }
      } //for (iter = 0; iter < len; iter++)
      res.append("]");

      return res.toString();
   } //public static String formatDoubleArray(double[] arr, int len)

   /**
    * Prints a report for the given case number. If the network is in training mode, it prints the
    * input case, the expected output, and the actual output. Otherwise, it
    * just prints the input case and the output. Does not run the network.
    *
    * @param num the case number to report
    */
   public static void reportSingleCase(int num)
   {
      System.out.print("Input Case #" + num);// + ": " + Arrays.toString(a[config.INPUT_LAYER]));

      if (config.networkMode == TRAINING)
      {
         System.out.print("     Expected: " + Arrays.toString(truthTableOutputs[num]));
      }

      System.out.println("     Output: " + formatDoubleArray(a[config.OUTPUT_LAYER],
            config.numActsInLayers[config.OUTPUT_LAYER]));
   } //public static void reportSingleCase(int num)

   /**
    * Trains the network using a gradient descent algorithm with backpropagation. The method trains
    * until the max number of iterations has been reached or the average error is under the error
    * threshold. For each iteration, every truth table case is run, the error is computed, and then
    * the delta weights are calculated and applied.
    */
   public static void train()
   {
      int n;
      int m;
      int k;
      int j;

      int caseIter;
      double omegaJ;
      double omegaM;
      double psiM;

      double prevTime;
      double prevError;
      double estItersToTrain;
      double timeToTrain;
      double prevDeltaTimeEMA;
      double currDeltaTimeEMA;
      double multiplier;
      double currDeltaErrorEMA = 0.0;
      double prevDeltaErrorEMA;

      System.out.println("Training...");

/**
 * Each iteration is defined as each execution of the body of the following while loop.
 */
      for (error = 0.0, caseIter = 0; caseIter < config.numCases; caseIter++)
      {
         a[config.INPUT_LAYER] = truthTableInputs[caseIter];
         error += runError(caseIter);
      }
      error /= (double) config.numCases;

      prevTime = System.nanoTime() / NANO_PER_SEC;
      prevDeltaTimeEMA = 0.0;
      prevDeltaErrorEMA = 0.0;
      prevError = error;


      System.out.println("Starting training at iteration 0 and error " + df.format(error));
      while (trainIterations < config.maxIters && error > config.errThreshold)
      {
         if (config.keepAliveInterval > 0 && trainIterations > 0 &&
               trainIterations % config.keepAliveInterval == 0)
         {
            System.out.print("Reached training iteration " + trainIterations + " with error " +
                  df.format(error) + " at ");
            System.out.print(formatTime((System.nanoTime() - initTime) / NANO_PER_SEC));
         }

         if (config.saveWeightsInterval > 0 && trainIterations > 0 &&
               trainIterations % config.saveWeightsInterval == 0)
         {
            weightsFileIOSaver = new WeightsFileIO("iter" + trainIterations + "_" + config.weightsFileOut, config);
            weightsFileIOSaver.saveWeights(w);
            System.out.println("Saved weights at iteration " + trainIterations);
         }

         if (config.etaInterval > 0 && trainIterations > 0)
         {
            multiplier = 2.0 / ((double) trainIterations);

            currDeltaTimeEMA =
                  (System.nanoTime() / NANO_PER_SEC - prevTime) * multiplier + prevDeltaTimeEMA * (1.0 - multiplier);
            prevTime = System.nanoTime() / NANO_PER_SEC;
            prevDeltaTimeEMA = currDeltaTimeEMA;

            currDeltaErrorEMA =
                  (prevError - error) * multiplier + prevDeltaErrorEMA * (1.0 - multiplier);
            prevError = error;
            prevDeltaErrorEMA = currDeltaErrorEMA;

            if (trainIterations % config.etaInterval == 0)
            {
               estItersToTrain = (error - config.errThreshold) / currDeltaErrorEMA;

               if (estItersToTrain > 0 && (estItersToTrain + trainIterations) < (double) config.maxIters)
               {
                  timeToTrain = (estItersToTrain * currDeltaTimeEMA);
                  System.out.print("ETA: Will converge in " + df.format(estItersToTrain) + " " +
                        "iterations and " + formatTime(timeToTrain));
               }
               else
               {
                  System.out.print("ETA: Will fail in " + formatTime(
                        ((double) (config.maxIters - trainIterations)) * currDeltaTimeEMA));
               }
            }
         }

         for (error = 0.0, caseIter = 0; caseIter < config.numCases; caseIter++)
         {
            a[config.INPUT_LAYER] = truthTableInputs[caseIter];
            runDuringTrain(caseIter);

            for (n = config.LAST_HIDDEN_LAYER; n > config.FIRST_HIDDEN_LAYER; n--)
            {
               for (k = 0; k < config.numActsInLayers[n]; k++)
               {
                  omegaJ = 0.0;
                  for (j = 0; j < config.numActsInLayers[n + 1]; j++)
                  {
                     omegaJ += psi[n + 1][j] * w[n][k][j];
                     w[n][k][j] += config.lambda * a[n][k] * psi[n + 1][j];
                  }

                  psi[n][k] = omegaJ * activationFunctionPrime(theta[n][k]);
               } //for (k = 0; k < config.numActsInLayers[n]; k++)
            } //for (n = config.LAST_HIDDEN_LAYER; n > config.FIRST_HIDDEN_LAYER; n--)

            n = config.FIRST_HIDDEN_LAYER;
            for (k = 0; k < config.numActsInLayers[n]; k++)
            {
               omegaM = 0.0;
               for (j = 0; j < config.numActsInLayers[n + 1]; j++)
               {
                  omegaM += psi[n + 1][j] * w[n][k][j];
                  w[n][k][j] += config.lambda * a[n][k] * psi[n + 1][j];
               }

               psiM = omegaM * activationFunctionPrime(theta[n][k]);
               for (m = 0; m < config.numActsInLayers[n - 1]; m++)
               {
                  w[n - 1][m][k] += config.lambda * a[n - 1][m] * psiM;
               }
            } //for (k = 0; k < config.numActsInLayers[n]; k++)

            error += runError(caseIter);
         } //for (caseIter = 0; caseIter < config.numCases; caseIter++)

         error /= (double) config.numCases;
         trainIterations++;
      } //while (trainIterations < config.maxIters && error > config.errThreshold)
   } //public static void train()

   /**
    * Accept a value representing seconds elapsed and print out a decimal value in easier to
    * digest units.
    *
    * Author: Dr. Eric R. Nelson
    * This method was modified by Akul Goyal to remove magic numbers, return the formatted time
    * instead of printing it, and follow the decimal formatting specified by the network configuration.
    */
   public static String formatTime(double seconds)
   {
      double minutes, hours, days, weeks;
      String time = "";

      if (seconds < 1.)
         time += df.format(seconds * MILLIS_PER_SEC) + " milliseconds";
      else if (seconds < SEC_PER_MIN)
         time += df.format(seconds) + " seconds";
      else
      {
         minutes = seconds / SEC_PER_MIN;

         if (minutes < MIN_PER_HR)
            time += df.format(minutes) + " minutes";
         else
         {
            hours = minutes / MIN_PER_HR;

            if (hours < HR_PER_DAY)
               time += df.format(hours) + " hours";
            else
            {
               days = hours / HR_PER_DAY;

               if (days < DAYS_PER_WK)
                  time += df.format(days) + " days";
               else
               {
                  weeks = days / DAYS_PER_WK;

                  time += df.format(weeks) + " weeks";
               }
            } //if (hours < HR_PER_DAY)...else
         } //if (minutes < MIN_PER_HR)...else
      } //else if (seconds < SEC_PER_MIN)...else

      return time + "\n";
   } //public static String formatTime(double seconds)

   /**
    * Sets the configuration parameters, echos the network's settings, allocates memory for all
    * arrays and variables, populates all the arrays, loading the weights from file depending on
    * the loadWeights boolean and loading the truth table from file. Either trains or runs the
    * network depending on the network mode configuration. Then it saves the weights to file depending
    * on the saveWeights configuration, and prints a final report.
    *
    * @param args the command line arguments
    */
   public static void main(String[] args)
   {
      try
      {
         setConfig(args);
         echoConfig();
         allocateMemory();
         populateArrays();

         if (config.networkMode == TRAINING)
         {
            train();
            runAll();
         }
         else
         {
            runAll();
         }

         if (config.saveWeightsInterval > 0)
         {
            System.out.println("Saving weights...");
            weightsFileIOSaver.saveWeights(w);
         }

         reportFull();
      } //try
      catch (IllegalArgumentException e)
      {
         System.out.println(e.getMessage());
      }
   } //public static void main(String[] args)
} //public class Main