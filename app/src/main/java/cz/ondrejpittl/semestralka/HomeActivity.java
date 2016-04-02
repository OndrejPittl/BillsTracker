package cz.ondrejpittl.semestralka;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.sql.DatabaseMetaData;
import java.util.ArrayList;

import cz.ondrejpittl.semestralka.controllers.HomeDataController;
import cz.ondrejpittl.semestralka.controllers.HomeUIController;
import cz.ondrejpittl.semestralka.database.DBManager;
import cz.ondrejpittl.semestralka.models.Payment;

public class HomeActivity extends AppCompatActivity {

    /**
     * Shared Preferences of this app.
     */
    private SharedPreferences prefs;

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

        //build ui
        this.controllerUI = new HomeUIController(this);
        this.controllerUI.initUI();

        //model layer
        this.dbManager = new DBManager(this);

        //data
        this.controllerData = new HomeDataController(this);





        //store in-app local-shared preferences
        this.prefs = getSharedPreferences("cz.ondrejpittl.semestralka", MODE_PRIVATE);

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
