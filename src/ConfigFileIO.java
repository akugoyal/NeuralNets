import java.io.*;

public class ConfigFileIO
{
   private DataOutputStream out;
   private DataInputStream in;
   private String fileName;
   private String ln;
   private int lnNumber;
   private Config config;

   public ConfigFileIO(String fileName, String defaultWeightsFile, String defaultTruthTableFile)
   {
      this.fileName = fileName;
      lnNumber = 1;
      config = new Config(defaultWeightsFile, defaultTruthTableFile);
   }

   public ConfigFileIO(String fileName, Config config, String defaultWeightsFile,
                       String defaultTruthTableFile)
   {
      this(fileName, defaultWeightsFile, defaultTruthTableFile);
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
         if (ln.trim().startsWith("#") || ln.isBlank()) {
            continue;
         }
         else if (ln.contains(":"))
         {
            read = ln.split(":");
            if (read.length < 2)
            {
               Util.exit("Missing key or value in config file. Line: \n\t" + ln, fileName);
            }
            read[0] = read[0].trim().toLowerCase();
            read[1] = read[1].trim();

            switch (read[0])
            {
               case "network configuration":
                  parseNetworkConfig(read[1]);
                  break;
               case "network mode":
                  try
                  {
                     config.networkMode = Util.toInt(read[1]);
                     break;
                  }catch (NumberFormatException e)
                  {
                     Util.exit("Poorly formatted integer for Network Mode: " + read[1],
                           fileName);
                  }
               case "number of cases":
                  try
                  {
                     config.numCases = Util.toInt(read[1]);
                     if (config.numCases == 0) {
                        Util.exit("Invalid: \"Number of Cases\" parameter is 0." + ln, fileName);
                     }
                     break;
                  }
                  catch (NumberFormatException e)
                  {
                     Util.exit("Poorly formatted integer for Number of Training Cases: " + read[1], fileName);
                  }
               case "max training iterations":
                  try
                  {
                     config.maxIters = Util.toInt(read[1]);
                     break;
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
                     break;
                  }
                  catch (NumberFormatException e)
                  {
                     Util.exit("Poorly formatted double for lambda: " + read[1], fileName);
                  }
               case "error threshold":
                  try
                  {
                     config.errThreshold = Util.toDouble(read[1]);
                     break;
                  }
                  catch (NumberFormatException e)
                  {
                     Util.exit("Poorly formatted double for error threshold: " + read[1], fileName);
                  }
               case "random range lower bound":
                  try
                  {
                     config.lowRand = Util.toDouble(read[1]);
                     break;
                  }
                  catch (NumberFormatException e)
                  {
                     Util.exit("Poorly formatted double for random number range lower bound: " + read[1], fileName);
                  }
               case "random range upper bound":
                  try
                  {
                     config.highRand = Util.toDouble(read[1]);
                     break;
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
               case "run case number":
                  try
                  {
                     config.runCaseNum = Util.toInt(read[1]);
                     if (config.runCaseNum > config.numCases) {
                        Util.exit("Case " + config.runCaseNum + " in config file exceeds total " +
                              "number of cases: " + config.numCases, fileName);
                     }
                     break;
                  }
                  catch (NumberFormatException e)
                  {
                     Util.exit("Poorly formatted double for Run Case Number: " + read[1], fileName);
                  }
               case "keep alive interval":
                  try {
                     config.keepAliveInterval = Util.toInt(read[1]);
                     break;
                  } catch (NumberFormatException e) {
                     Util.exit("Poorly formatted integer for Keep Alive Interval: " + read[1],
                           fileName);
                  }
               default:
                  Util.exit("Invalid configuration parameter \"" + read[0] + "\"", fileName);
            }
         } else {
            System.out.println("File \"" + fileName + "\" - Ignoring garbage line: " + ln);
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
         if (ln == null || ln.toLowerCase().trim().equals("eof")) {
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
      if (config.numLayers > 2)
      {
         config.numInAct = Util.toInt(read[0].trim());
         config.numHidAct = Util.toInt(read[1].trim());
         config.numOutAct = Util.toInt(read[2].trim());
      } else {
         Util.exit("Missing network configuration parameters. Parsed: " + ln, fileName);
      }

      if (config.numInAct == 0 || config.numHidAct == 0 || config.numOutAct == 0) {
         Util.exit("Invalid network configuration parameters. Parsed: " + ln, fileName);
      }
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
         out.writeUTF(Util.newLine("Network mode: " + config.networkMode));
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
