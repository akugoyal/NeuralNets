/**
 * This class represents the ReLU function and implements the Function interface.
 * ReLU is defined as:
 *
 * f(x) = x, if x > 0,
 *        0, if x <= 0
 * and
 * f'(x) = 1, if x > 0,
 *         0, if x <= 0.
 *
 * Table of Contents:
 * 1. f(double x)
 * 2. fPrime(double x)
 * 3. toString()
 *
 * Author: Akul Goyal
 * Date of Creation: 4/14/2024
 */
public class ReLU extends Function
{
/**
 * Constructs a ReLU function. Sets the BOUNDED property to false.
 */
   public ReLU()
   {
      super(false);
   }
/**
 * Computes the function value at x, given by x if x > 0, and 0 if x <= 0
 * @param x the input value
 * @return the function value
 */
   public double f(double x)
   {
      return (x <= 0) ? 0 : x;
   }

/**
 * Computes the derivative of the function at x, given by 1 if x > 0, and 0 if x <= 0
 * @param x the input value
 * @return the derivative value
 */
   public double fPrime(double x)
   {
      return (x <= 0) ? 0 : 1;
   }

/**
 * Returns the name of the function.
 */
   public String toString()
   {
      return "ReLU";
   }
} //public class ReLU implements Function
