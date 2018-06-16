package org.sethia.utils.graphing;

import java.util.Date;
import java.util.List;
import org.sethia.utils.Tuple;

public abstract class LineGrapher {

  protected final String frameTitle, xAxisName, yAxisName;

  public LineGrapher(final String frameTitle,
      final String xAxisName,
      final String yAxisName) {
    this.frameTitle = frameTitle;
    this.xAxisName = xAxisName;
    this.yAxisName = yAxisName;
  }

  /**
   * @param points instance of {@link Tuple} with (x,y) as point. i.e. x = {@link Tuple#getLeft()}
   * and y = {@link Tuple#getRight()}
   * @param lineName name of the line to which these points belong
   */
  /*public void addPoints(List<Tuple> points,
      final String lineName) {
    points
        .stream()
        .map(tuple -> ((Tuple<? extends Comparable, Number>) tuple))
        .forEach(tuple -> addPoint(tuple.getRight(), tuple.getLeft(), lineName));
  }*/
  public abstract void addPoint(final Date date, final Number yValue,
      final String lineName);

  public abstract void render();
}
