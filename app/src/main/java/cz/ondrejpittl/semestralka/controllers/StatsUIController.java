package cz.ondrejpittl.semestralka.controllers;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cz.ondrejpittl.semestralka.R;
import cz.ondrejpittl.semestralka.StatisticsActivity;
import cz.ondrejpittl.semestralka.layout.CustomSpinner;
import cz.ondrejpittl.semestralka.layout.StatsChart;
import cz.ondrejpittl.semestralka.models.Category;
import cz.ondrejpittl.semestralka.partial.Designer;
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

    /**
     * Active charts container.
     */
    private LinearLayout activeChartsContainer;

    /**
     * Month charts container.
     */
    private LinearLayout monthChartsContainer;

    /**
     * Year charts contaner.
     */
    private LinearLayout yearChartsContainer;

    /**
     * Flag differing device orientation.
     */
    private boolean portraitOrientation;

    /**
     * Chart height.
     */
    private int chartHeight;

    /**
     * Chart padding.
     */
    private int[] chartPadding;

    /**
     * Date button firing datepicker dialog.
     */
    private Button dateButton;

    /**
     * DatePicker dialog ID.
     */
    private int dateDialogID = 0;

    /**
     * Month statistics data.
     */
    private StatisticsChartObject monthStatsData;

    /**
     * Year statistics data.
     */
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
        this.registerDateButton();

        if(!this.portraitOrientation) {
            //landscape
            this.customizeLandscapeOrientation();
        }
    }

    /**
     * Builds date button.
     */
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

    /**
     * Updates height of charts.
     */
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

    /**
     * Changes coming with landscape orientation.
     */
    private void customizeLandscapeOrientation(){
        TextView lbl = (TextView) this.activity.findViewById(R.id.statisticsHeading);
        lbl.setVisibility(View.GONE);

        int p5 = (int) (getDislayWidthInPx() * 0.05),
            p2 = (int) (getDislayHeightInPx() * 0.02);
        LinearLayout wrapper = (LinearLayout) this.activity.findViewById(R.id.statsControlsWrapper);
        wrapper.setPadding(p5, p2, p5, p2);
    }

    /**
     * Draws statistic charts.
     */
    public void drawStats(){
        this.updateChartHeight();
        this.drawMonthStats();
        this.drawYearStats();
    }

    /**
     * Draws month charts.
     */
    private void drawMonthStats(){
        this.setActiveChartContainerMonth();
        this.monthChartsContainer.removeAllViews();

        TextView monthNoData = (TextView) this.activity.findViewById(R.id.statisticsMonthNoDataLabel);
        TextView monthLabel = (TextView) this.activity.findViewById(R.id.statisticsMonthHeadingLabel);
        monthLabel.setText(JodaCalendar.getMonthName(this.monthStatsData.getMonth()));

        if(this.monthStatsData.isEmpty()) {
            monthNoData.setVisibility(View.VISIBLE);
        } else {
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

    /**
     * Draws year charts.
     */
    private void drawYearStats(){
        this.setActiveChartContainerYear();
        this.yearChartsContainer.removeAllViews();

        TextView yearNoData = (TextView) this.activity.findViewById(R.id.statisticsYearNoDataLabel);

        TextView yearLabel = (TextView) this.activity.findViewById(R.id.statisticsYearHeadingLabel);
        yearLabel.setText(String.valueOf(this.yearStatsData.getYear()));


        if(this.yearStatsData.isEmpty()) {
            yearNoData.setVisibility(View.VISIBLE);
        } else {
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

    /**
     * Includes a new xml-defined view.
     * @return  view reference
     */
    private StatsChart inflateChart(){
        StatsChart chart = (StatsChart) layoutInflater.inflate(R.layout.stats_chart, monthChartsContainer, false);
        this.activeChartsContainer.addView(chart);
        return chart;
    }

    /**
     * Builds bar chart.
     * @param data  stats data
     * @param label chart heading
     * @param desc  chart description
     */
    private void buildBarChart(StatisticsChartObject data, String label, String desc){
        StatsChart chart = inflateChart();
        chart.setLabels(label, desc);
        chart.setData(data);
        chart.init(this.portraitOrientation, this.chartHeight, this.chartPadding);
        chart.buildBarChart();
    }

    /**
     * Builds day (in week) bar chart.
     * @param data  stats data
     * @param label chart heading
     * @param desc  chart description
     */
    private void buildDayWeekBarChart(StatisticsChartObject data, String label, String desc){
        StatsChart chart = inflateChart();
        chart.setLabels(label, desc);
        chart.setData(data);
        chart.init(this.portraitOrientation, this.chartHeight, this.chartPadding);
        chart.buildDayWeekBarChart();
    }

    /**
     * Builds line chart.
     * @param data  stats data
     * @param label chart heading
     * @param desc  chart description
     */
    private void buildLineChart(StatisticsChartObject data, String label, String desc){
        //no sense in this case
        if(data.getLineDataSet().getEntryCount() < 2) return;

        StatsChart chart = inflateChart();
        chart.setLabels(label, desc);
        chart.setData(data);
        chart.init(this.portraitOrientation, this.chartHeight, this.chartPadding);
        chart.buildLineChart();
    }

    /**
     * Builds pie chart.
     * @param data  stats data
     * @param label chart heading
     * @param desc  chart description
     */
    private void buildPieChart(StatisticsChartObject data, String label, String desc){
        StatsChart chart = inflateChart();

        int alternateHeight = this.buildAlternativePieChart(data, chart);

        if(!data.isPieChartBuildable()) {
            chart.setLabels(label, "Sorry. Your payment data cannot be visualized.");
            chart.init(this.portraitOrientation, 0, (int)(alternateHeight*1.3), this.chartPadding);
        } else {
            chart.setLabels(label, desc);
            chart.setData(data);
            chart.init(this.portraitOrientation, this.chartHeight, alternateHeight, this.chartPadding);
            chart.buildPieChart();
        }
    }

    /**
     * Builds alternative pie chart/legend.
     * @param data  stats data
     * @param chart stats chart
     * @return
     */
    private int buildAlternativePieChart(StatisticsChartObject data, StatsChart chart){
        List<String> xData = data.getPieData().getXVals();
        ArrayList<Entry> yData = (ArrayList<Entry>) data.getPieDataSet().getYVals();
        Map<Float, String> items = new TreeMap<>();

        for (Entry e : yData) {
            items.put(e.getVal(), xData.get(e.getXIndex()));
        }

        int itemCount = items.size();
        LinearLayout itemsContainer = (LinearLayout) chart.findViewById(R.id.alternateChartWrapper);

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
        }

        return (itemCount + 2) * Designer.dpToPx(25, this.activity);
    }

    /**
     * Builds month bar chart.
     * @param label label
     * @param desc  description
     */
    private void buildMonthBarChart(String label, String desc){
        this.buildBarChart(this.monthStatsData, label, desc);
    }

    /**
     * Builds month bar chart.
     * @param label label
     * @param desc  description
     */
    private void buildMonthWeekDayBarChart(String label, String desc){
        this.buildDayWeekBarChart(this.monthStatsData, label, desc);
    }


    /**
     * Builds month line chart.
     * @param label label
     * @param desc  description
     */
    private void buildMonthLineChart(String label, String desc){
        this.buildLineChart(this.monthStatsData, label, desc);
    }

    /**
     * Builds month pie chart.
     * @param label label
     * @param desc  description
     */
    private void buildMonthPieChart(String label, String desc){
        this.buildPieChart(this.monthStatsData, label, desc);
    }

    /**
     * Builds year bar chart.
     * @param label label
     * @param desc  description
     */
    private void buildYearWeekDayBarChart(String label, String desc){
        this.buildDayWeekBarChart(this.yearStatsData, label, desc);
    }

    /**
     * Builds year bar chart.
     * @param label label
     * @param desc  description
     */
    private void buildYearBarChart(String label, String desc){
        this.buildBarChart(this.yearStatsData, label, desc);
    }

    /**
     * Builds year line chart.
     * @param label label
     * @param desc  description
     */
    private void buildYearLineChart(String label, String desc){
        this.buildLineChart(this.yearStatsData, label, desc);
    }

    /**
     * Builds year pie chart.
     * @param label label
     * @param desc  description
     */
    private void buildYearPieChart(String label, String desc){
        this.buildPieChart(this.yearStatsData, label, desc);
    }

    /**
     * Sets active month chart container.
     */
    private void setActiveChartContainerMonth(){
        this.activeChartsContainer = this.monthChartsContainer;
    }

    /**
     * Sets active year chart container.
     */
    private void setActiveChartContainerYear(){
        this.activeChartsContainer = this.yearChartsContainer;
    }

    /**
     * Sets month statistics data.
     * @param data  stats data.
     */
    public void setMonthStatsData(StatisticsChartObject data){
        this.monthStatsData = data;
    }

    /**
     * Sets year statistics data.
     * @param data  stats data.
     */
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

    /**
     * Updates date button.
     * @param date  date
     */
    private void updateDateButton(DateTime date){
        dateButton.setText(JodaCalendar.getMonthName(date.getMonthOfYear()) + ", " + date.getYearOfEra());
    }

    /**
     * Handles date picker dialog creation.
     * @param id        dialog id
     * @param calendar  date
     * @return          dialog
     */
    public Dialog handleDatePickerDialogCreation(int id, DateTime calendar){
        if(id == this.dateDialogID) {
            int y = calendar.getYearOfEra(),
                    m = calendar.getMonthOfYear(),
                    d = calendar.getDayOfMonth();

            Log.i("Ondra-stats", "handle-m: " + m);

            //temporarily set US locale to force being set CalendarDialog English language
            StatisticsActivity.changeLocaleUS();
            DatePickerDialog dpDialog = new DatePickerDialog(this.activity, new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker arg0, int y, int m, int d) {
                    updateDateButton(y, m+1, d);
                    activity.handleDisplayedDateChange(y, m+1, d);
                    StatisticsActivity.changeLocaleDefault();
                }
            }, y, m-1, d);

            return dpDialog;
        }

        return null;
    }

    /**
     * Builds category control element.
     * @param categories    list of categories
     */
    public void buildCategoryControls(ArrayList<Category> categories){
        final CustomSpinner spinner = (CustomSpinner) this.activity.findViewById(R.id.statsCategory);
        spinner.init(this.activity, categories);
    }

    /**
     * Determines whether is portait/landscape orientation.
     * @return  true – is portrait, false – landscape
     */
    private boolean isPortraitOrientation(){
        int w = this.getDislayWidthInPx(),
            h = this.getDislayHeightInPx();

        return h > w;
    }

    /**
     * Gets display witdh.
     * @return  display width
     */
    private int getDislayWidthInPx(){
        DisplayMetrics dm = new DisplayMetrics();
        this.activity.getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels ;
    }

    /**
     * Gets display height.
     * @return  display width
     */
    private int getDislayHeightInPx(){
        DisplayMetrics dm = new DisplayMetrics();
        this.activity.getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels ;
    }
}
