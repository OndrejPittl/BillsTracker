package cz.ondrejpittl.semestralka.database;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import cz.ondrejpittl.semestralka.models.Category;
import cz.ondrejpittl.semestralka.models.Currency;
import cz.ondrejpittl.semestralka.models.Payment;
import cz.ondrejpittl.semestralka.models.Statistics;
import cz.ondrejpittl.semestralka.models.Store;

/**
 * Created by OndrejPittl on 31.03.16.
 */
public class DBManager extends SQLiteOpenHelper {

    /**
     * Database version.
     */
    //private static final int DATABASE_VERSION = 6;

    /**
     * Database name.
     */
    private static final String DATABASE_NAME = "billstracker_database";

    /**
     * Payment management module.
     */
    private PaymentsManager payments;

    /**
     * Store management module.
     */
    private StoresManager stores;

    /**
     * Category management module.
     */
    private CategoryManager categories;

    /**
     * Currencies management module.
     */
    private CurrencyManager currencies;

    /**
     * Database reference.
     */
    protected SQLiteDatabase db;

    /**
     * Activity context.
     */
    private Context context;


    /**
     * A constructor. Initializes DB layer.
     * @param context   activity context
     */
    public DBManager(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;

        this.payments = new PaymentsManager(this);
        this.stores = new StoresManager(this);
        this.categories = new CategoryManager(this);
        this.currencies = new CurrencyManager(this);

        this.db = getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        this.payments.createPaymentsTable(db);

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

    /**
     * Getter of stored stores.
     * @return  stored stores
     */
    public ArrayList<Store> getStoredStores(){
        return this.stores.selectAllStoresAlphabeticalOrdered();
    }

    /**
     * Getter of stored categories.
     * @return  stored categories
     */
    public ArrayList<Category> getStoredCategories(){
        return this.categories.selectAllCategoriesFrequencyOrdered();
    }

    /**
     * Getter of stored currencies.
     * @return  stored currencies
     */
    public ArrayList<Currency> getStoredCurrencies(){
        return this.currencies.selectAllCurrency();
    }

    /**
     * Selects payments of a month.
     * @param month month
     * @param year  year
     * @return      payments of a month
     */
    public ArrayList<Payment> getPaymentsByMonth(int month, int year){
        return this.payments.selectPaymentsOfMonth(month, year);
    }

    /**
     * Selects payments of a month parametrized with a category.
     * @param month month
     * @param year  year
     * @param category  category
     * @return      payments of a month
     */
    public ArrayList<Payment> getPaymentsByMonth(int month, int year, String category){
        return this.payments.selectPaymentsOfMonth(month, year, category);
    }

    /**
     * Selects summarized payments of a month parametrized with a category.
     * @param month month
     * @param year  year
     * @param category  category
     * @return      payments of a month
     */
    public ArrayList<Payment> getCategorySummaryByMonth(int month, int year, String category){
        return this.payments.selectCategorySummaryOfMonth(month, year, category);
    }

    /**
     * Selects all payments of a year parammetrized with a category.
     * @param year      year
     * @param category  category
     * @return          payments of a year
     */
    public ArrayList<Payment> getPaymentsByYear(int year, String category){
        return this.payments.selectPaymentsOfYear(year, category);
    }

    /**
     * Select summary of a category.
     * @param year      year
     * @param category  category
     * @return          summary
     */
    public ArrayList<Payment> getCategorySummaryByYear(int year, String category){
        return this.payments.selectCategorySummaryOfYear(year, category);
    }

    /**
     * Updates default currency.
     * @param curr  default currency
     */
    public void updateDefaultCurrency(String curr){
        if(this.currencies.checkIfCurrencyExists(curr))
            return;
        this.currencies.insertCurrency(curr);
    }

    /**
     * Gets a list of all table columns.
     * @return  selector
     */
    public String getAllTableColumnsList(){
        return  this.payments.getAllColumnsSelector() + ", " +
                this.categories.getAllColumnsSelector() + ", " +
                this.stores.getAllColumnsSelector();
    }

    /**
     * Getter of all table list.
     * @return  table list
     */
    public String getAllTablesList(){
        return "tb_payments inner join tb_categories on tb_payments.id_category = tb_categories.id " +
                        "inner join tb_stores on tb_payments.id_store = tb_stores.id";
    }

    /**
     * Checks if a DB is empty.
     * @param month month
     * @param year  year
     * @return      true – exists; false – not
     */
    public boolean isDBEmpty(int month, int year){
        ArrayList<Payment> payments = getPaymentsByMonth(month, year);
        return payments.size() == 0;
    }

    /**
     * Computes cmopact statistics.
     * @return  statistics element
     */
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

    /**
     * Computes other statistics.
     * @param month month
     * @param year  year
     * @return      statistics object
     */
    public Statistics computeOtherStatistics(int month, int year){
        int monthStats = this.payments.computeMonthPaymentAmount(month, year);

        Statistics s = new Statistics();
        s.setMonthAmount(monthStats);
        return s;
    }

    /**
     * Inserts a new paymnet.
     * @param p a new payment
     */
    public void insertNewPayment(Payment p){
        this.payments.insertPayment(p);
    }

    /**
     * Inserts a new category.
     * @param title a tittle
     */
    public void insertNewCategory(String title){
        this.categories.insertCategory(title, "@drawable/category_icon_default");
    }

    /**
     * Inserts a new Store..
     * @param title a title
     */
    public void insertNewStore(String title){
        this.stores.insertStore(title);
    }

    /**
     * Deletes a payment.
     * @param id    an id of a payment being deleted
     */
    public void deletePayment(int id){
        this.payments.deletePayment(new String[][]{
                {"id", "=", String.valueOf(id)}
        });
    }

    /**
     * Deletes a category.
     * @param id     an id of a category
     */
    public void deleteCategory(int id){
        this.categories.deleteCategory(new String[][]{{"id", "=", String.valueOf(id)}});
    }

    /**
     * Deletes a store.
     * @param id an id of store
     */
    public void deleteStore(int id){
        this.stores.deleteStore(new String[][]{{"id", "=", String.valueOf(id)}});
    }

    /**
     * Updates payment.
     * @param p  a payment reference
     */
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

    /**
     * Eraases all paymnets.
     */
    public void eraseAllPayments(){
        this.payments.deletePayments();
    }

    /**
     * Getter of a db reference.
     * @return  a db reference
     */
    public SQLiteDatabase getDB(){
        return this.db;
    }

    private void insertMockPayments(){
        //April payments
        this.payments.insertPayment("1", "2", "3", "123", "1459461600000", "prvního");
        this.payments.insertPayment("2", "3", "4", "234", "1462053599999", "posledního");
        this.payments.insertPayment("3", "4", "5", "345", "1462060599999", "moc pozdě");
        this.payments.insertPayment("4", "5", "6", "456", "1459460600000", "moc brzo");
        this.payments.insertPayment("5", "6", "7", "567", "1459469600000", "akorát");
    }
}
