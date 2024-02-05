import java.util.*;

/**
 * A-B-1
 */

public class Main
{
   private static final int MAX_ITERATIONS = 100000;
   private static final double LAMBDA = 0.3;
   private static final double ERROR_THRESHOLD = 2.0e-4;
   private static int numInAct;
   private static int numHidAct;
   private static int numOutAct;
   private static double lowRand;
   private static double highRand;
   private static boolean randomizeWeights;
   private static boolean isTraining;
   private static int numTrainingCases;
   private static double[] a;
   private static double[] thetaJ;
   private static double[] h;
   private static double theta0;
   private static double F0;
   private static double[][] kjWeights;
   private static double[] j0Weights;
   private static int j;
   private static int k;


   private static int xIter;


   private static int trainIterations;
   private static double[][] truthTable;
   private static double omega0;
   private static double psi0;
   private static double ePrimeWj0;
   private static double[] deltaWj0;
   private static double omegaJ;
   private static double psiJ;
   private static double ePrimeWkj;
   private static double[][] deltaWkj;
   private static double sigValue;
   private static double avgErrorAccumulator;
   private static double thetaAccumulator;


   /**
    * Initializes the configuration parameters: the number of nodes in the activation layer, the
    * number of nodes in the hidden layer, and the low and high bounds of the random number
    * range, whether to randomize the weights, and whether to train or run the network. The user
    * changes these values to modify the behavior of the network.
    */
   public static void setConfig()
   {
      //Configuration parameters for the user to modify
      numInAct = 2;
      numHidAct = 1;
      lowRand = -1.5;
      highRand = 1.5;
      randomizeWeights = true;
      isTraining = true;

      //The numTrainingCases parameter is only needed when the network is running in train mode
      if (isTraining)
      {
         numTrainingCases = 4;
      }

      //The user should not change the following parameter
      numOutAct = 1;

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
         System.out.println("Max training iterations: " + MAX_ITERATIONS);
         System.out.println("Lambda value: " + LAMBDA);
         System.out.println("Error threshold: " + ERROR_THRESHOLD);
         System.out.println("Network is in training mode.");
      } //if (isTraining)
      else
      {
         System.out.println("Network is in run mode.");
      }

      if (randomizeWeights)
      {
         System.out.println("Randomizing weights with range: " + lowRand + " to " + highRand);
      }
      else
      {
         System.out.println("Loading weights from array.");
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
         truthTable = new double[numTrainingCases][numInAct + 1];
         omega0 = Double.MAX_VALUE;
         psi0 = 0.0;
         ePrimeWj0 = 0.0;
         deltaWj0 = new double[numHidAct];
         omegaJ = 0.0;
         psiJ = 0.0;
         ePrimeWkj = 0.0;
         deltaWkj = new double[numInAct][numHidAct];
         trainIterations = 0;
         sigValue = 0;
         avgErrorAccumulator = 0.0;
         thetaJ = new double[numHidAct];
         theta0 = 0.0;
      } //if (isTraining)

      //The following memory is always allocated
      a = new double[numInAct];
      thetaAccumulator = 0.0;
      h = new double[numHidAct];
      F0 = 0.0;
      kjWeights = new double[numInAct][numHidAct];
      j0Weights = new double[numHidAct];
      j = 0;
      k = 0;
      xIter = 0;
   } //public static void allocateArrays()

   /**
    * Populates the weights manually or randomly, depending on the value of the randomizeWeights
    * boolean.
    * Also manually populates the array of activation nodes or the truth table depending on
    * whether the network is training.
    */
   public static void populateArrays()
   {
      //If the network is training, the user should populate the truth table with values.
      if (isTraining)
      {
         truthTable[0][0] = 0.0;    //Test Case #1, Input #1
         truthTable[0][1] = 0.0;    //Test Case #1, Input #2
         truthTable[0][2] = 0.0;    //Test Case #1, Output

         truthTable[1][0] = 0.0;    //Test Case #2, Input #1
         truthTable[1][1] = 1.0;    //Test Case #2, Input #2
         truthTable[1][2] = 1.0;    //Test Case #2, Output

         truthTable[2][0] = 1.0;    //Test Case #3, Input #1
         truthTable[2][1] = 0.0;    //Test Case #3, Input #2
         truthTable[2][2] = 1.0;    //Test Case #3, Output

         truthTable[3][0] = 1.0;    //Test Case #4, Input #1
         truthTable[3][1] = 1.0;    //Test Case #4, Input #2
         truthTable[3][2] = 0.0;    //Test Case #4, Output
      }  //if (isTraining)
      //If the network is in run mode, the user should populate the input activation array
      else
      {
         a = new double[]{0, 0};   //Inputs #1 and #2
      }

      if (randomizeWeights)
      {
         //Populates kjWeights with random weights
         for (k = 0; k < numInAct; k++)
         {
            for (j = 0; j < numHidAct; j++)
            {
               kjWeights[k][j] = randomize(lowRand, highRand);
            } //for (j = 0; j < numHidAct; j++)
         } //for (k = 0; k < numInAct; k++)

         //Populates j0Weights with random weights
         for (j = 0; j < numHidAct; j++)
         {
            j0Weights[j] = randomize(lowRand, highRand);
         } //for (j = 0; j < numHidAct; j++)
      } //if (randomizeWeights)
      else
      {
         kjWeights[0][0] = -1;
         kjWeights[0][1] = 0.66;
         kjWeights[1][0] = -0.14;
         kjWeights[1][1] = -0.2;
         j0Weights[0] = -0.98;
         j0Weights[1] = -0.98;
      }
   } //public static void populateArrays()

   /**
    * Uses Math.random() to return a random number between low and high.
    *
    * @param low  the lower bound for the random number range
    * @param high the higher bound for the random number range
    * @return a random double between
    */
   public static double randomize(double low, double high)
   {
      return (Math.random() * (low - high)) - low;
   } //public static double randomize(double low, double high)

   /**
    * Runs the network on the input given by the input activations array, a. Each hidden node is the
    * sigmoid of the sum of the dot products of each of the previous activations and the
    * corresponding weight between the previous activation and the current activation. This
    * method does not save the theta values because they are not needed after the network runs.
    */
   public static void run()
   {
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

      //Computes the theta value for the output layer
      thetaAccumulator = 0.0;
      for (j = 0; j < numHidAct; j++)
      {
         thetaAccumulator += h[j] * j0Weights[j];
      }

      //Computes the final output
      F0 = sigmoid(thetaAccumulator);
   } //public static void run()

   /**
    * Runs the network on the input given by the input activations array, a. Each hidden node is the
    * sigmoid of the sum of the dot products of each of the previous activations and the
    * corresponding weight between the previous activation and the current activation. This
    * method saves the theta values which are used when training.
    */
   public static void runDuringTrain()
   {
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

      //Computes and saves the theta value for the output layer
      theta0 = 0.0;
      for (j = 0; j < numHidAct; j++)
      {
         theta0 += h[j] * j0Weights[j];
      }

      //Computes the final output
      F0 = sigmoid(theta0);
   } //public static void runDuringTrain()

   /**
    * Computes the sigmoid of the passed double using Math.exp() to compute the exponent of e.
    */
   public static double sigmoid(double x)
   {
      return 1.0 / (1 + Math.exp(-x));
   } //public static double sigmoid(double in)

   /**
    * Computes the derivative of the sigmoid at the passed double using the previously
    * defined sigmoid() function.
    */
   public static double sigmoidPrime(double x)
   {
      sigValue = sigmoid(x);
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
      avgErrorAccumulator = 0.0;
      for (xIter = 0; xIter < numTrainingCases; xIter++)
      {
         for (k = 0; k < numInAct; k++)
         {
            a[k] = truthTable[xIter][k];
         }
         run();
         omega0 = (truthTable[xIter][numInAct] - F0);
         avgErrorAccumulator += 0.5 * omega0 * omega0;
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
      if (isTraining)
      {
         if (trainIterations >= MAX_ITERATIONS)
         {
            System.out.println("Ended training due to max iterations reached.");
         } //if (trainIterations >= MAX_ITERATIONS)
         else
         {
            System.out.println("Ended training due to reaching error threshold.");
         }
         System.out.println("Reached " + trainIterations + " iterations.");
         System.out.println("Reached " + avgError() + " average error.");

         for (xIter = 0; xIter < numTrainingCases; xIter++)
         {
            for (k = 0; k < numInAct; k++)
            {
               a[k] = truthTable[xIter][k];
            } //for (k = 0; k < numInAct; k++) {
            run();
            System.out.println("Input Case #" + xIter + ": " + Arrays.toString(a) +
                  "     Expected: " + truthTable[xIter][numInAct] + "     Output: " + F0);
         } //for (xIter = 0; xIter < numTrainingCases; xIter++)
      } //if (isTraining)
      else
      {
         System.out.println("Network output: " + F0);
      }
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
      System.out.println("Training...");
      //Each iteration is defined as each execution of the body of the while loop
      while (trainIterations < MAX_ITERATIONS && avgError() > ERROR_THRESHOLD)
      {
         for (xIter = 0; xIter < numTrainingCases; xIter++)
         {
            for (k = 0; k < numInAct; k++)
            {
               a[k] = truthTable[xIter][k];
            } //for (k = 0; k < numInAct; k++)
            runDuringTrain();

            omega0 = truthTable[xIter][numInAct] - F0;
            psi0 = omega0 * sigmoidPrime(theta0);

            for (j = 0; j < numHidAct; j++)
            {
               ePrimeWj0 = -h[j] * psi0;
               deltaWj0[j] = -LAMBDA * ePrimeWj0;
            } //for (j = 0; j < numHidAct; j++)

            for (j = 0; j < numHidAct; j++)
            {
               omegaJ = psi0 * j0Weights[j];
               psiJ = omegaJ * sigmoidPrime(thetaJ[j]);

               for (k = 0; k < numInAct; k++)
               {
                  ePrimeWkj = -a[k] * psiJ;
                  deltaWkj[k][j] = -LAMBDA * ePrimeWkj;
               } //for (k = 0; k < numInAct; k++)
            } //for (j = 0; j < numHidAct; j++)

            for (j = 0; j < numHidAct; j++)
            {
               j0Weights[j] += deltaWj0[j];
            }
            for (j = 0; j < numHidAct; j++)
            {
               for (k = 0; k < numInAct; k++)
               {
                  kjWeights[k][j] += deltaWkj[k][j];
               }
            }
         } //for (xIter = 0; xIter < numTrainingCases; xIter++)
         trainIterations++;
      } //while (trainIterations < MAX_ITERATIONS && avgError() > ERROR_THRESHOLD)
   }

   /**
    * Sets the configuration parameters, echos the network's settings, allocates memory for all
    * arrays and variables, populates all the arrays, either trains or runs the network depending
    * on the isTraining boolean, and prints a final report.
    *
    * @param args the command line arguments
    */
   public static void main(String[] args)
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

      report();
   }
}