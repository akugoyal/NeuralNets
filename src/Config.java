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
   public boolean isTraining;                     //Whether the network is in training mode
   public int numTrainingCases;
   public double maxIters;
   public double lambda;
   public double errThreshold;
   public String truthTableFile;

   public Config() {
      weightsFile = "weights.bin";
      truthTableFile = "AND.txt";
   }
}
