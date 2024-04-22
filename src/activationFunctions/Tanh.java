package activationFunctions;

/**
 * This class represents the hyperbolic tangent function and extends the Function abstract class.
 * Tanh is defined as:
 *
 * f(x) = (exp(x) - exp(-x)) / (exp(x) + exp(-x))
 * and
 * f'(x) = 1 - f(x)^2,
 * where exp(x) raises e to the power of x.
 *
 * Table of Contents:
 * 1. Tanh()
 * 2. f(double x)
 * 3. fPrime(double x)
 * 4. toString()
 *
 * Author: Akul Goyal
 * Date of Creation: 4/14/2024
 */
public class Tanh extends Function
{
/**
 * Constructs a Tanh function. Sets the BOUNDED property to true.
 */
   public Tanh() {
      super(true);
   }

/**
 * Computes the function value at x, given by (exp(x) - exp(-x)) / (exp(x) + exp(-x)).
 * @param x the input value
 * @return the function value
 */
   public double f(double x)
   {
      double sign;
      sign = (x > 0) ? 1.0 : -1.0;

      return sign * (1.0 - Math.exp(sign * -2.0 * x)) / (1.0 + Math.exp(sign * -2.0 * x));
   }

/**
 * Computes the derivative of the function at x, given by 1 - f(x)^2.
 * @param x the input value
 * @return the derivative value
 */
   public double fPrime(double x)
   {
      double fVal = this.f(x);
      return 1.0 - (fVal * fVal);
   }

/**
 * Returns the name of the function.
 */
   public String toString()
   {
      return "Tanh";
   }
} //public class Tanh implements Function
