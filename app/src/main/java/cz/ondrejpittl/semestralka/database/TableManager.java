package cz.ondrejpittl.semestralka.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Arrays;

import cz.ondrejpittl.semestralka.factories.DBQueryFactory;

/**
 * Created by OndrejPittl on 31.03.16.
 */
public class TableManager implements ITableManagable {

    /**
     * Table name.
     */
    protected String TABLE_NAME;



    /**
     * DatabaseManager reference.
     */
    protected DBManager dbManager;

    /**
     * Database reference.
     */
    protected SQLiteDatabase db;


    /**
     * Constructor, initializes TableManager.
     * @param dbManager DBManager reference.
     */
    public TableManager(String tbName, DBManager dbManager){
        Log.i("Ondra", "Table Manager constructor");
        this.TABLE_NAME = tbName;
        this.dbManager = dbManager;
    }


    /**
     * Creates table if NOT EXISTS.
     * @param args  Array of columns info of a table in a form: {{colName, params}, {colName, params}, ...}
     *              example: {{id, int primary key autoincrement}, {name, text}, {age, int}}
     */
    public void createTable(String[][] args){
        Log.i("Ondra", "Creating table " + TABLE_NAME + ":");
        Log.i("Ondra", DBQueryFactory.createTable(TABLE_NAME, args));

        this.db.execSQL(DBQueryFactory.createTable(TABLE_NAME, args));
    }


    /**
     * Inserts a single record into a table.
     * @param cols    Array of fields of a record in a form: {{colName, colVal}, {colName, colVal}, ...}
     *                example: {{name, "John"}, {age, 27}}
     */
    public void insertRecord(String[][] cols) {
        ContentValues contentValues = new ContentValues();

        for (int i = 0; i < cols.length; i++) {
            contentValues.put(cols[i][0], cols[i][1]);
        }

        Log.i("Ondra", "INSERTING into table " + TABLE_NAME);

        if(db == null) db = dbManager.getWritableDatabase();
        db.insert(TABLE_NAME, null, contentValues);
    }


    public Cursor selectAllRecords() {
        String query = DBQueryFactory.querySelectAll(TABLE_NAME);

        Log.i("Ondra", "Selecting all from table " + TABLE_NAME + ":");
        Log.i("Ondra", query);

        db = dbManager.getReadableDatabase();
        return db.rawQuery(query, null);
    }

    /**
     * Select records due to where conditions.
     * @param wheres    Array of where criteria in a form: {{colName, equality, colVal}, {colName, equality, colVal}, ...}
     *                  example: {{"name", "=", "John"}, {"age", "<", 27}}
     */
    public Cursor selectRecord(String[][] wheres, String[][] orderBy){
        String query = DBQueryFactory.querySelectOne(TABLE_NAME, wheres, orderBy);
        Log.i("Ondra", "Selecting all from table " + TABLE_NAME + ":");
        Log.i("Ondra", query);

        db = dbManager.getReadableDatabase();
        return db.rawQuery(query, null);
    }

    /**
     * Removes records due to where conditions.
     *
     * @param wheres    Two-dimensional array of where criteria in a form:
     *                  {{colName, equality, colVal}, {colName, equality, colVal}, ...}.
     *                  example: {{"name", "=", "John"}, {"age", "<", 27}}
     * @return  ???
     */
    public int deleteRecord(String[][] wheres) {

        ////db.delete("tablename","id=? and name=?",new String[]{"1","jack"});
        String whereClause = DBQueryFactory.queryDeleteWhereClause(wheres),
               whereArgs[] = DBQueryFactory.queryDeleteWhereArgs(wheres);

        Log.i("Ondra", "Deleting from table " + TABLE_NAME);
        Log.i("Ondra", whereClause);
        Log.i("Ondra", Arrays.toString(whereArgs));

        db = dbManager.getWritableDatabase();
        return db.delete(TABLE_NAME, whereClause, whereArgs);
    }

    /**
     * Updates records due to conditions.
     * @param changes   Array of pair-arrays with replacements in a form: {{colName, colVal}, {colName, colVal}, ...}.
     *                  example: {{"name", "John"}, {"age", 27}}
     * @param wheres    Two-dimensional array of where criteria in a form:
     *                  {{colName, equality, colVal}, {colName, equality, colVal}, ...}.
     *                  example: {{"name", "=", "John"}, {"age", "<", 27}}
     */
    public int updateRecord(String[][] changes, String[][] wheres) {
        int whereCount = wheres.length;

        String whereClause = DBQueryFactory.queryDeleteWhereClause(wheres),
                whereArgs[] = DBQueryFactory.queryDeleteWhereArgs(wheres);

        ContentValues contentValues = new ContentValues();

        for (int i = 0; i < whereCount; i++) {
            contentValues.put(changes[i][0], changes[i][1]);
        }

        Log.i("Ondra", "Updating table " + TABLE_NAME + ":");
        Log.i("Ondra", whereClause);
        Log.i("Ondra", Arrays.toString(whereArgs));

        db = dbManager.getWritableDatabase();
        return db.update(TABLE_NAME, contentValues, whereClause, whereArgs);
    }

    /**
     * Counts total number of records.
     * @return  Number of rows found.
     */
    public int getRecordCount(){
        Log.i("Ondra", "Counting total number of records of table " + TABLE_NAME);

        db = dbManager.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }

    public void setDB(SQLiteDatabase db){
        this.db = db;
    }


}
