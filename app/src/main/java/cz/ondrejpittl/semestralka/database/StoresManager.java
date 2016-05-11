package cz.ondrejpittl.semestralka.database;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.util.ArrayList;

import cz.ondrejpittl.semestralka.R;
import cz.ondrejpittl.semestralka.models.Payment;
import cz.ondrejpittl.semestralka.models.Store;

/**
 * Created by OndrejPittl on 02.04.16.
 */
public class StoresManager extends TableManager {

    /**
     * Table ID column name.
     */
    private static final String COLUMN_ID = "id";

    /**
     * Table NAME column name.
     */
    private static final String COLUMN_NAME = "name";

    /**
     * Table columns alias prefix.
     */
    private static final String ALIAS_PREFIX = "stores_";

    /**
     * A resources reference.
     */
    private Resources res;


    /**
     * Constructor, initializes TableManager.
     *
     * @param dbManager DBManager reference.
     */
    public StoresManager(DBManager dbManager) {
        super("tb_stores", dbManager);
        //Log.i("Ondra", "Stores Manager constructor");
    }

    /**
     * Creates a Stores table.
     * @param db    a db reference
     * @param res   a resource reference
     */
    public void createStoresTable(SQLiteDatabase db, Resources res) {
        this.db = db;
        this.res = res;

        String[][] args = new String[][] {
                {COLUMN_ID, "integer primary key autoincrement"},
                {COLUMN_NAME, "text"}
        };

        createTable(args);
        fillStoresTableFromXML();
    }

    /**
     * Fills an empty table with data stored in xml.
     */
    private void fillStoresTableFromXML() {

        try {
            XmlResourceParser parser = this.res.getXml(R.xml.stores);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tag = parser.getName();
                if((eventType == XmlPullParser.START_TAG) && (tag != null) && (tag.equals("store"))) {
                    String id = parser.getAttributeValue(null, COLUMN_ID);
                    String name = parser.getAttributeValue(null, COLUMN_NAME);
                    insertStore(id, name);
                }
                eventType = parser.next();
            }
            parser.close();
        } catch (XmlPullParserException e) {} catch (IOException e) {}
    }

    /**
     * Inserts a new store.
     * @param name  a store name
     */
    public void insertStore(String name){
        insertRecord(new String[][]{
                {COLUMN_NAME, name}
        });
    }

    /**
     * Inserts a new store.
     * @param id    store id
     * @param name  store name
     */
    private void insertStore(String id, String name){
        insertRecord(new String[][]{
                {COLUMN_ID, id},
                {COLUMN_NAME, name}
        });
    }

    /**
     *
     * @return
     */
    public ArrayList<Store> selectAllStoresAlphabeticalOrdered(){
        return this.selectStores(null, new String[][]{
                {"name COLLATE NOCASE", "asc"}
        });
    }

    /**
     * Selects all stores.
     * @param wheres    where criteria
     * @param orderBy   order by criteria
     * @return
     */
    public ArrayList<Store> selectStores(String[][] wheres, String[][] orderBy) {
        //wheres: {{"name", "=", "John"}, {"age", "<", 27}}
        //orderBy: {{"id", "asc"}, {"name", "desc"}}
        Cursor c = selectRecord(wheres, orderBy);
        ArrayList<Store> col = this.buildStoresArraylistFromCursor(c);
        c.close();

        return col;
    }

    /**
     * Transforms data from a cursor into a collection od stores.
     * @param c a cursor reference
     * @return  a collection of stores
     */
    private ArrayList<Store> buildStoresArraylistFromCursor(Cursor c){
        ArrayList<Store> stores = new ArrayList<>();

        c.moveToFirst();
        while(c.isAfterLast() == false){
            Store s = new Store();
            s.setID(Integer.parseInt(c.getString(c.getColumnIndex(COLUMN_ID))));
            s.setName(c.getString(c.getColumnIndex(COLUMN_NAME)));
            stores.add(s);
            c.moveToNext();
        }

        return stores;
    }

    /**
     * Getter of aliased ID column
     * @return  an aliased column name
     */
    public static String getColumnIdAliased(){
        return ALIAS_PREFIX + COLUMN_ID;
    }

    /**
     * Getter of name column
     * @return  a column name
     */
    public static String getColumnNameAliased(){
        return ALIAS_PREFIX + COLUMN_NAME;
    }

    /**
     * Getter of all aliased column list.
     * @return  list of all aliased columns.
     */
    public String getAllColumnsSelector(){
        return TABLE_NAME + "." + COLUMN_ID + " as " + getColumnIdAliased() + ", "
                + TABLE_NAME + "." + COLUMN_NAME + " as " + getColumnNameAliased();
    }

    /**
     * Deletes a store.
     * @param wheres    where criteria
     * @return          a number of rows affected
     */
    public int deleteStore(String[][] wheres){
        return deleteRecord(wheres);
    }
}
