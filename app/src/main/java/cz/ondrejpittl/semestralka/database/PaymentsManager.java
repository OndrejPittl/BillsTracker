package cz.ondrejpittl.semestralka.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.ArrayList;
import java.util.Calendar;
import cz.ondrejpittl.semestralka.models.Payment;

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
     * Constructor, initializes instance.
     */
    public PaymentsManager(DBManager dbManager) {
        super("tb_payments", dbManager);

        Log.i("Ondra", "Payment Manager constructor");




        /*insertPayment("cat0", "store1", "2Kč", "dnes", "pozn");

        selectAllPayments();

        selectPayments(new String[][]{
                {"name", "=", "John"}, {"age", "<", "27"}
        });

        deletePayment(new String[][]{
                {"name", "=", "John"}, {"age", "<", "27"}
        });

        updatePayment(new String[][]{
                {"name", "Johnn"}, {"age", "277"}
        }, new String[][]{
                {"name", "=", "John"}, {"age", "<", "27"}
        });

        getPaymentsCount();*/
    }

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

    // TODO: 02.04.16 pouze pro VYVOJ
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

    //insertPayment(Payment p){}
    public void insertPayment(String catID, String storeID, String amount, String date, String note){
        insertRecord(new String[][]{
                {COLUMN_CATEGORY_ID, catID},
                {COLUMN_STORE_ID, storeID},
                {COLUMN_AMOUNT, amount},
                {COLUMN_DATE, date},
                {COLUMN_NOTE, note}
        });
    }

    public ArrayList<Payment> selectAllPayments(){
        Cursor c = selectAllRecords();
        return this.buildPaymentArraylistFromCursor(c);
    }

    public ArrayList<Payment> selectPayments(String[][] wheres, String[][] orderBy) {
        //wheres: {{"name", "=", "John"}, {"age", "<", 27}}
        //orderBy: {{"id", "asc"}, {"name", "desc"}}
        Cursor c = selectRecord(wheres, orderBy);
        return this.buildPaymentArraylistFromCursor(c);
    }

    public ArrayList<Payment> selectPaymentsOfMonth(int month, int year) {
        long from, to;

        from = firstDayOfMonth(month, year);
        Log.i("Ondra", "from: " + from);

        to = lastDayOfMonth(month, year);
        Log.i("Ondra", "to: " + to);

        return this.selectPayments(new String[][]{
                {"date", ">=", "" + from},
                {"date", "<=", "" + to }

        }, new String[][]{{"date", "desc"}});
    }

    private ArrayList<Payment> buildPaymentArraylistFromCursor(Cursor c){
        ArrayList<Payment> payments = new ArrayList<Payment>();

        c.moveToFirst();
        while(c.isAfterLast() == false){
            Payment p = new Payment();
            p.setID(Integer.parseInt(c.getString(c.getColumnIndex(COLUMN_ID))));
            p.setCategory(Integer.parseInt(c.getString(c.getColumnIndex(COLUMN_CATEGORY_ID))), "");
            p.setStore(Integer.parseInt(c.getString(c.getColumnIndex(COLUMN_STORE_ID))), "");
            p.setAmount(Float.parseFloat(c.getString(c.getColumnIndex(COLUMN_AMOUNT))));
            p.setDate(Long.parseLong(c.getString(c.getColumnIndex(COLUMN_DATE))));
            p.setNote(c.getString(c.getColumnIndex(COLUMN_NOTE)));
            payments.add(p);
            c.moveToNext();
        }

        return payments;
    }

    public int deletePayment(String[][] wheres){
        return deleteRecord(wheres);
    }

    public int updatePayment(String[][] changes, String[][] wheres) {
        return updateRecord(changes, wheres);
    }

    public int getPaymentsCount(){
        return getRecordCount();
    }


    private long firstDayOfMonth(int month, int year){
        return nthDayOfMonth(1, month, year, 0) + 7200000;
    }

    private long lastDayOfMonth(int month, int year){
        return nthDayOfMonth(1, month+1, year, -1) + 7200000;
    }

    private long nthDayOfMonth(int day, int month, int year, int milliOffset){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.MILLISECOND, milliOffset);
        return cal.getTimeInMillis();
    }

}
