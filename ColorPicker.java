
import java.util.Random;
import java.awt.Color;

/** A pseudo-random pastel color generator.
  * 
  * @author RR
  */
public class ColorPicker {
    
    private Random generator; // underlying random number generator
    private final static int WHITE = 255;
    private final static int ULTIMATE_SEED = 42;
    
    /** Initializes the generator by initializing the underlying random number
      * generator.
      * 
      * Note that the underlying random number generator is always primed with
      * the same seed to ensure replicatable results.
      */
    public ColorPicker() {
        this.generator = new Random(ULTIMATE_SEED);
    }
    
    
    /** Returns a pseudo-random pastel shade.
      * 
      * @return a Color object whose components have been generated randomly. 
      *         Some averaging is applied to avoid garish colors.
      */
    public Color nextColor() {
        
        int red = generator.nextInt(WHITE + 1);
        int green = generator.nextInt(WHITE + 1);
        int blue = generator.nextInt(WHITE + 1);
        
        red = (red + WHITE) / 2;
        green = (green + WHITE) / 2;
        blue = (blue + WHITE) / 2;
        
        return new Color(red, green, blue);
    }
}

