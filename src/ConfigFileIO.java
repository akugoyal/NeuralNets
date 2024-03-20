import java.io.*;

public class ConfigFileIO
{
   private DataOutputStream out;
   private DataInputStream in;
   private String fileName;
   private String ln;
   private int lnNumber;
   private Config config;

   public ConfigFileIO(String fileName)
   {
      this.fileName = fileName;
      lnNumber = 1;
      config = new Config();
   }

   public ConfigFileIO(String fileName, Config config)
   {
      this(fileName);
      this.config = config;
   }

   public Config loadConfig()
   {
      String[] read;

      try
      {
         in = new DataInputStream(new FileInputStream(fileName));
      }
      catch (FileNotFoundException e)
      {
         Util.exit("Failed to open config file", fileName);
      }

      while (readLine())
      {
         if (ln.contains(":"))
         {
            read = ln.split(":");
            read[0] = read[0].trim().toLowerCase();
            read[1] = read[1].trim();

            switch (read[0])
            {
               case "network configuration":
                  parseNetworkConfig(read[1]);
                  break;
               case "is training":
                  config.isTraining = Util.toBoolean(read[1]);
                  break;
               case "number of cases":
                  try
                  {
                     config.numCases = Util.toInt(read[1]);
                  }
                  catch (NumberFormatException e)
                  {
                     Util.exit("Poorly formatted integer for Number of Training Cases: " + read[1], fileName);
                  }
               case "max training iterations":
                  try
                  {
                     config.maxIters = Util.toInt(read[1]);
                  }
                  catch (NumberFormatException e)
                  {
                     Util.exit("Poorly formatted integer for Max Training Iterations: " + read[1],
                           fileName);
                  }
               case "lambda":
                  try
                  {
                     config.lambda = Util.toDouble(read[1]);
                  }
                  catch (NumberFormatException e)
                  {
                     Util.exit("Poorly formatted double for lambda: " + read[1], fileName);
                  }
               case "error threshold":
                  try
                  {
                     config.errThreshold = Util.toDouble(read[1]);
                  }
                  catch (NumberFormatException e)
                  {
                     Util.exit("Poorly formatted double for error threshold: " + read[1], fileName);
                  }
               case "random range lower bound":
                  try
                  {
                     config.lowRand = Util.toDouble(read[1]);
                  }
                  catch (NumberFormatException e)
                  {
                     Util.exit("Poorly formatted double for random number range lower bound: " + read[1], fileName);
                  }
               case "random range upper bound":
                  try
                  {
                     config.highRand = Util.toDouble(read[1]);
                  }
                  catch (NumberFormatException e)
                  {
                     Util.exit("Poorly formatted double for random number range upper bound: " + read[1], fileName);
                  }
               case "truth table file":
                  config.truthTableFile = read[1];
                  break;
               case "load weights":
                  config.loadWeights = Util.toBoolean(read[1]);
                  break;
               case "save weights":
                  config.saveWeights = Util.toBoolean(read[1]);
                  break;
               case "weights file":
                  config.weightsFile = read[1];
                  break;
               case "run all":
                  config.runAll = Util.toBoolean(read[1]);
                  break;
               case "run case number":
                  config.runCaseNum = Util.toInt(read[1]);
               default:
                  Util.exit("Invalid configuration parameter \"" + read[0] + "\"", fileName);
            }
         }
      }

      return config;
   }

   public boolean readLine()
   {
      try
      {
         ln = in.readLine();
         lnNumber++;
         if (ln.toLowerCase().trim().equals("eof") || ln == null) {
            return false;
         }
         return true;
      }
      catch (EOFException e)
      {
         return false;
      }
      catch (IOException e)
      {
         Util.exit("Error reading line " + lnNumber, fileName);
      }

      return false;
   }

   public void parseNetworkConfig(String ln)
   {
      String[] read;
      read = ln.split("-");
      config.numLayers = read.length;
      config.numInAct = Util.toInt(read[0].trim());
      config.numHidAct = Util.toInt(read[1].trim());
      config.numOutAct = Util.toInt(read[2].trim());
   }

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
         out.writeUTF(Util.newLine(formatNetworkConfig()));
         out.writeUTF(Util.newLine(""));
         out.writeUTF(Util.newLine("Is training: " + config.isTraining));
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
         out.writeUTF(Util.newLine("Save weights: " + config.saveWeights));
         out.writeUTF(Util.newLine("Weights file: " + config.weightsFile));
      }
      catch (IOException e)
      {
         Util.exit("Error saving config", fileName);
      }
   }

   public String formatNetworkConfig()
   {
      return "Network configuration: " + config.numInAct + "-" + config.numHidAct + "-" + config.numOutAct;
   }
}
