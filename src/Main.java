import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * This class is an A-B-C-D feedforward neural network. The network is fully connected and supports
 * a sigmoid activation function. The network can be run in training, run-all, or run single mode.
 * In training mode, the network uses a gradient descent algorithm with backpropagation to
 * calculate the delta weights, until either the maximum number of iterations is reached or the
 * average error is under the error threshold. In run-all mode, the network runs each input in
 * the truth table through the network and prints the value of the output node(s).
 *
 * The network reads the configuration parameters from a file. The configuration file contains
 * the following parameters:
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
 *
 * Missing parameters will be set to their default values.
 *
 * Author: Akul Goyal
 * Date of Creation: 01/30/2024
 */

public class Main
{
   public static final int INPUT_LAYER = 0;
   public static final int HIDDEN_LAYER1 = 1;
   public static final int HIDDEN_LAYER2 = 2;
   public static final int OUTPUT_LAYER = 3;
/**
 * Variables for reading and writing the configuration, weights, and truth table files.
 */
   public static final String DEFAULT_CONFIG_FILE = "config.txt";
   public static final String DEFAULT_WEIGHTS_FILE = "weights.bin";
   public static final String DEFAULT_TRUTH_TABLE_FILE = "truthTable.txt";

/**
 * Variables for the network configuration
 */
   public static final int TRAINING = 0;
   public static final int RUN_ALL = 1;
   public static final int RUN_SINGLE = 2;

/**
 * Variables for the printTime() method
 */
   public static final double NANO_PER_SEC = 1.0e9;        //Nanoseconds per second
   public static final double MILLIS_PER_SEC = 1000.0;     //Milliseconds per second
   public static final double SEC_PER_MIN = 60.0;          //Seconds per minute
   public static final double MIN_PER_HR = 60.0;           //Minutes per hour
   public static final double HR_PER_DAY = 24.0;           //Hours per day
   public static final double DAYS_PER_WK = 7.0;           //Days per week
   public static WeightsFileIO weightsFileIO;
   public static TruthTableFileIO truthTableFileIO;
   public static Config config;
   public static double initTime;                          //Time at the start of the program

/**
 * Variables used during both running and training modes
 */
   public static double[][] a;                              //Array of input activation nodes
   public static double[][][] w;                         //Weights between input and hidden1 layer
   public static int trainIterations;                    //Number of iterations done during training
   public static double[][] truthTableInputs;
   public static double[][] truthTableOutputs;

/**
 * Variables used during training mode only
 */
   public static double[][] theta;
   public static double[] psiI;
   public static double[] psiJ;
   public static double error;

/**
 * Loads the configuration from the file passed as the first command line argument. If no file is
 * passed, the default configuration file is used. The configuration is stored in a Config object.
 * It also creates objects which can read and write the weights and truth table files, but doesn't
 * load or save either yet.
 */
   public static void setConfig(String[] args)
   {
      ConfigFileIO configFileIO;
      String configFile = DEFAULT_CONFIG_FILE;

/**
 * Record the time at the start of the program. Not for the user to modify.
 */
      initTime = System.nanoTime();

/**
 * Load the configuration from file.
 */
      if (args.length > 0)
      {
         configFile = args[0];
      }

      configFileIO = new ConfigFileIO(configFile, DEFAULT_WEIGHTS_FILE, DEFAULT_TRUTH_TABLE_FILE);
      config = configFileIO.loadConfig();

      truthTableFileIO = new TruthTableFileIO(config.numInAct, config.numOutAct,
            config.numCases, config.truthTableFile);
      if (config.loadWeights || config.saveWeights)
      {
         weightsFileIO = new WeightsFileIO(config.numInAct, config.numHidAct1,
               config.numHidAct2, config.numOutAct,
               config.weightsFile);
      }
   } //public static void setConfig(String[] args)

/**
 * Prints the configuration parameters.
 */
   public static void echoConfig()
   {
      System.out.println(
            "\n" +
                  "=========================================================================================================================");
      System.out.println("=========================================================================================================================");
      System.out.println("=========================================================================================================================");
      System.out.println("Network configuration: " + config.numInAct + "-" + config.numHidAct1 +
            "-" + config.numHidAct2 + "-" + config.numOutAct);

      if (config.networkMode == TRAINING)
      {
         System.out.println("Network is in mode: " + config.networkMode + " (Training)\n");
         System.out.println("Number of training cases: " + config.numCases);
         System.out.println("Max training iterations: " + config.maxIters);
         System.out.println("Lambda value: " + config.lambda);
         System.out.println("Error threshold: " + config.errThreshold);
         System.out.println("Keep alive interval: " + config.keepAliveInterval + "\n");
      } //if (config.networkMode == TRAINING)
      else if (config.networkMode == RUN_ALL)
      {

         System.out.println("Network is in mode: " + config.networkMode + " (Run All)\n");
      }
      else
      {
         System.out.println("Network is in mode: " + config.networkMode + " (Run Single)");
         System.out.println("Running case number: " + config.runCaseNum + "\n");
      }

      System.out.println("Loading truth table from file: " + config.truthTableFile);

      if (config.loadWeights)
      {
         System.out.println("Loading weights from file: " + config.weightsFile);
      } //if (config.loadWeights)
      else
      {
         System.out.println("Randomizing weights with range: " + config.lowRand + " to " +
               config.highRand);
      }

      if (config.saveWeights)
      {
         System.out.println("Saving weights to file: " + config.weightsFile);
      }

      System.out.println("----------------------------------------------------------------------------------------------------");
   } //public static void echoConfig

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
         psiI = new double[config.numOutAct];
         psiJ = new double[config.numHidAct2];
         error = Double.MAX_VALUE;
         truthTableOutputs = new double[config.numCases][config.numOutAct];

         theta = new double[config.numLayers][];
         n = HIDDEN_LAYER1;
         theta[n] = new double[config.numHidAct1];
         n = HIDDEN_LAYER2;
         theta[n] = new double[config.numHidAct2];
      } //if (config.networkMode == TRAINING)

/**
 * The following memory is always allocated.
 */
      a = new double[config.numLayers][];
      n = INPUT_LAYER;
      a[n] = new double[config.numInAct];
      n = HIDDEN_LAYER1;
      a[n] = new double[config.numHidAct1];
      n = HIDDEN_LAYER2;
      a[n] = new double[config.numHidAct2];
      n = OUTPUT_LAYER;
      a[n] = new double[config.numOutAct];

      w = new double[config.numLayers - 1][][];
      n = INPUT_LAYER;
      w[n] =  new double[config.numInAct][config.numHidAct1];
      n = HIDDEN_LAYER1;
      w[n] = new double[config.numHidAct1][config.numHidAct2];
      n = HIDDEN_LAYER2;
      w[n] = new double[config.numHidAct2][config.numOutAct];

      truthTableInputs = new double[config.numCases][config.numInAct];
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
      int n;
      if (config.networkMode != TRAINING)
      {
         truthTableFileIO.loadTruthTableInputs(truthTableInputs);
         n = INPUT_LAYER;
         if (config.networkMode == RUN_ALL)
         {
            a[n] = truthTableInputs[0];
         }
         else
         {
            a[n] = truthTableInputs[config.runCaseNum];
         }
      } //if (config.networkMode != TRAINING)
      else {
         truthTableFileIO.loadTruthTable(truthTableInputs, truthTableOutputs);
      }

      if (config.loadWeights)
      {
         weightsFileIO.loadWeights(w);
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
      int m;
      int k;
      int j;
      int i;

/**
 * Populates mkWeights with random weights
 */
      n = INPUT_LAYER;
      for (m = 0; m < config.numInAct; m++)
      {
         for (k = 0; k < config.numHidAct1; k++)
         {
            w[n][m][k] = randomize(config.lowRand, config.highRand);
         } //for (j = 0; j < config.numHidAct; j++)
      } //for (k = 0; k < config.numInAct; k++)

/**
 * Populates kjWeights with random weights
 */
      n = HIDDEN_LAYER1;
      for (k = 0; k < config.numHidAct1; k++)
      {
         for (j = 0; j < config.numHidAct2; j++)
         {
            w[n][k][j] = randomize(config.lowRand, config.highRand);
         } //for (j = 0; j < config.numHidAct; j++)
      } //for (k = 0; k < config.numInAct; k++)

/**
 * Populates jiWeights with random weights
 */
      n = HIDDEN_LAYER2;
      for (j = 0; j < config.numHidAct2; j++)
      {
         for (i = 0; i < config.numOutAct; i++)
         {
            w[n][j][i] = randomize(config.lowRand, config.highRand);
         } //for (i = 0; i < config.numOutAct; i++)
      } //for (j = 0; j < config.numHidAct; j++)
   } //public static void randomizeWeights()

/**
 * Runs the network on the input given by the input activations array, a. Each hidden node is the
 * sigmoid of the sum of the dot products of each of the previous activations and the
 * corresponding weight between the previous activation and the current activation. This
 * method does not save the theta values because they are not needed after the network runs.
 */
   public static void runSingleCase()
   {
      int n;
      int m;
      int k;
      int j;
      int i;
      double thetaAccumulator;

/**
 * Computes the hidden1 layer.
 */
      for (k = 0; k < config.numHidAct1; k++)
      {
         thetaAccumulator = 0.0;
         n = INPUT_LAYER;
         for (m = 0; m < config.numInAct; m++)
         {
            thetaAccumulator += a[n][m] * w[n][m][k];
         }
         n = HIDDEN_LAYER1;
         a[n][k] = activationFunction(thetaAccumulator);
      } //for (j = 0; j < config.numHidAct; j++)

/**
 * Computes the hidden2 layer.
 */
      for (j = 0; j < config.numHidAct2; j++)
      {
         thetaAccumulator = 0.0;
         n = HIDDEN_LAYER1;
         for (k = 0; k < config.numHidAct1; k++)
         {
            thetaAccumulator += a[n][k] * w[n][k][j];
         }
         n = HIDDEN_LAYER2;
         a[n][j] = activationFunction(thetaAccumulator);
      } //for (j = 0; j < config.numHidAct; j++)

/**
 * Computes the output layer.
 */
      for (i = 0; i < config.numOutAct; i++)
      {
         thetaAccumulator = 0.0;
         n = HIDDEN_LAYER2;
         for (j = 0; j < config.numHidAct2; j++)
         {
            thetaAccumulator += a[n][j] * w[n][j][i];
         }

         n = OUTPUT_LAYER;
//         System.out.println();
//         for (int hi = 0; hi < 4; hi++) {
//            System.out.println(a[hi].length);
//         }
//         System.out.println(n + " " + i + " " + config.numOutAct);
//         System.out.println();
         a[n][i] = activationFunction(thetaAccumulator);
      } //for (i = 0; i < config.numOutAct; i++)
   } //public static void runSingleCase()

/**
 * If the network is in the training or run all modes, it runs the network on all the inputs
 * given by the truth table. If the network is in run single mode, it runs the network on the
 * input specified in the configuration file.
 */
   public static void runAll()
   {
      int n;
      int caseIter;

      if (config.networkMode == RUN_ALL || config.networkMode == TRAINING)
      {
         for (caseIter = 0; caseIter < config.numCases; caseIter++)
         {
            n = INPUT_LAYER;
            a[n] = truthTableInputs[caseIter];
            runSingleCase();
         }
      } //if (config.networkMode == RUN_ALL || config.networkMode == TRAINING)
      else
      {
         n = INPUT_LAYER;
         a[n] = truthTableInputs[config.runCaseNum];
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
      int m;
      int k;
      int j;
      int i;
      double thetaI;
      double omegaI;
      double Ti;

      n = INPUT_LAYER;
      for (k = 0; k < config.numHidAct1; k++)
      {
         theta[n + 1][k] = 0.0;
         for (m = 0; m < config.numInAct; m++)
         {
            theta[n + 1][k] += a[n][m] * w[n][m][k];
         }
         a[n + 1][k] = activationFunction(theta[n + 1][k]);
      } //for (j = 0; j < config.numHidAct; j++)

/**
 * Computes and saves the theta values for the hidden layer.
 */
      n = HIDDEN_LAYER1;
      for (j = 0; j < config.numHidAct2; j++)
      {
         theta[n + 1][j] = 0.0;
         for (k = 0; k < config.numHidAct1; k++)
         {
            theta[n + 1][j] += a[n][k] * w[n][k][j];
         }
         a[n + 1][j] = activationFunction(theta[n + 1][j]);
      } //for (j = 0; j < config.numHidAct; j++)

/**
 * Computes the output layer and the psi values for the output layer. Does not save the theta
 * values.
 */
      n = HIDDEN_LAYER2;
      for (i = 0; i < config.numOutAct; i++)
      {
         thetaI = 0.0;
         for (j = 0; j < config.numHidAct2; j++)
         {
            thetaI += a[n][j] * w[n][j][i];
         }
         a[n + 1][i] = activationFunction(thetaI);

         Ti = truthTableOutputs[caseNum][i];
         omegaI = Ti - a[n + 1][i];
         psiI[i] = omegaI * activationFunctionPrime(thetaI);
      } //for (i = 0; i < config.numOutAct; i++)
   } //public static void runDuringTrain(int caseNum)

/**
 * Computes the sigmoid of the passed double using Math.exp() to compute the exponent of e.
 */
   public static double sigmoid(double x)
   {
      return 1.0 / (1.0 + Math.exp(-x));
   } //public static double sigmoid(double x)

/**
 * Computes the derivative of the sigmoid at the passed double using the previously
 * defined sigmoid() function.
 */
   public static double sigmoidPrime(double x)
   {
      double sigValue = sigmoid(x);
      return sigValue * (1.0 - sigValue);
   } //public static double sigmoidPrime(double x)

/**
 * Activation function for the network.
 * @param x the input to the activation function
 * @return the output of the activation function
 */
   public static double activationFunction(double x)
   {
      return sigmoid(x);
   } //public static double activationFunction(double x)

/**
 * Derivative of the activation function for the network.
 * @param x the input to the derivative of the activation function
 * @return the output of the derivative
 */
   public static double activationFunctionPrime(double x)
   {
      return sigmoidPrime(x);
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
      double avgErrorAccumulator;
      double Ti;

      runSingleCase();

      avgErrorAccumulator = 0.0;
      n = OUTPUT_LAYER;
      for (i = 0; i < config.numOutAct; i++)
      {
         Ti = truthTableOutputs[caseNum][i];
         avgErrorAccumulator += (Ti - a[n][i]) * (Ti - a[n][i]);
      }

      return 0.5 * avgErrorAccumulator;
   } //public static double runError(int caseNum)

/**
 * Prints a report at the end of either training or running. For training, it prints the
 * reason for ending training, the number of iterations, the average error, and the truth
 * table with outputs. If the network is in the run all mode, it runs every truth table case
 * and reports each case. If the network is in the run single mode, it just reports the specified
 * case. It also prints the time elapsed since the start of the program.
 */
   public static void reportFull()
   {
      int n;
      int caseIter;

      System.out.println("----------------------------------------------------------------------------------------------------");

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

         for (caseIter = 0; caseIter < config.numCases; caseIter++)
         {
            n = INPUT_LAYER;
            a[n] = truthTableInputs[caseIter];
            runSingleCase();
            reportSingleCase(caseIter);
         } //for (caseIter = 0; caseIter < config.numCases; caseIter++)
      } //if (config.networkMode == TRAINING || config.networkMode == RUN_ALL)
      else if (config.networkMode == RUN_SINGLE)
      {
         reportSingleCase(config.runCaseNum);
      }

      printTime((System.nanoTime() - initTime) / NANO_PER_SEC);
   } //public static void reportFull()

/**
 * Formats the given double array into a string with all the doubles formatted to the given
 * precision.
 * @param arr the double array to format
 * @param len the length of the array
 * @param precision the number of decimal places to format the doubles to
 * @return the formatted string
 */
   public static String formatDoubleArray(double[] arr, int len, int precision)
   {
      int iter;
      StringBuilder res;

      res = new StringBuilder();
      for (iter = 0; iter < len; iter++)
      {
         res.append(String.format("%." + precision + "f", arr[iter]));
         if (iter < len - 1)
         {
            res.append(", ");
         }
      } //for (iter = 0; iter < len; iter++)
      res.append("]");

      return res.toString();
   } //public static String formatDoubleArray(double[] arr, int len, int precision)

/**
 * Prints a report for the given case number. If the network is in training mode, it prints the
 * input case, the expected output, and the actual output. If the network is in run mode, it just
 * prints the input case and the output. Does not run the network.
 *
 * @param num the case number to report
 */
   public static void reportSingleCase(int num)
   {
      int n;
      if (config.networkMode == TRAINING)
      {
         n = INPUT_LAYER;
         System.out.print("Input Case #" + num + ": " + Arrays.toString(a[n]) + "     Expected:" +
               " " +
               Arrays.toString(truthTableOutputs[num]));

         n = OUTPUT_LAYER;
         System.out.println("     Output: " + formatDoubleArray(a[n],
               config.numOutAct, config.decimalPrecision));
      }
      else
      {
         n = INPUT_LAYER;
         System.out.print("Input Case #" + num + ": " + Arrays.toString(a[n]));

         n = OUTPUT_LAYER;
         System.out.println("     Output:" +
            " " +
               formatDoubleArray(a[n], config.numOutAct, config.decimalPrecision));
      }
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
      int i;
      int caseIter;
      double omegaJ;
      double omegaK;
      double psiK;

      System.out.println("Training...");

/**
 * Each iteration is defined as each execution of the body of the while loop.
 */
      while (trainIterations < config.maxIters && error > config.errThreshold)
      {
         if (trainIterations % config.keepAliveInterval == 0)
         {
            System.out.println("Reached training iteration " + trainIterations + " with error " + error);
         }

         error = 0.0;

         for (caseIter = 0; caseIter < config.numCases; caseIter++)
         {
            n = INPUT_LAYER;
            a[n] = truthTableInputs[caseIter];
            runDuringTrain(caseIter);

            n = HIDDEN_LAYER2;
            for (j = 0; j < config.numHidAct2; j++)
            {
               omegaJ = 0.0;
               for (i = 0; i < config.numOutAct; i++)
               {
                  omegaJ += psiI[i] * w[n][j][i];
                  w[n][j][i] += config.lambda * a[n][j] * psiI[i];
               }

               psiJ[j] = omegaJ * activationFunctionPrime(theta[n][j]);
            } //for (j = 0; j < config.numHidAct; j++)


            for (k = 0; k < config.numHidAct1; k++)
            {
               n = HIDDEN_LAYER1;
               omegaK = 0.0;
               for (j = 0; j < config.numHidAct2; j++)
               {
                  omegaK += psiJ[j] * w[n][k][j];
                  w[n][k][j] += config.lambda * a[n][k] * psiJ[j];
               }

               n = INPUT_LAYER;
               psiK = omegaK * activationFunctionPrime(theta[n + 1][k]);
               for (m = 0; m < config.numInAct; m++)
               {
                  w[n][m][k] += config.lambda * a[n][m] * psiK;
               }
            } //for (j = 0; j < config.numHidAct; j++)

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
 * This method was modified by Akul Goyal to remove magic numbers.
 */
   public static void printTime(double seconds)
   {
      double minutes, hours, days, weeks;

      System.out.print("Elapsed time: ");

      if (seconds < 1.)
         System.out.printf("%g milliseconds", seconds * MILLIS_PER_SEC);
      else if (seconds < SEC_PER_MIN)
         System.out.printf("%g seconds", seconds);
      else
      {
         minutes = seconds / SEC_PER_MIN;

         if (minutes < MIN_PER_HR)
            System.out.printf("%g minutes", minutes);
         else
         {
            hours = minutes / MIN_PER_HR;

            if (hours < HR_PER_DAY)
               System.out.printf("%g hours", hours);
            else
            {
               days = hours / HR_PER_DAY;

               if (days < DAYS_PER_WK)
                  System.out.printf("%g days", days);
               else
               {
                  weeks = days / DAYS_PER_WK;

                  System.out.printf("%g weeks", weeks);
               }
            } //if (hours < HR_PER_DAY)...else
         } //if (minutes < MIN_PER_HR)...else
      } //else if (seconds < SEC_PER_MIN)...else

      System.out.print("\n\n");
   } //public static void printTime(double seconds)

/**
 * Sets the configuration parameters, echos the network's settings, allocates memory for all
 * arrays and variables, populates all the arrays, loading the weights from file depending on
 * the loadWeights boolean and loading the truth table from file. Either trains or runs the
 * network depending on the network mode configuration. Then saves the weights to file depending
 * on the saveWeights configuration, and prints a final report.
 *
 * @param args the command line arguments
 */
   public static void main(String[] args)
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

      if (config.saveWeights)
      {
         weightsFileIO.saveWeights(w);
      }

      reportFull();
   } //public static void main(String[] args)
} //public class Main