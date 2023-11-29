package org.plotting;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
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

    public static JFreeChart createXYLineChart(String title, String xAxisLabel, String yAxisLabel, XYSeriesCollection... dataset) {
        XYSeriesCollection datasets = new XYSeriesCollection();

        for (XYSeriesCollection ds : dataset) {
            datasets.addSeries(ds.getSeries(0));
        }

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
}
