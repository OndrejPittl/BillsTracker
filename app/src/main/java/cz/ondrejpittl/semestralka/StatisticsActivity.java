package cz.ondrejpittl.semestralka;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.joda.time.DateTime;

import java.util.Locale;

import cz.ondrejpittl.semestralka.controllers.StatsDataController;
import cz.ondrejpittl.semestralka.controllers.StatsUIController;
import cz.ondrejpittl.semestralka.database.DBManager;
import cz.ondrejpittl.semestralka.models.Category;
import cz.ondrejpittl.semestralka.partial.Designer;
import cz.ondrejpittl.semestralka.partial.StatisticsChartObject;

public class StatisticsActivity extends AppCompatActivity {

    /**
     * Intent identification.
     */
    public static final int INTENT_INDEX = 2;

    /**
     * Stored origin locale enables temporarily change locale to force ENG lang and then setting
     * origin locale back.
     *
     */
    private static Locale originLocale;

    /**
     *
     */
    private DateTime calendar;

    /**
     *
     */
    private String category;

    /**
     * Activity UI controller.
     */
    private StatsUIController controllerUI;

    /**
     * Activity Model controller.
     */
    private StatsDataController controllerData;

    /**
     * Activity model layer reference.
     */
    private DBManager dbManager;


    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Ondra-init", "initializing Statistics activity");

        super.onCreate(savedInstanceState);
        Designer.setFullscreenActivity(this);
        setContentView(R.layout.activity_statistics);

        this.init();
    }

    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }

    /**
     * Activity initialization.
     */
    private void init(){
        Designer.updateDesign(this);
        this.originLocale = Locale.getDefault();
        this.calendar = new DateTime();

        Log.i("Ondra-debugLand", "-------------------------------------");
        Log.i("Ondra-debugLand", "initializing Statistics Activity");
        Log.i("Ondra-stats", this.calendar.toDateTime().toString());

        this.category = "";

        //view layer
        this.controllerUI = new StatsUIController(this);

        //model layer
        this.dbManager = new DBManager(this);

        //data
        this.controllerData = new StatsDataController(this);

        //build stats
        this.buildStatistics();
    }

    /**
     * Builds statistics data and fires chart drawing.
     */
    private void buildStatistics(){
        int month = this.calendar.getMonthOfYear(),
            year = this.calendar.getYearOfEra();

        Log.i("Ondra-stats", "init: " + this.calendar.toDateTime());

        //Month stats
        StatisticsChartObject monthStatsData = this.controllerData.computeMonthStatsData(month, year, this.category);
        this.controllerUI.setMonthStatsData(monthStatsData);

        //Year stats
        StatisticsChartObject yearStatsData = this.controllerData.computeYearStatsData(year, this.category);
        this.controllerUI.setYearStatsData(yearStatsData);

        //fire drawing
        this.controllerUI.drawStats();
    }

    /**
     * Category change event handler.
     * Rebuilds statistics data and redraws charts.
     * @param category  changed category
     */
    public void handleDisplayCategoryChange(Category category){
        Log.i("Ondra-stats", "selected category – id: " + category.getID() + ", name: " + category.getName());

        if(category.getID() == -1) {
            this.category = "";
        } else {
            this.category = category.getName();
        }

        this.buildStatistics();
    }


    /**
     * Date change event handler.
     * Rebuilds statistics data and redraws charts.
     *
     * @param y changed year
     * @param m changed month
     * @param d changed day
     */
    public void handleDisplayedDateChange(int y, int m, int d){
        this.calendar = this.calendar.withMonthOfYear(m).withDayOfMonth(d).withYearOfEra(y);
        this.buildStatistics();
    }

    /**
     * Builds DatePicker dialog.
     * @param id    ID of dialog.
     * @return      Built DatePicker dialog.
     */
    protected Dialog onCreateDialog(int id) {
        return this.controllerUI.handleDatePickerDialogCreation(id, this.calendar.toDateTime());
    }

    /**
     * Restores default locale back to original one.
     */
    public static void changeLocaleDefault(){
        Locale.setDefault(StatisticsActivity.originLocale);
    }

    /**
     * Sets default locale to US one.
     * Forcing SDK components use american format.
     */
    public static void changeLocaleUS(){
        Locale.setDefault(Locale.US);
    }

    /**
     * Model layer reference getter.
     * @return  model layer reference
     */
    public DBManager getDbManager(){
        return this.dbManager;
    }

    /**
     * UI controller reference getter.
     * @return  UI controller reference
     */
    public StatsUIController getControllerUI(){
        return this.controllerUI;
    }
}
