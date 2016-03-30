package cz.ondrejpittl.semestralka;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;

import java.util.Calendar;

public class HomeActivity extends AppCompatActivity {

    private Calendar cal;
    private Button dateButton;



    private int dateDialogID = 0;
    private int date_year, date_month, date_day;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();
    }

    private void init(){
        this.cal = Calendar.getInstance();
        initDateControls();
        initCategoryControls();
        initStoreControls();
    }

    private void initDateControls(){
        this.date_year = this.cal.get(Calendar.YEAR);
        this.date_month = this.cal.get(Calendar.MONTH);
        this.date_day = this.cal.get(Calendar.DAY_OF_MONTH);


        this.dateButton = (Button) findViewById(R.id.btn_date);
        this.dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(dateDialogID);
            }
        });
    }

    private void initCategoryControls(){
        Spinner spinner = (Spinner) findViewById(R.id.spinner_category);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void initStoreControls(){
        Spinner spinner = (Spinner) findViewById(R.id.spinner_store);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.stores, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    protected Dialog onCreateDialog(int id) {
        if(id == this.dateDialogID) {

            DatePickerDialog dpDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker arg0, int y, int m, int d) {
                    updateDateButton(y, m, d);
                    saveSelectedDate(y, m, d);
                }
            }, this.date_year, this.date_month, this.date_day);


            //o 1 den navic oproti StackOverflow! @TODO otestovat!
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, 1);

            DatePicker datePicker = dpDialog.getDatePicker();
            datePicker.setMaxDate(c.getTimeInMillis());
            return dpDialog;
        }
        return null;
    }

    private void saveSelectedDate(int y, int m, int d){
        this.date_year = y;
        this.date_month = m;
        this.date_day = d;
    }

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




}
