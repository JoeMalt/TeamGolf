package MixedRealityPDF.ImageProcessor.Alignment;

import java.awt.image.BufferedImage;

public class Coordinate {
    Coordinate(int x, int y) {
        this.x = x; this.y = y;
    }
    int x, y;


    Coordinate(Coordinate c) {
        this.x = c.x;
        this.y = c.y;
    }

    public Coordinate getCopy() {
        return new Coordinate(this);
    }


    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public boolean xCoordIsAtLeastThatOf(Coordinate possB) {
        return (this.x >= possB.x);
    }

    public boolean xCoordIsStrictlyLessThanThatOf(Coordinate possB) {
        return (this.x < possB.x);
    }

    public boolean xCoordIsStrictlyGreaterThanThatOf(Coordinate possB) {
        return (this.x > possB.x);
    }

    public boolean xCoordIsAtMostThatOf(Coordinate possB) {
        return (this.x <= possB.x);
    }


    public int squareDistanceFrom(Coordinate A, Coordinate referencePoint) {
        return (int) (Math.pow(A.x - referencePoint.x, 2) + Math.pow(A.y - referencePoint.y, 2));
    }

    public static Coordinate getTopRight(BufferedImage image) {
        return new Coordinate(image.getWidth(), 0);
    }

    public static Coordinate getBottomRight(BufferedImage image) {
        return new Coordinate(image.getWidth(), image.getHeight());
    }

    public static Coordinate getBottomLeft(BufferedImage image) {
        return new Coordinate(0, image.getHeight());
    }

    public static final Coordinate ORIGIN = new Coordinate(0, 0);

    public boolean beatsA(Coordinate A) {



        return squareDistanceFrom(this, ORIGIN) < squareDistanceFrom(A, ORIGIN);
    }


    public boolean beatsB(Coordinate B, BufferedImage image) {
        return squareDistanceFrom(this, getTopRight(image)) < squareDistanceFrom(B, getTopRight(image));
    }
    public boolean beatsC(Coordinate C, BufferedImage image) {
        return squareDistanceFrom(this, getBottomRight(image)) < squareDistanceFrom(C, getBottomRight(image));
    }
    public boolean beatsD(Coordinate D, BufferedImage image) {
        return squareDistanceFrom(this, getBottomLeft(image)) < squareDistanceFrom(D, getBottomLeft(image));
    }



}


