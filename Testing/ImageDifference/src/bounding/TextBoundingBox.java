package bounding;


public class TextBoundingBox {

    Coordinate  coordA, coordB, coordC, coordD;

    TextBoundingBox(Coordinate A, Coordinate B, Coordinate C, Coordinate D) {
        coordA = A; coordB = B; coordC = C; coordD = D;
    }

    @Override
    public String toString(){
        return coordA.toString() + " -- " + coordB.toString() + " -- " + coordC.toString() + " -- " + coordD.toString() + " dimensions: " + getDimensions();
    }

    private String getDimensions() {
        return (coordB.x - coordA.x) + " x " + (coordD.y - coordA.y);
    }




}
