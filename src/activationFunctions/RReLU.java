package activationFunctions;

/**
 * This class represents the RReLU (Randomized ReLU) function and extends the LeakyReLU class.
 * RReLU is defined the same as the Leaky ReLU function, but with a random slope. The random
 * slope is generated once in the constructor and used throughout the function.
 *
 * Table of Contents:
 * 1. RReLU()
 * 4. toString()
 *
 * Author: Akul Goyal
 * Date of Creation: 4/14/2024
 */
public class RReLU extends LeakyReLU
{

/**
 * Constructs a RReLU function based upon an underlying LeakyReLU with a random slope.
 */
   public RReLU()
   {
      super(Math.random());
   }

/**
 * Returns the name and configuration of the function.
 */
   public String toString()
   {
      return "ReLU" + ", α = " + getA();
   }
} //public class RReLU implements Function
