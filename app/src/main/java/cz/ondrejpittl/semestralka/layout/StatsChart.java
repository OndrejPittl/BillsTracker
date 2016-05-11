package cz.ondrejpittl.semestralka.layout;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.PieData;

import cz.ondrejpittl.semestralka.R;
import cz.ondrejpittl.semestralka.partial.ChartValueFormatter;
import cz.ondrejpittl.semestralka.partial.StatisticsChartObject;

/**
 * Created by OndrejPittl on 15.04.16.
 */
public class StatsChart extends LinearLayout {

    /**
     * Chart container.
     */
    private FrameLayout container;

    /**
     * Chart padding.
     */
    private int[] chartPadding;

    /**
     * Chart reference.
     */
    private Chart chart;

    /**
     * Collapsed/expanded flag.
     */
    private boolean collapsed;

    /**
     * Statistics container holding charts data.
     */
    private StatisticsChartObject data;


    /**
     * A constructor. Basics initialization.
     * @param context   an activity context reference
     * @param attrs     xml attributes
     */
    public StatsChart(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    /**
     * Initialization.
     * @param collapsed         a collapsed/expanded flag
     * @param devHeight         a device height
     * @param alternateHeight   an alternate chart height
     * @param chartPadding      a chart padding
     */
    public void init(boolean collapsed, int devHeight, int alternateHeight, int[] chartPadding){
        this.container = (FrameLayout) findViewById(R.id.chContainer);
        this.collapsed = collapsed;
        this.chartPadding = chartPadding;
        this.updateChartDimensions(devHeight, alternateHeight);
    }

    /**
     * Initialization.
     * @param collapsed     a collapsed/expanded flag
     * @param devHeight     a device height
     * @param chartPadding  a chart padding
     */
    public void init(boolean collapsed, int devHeight, int[] chartPadding){
        this.init(collapsed, devHeight, 0, chartPadding);
    }

    /**
     * Updates chart dimensions.
     * @param chartHeight       a chart height
     * @param alternateHeight   an alternate chart height
     */
    private void updateChartDimensions(int chartHeight, int alternateHeight){
        ViewGroup.LayoutParams chParams = this.getLayoutParams();
        chParams.height = chartHeight + alternateHeight;
        this.setLayoutParams(chParams);

        LinearLayout alter = (LinearLayout) this.findViewById(R.id.alternateChartWrapper);
        ViewGroup.LayoutParams alParams = alter.getLayoutParams();
        alParams.height = alternateHeight;
        alter.setLayoutParams(alParams);
    }

    /**
     * Sets texts of a chart.
     * @param label a label
     * @param desc  a description
     */
    public void setLabels(String label, String desc) {
        if(label == null) return;

        TextView lbl = (TextView) findViewById(R.id.txtViewChartLabel);
        lbl.setText(label);

        TextView ds = (TextView) findViewById(R.id.txtViewChartDesc);
        ds.setText(desc);
    }

    /**
     * Sets s chart padding.
     */
    private void setChartPadding(){
        this.setPadding(0, this.chartPadding[1], 0, this.chartPadding[1]);
    }

    /**
     * Sets a data collection.
     * @param data  data collection
     */
    public void setData(StatisticsChartObject data){
        this.data = data;
    }

    /**
     * Builds a line chart.
     */
    public void buildLineChart(){
        LineChart chart = new LineChart(getContext());

        LineData data = this.data.getLineData();
        this.setLineChartData(data);
        this.setChart(chart);
        this.setLineChart(chart);
        this.setLineChartAxis(chart, collapsed);

        this.chart = chart;
        this.container.addView(this.chart);
        this.setChartPadding();

        this.chart.setData(data);
        this.chart.invalidate();
    }

    /**
     * Builds a bar chart.
     */
    public void buildBarChart(){
        BarChart chart = new BarChart(getContext());

        BarData data = this.data.getBarData();
        this.setBarChartData(data);
        this.setChart(chart);
        this.setBarChartAxis(chart, collapsed);

        this.chart = chart;
        this.container.addView(this.chart);
        this.setChartPadding();

        this.chart.setData(data);
        this.chart.invalidate();
    }

    /**
     * Builds a day-week bar chart.
     */
    public void buildDayWeekBarChart(){
        BarChart chart = new BarChart(getContext());

        BarData data = this.data.getDayWeekBarData();
        this.setBarChartData(data);
        this.setChart(chart);
        this.setBarChartAxis(chart, collapsed);

        this.chart = chart;
        this.container.addView(this.chart);
        this.setChartPadding();

        this.chart.setData(data);
        this.chart.invalidate();
    }

    /**
     * Builds a pie chart.
     */
    public void buildPieChart() {
        PieChart chart = new PieChart(getContext());

        PieData data = this.data.getPieData();
        this.setPieChartData(data);
        this.setChart(chart);

        this.chart = chart;
        this.container.addView(this.chart);

        this.chart.setData(data);
        this.chart.invalidate();
    }

    /**
     * Chart initialization.
     */
    private void setChart(Chart chart){
        chart.setDescription("");
        chart.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.transparent));
        chart.setHighlightPerTapEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setHardwareAccelerationEnabled(true);
    }

    /**
     * Line chart modification.
     * @param chart chart reference
     */
    private void setLineChart(LineChart chart) {
        chart.setBorderColor(ContextCompat.getColor(getContext(), R.color.appWhite));
        chart.setGridBackgroundColor(ContextCompat.getColor(getContext(), R.color.appWhite));
    }

    /**
     * Line chart data modification.
     * @param data chart data
     */
    private void setLineChartData(LineData data){
        data.setValueFormatter(null);
        data.setValueTextColor(ContextCompat.getColor(getContext(), R.color.appWhite));
        data.setValueTextSize(9);
    }

    /**
     * Bar chart data modification.
     * @param data chart data
     */
    private void setBarChartData(BarData data){
        data.setValueTextColor(ContextCompat.getColor(getContext(), R.color.appWhite));
        data.setValueTextSize(9);
        data.setValueFormatter(new ChartValueFormatter());
    }

    /**
     * Pie chart data modification.
     * @param data chart data
     */
    private void setPieChartData(PieData data){
        data.setValueTextColor(ContextCompat.getColor(getContext(), R.color.appTextBlack));
        data.setValueTextSize(9);
    }

    /**
     * Line chart axis configuration.
     * @param chart     chart reference
     * @param collapsed collapsed flag
     */
    private void setLineChartAxis(LineChart chart, boolean collapsed){
        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(ContextCompat.getColor(getContext(), R.color.appWhite));
        xAxis.setAxisLineColor(ContextCompat.getColor(getContext(), R.color.appWhite));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setXOffset(10);

        YAxis lAxis = chart.getAxisLeft();
        lAxis.setTextColor(ContextCompat.getColor(getContext(), R.color.appWhite));
        lAxis.setGridColor(ContextCompat.getColor(getContext(), R.color.appWhite));
        lAxis.setAxisLineColor(ContextCompat.getColor(getContext(), R.color.appWhite));
        lAxis.setXOffset(15);

        if(collapsed) {
            lAxis.setDrawGridLines(false);
            lAxis.setDrawAxisLine(false);
            lAxis.setDrawLabels(false);
            lAxis.setXOffset(0);
        }

        YAxis rAxis = chart.getAxisRight();
        rAxis.setEnabled(false);
    }

    /**
     * Bar chart axis configuration.
     * @param chart     chart reference
     * @param collapsed collapsed flag
     */
    private void setBarChartAxis(BarChart chart, boolean collapsed) {
        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(ContextCompat.getColor(getContext(), R.color.appWhite));
        xAxis.setAxisLineColor(ContextCompat.getColor(getContext(), R.color.appWhite));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setXOffset(10);

        YAxis lAxis = chart.getAxisLeft();
        lAxis.setTextColor(ContextCompat.getColor(getContext(), R.color.appWhite));
        lAxis.setGridColor(ContextCompat.getColor(getContext(), R.color.appWhite));
        lAxis.setAxisLineColor(ContextCompat.getColor(getContext(), R.color.appWhite));
        lAxis.setXOffset(15);

        if (collapsed) {
            lAxis.setDrawGridLines(false);
            lAxis.setDrawAxisLine(false);
            lAxis.setDrawLabels(false);
            lAxis.setXOffset(0);
        }

        YAxis rAxis = chart.getAxisRight();
        rAxis.setEnabled(false);
    }
}
