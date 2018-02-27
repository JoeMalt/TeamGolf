package MixedRealityPDF.ImageProcessor.ColourRemoval;

import java.awt.Color;
import java.util.function.BiFunction;

public interface IColorDistance extends BiFunction<Color, Color, Double> {
    double max();
}
