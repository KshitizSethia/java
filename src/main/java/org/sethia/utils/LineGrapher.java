package org.sethia.utils;

import java.util.List;
import org.apache.logging.log4j.util.Strings;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO convert to builder to prevent adding data modifications after render

/**
 * references: https://www.tutorialspoint.com/jfreechart/jfreechart_line_chart.htm
 */
public class LineGrapher {

  private static final Logger log = LoggerFactory.getLogger(LineGrapher.class);

  private final ApplicationFrame frame;
  private final DefaultCategoryDataset dataset;
  private final String graphTitle;
  private final String yAxisName;
  private final String xAxisName;
  private Double minY = Double.MAX_VALUE;
  private Double maxY = Double.MIN_VALUE;

  public LineGrapher(final String frameTitle,
      final String xAxisName,
      final String yAxisName) {

    frame = new ApplicationFrame(frameTitle);
    dataset = new DefaultCategoryDataset();
    this.graphTitle = Strings.EMPTY;
    this.xAxisName = xAxisName;
    this.yAxisName = yAxisName;
  }

  private void updateMinMax(Double yVal) {
    minY = Double.min(yVal, minY);
    maxY = Double.max(yVal, maxY);
  }

  /**
   * @param points instance of {@link Tuple} with (x,y) as point. i.e. x = {@link Tuple#getLeft()}
   * and y = {@link Tuple#getRight()}
   * @param lineName name of the line to which these points belong
   */
  public void addPoints(List<Tuple<? extends Comparable, Number>> points, final String lineName) {
    for (Tuple<? extends Comparable, Number> tuple : points) {
      addPoint(tuple.getRight(), tuple.getLeft(), lineName);
    }
  }

  public void addPoint(final Number value, final Comparable nameForPoint, final String lineName) {
    log.trace("adding point - value: {}, nameForPoint: {}, lineName: {}", value,
        nameForPoint.toString(),
        lineName);
    dataset.addValue(value, lineName, nameForPoint);
    updateMinMax((Double) value);
  }

  public void render() {
    JFreeChart lineChart = ChartFactory
        .createLineChart(graphTitle, xAxisName, yAxisName, dataset, PlotOrientation.VERTICAL,
            true, true, false);

    // TODO space out x-axis (domain axis) ticks

    // scale graph vertically according to entries
    ValueAxis axis = lineChart.getCategoryPlot().getRangeAxis(0);
    axis.setRange(minY * 0.8, maxY * 1.05);

    // show points as shapes
    //LineAndShapeRenderer renderer = (LineAndShapeRenderer) lineChart.getCategoryPlot()
    //    .getRenderer();
    //renderer.setBaseShapesVisible(true);

    ChartPanel chartPanel = new ChartPanel(lineChart);

    // display tooltips instantly (default seems to be a 1 second delay)
    chartPanel.setInitialDelay(0);

    frame.setContentPane(chartPanel);
    frame.pack();
    RefineryUtilities.centerFrameOnScreen(frame);
    frame.setVisible(true);

  }
}
