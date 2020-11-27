/**
 * Implementation of image segmentation.
 * Partitions a given image into a set of distint segments.
 * 
 * @author Nguyen Cao Nghi
 * @author Nguyen Hoang Nam Anh
 * 
 * Time spent: 6 hours
 */
import java.awt.Color;
import java.util.*;

public class ImageSegmenter {
    /**
     * Returns a 2d Color array, containing the shading information
     * for each segment in the image.
     * 
     * @param rgbArray the 2d Color array containing the RGB data
     * for the image.
     * @param granularity the granularity used for performing the segmentation.
     * 
     * @return the 2d Color array, containing the shading information
     * for each segment in the image.
     */
    public static Color[][] segment(Color[][] rgbArray, double granularity) {
        // constructs graph of the image 
        Pixel[][] pixelArray = getPixelArray(rgbArray);
        Queue<Edge> edgeList = getEdgeList(pixelArray); 

        DisjointSetForest dSF = new DisjointSetForest(pixelArray);
        
        // performs segmentation
        while (!edgeList.isEmpty()) {
            Edge edge = edgeList.poll();
            Pixel first = edge.getFirstPixel();
            Pixel second = edge.getSecondPixel();

            // finds representative of each pixel's segment
            Pixel firstRep = dSF.find(first);
            Pixel secondRep = dSF.find(second);

            if (!firstRep.equals(secondRep)) {
                if (weightTest(edge, dSF, firstRep, secondRep, granularity)) {
                    dSF.union(firstRep, secondRep, edge.getWeight());
                }
            }
        }

        Color[][] result = dSF.colorTheSections(rgbArray);
        pixelArray = null;

        return result; 
    }

    /**
     * Returns the 2d Pixel representation of the image. 
     * 
     * @param rgbArray the 2d Color array containing the RGB data
     * for the image.
     * 
     * @return the 2d Pixel array containing the pixels of the image.
     */
    private static Pixel[][] getPixelArray (Color[][] rgbArray){
        Pixel[][] pixelArray = new Pixel[rgbArray.length][rgbArray[0].length];
        for (int r = 0; r < rgbArray.length; r++){
            for (int c = 0; c < rgbArray[0].length; c++){
                pixelArray[r][c] = new Pixel(r, c, rgbArray[r][c]);
            }
        }
        return pixelArray;
    }

    /**
     * Returns the list of undirected edges of the image. 
     * An edge is formed by one pixel and its neighbors in 8 directions (if 
     * they exist).
     * 
     * @param pixelArray the 2d Pixel array containing the pixels of the image.
     * 
     * @return the list of edges formed by the pixels.
     */
    private static Queue<Edge> getEdgeList(Pixel[][] pixelArray){
        Queue<Edge> edgeList = new PriorityQueue<Edge>();
        int numRow = pixelArray.length;
        int numCol = pixelArray[0].length;

        // deals with non-corner pixels
        for (int r = 1; r < numRow -1; r++){
            for (int c = 1; c < numCol -1; c++){
                getMostEdges(pixelArray, edgeList, r, c);
            }
        }

        // deals with corner-pixels on the sides of the image
        // horizontal sides:
        for (int r = 0; r < numRow; r += (numRow - 1)) {
            for (int c = 0; c < numCol; c++) {
                getSideEdges(pixelArray, edgeList, r, c);
            }
        } 
        // vertical sides:
        for (int r = 1; r < numRow -1; r++) {
            for (int c = 0; c < numCol; c += (numCol - 1)) {
                getSideEdges(pixelArray, edgeList, r, c);
            }
        }

        return edgeList;
    }

    /**
     * Adds the edges formed by non-corner pixels to the list of edges.
     * 
     * @param pixelArray the 2d Pixel array containing the pixels of the image.
     * @param edgeList the list of edges formed by the pixels.
     * @param r row index of the pixel.
     * @param c column index of the pixel.
     */
    private static void getMostEdges(Pixel[][] pixelArray, 
                                Queue<Edge> edgeList, int r, int c) {
        // explores east
        edgeList.offer(new Edge(pixelArray[r][c], pixelArray[r][c+1]));

        // explores south
        edgeList.offer(new Edge(pixelArray[r][c], pixelArray[r+1][c]));

        // explores south east
        edgeList.offer(new Edge(pixelArray[r][c], pixelArray[r+1][c+1]));

        // explores south west
        edgeList.offer(new Edge(pixelArray[r][c], pixelArray[r+1][c-1]));
    }

    /**
     * Adds the edges formed by corner pixels to the list of edges.
     * 
     * @param pixelArray the 2d Pixel array containing the pixels of the image.
     * @param edgeList the list of edges formed by the pixels.
     * @param r row index of the pixel.
     * @param c column index of the pixel.
     */
    private static void getSideEdges(Pixel[][] pixelArray, 
                                Queue<Edge> edgeList, int r, int c) {
        // explores east
        if (neighborExists(pixelArray, r, c+1))
        edgeList.offer(new Edge(pixelArray[r][c], pixelArray[r][c+1]));

        // explores south
        if (neighborExists(pixelArray, r+1, c))
        edgeList.offer(new Edge(pixelArray[r][c], pixelArray[r+1][c]));

        // explores south east
        if (neighborExists(pixelArray, r+1, c+1))
        edgeList.offer(new Edge(pixelArray[r][c], pixelArray[r+1][c+1]));

        // explores south west
        if (neighborExists(pixelArray, r+1, c-1))
        edgeList.offer(new Edge(pixelArray[r][c], pixelArray[r+1][c-1]));
    }

    /**
     * Returns whether this neighboring pixel exists.
     * 
     * @param pixelArray the 2d Pixel array containing the pixels of the image.
     * @param r row index of the pixel.
     * @param c column index of the pixel.
     * 
     * @return true iff this pixel exists.
     */
    private static boolean neighborExists(Pixel[][] pixelArray, int r, int c){
        return r >= 0 && r < pixelArray.length 
                                    && c >= 0 && c < pixelArray[0].length;
    }

    /**
     * Returns whether the weight of the bridging edge is within some 
     * neighborhood of the internal distance of the two segments.
     * 
     * @param edge the bridging edge. 
     * @param dSF the disjoint set forest of segments. 
     * @param firstRep representative of the segment that the first pixel 
     * vertice belongs to.
     * @param secondRep representative of the segment that the second pixel 
     * vertice belongs to.
     * @param granularity the granularity used for performing the segmentation.
     * 
     * @return true iff the weight of the bridging eight is within some 
     * neighborhood of the internal distance of the two segments.
     */
    private static boolean weightTest(Edge edge, DisjointSetForest dSF, 
                        Pixel firstRep, Pixel secondRep, double granularity) {
        return edge.getWeight() < Math.min(
            dSF.getSectID(firstRep) + granularity/dSF.getSectSize(firstRep),
            dSF.getSectID(secondRep) + granularity/dSF.getSectSize(secondRep));
    }

}

