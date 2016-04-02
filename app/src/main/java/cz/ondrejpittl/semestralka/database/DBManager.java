package cz.ondrejpittl.semestralka.database;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import cz.ondrejpittl.semestralka.models.Category;
import cz.ondrejpittl.semestralka.models.Payment;
import cz.ondrejpittl.semestralka.models.Store;

/**
 * Created by OndrejPittl on 31.03.16.
 */
public class DBManager extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;

    private static final String DATABASE_NAME = "billstracker_database";

    private PaymentsManager payments;
    private StoresManager stores;
    private CategoryManager categories;

    protected SQLiteDatabase db;

    private Context context;




    public DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        this.context = context;

        Log.i("Ondra", "DB Manager constructor");


        this.payments = new PaymentsManager(this);
        this.stores = new StoresManager(this);
        this.categories = new CategoryManager(this);

        Log.i("Ondra", "All table managers initialised.");


        this.db = getWritableDatabase();


        Log.i("Ondra", "All tables created.");

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        this.payments.createPaymentsTable(db);

        this.payments.insertPayment("1", "2", "3", "123", "1459468800000", "prvního");
        this.payments.insertPayment("2", "3", "4", "234", "1461974400000", "posledního");
        this.payments.insertPayment("3", "4", "5", "345", "1464566400000", "moc pozdě");
        this.payments.insertPayment("4", "5", "6", "456", "1459458800000", "moc brzo");
        this.payments.insertPayment("5", "6", "7", "567", "1459478800000", "akorát");


        Resources res = this.context.getResources();
        this.stores.createStoresTable(db, res);
        this.categories.createCategoriesTable(db, res);

        Log.i("Ondra", "All table managers initialised.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

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