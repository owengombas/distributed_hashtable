package org.plotting;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;

public class Plotting {
    /**
     * Display a set of plots (one per chart)
     *
     * @param charts The plots to display
     */
    public static void displayPlots(JFreeChart... charts) {
        JFrame frame = new JFrame();
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        for (JFreeChart chart : charts) {
            frame.add(new ChartPanel(chart));
        }
        frame.pack();
        frame.setVisible(true);
    }

    public static XYSeriesCollection mergeXYDatasets(XYSeriesCollection... datasets) {
        XYSeriesCollection mergedDataset = new XYSeriesCollection();
        for (XYSeriesCollection dataset : datasets) {
            for (int i = 0; i < dataset.getSeriesCount(); i++) {
                mergedDataset.addSeries(dataset.getSeries(i));
            }
        }
        return mergedDataset;
    }

    public static JFreeChart createXYLineChart(String title, String xAxisLabel, String yAxisLabel, XYSeriesCollection... dataset) {
        XYSeriesCollection datasets = mergeXYDatasets(dataset);
        return ChartFactory.createXYLineChart(
                title,
                xAxisLabel,
                yAxisLabel,
                datasets,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
    }

    public static JFreeChart createXYBarChart(String title, String xAxisLabel, String yAxisLabel, HistogramDataset dataset) {
        JFreeChart chart = ChartFactory.createHistogram(
                title,
                xAxisLabel,
                yAxisLabel,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Set the renderer to be a bar renderer
        XYBarRenderer renderer = new XYBarRenderer();
        chart.getXYPlot().setRenderer(renderer);

        return chart;
    }
}
