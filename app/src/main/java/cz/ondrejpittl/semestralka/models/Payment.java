package cz.ondrejpittl.semestralka.models;

import android.util.Log;

import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by OndrejPittl on 01.04.16.
 */
public class Payment {

    private int ID;

    private Category category;

    private Store store;

    private float amount;

    private DateTime date;

    private String note;


    public Payment(){}

    public Payment(String[] inputs){
        //0: date/long, 1: category/String, 2: category/int, 3: amount/int,
        //4: note/String, 5: store/String, 6: store/int

        Log.i("Ondra", Arrays.toString(inputs));

        this.setDate(Long.parseLong(inputs[0]));
        this.setCategory(Integer.parseInt(inputs[2]), inputs[1]);
        this.setAmount(Float.parseFloat(inputs[3]));
        this.setNote(inputs[4]);
        this.setStore(Integer.parseInt(inputs[6]), inputs[5]);
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(int categoryID, String categoryName, String categoryIcon) {
        this.category = new Category(categoryID, categoryName, categoryIcon);
    }

    public void setCategory(int categoryID, String categoryName) {
        this.category = new Category(categoryID, categoryName);
    }

    public Store getStore() {
        return this.store;
    }

    public void setStore(int storeID, String storeName) {
        this.store = new Store(storeID, storeName);
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public DateTime getDate() {
        return date;
    }

    // TODO: 01.04.16 zkontrolovat, zda sedí milisekundy!
    public Long getDateLong() {
        return date.getMillis();
    }

    public void setDate(Long date) {
        this.date = new DateTime(date);
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String toString(){
        return "ID: " + this.ID + ", CAT: " + this.category.getID() + ", STORE:" + this.store.getID() + ", DATE: " + this.date + ": " + this.amount + ",- (" + this.note + ")";
    }
}
