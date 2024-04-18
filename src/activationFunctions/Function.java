package activationFunctions;

/**
 * Abstract class for a function and its derivative.
 *
 * Table of Contents:
 * 1. Function(boolean bounded)
 * 2. f(double x)
 * 3. fPrime(double x)
 * 4. toString()
 *
 * Author: Akul Goyal
 * Date of Creation: 04/14/2024
 */

public abstract class Function
{
   public final boolean BOUNDED;

   public Function(boolean bounded)
   {
      BOUNDED = bounded;
   }

   /**
 * Computes the function value at x.
 * @param x the input value
 * @return the function value
 */
   public abstract double f(double x);

/**
 * Computes the derivative of the function at x.
 * @param x the input value
 * @return the derivative value
 */
   public abstract double fPrime(double x);

/**
 * Returns the name of the function.
 */
   public abstract String toString();
} //public interface Function
