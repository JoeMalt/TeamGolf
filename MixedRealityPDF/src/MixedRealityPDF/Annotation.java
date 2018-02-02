package MixedRealityPDF;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public final class Annotation {

  public static enum Type{
    HIGHLIGHT,
    UNDERLINE,
    NEW_LINE;
  }

  private final double x;
  private final double y;
  private final Annotation.Type type;


  public Annotation(double x, double y, Annotation.Type type){
    this.type = type;
    this.x = x;
    this.y = y;
  }

  public Annotation(Point2D.Double p, Annotation.Type type){
    this.type = type;
    this.x = p.x;
    this.y = p.y;
  }

  public Type getType() {
    return type;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public Point2D.Double getPoint(){
    return new Point2D.Double(x, y);
  }

}
