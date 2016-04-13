package cz.ondrejpittl.semestralka.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.FloatRange;
import android.util.Log;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import cz.ondrejpittl.semestralka.HomeActivity;
import cz.ondrejpittl.semestralka.database.DBManager;
import cz.ondrejpittl.semestralka.models.Category;
import cz.ondrejpittl.semestralka.models.Payment;
import cz.ondrejpittl.semestralka.models.Statistics;
import cz.ondrejpittl.semestralka.models.Store;
import cz.ondrejpittl.semestralka.partial.JodaCalendar;
import cz.ondrejpittl.semestralka.partial.MonthChange;

/**
 * Created by OndrejPittl on 01.04.16.
 */
public class HomeDataController {

    //store in-app local-shared preferences
    /**
     *  SharedPrefs holding app settings.
     */
    SharedPreferences prefs;

    /**
     * Activity reference.
     */
    private HomeActivity activity;

    /**
     * View layer.
     * UI Controller reference.
     */
    private HomeUIController controllerUI;

    /**
     *  Model layer.
     *  DB Manager reference.
     */
    private DBManager dbManager;

    /**
     * Calendar holding actually displayed month.
     */
    //private Calendar actualCalendar;

    private DateTime jodaDate;

    /**
     *  Number of month being displayed.
     */
    //private int displayedMonth;


    /**
     * Collection of stored Stores in database.
     * This collection is being injected into view layer and being displayed
     * to user via store offer in spinner.
     */
    private ArrayList<Store> storedStores;

    /**
     * Collection of stored Categories in database.
     * This collection is being injected into view layer and being displayed
     * to user via category offer in spinner.
     */
    private ArrayList<Category> storedCategories;

    /**
     * Collection of stored Payments of actual month in database.
     * This collection is being injected into view layer and being displayed
     * to user as a list of payments of actual month.
     */
    private ArrayList<Payment> displayedPayments;



    public HomeDataController(HomeActivity activity) {
        this.activity = activity;
        this.controllerUI = activity.getUIController();
        this.dbManager = activity.getDbManager();

        init();
    }

    private void init(){
        this.jodaDate = new DateTime();
        this.prefs = this.activity.getSharedPreferences("cz.ondrejpittl.semestralka", Context.MODE_PRIVATE);

        //defaults
        this.loadStoredSettings();

        this.loadStoredCategories();
        this.loadStoredStores();


        this.loadPaymentsOfMonth();



    }

    private void loadStoredSettings(){
        this.loadDefaultCurrency();


    }

    private String loadDefaultCurrencyFromPrefs(){
        return this.prefs.getString("defaultCurrency", "");
    }

    private void loadDefaultCurrency(){
        String defaultCurrency = loadDefaultCurrencyFromPrefs();

        if(defaultCurrency == null || (defaultCurrency != null && defaultCurrency.length() <= 0)) {
            //defaultCurrency = this.dbManager.getDefaultCurrency();

            Locale l = Locale.getDefault();
            defaultCurrency = java.util.Currency.getInstance(l).getCurrencyCode();

            this.dbManager.updateDefaultCurrency(defaultCurrency);
            this.prefs.edit().putString("defaultCurrency", defaultCurrency).commit();
        }

        this.controllerUI.updateCurrencyViews(defaultCurrency);
    }

    private void loadStoredStores(){
        this.storedStores = this.dbManager.getStoredStores();
        this.controllerUI.buildStoreControls(this.storedStores);
    }

    private void loadStoredCategories(){
        this.storedCategories = this.dbManager.getStoredCategories();
        this.controllerUI.buildCategoryControls(this.storedCategories);
    }

    private void loadPaymentsOfMonth(){
        //app launches with actual month displayed by default

        HomeActivity.changeLocaleUS();
        int year = this.jodaDate.getYear();
        int month = this.jodaDate.getMonthOfYear();
        String monthStr = this.jodaDate.toString("MMMM");
        HomeActivity.changeLocaleDefault();


        String currencyUnits = loadDefaultCurrencyFromPrefs();
        this.displayedPayments = this.dbManager.getPaymentsByMonth(month, year);
        this.controllerUI.updatePayments(monthStr, year, this.displayedPayments, currencyUnits, this);

        logDisplayedPayments();
        computeStatistics();
    }

    private void computeStatistics(){
        Statistics s = this.dbManager.computeStatistics();
        this.controllerUI.updateStatistics(s);
    }


    public void registerActiveMonthChanged(MonthChange event){
        switch (event) {
            case NEXT:
                this.jodaDate = this.jodaDate.plusMonths(1);
                break;

            case PREV:
            default:
                this.jodaDate = this.jodaDate.minusMonths(1);
                break;
        }

        Log.i("Ondra", "mesicek: " + this.jodaDate.getMonthOfYear());

        this.loadPaymentsOfMonth();
    }

    public void registerNewPaymentInsert(){
        String[] contents = this.controllerUI.getControlsContents();

        if(!validatePaymentInputs(contents))
            return;

        this.dbManager.insertNewPayment(new Payment(contents));
        this.controllerUI.clearControls();
        this.loadPaymentsOfMonth();
    }

    public void registerPaymentDelete(int paymentID){
        this.dbManager.deletePayment(paymentID);
        this.loadPaymentsOfMonth();
    }

    private boolean validatePaymentInputs(String[] contents){
        //contents[0] ... date              (long)
        //being chosen via dialog with limited options – no way of invalid input

        //contents[1] ... category name     (String)
        //contents[2] ... category id       (int)
        //being chosen via spinner with limited options – no way of invalid input

        //contents[3] ... amount            (int)
        //numeric keyboard, test with regex to eliminate multiple dots and commmas

        //contents[4] ... note              (String)
        //default keyboard, no limits = no need to test

        //contents[0] ... store name
        //contents[0] ... store id
        //being chosen via spinner with limited options – no way of invalid input


        if(!isNumericPositive(contents[3])) {
            this.controllerUI.markInputAmountError();
            return false;
        }

        return true;
    }

    private boolean isNumericPositive(String num){
        try {
            Float f = Float.parseFloat(num);
            if(f < 0) return false;

        } catch (NumberFormatException ex) {
            return false;
        }

        return true;
        //return num.matches("/^([1-9]\\d*|0)?(?:\\.\\d+)?$/");
    }




    private void logDisplayedPayments(){
        Log.i("Ondra", "loggggggggggggggggging displayed:");
        for(Payment p : this.displayedPayments) {
            Log.i("Ondra", p.toString());
        }
    }

    private void logStoredStores(){
        for(Store s : this.storedStores) {
            Log.i("Ondra", s.toString());
        }
    }

}
