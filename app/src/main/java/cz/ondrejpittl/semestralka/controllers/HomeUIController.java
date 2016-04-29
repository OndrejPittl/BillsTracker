package cz.ondrejpittl.semestralka.controllers;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
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

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;

import cz.ondrejpittl.semestralka.HomeActivity;
import cz.ondrejpittl.semestralka.R;
import cz.ondrejpittl.semestralka.factories.AnimationFactory;
import cz.ondrejpittl.semestralka.layout.CustomSpinner;
import cz.ondrejpittl.semestralka.layout.PaymentRecord;
import cz.ondrejpittl.semestralka.models.Category;
import cz.ondrejpittl.semestralka.models.Payment;
import cz.ondrejpittl.semestralka.models.Statistics;
import cz.ondrejpittl.semestralka.models.Store;
import cz.ondrejpittl.semestralka.partial.JodaCalendar;
import cz.ondrejpittl.semestralka.partial.SharedPrefs;

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
    private HomeActivity activity;

    /**
     * Object inflating view defined as XML.
     */
    private LayoutInflater layoutInflater;


    LinearLayout paymentRecordsContainer;

    private ArrayList<PaymentRecord> records;



    private SharedPreferences prefs;

    private boolean animationAllowed;

    /**
     * Calendar singleton object reference with actual date.
     */
    //private Calendar cal;
    private DateTime calendar;

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
    public HomeUIController(HomeActivity activity){
        this.activity = activity;
    }

    /**
     * Initializes UI of Home activity.
     */
    public void initUI(){
        //this.cal = Calendar.getInstance();
        this.calendar = new DateTime();
        this.prefs = this.activity.getSharedPreferences("cz.ondrejpittl.semestralka", Context.MODE_PRIVATE);
        this.layoutInflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.paymentRecordsContainer = (LinearLayout) this.activity.findViewById(R.id.recordsContainer);

        this.animationAllowed = SharedPrefs.isPaymentAnimationSet() && SharedPrefs.getPaymentAnimation();

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

    /*private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        //skryj soft klávesnici
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        //zobraz soft klávestnici
        //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }*/

    public void updatePaymentRecords(final String month, final int year, final ArrayList<Payment> payments, final String currencyUnits, final HomeDataController dControl){
        AnimationFactory.fadeOutPaymentRecords(this.records);

        int timeOffset = 230;

        if(!animationAllowed) {
            timeOffset = 1;
        }

        new Handler().postDelayed(new Runnable() {
            public void run() {
                updateDateLabels(month, year);
                buildPaymentBoxes(payments, currencyUnits, dControl);
            }
        }, timeOffset);
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

        records = new ArrayList<>();

        for(Payment p : payments) {
            final PaymentRecord record = (PaymentRecord) layoutInflater.inflate(R.layout.payment_record, paymentRecordsContainer, false);

            //payment ID
            //record.setId(paymentID);
            //record.setPaymentId(p.getID());

            record.setAlpha(0);
            record.setPayment(p);
            //record.updateIconVisibility();

            //record.setHomeController(dControl);

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
            //cal.setTime(p.getDate());
            cal.setTime(p.getDate().toDate());
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

            this.records.add(record);
            paymentRecordsContainer.addView(record);
            record.updateRecordHeight(this.activity);
            record.updateIconVisibility();
        }

        updateRecordsHeight();

        AnimationFactory.fadeInPaymentRecords(this.records);

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
        /*this.date_year = this.cal.get(Calendar.YEAR);
        this.date_month = this.cal.get(Calendar.MONTH);
        this.date_day = this.cal.get(Calendar.DAY_OF_MONTH);*/

        this.date_year = this.calendar.getYear();
        this.date_month = this.calendar.getMonthOfYear();
        this.date_day = this.calendar.getDayOfMonth();

        this.dateButton = (Button) this.activity.findViewById(R.id.btn_date);
        this.dateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                HomeActivity.clearControlsFocus(activity);
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
        final CustomSpinner spinner = (CustomSpinner) this.activity.findViewById(R.id.spinner_category);
        spinner.init(this.activity, categories);

        this.updateCategoryControlsSelection();

        spinner.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                HomeActivity.clearControlsFocus(activity);
                return false;
            }
        });
    }

    public void updateCategoryControlsSelection(){
        CustomSpinner spinner = (CustomSpinner) this.activity.findViewById(R.id.spinner_category);
        spinner.selectItem(SharedPrefs.getDefaultCategory());
    }

    /*private void clearControlsFocus(){
        View v = this.activity.getCurrentFocus();

        if(v != null){
            InputMethodManager imm = (InputMethodManager) this.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            v.clearFocus();
        }
    }*/



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
        final CustomSpinner spinner = (CustomSpinner) this.activity.findViewById(R.id.spinner_store);
        spinner.init(this.activity, stores);
        this.updateStoreControlsSelection();
        spinner.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                HomeActivity.clearControlsFocus(activity);
                return false;
            }
        });
    }

    public void updateStoreControlsSelection(){
        CustomSpinner spinner = (CustomSpinner) this.activity.findViewById(R.id.spinner_store);
        spinner.selectItem(SharedPrefs.getDefaultStore());
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

        this.calendar = this.calendar.withYearOfEra(y).withMonthOfYear(m).withDayOfMonth(d);
    }

    /*
     * Updates date button label.
     * @param y Selected year.
     * @param m Selected month.
     * @param d Selected day.
     */
    /*private void updateDateButton(int y, int m, int d){
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
    }*/

    /**
     * Updates date button label.
     * @param y Selected year.
     * @param m Selected month.
     * @param d Selected day.
     */
    private void updateDateButton(int y, int m, int d){
        DateTime now = new DateTime();
        DateTime selected = new DateTime().withYearOfEra(y).withMonthOfYear(m).withDayOfMonth(d);

        Log.i("Ondra-stats", "now: " + now);
        Log.i("Ondra-stats", "sel: " + selected);

        if(JodaCalendar.compareMonthYear(now, selected)) {
            if(JodaCalendar.compareDateTimes(now, selected)) {
                this.dateButton.setText("Today");
                return;
            } else if(JodaCalendar.compareDateTimes(now, JodaCalendar.clone(selected).plusDays(1))) {
                this.dateButton.setText("Yesterday");
                return;
            } else if(JodaCalendar.compareDateTimes(now, JodaCalendar.clone(selected).minusDays(1))) {
                this.dateButton.setText("Tomorrow");
                return;
            }
        }

        dateButton.setText(d + ". " + m + ". " + y);
    }

    public Dialog handleDatePickerDialogCreation(int id){
        if(id == this.dateDialogID) {

            //temporarily set US locale to force being set CalendarDialog English language
            HomeActivity.changeLocaleUS();

            DatePickerDialog dpDialog = new DatePickerDialog(this.activity, new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker arg0, int y, int m, int d) {
                    updateDateButton(y, m+1, d);
                    saveSelectedDate(y, m+1, d);
                    HomeActivity.changeLocaleDefault();
                }
            }, this.date_year, this.date_month - 1, this.date_day);


            //o 1 den navic oproti StackOverflow! @TODO otestovat!!!
            //Calendar c = Calendar.getInstance();
            //c.add(Calendar.DATE, 1);
            //DatePicker datePicker = dpDialog.getDatePicker();
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
        CustomSpinner catSpin = (CustomSpinner) this.activity.findViewById(R.id.spinner_category);
        //catSpin.setSelection(0, true);
        catSpin.selectItem(SharedPrefs.getDefaultCategory());

        //amount edittext
        EditText etA = (EditText) this.activity.findViewById(edTxt_amount);
        etA.setText("");

        //note edittext
        EditText etN = (EditText) this.activity.findViewById(R.id.edTxt_note);
        etN.setText("");

        //store spinner
        CustomSpinner stSpin = (CustomSpinner) this.activity.findViewById(R.id.spinner_store);
        //stSpin.setSelection(0, true);
        stSpin.selectItem(SharedPrefs.getDefaultStore());


        if(editingRecord != null)
            editingRecord.setEditing(false);

        HomeActivity.clearControlsFocus(activity);
        this.unmarkInputAmountError();
    }

    public String[] getControlsContents(){
        String[] contents = new String[7];

        //date
        //contents[0] = String.valueOf(this.cal.getTimeInMillis());
        contents[0] = String.valueOf(this.calendar.getMillis());

        //category
        CustomSpinner catSpin = (CustomSpinner) this.activity.findViewById(R.id.spinner_category);
        contents[1] = ((Category) catSpin.getSelectedItem()).getName();
        contents[2] = String.valueOf(((Category) catSpin.getSelectedItem()).getID());

        //amount
        EditText etA = (EditText) this.activity.findViewById(edTxt_amount);
        contents[3] = String.valueOf(etA.getText());

        //note
        EditText etN = (EditText) this.activity.findViewById(R.id.edTxt_note);
        contents[4] = String.valueOf(etN.getText());

        //store
        CustomSpinner stSpin = (CustomSpinner) this.activity.findViewById(R.id.spinner_store);
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
        updateDateButton(y, m+1, d);

        //category spinner
        CustomSpinner catSpin = (CustomSpinner) this.activity.findViewById(R.id.spinner_category);
        //catSpin.setSelection(catSpin.findSpinnerItemIndex(p.getCategory().getName()), true);
        catSpin.selectItem(p.getCategory().getName());

        //amount edittext
        EditText etA = (EditText) this.activity.findViewById(edTxt_amount);
        etA.setText(String.valueOf(p.getAmount()));

        //note edittext
        EditText etN = (EditText) this.activity.findViewById(R.id.edTxt_note);
        etN.setText(p.getNote());

        //store spinner
        CustomSpinner stSpin = (CustomSpinner) this.activity.findViewById(R.id.spinner_store);
        //stSpin.setSelection(stSpin.findSpinnerItemIndex(p.getStore().getName()), true);
        stSpin.selectItem(p.getCategory().getName());
    }


    public void setInsertUpdateButtonText(boolean editing){
        Button btn = (Button) this.activity.findViewById(R.id.btn_ok);

        if(editing) {
            btn.setText(R.string.home_btn_ok_update_label);
        } else {
            btn.setText(R.string.home_btn_ok_insert_label);
        }
    }

    public void updateRecordsHeight(){
        boolean collapsed = SharedPrefs.isPaymentNoteDisplaySet() && !SharedPrefs.getPaymentNoteDisplay();
            int childcount = paymentRecordsContainer.getChildCount();

        if(childcount <= 0)
            return;

        for (int i=0; i < childcount; i++){
            PaymentRecord rec = (PaymentRecord) paymentRecordsContainer.getChildAt(i);
            rec.setCollapsed(collapsed);
            rec.updateRecordHeight(this.activity);
        }
    }



    // TODO: 01.04.16 DELETE
    private void resetVisitOnDoubleTap(){
        final GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener() {
            public boolean onDoubleTap(MotionEvent e) {
                SharedPrefs.reset();
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
