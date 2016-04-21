package cz.ondrejpittl.semestralka.controllers;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.ArrayList;

import cz.ondrejpittl.semestralka.R;
import cz.ondrejpittl.semestralka.StatisticsActivity;
import cz.ondrejpittl.semestralka.layout.StatsChart;
import cz.ondrejpittl.semestralka.models.Category;
import cz.ondrejpittl.semestralka.partial.JodaCalendar;
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


    private LinearLayout monthChartsContainer;
    private LinearLayout yearChartsContainer;

    private boolean portraitOrientation;

    private int chartHeight;

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
        this.portraitOrientation = isPortraitOrientation();
        this.layoutInflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.monthChartsContainer = (LinearLayout) this.activity.findViewById(R.id.monthChartsContainer);
        this.yearChartsContainer = (LinearLayout) this.activity.findViewById(R.id.yearChartsContainer);

        this.registerDateButton();
        this.updateChartHeight();

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
    }

    private void clearControlsFocus(){
        View v = this.activity.getCurrentFocus();

        if(v != null){
            InputMethodManager imm = (InputMethodManager) this.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            v.clearFocus();
        }
    }

    private void updateChartHeight(){
        LinearLayout container;

        if(this.portraitOrientation) {
            container = (LinearLayout) this.activity.findViewById(R.id.statsHeader);
        } else {
            container = (LinearLayout) this.activity.findViewById(R.id.statsControlsWrapper);
        }

        container.measure(View.MeasureSpec.makeMeasureSpec(getDislayWidthInPx(), View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(getDislayHeightInPx(), View.MeasureSpec.AT_MOST));

        this.chartHeight = (int) ((getDislayHeightInPx() - container.getMeasuredHeight()) * 0.9);
    }

    private void customizeLandscapeOrientation(){
        TextView lbl = (TextView) this.activity.findViewById(R.id.statisticsHeading);
        lbl.setVisibility(View.GONE);

        int p5 = (int) (getDislayWidthInPx() * 0.05),
            p2 = (int) (getDislayHeightInPx() * 0.02);
        LinearLayout wrapper = (LinearLayout) this.activity.findViewById(R.id.statsControlsWrapper);
        wrapper.setPadding(p5, p2, p5, p2);
    }


    public void buildStats(){
        /*for(int i = 0; i < 3; i++) {
            StatsChart chart = (StatsChart) layoutInflater.inflate(R.layout.stats_chart, monthChartsContainer, false);
            this.monthChartsContainer.addView(chart);
            chart.init();
            Log.i("Ondra-chart", "x");
        }*/

        this.buildMonthStats();

        //this.buildYearStats();


    }

    private void buildMonthStats(){
        this.monthChartsContainer.removeAllViews();

        TextView monthNoData = (TextView) this.activity.findViewById(R.id.statisticsMonthNoDataLabel);

        if(this.monthStatsData.isEmpty()) {
            //Log.i("Ondra-stats", "NO DATA");
            monthNoData.setVisibility(View.VISIBLE);
        } else {
            //Log.i("Ondra-stats", "DATA");
            monthNoData.setVisibility(View.GONE);
            this.buildMonthBarChart(null);
            this.buildMonthLineChart(null);
            this.buildMonthPieChart(null);
        }
    }

    private void buildYearStats(){
        this.yearChartsContainer.removeAllViews();

        TextView yearNoData = (TextView) this.activity.findViewById(R.id.statisticsYearNoDataLabel);

        if(this.monthStatsData.isEmpty()) {
            //Log.i("Ondra-stats", "y NO DATA");
            yearNoData.setVisibility(View.VISIBLE);
        } else {
            //Log.i("Ondra-stats", "y DATA");
            yearNoData.setVisibility(View.GONE);
            this.buildYearBarChart(null);
            /*this.buildYearLineChart(null);
            this.buildYearPieChart(null);*/
        }

    }


    private StatsChart inflateChart(){
        StatsChart chart = (StatsChart) layoutInflater.inflate(R.layout.stats_chart, monthChartsContainer, false);
        this.monthChartsContainer.addView(chart);
        return chart;
    }

    private void buildBarChart(StatisticsChartObject data, String label){
        StatsChart chart = inflateChart();
        chart.setLabel(label);
        chart.setData(data);
        chart.init(this.portraitOrientation, this.chartHeight);
        chart.buildBarChart();
    }

    private void buildLineChart(StatisticsChartObject data, String label){
        StatsChart chart = inflateChart();
        chart.setData(data);
        chart.init(this.portraitOrientation, this.chartHeight);
        chart.buildLineChart();
    }

    private void buildPieChart(StatisticsChartObject data, String label){
        StatsChart chart = inflateChart();
        chart.setLabel(label);
        chart.setData(data);
        chart.init(this.portraitOrientation, this.chartHeight);
        chart.buildPieChart();
    }

    private void buildMonthBarChart(String label){
        this.buildBarChart(this.monthStatsData, label);
    }

    private void buildMonthLineChart(String label){
        this.buildLineChart(this.monthStatsData, label);
    }

    private void buildMonthPieChart(String label){
        this.buildPieChart(this.monthStatsData, label);
    }

    private void buildYearBarChart(String label){
        this.buildBarChart(this.yearStatsData, label);
    }

    private void buildYearLineChart(String label){
        this.buildLineChart(this.yearStatsData, label);
    }

    private void buildYearPieChart(String label){
        this.buildPieChart(this.yearStatsData, label);
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
        DateTime now = new DateTime();
        DateTime selected = new DateTime().withYearOfEra(y).withMonthOfYear(m).withDayOfMonth(d);

        /*Log.i("Ondra-stats", "now: " + now);
        Log.i("Ondra-stats", "sel: " + selected);*/

        if(JodaCalendar.compareMonthYear(now, selected)) {
            if(JodaCalendar.compareDateTimes(now, selected)) {
                this.dateButton.setText("Today");
                return;
            } else if(JodaCalendar.compareDateTimes(now, JodaCalendar.clone(selected).plusDays(1))) {
                this.dateButton.setText("Yesterday");
                return;
            } else if(JodaCalendar.compareDateTimes(now, JodaCalendar.clone(selected).minusDays(1))) {
                this.dateButton.setText("Tomorrow");
                return;
            }
        }

        dateButton.setText(d + ". " + m + ". " + y);
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
        final Spinner spinner = (Spinner) this.activity.findViewById(R.id.statsCategory);
        ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(this.activity, R.layout.spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                activity.handleDisplayCategoryChange((Category) spinner.getSelectedItem());
            }

            public void onNothingSelected(AdapterView<?> parent) {}
        });

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
