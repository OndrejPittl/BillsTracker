package cz.ondrejpittl.semestralka.controllers;

import android.util.Log;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

import cz.ondrejpittl.semestralka.StatisticsActivity;
import cz.ondrejpittl.semestralka.database.DBManager;
import cz.ondrejpittl.semestralka.models.Category;
import cz.ondrejpittl.semestralka.models.Payment;
import cz.ondrejpittl.semestralka.partial.JodaCalendar;
import cz.ondrejpittl.semestralka.partial.StatisticsChartObject;

/**
 * Created by OndrejPittl on 16.04.16.
 */
public class StatsDataController {

    /**
     * UIController reference.
     */
    private StatsUIController controllerUI;

    /**
     * DB managing object reference.
     */
    private DBManager dbManager;


    /**
     * A controller. Initializes a model layer of this activity.
     * @param activity  StatisticsActivity
     */
    public StatsDataController(StatisticsActivity activity) {
        this.dbManager = activity.getDbManager();
        this.controllerUI = activity.getControllerUI();
        this.loadStoredCategories();
    }

    /**
     * Computes month line-chart data.
     * @param data  statistics object container
     * @param payments  payment list
     */
    private void computeMonthLineStatsData(StatisticsChartObject data, ArrayList<Payment> payments){
        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<Entry> yVals = new ArrayList<>();

        float[] amounts = computeDayAmounts(data, payments);

        float sum = 0;
        int index = 0;

        for(int i = 0; i < amounts.length; i++) {
            float amount = amounts[i];
            if(amount == 0) continue;


            xVals.add(JodaCalendar.getDayLabeled(i + 1));

            sum += amount;
            yVals.add(new Entry(sum, index++));
        }

        data.buildLineChartData(xVals, yVals);
    }

    /**
     * Summarizes payment amounts day by day.
     * @param data      statistics container
     * @param payments  list of payments
     * @return          summarized amounts
     */
    private float[] computeDayAmounts(StatisticsChartObject data, ArrayList<Payment> payments){
        int dayCount = JodaCalendar.getDayCountInMonth(data.getMonth());
        float[] amounts = new float[dayCount];

        for(int i = 0; i < payments.size(); i++) {
            Payment p = payments.get(i);

            int paymentDay = p.getDate().getDayOfMonth();
            float amount = p.getAmount();

            amounts[paymentDay - 1] += amount;
        }

        return amounts;
    }

    /**
     * Summarizes payment amounts day (in a week) by day.
     * @param payments  list of payments
     * @return  summarized payment amounts
     */
    private float[] computeDayWeekAmounts(ArrayList<Payment> payments){
        int dayCount = 7;
        float[] amounts = new float[dayCount];

        for(int i = 0; i < payments.size(); i++) {
            Payment p = payments.get(i);

            int m = p.getDate().getMonthOfYear(),
                d = p.getDate().getDayOfMonth(),
                    day = JodaCalendar.getWeekDayNum(m, d);

            amounts[day-1] += p.getAmount();
        }

        return amounts;
    }

    /**
     * Summarizes amounts of a year.
     * @param payments  list of payments
     * @return  summarized amounts
     */
    private float[] computeYearAmounts(ArrayList<Payment> payments){
        float[] amounts = new float[]{ 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f };

        for(int i = 0; i < payments.size(); i++) {
            Payment p = payments.get(i);

            int paymentMonth = p.getDate().getMonthOfYear();
            float amount = p.getAmount();

            amounts[paymentMonth - 1] += amount;
        }

        return amounts;
    }

    /**
     * Computes month bar-chart data.
     * @param data  statistics container
     * @param payments  payment list
     */
    private void computeMonthDayMonthBarData(StatisticsChartObject data, ArrayList<Payment> payments){
        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<BarEntry> yVals = new ArrayList<>();

        float[] amounts = computeDayAmounts(data, payments);

        int index = 0;
        for(int i = 0; i < amounts.length; i++) {
            float amount = amounts[i];
            if(amount == 0) continue;

            xVals.add(JodaCalendar.getDayLabeled(i + 1));
            yVals.add(new BarEntry(amount, index++));
        }

        data.buildBarChartData(xVals, yVals);
    }

    /**
     * Computes month bar-chart data.
     * @param data  statistics container
     * @param payments  payment list
     */
    private void computeDayWeekBarData(StatisticsChartObject data, ArrayList<Payment> payments){
        String[] days;
        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<BarEntry> yVals = new ArrayList<>();

        float[] amounts = computeDayWeekAmounts(payments);


        int notZero = 0;
        for(int i = 0; i < amounts.length; i++) {
            if(amounts[i] > 0) notZero++;
        }

        if(notZero > 4) {
            days = JodaCalendar.buildDayWeekArray(true);
        } else {
            days = JodaCalendar.buildDayWeekArray(false);
        }


        int index = 0;
        for(int i = 0; i < amounts.length; i++) {
            if(amounts[i] <= 0) continue;

            String d = days[i];
            xVals.add(d);
            yVals.add(new BarEntry(amounts[i], index++));
        }

        data.buildWeekDayBarChartData(xVals, yVals);
    }

    /**
     * Computes month pie-chart data.
     * @param data  statistics container
     * @param payments  payment list
     */
    private void computePieStatsData(StatisticsChartObject data, ArrayList<Payment> payments){
        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<Entry> yVals = new ArrayList<>();

        for(int i = 0; i < payments.size(); i++) {
            Payment p = payments.get(i);
            xVals.add(p.getCategory().getName());
            yVals.add(new Entry(p.getAmount(), i));
        }

        data.buildPieChartData(xVals, yVals);
    }

    /**
     * Computes year bar-chart data.
     * @param data  statistics container
     * @param payments  payment list
     */
    private void computeYearMonthBarData(StatisticsChartObject data, ArrayList<Payment> payments){
        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<BarEntry> yVals = new ArrayList<>();


        float[] amounts = this.computeYearAmounts(payments);


        StatisticsActivity.changeLocaleUS();

        int index = 0;
        for(int i = 0; i < amounts.length; i++) {
            float amount = amounts[i];
            if(amount == 0) continue;

            xVals.add(JodaCalendar.getMonthName(i + 1));
            yVals.add(new BarEntry(amount, index++));
        }

        StatisticsActivity.changeLocaleDefault();

        data.buildBarChartData(xVals, yVals);
    }

    /**
     * Computes year line-chart data.
     * @param data  statistics container
     * @param payments  payment list
     */
    private void computeYearLineStatsData(StatisticsChartObject data, ArrayList<Payment> payments){
        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<Entry> yVals = new ArrayList<>();

        float[] amounts = this.computeYearAmounts(payments);

        float sum = 0;
        int index = 0;

        for(int i = 0; i < amounts.length; i++) {
            float amount = amounts[i];
            if(amount == 0) continue;

            xVals.add(JodaCalendar.getMonthName(i + 1));

            sum += amount;
            yVals.add(new Entry(sum, index++));
        }

        data.buildLineChartData(xVals, yVals);
    }

    /**
     * Loads stored categories.
     */
    private void loadStoredCategories(){
        ArrayList<Category> categories = new ArrayList<>();
        categories.add(new Category(-1, "– all –"));
        categories.addAll(this.dbManager.getStoredCategories());
        this.controllerUI.buildCategoryControls(categories);
    }

    /**
     * Computes month statistics data.
     * @param month     month
     * @param year      year
     * @param category  category
     * @return  parametrized statistics data
     */
    public StatisticsChartObject computeMonthStatsData(int month, int year, String category){

        Log.i("Ondra-stats", "Computing month stats: ");

        //stats data
        StatisticsChartObject data = new StatisticsChartObject();
        data.setMonth(month);

        //payments loaded from DB
        ArrayList<Payment> payments = this.dbManager.getPaymentsByMonth(month, year, category);

        this.computeMonthDayMonthBarData(data, payments);
        this.computeMonthLineStatsData(data, payments);
        this.computeDayWeekBarData(data, payments);

        payments = this.dbManager.getCategorySummaryByMonth(month, year, category);
        this.computePieStatsData(data, payments);

        return data;
    }

    /**
     * Computes year statistics data.
     * @param year      year
     * @param category  category
     * @return  computed stats data
     */
    public StatisticsChartObject computeYearStatsData(int year, String category){

        Log.i("Ondra-stats", "Computing year stats: ");

        //stats data
        StatisticsChartObject data = new StatisticsChartObject();

        data.setYear(year);

        //payments loaded from DB
        ArrayList<Payment> payments = this.dbManager.getPaymentsByYear(year, category);

        this.computeYearMonthBarData(data, payments);
        this.computeYearLineStatsData(data, payments);
        this.computeDayWeekBarData(data, payments);

        payments = this.dbManager.getCategorySummaryByYear(year, category);
        this.computePieStatsData(data, payments);

        return data;
    }
}