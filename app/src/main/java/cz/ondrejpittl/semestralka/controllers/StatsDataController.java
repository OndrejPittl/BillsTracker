package cz.ondrejpittl.semestralka.controllers;

import android.util.Log;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import java.lang.reflect.Array;
import java.util.ArrayList;

import cz.ondrejpittl.semestralka.StatisticsActivity;
import cz.ondrejpittl.semestralka.database.DBManager;
import cz.ondrejpittl.semestralka.models.Category;
import cz.ondrejpittl.semestralka.models.Payment;
import cz.ondrejpittl.semestralka.partial.StatisticsChartObject;

/**
 * Created by OndrejPittl on 16.04.16.
 */
public class StatsDataController {

    private StatisticsActivity activity;

    private StatsUIController controllerUI;

    private DBManager dbManager;



    public StatsDataController(StatisticsActivity activity) {
        this.activity = activity;
        this.dbManager = activity.getDbManager();
        this.controllerUI = activity.getControllerUI();
        this.loadStoredCategories();

        /*
            this.xData = new String[]{"Sony", "Huawei", "LG", "Apple", "Samsung"};
            this.yData =  new int[]{5, 10, 15, 30, 40};
        */
    }

    private void computeMonthLineStatsData(StatisticsChartObject data, ArrayList<Payment> payments){
        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<Entry> yVals = new ArrayList<>();

        float sum = 0;

        for(int i = 0; i < payments.size(); i++) {
            Payment p = payments.get(i);
            xVals.add(p.getDate().dayOfMonth().get() + "");

            sum += p.getAmount();
            yVals.add(new Entry(sum, i));
        }

        data.buildLineChartData(xVals, yVals);
    }

    private void computeMonthBarStatsData(StatisticsChartObject data, ArrayList<Payment> payments){
        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<BarEntry> yVals = new ArrayList<>();

        for(int i = 0; i < payments.size(); i++) {
            Payment p = payments.get(i);
            xVals.add(p.getDate().dayOfMonth().get() + "");
            yVals.add(new BarEntry(p.getAmount(), i));
        }

        data.buildBarChartData(xVals, yVals);
    }

    private void computeMonthPieStatsData(StatisticsChartObject data, ArrayList<Payment> payments){
        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<Entry> yVals = new ArrayList<>();

        for(int i = 0; i < payments.size(); i++) {
            Payment p = payments.get(i);
            xVals.add(p.getCategory().getName());
            yVals.add(new Entry(p.getAmount(), i));
        }

        data.buildPieChartData(xVals, yVals);
    }

    private void loadStoredCategories(){
        ArrayList<Category> categories = new ArrayList<>();
        categories.add(new Category(-1, "– all –"));
        categories.addAll(this.dbManager.getStoredCategories());
        this.controllerUI.buildCategoryControls(categories);
    }

    public StatisticsChartObject computeMonthStatsData(int month, int year, String category){

        //stats data
        StatisticsChartObject data = new StatisticsChartObject();

        //payments loaded from DB
        ArrayList<Payment> payments = this.dbManager.getPaymentsByMonth(month, year, category);

        this.computeMonthBarStatsData(data, payments);
        this.computeMonthLineStatsData(data, payments);


        payments = this.dbManager.getCategorySummaryByMonth(month, year, category);
        this.computeMonthPieStatsData(data, payments);



        return data;
    }

    public StatisticsChartObject computeYearStatsData(int month, int year, String category){

        //stats data
        StatisticsChartObject data = new StatisticsChartObject();

        //payments loaded from DB
        ArrayList<Payment> payments = this.dbManager.getPaymentsByYear(year, category);

        for (Payment p : payments)
            Log.i("Ondra-stats", "yearstats: " + p);

//        this.computeMonthBarStatsData(data, payments);
//        this.computeMonthLineStatsData(data, payments);
//
//
//        payments = this.dbManager.getCategorySummaryByMonth(month, year, category);
//        this.computeMonthPieStatsData(data, payments);



        return data;
    }









}




 /*String[] daysXData = new String[30];
        for (int i = 0; i < daysXData.length; i++) {
            daysXData[i] = String.valueOf(i + 1);
        }*/

        /*int[] yData = new int[5];
        for (int i = 0; i < yData.length; i++) {
            yData[i] = i * 2;
        }*/

//int[] yData = new int[]{200, 500, 169, 230, 891, 169, 1205};



        /*for(int i = 0; i < paymentCount; i++) {
            xVals.add(daysXData[i]);
        }*/

        /*xVals.add(daysXData[2]);
        xVals.add(daysXData[3]);
        xVals.add(daysXData[9]);
        xVals.add(daysXData[15]);
        xVals.add(daysXData[17]);
        xVals.add(daysXData[22]);
        xVals.add(daysXData[29]);*/



//(int) (Math.random() * 9 + 1);
        /*ArrayList<Entry> yVals = new ArrayList<>();
        for(int i = 0; i < paymentCount; i++) {
            //yVals.add(new Entry((int) (Math.random() * 9 + 1), i));
            yVals.add(new Entry(yData[i], i));
        }*/