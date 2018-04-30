package org.sethia.utils;

import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

// TODO convert to builder to prevent adding data modifications after render

/**
 * references: https://www.tutorialspoint.com/jfreechart/jfreechart_line_chart.htm
 */
public class Grapher {

  private final ApplicationFrame frame;
  private final DefaultCategoryDataset dataset;
  private final String graphTitle;
  private Double minY = Double.MAX_VALUE;
  private Double maxY = Double.MIN_VALUE;

  public Grapher(final String frameTitle, final String graphTitle) {
    frame = new ApplicationFrame(frameTitle);
    dataset = new DefaultCategoryDataset();
    this.graphTitle = graphTitle;
  }

  private void updateMinMax(Double yVal) {
    minY = Double.min(yVal, minY);
    maxY = Double.max(yVal, maxY);
  }

  /**
   * @param points instance of {@link Tuple} with (x,y) as point. i.e. x = {@link Tuple#getLeft()}
   * and y = {@link Tuple#getRight()}
   * @param title name of the line which will be drawn on the graph
   */
  public void addLineGraph(List<Tuple<Double, Double>> points, final String title) {
    for (Tuple<Double, Double> tuple : points) {
      Double xVal = tuple.getLeft();
      Double yVal = tuple.getRight();
      dataset.addValue(yVal, title, xVal);
      updateMinMax(yVal);
    }
  }

  public void render() {
    JFreeChart lineChart = ChartFactory
        .createLineChart(graphTitle, "Time", "Value", dataset, PlotOrientation.VERTICAL, true, true,
            false);

    ValueAxis axis = lineChart.getCategoryPlot().getRangeAxis(0);
    axis.setRange(minY * 0.95, maxY * 1.05);
    ChartPanel chartPanel = new ChartPanel(lineChart);
    frame.setContentPane(chartPanel);
    frame.pack();
    RefineryUtilities.centerFrameOnScreen(frame);
    frame.setVisible(true);
  }
}
