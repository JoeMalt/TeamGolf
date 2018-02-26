package bounding.DocumentProcessor;

import java.awt.Color;

public class EuclidianSquared implements IColorDistance {

    private static final double MAX = 3*Math.pow(255, 2);

    @Override
    public double max() {
        return MAX;
    }

    @Override
    public Double apply(Color a, Color b) {
        return Math.pow(a.getRed()-b.getRed(), 2)
             + Math.pow(a.getGreen()-b.getGreen(), 2)
             + Math.pow(a.getBlue()-b.getBlue(), 2)
        ;
    }
}
