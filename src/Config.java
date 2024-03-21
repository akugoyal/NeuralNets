public class Config
{
   public int numLayers;
   public int numInAct;
   public int numHidAct;
   public int numOutAct;
   public double lowRand;                         //Lower bound for random number range
   public double highRand;                        //Upper bound for random number range
   public boolean loadWeights;                    //Whether to randomize the weights
   public boolean saveWeights;                    //Whether to save the weights
   public String weightsFile;                     //File containing weights for the network
   public int networkMode;                         //0 if the network is training, 1 is the
   // network is running all cases in the truth table, 2 if the network is running only one case
   // in the truth table
   public int numCases;
   public int maxIters;
   public double lambda;
   public double errThreshold;
   public String truthTableFile;
   public int runCaseNum;
   public int keepAliveInterval;

   public Config(String defaultWeightsFile, String defaultTruthTableFile) {
      numLayers = 3;
      numInAct = 1;
      numHidAct = 1;
      numOutAct = 1;
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
   }
}
