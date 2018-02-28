package MixedRealityPDF.ImageProcessor.Alignment;

public class LineSegment {

    LineSegment(Coordinate startCoord, Coordinate finishCoord) {
        start = startCoord; finish = finishCoord;
    }
    Coordinate start, finish;

    @Override
    public String toString(){
        return start.toString() + "--" + finish.toString();
    }

    public int xprojlength() {
        return Math.abs(finish.x - start.x);
    }

}
