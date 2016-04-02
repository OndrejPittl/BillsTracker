package cz.ondrejpittl.semestralka.controllers;

import android.util.Log;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Calendar;

import cz.ondrejpittl.semestralka.HomeActivity;
import cz.ondrejpittl.semestralka.R;
import cz.ondrejpittl.semestralka.database.DBManager;
import cz.ondrejpittl.semestralka.models.Category;
import cz.ondrejpittl.semestralka.models.Payment;
import cz.ondrejpittl.semestralka.models.Store;

/**
 * Created by OndrejPittl on 01.04.16.
 */
public class HomeDataController {

    private HomeActivity activity;

    private HomeUIController controllerUI;

    private DBManager dbManager;

    private LinearLayout paymentRecordsContainer;




    private Calendar actualCalendar;

    private int displayedMonth;


    private ArrayList<Store> storedStores;
    private ArrayList<Category> storedCategories;

    private ArrayList<Payment> displayedPayments;



    public HomeDataController(HomeActivity activity) {
        this.activity = activity;
        this.controllerUI = activity.getUIController();
        this.dbManager = activity.getDbManager();

        init();
    }

    private void init(){
        this.actualCalendar = Calendar.getInstance();

        this.loadStoredCategories();
        this.loadStoredStores();


        this.loadPaymentsOfMonth();



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

        int year = this.actualCalendar.get(Calendar.YEAR);
        this.displayedMonth = this.actualCalendar.get(Calendar.MONTH);

        this.displayedPayments = this.dbManager.getPaymentsByMonth(this.displayedMonth, year);
        //this.logDisplayedPayments();



        this.controllerUI.buildPaymentBoxes(this.displayedPayments);

    }




    private void logDisplayedPayments(){
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
