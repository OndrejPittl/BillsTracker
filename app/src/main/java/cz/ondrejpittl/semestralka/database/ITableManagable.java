package cz.ondrejpittl.semestralka.database;

import android.database.Cursor;

/**
 * Created by OndrejPittl on 31.03.16.
 */
public interface ITableManagable {

    /**
     * Inserts a record.
     * @param fields    record field texts
     */
    void insertRecord (String[][] fields);

    /**
     * Select all records.
     * @return  all records
     */
    Cursor selectAllRecords();

    /**
     * Selects a refcord.
     * @param wheres    where coonfig
     * @param orderBy   orderby config
     * @return  records
     */
    Cursor selectRecord(String[][] wheres, String[][] orderBy);

    /**
     * Deletes a record.
     * @param wheres    where congfig.
     * @return  number of rows affected
     */
    int deleteRecord(String[][] wheres);

    /**
     * updates a record
     * @param changes   new record elements
     * @param wheres       wheres config
     * @return
     */
    int updateRecord(String[][] changes, String[][] wheres);


    /**
     * Getter of total row count.
     * @return  row count
     */
    int getRecordCount();

}
