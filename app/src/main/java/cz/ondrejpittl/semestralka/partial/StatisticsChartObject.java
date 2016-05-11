package cz.ondrejpittl.semestralka.partial;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.util.ArrayList;

import cz.ondrejpittl.semestralka.R;


/**
 * Created by OndrejPittl on 19.04.16.
 */
public class StatisticsChartObject {

    /**
     * List of colors.
     */
    ArrayList<Integer> colors;

    /**
     * Min count of xVals;
     */
    private int count;

    /**
     * Month of stored statistics.
     */
    private int month;

    /**
     * Year of stored statistics.
     */
    private int year;

    /**
     * Line chart data.
     */
    private LineData lineData;

    /**
     * Line chart dataset.
     */
    private LineDataSet lineDataSet;

    /**
     * Bar chart data.
     */
    private BarData barData;

    /**
     * Bar chart dataset.
     */
    private BarDataSet barDataSet;

    /**
     * Bar chart data – weekday.
     */
    private BarData dayWeekBarData;

    /**
     * Bar chart dataset – weekday.
     */
    private BarDataSet dayWeekBarDataSet;

    /**
     * Was checked whether is pie chart buildable?
     */
    private boolean isPieChartBuildableChecked;

    /**
     * Is it possible to build pie chart?
     */
    private boolean isPieChartBuildable;

    /**
     * Pie chart data.
     */
    private PieData pieData;

    /**
     * Pie chart dataset.
     */
    private PieDataSet pieDataSet;

    /**
     *  Constructor. Basics initialization.
     */
    public StatisticsChartObject() {
        this.colors = new ArrayList<>();
        this.colors.add(Color.WHITE);
        this.count = 0;
        this.isPieChartBuildable = false;
        this.isPieChartBuildableChecked = false;
    }

    /**
     * Is any chart empty?
     * @return  true – it is, false – not
     */
    public boolean isEmpty(){
        return this.count == 0;
    }

    /**
     * Getter of xvals record count.
     * @return  record count
     */
    public int getRecordCount(){
        return this.count;
    }

    /**
     * Builds line chart data collection.
     * @param xVals x-values
     * @param yVals y-values
     */
    public void buildLineChartData(ArrayList<String> xVals, ArrayList<Entry> yVals){
        this.count = xVals.size();

        this.lineDataSet = new LineDataSet(yVals, "");
        this.lineDataSet.setColors(this.colors);
        this.lineData = new LineData(xVals, lineDataSet);
        this.lineData.setHighlightEnabled(false);
    }

    /**
     * Builds bar chart data collection.
     * @param xVals x-values
     * @param yVals y-values
     */
    public void buildBarChartData(ArrayList<String> xVals, ArrayList<BarEntry> yVals){
        this.count = xVals.size();

        this.barDataSet = new BarDataSet(yVals, "");
        this.barDataSet.setColors(this.colors);
        this.barData = new BarData(xVals, this.barDataSet);
        this.barData.setHighlightEnabled(false);
    }

    /**
     * Builds bar chart data collection – weekday.
     * @param xVals x-values
     * @param yVals y-values
     */
    public void buildWeekDayBarChartData(ArrayList<String> xVals, ArrayList<BarEntry> yVals){
        this.count = xVals.size();

        this.dayWeekBarDataSet = new BarDataSet(yVals, "");
        this.dayWeekBarDataSet.setColors(this.colors);
        this.dayWeekBarData = new BarData(xVals, this.dayWeekBarDataSet);
        this.dayWeekBarData.setHighlightEnabled(false);
    }

    /**
     * Builds pie chart data collection.
     * @param xVals x-values
     * @param yVals y-values
     */
    public void buildPieChartData(ArrayList<String> xVals, ArrayList<Entry> yVals){
        this.count = xVals.size();

        this.pieDataSet = new PieDataSet(yVals, "");
        this.pieDataSet.setColors(this.colors);
        this.pieDataSet.setSliceSpace(5);
        this.pieDataSet.setSelectionShift(3);
        this.pieData = new PieData(xVals, this.pieDataSet);
        this.pieData.setHighlightEnabled(false);
    }


    /**
     * Getter of data of a bar chart.
     * @return bar chart data
     */
    public BarData getBarData(){
        return this.barData;
    }

    /**
     * Getter of data of a line chart.
     * @return line chart data
     */
    public LineData getLineData() {
        return lineData;
    }

    /**
     * Getter of dataset of a line chart.
     * @return line chart dataset
     */
    public LineDataSet getLineDataSet() {
        return lineDataSet;
    }

    /**
     * Getter of data of a pie chart.
     * @return pie chart data
     */
    public PieData getPieData() {
        return pieData;
    }

    /**
     * Getter of dataset of a pie chart.
     * @return pie chart dataset
     */
    public PieDataSet getPieDataSet() {
        return pieDataSet;
    }

    /**
     * Getter of statistics month.
     * @return  month of the stats data
     */
    public int getMonth() {
        return month;
    }

    /**
     * Setter of a month of statistics data.
     * @param month month of stats data
     */
    public void setMonth(int month) {
        this.month = month;
    }

    /**
     * Getter of statistics year.
     * @return  year of the stats data
     */
    public int getYear() {
        return year;
    }

    /**
     * Setter of a year of statistics data.
     * @param year year of stats data
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * Getter of a weekday of stats data.
     * @return  weekday
     */
    public BarData getDayWeekBarData() {
        return dayWeekBarData;
    }

    /**
     * Check whether is a pie chart buildable or not.
     * @return  true – pie chart is possible to build, false – not
     */
    public boolean isPieChartBuildable(){
        if(this.isPieChartBuildableChecked)
            return this.isPieChartBuildable;

        float min = this.pieData.getYMin(),
              sum = this.pieData.getYValueSum();

        return (sum/min) < 35;
    }

}
