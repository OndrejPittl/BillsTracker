package cz.ondrejpittl.semestralka.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Locale;

import cz.ondrejpittl.semestralka.HomeActivity;
import cz.ondrejpittl.semestralka.database.DBManager;
import cz.ondrejpittl.semestralka.models.Category;
import cz.ondrejpittl.semestralka.models.Payment;
import cz.ondrejpittl.semestralka.models.Statistics;
import cz.ondrejpittl.semestralka.models.Store;
import cz.ondrejpittl.semestralka.partial.InputFieldType;
import cz.ondrejpittl.semestralka.partial.JodaCalendar;
import cz.ondrejpittl.semestralka.partial.MonthChangeEnum;
import cz.ondrejpittl.semestralka.partial.SharedPrefs;

/**
 * Created by OndrejPittl on 01.04.16.
 */
public class HomeDataController {

    /**
     *  SharedPrefs holding app settings.
     *  Stores in-app local-shared preferences.
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
    private DateTime jodaDate;

    /**
     *  Calendar holding current REAL date.
     */
    private DateTime jodaDateCurrent;

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


    /**
     * Flag indicating a payment is being edited.
     */
    private boolean editingPayment;

    /**
     * ID of a payment being currently edited.
     */
    private int editingPaymentID;


    /**
     * Constructor. Initializes model layer of HomeActivity.
     * @param activity  HomeActivity reference
     */
    public HomeDataController(HomeActivity activity) {
        this.activity = activity;
        this.controllerUI = activity.getUIController();
        this.dbManager = activity.getDbManager();
        this.init();
    }

    /**
     * Initializes HomeActivity.
     */
    private void init(){
        this.jodaDate = new DateTime();
        this.jodaDateCurrent = new DateTime();

        this.editingPayment = false;
        this.prefs = this.activity.getSharedPreferences("cz.ondrejpittl.semestralka", Context.MODE_PRIVATE);

        //defaults
        this.loadStoredSettings();
        this.loadStoredCategories();
        this.loadStoredStores();

        //payments being displayed
        this.loadPaymentsOfMonth();
    }

    /**
     * Loads settings being stored in SharedPreferences.
     */
    private void loadStoredSettings(){
        this.loadDefaultCurrency();
    }

    /**
     * Loads default currency from SharedPrefs.
     * @return
     */
    private void loadDefaultCurrency(){
        String defaultCurrency = SharedPrefs.getDefaultCurrency();

        if(defaultCurrency == null || (defaultCurrency != null && defaultCurrency.length() <= 0)) {
            Locale l = Locale.getDefault();
            defaultCurrency = java.util.Currency.getInstance(l).getCurrencyCode();

            this.dbManager.updateDefaultCurrency(defaultCurrency);
            SharedPrefs.storeDefaultCurrency(defaultCurrency);
        }

        this.controllerUI.updateCurrencyViews(defaultCurrency);
    }

    /**
     *  Loads a list of stored stores from DB.
     */
    public void loadStoredStores(){
        this.storedStores = this.dbManager.getStoredStores();
        this.controllerUI.buildStoreControls(this.storedStores);
    }

    /**
     *  Loads a list of stored categories from DB.
     */
    public void loadStoredCategories(){
        this.storedCategories = this.dbManager.getStoredCategories();
        this.controllerUI.buildCategoryControls(this.storedCategories);
    }

    /**
     *  Loads a list of stored payments from DB.
     */
    public void loadPaymentsOfMonth(){

        HomeActivity.changeLocaleUS();
        int year = this.jodaDate.getYear();
        int month = this.jodaDate.getMonthOfYear();
        String monthStr = this.jodaDate.toString("MMMM");
        HomeActivity.changeLocaleDefault();

        String currencyUnits = SharedPrefs.getDefaultCurrency();

        this.displayedPayments = this.dbManager.getPaymentsByMonth(month, year);
        this.controllerUI.updatePaymentRecords(monthStr, year, this.displayedPayments, currencyUnits, this);

        this.logDisplayedPayments();
        this.computeStatistics();
    }

    /**
     * Creates a mock payment for a first-launch tutorial.
     */
    public void createMockPaymentIfNeeded(){
        int year = this.jodaDate.getYear();
        int month = this.jodaDate.getMonthOfYear();

        if(this.dbManager.isDBEmpty(month, year) && !SharedPrefs.wasTutorialDisplayed()) {
            this.dbManager.insertNewPayment(Payment.getMockPayment(month, year));
            this.loadPaymentsOfMonth();
        }
    }

    /**
     * Computes compact statistics placed in the footer of this activity.
     */
    private void computeStatistics(){
        Statistics s;

        if(JodaCalendar.compareMonthYear(jodaDateCurrent, jodaDate)){
            //if currently viewd month is a current calendar one
            s = this.dbManager.computeCurrentStatistics();
            this.controllerUI.updateStatistics(s, true);
        } else {
            int displayedM = this.jodaDate.getMonthOfYear(),
                displayedY = this.jodaDate.getYearOfEra();

            s = this.dbManager.computeOtherStatistics(displayedM, displayedY);
            this.controllerUI.updateStatistics(s, false);
        }
    }

    /**
     * Handles change of a currently viewed month.
     * @param event type of month change: increased/decreased
     */
    public void registerActiveMonthChanged(MonthChangeEnum event){
        switch (event) {
            case NEXT:
                this.jodaDate = this.jodaDate.plusMonths(1);
                break;

            case PREV:
            default:
                this.jodaDate = this.jodaDate.minusMonths(1);
                break;
        }
    }

    /**
     *
     */
    public void handlePaymentCancel(){
        this.controllerUI.clearControls();

        if(this.editingPayment){
            this.controllerUI.setInsertUpdateButtonText(false);
            this.controllerUI.setClearCancelButtonText(false);
            this.editingPayment = false;
        }

    }

    public void handleNewPaymentInsert(){
        String[] contents = this.controllerUI.getControlsContents();

        if(!validatePaymentInputs(contents))
            return;

        if(this.editingPayment) {

            //old payment is being editted
            Payment p = new Payment(contents);
            p.setID(this.editingPaymentID);

            this.controllerUI.registerPaymentUpdateFinished();
            this.dbManager.updatePayment(p);
            this.editingPayment = false;

        } else {

            //new payment is being inserted
            this.dbManager.insertNewPayment(new Payment(contents));

        }

        this.controllerUI.clearControls();
        this.loadPaymentsOfMonth();
    }

    public void handlePaymentDelete(int paymentID){
        this.dbManager.deletePayment(paymentID);
        this.loadPaymentsOfMonth();
    }

    public void handlePaymentEdit(int paymentID){
        this.editingPayment = true;
        this.editingPaymentID = paymentID;
        //this.controllerUI.fillPaymentInfoOnUpdate(p);


        //this.dbManager.updatePayment(paymentID);
        //this.loadPaymentsOfMonth();
    }

    private boolean validatePaymentInputs(String[] contents){
        boolean correct = true;

        //contents[0] ... date              (long)
        //being chosen via dialog with limited options – no way of invalid input

        //contents[1] ... category name     (String)
        //contents[2] ... category id       (int)
        if(contents[1].length() == 0) {
            this.controllerUI.markInputError(InputFieldType.CUSTOM_SPINNER_CATEGORY);
            correct = false;
        } else {
            this.controllerUI.unmarkInputError(InputFieldType.CUSTOM_SPINNER_CATEGORY);
        }

        //contents[3] ... amount            (int)
        //numeric keyboard, test with regex to eliminate multiple dots and commmas
        if(!isNumericPositive(contents[3]) || !isInAmountRange(contents[3])) {
            this.controllerUI.markInputError(InputFieldType.EDIT_TEXT_AMOUNT);
            correct = false;
        } else {
            this.controllerUI.unmarkInputError(InputFieldType.EDIT_TEXT_AMOUNT);
        }

        //contents[4] ... note              (String)
        //default keyboard, no limits = no need to test

        //contents[5] ... store name
        //contents[6] ... store id
        if(contents[5].length() == 0) {
            this.controllerUI.markInputError(InputFieldType.CUSTOM_SPINNER_STORE);
            correct = false;
        } else {
            this.controllerUI.unmarkInputError(InputFieldType.CUSTOM_SPINNER_STORE);
        }

        return correct;
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

    private boolean isInAmountRange(String num){
        Float f = Float.parseFloat(num);
        if(f < 0 || f > 10000000) return false;
        return true;
        //return num.matches("/^([1-9]\\d*|0)?(?:\\.\\d+)?$/");
    }

    public DateTime getViewedMonthDate(){
        return this.jodaDate;
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
