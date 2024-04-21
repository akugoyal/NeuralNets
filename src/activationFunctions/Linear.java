package activationFunctions;

/**
 * This class represents the linear function and extends the Function abstract class.
 * Linear is defined as:
 *
 * f(x) = mx + b
 * and
 * f'(x) = m.
 *
 * Table of Contents:
 * 1. Linear(double m, double b)
 * 2. f(double x)
 * 3. fPrime(double x)
 * 4. toString()
 *
 * Author: Akul Goyal
 * Date of Creation: 4/14/2024
 */
public class Linear extends Function
{
   public double m;
   public double b;

/**
 * Constructs a linear function with the given slope and y-intercept. Sets the BOUNDED property to
 * false.
 * @param m the slope
 * @param b the y-intercept
 */
   public Linear(double m, double b)
   {
      super(false);
      this.m = m;
      this.b = b;
   }

/**
 * Computes the function value at x, given by mx + b.
 * @param x the input value
 * @return the function value
 */
   public double f(double x)
   {
      return m * x + b;
   }

/**
 * Computes the derivative of the function at x, given by m.
 * @param x the input value
 * @return the derivative value
 */
   public double fPrime(double x)
   {
      return m;
   }

/**
 * Returns the name and configuration of the function.
 */
   public String toString()
   {
      return "Linear, y = " + m + "x  + " + b;
   }
} //public class Linear implements Function
