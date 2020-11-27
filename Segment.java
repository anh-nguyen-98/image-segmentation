
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.Color;
import java.util.Scanner;


/** Program that segments a given JPEG image using the algorithm described in 
  * "Efficient Graph-Based Image Segmentation", by Felzenswalb & Huttenlocher.
  * 
  * @author RR
  */
public class Segment {
    
    private final static int NUM_COLOR_CHANNELS = 3;
    private final static int MAX_COLOR_DEPTH = 256;
    private final static int[] OFFSETS = {24, 16, 8, 0};
    
    
    /** Main driver method.
      * 
      * Prompts the user for program parameters and segments the specified
      * image.
      * 
      * @param args ignored.
      * @throws IOException if there is a read/write failure to the supplied
      *         files
      */
    public static void main(String[] args) throws IOException {
        
        // Grab segmentation parameters
        Scanner console = new Scanner(System.in);
        System.out.println("Please enter the name of the image file " 
                               + "to be segmented (must end with .jpg): ");
        String inputFile = console.nextLine();
        System.out.println("Please provide a name for the output image " 
                               + "file (must end with .jpg): ");
        String outputFile = console.nextLine();
        System.out.println("Please enter a value for the granularity " 
                               + "parameter: ");
        double granularity = console.nextDouble();
        
        // Read in RGB data
        Color[][] rgbArray = getImageRaster(inputFile);

        
        // Segment and write output
        writeImageRaster(outputFile, 
                         ImageSegmenter.segment(rgbArray, granularity));
    }
    
    
    /** Returns the image data read in from the specified JPG file.
      * 
      * @param fileName the name of the JPG file to be read.
      * @throws IOException if the given image file cannot be read.
      * @return a 2-d array of Color objects, where the object at (i, j)
      *         contains the RGB information for the pixel at row i and column
      *         j.
      */
    public static Color[][] getImageRaster(String fileName) throws IOException {
        BufferedImage img = null;
        
        // First open the file
        img = ImageIO.read(new File(fileName));
        
        // Compute the height and width of the image
        int width = img.getWidth(null);
        int height = img.getHeight(null);
        
        Color[][] pixels = new Color[height][width];
        
        // For each pixel in the image, extract the value of the specified color
        // channel
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                pixels[i][j] = new Color((img.getRGB(j, i) >> OFFSETS[1])&0xff,
                                         (img.getRGB(j, i) >> OFFSETS[2])&0xff,
                                         (img.getRGB(j, i) >> OFFSETS[3])&0xff);
            }
        }
        
        return pixels;
    }
    
     
    /** Writes the supplied image data to the specified file.
      * 
      * @param fileName the name of the output image file.
      * @param newRaster a two-dimensional array of Color objects containing
      *        the color information to be written at each pixel location.
      * @throws IOException if there is a failure when writing to the specified
      *        file.
      * @throws IllegalArgumentException if newRaster is null.
      */
    public static void writeImageRaster(String fileName, Color[][] newRaster) 
        throws IOException {
        
        if (newRaster == null) {
            throw new IllegalArgumentException("Cannot write null raster!");     
        }
        
        int height = newRaster.length;
        int width = newRaster[0].length;
        
        // Create an empty "canvas" of the required size
        BufferedImage img = new BufferedImage(width, height, 
                                              BufferedImage.TYPE_INT_RGB);
        
        // For every pixel in the image, determine the "combined" RGB value
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pixValue = ((MAX_COLOR_DEPTH << OFFSETS[0]) |
                                (newRaster[i][j].getRed() << OFFSETS[1]) |
                                (newRaster[i][j].getGreen() << OFFSETS[2]) |
                                (newRaster[i][j].getBlue() << OFFSETS[3]));
                img.setRGB(j, i, pixValue);
            }
        }
        
        // Write the pixel data to a file using the supplied filename
        ImageIO.write(img, "jpg", new File(fileName));
    }
    
    
}

