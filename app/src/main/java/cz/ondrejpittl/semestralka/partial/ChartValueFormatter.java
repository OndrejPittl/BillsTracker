package cz.ondrejpittl.semestralka.partial;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * Created by OndrejPittl on 01.05.16.
 */
public class ChartValueFormatter implements ValueFormatter {

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return value + "  " + SharedPrefs.getDefaultCurrency();
    }

}