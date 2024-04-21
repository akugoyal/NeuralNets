package activationFunctions;

/**
 * This class represents the Gaussian function and extends the Function abstract class.
 * Gaussian is defined as:
 *
 * f(x) = exp(-x^2)
 * and
 * f'(x) = -2x * exp(-x^2),
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
public class Gaussian extends Function
{
/**
 * Constructs a Gaussian function. Sets the BOUNDED property to true.
 */
   public Gaussian()
   {
      super(true);
   }
/**
 * Computes the function value at x, given by exp(-x^2).
 * @param x the input value
 * @return the function value
 */
   public double f(double x)
   {
      return Math.exp(-x * x);
   }

/**
 * Computes the derivative of the function at x, given by -2x * exp(-x^2).
 * @param x the input value
 * @return the derivative value
 */
   public double fPrime(double x)
   {
      return -2.0 * x * f(x);
   }

/**
 * Returns the name of the function.
 */
   public String toString()
   {
      return "Gaussian";
   }
} //public class Gaussian implements Function
