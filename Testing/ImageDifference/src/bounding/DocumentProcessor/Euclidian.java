package bounding.DocumentProcessor;

import java.awt.Color;

public class Euclidian extends EuclidianSquared {

    private final double MAX;

    public Euclidian() {
        super();
        MAX = Math.sqrt( super.max() );
    }

    @Override
    public double max() {
        return MAX;
    }

    @Override
    public Double apply(Color a, Color b) {
        return Math.sqrt( super.apply(a, b) );
    }
}
