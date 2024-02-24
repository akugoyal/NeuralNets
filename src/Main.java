import java.util.Arrays;
import java.io.*;
import java.util.Scanner;

/**
 * This class is an A-B-C feedforward neural network. The network is fully connected and uses a
 * sigmoid activation function. The network can be run in training mode or run mode. In training
 * mode, the network uses a gradient descent algorithm to calculate the delta weights, until
 * either the maximum number of iterations is reached or the average error is under the error
 * threshold. In run mode, the network simply runs the input through the network and prints the
 * value of the output node(s).
 * <p>
 * The user can modify the following configuration parameters to change the behavior of the
 * network.
 * numInAct: the number of input activation nodes
 * numHidAct: the number of hidden activation nodes
 * numOutAct: the number of output activation nodes
 * lowRand: the lower bound for the random number range
 * highRand: the upper bound for the random number range
 * randomizeWeights: whether to randomize the weights. If randomizeWeights is true, the weights
 * will be randomized using the Math.random() function. If randomizeWeights is
 * false, the weights will be set manually in the populateArrays() method.
 * isTraining: whether the network is in training or run mode. If the network is in training
 * mode, the user should set the number of training cases in setConfig() and manually
 * populate the truth table in populateArrays() with the input and output values. If
 * the network is in run mode, the user should manually populate the input activation
 * array, a, in populateArrays().
 * maxIters: the maximum number of training iterations to run before ending training mode
 * lambda: the lambda value used to scale delta weights during training mode
 * errThreshold: the error threshold used to determine when to end training
 * The user should not modify the numOutAct parameter, which is set to 1.
 * <p>
 * Author: Akul Goyal
 * Date of Creation: 01/30/2024
 */

public class Main
{
   /**
    * Variables for the printTime() method
    */
   public static final double NANO_PER_SEC = 1.0e9;        //Nanoseconds per second
   public static final double MILLIS_PER_SEC = 1000.0;     //Milliseconds per second
   public static final double SEC_PER_MIN = 60.0;          //Seconds per minute
   public static final double MIN_PER_HR = 60.0;           //Minutes per hour
   public static final double HR_PER_DAY = 24.0;           //Hours per day
   public static final double DAYS_PER_WK = 7.0;           //Days per week
   public static double initTime;                          //Time at the start of the program

   /**
    * Variables used during both running and training modes
    */
   public static int numLayers;                          //Number of activation layers in the network
   public static int numInAct;                           //Number of input activation nodes
   public static int numHidAct;                          //Number of hidden activation nodes
   public static int numOutAct;                          //Number of output activation nodes
   public static double lowRand;                         //Lower bound for random number range
   public static double highRand;                        //Upper bound for random number range
   public static boolean loadWeights;                    //Whether to randomize the weights
   public static boolean saveWeights;                    //Whether to save the weights
   public static String weightsFile;                     //File containing weights for the network
   public static boolean isTraining;                     //Whether the network is in training mode
   public static double[] a;                             //Array of input activation nodes
   public static double[] h;                             //Array of hidden activation nodes
   public static double[] F;                             //Output activation nodes
   public static double[][] kjWeights;                   //Weights between input and hidden layer
   public static double[][] jiWeights;                   //Weights between hidden and output layer

   /**
    * Variables used during training mode only
    */
   public static final int MAX_ITERATIONS = 100000;
   public static final double LAMBDA = 0.3;
   public static final double ERROR_THRESHOLD = 2.0e-4;
   public static double maxIters;
   public static double lambda;
   public static double errThreshold;
   public static int numTrainingCases;
   public static int trainIterations;
   public static double[][] truthTableInputs;
   public static double[][] truthTableOutputs;
   public static double[] thetaJ;
   public static double[] thetaI;

   /**
    * Variables used to calculate the delta weights during training mode only
    */
   public static double[] omegaI;
   public static double[] psiI;
   public static double ePartialWji;
   public static double[][] deltaWji;
   public static double[][] deltaWkj;
   public static double omegaJ;
   public static double psiJ;
   public static double ePrimeWkj;

   /**
    * Initializes the configuration parameters: the number of nodes in the activation layer, the
    * number of nodes in the hidden layer, and the low and high bounds of the random number
    * range, whether to randomize the weights, and whether to train or run the network. The user
    * changes these values to modify the behavior of the network.
    */
   public static void setConfig()
   {
      //Record the time at the start of the program
      initTime = System.nanoTime();

      //The user should not modify the following parameter
      numLayers = 3;

      //Configuration parameters for the user to modify
      numInAct = 2;
      numHidAct = 5;
      numOutAct = 3;
      loadWeights = true;
      saveWeights = true;
      isTraining = false;

      //The following parameters are only used when the network is running in train mode
      if (isTraining)
      {
         maxIters = MAX_ITERATIONS;
         lambda = LAMBDA;
         errThreshold = ERROR_THRESHOLD;
         numTrainingCases = 4;
      }

      //The following parameters are only used when the network is loading weights from or saving
      // weights to a file
      if (loadWeights || saveWeights) {
         weightsFile = "weights.txt";
      }

      //The following parameters are only used when the network is not loading weights from a file
      if (!loadWeights) {
         lowRand = 0.1;
         highRand = 1.5;
      }
   } //public static void setConfig

   /**
    * Prints the configuration parameters.
    */
   public static void echoConfig()
   {
      System.out.println("Network configuration: " + numInAct + "-" + numHidAct + "-" + numOutAct);

      if (isTraining)
      {
         System.out.println("Number of training cases: " + numTrainingCases);
         System.out.println("Max training iterations: " + maxIters);
         System.out.println("Lambda value: " + lambda);
         System.out.println("Error threshold: " + errThreshold);
         System.out.println("Network is in training mode.");
      } //if (isTraining)
      else
      {
         System.out.println("Network is in run mode.");
      } //if (isTraining)

      if (loadWeights)
      {
         System.out.println("Loading weights from file: " + weightsFile);
      }
      else
      {
         System.out.println("Randomizing weights with range: " + lowRand + " to " + highRand);
      } //if (loadWeights)

      if (saveWeights) {
         System.out.println("Saving weights to file: " + weightsFile);
      }
   } //public static void echoConfig

   /**
    * Initializes the activation and weights arrays. Allocates space on the heap for the arrays
    * and variables used.
    */
   public static void allocateMemory()
   {
      //Allocates the following memory only if the network is in training mode
      if (isTraining)
      {
         truthTableInputs = new double[numTrainingCases][numInAct];
         truthTableOutputs = new double[numTrainingCases][numOutAct];
         omegaI = new double[numOutAct];
         psiI = new double[numOutAct];
         ePartialWji = 0.0;
         deltaWji = new double[numHidAct][numOutAct];
         omegaJ = 0.0;
         psiJ = 0.0;
         ePrimeWkj = 0.0;
         deltaWkj = new double[numInAct][numHidAct];
         trainIterations = 0;
         thetaJ = new double[numHidAct];
         thetaI = new double[numOutAct];
      } //if (isTraining)

      //The following memory is always allocated
      a = new double[numInAct];
      h = new double[numHidAct];
      F = new double[numOutAct];
      kjWeights = new double[numInAct][numHidAct];
      jiWeights = new double[numHidAct][numOutAct];
   } //public static void allocateArrays()

   public static void saveWeights(String fileName) throws IOException
   {
      int i;
      int j;
      int k;
      BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
      writer.write(Integer.toString(numInAct) + " " + Integer.toString(numHidAct) + " " + Integer.toString(numOutAct) + "\n");

      for (k = 0; k < numInAct; k++) {
         for (j = 0; j < numHidAct - 1; j++) {
            writer.write(Double.toString(kjWeights[k][j]) + " ");
         }
         writer.write(Double.toString(kjWeights[k][numHidAct - 1]) + "\n");
      }
      for (j = 0; j < numHidAct; j++) {
         for (i = 0; i < numOutAct - 1; i++) {
            writer.write(Double.toString(jiWeights[j][i]) + " ");
         }
         writer.write(Double.toString(jiWeights[j][numOutAct - 1]) + "\n");
      }

      writer.flush();
      writer.close();
      System.out.println("Saved weights successfully");
   }

   public static void loadWeights(String fileName) throws IOException{
      int i;
      int j;
      int k;

      BufferedReader br = new BufferedReader(new FileReader(fileName));
      String[] lnRead = br.readLine().split(" ");
      int in = Integer.parseInt(lnRead[0]);
      int hid = Integer.parseInt(lnRead[1]);
      int out = Integer.parseInt(lnRead[2]);
      if (in != numInAct || hid != numHidAct || out != numOutAct) {
         System.out.println("Error: Network configuration mismatch. Weights configured for " + in + "-" + hid + "-" + out + ".");
         System.exit(1);
      }

      for (k = 0; k < numInAct; k++) {
         lnRead = br.readLine().split(" ");
         for (j = 0; j < numHidAct; j++) {
            kjWeights[k][j] = Integer.parseInt(lnRead[j]);
         }
      }

      for (j = 0; j < numHidAct; j++) {
         lnRead = br.readLine().split(" ");
         for (i = 0; i < numOutAct; i++) {
            jiWeights[j][i] = Integer.parseInt(lnRead[i]);
         }
      }

      System.out.println("Loaded weights successfully.");
   }

   /**
    * Uses Math.random() to return a random number between low and high.
    *
    * @param low  the lower bound for the random number range
    * @param high the higher bound for the random number range
    * @return a random double between
    */
   public static double randomize(double low, double high)
   {
      return (Math.random() * (high - low)) + low;
   } //public static double randomize(double low, double high)

   /**
    * Populates the weights manually or randomly, depending on the value of the randomizeWeights
    * boolean. Also, manually populates the array of activation nodes or the truth table depending on
    * whether the network is training.
    */
   public static void populateArrays() throws IOException
   {
      int j;
      int k;
      int i;

      //If the network is training, the user should populate the truth table with values.
      if (isTraining)
      {
         truthTableInputs[0][0] = 0.0;    //Test Case #1, Input #1
         truthTableInputs[0][1] = 0.0;    //Test Case #1, Input #2

         truthTableInputs[1][0] = 0.0;    //Test Case #2, Input #1
         truthTableInputs[1][1] = 1.0;    //Test Case #2, Input #2

         truthTableInputs[2][0] = 1.0;    //Test Case #3, Input #1
         truthTableInputs[2][1] = 0.0;    //Test Case #3, Input #2

         truthTableInputs[3][0] = 1.0;    //Test Case #4, Input #1
         truthTableInputs[3][1] = 1.0;    //Test Case #4, Input #2


         truthTableOutputs[0][0] = 0.0;   //Test Case #1, Output #1
         truthTableOutputs[0][1] = 0.0;   //Test Case #1, Output #2
         truthTableOutputs[0][2] = 0.0;   //Test Case #1, Output #3

         truthTableOutputs[1][0] = 0.0;   //Test Case #2, Output #1
         truthTableOutputs[1][1] = 1.0;   //Test Case #2, Output #2
         truthTableOutputs[1][2] = 1.0;   //Test Case #2, Output #3

         truthTableOutputs[2][0] = 0.0;   //Test Case #3, Output #1
         truthTableOutputs[2][1] = 1.0;   //Test Case #3, Output #2
         truthTableOutputs[2][2] = 1.0;   //Test Case #3, Output #3

         truthTableOutputs[3][0] = 1.0;   //Test Case #4, Output #1
         truthTableOutputs[3][1] = 1.0;   //Test Case #4, Output #2
         truthTableOutputs[3][2] = 0.0;   //Test Case #4, Output #3

//         truthTableInputs[0][0] = 0.0;    //Test Case #1, Input #1
//         truthTableInputs[0][1] = 0.0;    //Test Case #1, Input #2
//         truthTableOutputs[0][0] = 0.0;    //Test Case #1, Output
//
//         truthTableInputs[1][0] = 0.0;    //Test Case #2, Input #1
//         truthTableInputs[1][1] = 1.0;    //Test Case #2, Input #2
//         truthTableOutputs[1][0] = 1.0;    //Test Case #2, Output
//
//         truthTableInputs[2][0] = 1.0;    //Test Case #3, Input #1
//         truthTableInputs[2][1] = 0.0;    //Test Case #3, Input #2
//         truthTableOutputs[2][0] = 1.0;    //Test Case #3, Output
//
//         truthTableInputs[3][0] = 1.0;    //Test Case #4, Input #1
//         truthTableInputs[3][1] = 1.0;    //Test Case #4, Input #2
//         truthTableOutputs[3][0] = 0.0;    //Test Case #4, Output
      }  //if (isTraining)
      //If the network is in run mode, the user should populate the input activation array
      else
      {
         a = new double[] {0, 1};   //Inputs #1 and #2
      }  //if (isTraining)

      if (loadWeights)
      {
         /*
          * If the loadWeights boolean was set to true in setConfig(), the weights will be
          * loaded from file.
          */
         loadWeights(weightsFile);
      } //if (loadWeights)
      else
      {
         //Populates kjWeights with random weights
         for (k = 0; k < numInAct; k++)
         {
            for (j = 0; j < numHidAct; j++)
            {
               kjWeights[k][j] = randomize(lowRand, highRand);
            } //for (j = 0; j < numHidAct; j++)
         } //for (k = 0; k < numInAct; k++)

         //Populates jiWeights with random weights
         for (j = 0; j < numHidAct; j++)
         {
            for (i = 0; i < numOutAct; i++)
            {
               jiWeights[j][i] = randomize(lowRand, highRand);
            } //for (i = 0; i < numOutAct; i++)
         } //for (j = 0; j < numHidAct; j++)
      } //if (loadWeights)
   } //public static void populateArrays()

   /**
    * Runs the network on the input given by the input activations array, a. Each hidden node is the
    * sigmoid of the sum of the dot products of each of the previous activations and the
    * corresponding weight between the previous activation and the current activation. This
    * method does not save the theta values because they are not needed after the network runs.
    */
   public static void run()
   {
      int j;
      int k;
      int i;

      double thetaAccumulator = 0.0;
      //Computes the theta values for the hidden layer
      for (j = 0; j < numHidAct; j++)
      {
         thetaAccumulator = 0.0;
         for (k = 0; k < numInAct; k++)
         {
            thetaAccumulator += a[k] * kjWeights[k][j];
         } //for (k = 0; k < numInAct; k++)
         h[j] = sigmoid(thetaAccumulator);
      } //for (j = 0; j < numHidAct; j++)

      //Computes the output layer
      for (i = 0; i < numOutAct; i++)
      {
         //Computes the theta values for the output layer
         thetaAccumulator = 0.0;
         for (j = 0; j < numHidAct; j++)
         {
            thetaAccumulator += h[j] * jiWeights[j][i];
         }

         //Computes the final output
         F[i] = sigmoid(thetaAccumulator);
      }
   } //public static void run()

   /**
    * Runs the network on the input given by the input activations array, a. Each hidden node is the
    * sigmoid of the sum of the dot products of each of the previous activations and the
    * corresponding weight between the previous activation and the current activation. This
    * method saves the theta values which are used when training.
    */
   public static void runDuringTrain()
   {
      int j;
      int k;
      int i;

      //Computes and saves the theta values for the hidden layer
      for (j = 0; j < numHidAct; j++)
      {
         thetaJ[j] = 0.0;
         for (k = 0; k < numInAct; k++)
         {
            thetaJ[j] += a[k] * kjWeights[k][j];
         } //for (k = 0; k < numInAct; k++)
         h[j] = sigmoid(thetaJ[j]);
      } //for (j = 0; j < numHidAct; j++)

      //Computes and saves the values for the output layer
      for (i = 0; i < numOutAct; i++)
      {
         //Computes and saves the theta values for the output layer
         thetaI[i] = 0.0;
         for (j = 0; j < numHidAct; j++)
         {
            thetaI[i] += h[j] * jiWeights[j][i];
         }

         //Computes the final output
         F[i] = sigmoid(thetaI[i]);
      }
   } //public static void runDuringTrain()

   /**
    * Computes the sigmoid of the passed double using Math.exp() to compute the exponent of e.
    */
   public static double sigmoid(double x)
   {
      return 1.0 / (1.0 + Math.exp(-x));
   } //public static double sigmoid(double in)

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
    * Calculates the average error across all the test cases by running each one, accumulating
    * the error, and then dividing by the total number of test cases.
    *
    * @return the average error
    */
   public static double avgError()
   {
      int trainIter;
      int k;
      int i;

//      double errorAcc = 0.0;
      double avgErrorAccumulator = 0.0;
      for (trainIter = 0; trainIter < numTrainingCases; trainIter++)
      {
         //Run the network for the current training case
         for (k = 0; k < numInAct; k++)
         {
            a[k] = truthTableInputs[trainIter][k];
         }
         run();

         //Calculate error for each training case
//         errorAcc = 0.0;
         for (i = 0; i < numOutAct; i++)
         {
            avgErrorAccumulator += 0.5 * (truthTableOutputs[trainIter][i] - F[i]) *
                  (truthTableOutputs[trainIter][i] - F[i]);
         }
//         avgErrorAccumulator += errorAcc * 0.5;
      }
      return avgErrorAccumulator / numTrainingCases;
   } //public static double avgError()

   /**
    * Prints a report at the end of either training or running. For training, it prints the
    * reason for ending training, the number of iterations, the average error, and the truth
    * table with outputs.
    */
   public static void report()
   {
      int trainIter;
      int k;

      if (isTraining)
      {
         if (trainIterations >= maxIters)
         {
            System.out.println("Ended training due to max iterations reached.");
         } //if (trainIterations >= maxIters)
         else
         {
            System.out.println("Ended training due to reaching error threshold.");
         } //if (trainIterations >= maxIters)
         System.out.println("Reached " + trainIterations + " iterations.");
         System.out.println("Reached " + avgError() + " average error.");

         for (trainIter = 0; trainIter < numTrainingCases; trainIter++)
         {
            for (k = 0; k < numInAct; k++)
            {
               a[k] = truthTableInputs[trainIter][k];
            } //for (k = 0; k < numInAct; k++) {
            run();

            System.out.println("Input Case #" + trainIter + ": " + Arrays.toString(a) +
                  "     Expected: " + Arrays.toString(truthTableOutputs[trainIter]) + "     " +
                  "Output: " + Arrays.toString(F));
         } //for (xIter = 0; xIter < numTrainingCases; xIter++)
      } //if (isTraining)
      else
      {
         System.out.println("Network input: " + Arrays.toString(a));
         System.out.println("Network output: " + Arrays.toString(F));
      } //if (isTraining)

      //Prints the time elapsed
      printTime((System.nanoTime() - initTime) / NANO_PER_SEC);
   } //public static void report()

   /**
    * Trains the network using a gradient descent algorithm. The method trains until the max
    * number of iterations has been reached or the average error is under the error threshold.
    * For each iteration, every test case is run, the error is computed, and then the partial
    * derivatives are calculated. The lambda value is then multiplied by the partial derivatives
    * to compute the delta weights, which are applied for each test case.
    */
   public static void train()
   {
      int j;
      int k;
      int i;
      int trainIter;

      System.out.println("Training...");
      //Each iteration is defined as each execution of the body of the while loop
      while (trainIterations < maxIters && avgError() > errThreshold)
      {
         for (trainIter = 0; trainIter < numTrainingCases; trainIter++)
         {
            for (k = 0; k < numInAct; k++)
            {
               a[k] = truthTableInputs[trainIter][k];
            } //for (k = 0; k < numInAct; k++)
            runDuringTrain();

            for (i = 0; i < numOutAct; i++)
            {
               omegaI[i] = truthTableOutputs[trainIter][i] - F[i];
               psiI[i] = omegaI[i] * sigmoidPrime(thetaI[i]);

               for (j = 0; j < numHidAct; j++)
               {
                  ePartialWji = -h[j] * psiI[i];
                  deltaWji[j][i] = -lambda * ePartialWji;
               } //for (j = 0; j < numHidAct; j++)
            } //for (i = 0; i < numOutAct; i++) {

            for (j = 0; j < numHidAct; j++)
            {
               omegaJ = 0.0;
               for (i = 0; i < numOutAct; i++)
               {
                  omegaJ += psiI[i] * jiWeights[j][i];
               }

               psiJ = omegaJ * sigmoidPrime(thetaJ[j]);

               for (k = 0; k < numInAct; k++)
               {
                  ePrimeWkj = -a[k] * psiJ;
                  deltaWkj[k][j] = -lambda * ePrimeWkj;
               } //for (k = 0; k < numInAct; k++)
            } //for (j = 0; j < numHidAct; j++)

            for (j = 0; j < numHidAct; j++)
            {
               for (i = 0; i < numOutAct; i++)
               {
                  jiWeights[j][i] += deltaWji[j][i];
               }

               for (k = 0; k < numInAct; k++)
               {
                  kjWeights[k][j] += deltaWkj[k][j];
               }
            } //for (j = 0; j < numHidAct; j++)
         } //for (xIter = 0; xIter < numTrainingCases; xIter++)
         trainIterations++;
      } //while (trainIterations < maxIters && avgError() > errThreshold)
   } //public static void train()

   /**
    * Accept a value representing seconds elapsed and print out a decimal value in easier to
    * digest units.
    * <p>
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
            } // if (hours < hrPerDay)...else
         } // if (minutes < minPerHr)...else
      } // else if (seconds < secPerMin)...else

      System.out.printf("\n\n");
   } // public static void printTime(double seconds)

   /**
    * Sets the configuration parameters, echos the network's settings, allocates memory for all
    * arrays and variables, populates all the arrays, either trains or runs the network depending
    * on the isTraining boolean, and prints a final report.
    *
    * @param args the command line arguments
    */
   public static void main(String[] args) throws IOException
   {
      setConfig();
      echoConfig();
      allocateMemory();
      populateArrays();

      if (isTraining)
      {
         train();
      }
      else
      {
         run();
      }

      if (saveWeights) {
         saveWeights(weightsFile);
      }

      report();
   } //public static void main(String[] args)
} //public class Main