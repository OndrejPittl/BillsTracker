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

    ArrayList<Integer> colors;

    private int count;

    private int month;
    private int year;

    private LineData lineData;
    private LineDataSet lineDataSet;

    private BarData barData;
    private BarDataSet barDataSet;

    private BarData dayWeekBarData;
    private BarDataSet dayWeekBarDataSet;

    private boolean isPieChartBuildableChecked;
    private boolean isPieChartBuildable;
    private PieData pieData;
    private PieDataSet pieDataSet;




    public StatisticsChartObject() {
        this.colors = new ArrayList<>();
        this.colors.add(Color.WHITE);
        this.count = 0;
        this.isPieChartBuildable = false;
        this.isPieChartBuildableChecked = false;
    }

    public boolean isEmpty(){
        return this.count == 0;
    }

    public int getRecordCount(){
        return this.count;
    }

    public void buildLineChartData(ArrayList<String> xVals, ArrayList<Entry> yVals){
        this.count = xVals.size();

        this.lineDataSet = new LineDataSet(yVals, "");
        this.lineDataSet.setColors(this.colors);
        this.lineData = new LineData(xVals, lineDataSet);
        this.lineData.setHighlightEnabled(false);
    }

    public void buildBarChartData(ArrayList<String> xVals, ArrayList<BarEntry> yVals){
        this.count = xVals.size();

        this.barDataSet = new BarDataSet(yVals, "");
        this.barDataSet.setColors(this.colors);
        this.barData = new BarData(xVals, this.barDataSet);
        this.barData.setHighlightEnabled(false);
    }

    public void buildWeekDayBarChartData(ArrayList<String> xVals, ArrayList<BarEntry> yVals){
        this.count = xVals.size();

        this.dayWeekBarDataSet = new BarDataSet(yVals, "");
        this.dayWeekBarDataSet.setColors(this.colors);
        this.dayWeekBarData = new BarData(xVals, this.dayWeekBarDataSet);
        this.dayWeekBarData.setHighlightEnabled(false);
    }

    public void buildPieChartData(ArrayList<String> xVals, ArrayList<Entry> yVals){
        this.count = xVals.size();

        this.pieDataSet = new PieDataSet(yVals, "");
        this.pieDataSet.setColors(this.colors);
        this.pieDataSet.setSliceSpace(5);
        this.pieDataSet.setSelectionShift(3);
        this.pieData = new PieData(xVals, this.pieDataSet);
        this.pieData.setHighlightEnabled(false);
    }

    public BarDataSet getBarDataSet(){
        return this.barDataSet;
    }

    public BarData getBarData(){
        return this.barData;
    }

    public LineData getLineData() {
        return lineData;
    }

    public void setLineData(LineData lineData) {
        this.lineData = lineData;
    }

    public LineDataSet getLineDataSet() {
        return lineDataSet;
    }

    public void setLineDataSet(LineDataSet lineDataSet) {
        this.lineDataSet = lineDataSet;
    }

    public PieData getPieData() {
        return pieData;
    }

    public void setPieData(PieData pieData) {
        this.pieData = pieData;
    }

    public PieDataSet getPieDataSet() {
        return pieDataSet;
    }

    public void setPieDataSet(PieDataSet pieDataSet) {
        this.pieDataSet = pieDataSet;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public BarData getDayWeekBarData() {
        return dayWeekBarData;
    }

    public void setDayWeekBarData(BarData dayWeekBarData) {
        this.dayWeekBarData = dayWeekBarData;
    }

    public BarDataSet getDayWeekBarDataSet() {
        return dayWeekBarDataSet;
    }

    public void setDayWeekBarDataSet(BarDataSet dayWeekBarDataSet) {
        this.dayWeekBarDataSet = dayWeekBarDataSet;
    }

    public boolean isPieChartBuildable(){
        if(this.isPieChartBuildableChecked)
            return this.isPieChartBuildable;

        float min = this.pieData.getYMin(),
              sum = this.pieData.getYValueSum();

        Log.i("Ondra-pie", "piable: " + (sum/min));
        return (sum/min) < 35;
    }

}
