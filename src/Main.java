import java.util.Arrays;

/**
 * This class is an A-B-C feedforward neural network. The network is fully connected and supports
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
   public static ConfigFileIO configFileIO;
   public static WeightsFileIO weightsFileIO;
   public static TruthTableFileIO truthTableFileIO;
   public static Config config;
   public static double initTime;                          //Time at the start of the program

/**
 * Variables used during both running and training modes
 */
   public static double[] a;                             //Array of input activation nodes
   public static double[] h;                             //Array of hidden activation nodes
   public static double[] F;                             //Output activation nodes
   public static double[][] kjWeights;                   //Weights between input and hidden layer
   public static double[][] jiWeights;                   //Weights between hidden and output layer
   public static int trainIterations;                    //Number of iterations done during training
   public static double[][] truthTableInputs;
   public static double[][] truthTableOutputs;

/**
 * Variables used to calculate the delta weights during training mode only
 */
   public static double[] thetaJ;
   public static double thetaI;
   public static double omegaI;
   public static double Ti;
   public static double[] psiI;
   public static double deltaWji;
   public static double deltaWkj;
   public static double omegaJ;
   public static double psiJ;
   public static double error;

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
         weightsFileIO = new WeightsFileIO(config.numInAct, config.numHidAct, config.numOutAct,
               config.weightsFile);
      }
   } //public static void setConfig(String[] args)

/**
 * Prints the configuration parameters.
 */
   public static void echoConfig()
   {
      System.out.println("=======================================================================");
      System.out.println("=======================================================================");
      System.out.println("Network configuration: " + config.numInAct + "-" + config.numHidAct +
            "-" + config.numOutAct);

      if (config.networkMode == TRAINING)
      {
         System.out.println("Network is in training mode.");
         System.out.println("Number of training cases: " + config.numCases);
         System.out.println("Max training iterations: " + config.maxIters);
         System.out.println("Lambda value: " + config.lambda);
         System.out.println("Error threshold: " + config.errThreshold);
      } //if (config.networkMode == TRAINING)
      else
      {
         System.out.println("Network is in run mode.");
      }

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

      System.out.println("-----------------------------------------------------------------------");
   } //public static void echoConfig

/**
 * Initializes the activation, weights, and truth table arrays. Allocates space on the heap for the
 * arrays and variables used.
 */
   public static void allocateMemory()
   {
/**
 * Allocates the following memory only if the network is in training mode.
 */
      if (config.networkMode == TRAINING)
      {
         psiI = new double[config.numOutAct];
         error = Double.MAX_VALUE;
         thetaJ = new double[config.numHidAct];
      } //if (config.networkMode == TRAINING)

/**
 * The following memory is always allocated.
 */
      a = new double[config.numInAct];
      h = new double[config.numHidAct];
      F = new double[config.numOutAct];
      kjWeights = new double[config.numInAct][config.numHidAct];
      jiWeights = new double[config.numHidAct][config.numOutAct];
      truthTableInputs = new double[config.numCases][config.numInAct];
      truthTableOutputs = new double[config.numCases][config.numOutAct];
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
      truthTableFileIO.loadTruthTable(truthTableInputs, truthTableOutputs);
      if (config.networkMode != TRAINING)
      {
         if (config.networkMode == RUN_ALL)
         {
            a = truthTableInputs[0];
         }
         else
         {
            a = truthTableInputs[config.runCaseNum];
         }
      } //if (config.networkMode != TRAINING)

      if (config.loadWeights)
      {
         weightsFileIO.loadWeights(kjWeights, jiWeights);
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
      int k;
      int j;
      int i;

/**
 * Populates kjWeights with random weights
 */
      for (k = 0; k < config.numInAct; k++)
      {
         for (j = 0; j < config.numHidAct; j++)
         {
            kjWeights[k][j] = randomize(config.lowRand, config.highRand);
         } //for (j = 0; j < config.numHidAct; j++)
      } //for (k = 0; k < config.numInAct; k++)

/**
 * Populates jiWeights with random weights
 */
      for (j = 0; j < config.numHidAct; j++)
      {
         for (i = 0; i < config.numOutAct; i++)
         {
            jiWeights[j][i] = randomize(config.lowRand, config.highRand);
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
      int j;
      int k;
      int i;
      double thetaAccumulator;

/**
 * Computes the hidden layer.
 */
      for (j = 0; j < config.numHidAct; j++)
      {
         thetaAccumulator = 0.0;
         for (k = 0; k < config.numInAct; k++)
         {
            thetaAccumulator += a[k] * kjWeights[k][j];
         }
         h[j] = activationFunction(thetaAccumulator);
      } //for (j = 0; j < config.numHidAct; j++)

/**
 * Computes the output layer.
 */
      for (i = 0; i < config.numOutAct; i++)
      {
         thetaAccumulator = 0.0;
         for (j = 0; j < config.numHidAct; j++)
         {
            thetaAccumulator += h[j] * jiWeights[j][i];
         }
         F[i] = activationFunction(thetaAccumulator);
      } //for (i = 0; i < config.numOutAct; i++)
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
            a = truthTableInputs[caseIter];
            runSingleCase();
         }
      } //if (config.networkMode == RUN_ALL || config.networkMode == TRAINING)
      else
      {
         a = truthTableInputs[config.runCaseNum];
         runSingleCase();
      }
   } //public static void runAll()

/**
 * Runs the network the same as the runSingleCase() method, but also saves the theta values for
 * the hidden layer and saves the psi values for the output layer.
 */
   public static void runDuringTrain(int caseNum)
   {
      int j;
      int k;
      int i;

/**
 * Computes and saves the theta values for the hidden layer.
 */
      for (j = 0; j < config.numHidAct; j++)
      {
         thetaJ[j] = 0.0;
         for (k = 0; k < config.numInAct; k++)
         {
            thetaJ[j] += a[k] * kjWeights[k][j];
         }
         h[j] = activationFunction(thetaJ[j]);
      } //for (j = 0; j < config.numHidAct; j++)

/**
 * Computes the output layer and the psi values for the output layer. Does not save the theta
 * values.
 */
      for (i = 0; i < config.numOutAct; i++)
      {
         thetaI = 0.0;
         for (j = 0; j < config.numHidAct; j++)
         {
            thetaI += h[j] * jiWeights[j][i];
         }
         F[i] = activationFunction(thetaI);

         Ti = truthTableOutputs[caseNum][i];
         omegaI = Ti - F[i];
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
 * Calculates the error for the given case number by first running the network on the given truth
 * table case. The error is half the sum of the squares of the differences between the expected
 * output and the actual output.
 *
 * @param caseNum the case number to calculate the error for
 * @return the average error
 */
   public static double runError(int caseNum)
   {
      int i;
      double avgErrorAccumulator;

      a = truthTableInputs[caseNum];
      runSingleCase();

      avgErrorAccumulator = 0.0;
      for (i = 0; i < config.numOutAct; i++)
      {
         Ti = truthTableOutputs[caseNum][i];
         avgErrorAccumulator += (Ti - F[i]) * (Ti - F[i]);
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
      int caseIter;

      System.out.println("-----------------------------------------------------------------------");

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
            a = truthTableInputs[caseIter];
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
 * Prints a report for the given case number. If the network is in training mode, it prints the
 * input case, the expected output, and the actual output. If the network is in run mode, it just
 * prints the input case and the output. Does not run the network.
 *
 * @param num the case number to report
 */
   public static void reportSingleCase(int num)
   {
      if (config.networkMode == TRAINING)
      {
         System.out.println("Input Case #" + num + ": " + Arrays.toString(a) + "     Expected: "
               + Arrays.toString(truthTableOutputs[num]) + "     " + "Output: " +
               Arrays.toString(F));
      }
      else
      {
         System.out.println("Input Case #" + num + ": " + Arrays.toString(a) + "     " +
               "Output: " + Arrays.toString(F));
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
      int j;
      int k;
      int i;
      int caseIter;

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
            a = truthTableInputs[caseIter];
            runDuringTrain(caseIter);

            for (j = 0; j < config.numHidAct; j++)
            {
               omegaJ = 0.0;
               for (i = 0; i < config.numOutAct; i++)
               {
                  omegaJ += psiI[i] * jiWeights[j][i];
                  deltaWji = config.lambda * h[j] * psiI[i];
                  jiWeights[j][i] += deltaWji;
               }

               psiJ = omegaJ * activationFunctionPrime(thetaJ[j]);

               for (k = 0; k < config.numInAct; k++)
               {
                  deltaWkj = config.lambda * a[k] * psiJ;
                  kjWeights[k][j] += deltaWkj;
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

      System.out.printf("Elapsed time: ");

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

      System.out.printf("\n\n");
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
         weightsFileIO.saveWeights(kjWeights, jiWeights);
      }

      reportFull();
   } //public static void main(String[] args)
} //public class Main