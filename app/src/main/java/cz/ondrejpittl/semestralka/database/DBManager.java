package cz.ondrejpittl.semestralka.database;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;

import cz.ondrejpittl.semestralka.models.Category;
import cz.ondrejpittl.semestralka.models.Currency;
import cz.ondrejpittl.semestralka.models.Payment;
import cz.ondrejpittl.semestralka.models.Statistics;
import cz.ondrejpittl.semestralka.models.Store;

/**
 * Created by OndrejPittl on 31.03.16.
 */
public class DBManager extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 5;

    private static final String DATABASE_NAME = "billstracker_database";

    private PaymentsManager payments;
    private StoresManager stores;
    private CategoryManager categories;
    private CurrencyManager currencies;

    protected SQLiteDatabase db;

    private Context context;




    public DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        this.context = context;

        Log.i("Ondra", "DB Manager constructor");


        this.payments = new PaymentsManager(this);
        this.stores = new StoresManager(this);
        this.categories = new CategoryManager(this);
        this.currencies = new CurrencyManager(this);

        Log.i("Ondra", "All table managers initialised.");


        this.db = getWritableDatabase();


        Log.i("Ondra", "All tables created.");

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        this.payments.createPaymentsTable(db);

        this.payments.insertPayment("1", "2", "3", "123", "1459461600000", "prvního");
        this.payments.insertPayment("2", "3", "4", "234", "1462053599999", "posledního");
        this.payments.insertPayment("3", "4", "5", "345", "1462060599999", "moc pozdě");
        this.payments.insertPayment("4", "5", "6", "456", "1459460600000", "moc brzo");
        this.payments.insertPayment("5", "6", "7", "567", "1459469600000", "akorát");


        Resources res = this.context.getResources();
        this.stores.createStoresTable(db, res);
        this.categories.createCategoriesTable(db, res);
        this.currencies.createCurrenciesTable(db, res);

        Log.i("Ondra", "All table managers initialised.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + this.payments.getTableName());
        db.execSQL("DROP TABLE IF EXISTS " + this.stores.getTableName());
        db.execSQL("DROP TABLE IF EXISTS " + this.categories.getTableName());
        db.execSQL("DROP TABLE IF EXISTS " + this.currencies.getTableName());
        Log.i("Ondra", "TABLES DROPPED.");
        onCreate(db);
    }

    public ArrayList<Store> getStoredStores(){
        return this.stores.selectAllStoresFrequencyOrdered();
    }

    public ArrayList<Category> getStoredCategories(){
        return this.categories.selectAllCategories();
    }

    public ArrayList<Payment> getPaymentsByMonth(int month, int year){
        return this.payments.selectPaymentsOfMonth(month, year);
    }

    /*
    public String getDefaultCurrency(){
        String[] defaults = {"USD", "EUR"};

        for(int i = 0; i < defaults.length; i++) {
            if(this.currencies.checkIfCurrencyExists(defaults[i])) {
                return defaults[i];
            }
        }

        Log.i("Ondra", "DEFAULT CURRENCY2: not found, getting first");

        return this.currencies.getFirstCurrency();
    }
    */

    public void updateDefaultCurrency(String curr){
        if(this.currencies.checkIfCurrencyExists(curr))
            return;
        this.currencies.insertCurrency(curr);
    }

    public String getAllTableColumnsList(){
        return  this.payments.getAllColumnsSelector() + ", " +
                this.categories.getAllColumnsSelector() + ", " +
                this.stores.getAllColumnsSelector();
    }

    public Statistics computeCurrentStatistics(){
        int today, week,
            month, year, total;

        today = this.payments.computeTodayPaymentAmount();
        week = this.payments.computeWeekPaymentAmount();
        month = this.payments.computeMonthPaymentAmount();
        year = this.payments.computeYearPaymentAmount();
        total = this.payments.computeTotalPaymentAmount();

        Statistics s = new Statistics();
        s.setTodayAmount(today);
        s.setWeekAmount(week);
        s.setMonthAmount(month);
        s.setYearAmount(year);
        s.setTotalAmount(total);

        return s;
    }

    public Statistics computeOtherStatistics(int month, int year){
        int monthStats = this.payments.computeMonthPaymentAmount(month, year);

        Statistics s = new Statistics();
        s.setMonthAmount(monthStats);
        return s;
    }

    public void insertNewPayment(Payment p){
        this.payments.insertPayment(p);
    }

    public void deletePayment(int id){
        this.payments.deletePayment(new String[][]{
                {"id", "=", String.valueOf(id)}
        });
    }

    public void updatePayment(Payment p){
        /*this.payments.deletePayment(new String[][]{
                {"id", "=", String.valueOf(id)}
        });*/

        this.payments.updatePayment(new String[][]{
                {"id_category", String.valueOf(p.getCategory().getID())},
                {"id_store", String.valueOf(p.getStore().getID())},
                {"amount", String.valueOf(p.getAmount())},
                {"date", String.valueOf(p.getDateLong())},
                {"note", p.getNote()}
        }, new String[][]{
                {"id", "=", String.valueOf(p.getID())}
        });
    }



    public SQLiteDatabase getDB(){
        return this.db;
    }
}


/*

    PAYMENTS EXAMPLES!

        Log.i("Ondra", "----------------------");

        this.payments.insertPayment("0", "1", "2", "123456", "pozn");
        this.payments.insertPayment("1", "3", "9.90", "234567", "poznáááámkáááá");
        this.payments.insertPayment("0", "1", "2", "123456", "pozn");
        this.payments.insertPayment("1", "3", "9.90", "234567", "poznáááámkáááá");
        this.payments.insertPayment("0", "1", "2", "123456", "pozn");
        this.payments.insertPayment("1", "3", "9.90", "234567", "poznáááámkáááá");

        Log.i("Ondra", "----------------------");

        //this.payments.insertPayment("cat0", "store1", "2Kč", "dnes", "pozn");
        for(Payment p : this.payments.selectAllPayments()) {
            Log.i("Ondra", p.toString());
        }

        Log.i("Ondra", "----------------------");

        for(Payment p : this.payments.selectPayments(new String[][]{{"id_store", "<", "3"}, {"amount", "=", "2"}})) {
            Log.i("Ondra", p.toString());
        }

        Log.i("Ondra", "----------------------");

        Log.i("Ondra", "deleted: " + this.payments.deletePayment(new String[][]{{"id", "<", "4"}, {"amount", "=", "2"}}));

        Log.i("Ondra", "----------------------");

        Log.i("Ondra", "updated: " + this.payments.updatePayment(
                new String[][]{{"amount", "7.99"}, {"id_store", "222"}},
                new String[][]{{"id", ">", "5"}, {"amount", ">", "5"}}
        ));

        Log.i("Ondra", "----------------------");

        Log.i("Ondra", "total count: " + this.payments.getPaymentsCount());

*/