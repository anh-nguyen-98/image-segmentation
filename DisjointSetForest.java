import java.util.*;
import java.awt.Color;

public class DisjointSetForest {
    Map<Pixel, Section> forest;

    private class Section {
        Pixel representative;
        double iD;
        int size, rank;
        Section parent;
        Color color;

        public Section(Pixel rep) {
            this.representative = rep;  
            this.iD = 0.0;
            this.size = 1;
            this.rank = 0;
            this.parent = this;
            this.color = null;
        }
    }

    public DisjointSetForest(Pixel[][] graph) {
        this.forest = new HashMap<Pixel, Section>();
        for (Pixel[] row : graph) {
            for (Pixel pixel : row) {
                Section sect = new Section(pixel);
                this.forest.put(pixel, sect);
            }
        }
    }

    public void union(Pixel first, Pixel second, double newID) {
        Section root1 = findSection(this.forest.get(first));
        Section root2 = findSection(this.forest.get(second));

        if (root1.rank == root2.rank) {
            root1.rank++;
            root1.iD = newID;
            root1.size += root2.size;
            root2.parent = root1;
        } 
        else if (root1.rank > root2.rank) {
            root1.iD = newID;
            root1.size += root2.size;
            root2.parent = root1;
        } 
        else {
            root2.iD = newID;
            root2.size += root1.size;
            root1.parent = root2;
        }
    }
    /**
     * Returns the representative pixel of the root section of this pixel.
     */
    public Pixel find(Pixel pixel){
        Section section = this.forest.get(pixel); // ***
        Section root = findSection(section);
        return root.representative;
    }

    private Section findSection(Section sect) {
        Section parent = sect.parent;
        if (sect.equals(parent)) {
            return parent;
        }
        sect.parent = findSection(parent);
        return sect.parent;
    }

    public Color[][] colorTheSections(Color[][] rgbArray) {
        ColorPicker lottery = new ColorPicker();
        for (Pixel pixel : forest.keySet()){
            Section root = findSection(forest.get(pixel));
            if (root.color == null){
                Color randomColor = lottery.nextColor();
                root.color = randomColor;
                rgbArray[pixel.getRow()][pixel.getCol()] = randomColor;
            } else {
                rgbArray[pixel.getRow()][pixel.getCol()] = root.color;
            }
        }
        return rgbArray;
    }

    public int getSectSize(Pixel pixel){
        Section root = findSection(this.forest.get(pixel));
        return root.size;
    }

    public double getSectID(Pixel pixel){
        Section root = findSection(this.forest.get(pixel));
        return root.iD;
    }

}