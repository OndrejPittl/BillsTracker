package cz.ondrejpittl.semestralka.database;

import android.database.Cursor;

/**
 * Created by OndrejPittl on 31.03.16.
 */
public interface ITableManagable {

    public void insertRecord (String[][] fields);

    public Cursor selectAllRecords();

    public Cursor selectRecord(String[][] wheres, String[][] orderBy);

    public int deleteRecord(String[][] wheres);

    public int updateRecord(String[][] changes, String[][] wheres);

    public int getRecordCount();

}
