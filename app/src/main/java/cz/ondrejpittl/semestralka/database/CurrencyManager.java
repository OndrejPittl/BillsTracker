package cz.ondrejpittl.semestralka.database;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import cz.ondrejpittl.semestralka.R;
import cz.ondrejpittl.semestralka.models.Category;
import cz.ondrejpittl.semestralka.models.Currency;

/**
 * Created by OndrejPittl on 02.04.16.
 */
public class CurrencyManager extends TableManager {

    /**
     * Table ID column name.
     */
    private static final String COLUMN_ID = "id";

    /**
     * Table NAME column name.
     */
    private static final String COLUMN_NAME = "name";

    /**
     * Resources.
     */
    private Resources res;



    /**
     * Constructor, initializes TableManager.
     *
     * @param dbManager DBManager reference.
     */
    public CurrencyManager(DBManager dbManager) {
        super("tb_currencies", dbManager);
        Log.i("Ondra", "Currency Manager constructor");
    }

    /**
     * Creates a currencies table.
     * @param db    a db reference
     * @param res   a resource reference
     */
    public void createCurrenciesTable(SQLiteDatabase db, Resources res) {
        this.db = db;
        this.res = res;

        String[][] args = new String[][] {
                {COLUMN_ID, "integer primary key autoincrement"},
                {COLUMN_NAME, "text"}
        };

        createTable(args);
        fillCurrencyTableFromXML();
    }

    /**
     * Fills an empty table with data stored in xml.
     */
    private void fillCurrencyTableFromXML() {

        try {
            XmlResourceParser parser = this.res.getXml(R.xml.currency);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tag = parser.getName();
                if((eventType == XmlPullParser.START_TAG) && (tag != null) && (tag.equals("currency"))) {
                    String id = parser.getAttributeValue(null, COLUMN_ID);
                    String name = parser.getAttributeValue(null, COLUMN_NAME);
                    insertCurrency(id, name);
                    Log.e("Ondra", "CURRENCY: " + name);
                }
                eventType = parser.next();
            }
            parser.close();
        } catch (XmlPullParserException e) {
            //Log.e("Ondra", e.getMessage(), e);
        } catch (IOException e) {
            //Log.e("Ondra", e.getMessage(), e);
        }
    }

    /**
     * Inserts a new currency record.
     * @param name  a new currency name
     */
    public void insertCurrency(String name){
        insertRecord(new String[][]{
                {COLUMN_NAME, name}
        });
    }

    /**
     * Inserts a new currency.
     * @param id    a new currency id
     * @param name  a new currency name
     */
    private void insertCurrency(String id, String name){
        insertRecord(new String[][]{
                {COLUMN_ID, id},
                {COLUMN_NAME, name}
        });
    }

    /**
     * Selects all currencies.
     * @return  a collection of currencies
     */
    public ArrayList<Currency> selectAllCurrency(){
        Cursor c = selectAllRecords();
        ArrayList<Currency> col = this.buildCurrencyArraylistFromCursor(c);
        c.close();
        return col;
    }

    /**
     * Transforms data from a cursor into a collection.
     * @param c a cursor reference
     * @return  a collection of currencies
     */
    private ArrayList<Currency> buildCurrencyArraylistFromCursor(Cursor c){
        ArrayList<Currency> currency = new ArrayList<Currency>();

        c.moveToFirst();
        while(c.isAfterLast() == false){
            Currency cur = new Currency();
            cur.setID(Integer.parseInt(c.getString(c.getColumnIndex(COLUMN_ID))));
            cur.setName(c.getString(c.getColumnIndex(COLUMN_NAME)));
            currency.add(cur);
            c.moveToNext();
        }

        return currency;
    }

    /**
     * Checks existence of a currency in a DB.
     * @param name  a new currency name
     * @return  true – exists, false – not
     */
    public boolean checkIfCurrencyExists(String name){
        return checkIfRecordExists(new String[][]{
                {"name", "=", name}
        });
    }
}
