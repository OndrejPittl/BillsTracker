package cz.ondrejpittl.semestralka.models;

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

    private Date date;

    private String note;


    public Payment(){

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

    public Date getDate() {
        return date;
    }

    // TODO: 01.04.16 zkontrolovat, zda sedí milisekundy!
    public Long getDateLong() {
        return date.getTime();
    }

    public void setDate(Long date) {
        this.date = new Date(date);
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
