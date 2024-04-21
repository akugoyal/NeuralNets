package activationFunctions;

/**
 * This class represents the ReLU function and extends the LeakyReLU class.
 * ReLU is defined as:
 *
 * f(x) = x, if x > 0,
 *        0, if x <= 0
 * and
 * f'(x) = 1, if x > 0,
 *         0, if x <= 0.
 *
 * Table of Contents:
 * 1. ReLU
 * 3. toString()
 *
 * Author: Akul Goyal
 * Date of Creation: 4/14/2024
 */
public class ReLU extends LeakyReLU
{
/**
 * Constructs a ReLU function. Sets the BOUNDED property to false.
 */
   public ReLU()
   {
      super(0);
   }

/**
 * Returns the name of the function.
 */
   public String toString()
   {
      return "ReLU";
   }
} //public class ReLU implements Function
