package cz.ondrejpittl.semestralka;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import java.util.Locale;

import cz.ondrejpittl.semestralka.controllers.HomeDataController;
import cz.ondrejpittl.semestralka.controllers.HomeUIController;
import cz.ondrejpittl.semestralka.database.DBManager;
import cz.ondrejpittl.semestralka.partial.Designer;
import cz.ondrejpittl.semestralka.partial.MonthChangeEnum;
import cz.ondrejpittl.semestralka.partial.SharedPrefs;

public class HomeActivity extends AppCompatActivity {

    /**
     * Shared Preferences of this app.
     */
    //private SharedPreferences prefs;

    /**
     * Stored origin locale enables temporarily change locale to force ENG lang and then setting
     * origin locale back.
     *
     */
    private static Locale originLocale;

    /**
     * Model layer.
     * Database Manager representing a model layer od app. Completely controls database.
     */
    DBManager dbManager;

    /**
     * View Layer.
     * Controller building UI layout of this activity.
     */
    private HomeUIController controllerUI;

    /**
     * Logic layer.
     * Controls data displayed to user.
     */
    private HomeDataController controllerData;






    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Designer.setFullscreenActivity(this);
        setContentView(R.layout.activity_home);
        init();
    }

    protected void onResume() {
        super.onResume();
        Designer.updateDesign(this);
    }

    @Override
    protected void onDestroy() {
        Log.i("Ondra-mem", "Home destroyed.");
        super.onDestroy();
        Runtime.getRuntime().gc();
    }

    /**
     * Initializes Activity properties and major objects.
     */
    private void init(){
        Designer.updateDesign(this);
        this.originLocale = Locale.getDefault();

        //build ui
        this.controllerUI = new HomeUIController(this);
        this.controllerUI.initUI();

        //model layer
        this.dbManager = new DBManager(this);

        //data
        this.controllerData = new HomeDataController(this);

        //tutorial
        this.handleTutorialStart();
    }


    /**
     * Check if the database exist and can be read.
     *
     * @return true if it exists and can be read, false if it doesn't
     */
    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase("/data/data/YOUR_APP/databases/billstracker_database", null,
                    SQLiteDatabase.OPEN_READONLY);
            checkDB.close();
        } catch (SQLiteException e) {
            // database doesn't exist yet.
        }
        return checkDB != null;
    }

    /**
     * Builds DatePicker dialog.
     * @param id    ID of dialog.
     * @return      Built DatePicker dialog.
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        return this.controllerUI.handleDatePickerDialogCreation(id);
    }

    /**
     * Handles actual month change.
     */
    public void activeMonthChangeHandler(View v){
        MonthChangeEnum event;
        Button b = (Button) v;

        if(b.getId() == R.id.btn_recordsListPrev) {
            event = MonthChangeEnum.PREV;
        } else {
            event = MonthChangeEnum.NEXT;
        }

        this.controllerData.registerActiveMonthChanged(event);
        this.updatePaymentRecords();
    }

    private void updatePaymentRecords(){
        this.controllerData.loadPaymentsOfMonth();
    }

    public void handleClearEvent(View v){

        this.controllerData.handlePaymentCancel();
    }

    public void handleInsertEvent(View v){

        this.controllerData.handleNewPaymentInsert();
    }

    public void startStatisticsActivity(View v){
        /*this.controllerUI.hideImgButton(v);
        HomeActivity.clearControlsFocus(this);
        Intent i = new Intent(this, StatisticsActivity.class);
        startActivity(i);*/
    }

    public void startSettingsActivity(View v){
        //this.controllerUI.hideImgButton(v);
        HomeActivity.clearControlsFocus(this);

        Intent i = new Intent(this, SettingsActivity.class);
        //startActivity(i);
        startActivityForResult(i, 1);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //this.controllerUI.showLastImgButton();

        if(requestCode == 1) {

            //settings activity ended

            if(SharedPrefs.isFirstTimeLaunch())
                    finish();


            this.controllerUI.resetSettingsImgButton();

            this.controllerUI.updateCurrencyViews(SharedPrefs.getDefaultCurrency());
            this.controllerUI.updateCategoryControlsSelection();
            this.controllerUI.updateStoreControlsSelection();

            this.updatePaymentRecords();
            this.handleTutorialStart();


        } else if(requestCode == 2) {

            //stats activity ended
            this.controllerUI.resetStatsImgButton();

        }
    }

    private void handleTutorialStart(){
        if(!SharedPrefs.wasTutorialDisplayed()) {
            this.controllerData.createMockPaymentIfNeeded();
            this.controllerUI.startTutorial();
        }
    }





    public static void clearControlsFocus(Activity activity){
        View v = activity.getCurrentFocus();

        if(v != null){
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            v.clearFocus();
        }
    }

    public static void changeLocaleDefault(){
        Locale.setDefault(HomeActivity.originLocale);
    }

    public static void changeLocaleUS(){
        Locale.setDefault(Locale.US);
    }

    public HomeDataController getDataController(){
        return this.controllerData;
    }

    public HomeUIController getUIController(){
        return this.controllerUI;
    }

    public DBManager getDbManager(){
        return this.dbManager;
    }




}
