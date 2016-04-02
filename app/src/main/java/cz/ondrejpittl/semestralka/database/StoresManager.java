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


    private Resources res;



    /**
     * Constructor, initializes TableManager.
     *
     * @param dbManager DBManager reference.
     */
    public StoresManager(DBManager dbManager) {
        super("tb_stores", dbManager);
        Log.i("Ondra", "Stores Manager constructor");
    }

    public void createStoresTable(SQLiteDatabase db, Resources res) {
        this.db = db;
        this.res = res;

        String[][] args = new String[][] {
                {COLUMN_ID, "integer primary key autoincrement"},
                {COLUMN_NAME, "test"}
        };

        createTable(args);
        fillStoresTableFromXML();
    }

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
                    Log.e("Ondra", "STORE: " + name);
                }
                eventType = parser.next();
            }
            parser.close();
        } catch (XmlPullParserException e) {
            Log.e("Ondra", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("Ondra", e.getMessage(), e);
        }
    }

    public void insertStore(String name){
        insertRecord(new String[][]{
                {COLUMN_NAME, name}
        });
    }

    private void insertStore(String id, String name){
        insertRecord(new String[][]{
                {COLUMN_ID, id},
                {COLUMN_NAME, name}
        });
    }

    // TODO: 02.04.16 RADI SE ZATIM POUZE ABECEDNE
    public ArrayList<Store> selectAllStoresFrequencyOrdered(){
        return this.selectStores(null, new String[][]{
                {"name", "asc"}
        });
    }

    public ArrayList<Store> selectAllStores(){
        Cursor c = selectAllRecords();
        return this.buildStoresArraylistFromCursor(c);
    }

    public ArrayList<Store> selectStores(String[][] wheres, String[][] orderBy) {
        //wheres: {{"name", "=", "John"}, {"age", "<", 27}}
        //orderBy: {{"id", "asc"}, {"name", "desc"}}
        Cursor c = selectRecord(wheres, orderBy);
        return this.buildStoresArraylistFromCursor(c);
    }


    private ArrayList<Store> buildStoresArraylistFromCursor(Cursor c){
        ArrayList<Store> stores = new ArrayList<Store>();

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

    public int deleteStore(String[][] wheres){
        return deleteRecord(wheres);
    }

    public int getStoresCount(){
        return getRecordCount();
    }

}
