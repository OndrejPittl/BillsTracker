package cz.ondrejpittl.semestralka;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Locale;

import cz.ondrejpittl.semestralka.controllers.StatsDataController;
import cz.ondrejpittl.semestralka.controllers.StatsUIController;
import cz.ondrejpittl.semestralka.database.DBManager;
import cz.ondrejpittl.semestralka.models.Category;
import cz.ondrejpittl.semestralka.models.Statistics;
import cz.ondrejpittl.semestralka.partial.StatisticsChartObject;

public class StatisticsActivity extends AppCompatActivity {

    /**
     * Stored origin locale enables temporarily change locale to force ENG lang and then setting
     * origin locale back.
     *
     */
    private static Locale originLocale;

    private DateTime calendar;
    //private Calendar calendar;
    private String category;


    private StatsUIController controllerUI;
    private StatsDataController controllerData;
    private DBManager dbManager;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        this.init();
    }

    private void init(){
        this.originLocale = Locale.getDefault();
        this.calendar = new DateTime();
        //this.calendar = Calendar.getInstance();

        //Log.i("Ondra-stats", this.calendar.getTime().toString());
        Log.i("Ondra-stats", this.calendar.toDateTime().toString());
        this.category = "";

        //view layer
        this.controllerUI = new StatsUIController(this);

        //model layer
        this.dbManager = new DBManager(this);

        //data
        this.controllerData = new StatsDataController(this);

        this.buildStatistics();
        //this.controllerUI.buildStats();
    }


    private void buildStatistics(){
        int month = this.calendar.getMonthOfYear(),
            year = this.calendar.getYearOfEra();

        Log.i("Ondra-stats", "init: " + this.calendar.toDateTime());


        /*int month = this.calendar.get(Calendar.MONTH),
                year = this.calendar.get(Calendar.YEAR);

        Log.i("Ondra-stats", "init: " + this.calendar.getTime());*/

        StatisticsChartObject monthStatsData = this.controllerData.computeMonthStatsData(month, year, this.category);
        this.controllerUI.setMonthStatsData(monthStatsData);

        /*StatisticsChartObject yearStatsData = this.controllerData.computeYearStatsData(month, year, this.category);
        this.controllerUI.setYearStatsData(yearStatsData);*/

        this.controllerUI.buildStats();
    }


    public void handleDisplayCategoryChange(Category category){
        Log.i("Ondra-stats", "selected category – id: " + category.getID() + ", name: " + category.getName());

        if(category.getID() == -1) {
            this.category = "";
        } else {
            this.category = category.getName();
        }

        this.buildStatistics();
    }


    public void handleDisplayedDateChange(int y, int m, int d){
        /*Log.i("Ondra-stats", "selected-y: " + y);
        Log.i("Ondra-stats", "selected-m: " + m);
        Log.i("Ondra-stats", "selected-d: " + d);*/

        this.calendar = this.calendar.withMonthOfYear(m).withDayOfMonth(d).withYearOfEra(y);
        this.buildStatistics();
    }

    /**
     * Builds DatePicker dialog.
     * @param id    ID of dialog.
     * @return      Built DatePicker dialog.
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        return this.controllerUI.handleDatePickerDialogCreation(id, this.calendar.toDateTime());
    }

    public static void changeLocaleDefault(){
        Locale.setDefault(StatisticsActivity.originLocale);
    }

    public static void changeLocaleUS(){
        Locale.setDefault(Locale.US);
    }

    public DBManager getDbManager(){
        return this.dbManager;
    }

    public StatsUIController getControllerUI(){
        return this.controllerUI;
    }
}
