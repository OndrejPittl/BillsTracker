package cz.ondrejpittl.semestralka.controllers;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import org.joda.time.DateTime;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import cz.ondrejpittl.semestralka.R;
import cz.ondrejpittl.semestralka.StatisticsActivity;
import cz.ondrejpittl.semestralka.layout.CustomSpinner;
import cz.ondrejpittl.semestralka.layout.StatsChart;
import cz.ondrejpittl.semestralka.models.Category;
import cz.ondrejpittl.semestralka.partial.JodaCalendar;
import cz.ondrejpittl.semestralka.partial.SharedPrefs;
import cz.ondrejpittl.semestralka.partial.StatisticsChartObject;

/**
 * Created by OndrejPittl on 16.04.16.
 */
public class StatsUIController {


    /**
     * Activity its UI is being controlled.
     */
    private StatisticsActivity activity;

    /**
     * Object inflating view defined as XML.
     */
    private LayoutInflater layoutInflater;


    private LinearLayout activeChartsContainer;
    private LinearLayout monthChartsContainer;
    private LinearLayout yearChartsContainer;

    private boolean portraitOrientation;

    private int chartHeight;
    private int[] chartPadding;



    /**
     * Date button firing datepicker dialog.
     */
    private Button dateButton;

    /**
     * DatePicker dialog ID.
     */
    private int dateDialogID = 0;



    private StatisticsChartObject monthStatsData;
    private StatisticsChartObject yearStatsData;



    /**
     * Constructor. Controller initialization.
     * @param activity  Activity its UI is being controlled.
     */
    public StatsUIController(StatisticsActivity activity){
        this.activity = activity;
        this.initUI();
    }

    /**
     * Initializes UI of Home activity.
     */
    private void initUI(){
        this.chartPadding = new int[2];
        this.portraitOrientation = isPortraitOrientation();
        this.layoutInflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.monthChartsContainer = (LinearLayout) this.activity.findViewById(R.id.monthChartsContainer);
        this.yearChartsContainer = (LinearLayout) this.activity.findViewById(R.id.yearChartsContainer);

        Log.i("Ondra-debugLand", "initializing UI controller.");


        this.registerDateButton();
        //this.updateChartHeight();

        if(!this.portraitOrientation) {
            //landscape
            this.customizeLandscapeOrientation();
        }
    }

    private void registerDateButton(){
        this.dateButton = (Button) this.activity.findViewById(R.id.statsDate);
        this.dateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //clearControlsFocus();
                activity.showDialog(dateDialogID);
            }
        });

        this.updateDateButton(new DateTime());
    }

    /*private void clearControlsFocus(){
        View v = this.activity.getCurrentFocus();

        if(v != null){
            InputMethodManager imm = (InputMethodManager) this.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            v.clearFocus();
        }
    }*/

    private void updateChartHeight(){
        LinearLayout container;

        if(this.portraitOrientation) {
            container = (LinearLayout) this.activity.findViewById(R.id.statsHeader);
        } else {
            container = (LinearLayout) this.activity.findViewById(R.id.statsControlsWrapper);
        }

        container.measure(View.MeasureSpec.makeMeasureSpec(getDislayWidthInPx(), View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(getDislayHeightInPx(), View.MeasureSpec.AT_MOST));

        float percetnage = 0.8f;

        if(this.monthStatsData.getRecordCount() < 2 || this.yearStatsData.getRecordCount() < 2) {
            percetnage = 0.6f;
        }

        this.chartHeight = (int) ((getDislayHeightInPx() - container.getMeasuredHeight()) * percetnage);


        //padding

        float complement = 0.8f - percetnage;

        //horizontal – currently not used
        this.chartPadding[0] = (int) (getDislayWidthInPx() * complement)/2;

        //vertical
        this.chartPadding[1] = (int) (getDislayHeightInPx() * complement)/3;
    }

    private void customizeLandscapeOrientation(){
        TextView lbl = (TextView) this.activity.findViewById(R.id.statisticsHeading);
        lbl.setVisibility(View.GONE);

        int p5 = (int) (getDislayWidthInPx() * 0.05),
            p2 = (int) (getDislayHeightInPx() * 0.02);
        LinearLayout wrapper = (LinearLayout) this.activity.findViewById(R.id.statsControlsWrapper);
        wrapper.setPadding(p5, p2, p5, p2);
    }


    public void drawStats(){
        /*for(int i = 0; i < 3; i++) {
            StatsChart chart = (StatsChart) layoutInflater.inflate(R.layout.stats_chart, monthChartsContainer, false);
            this.monthChartsContainer.addView(chart);
            chart.init();
            Log.i("Ondra-chart", "x");
        }*/

        this.updateChartHeight();
        this.drawMonthStats();
        this.drawYearStats();
    }

    private void drawMonthStats(){
        this.setActiveChartContanerMonth();
        this.monthChartsContainer.removeAllViews();

        TextView monthNoData = (TextView) this.activity.findViewById(R.id.statisticsMonthNoDataLabel);
        TextView monthLabel = (TextView) this.activity.findViewById(R.id.statisticsMonthHeadingLabel);
        monthLabel.setText(JodaCalendar.getMonthName(this.monthStatsData.getMonth()));

        if(this.monthStatsData.isEmpty()) {
            //Log.i("Ondra-stats", "NO DATA");
            monthNoData.setVisibility(View.VISIBLE);
        } else {
            //Log.i("Ondra-stats", "DATA");
            monthNoData.setVisibility(View.GONE);
            this.buildMonthBarChart(
                    this.activity.getString(R.string.statsMonthHeading1),
                    this.activity.getString(R.string.statsMonthDesc1)
            );
            this.buildMonthLineChart(
                    "",
                    this.activity.getString(R.string.statsMonthDesc2)
            );
            this.buildMonthWeekDayBarChart(
                    this.activity.getString(R.string.statsMonthHeading3),
                    this.activity.getString(R.string.statsMonthDesc3)
            );
            this.buildMonthPieChart(
                    this.activity.getString(R.string.statsMonthHeading4),
                    this.activity.getString(R.string.statsMonthDesc4)
            );
        }
    }

    private void drawYearStats(){
        this.setActiveChartContanerYear();
        this.yearChartsContainer.removeAllViews();

        TextView yearNoData = (TextView) this.activity.findViewById(R.id.statisticsYearNoDataLabel);

        TextView yearLabel = (TextView) this.activity.findViewById(R.id.statisticsYearHeadingLabel);
        yearLabel.setText(String.valueOf(this.yearStatsData.getYear()));



        if(this.yearStatsData.isEmpty()) {
            //Log.i("Ondra-stats", "y NO DATA");
            yearNoData.setVisibility(View.VISIBLE);
        } else {
            //Log.i("Ondra-stats", "y DATA");
            yearNoData.setVisibility(View.GONE);

            this.buildYearBarChart(
                    this.activity.getString(R.string.statsYearHeading1),
                    this.activity.getString(R.string.statsYearDesc1)
            );
            this.buildYearLineChart(
                    "",
                    this.activity.getString(R.string.statsYearDesc2)
            );
            this.buildYearWeekDayBarChart(
                    this.activity.getString(R.string.statsYearHeading3),
                    this.activity.getString(R.string.statsYearDesc3)
            );
            this.buildYearPieChart(
                    this.activity.getString(R.string.statsYearHeading4),
                    this.activity.getString(R.string.statsYearDesc4)
            );
        }

    }


    private StatsChart inflateChart(){
        StatsChart chart = (StatsChart) layoutInflater.inflate(R.layout.stats_chart, monthChartsContainer, false);
        this.activeChartsContainer.addView(chart);
        return chart;
    }

    private void buildBarChart(StatisticsChartObject data, String label, String desc){
        StatsChart chart = inflateChart();
        chart.setLabels(label, desc);
        chart.setData(data);
        chart.init(this.portraitOrientation, this.chartHeight, this.chartPadding);
        chart.buildBarChart();
    }

    private void buildDayWeekBarChart(StatisticsChartObject data, String label, String desc){
        StatsChart chart = inflateChart();
        chart.setLabels(label, desc);
        chart.setData(data);
        chart.init(this.portraitOrientation, this.chartHeight, this.chartPadding);
        chart.buildDayWeekBarChart();
    }

    private void buildLineChart(StatisticsChartObject data, String label, String desc){

        //no sense in this case
        if(data.getLineDataSet().getEntryCount() < 2) return;

        StatsChart chart = inflateChart();
        chart.setLabels(label, desc);
        chart.setData(data);
        chart.init(this.portraitOrientation, this.chartHeight, this.chartPadding);
        chart.buildLineChart();
    }

    private void buildPieChart(StatisticsChartObject data, String label, String desc){
        StatsChart chart = inflateChart();

        int alternateHeight = this.buildAlternativePieChart(data, chart);


        if(!data.isPieChartBuildable()) {
            chart.setLabels(label, "Sorry. Your payment data cannot be visualized.");
            chart.init(this.portraitOrientation, 0, (int)(alternateHeight*1.3), this.chartPadding);
            //chart.hideChart();
            //this.buildAlternativePieChart(data, chart);
        } else {
            chart.setLabels(label, desc);
            chart.setData(data);
            chart.init(this.portraitOrientation, this.chartHeight, alternateHeight, this.chartPadding);
            chart.buildPieChart();
        }

    }

    private int buildAlternativePieChart(StatisticsChartObject data, StatsChart chart){
        List<String> xData = data.getPieData().getXVals();
        ArrayList<Entry> yData = (ArrayList<Entry>) data.getPieDataSet().getYVals();
        Map<Float, String> items = new TreeMap<>();

        for (Entry e : yData) {
            items.put(e.getVal(), xData.get(e.getXIndex()));
        }

        int itemCount = items.size();
        LinearLayout itemsContainer = (LinearLayout) chart.findViewById(R.id.alternateChartWrapper);

        //for(Map.Entry<Float, String> item : items.entrySet()) {
        for(int i = itemCount - 1; i >= 0; i--) {
            Map.Entry<Float, String> item = (Map.Entry<Float, String>) items.entrySet().toArray()[i];
            LinearLayout record = (LinearLayout) layoutInflater.inflate(R.layout.alternative_pie_item, itemsContainer, false);
            TextView tvOrder = (TextView) record.findViewById(R.id.pieOrder),
                    tvCat = (TextView) record.findViewById(R.id.pieCategory),
                    tvVal = (TextView) record.findViewById(R.id.pieValue);

            tvOrder.setText((itemCount - i) + ".");
            tvCat.setText(item.getValue());
            tvVal.setText(item.getKey() + " " + SharedPrefs.getDefaultCurrency());
            itemsContainer.addView(record);

            Log.i("Ondra-pieeee", item.getValue());

        }

        return (itemCount + 2) * dpToPx(25);
    }

    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }


    private void buildMonthBarChart(String label, String desc){
        this.buildBarChart(this.monthStatsData, label, desc);
    }

    private void buildMonthWeekDayBarChart(String label, String desc){
        this.buildDayWeekBarChart(this.monthStatsData, label, desc);
    }

    private void buildYearWeekDayBarChart(String label, String desc){
        this.buildDayWeekBarChart(this.yearStatsData, label, desc);
    }

    private void buildMonthLineChart(String label, String desc){
        this.buildLineChart(this.monthStatsData, label, desc);
    }

    private void buildMonthPieChart(String label, String desc){
        this.buildPieChart(this.monthStatsData, label, desc);
    }

    private void buildYearBarChart(String label, String desc){
        this.buildBarChart(this.yearStatsData, label, desc);
    }

    private void buildYearLineChart(String label, String desc){
        this.buildLineChart(this.yearStatsData, label, desc);
    }

    private void buildYearPieChart(String label, String desc){
        this.buildPieChart(this.yearStatsData, label, desc);
    }




    private void setActiveChartContanerMonth(){
        this.activeChartsContainer = this.monthChartsContainer;
    }

    private void setActiveChartContanerYear(){
        this.activeChartsContainer = this.yearChartsContainer;
    }

    private void initCharts(){
        /*LinearLayout container = (LinearLayout) this.findViewById(R.id.monthChartsContainer);
        StatsChart chart = new StatsChart(this);*/

        /*//this.container.addView(chart);
        this.monthChartsContainer.addView(new StatsChart(this.activity));
        this.monthChartsContainer.setBackgroundColor(LTGRAY);*/

    }

    public void setMonthStatsData(StatisticsChartObject data){
        this.monthStatsData = data;
    }

    public void setYearStatsData(StatisticsChartObject data){
        this.yearStatsData = data;
    }



    /**
     * Updates date button label.
     * @param y Selected year.
     * @param m Selected month.
     * @param d Selected day.
     */
    private void updateDateButton(int y, int m, int d){

        dateButton.setText(JodaCalendar.getMonthName(m) + ", " + y);

    }

    private void updateDateButton(DateTime date){

        dateButton.setText(JodaCalendar.getMonthName(date.getMonthOfYear()) + ", " + date.getYearOfEra());

    }


    public Dialog handleDatePickerDialogCreation(int id, DateTime calendar){
        if(id == this.dateDialogID) {

            /*int y = calendar.get(Calendar.YEAR),
                m = calendar.get(Calendar.MONTH),
                d = calendar.get(Calendar.DAY_OF_MONTH);*/

            int y = calendar.getYearOfEra(),
                    m = calendar.getMonthOfYear(),
                    d = calendar.getDayOfMonth();

            Log.i("Ondra-stats", "handle-m: " + m);

            //temporarily set US locale to force being set CalendarDialog English language
            StatisticsActivity.changeLocaleUS();
            DatePickerDialog dpDialog = new DatePickerDialog(this.activity, new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker arg0, int y, int m, int d) {
                    /*Log.i("Ondra-stats", "selected-y: " + y);
                    Log.i("Ondra-stats", "selected-m: " + m);
                    Log.i("Ondra-stats", "selected-d: " + d);*/

                    updateDateButton(y, m+1, d);
                    activity.handleDisplayedDateChange(y, m+1, d);
                    StatisticsActivity.changeLocaleDefault();
                }
            }, y, m-1, d);

            return dpDialog;
        }


        return null;
    }


    public void buildCategoryControls(ArrayList<Category> categories){
        final CustomSpinner spinner = (CustomSpinner) this.activity.findViewById(R.id.statsCategory);
        spinner.init(this.activity, categories);
    }


    private boolean isPortraitOrientation(){
        int w = this.getDislayWidthInPx(),
            h = this.getDislayHeightInPx();

        return h > w;
    }

    private int getDislayWidthInPx(){
        DisplayMetrics dm = new DisplayMetrics();
        this.activity.getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels ;
    }

    private int getDislayHeightInPx(){
        DisplayMetrics dm = new DisplayMetrics();
        this.activity.getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels ;
    }

}
