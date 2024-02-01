import java.util.*;

/**
 *
 */

public class Main
{
   private static final int MAX_ITERATIONS = 100000;
   private static final double LAMBDA = 0.3;
   private static final double ERROR_THRESHOLD = 2e-4;
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
   public static int i;
   private static int j;
   private static int k;


   private static int xIter;
   private static Scanner scanner = new Scanner(System.in);


   private static int trainIterations;
   private static double[][] truthTable;
   private static double omega0;
   private static double psi0;
   private static double ePrimeWj0;
   private static double deltaWj0;
   private static double omegaJ;
   private static double psiJ;
   private static double ePrimeWkj;
   private static double deltaWkj;



   /**
    * Initializes the configuration parameters. The user changes these values to modify the
    * behavior of the network.
    */
   public static void setConfig()
   {
      numInAct = 2;
      numHidAct = 2;
      numOutAct = 1;
      lowRand = -1.5;
      highRand = 1.5;
      randomizeWeights = false;
      isTraining = false;
      if (isTraining)
      {
         System.out.println("Number of training cases: ");
         numTrainingCases = scanner.nextInt();
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
         System.out.println("Max training iterations: " + MAX_ITERATIONS);
         System.out.println("Lambda value: " + LAMBDA);
         System.out.println("Error threshold: " + ERROR_THRESHOLD);
      }

      if (randomizeWeights)
      {
         System.out.println("Random number range: " + lowRand + " to " + highRand);
      }
      else
      {
         System.out.println("Loading weights from file");
      }
   } //public static void echoConfig

   /**
    * Initializes the activation and weights arrays. Allocates space on the heap for the arrays.
    */
   public static void allocateArrays()
   {
      if (isTraining)
      {
         truthTable = new double[numTrainingCases][numInAct + 1];
         omega0 = 0.0;
         psi0 = 0.0;
         ePrimeWj0 = 0.0;
         deltaWj0 = 0.0;
         omegaJ = 0.0;
         psiJ = 0.0;
         ePrimeWkj = 0.0;
         deltaWkj = 0.0;
         trainIterations = 0;
      }
      a = new double[numInAct];
      thetaJ = new double[numInAct];
      h = new double[numHidAct];
      theta0 = 0.0;
      F0 = 0.0;
      kjWeights = new double[numInAct][numHidAct];
      j0Weights = new double[numHidAct];
      i = 0;
      j = 0;
      k = 0;
      xIter = 0;
   } //public static void allocateArrays()

   /**
    * Populates the weights from file or randomly, depending on the value of the randomizeWeights
    * boolean.
    */
   public static void populateArrays()
   {
      if (isTraining)
      {
         for (xIter = 0; xIter < numTrainingCases; xIter++)
         {
            for (k = 0; k < numInAct; k++)
            {
               System.out.print("Training case #" + xIter + " - Input #" + k + ": ");
               truthTable[xIter][k] = scanner.nextDouble();
            } //for (yIter = 0; yIter < numInAct; yIter++)
            System.out.print("Training case #" + xIter + " - Output: ");
            truthTable[xIter][numInAct] = scanner.nextDouble();
         } //for (xIter = 0; xIter < numTrainingCases; xIter++)
      }  //if (isTraining)
      else
      {
         for (k = 0; k < numInAct; k++)
         {
            System.out.print("Input #" + k + ": ");
            a[k] = scanner.nextDouble();
         } //for (k = 0; k < numInAct; k++)
      }

      if (randomizeWeights)
      {
         for (k = 0; k < numInAct; k++)
         {         // populates kjWeights with random weights
            for (j = 0; j < numHidAct; j++)
            {
               kjWeights[k][j] = randomize(lowRand, highRand);
            }
         }
         for (j = 0; j < numHidAct; j++)
         {         // populates j0Weights with random weights
            j0Weights[j] = randomize(lowRand, highRand);
         }
      }
      else
      { //if (randomizeWeights)
         kjWeights[0][0] = -1;
         kjWeights[0][1] = 0.66;
         kjWeights[1][0] = -0.14;
         kjWeights[1][1] = -0.2;
         j0Weights[0] = -0.98;
         j0Weights[1] = -0.98;
         //         for (k = 0; k < numInAct; k++) {         // populates kjWeights with random weights
//            for (j = 0; j < numHidAct; j++) {
//               System.out.print("kjWeight " + k + " " + j + ": ");
//               kjWeights[k][j] = scanner.nextDouble();
//            }
//         }
//         for (j = 0; j < numHidAct; j++) {         // populates j0Weights with random weights
//            System.out.print("j0Weight " + j + " 0: ");
//            j0Weights[j] = scanner.nextDouble();
//         }
//         System.out.println("Loading weights from file is not yet supported. Setting weights " +
//               "manually instead.");

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
    * Runs the model on the input given by the input activations array, a. Each hidden node is the
    * sigmoid of the sum of the dot products of each of the previous activations and the
    * corresponding weight between the previous activation and the hidden activation.
    */
   public static void run()
   {
      for (j = 0; j < numHidAct; j++)
      {
         thetaJ[j] = 0;
         for (k = 0; k < numInAct; k++)
         {
            thetaJ[j] += a[k] * kjWeights[k][j];
         }
         h[j] = sigmoid(thetaJ[j]);
      }

      for (j = 0; j < numHidAct; j++)
      {
         theta0 += h[j] * j0Weights[j];
      }
      F0 = sigmoid(theta0);
   } //public static void run()

   public static double sigmoid(double in)
   {
      return 1.0 / (1 + Math.exp(-in));
   }

   public static void train() {
      while ()
      omega0 =
   }
   public static void main(String[] args)
   {
      setConfig();
      echoConfig();
      allocateArrays();
      populateArrays();

      if (isTraining)
      {
//         train();
//         System.out.println("Training is not supported yet. I think it would be a good idea to " +
//               "run the network instead.");
      }
      else
      {
         run();
         System.out.println("Model output: " + F0);
      }

//      report();
   }
}