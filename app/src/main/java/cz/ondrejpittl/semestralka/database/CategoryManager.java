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

/**
 * Created by OndrejPittl on 02.04.16.
 */
public class CategoryManager extends TableManager {

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
    public CategoryManager(DBManager dbManager) {
        super("tb_categories", dbManager);
        Log.i("Ondra", "Category Manager constructor");
    }

    public void createCategoriesTable(SQLiteDatabase db, Resources res) {
        this.db = db;
        this.res = res;

        String[][] args = new String[][] {
                {COLUMN_ID, "integer primary key autoincrement"},
                {COLUMN_NAME, "test"}
        };

        createTable(args);
        fillCategoriesTableFromXML();
    }

    private void fillCategoriesTableFromXML() {

        try {
            XmlResourceParser parser = this.res.getXml(R.xml.categories);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tag = parser.getName();
                if((eventType == XmlPullParser.START_TAG) && (tag != null) && (tag.equals("category"))) {
                    String id = parser.getAttributeValue(null, COLUMN_ID);
                    String name = parser.getAttributeValue(null, COLUMN_NAME);
                    insertCategory(id, name);
                    Log.e("Ondra", "CATEGORY: " + name);
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

    public void insertCategory(String name){
        insertRecord(new String[][]{
                {COLUMN_NAME, name}
        });
    }

    private void insertCategory(String id, String name){
        insertRecord(new String[][]{
                {COLUMN_ID, id},
                {COLUMN_NAME, name}
        });
    }

    public ArrayList<Category> selectAllCategories(){
        Cursor c = selectAllRecords();
        return this.buildCategoriesArraylistFromCursor(c);
    }

    private ArrayList<Category> buildCategoriesArraylistFromCursor(Cursor c){
        ArrayList<Category> categories = new ArrayList<Category>();

        c.moveToFirst();
        while(c.isAfterLast() == false){
            Category cat = new Category();
            cat.setID(Integer.parseInt(c.getString(c.getColumnIndex(COLUMN_ID))));
            cat.setName(c.getString(c.getColumnIndex(COLUMN_NAME)));
            categories.add(cat);
            c.moveToNext();
        }

        return categories;
    }

    public int deleteCategory(String[][] wheres){
        return deleteRecord(wheres);
    }

    public int getCategoriesCount(){
        return getRecordCount();
    }

}
