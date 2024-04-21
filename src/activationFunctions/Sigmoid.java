package activationFunctions;

/**
 * This class represents the sigmoid function and extends the Function abstract class. Sigmoid
 * is defined as:
 *
 * f(x) = 1 / (1 + exp(-x))
 * and
 * f'(x) = f(x) * (1 - f(x)),
 * where exp(x) raises e to the power of x.
 *
 * Table of Contents:
 * 1. f(double x)
 * 2. fPrime(double x)
 * 3. toString()
 *
 * Author: Akul Goyal
 * Date of Creation: 4/14/2024
 */

public class Sigmoid extends Function
{
/**
 * Constructs a Sigmoid function. Sets the BOUNDED property to true.
 */
   public Sigmoid()
   {
      super(true);
   }
/**
 * Computes the function value at x, given by 1 / (1 + exp(-x)).
 * @param x the input value
 * @return the function value
 */
   public double f(double x)
   {
      return 1.0 / (1.0 + Math.exp(-x));
   }

/**
 * Computes the derivative of the function at x, given by f(x) * (1 - f(x)).
 * @param x the input value
 * @return the derivative value
 */
   public double fPrime(double x)
   {
      double fVal = this.f(x);
      return fVal * (1.0 - fVal);
   }

/**
 * Returns the name of the function.
 */
   public String toString()
   {
      return "Sigmoid";
   }
} //public class Sigmoid implements Function
