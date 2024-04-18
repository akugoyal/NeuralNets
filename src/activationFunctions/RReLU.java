package activationFunctions;

/**
 * This class represents the RReLU (Randomized ReLU) function and implements the Function interface.
 * RReLU is defined the same as the Leaky ReLU function, but with a random slope. The random
 * slope is generated once in the constructor and used throughout the function.
 *
 * Table of Contents:
 * 1. RReLU()
 * 2. f(double x)
 * 3. fPrime(double x)
 * 4. toString()
 *
 * Author: Akul Goyal
 * Date of Creation: 4/14/2024
 */
public class RReLU extends Function
{
   public LeakyReLU leaky;

/**
 * Constructs a RReLU function based upon an underlying LeakyReLU with a random slope.
 */
   public RReLU()
   {
      super(false);
      this.leaky = new LeakyReLU(Math.random());
   }

/**
 * Uses a LeakyReLU object to compute the function value at x, given by x if x > 0, and ax if
 * x <= 0, with the randomly chosen a value.
 * @param x the input value
 * @return the function value
 */
   public double f(double x)
   {
      return leaky.f(x);
   }

/**
 * Uses a LeakyReLU object to compute the derivative of the function at x, given by 1 if x > 0,
 * and a if x <= 0, with the randomly chosen a value.
 * @param x the input value
 * @return the derivative value
 */
   public double fPrime(double x)
   {
      return leaky.fPrime(x);
   }

/**
 * Returns the name and configuration of the function.
 */
   public String toString()
   {
      return "ReLU" + ", " + leaky.getA();
   }
} //public class RReLU implements Function
