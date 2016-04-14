package cz.ondrejpittl.semestralka.controllers;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import cz.ondrejpittl.semestralka.HomeActivity;
import cz.ondrejpittl.semestralka.R;
import cz.ondrejpittl.semestralka.layout.PaymentRecord;
import cz.ondrejpittl.semestralka.models.Category;
import cz.ondrejpittl.semestralka.models.Payment;
import cz.ondrejpittl.semestralka.models.Statistics;
import cz.ondrejpittl.semestralka.models.Store;

import static cz.ondrejpittl.semestralka.R.color.appError;
import static cz.ondrejpittl.semestralka.R.drawable.shape_thin_border;
import static cz.ondrejpittl.semestralka.R.drawable.shape_thin_border_error;
import static cz.ondrejpittl.semestralka.R.id.edTxt_amount;
import static cz.ondrejpittl.semestralka.R.id.homeAmountContainer;

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


    private PaymentRecord editingRecord;






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

    private void setEditTextFocusLoseHandler(){

        /*this.activity.findViewById()
        hideKeyboard();*/

    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        //skryj soft klávesnici
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        //zobraz soft klávestnici
        //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void updatePayments(String month, int year, ArrayList<Payment> payments, String currencyUnits, HomeDataController dControl){
        this.updateDateLabels(month, year);
        this.buildPaymentBoxes(payments, currencyUnits, dControl);
    }

    private void updateDateLabels(String month, int year){
        TextView m = (TextView) this.activity.findViewById(R.id.txtView_recordsListMonth),
                 y = (TextView) this.activity.findViewById(R.id.txtView_recordsListYear);

        m.setText(month);
        y.setText(String.valueOf(year));
    }

    private int getDislayWidthInPx(){
        DisplayMetrics dm = new DisplayMetrics();
        this.activity.getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels ;
    }


    private void buildPaymentBoxes(ArrayList<Payment> payments, String currencyUnits, final HomeDataController dControl){
        this.paymentRecordsContainer.removeAllViews();

        PaymentRecord.setDisplayWidth(getDislayWidthInPx());

        for(Payment p : payments) {
            final PaymentRecord record = (PaymentRecord) layoutInflater.inflate(R.layout.payment_record, paymentRecordsContainer, false);

            //payment ID
            //record.setId(paymentID);
            //record.setPaymentId(p.getID());

            record.setPayment(p);
            record.setHomeController(dControl);

            record.getRecordDeleteBtn().setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    dControl.handlePaymentDelete(record.getPaymentId());
                    return false;
                }
            });

            record.getRecordEditBtn().setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    Payment p = record.getPayment();
                    dControl.handlePaymentEdit(p.getID());
                    registerPaymentUpdateStart(record);
                    return false;
                }
            });

            //payment category icon
            String iconPath = p.getCategory().getIcon();
            int imageResource = this.activity.getResources().getIdentifier(iconPath, null, this.activity.getPackageName());
            //Drawable image = ResourcesCompat.getDrawable(this.activity.getResources(), imageResource, null);

            ImageView icon = (ImageView) record.findViewById(R.id.imgViewRecordIcon);
            //icon.setBackground(image);
            //icon.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.appWhite50));
            icon.setImageResource(imageResource);



            //payment day
            TextView d = (TextView) record.findViewById(R.id.txtViewRecordDay);
            Calendar cal = Calendar.getInstance();
            cal.setTime(p.getDate());
            d.setText(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)) + ".");

            //payment category name
            TextView c = (TextView) record.findViewById(R.id.txtViewRecordCategory);
            c.setText(p.getCategory().getName());

            //payment store name
            TextView s = (TextView) record.findViewById(R.id.txtViewRecordStore);
            s.setText(p.getStore().getName());

            //payment amount
            TextView a = (TextView) record.findViewById(R.id.txtViewRecordPrice);
            a.setText(String.valueOf(p.getAmount()));

            //payment amount currency
            //TextView cur = (TextView) record.findViewById(R.id.txtViewRecordCurrency);
            //cur.setText(currencyUnits);


            //paymentNote
            TextView v = (TextView) record.findViewById(R.id.txtViewRecordNote);
            v.setText(p.getNote());

            paymentRecordsContainer.addView(record);
        }


    }


    public void registerPaymentUpdateStart(PaymentRecord record){
        fillPaymentInfoOnUpdate(record.getPayment());
        setInsertUpdateButtonText(true);
        record.hideEditActionButton();

        if(this.editingRecord != null)
            this.editingRecord.setEditing(false);

        this.editingRecord = record;
        this.editingRecord.setEditing(true);

    }


    public void registerPaymentUpdateFinished(){
        setInsertUpdateButtonText(false);
        this.editingRecord.setEditing(true);
        this.editingRecord = null;
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
                clearControlsFocus();
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
        final Spinner spinner = (Spinner) this.activity.findViewById(R.id.spinner_category);
        ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(this.activity, R.layout.spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                clearControlsFocus();
                return false;
            }
        });
    }

    private void clearControlsFocus(){
        View v = this.activity.getCurrentFocus();

        if(v != null){
            InputMethodManager imm = (InputMethodManager) this.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            v.clearFocus();
        }
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
        spinner.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                clearControlsFocus();
                return false;
            }
        });
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
        Calendar cal = Calendar.getInstance();

        if(y == cal.get(Calendar.YEAR) && m == cal.get(Calendar.MONTH)) {

            if(d == cal.get(Calendar.DAY_OF_MONTH)) {
                this.dateButton.setText("Today");
            } else if((d + 1) == cal.get(Calendar.DAY_OF_MONTH)) {
                this.dateButton.setText("Yesterday");
            } else {
                dateButton.setText(d + ". " + m + ". " + y);
            }
        }

        this.cal.set(Calendar.YEAR, y);
        this.cal.set(Calendar.MONTH, m);
        this.cal.set(Calendar.DAY_OF_MONTH, d);
    }

    public Dialog handleDatePickerDialogCreation(int id){
        if(id == this.dateDialogID) {

            //temporarily set US locale to force being set CalendarDialog English language
            HomeActivity.changeLocaleUS();

            DatePickerDialog dpDialog = new DatePickerDialog(this.activity, new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker arg0, int y, int m, int d) {
                    updateDateButton(y, m, d);
                    saveSelectedDate(y, m, d);
                    HomeActivity.changeLocaleDefault();
                }
            }, this.date_year, this.date_month, this.date_day);


            //o 1 den navic oproti StackOverflow! @TODO otestovat!!!
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, 1);

            DatePicker datePicker = dpDialog.getDatePicker();
            //datePicker.setMaxDate(c.getTimeInMillis());
            return dpDialog;
        }


        return null;
    }

    public void updateCurrencyViews(String curr){
        //controls – price amount
        TextView tv = (TextView) this.activity.findViewById(R.id.txtView_currency);
        tv.setText(curr);

        //statistics – today
        TextView tvT = (TextView) this.activity.findViewById(R.id.txtView_statisticsTodayCurrency);
        tvT.setText(curr);

        //statistics – week
        TextView tvW = (TextView) this.activity.findViewById(R.id.txtView_statisticsWeekCurrency);
        tvW.setText(curr);

        //statistics – month
        TextView tvM = (TextView) this.activity.findViewById(R.id.txtView_statisticsMonthCurrency);
        tvM.setText(curr);
    }

    public void updateStatistics(Statistics s, boolean dispayAll){
        int today = s.getTodayAmount(),
            week = s.getWeekAmount(),
            month = s.getMonthAmount();

        LinearLayout tW = (LinearLayout) this.activity.findViewById(R.id.statsTodayWrapper);
        LinearLayout wW = (LinearLayout) this.activity.findViewById(R.id.statsWeekWrapper);

        TextView tvM = (TextView) this.activity.findViewById(R.id.txtView_statisticsMonth);
        tvM.setText(String.valueOf(month));

        if(dispayAll) {
            TextView tvT = (TextView) this.activity.findViewById(R.id.txtView_statisticsToday);
            tvT.setText(String.valueOf(today));
            tW.setVisibility(View.VISIBLE);

            TextView tvW = (TextView) this.activity.findViewById(R.id.txtView_statisticsWeek);
            tvW.setText(String.valueOf(week));
            wW.setVisibility(View.VISIBLE);
        } else {
            tW.setVisibility(View.INVISIBLE);
            wW.setVisibility(View.INVISIBLE);
        }
    }

    public void clearControls(){
        Calendar cal = Calendar.getInstance();

        int y = cal.get(Calendar.YEAR),
                m = cal.get(Calendar.MONTH),
                d = cal.get(Calendar.DAY_OF_MONTH);

        //date button
        updateDateButton(y, m, d);

        //category spinner
        Spinner catSpin = (Spinner) this.activity.findViewById(R.id.spinner_category);
        catSpin.setSelection(0, true);

        //amount edittext
        EditText etA = (EditText) this.activity.findViewById(edTxt_amount);
        etA.setText("");

        //note edittext
        EditText etN = (EditText) this.activity.findViewById(R.id.edTxt_note);
        etN.setText("");

        //store spinner
        Spinner stSpin = (Spinner) this.activity.findViewById(R.id.spinner_store);
        stSpin.setSelection(0, true);


        if(editingRecord != null)
            editingRecord.setEditing(false);

        this.clearControlsFocus();
        this.unmarkInputAmountError();
    }

    public String[] getControlsContents(){
        String[] contents = new String[7];

        //date
        contents[0] = String.valueOf(this.cal.getTimeInMillis());

        //category
        Spinner catSpin = (Spinner) this.activity.findViewById(R.id.spinner_category);
        contents[1] = ((Category) catSpin.getSelectedItem()).getName();
        contents[2] = String.valueOf(((Category) catSpin.getSelectedItem()).getID());

        //amount
        EditText etA = (EditText) this.activity.findViewById(edTxt_amount);
        contents[3] = String.valueOf(etA.getText());

        //note
        EditText etN = (EditText) this.activity.findViewById(R.id.edTxt_note);
        contents[4] = String.valueOf(etN.getText());

        //store
        Spinner stSpin = (Spinner) this.activity.findViewById(R.id.spinner_store);
        contents[5] = ((Store) stSpin.getSelectedItem()).getName();
        contents[6] = String.valueOf(((Store) stSpin.getSelectedItem()).getID());

        return contents;
    }

    public void markInputAmountError(){
        int errColor = ContextCompat.getColor(this.activity, appError);

        EditText et = (EditText) this.activity.findViewById(edTxt_amount);
        et.setTextColor(errColor);
        et.setHintTextColor(errColor);
        et.requestFocus();

        LinearLayout layout = (LinearLayout) this.activity.findViewById(homeAmountContainer);
        layout.setBackgroundResource(shape_thin_border_error);
    }

    public void unmarkInputAmountError(){
        int okColor = ContextCompat.getColor(this.activity, R.color.appWhite);

        EditText et = (EditText) this.activity.findViewById(edTxt_amount);
        et.setTextColor(okColor);
        et.setHintTextColor(okColor);

        LinearLayout layout = (LinearLayout) this.activity.findViewById(homeAmountContainer);
        layout.setBackgroundResource(shape_thin_border);
    }

    public void fillPaymentInfoOnUpdate(Payment p){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(p.getDateLong());

        int y = cal.get(Calendar.YEAR),
            m = cal.get(Calendar.MONTH),
            d = cal.get(Calendar.DAY_OF_MONTH);

        //date button
        updateDateButton(y, m, d);

        //category spinner
        Spinner catSpin = (Spinner) this.activity.findViewById(R.id.spinner_category);
        catSpin.setSelection(findSpinnerItemIndex(catSpin, p.getCategory().getName()), true);

        //amount edittext
        EditText etA = (EditText) this.activity.findViewById(edTxt_amount);
        etA.setText(String.valueOf(p.getAmount()));

        //note edittext
        EditText etN = (EditText) this.activity.findViewById(R.id.edTxt_note);
        etN.setText(p.getNote());

        //store spinner
        Spinner stSpin = (Spinner) this.activity.findViewById(R.id.spinner_store);
        stSpin.setSelection(findSpinnerItemIndex(stSpin, p.getStore().getName()), true);
    }

    private int findSpinnerItemIndex(Spinner s, String value){
        for(int i = 0; i < s.getCount(); i++) {
            String val = s.getItemAtPosition(i).toString();
            if(val.equals(value)) return i;
        }

        return 0;
    }

    public void setInsertUpdateButtonText(boolean editing){
        Button btn = (Button) this.activity.findViewById(R.id.btn_ok);

        if(editing) {
            btn.setText(R.string.home_btn_ok_update_label);
        } else {
            btn.setText(R.string.home_btn_ok_insert_label);
        }
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
