package org.plotting;

import org.jfree.data.xy.XYSeriesCollection;

public interface LinePlottable {
    XYSeriesCollection getPlotCollection(double min, double max, int n, String title);
}
