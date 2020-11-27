import java.awt.Color;
import java.util.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImageSegmenterClient{
    private final static int NUM_COLOR_CHANNELS = 3;
    private final static int MAX_COLOR_DEPTH = 256;
    private final static int[] OFFSETS = {24, 16, 8, 0};
    
    private static boolean edgeWeightTest(Edge edge, DisjointSetForest sectionForest, Pixel firstSectionRep, Pixel secondSectionRep, double granularity){
        return edge.getWeight() < Math.min(sectionForest.getSectID(firstSectionRep) + granularity/sectionForest.getSectSize(firstSectionRep),
        sectionForest.getSectID(secondSectionRep) + granularity/sectionForest.getSectSize(secondSectionRep));
    }
    public static Pixel[][] getPixelArray (Color[][] rgbArray){
        Pixel[][] pixelArray = new Pixel[rgbArray.length][rgbArray[0].length];
        for (int row = 0; row < rgbArray.length; row++){
            for (int col = 0; col < rgbArray[0].length; col++){
                pixelArray[row][col] = new Pixel(row, col, rgbArray[row][col]);
            }
        }
        return pixelArray;
    }

    public static boolean neighborExists(Pixel[][] pixelArray, int row, int col){
        return (row >= 0 && row < pixelArray.length && col >= 0 && col < pixelArray[0].length);
    }


    public static Queue<Edge> getEdgeList(Pixel[][] pixelArray){
        Queue<Edge> edgeList = new PriorityQueue<Edge>();
        
        for (int row = 0; row < pixelArray.length; row++){
            for (int col = 0; col < pixelArray[0].length; col++){
                getEdges(pixelArray, edgeList, row, col);
            }
        }
        return edgeList;
    }

    public static void getEdges(Pixel[][] pixelArray, Queue<Edge> edgeList, int row, int col){
        // explores east
        if (neighborExists(pixelArray, row, col+1)){
            edgeList.add(new Edge(pixelArray[row][col], pixelArray[row][col+1]));
        }

        // explores south
        if (neighborExists(pixelArray, row+1, col)){
            edgeList.add(new Edge(pixelArray[row][col], pixelArray[row+1][col]));
        }

        // explores south east
        if (neighborExists(pixelArray, row+1, col+1)){
            edgeList.add(new Edge(pixelArray[row][col], pixelArray[row+1][col+1]));
        }

        // explores south west
        if (neighborExists(pixelArray, row+1, col-1)){
            edgeList.add(new Edge(pixelArray[row][col], pixelArray[row+1][col-1]));
        }
    }

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

    public static void main (String[] args) throws IOException {


        // double granularity = 2200;
        // Color[][] rgbArray = new Color[2][3];
        // ColorPicker colorGenerator = new ColorPicker();
        // for (int row = 0; row < rgbArray.length; row++){
        //     for (int col = 0; col < rgbArray[0].length; col++){
        //         rgbArray[row][col] = colorGenerator.nextColor();
        //     }
        // }
        
        // for (int row = 0; row < rgbArray.length; row++){
        //     System.out.println(Arrays.toString(rgbArray[row]));
        // }

        // Pixel[][] pixelArray = getPixelArray(rgbArray);

        // for (int row = 0; row < pixelArray.length; row++){
        //     for (int col= 0; col < pixelArray[0].length; col++){
        //         System.out.println(pixelArray[row][col]);
        //     }
        // }

        // Grab segmentation parameters
        Scanner console = new Scanner(System.in);
        System.out.println("Please enter the name of the image file " 
                               + "to be segmented (must end with .jpg): ");
        String inputFile = console.nextLine();
        System.out.println("Please enter a value for the granularity " 
                               + "parameter: ");
        double granularity = console.nextDouble();
        
        // Read in RGB data
        Color[][] rgbArray = getImageRaster(inputFile);
        Pixel[][] pixelArray = getPixelArray(rgbArray);

        Queue<Edge> edgeList = getEdgeList(pixelArray);
        // if (edgeList.get(0).compareTo(edgeList.get(1)) <0){
        //     System.out.println("true");
        // }

        // if (edgeList.get(23).compareTo(edgeList.get(100)) <0){
        //     System.out.println("true");
        // }
        
        // System.out.println("Edge weight: ");
        // for (Edge edge: edgeList){
        //     System.out.println(edge.getWeight());
        // }

        // System.out.println();
        // System.out.println(edgeList.size());
        // System.out.println();
        // for (int i= 0; i < edgeList.size(); i++){
        //     for (int j = i+1; j < edgeList.size(); j++){
        //         if (edgeList.get(i).equals(edgeList.get(j))){
        //             System.out.println("true");
        //         }
        //     }
        // }
        // System.out.println(edgeList);
        DisjointSetForest sectionForest = new DisjointSetForest(pixelArray);
        
        while(!edgeList.isEmpty()){
            Edge edge = edgeList.remove();
            Pixel firstPixel = edge.getFirstPixel();
            Pixel secondPixel = edge.getSecondPixel();

            Pixel firstSectionRep = sectionForest.find(firstPixel);
            Pixel secondSectionRep = sectionForest.find(secondPixel);

            if (!firstSectionRep.equals(secondSectionRep)){
                if (edgeWeightTest(edge, sectionForest, firstSectionRep, secondSectionRep, granularity)){
                    sectionForest.union(firstSectionRep, secondSectionRep, edge.getWeight());
                }
            }
        }

        Color[][] result = sectionForest.colorTheSections(rgbArray);
        pixelArray = null;
        System.out.println(sectionForest.getNumSections());

    
    }
}