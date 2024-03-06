import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class test
{
   public static void main(String[] args) throws FileNotFoundException
   {
      Scanner s = new Scanner(new File("test.txt"));
      s.useDelimiter(Pattern.compile("(\\p{javaWhitespace}|:)+"));
      s.next("hi");
      System.out.println(s.next());
   }
}
