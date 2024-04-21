package activationFunctions;

/**
 * This class represents the Leaky ReLU function and extends the Function abstract class.
 * Leaky ReLU is defined as:
 *
 * f(x) = x, if x > 0,
 *        ax, if x <= 0
 * and
 * f'(x) = 1, if x > 0,
 *         a, if x <= 0.
 *
 * Table of Contents:
 * 1. LeakyReLU(double a)
 * 2. f(double x)
 * 3. fPrime(double x)
 * 4. toString()
 * 5. getA()
 *
 * Author: Akul Goyal
 * Date of Creation: 4/14/2024
 */
public class LeakyReLU extends Function
{
   double a;

/**
 * Constructs a Leaky ReLU function with the given slope. Sets the BOUNDED property to false.
 * @param a the slope
 */
   public LeakyReLU(double a)
   {
      super(false);
      this.a = a;
   }

/**
 * Computes the function value at x, given by x if x > 0, and ax if x <= 0
 * @param x the input value
 * @return the function value
 */
   public double f(double x)
   {
      return (x <= 0.0) ? a * x : x;
   }

/**
 * Computes the derivative of the function at x, given by 1 if x > 0, and a if x <= 0.
 * @param x the input value
 * @return the derivative value
 */
   public double fPrime(double x)
   {
      return (x <= 0.0) ? a : 1.0;
   }

/**
 * Returns the name and configuration of the function.
 */
   public String toString()
   {
      return "LeakyReLU" + ", α = " + getA();
   }

/**
 * Returns the slope of the Leaky ReLU function.
 */
   public double getA()
   {
      return a;
   }
} //public class LeakyReLU implements Function
