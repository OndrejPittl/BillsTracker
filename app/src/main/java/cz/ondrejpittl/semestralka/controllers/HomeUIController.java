package cz.ondrejpittl.semestralka.controllers;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;

import cz.ondrejpittl.semestralka.HomeActivity;
import cz.ondrejpittl.semestralka.R;
import cz.ondrejpittl.semestralka.factories.AnimationFactory;
import cz.ondrejpittl.semestralka.layout.CustomSpinner;
import cz.ondrejpittl.semestralka.layout.LoadingImgButton;
import cz.ondrejpittl.semestralka.layout.PaymentRecord;
import cz.ondrejpittl.semestralka.models.Category;
import cz.ondrejpittl.semestralka.models.Payment;
import cz.ondrejpittl.semestralka.models.Statistics;
import cz.ondrejpittl.semestralka.models.Store;
import cz.ondrejpittl.semestralka.partial.InputFieldType;
import cz.ondrejpittl.semestralka.partial.JodaCalendar;
import cz.ondrejpittl.semestralka.partial.LoadingButtonType;
import cz.ondrejpittl.semestralka.partial.SharedPrefs;
import cz.ondrejpittl.semestralka.partial.TutorialManager;

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


    private TutorialManager tut;


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


    private DatePickerDialog dateDPDialog;

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



    private LoadingImgButton settingsBtn;
    private LoadingImgButton statsBtn;






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
        this.tut = new TutorialManager(this.activity);
        this.layoutInflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.paymentRecordsContainer = (LinearLayout) this.activity.findViewById(R.id.recordsContainer);
        this.animationAllowed = SharedPrefs.isPaymentAnimationSet() && SharedPrefs.getPaymentAnimation();

        this.buildControlPanel();

        //tmp
        //this.resetVisitOnDoubleTap();
    }

    /**
     * Builds control panel.
     */
    private void buildControlPanel(){
        this.buildDateControls();
        this.buildSettingsImgButton();
        this.buildStatsImgButton();
        //this.buildCategoryControls();
        //this.buildStoreControls();
    }

    public void startTutorial(){
        this.tut.start();
    }

    private void buildSettingsImgButton(){
        FrameLayout container = (FrameLayout) this.activity.findViewById(R.id.settingsBtnWrapper);
        this.settingsBtn = (LoadingImgButton) layoutInflater.inflate(R.layout.loading_img_button, container, false);
        this.settingsBtn.init(LoadingButtonType.SETTINGS, this.activity);
        container.addView(this.settingsBtn);
    }

    private void buildStatsImgButton(){
        FrameLayout container = (FrameLayout) this.activity.findViewById(R.id.statsBtnWrapper);
        this.statsBtn = (LoadingImgButton) layoutInflater.inflate(R.layout.loading_img_button, container, false);
        this.statsBtn.init(LoadingButtonType.STATISTICS, this.activity);
        container.addView(this.statsBtn);
    }

    public void resetSettingsImgButton(){
        this.settingsBtn.reset();
    }

    public void resetStatsImgButton(){
        this.statsBtn.reset();
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

            if(SharedPrefs.isPaymentAnimationSet())
                record.setAlpha(0);

            record.setPayment(p);
            record.update();
            //record.updateIconVisibility();
            //record.setHomeController(dControl);

            /*int containerHeight = record.updateRecordHeight(this.activity);
            float textSize = (int)(containerHeight * 0.9);*/

            /*float textSize = record.isCollapsed()
                    ? PaymentRecord.getPaymentCollapsedHeight()
                    : PaymentRecord.getPaymentUnCollapsedHeight();*/




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
            //d.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

            //payment category name
            TextView c = (TextView) record.findViewById(R.id.txtViewRecordCategory);
            c.setText(p.getCategory().getName());
            //c.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

            //payment store name
            TextView s = (TextView) record.findViewById(R.id.txtViewRecordStore);
            s.setText(p.getStore().getName());
            //s.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

            //payment amount
            TextView a = (TextView) record.findViewById(R.id.txtViewRecordPrice);
            a.setText(String.valueOf(p.getAmount()));
            //a.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

            //payment amount currency
            //TextVidatew cur = (TextView) record.findViewById(R.id.txtViewRecordCurrency);
            //cur.setText(currencyUnits);


            //paymentNote
            TextView v = (TextView) record.findViewById(R.id.txtViewRecordNote);
            v.setText(p.getNote());
            //v.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

            this.records.add(record);
            paymentRecordsContainer.addView(record);
            record.updateIconVisibility();
        }

        updateRecordsHeight();

        AnimationFactory.fadeInPaymentRecords(this.records);

    }


    public void registerPaymentUpdateStart(PaymentRecord record){
        fillPaymentInfoOnUpdate(record.getPayment());
        setInsertUpdateButtonText(true);
        setClearCancelButtonText(true);
        record.hideEditActionButton();

        if(this.editingRecord != null)
            this.editingRecord.setEditing(false);

        this.editingRecord = record;
        this.editingRecord.setEditing(true);

    }


    public void registerPaymentUpdateFinished(){
            setInsertUpdateButtonText(false);
        setClearCancelButtonText(false);
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


        this.dateDPDialog = new DatePickerDialog(this.activity, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker arg0, int y, int m, int d) {
                updateDateButton(y, m+1, d);
                saveSelectedDate(y, m+1, d);
                HomeActivity.changeLocaleDefault();
            }
        }, this.date_year, this.date_month - 1, this.date_day);

        this.dateButton = (Button) this.activity.findViewById(R.id.btn_date);
        this.dateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                HomeActivity.clearControlsFocus(activity);
                dateDPDialog.updateDate(date_year, date_month-1, date_day);
                dateDPDialog.show();
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

    /*public Dialog handleDatePickerDialogCreation(int id){
        if(id == this.dateDialogID) {

            //temporarily set US locale to force being set CalendarDialog English language
            HomeActivity.changeLocaleUS();

             this.dateDPDialog = new DatePickerDialog(this.activity, new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker arg0, int y, int m, int d) {
                    updateDateButton(y, m+1, d);
                    saveSelectedDate(y, m+1, d);
                    HomeActivity.changeLocaleDefault();
                }
            }, this.date_year, this.date_month - 1, this.date_day);



            return dpDialog;
        }


        return null;
    }*/

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

        TextView tvDayLbl = (TextView) this.activity.findViewById(R.id.txtView_statsTodayLabel);
        TextView tvMonthLbl = (TextView) this.activity.findViewById(R.id.txtView_statsMonthLabel);
        TextView tvWeekLbl = (TextView) this.activity.findViewById(R.id.txtView_statsWeekLabel);

        //display month stats (always)
        TextView tvM = (TextView) this.activity.findViewById(R.id.txtView_statisticsMonth);
        tvM.setText(String.valueOf(month));



        if(dispayAll) {
            //actual month

            int statsWidth = 0;
            TextView tvT = (TextView) this.activity.findViewById(R.id.txtView_statisticsToday);
            TextView tvW = (TextView) this.activity.findViewById(R.id.txtView_statisticsWeek);

            //measuring labels
            statsWidth += measureTextWidth(this.activity.getString(R.string.home_stats_day_label), tvDayLbl);
            statsWidth += measureTextWidth(this.activity.getString(R.string.home_stats_week_label), tvWeekLbl);
            statsWidth += measureTextWidth(this.activity.getString(R.string.home_stats_month_label), tvMonthLbl);

            //measuring values
            statsWidth += measureTextWidth(String.valueOf(today), tvT);
            statsWidth += measureTextWidth(String.valueOf(week), tvW);
            statsWidth += measureTextWidth(String.valueOf(month), tvM);


            //update month label length ("Month" vs. "M")
            Log.i("Ondra-ministats", "measured: " + statsWidth + ", max: " + (getDislayWidthInPx() * 0.8));
            if(statsWidth > getDislayWidthInPx() * 0.80) {
                tvDayLbl.setText(R.string.home_stats_d_label);
                tvWeekLbl.setText(R.string.home_stats_w_label);
                tvMonthLbl.setText(R.string.home_stats_m_label);
            } else {
                tvDayLbl.setText(R.string.home_stats_day_label);
                tvWeekLbl.setText(R.string.home_stats_week_label);
                tvMonthLbl.setText(R.string.home_stats_month_label);
            }


            //display today stats
            tvT.setText(String.valueOf(today));
            tW.setVisibility(View.VISIBLE);

            //display week stats
            tvW.setText(String.valueOf(week));
            wW.setVisibility(View.VISIBLE);
        } else {
            //the other ones
            //hide week and today stats
            tW.setVisibility(View.INVISIBLE);
            wW.setVisibility(View.INVISIBLE);
        }
    }

    private int measureTextWidth(String txt, TextView tv){
        Rect bounds = new Rect();

        //len-1?
        tv.getPaint().getTextBounds(txt, 0, txt.length(), bounds);
        int width = bounds.left + bounds.width();
        return width;
    }

    public void clearControls(){
        Calendar cal = Calendar.getInstance();

        int y = cal.get(Calendar.YEAR),
                m = cal.get(Calendar.MONTH),
                d = cal.get(Calendar.DAY_OF_MONTH);

        //date button
        updateDateButton(y, m+1, d);

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
        this.unmarkInputErrorAll();
    }

    public String[] getControlsContents(){
        String[] contents = new String[7];

        //date
        //contents[0] = String.valueOf(this.cal.getTimeInMillis());
        contents[0] = String.valueOf(this.calendar.getMillis());

        //category
        CustomSpinner catSpin = (CustomSpinner) this.activity.findViewById(R.id.spinner_category);
        Category c = (Category) catSpin.getSelectedItem();
        if(c != null) {
            contents[1] = c.getName();
            contents[2] = String.valueOf(c.getID());
        } else {
            contents[1] = "";
            contents[2] = String.valueOf(-1);
        }


        //amount
        EditText etA = (EditText) this.activity.findViewById(edTxt_amount);
        contents[3] = String.valueOf(etA.getText());

        //note
        EditText etN = (EditText) this.activity.findViewById(R.id.edTxt_note);
        contents[4] = String.valueOf(etN.getText());

        //store
        CustomSpinner stSpin = (CustomSpinner) this.activity.findViewById(R.id.spinner_store);
        Store s = (Store) stSpin.getSelectedItem();
        if(s != null) {
            contents[5] = s.getName();
            contents[6] = String.valueOf(s.getID());
        } else {
            contents[5] = "";
            contents[6] = String.valueOf(-1);
        }

        /*
        contents[5] = ((Store) stSpin.getSelectedItem()).getName();
        contents[6] = String.valueOf(((Store) stSpin.getSelectedItem()).getID());
        */

        return contents;
    }

    public void markInputError(InputFieldType t){
        int errColor = ContextCompat.getColor(this.activity, appError);


        switch(t) {
            case EDIT_TEXT_AMOUNT:
                EditText et = (EditText) this.activity.findViewById(edTxt_amount);
                et.setTextColor(errColor);
                et.setHintTextColor(errColor);
                //et.requestFocus();

                LinearLayout layout = (LinearLayout) this.activity.findViewById(homeAmountContainer);
                layout.setBackgroundResource(shape_thin_border_error);

                break;

            case CUSTOM_SPINNER_CATEGORY:

                CustomSpinner spinCat = (CustomSpinner) this.activity.findViewById(R.id.spinner_category);
                spinCat.setBackgroundResource(shape_thin_border_error);

                break;

            case CUSTOM_SPINNER_STORE:

                CustomSpinner spinSt = (CustomSpinner) this.activity.findViewById(R.id.spinner_store);
                spinSt.setBackgroundResource(shape_thin_border_error);

                break;
        }
    }

    public void unmarkInputError(InputFieldType t){
        int okColor = ContextCompat.getColor(this.activity, R.color.appWhite);

        switch(t) {
            case EDIT_TEXT_AMOUNT:

                EditText et = (EditText) this.activity.findViewById(edTxt_amount);
                et.setTextColor(okColor);
                et.setHintTextColor(okColor);

                LinearLayout layout = (LinearLayout) this.activity.findViewById(homeAmountContainer);
                layout.setBackgroundResource(shape_thin_border);

                break;

            case CUSTOM_SPINNER_CATEGORY:

                CustomSpinner spinCat = (CustomSpinner) this.activity.findViewById(R.id.spinner_category);
                spinCat.setBackgroundResource(shape_thin_border);

                break;

            case CUSTOM_SPINNER_STORE:

                CustomSpinner spinSt = (CustomSpinner) this.activity.findViewById(R.id.spinner_store);
                spinSt.setBackgroundResource(shape_thin_border);

                break;
        }
    }


    public void unmarkInputErrorAll(){
        int okColor = ContextCompat.getColor(this.activity, R.color.appWhite);

        //amount
        EditText et = (EditText) this.activity.findViewById(edTxt_amount);
        et.setTextColor(okColor);
        et.setHintTextColor(okColor);

        LinearLayout layout = (LinearLayout) this.activity.findViewById(homeAmountContainer);
        layout.setBackgroundResource(shape_thin_border);


        //category
        CustomSpinner spinCat = (CustomSpinner) this.activity.findViewById(R.id.spinner_category);
        spinCat.setBackgroundResource(shape_thin_border);


        //store
        CustomSpinner spinSt = (CustomSpinner) this.activity.findViewById(R.id.spinner_store);
        spinSt.setBackgroundResource(shape_thin_border);

    }


    /*public void unmarkInputError(){
        int okColor = ContextCompat.getColor(this.activity, R.color.appWhite);

        EditText et = (EditText) this.activity.findViewById(edTxt_amount);
        et.setTextColor(okColor);
        et.setHintTextColor(okColor);

        LinearLayout layout = (LinearLayout) this.activity.findViewById(homeAmountContainer);
        layout.setBackgroundResource(shape_thin_border);
    }*/

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
        stSpin.selectItem(p.getStore().getName());
    }


    public void setInsertUpdateButtonText(boolean editing) {
        Button btn = (Button) this.activity.findViewById(R.id.btn_ok);

        if (editing) {
            btn.setText(R.string.home_btn_ok_update_label);
        } else {
            btn.setText(R.string.home_btn_ok_insert_label);
        }
    }

    public void setClearCancelButtonText(boolean editing){
        Button btn = (Button) this.activity.findViewById(R.id.btn_clear);

        if(editing) {
            btn.setText(R.string.home_btn_cancel_label);
        } else {
            btn.setText(R.string.home_btn_cancel_clear_label);
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

    public void updateDateViewed(DateTime date){
        this.date_month = date.getMonthOfYear();

        int y = date.getYearOfEra(),
            m = date.getMonthOfYear(),
            d = date.getDayOfMonth();

        this.calendar = this.calendar.withYearOfEra(y)
                                     .withMonthOfYear(m)
                                     .withDayOfMonth(d);

        this.updateDateButton(y, m, d);

        this.date_year = y;
        this.date_month = m;
        this.date_day = d;
    }

    /*public void hideImgButton(View v){
        this.lastImgButton = v;
        this.lastImgButton.setVisibility(View.INVISIBLE);
    }

    public void showLastImgButton(){
        this.lastImgButton.setVisibility(View.VISIBLE);
    }*/


    // TODO: 01.04.16 DELETE
    private void resetVisitOnDoubleTap(){
        final GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener() {
            public boolean onDoubleTap(MotionEvent e) {
                SharedPrefs.clear();
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
