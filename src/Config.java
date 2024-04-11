/**
 * This class represents the configuration of the A-B-C-D neural network. It stores the following
 * parameters:
 *
 * INPUT_LAYER:       the index of the input layer in the network. This is always 0.
 * HIDDEN_LAYER1:     the index of the first hidden layer in the network. This is always 1.
 * HIDDEN_LAYER2:     the index of the second hidden layer in the network. This is always 2.
 * OUTPUT_LAYER:      the index of the output layer in the network. This is always 3.
 * numLayers:         the number of layers in the network. This is always 4.
 * numActsInLayers:   the number of activations in each layer. This defaults to 1 for each layer.
 * lowRand:           the lower bound for the random number range, which may be used to randomize
 *                    the weights. This defaults to 0.0.
 * highRand:          the upper bound for the random number range, which may be used to randomize
 *                    the weights. This defaults to 0.0.
 * loadWeights:       whether to load the weights from a file. This defaults to false.
 * saveWeights:       whether to save the weights to a file. This defaults to false.
 * weightsFile:       the file to load/save the weights from/to. This defaults to the value
 *                    passed in the constructor.
 * networkMode:       0 if the network is training, 1 if the network is running all cases in the
 *                    truth table, 2 if the network is running only one case in the truth table.
 *                    This defaults to 0.
 * numCases:          the number of cases in the truth table. This defaults to 1.
 * maxIters:          the maximum number of iterations for training. This defaults to 0.
 * lambda:            the learning rate. This defaults to 0.0.
 * errThreshold:      the error threshold to end under for training. This defaults to 0.0.
 * truthTableFile:    the file containing the truth table. This defaults to the value passed in the
 *                    constructor.
 * runCaseNum:        the case number to run in the truth table. Used only when networkMode is 2
 *                    (Run single case). This defaults to 0.
 * keepAliveInterval: the interval to print status updates to the console during training. This
 *                    defaults to 0.
 * decimalPrecision:  the number of decimal places to round the weights to. This defaults to 17.
 *
 *
 * Table of Contents:
 * 1. Config(String defaultWeightsFile, String defaultTruthTableFile)
 *
 * Author: Akul Goyal
 * Date of Creation: 03/19/2024
 */

public class Config
{
   public int numLayers;
   public int INPUT_LAYER = 0;
   public int FIRST_HIDDEN_LAYER = 1;
   public int OUTPUT_LAYER;
   public int LAST_HIDDEN_LAYER;
   public int[] numActsInLayers;
   public double lowRand;
   public double highRand;
   public boolean loadWeights;
   public boolean saveWeights;
   public String weightsFile;
   public int networkMode;
   public int numCases;
   public int maxIters;
   public double lambda;
   public double errThreshold;
   public String truthTableFile;
   public int runCaseNum;
   public int keepAliveInterval;
   public int decimalPrecision;

   /**
    * Constructor for the Config class. Initializes the parameters to their default values.
    *
    * @param defaultWeightsFile    the default file to load/save the weights from/to
    * @param defaultTruthTableFile the default file containing the truth table
    */
   public Config(String defaultWeightsFile, String defaultTruthTableFile)
   {
      numActsInLayers = new int[0];
      lowRand = 0.0;
      highRand = 0.0;
      loadWeights = false;
      saveWeights = false;
      weightsFile = defaultWeightsFile;
      networkMode = 0;
      numCases = 1;
      maxIters = 0;
      lambda = 0.0;
      errThreshold = 0.0;
      truthTableFile = defaultTruthTableFile;
      runCaseNum = 0;
      keepAliveInterval = 0;
      decimalPrecision = 17;
   } //public Config(String defaultWeightsFile, String defaultTruthTableFile)
} //public class Config
