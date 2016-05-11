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
import cz.ondrejpittl.semestralka.models.Store;

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

    /**
     * Table ICON column name.
     */
    private static final String COLUMN_ICON = "icon";

    /**
     * Table columns alias prefix.
     */
    private static final String ALIAS_PREFIX = "categories_";


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

    /**
     * Builds category table.
     * @param db    db reference
     * @param res   resources
     */
    public void createCategoriesTable(SQLiteDatabase db, Resources res) {
        this.db = db;
        this.res = res;

        String[][] args = new String[][] {
                {COLUMN_ID, "integer primary key autoincrement"},
                {COLUMN_NAME, "text"},
                {COLUMN_ICON, "text"}
        };

        createTable(args);
        fillCategoriesTableFromXML();
    }

    /**
     * Fills an empty category table from XML values.
     */
    private void fillCategoriesTableFromXML() {

        try {
            XmlResourceParser parser = this.res.getXml(R.xml.categories);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tag = parser.getName();
                if((eventType == XmlPullParser.START_TAG) && (tag != null) && (tag.equals("category"))) {
                    String id = parser.getAttributeValue(null, COLUMN_ID);
                    String name = parser.getAttributeValue(null, COLUMN_NAME);
                    String icon = parser.getAttributeValue(null, COLUMN_ICON);
                    insertCategory(id, name, icon);
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

    /**
     * Inserts a new category.
     * @param name  category name
     * @param icon  category icon path
     */
    public void insertCategory(String name, String icon){
        insertRecord(new String[][]{
                {COLUMN_NAME, name},
                {COLUMN_ICON, icon}
        });
    }

    /**
     * Inserts a new category.
     * @param id    category id
     * @param name  category name
     * @param icon  category icon path
     */

    private void insertCategory(String id, String name, String icon){
        insertRecord(new String[][]{
                {COLUMN_ID, id},
                {COLUMN_NAME, name},
                {COLUMN_ICON, icon}
        });
    }

    /**
     * Alphabetical-ordered categories.
     * @return  category list
     */
    public ArrayList<Category> selectAllCategoriesFrequencyOrdered(){
        return this.selectCategories(null, new String[][]{
                {"name COLLATE NOCASE", "asc"}
        });
    }

    /**
     * Selects all categories from FB.
     * @param wheres    where conditions
     * @param orderBy   orderby conditions
     * @return  list of all categories
     */
    public ArrayList<Category> selectCategories(String[][] wheres, String[][] orderBy) {
        //wheres: {{"name", "=", "John"}, {"age", "<", 27}}
        //orderBy: {{"id", "asc"}, {"name", "desc"}}

        Cursor c = selectRecord(wheres, orderBy);
        ArrayList<Category> col = this.buildCategoriesArraylistFromCursor(c);
        c.close();

        return col;
    }

    /**
     * Builds a list of categories from a cursor.
     * @param c cursor
     * @return  list of categories
     */
    private ArrayList<Category> buildCategoriesArraylistFromCursor(Cursor c){
        ArrayList<Category> categories = new ArrayList<Category>();

        c.moveToFirst();
        while(c.isAfterLast() == false){
            Category cat = new Category();
            cat.setID(Integer.parseInt(c.getString(c.getColumnIndex(COLUMN_ID))));
            cat.setName(c.getString(c.getColumnIndex(COLUMN_NAME)));
            cat.setIcon(c.getString(c.getColumnIndex(COLUMN_ICON)));
            categories.add(cat);
            c.moveToNext();
        }

        return categories;
    }

    /**
     * Aliased id column.
     * @return  Aliased id column
     */
    public static String getColumnIdAliased(){
        return ALIAS_PREFIX + COLUMN_ID;
    }

    /**
     * Aliased name column.
     * @return  Aliased name column
     */
    public static String getColumnNameAliased(){
        return ALIAS_PREFIX + COLUMN_NAME;
    }

    /**
     * Aliased icon column.
     * @return  Aliased icon column
     */
    public static String getColumnIconAliased(){
        return ALIAS_PREFIX + COLUMN_ICON;
    }

    /**
     * Name column.
     * @return  Name column
     */
    public static String getColumnName(){
        return COLUMN_NAME;
    }

    /**
     * All aliased colums selector.
     * @return all aliased colums selector
     */
    public String getAllColumnsSelector(){
        return TABLE_NAME + "." + COLUMN_ID + " as " + getColumnIdAliased()  + ", "
                + TABLE_NAME + "." + COLUMN_NAME + " as " + getColumnNameAliased()  + ", "
                + TABLE_NAME + "." + COLUMN_ICON + " as " + getColumnIconAliased();
    }

    /**
     * Deletes category.
     * @param wheres    where conditions
     * @return          number of rows affected
     */
    public int deleteCategory(String[][] wheres){
        return deleteRecord(wheres);
    }
}
