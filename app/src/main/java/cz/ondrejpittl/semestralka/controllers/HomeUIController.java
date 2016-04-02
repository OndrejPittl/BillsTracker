package cz.ondrejpittl.semestralka.controllers;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;

import cz.ondrejpittl.semestralka.R;
import cz.ondrejpittl.semestralka.models.Category;
import cz.ondrejpittl.semestralka.models.Payment;
import cz.ondrejpittl.semestralka.models.Store;

/**
 * Created by OndrejPittl on 30.03.16.
 */
public class HomeUIController {

    /**
     * Activity its UI is being controlled.
     */
    private Activity activity;

    /**
     * Object inflating view defined as XML.
     */
    private LayoutInflater layoutInflater;


    LinearLayout paymentRecordsContainer;


    private SharedPreferences prefs;

    /**
     * Calendar singleton object reference with actual date.
     */
    private Calendar cal;

    /**
     * Date button firing datepicker dialog.
     */
    private Button dateButton;

    /**
     * DatePicker dialog ID.
     */
    private int dateDialogID = 0;

    /**
     * Selected year.
     */
    private int date_year;

    /**
     * Selected month.
     */
    private int date_month;

    /**
     * Selected day.
     */
    private int date_day;








    /**
     * Constructor. Controller initialization.
     * @param activity  Activity its UI is being controlled.
     */
    public HomeUIController(Activity activity){
        this.activity = activity;
    }

    /**
     * Initializes UI of Home activity.
     */
    public void initUI(){
        this.cal = Calendar.getInstance();
        this.prefs = this.activity.getSharedPreferences("cz.ondrejpittl.semestralka", Context.MODE_PRIVATE);
        this.layoutInflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.paymentRecordsContainer = (LinearLayout) this.activity.findViewById(R.id.recordsContainer);

        buildControlPanel();




        resetVisitOnDoubleTap();
    }

    /**
     * Builds control panel.
     */
    private void buildControlPanel(){
        this.buildDateControls();
        //this.buildCategoryControls();
        //this.buildStoreControls();
    }


    public void buildPaymentBoxes(ArrayList<Payment> payments){
        //paymentRecordsContainer

        for(Payment p : payments) {
            View record = (View) layoutInflater.inflate(R.layout.payment_record, paymentRecordsContainer, false);

            //payment ID
            record.setId(p.getID());

            //payment day
            TextView d = (TextView) record.findViewById(R.id.txtViewRecordDay);
            Calendar cal = Calendar.getInstance();
            cal.setTime(p.getDate());
            d.setText(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));

            //payment category name
            TextView c = (TextView) record.findViewById(R.id.txtViewRecordCategory);
            c.setText(p.getCategory().getName());

            //payment amount
            TextView a = (TextView) record.findViewById(R.id.txtViewRecordPrice);
            a.setText(String.valueOf(p.getAmount()));

            //paymentNote
            TextView v = (TextView) record.findViewById(R.id.txtViewRecordNote);
            v.setText(p.getNote());

            paymentRecordsContainer.addView(record);
        }


    }




    /**
     * Initializes date controls represented with button and DatePicker.
     */
    private void buildDateControls(){
        this.date_year = this.cal.get(Calendar.YEAR);
        this.date_month = this.cal.get(Calendar.MONTH);
        this.date_day = this.cal.get(Calendar.DAY_OF_MONTH);

        this.dateButton = (Button) this.activity.findViewById(R.id.btn_date);
        this.dateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                activity.showDialog(dateDialogID);
            }
        });
    }

    /**
     * Initializes category selection controls represented with spinner.
     */
    /*private void buildCategoryControls(){
        Spinner spinner = (Spinner) this.activity.findViewById(R.id.spinner_category);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.activity, R.array.categories, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }*/

    public void buildCategoryControls(ArrayList<Category> categories){
        Spinner spinner = (Spinner) this.activity.findViewById(R.id.spinner_category);
        ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(this.activity, R.layout.spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }



    /**
     * Initializes store selection controls.
     */
    /*private void buildStoreControls(){
        Spinner spinner = (Spinner) this.activity.findViewById(R.id.spinner_store);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.activity, R.array.stores, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }*/

    /**
     * Initializes store selection controls.
     */
    public void buildStoreControls(ArrayList<Store> stores) {
        Spinner spinner = (Spinner) this.activity.findViewById(R.id.spinner_store);
        ArrayAdapter<Store> adapter = new ArrayAdapter<Store>(this.activity, R.layout.spinner_item, stores);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }



    /**
     * Stores selected date.
     * @param y Selected year.
     * @param m Selected month.
     * @param d Selected day.
     */
    private void saveSelectedDate(int y, int m, int d){
        this.date_year = y;
        this.date_month = m;
        this.date_day = d;
    }

    /**
     * Updates date button label.
     * @param y Selected year.
     * @param m Selected month.
     * @param d Selected day.
     */
    private void updateDateButton(int y, int m, int d){
        if(y == this.cal.get(Calendar.YEAR) && m == this.cal.get(Calendar.MONTH)) {

            if(d == this.cal.get(Calendar.DAY_OF_MONTH)) {
                this.dateButton.setText("Today");
            } else if((d + 1) == this.cal.get(Calendar.DAY_OF_MONTH)) {
                this.dateButton.setText("Yesterday");
            } else {
                dateButton.setText(d + ". " + m + ". " + y);
            }
        }
    }

    public Dialog handleDatePickerDialogCreation(int id){
        if(id == this.dateDialogID) {

            DatePickerDialog dpDialog = new DatePickerDialog(this.activity, new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker arg0, int y, int m, int d) {
                    updateDateButton(y, m, d);
                    saveSelectedDate(y, m, d);
                }
            }, this.date_year, this.date_month, this.date_day);


            //o 1 den navic oproti StackOverflow! @TODO otestovat!!!
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, 1);

            DatePicker datePicker = dpDialog.getDatePicker();
            datePicker.setMaxDate(c.getTimeInMillis());
            return dpDialog;
        }
        return null;
    }





    // TODO: 01.04.16 DELETE
    private void resetVisitOnDoubleTap(){
        final GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener() {
            public boolean onDoubleTap(MotionEvent e) {
                prefs.edit().clear();
                prefs.edit().putBoolean("firstLaunch", true).commit();
                Toast.makeText(activity, "Time-travelled back.", Toast.LENGTH_SHORT).show();
                return true;
            }
        };

        final GestureDetector detector = new GestureDetector(listener);
        detector.setOnDoubleTapListener(listener);

        this.activity.getWindow().getDecorView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                return detector.onTouchEvent(event);
            }
        });

    }

}
