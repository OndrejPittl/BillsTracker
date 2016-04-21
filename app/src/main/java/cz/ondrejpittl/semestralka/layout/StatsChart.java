package cz.ondrejpittl.semestralka.layout;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.w3c.dom.Text;

import java.util.ArrayList;

import cz.ondrejpittl.semestralka.R;
import cz.ondrejpittl.semestralka.models.Statistics;
import cz.ondrejpittl.semestralka.partial.ChartType;
import cz.ondrejpittl.semestralka.partial.StatisticsChartObject;
import cz.ondrejpittl.semestralka.partial.StatisticsType;

/**
 * Created by OndrejPittl on 15.04.16.
 */
public class StatsChart extends LinearLayout {

    private FrameLayout container;

    private int chartHeight;

    private Chart chart;

    private boolean collapsed;


    private StatisticsChartObject data;



    public StatsChart(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public void init(boolean collapsed, int devHeight){

        /*switch (this.type) {
            case YEAR_SUMMARY:

                break;

            default:
            case MONTH_SUMMARY:
                break;
        }*/

        this.container = (FrameLayout) findViewById(R.id.chContainer);
        this.collapsed = collapsed;
        this.chartHeight = devHeight;
        this.updateChartHeight();

        Log.i("Ondra-chart", "chart init");
    }

    private void updateChartHeight(){
        ViewGroup.LayoutParams params = this.getLayoutParams();
        params.height = this.chartHeight;
        this.setLayoutParams(params);
    }

    public void setLabel(String label) {
        if(label == null) return;

        TextView lbl = (TextView) findViewById(R.id.txtViewChartLabel);
        lbl.setText(label);
    }

    public void setData(StatisticsChartObject data){
        this.data = data;
    }

    public void buildPieChart(String[] xData, int[] yData){
        /*this.xData = xData;
        this.yData = yData;

        this.chart = new PieChart(getContext());
        this.container.addView(this.chart);

        ArrayList<Entry> yVals = new ArrayList<>();
        for(int i = 0; i < yData.length; i++) {
            yVals.add(new Entry(yData[i], i));
        }

        ArrayList<String> xVals = new ArrayList<>();
        for(int i = 0; i < xData.length; i++) {
            xVals.add(xData[i]);
        }

        PieDataSet dataSet = new PieDataSet(yVals, "Market Share");
        dataSet.setSliceSpace(2);
        dataSet.setSelectionShift(5);


        ArrayList<Integer> colors = new ArrayList<>();
        for(int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for(int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for(int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for(int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for(int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());
        //colors.add(Color.WHITE);
        dataSet.setColors(colors);


        PieData data = new PieData(xVals, dataSet);
        //data.setValueFormatter(new PercentFormatter());

        chart.setData(data);
        chart.highlightValues(null);
        chart.invalidate();*/
    }

    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
    }

    public void buildLineChart(){
        LineChart chart = new LineChart(getContext());

        LineData data = this.data.getLineData();
        this.setLineChartData(data);
        this.setChart(chart);
        this.setLineChart(chart);
        this.setLineChartAxis(chart, collapsed);

        this.chart = chart;
        this.container.addView(this.chart);

        this.chart.setData(data);
        //this.chart.highlightValues(null);
        //this.chart.setHighlightEnabled(false);
        this.chart.invalidate();
    }


    //SELECT sum(tb_payments.amount) as amount, tb_categories.name, tb_categories.id FROM tb_payments inner join tb_categories on tb_payments.id_category = tb_categories.id WHERE tb_payments.date >= '1459461600000' AND tb_payments.date <= '1462053599999' group by tb_categories.id

    public void buildBarChart(){
        BarChart chart = new BarChart(getContext());

        BarData data = this.data.getBarData();
        this.setBarChartData(data);
        this.setChart(chart);
        //this.setBarChart(chart);
        this.setBarChartAxis(chart, collapsed);

        this.chart = chart;
        this.container.addView(this.chart);

        this.chart.setData(data);
        //this.chart.highlightValues(null);
        //this.chart.setHighlightEnabled(false);
        this.chart.invalidate();
    }

    public void buildPieChart() {
        PieChart chart = new PieChart(getContext());

        PieData data = this.data.getPieData();
        this.setPieChartData(data);
        this.setChart(chart);

        this.chart = chart;
        this.container.addView(this.chart);

        this.chart.setData(data);
        //this.chart.highlightValues(null);
        //this.chart.setHighlightEnabled(false);
        this.chart.invalidate();
    }


    private void setChart(Chart chart){
        chart.setDescription("");
        chart.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.transparent));
        chart.setHighlightPerTapEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setHardwareAccelerationEnabled(true);
    }

    private void setLineChart(LineChart chart) {
        chart.setBorderColor(ContextCompat.getColor(getContext(), R.color.appWhite));
        chart.setGridBackgroundColor(ContextCompat.getColor(getContext(), R.color.appWhite));
    }

    private void setBarChart(BarChart chart) {
        chart.setBorderColor(ContextCompat.getColor(getContext(), R.color.appWhite));
        chart.setGridBackgroundColor(ContextCompat.getColor(getContext(), R.color.appWhite));
    }



    private void setLineChartData(LineData data){
        data.setValueFormatter(null);
        data.setValueTextColor(ContextCompat.getColor(getContext(), R.color.appWhite));
        data.setValueTextSize(9);
        //data.getFirstLeft().setDrawValues(false);
        //data.getFirstRight().setDrawValues(false);

        if(collapsed) {
            //data.setDrawValues(false);
        }
    }

    private void setBarChartData(BarData data){
        data.setValueTextColor(ContextCompat.getColor(getContext(), R.color.appWhite));
        data.setValueTextSize(9);
    }

    private void setPieChartData(PieData data){
        data.setValueTextColor(ContextCompat.getColor(getContext(), R.color.appTextBlack));
        data.setValueTextSize(9);
    }


    //collapsed – true: portrait orientation –> yAxis left visible
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
            //lAxis.setShowOnlyMinMax(true);

            lAxis.setXOffset(0);
        }

        YAxis rAxis = chart.getAxisRight();
        rAxis.setEnabled(false);
        /*rAxis.setTextColor(ContextCompat.getColor(getContext(), R.color.appWhite));
        rAxis.setGridColor(ContextCompat.getColor(getContext(), R.color.appWhite));
        rAxis.setAxisLineColor(ContextCompat.getColor(getContext(), R.color.appWhite));*/
    }

    //collapsed – true: portrait orientation –> yAxis left visible
    private void setBarChartAxis(BarChart chart, boolean collapsed){
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





}
