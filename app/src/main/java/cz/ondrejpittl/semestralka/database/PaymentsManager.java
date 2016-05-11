package cz.ondrejpittl.semestralka.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.joda.time.DateTime;

import java.lang.reflect.Array;
import java.util.ArrayList;

import cz.ondrejpittl.semestralka.models.Category;
import cz.ondrejpittl.semestralka.models.Payment;
import cz.ondrejpittl.semestralka.partial.JodaCalendar;

/**
 * Created by OndrejPittl on 31.03.16.
 */
public class PaymentsManager extends TableManager {

    /**
     * Table name.
     */
    //private static final String TABLE_NAME = "tb_payments";

    /**
     * Table ID column name.
     */
    private static final String COLUMN_ID = "id";

    /**
     * Table category ID column name.
     */
    private static final String COLUMN_CATEGORY_ID = "id_category";

    /**
     * Table store ID column name.
     */
    private static final String COLUMN_STORE_ID = "id_store";

    /**
     * Table amount column name.
     */
    private static final String COLUMN_AMOUNT = "amount";

    /**
     * Table date column name.
     */
    private static final String COLUMN_DATE = "date";

    /**
     * Table note column name.
     */
    private static final String COLUMN_NOTE = "note";


    /**
     * Table columns alias prefix.
     */
    private static final String ALIAS_PREFIX = "payments_";



    private JodaCalendar jodaCalendar;




    /**
     * Constructor, initializes instance.
     */
    public PaymentsManager(DBManager dbManager) {
        super("tb_payments", dbManager);

        this.jodaCalendar = new JodaCalendar();

        Log.i("Ondra", "Payment Manager constructor");
    }

    /**
     * Creates paymnets table.
     * @param db    db reference
     */
    public void createPaymentsTable(SQLiteDatabase db) {
        this.db = db;

        String[][] args = new String[][] {
                {COLUMN_ID, "integer primary key autoincrement"},
                {COLUMN_CATEGORY_ID, "integer"},
                {COLUMN_STORE_ID, "integer"},
                {COLUMN_AMOUNT, "real"},
                {COLUMN_DATE, "long"},
                {COLUMN_NOTE, "text"}
        };

        createTable(args);
    }

    /**
     * Inserts a new payment record.
     * @param ID    payment id
     * @param catID paymetncategory
     * @param storeID   payment store id
     * @param amount    payment amount
     * @param date      payment date
     * @param note      payment note
     */
    public void insertPayment(String ID, String catID, String storeID, String amount, String date, String note){
        insertRecord(new String[][]{
                {COLUMN_ID, ID},
                {COLUMN_CATEGORY_ID, catID},
                {COLUMN_STORE_ID, storeID},
                {COLUMN_AMOUNT, amount},
                {COLUMN_DATE, date},
                {COLUMN_NOTE, note}
        });
    }

    /**
     * Inserts a new paymnet.
     * @param p payment instance
     */
    public void insertPayment(Payment p){
        insertRecord(new String[][]{
                {COLUMN_CATEGORY_ID, String.valueOf(p.getCategory().getID())},
                {COLUMN_STORE_ID, String.valueOf(p.getStore().getID())},
                {COLUMN_AMOUNT, String.valueOf(p.getAmount())},
                {COLUMN_DATE, String.valueOf(p.getDateLong())},
                {COLUMN_NOTE, String.valueOf(p.getNote())}
        });
    }

    /**
     * Selects all payments.
     * @return  payment collection
     */
    public ArrayList<Payment> selectAllPayments(){
        Cursor c = selectAllRecords();
        ArrayList<Payment> col = this.buildPaymentArraylistFromCursor(c);
        c.close();
        return col;
    }

    /**
     * Selects all payments
     * @param wheres    where criteria
     * @param orderBy   orderby criteria
     * @return          payment collection
     */
    public ArrayList<Payment> selectPayments(String[][] wheres, String[][] orderBy) {
        //wheres: {{"name", "=", "John"}, {"age", "<", 27}}
        //orderBy: {{"id", "asc"}, {"name", "desc"}}
        Cursor c = selectRecord(wheres, orderBy);
        ArrayList<Payment> col = this.buildPaymentArraylistFromCursor(c);
        c.close();
        return col;
    }

    /**
     * Selects all payments.
     * @param tableCols a column list
     * @param tables    a table list
     * @param wheres    where criteria
     * @param orderBy   order by criteria
     * @return          a collection of payments
     */
    public ArrayList<Payment> selectPaymentsDetailed(String tableCols, String tables, String[][] wheres, String[][] orderBy) {
        //examples:
        //tablecols: "a.name, b.id"
        //tables: "tableA a inner join tableB b on a.id = b.id"
        //wheres: {{"name", "=", "John"}, {"age", "<", 27}}
        //orderBy: {{"id", "asc"}, {"name", "desc"}}
        Cursor c = selectRecord(wheres, orderBy);
        ArrayList<Payment> col = this.buildPaymentArraylistFromCursorAliased(c);
        c.close();

        return col;
    }

    /**
     * Computes a category summary of a month.
     * @param month     a month
     * @param year      a year
     * @param category  a category
     * @return          a payment summary collection
     */
    public ArrayList<Payment> selectCategorySummaryOfMonth(int month, int year, String category){
        long from = this.jodaCalendar.getFirstDayOfMonth(month, year).getMillis(),
                to = this.jodaCalendar.getLastDayOfMonth(month, year).getMillis();

        return this.selectCategorySummaryIn(from, to, category);
    }

    /**
     * Computes a category summary of a month.
     * @param year      a year
     * @param category  a category
     * @return          a payment summary collection
     */
    public ArrayList<Payment> selectCategorySummaryOfYear(int year, String category){
        long from = this.jodaCalendar.getFirstDayOfYear(year).getMillis(),
               to = this.jodaCalendar.getLastDayOfYear(year).getMillis();

        return this.selectCategorySummaryIn(from, to, category);
    }

    /**
     * Computes a category summary of a term.
     * @param from  from date
     * @param to    to date
     * @param category  a category
     * @return          a payment summary collection
     */
    public ArrayList<Payment> selectCategorySummaryIn(long from, long to, String category){
        String  tableCols = "sum(tb_payments.amount) as amount, tb_categories.id as categories_id, tb_categories.name",
                tables = "tb_payments inner join tb_categories on tb_payments.id_category = tb_categories.id";

        int limit = -1;

        Cursor c = this.selectRecordsDetailed(
                tableCols,
                tables,
                new String[][]{
                        {PaymentsManager.COLUMN_DATE, ">=", "" + String.valueOf(from)},
                        {PaymentsManager.COLUMN_DATE, "<=", String.valueOf(to)},
                        {CategoryManager.getColumnName(), "like", "%" + category + "%"}
                },
                new String[]{CategoryManager.getColumnIdAliased()},
                new String[][]{{PaymentsManager.COLUMN_DATE, "asc"}},
                limit);


        ArrayList<Payment> payments = new ArrayList<Payment>();
        c.moveToFirst();
        while(c.isAfterLast() == false){
            Payment p = new Payment();
            p.setAmount(Float.parseFloat(c.getString(c.getColumnIndex("amount"))));
            p.setCategory(Integer.parseInt(c.getString(c.getColumnIndex("categories_id"))),
                    c.getString(c.getColumnIndex("name")));
            payments.add(p);
            c.moveToNext();
        }

        c.close();

        return payments;
    }

    /**
     * Selects all payments with where criteria.
     * @param wheres    wheres criteria
     * @param orderBy   order by criteria
     * @return          a collection of payments
     */
    private ArrayList<Payment> selectPaymentsOfMonthWhere(String wheres[][], String orderBy[][]) {
        String  tableCols = this.dbManager.getAllTableColumnsList(),
                tables = this.dbManager.getAllTablesList();

        int limit = -1;

        Cursor c = this.selectRecordsDetailed(tableCols, tables, wheres, null, orderBy, limit);
        ArrayList<Payment> col = this.buildPaymentArraylistFromCursorAliased(c);
        c.close();
        return col;
    }

    /**
     * Selects payments of a month.
     * @param month     a month
     * @param year      a year
     * @param category  a category
     * @return          a collection of paymnets
     */
    public ArrayList<Payment> selectPaymentsOfMonth(int month, int year, String category) {
        long from = this.jodaCalendar.getFirstDayOfMonth(month, year).getMillis(),
                to = this.jodaCalendar.getLastDayOfMonth(month, year).getMillis();

        return this.selectPaymentsOf(from, to, category);
    }

    /**
     * Selects payments of a year
     * @param year      a year
     * @param category  a category
     * @return          a collection of payments
     */
    public ArrayList<Payment> selectPaymentsOfYear(int year, String category) {
        long from = this.jodaCalendar.getFirstDayOfYear(year).getMillis(),
               to = this.jodaCalendar.getLastDayOfYear(year).getMillis();

        return this.selectPaymentsOf(from, to, category);
    }

    /**
     * Selects payments in a term.
     * @param from      from date
     * @param to        to date
     * @param category  a category
     * @return          a collection of payments
     */
    public ArrayList<Payment> selectPaymentsOf(long from, long to, String category) {
        String wheres[][] =  new String[][]{
                {PaymentsManager.getColumnDateAliased(), ">=", "" + String.valueOf(from)},
                {PaymentsManager.getColumnDateAliased(), "<=", String.valueOf(to)},
                {CategoryManager.getColumnNameAliased(), "like", "%" + category + "%"}
        },
                orderBy[][] = new String[][]{{PaymentsManager.getColumnDateAliased(), "asc"}};

        return this.selectPaymentsOfMonthWhere(wheres, orderBy);
    }

    /**
     * Selects payments of month.
     * @param month a month
     * @param year  a year
     * @return      a collection of payments
     */
    public ArrayList<Payment> selectPaymentsOfMonth(int month, int year) {
        long from = this.jodaCalendar.getFirstDayOfMonth(month, year).getMillis(),
                to = this.jodaCalendar.getLastDayOfMonth(month, year).getMillis();

        return this.selectPaymentsOf(from, to);
    }

    /**
     * Selects payments in a term.
     * @param from  from date
     * @param to    to date
     * @return      a collection of payments
     */
    public ArrayList<Payment> selectPaymentsOf(long from, long to) {
        String wheres[][] =  new String[][]{
                {PaymentsManager.getColumnDateAliased(), ">=", "" + from},
                {PaymentsManager.getColumnDateAliased(), "<=", "" + to}
        },
                orderBy[][] = new String[][]{{PaymentsManager.getColumnDateAliased(), "desc"}};

        return this.selectPaymentsOfMonthWhere(wheres, orderBy);
    }

    /**
     * Getter of aliased ID column.
     * @return  aliased column name
     */
    public static String getColumnIdAliased(){
        return ALIAS_PREFIX + COLUMN_ID;
    }

    /**
     * Getter of aliased category ID column.
     * @return  aliased column name
     */
    public static String getColumnCategoryIdAliased(){
        return ALIAS_PREFIX + COLUMN_CATEGORY_ID;
    }

    /**
     * Getter of aliased store ID column.
     * @return  aliased column name
     */
    public static String getColumnStoreIdAliased(){
        return ALIAS_PREFIX + COLUMN_STORE_ID;
    }

    /**
     * Getter of aliased amount column.
     * @return  aliased column name
     */
    public static String getColumnAmountAliased(){
        return ALIAS_PREFIX + COLUMN_AMOUNT;
    }

    /**
     * Getter of aliased date column.
     * @return  aliased column name
     */
    public static String getColumnDateAliased(){
        return ALIAS_PREFIX + COLUMN_DATE;
    }

    /**
     * Getter of aliased note column.
     * @return  aliased column name
     */
    public static String getColumnNoteAliased(){
        return ALIAS_PREFIX + COLUMN_NOTE;
    }


    /**
     * Getter of all aliased columns.
     * @return  aliased column name list
     */
    public String getAllColumnsSelector(){
        return TABLE_NAME + "." + COLUMN_ID + " as " + getColumnIdAliased() + ", "
                + TABLE_NAME + "." + COLUMN_CATEGORY_ID + " as " + getColumnCategoryIdAliased() + ", "
                + TABLE_NAME + "." + COLUMN_STORE_ID + " as " + getColumnStoreIdAliased() + ", "
                + TABLE_NAME + "." + COLUMN_AMOUNT + " as " + getColumnAmountAliased() + ", "
                + TABLE_NAME + "." + COLUMN_DATE + " as " + getColumnDateAliased() + ", "
                + TABLE_NAME + "." + COLUMN_NOTE + " as " + getColumnNoteAliased();
    }

    /**
     * Transforms data from a cursor into a collection of payments (aliased).
     * @param c a cursor reference
     * @return  a payment collection
     */
    private ArrayList<Payment> buildPaymentArraylistFromCursorAliased(Cursor c){
        ArrayList<Payment> payments = new ArrayList<Payment>();

        c.moveToFirst();
        while(c.isAfterLast() == false){
            Payment p = new Payment();
            p.setID(Integer.parseInt(c.getString(c.getColumnIndex(getColumnIdAliased()))));
            p.setCategory(Integer.parseInt(c.getString(c.getColumnIndex(getColumnCategoryIdAliased()))), c.getString(c.getColumnIndex(CategoryManager.getColumnNameAliased())), c.getString(c.getColumnIndex(CategoryManager.getColumnIconAliased())));
            p.setStore(Integer.parseInt(c.getString(c.getColumnIndex(getColumnStoreIdAliased()))), c.getString(c.getColumnIndex(StoresManager.getColumnNameAliased())));
            p.setAmount(Float.parseFloat(c.getString(c.getColumnIndex(getColumnAmountAliased()))));
            p.setDate(Long.parseLong(c.getString(c.getColumnIndex(getColumnDateAliased()))));
            p.setNote(c.getString(c.getColumnIndex(getColumnNoteAliased())));
            payments.add(p);
            c.moveToNext();
        }

        return payments;
    }

    /**
     * Transforms data from a cursor into a collection of payments.
     * @param c a cursor reference
     * @return  a payment collection
     */
    private ArrayList<Payment> buildPaymentArraylistFromCursor(Cursor c){
        ArrayList<Payment> payments = new ArrayList<Payment>();

        c.moveToFirst();
        while(c.isAfterLast() == false){
            Payment p = new Payment();
            p.setID(Integer.parseInt(c.getString(c.getColumnIndex(COLUMN_ID))));
            p.setCategory(Integer.parseInt(c.getString(c.getColumnIndex(COLUMN_CATEGORY_ID))), "", "");
            p.setStore(Integer.parseInt(c.getString(c.getColumnIndex(COLUMN_STORE_ID))), "");
            p.setAmount(Float.parseFloat(c.getString(c.getColumnIndex(COLUMN_AMOUNT))));
            p.setDate(Long.parseLong(c.getString(c.getColumnIndex(COLUMN_DATE))));
            p.setNote(c.getString(c.getColumnIndex(COLUMN_NOTE)));
            payments.add(p);
            c.moveToNext();
        }

        return payments;
    }

    /**
     * Deletes a payment.
     * @param wheres    where criteria
     * @return          a number of rows affected
     */
    public int deletePayment(String[][] wheres){
        return deleteRecord(wheres);
    }

    /**
     * Delete all payments.
     * @return  a number of rows affected
     */
    public int deletePayments(){
        return deleteRecords();
    }

    /**
     * Updates a payment.
     * @param changes   an array of changes
     * @param wheres    where criteria
     * @return
     */
    public int updatePayment(String[][] changes, String[][] wheres) {
        return updateRecord(changes, wheres);
    }

    /**
     * Selects payments in a term.
     * @param from  from date
     * @param to    to date
     * @return      a collection of payments
     */
    private ArrayList<Payment> getPaymentsIn(long from, long to){
        return this.selectPayments(
                new String[][]{
                        {"date", ">=", String.valueOf(from)},
                        {"date", "<=", String.valueOf(to)}
                }, null);
    }

    /**
     * Summarizes all amounts of payments given.
     * @param payments  payments given
     * @return          total amount
     */
    private int computeAmount(ArrayList<Payment> payments) {
        int sum = 0;

        for (Payment p : payments) {
            sum += p.getAmount();
        }

        return sum;
    }

    /**
     * Logs a term.
     * @param from  from date
     * @param to    to date
     * @param label a label
     */
    private void reportTimes(long from, long to, String label){
        Log.i("Ondra", label + "-from: " + new DateTime(from));
        Log.i("Ondra", label + "-to: " + new DateTime(to));
    }

    /**
     * Computes today payment amount.
     * @return  a sum of today payments
     */
    public int computeTodayPaymentAmount(){
        long from = this.jodaCalendar.getStartOfDay().getMillis(),
               to = this.jodaCalendar.getEndOfDay().getMillis();

        //reportTimes(from, to, "TODAY");
        return computeAmount(getPaymentsIn(from, to));
    }

    /**
     * Computes week payment amount.
     * @return  a sum of week payments
     */
    public int computeWeekPaymentAmount(){
        long from = this.jodaCalendar.getFirstDayOfWeek().getMillis(),
               to = this.jodaCalendar.getLastDayOfWeek().getMillis();

        //reportTimes(from, to, "WEEK");
        return computeAmount(getPaymentsIn(from, to));
    }

    /**
     * Computes month payment amount.
     * @return  a sum of month payments
     */
    public int computeMonthPaymentAmount() {
        long from = this.jodaCalendar.getFirstDayOfMonth().getMillis(),
                to = this.jodaCalendar.getLastDayOfMonth().getMillis();

        //reportTimes(from, to, "MONTH");
        return computeAmount(getPaymentsIn(from, to));
    }

    /**
     * Computes month payment amount.
     * @param month a month
     * @param year a year
     * @return  a sum of month payments
     */
    public int computeMonthPaymentAmount(int month, int year) {
        long from = this.jodaCalendar.getFirstDayOfMonth(month, year).getMillis(),
                to = this.jodaCalendar.getLastDayOfMonth(month, year).getMillis();

        //reportTimes(from, to, "MONTH");
        return computeAmount(getPaymentsIn(from, to));
    }

    /**
     * Computes year payment amount.
     * @return  a sum of year payments
     */
    public int computeYearPaymentAmount(){
        long from = this.jodaCalendar.getFirstDayOfYear().getMillis(),
                to = this.jodaCalendar.getLastDayOfYear().getMillis();

        reportTimes(from, to, "YEAR");
        return computeAmount(getPaymentsIn(from, to));
    }

    /**
     * Computes total payment amount.
     * @return  a payment total amount
     */
    public int computeTotalPaymentAmount(){
        return computeAmount(selectAllPayments());
    }
}
