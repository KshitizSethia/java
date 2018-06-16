package org.sethia.utils.graphing;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.util.Strings;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO convert to builder to prevent adding data modifications after render

/**
 * references: https://www.tutorialspoint.com/jfreechart/jfreechart_line_chart.htm
 */
public final class LineGrapherJFreeChart extends LineGrapher {

  private static final Logger log = LoggerFactory.getLogger(LineGrapherJFreeChart.class);

  private final ApplicationFrame frame;
  private final TimeSeriesCollection seriesCollection;
  private final ConcurrentHashMap<String, TimeSeries> series;
  private final String graphTitle;
  private Double minY = Double.MAX_VALUE;
  private Double maxY = Double.MIN_VALUE;

  public LineGrapherJFreeChart(final String frameTitle,
      final String xAxisName,
      final String yAxisName) {
    super(frameTitle, xAxisName, yAxisName);
    frame = new ApplicationFrame(frameTitle);
    series = new ConcurrentHashMap<>();
    seriesCollection = new TimeSeriesCollection();

    this.graphTitle = Strings.EMPTY;
  }

  private void updateMinMax(Double yVal) {
    minY = Double.min(yVal, minY);
    maxY = Double.max(yVal, maxY);
  }

  public void addPoint(final Date date, final Number yValue, final String lineName) {
    log.trace("adding point - value: {}, date: {}, lineName: {}", yValue,
        date.toString(),
        lineName);
    series.putIfAbsent(lineName, new TimeSeries(lineName));

    series.get(lineName).add(new Day(date), yValue);
    updateMinMax((Double) yValue);
  }

  public void render() {
    series.values().forEach(xySeries -> seriesCollection.addSeries(xySeries));

    JFreeChart lineChart = ChartFactory
        .createTimeSeriesChart(graphTitle, xAxisName, yAxisName, seriesCollection,
            true, true, false);

    // display date axis properly
    XYPlot plot = (XYPlot) lineChart.getPlot();
    DateAxis axis = (DateAxis) plot.getDomainAxis();
    axis.setDateFormatOverride(new SimpleDateFormat("dd-MMM-yyyy"));

    // TODO space out x-axis (domain axis) ticks

    // scale graph vertically according to entries
    //ValueAxis axis = lineChart.getCategoryPlot().getRangeAxis(0);
    //axis.setRange(minY * 0.8, maxY * 1.05);

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
