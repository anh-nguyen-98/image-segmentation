
import java.awt.Color;

/** A class for maintaining the information pertaining to a single pixel in an 
  * image.
  * 
  * The class has been designed particularly for the task of representing nodes
  * in an image grid-graph. This implementation is immutable.
  * 
  * @author RR
 */
public class Pixel implements Comparable<Pixel> {
    
    private int row; // row coordinate of the pixel in the image raster
    private int col; // column coordinate of the pixel
    private double luminance; // luminance of the pixel


    

    /** Constructs a new Pixel object for the specified (x, y) coordinate with
      * given RGB color.
      * 
      * @param row the row index of this Pixel's location in the image.
      * @param col the column index of this Pixel's location in the image.
      * @param rgb the color of this Pixel.
      * @throws IllegalArgumentException if either row or col is negative, or if
      *         rgb is null.
      */
    public Pixel(int row, int col, Color rgb) {

        if ((row < 0) || (col < 0) || (rgb == null))
            throw new IllegalArgumentException("Illegal pixel!");
        
        final double[] CHANNEL_WEIGHTS = {0.30, 0.59, 0.11};
        this.row = row;
        this.col = col;
        this.luminance = ((CHANNEL_WEIGHTS[0] * rgb.getRed()) +
                          (CHANNEL_WEIGHTS[1] * rgb.getGreen()) +
                          (CHANNEL_WEIGHTS[2] * rgb.getBlue()));  
    }
    
    
    /** Returns the row index of this Pixel.
      * 
      * @return the row index of this Pixel.
      */
    public int getRow() {
        return this.row;
    }
    
    
    /** Returns the column index of this Pixel.
      * 
      * @return the column index of this Pixel.
      */
    public int getCol() {
        return this.col;
    }
    
    
    /** Returns the luminance of this Pixel.
      * 
      * @return the luminance value of this pixel.
      */
    public double getLuminance() {
        return this.luminance;
    }
    
    
    /** Returns the outcome of comparing this Pixel to the specified Pixel for
      * order.
      * 
      * The ordering among Pixels is imposed based on their coordinates --- a
      * Pixel with a lower row index is "smaller" than a Pixel with a higher
      * row index. If the Pixels are on the same row, then the ordering is
      * determined by the column index.
      * 
      * @param other the other Pixel we are comparing this Pixel to.
      * @throws IllegalArgumentException if other is null
      * @return a positive integer if this Edge is greater than the other, -1 
      *         if it's lesser, and 0 if they're equal.
      */
    public int compareTo(Pixel other) {
        if (other == null) {
            throw new IllegalArgumentException("Pixels being compared must be" +
                                               " non-null!");
        }
        
        if (this.row != other.row)
            return this.row - other.row;
        
        return this.col - other.col;
    }
    
    
    @Override
    public boolean equals(Object other) {
        
        if (other instanceof Pixel) {
            Pixel p = (Pixel)other;
            return ((this.row == p.row) && (this.col == p.col));
        }
        
        return false;
    }
    
    
    @Override
    public int hashCode() {        
        // The 31 is an arbitrary, albeit popular choice
        return (31 * this.row) + this.col;
    }
    
    
    @Override
    public String toString() {
        return "(" + this.row + ", " + this.col + ", " + this.luminance + ")";
    }
}

