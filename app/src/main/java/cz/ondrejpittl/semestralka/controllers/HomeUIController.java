package cz.ondrejpittl.semestralka.controllers;

import android.app.DatePickerDialog;
import android.content.Context;
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
import cz.ondrejpittl.semestralka.partial.Designer;
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


    /**
     * An object handling a first-time-launch tutorial.
     */
    private TutorialManager tutorial;

    /**
     * Object inflating view defined as XML.
     */
    private LayoutInflater layoutInflater;

    /**
     * A container of currently viewed payments.
     */
    LinearLayout paymentRecordsContainer;

    /**
     * A collection of currently viewed payments.
     */
    private ArrayList<PaymentRecord> records;

    /**
     * A flag indicating whether are animations allowed or not.
     */
    private boolean animationAllowed;

    /**
     * Calendar singleton object reference with actual date.
     */
    private DateTime calendar;

    /**
     * Date button firing datepicker dialog.
     */
    private Button dateButton;



    /**
     * A new payment control elements – date picker dialog
     */
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

    /**
     *  A payment record being edited reference.
     */
    private PaymentRecord editingRecord;


    /**
     * A settings button reference.
     * Starts SettingsActivity.
     */
    private LoadingImgButton settingsBtn;

    /**
     * A statistics button reference.
     * Starts StatisticsActivity.
     */
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
        this.calendar = new DateTime();

        this.tutorial = new TutorialManager(this.activity);
        this.layoutInflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.paymentRecordsContainer = (LinearLayout) this.activity.findViewById(R.id.recordsContainer);
        this.animationAllowed = SharedPrefs.isPaymentAnimationSet() && SharedPrefs.getPaymentAnimation();
        this.buildControlPanel();
    }

    /**
     * Builds a control panel.
     */
    private void buildControlPanel(){
        this.buildDateControls();
        this.buildSettingsImgButton();
        this.buildStatsImgButton();
    }

    /**
     * Starts a first-time-launch tutorial.
     */
    public void startTutorial(){
        this.tutorial.start();
    }

    /**
     * Builds a settings button.
     */
    private void buildSettingsImgButton(){
        FrameLayout container = (FrameLayout) this.activity.findViewById(R.id.settingsBtnWrapper);
        this.settingsBtn = (LoadingImgButton) layoutInflater.inflate(R.layout.loading_img_button, container, false);
        this.settingsBtn.init(LoadingButtonType.SETTINGS, this.activity);
        container.addView(this.settingsBtn);
    }

    /**
     * Builds a stats button.
     */
    private void buildStatsImgButton(){
        FrameLayout container = (FrameLayout) this.activity.findViewById(R.id.statsBtnWrapper);
        this.statsBtn = (LoadingImgButton) layoutInflater.inflate(R.layout.loading_img_button, container, false);
        this.statsBtn.init(LoadingButtonType.STATISTICS, this.activity);
        container.addView(this.statsBtn);
    }

    /**
     * Resets a settings button.
     */
    public void resetSettingsImgButton(){
        this.settingsBtn.reset();
    }

    /**
     * Resets a statistics button.
     */
    public void resetStatsImgButton(){
        this.statsBtn.reset();
    }

    /**
     * Updates a list of payment records being currently viewed.
     * @param month             currently viewed month
     * @param year              currently viewed year
     * @param payments          a list of payment records
     * @param dControl          DataController reference
     */
    public void updatePaymentRecords(final String month, final int year, final ArrayList<Payment> payments, final HomeDataController dControl){
        AnimationFactory.fadeOutPaymentRecords(this.records);

        int timeOffset = 230;

        if(!animationAllowed) {
            timeOffset = 1;
        }

        new Handler().postDelayed(new Runnable() {
            public void run() {
                updateDateLabels(month, year);
                buildPaymentBoxes(payments, dControl);
            }
        }, timeOffset);
    }

    /**
     * Updates date label.
     * @param month a currently viewed month
     * @param year  a currently viewed year
     */
    private void updateDateLabels(String month, int year){
        TextView m = (TextView) this.activity.findViewById(R.id.txtView_recordsListMonth),
                 y = (TextView) this.activity.findViewById(R.id.txtView_recordsListYear);

        m.setText(month);
        y.setText(String.valueOf(year));
    }

    /**
     * Builds payment boxes representing currently viewed payments.
     * @param payments          a list of currently viewed payments
     * @param dControl          DataController reference
     */
    private void buildPaymentBoxes(ArrayList<Payment> payments, final HomeDataController dControl){
        this.paymentRecordsContainer.removeAllViews();
        PaymentRecord.setDisplayWidth(Designer.getDisplayWidthInPx(this.activity));
        records = new ArrayList<>();

        for(Payment p : payments) {
            final PaymentRecord record = (PaymentRecord) layoutInflater.inflate(R.layout.payment_record, paymentRecordsContainer, false);

            if(SharedPrefs.isPaymentAnimationSet())
                record.setAlpha(0);

            record.setPayment(p);
            record.update();

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

            ImageView icon = (ImageView) record.findViewById(R.id.imgViewRecordIcon);
            icon.setImageResource(imageResource);

            //payment day
            TextView d = (TextView) record.findViewById(R.id.txtViewRecordDay);
            Calendar cal = Calendar.getInstance();
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
            a.setText(String.valueOf(p.getAmount()) + " " + SharedPrefs.getDefaultCurrency());

            //paymentNote
            TextView v = (TextView) record.findViewById(R.id.txtViewRecordNote);
            v.setText(p.getNote());

            this.records.add(record);
            paymentRecordsContainer.addView(record);
            record.updateIconVisibility();
        }
        updateRecordsHeight();
        AnimationFactory.fadeInPaymentRecords(this.records);
    }

    /**
     * Registers payment update.
     * @param record    a payment record being updated
     */
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

    /**
     * Registers a payment update confirmed.
     */
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
     *  Builds a category control element.
     * @param categories    a list of stored categories
     */
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

    /**
     * Updates a selected item of a category spinner.
     */
    public void updateCategoryControlsSelection(){
        CustomSpinner spinner = (CustomSpinner) this.activity.findViewById(R.id.spinner_category);
        spinner.selectItem(SharedPrefs.getDefaultCategory());
    }

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

    /**
     * Updates a selected item of a store spinner.
     */
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

    /**
     * Updates currencies on currency change.
     * @param curr  new currency
     */
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

    /**
     * Updates compact statistics placed in the footer of this activity.
     * @param s         Statistics object reference.
     * @param displayAll a flag indicating whether are all statistics data drawn or just a part
     */
    public void updateStatistics(Statistics s, boolean displayAll){
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

        if(displayAll) {
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
            Log.i("Ondra-ministats", "measured: " + statsWidth + ", max: " + (Designer.getDisplayWidthInPx(this.activity) * 0.8));
            if(statsWidth > Designer.getDisplayWidthInPx(this.activity) * 0.80) {
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

    /**
     * Function measuring width od a text.
     * @param txt   text being measured
     * @param tv    textview where is a text inserted/formatted
     * @return      width of a text formated by textview
     */
    private int measureTextWidth(String txt, TextView tv){
        Rect bounds = new Rect();

        //len-1?
        tv.getPaint().getTextBounds(txt, 0, txt.length(), bounds);
        int width = bounds.left + bounds.width();
        return width;
    }

    /**
     * Resets all payment input controls.
     */
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
        Designer.unmarkInputErrorAll(this.activity);
    }

    /**
     * Collects data from payment input controls.
     * @return  input data
     */
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

        return contents;
    }

    /**
     * Fills payment info into payment input elements.
     * @param p a payment being edited
     */
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
        catSpin.selectItem(p.getCategory().getName());

        //amount edittext
        EditText etA = (EditText) this.activity.findViewById(edTxt_amount);
        etA.setText(String.valueOf(p.getAmount()));

        //note edittext
        EditText etN = (EditText) this.activity.findViewById(R.id.edTxt_note);
        etN.setText(p.getNote());

        //store spinner
        CustomSpinner stSpin = (CustomSpinner) this.activity.findViewById(R.id.spinner_store);
        stSpin.selectItem(p.getStore().getName());
    }

    /**
     * Sets texts of insert/update control button.
     * @param editing   a flag indicating whether is a payment being edited or not
     */
    public void setInsertUpdateButtonText(boolean editing) {
        Button btn = (Button) this.activity.findViewById(R.id.btn_ok);

        if (editing) {
            btn.setText(R.string.home_btn_ok_update_label);
        } else {
            btn.setText(R.string.home_btn_ok_insert_label);
        }
    }

    /**
     * Sets texts of clear/cancel control button.
     * @param editing   a flag indicating whether is a payment being edited or not
     */
    public void setClearCancelButtonText(boolean editing){
        Button btn = (Button) this.activity.findViewById(R.id.btn_clear);

        if(editing) {
            btn.setText(R.string.home_btn_cancel_label);
        } else {
            btn.setText(R.string.home_btn_cancel_clear_label);
        }
    }

    /**
     * Updates a height of payment records.
     */
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

    /**
     * Updates a date being currently viewed.
     * @param date  a new date
     */
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
}
