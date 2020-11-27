
/** A class for representing and manipulating the information pertaining to an
  * undirected edge in an image-based grid graph.
  * 
  * This implementation is immutable.
  * 
  * @author RR
 */
public class Edge implements Comparable<Edge> {
    
    private Pixel node1; // first node connected by the edge
    private Pixel node2; // second node connected by the edge
    private double weight; // weight of the edge
    

    /** Constructs a new edge between the specified pixels (nodes).
      * 
      * @param node1 the first node connected by the edge
      * @param node2 the second node connected by the edge
      * @throws IllegalArgumentException if either node1 or node2 is null
      */
    public Edge(Pixel node1, Pixel node2) {
        
        if ((node1 == null) || (node2 == null)) {
            throw new IllegalArgumentException("Edges cannot connect one " 
                                                   + "or more null nodes");
        }
        
        this.node1 = node1;
        this.node2 = node2;
        this.weight = Math.abs(node1.getLuminance() - node2.getLuminance());
    }
    

    /** Returns the outcome of comparing this Edge to the specified Edge for
      * order.
      * 
      * The ordering among edges is imposed based on their weights -- the Edge
      * with the smaller weight is considered the "smaller" of the Edges. If two
      * Edges have the same weight, then the ordering is determined by the
      * ordering among the pixels that they connect.
      * 
      * @param other the other Edge we are comparing this Edge to.
      * @throws IllegalArgumentException if other is null
      * @return +1 if this Edge is greater than the other, -1 if it's lesser,
      *         and 0 if they're equal.
      */
    public int compareTo(Edge other) {
        if (other == null) {
            throw new IllegalArgumentException("Edges being compared " 
                                                   + "must be non-null");
        }
        
        if (this.equals(other))
            return 0;
        
        if (this.weight != other.weight)
            return (int)Math.signum(this.weight - other.weight);        
        else if (!this.node1.equals(other.node1))
            return this.node1.compareTo(other.node1);
        else
            return this.node2.compareTo(other.node2);
    }
    
    
    /** Returns the first node (pixel) that is connected by this Edge.
      * 
      * @return the first node (pixel) connected by the edge.
      */
    public Pixel getFirstPixel() {
        return node1;
    }
    
    
    /** Returns the second node (pixel) that is connected by this Edge.
      * 
      * @return the second node (pixel) connected by the edge.
      */
    public Pixel getSecondPixel() {
        return node2;
    }
    
    
    /** Returns the weight of this Edge. 
      * 
      * @return the weight of this Edge.
      */
    public double getWeight() {
        return weight;
    }
    
    
    @Override
    public boolean equals(Object other) {
        if (other instanceof Edge) {
            Edge e = (Edge)other;
            return (((this.node1.equals(e.node1) && this.node2.equals(e.node2)) 
                  || (this.node1.equals(e.node2) && this.node2.equals(e.node1)))
                  && (this.weight == e.weight));
        }
        
        return false;
    }
    
        
    @Override
    public int hashCode() {
        return ((31 * 31 * this.node1.hashCode()) + (31 * this.node2.hashCode())
                    + (int)this.weight);
    }
    
        
    @Override
    public String toString() {
        return ("(" + node1.toString() + ", " + node2.toString() + ", " 
                    + this.weight + ")");
    }
}
