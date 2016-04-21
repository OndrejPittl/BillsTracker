package cz.ondrejpittl.semestralka;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import android.widget.Button;

import java.util.Locale;

import cz.ondrejpittl.semestralka.controllers.HomeDataController;
import cz.ondrejpittl.semestralka.controllers.HomeUIController;
import cz.ondrejpittl.semestralka.database.DBManager;
import cz.ondrejpittl.semestralka.partial.MonthChangeEnum;

public class HomeActivity extends AppCompatActivity {

    /**
     * Shared Preferences of this app.
     */
    private SharedPreferences prefs;

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
        setContentView(R.layout.activity_home);

        init();
    }

    /**
     * Initializes Activity properties and major objects.
     */
    private void init(){

        this.originLocale = Locale.getDefault();

        //build ui
        this.controllerUI = new HomeUIController(this);
        this.controllerUI.initUI();

        //model layer
        this.dbManager = new DBManager(this);

        //data
        this.controllerData = new HomeDataController(this);


        //this.controllerData.loadStoredSettings();

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
    }

    public void handleClearEvent(View v){

        this.controllerData.handlePaymentCancel();
    }

    public void handleInsertEvent(View v){

        this.controllerData.handleNewPaymentInsert();
    }

    public void startStatisticsActivity(View v){
        Intent i = new Intent(this, StatisticsActivity.class);
        startActivity(i);
    }



    private void loadPayments(){




        /*

        LinearLayout container = (LinearLayout) findViewById(R.id.recordsContainer);

        for(int i = 0; i < 5; i++) {
            View rec = (View) layoutInflater.inflate(R.layout.payment_record, container, false);

            rec.setId(i);

            TextView v = (TextView) rec.findViewById(R.id.txtViewNote);
            v.setText(i);

            container.addView(rec);
        }

        */

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
