package bounding;

public class Transformation {

    // Semantics: apply the translation to the scan
    // Then consider the xscale and yscale as stretch factors to be applied to the scan after it's been translated, around the point
    // at the top left of the bounding box




    int xtrans, ytrans;
    Double xScale, yScale;

    Double rotationRadians = 0d;

    public Transformation(int xtrans, int ytrans, Double xScale, Double yScale, Double rotationRadians) {
        this.xtrans = xtrans;
        this.ytrans = ytrans;
        this.xScale = xScale;
        this.yScale = yScale;
        this.rotationRadians = rotationRadians;
    }
}
