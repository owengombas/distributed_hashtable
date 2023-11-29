package org.plotting;

import org.jfree.data.statistics.HistogramDataset;

public interface HistogramPlottable {
    HistogramDataset getHistogramDataset(int n, String title);
}
