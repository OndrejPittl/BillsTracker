package cz.ondrejpittl.semestralka.models;

import android.util.Log;

import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import cz.ondrejpittl.semestralka.partial.JodaCalendar;

/**
 * Created by OndrejPittl on 01.04.16.
 */
public class Payment {

    /**
     * Payment record ID in a DB.
     */
    private int ID;

    /**
     * Category of a payment.
     */
    private Category category;

    /**
     * Store fo a payment. Optional.
     */
    private Store store;

    /**
     * Payment amount.
     */
    private float amount;

    /**
     * Payment date.
     */
    private DateTime date;

    /**
     * Payment note. Optional.
     */
    private String note;


    /**
     * Constructor.
     */
    public Payment(){}

    /**
     * Constructor. Fill itself from inputs[] given.
     * @param inputs    payment inputs
     */
    public Payment(String[] inputs){
        //0: date/long, 1: category/String, 2: category/int, 3: amount/int,
        //4: note/String, 5: store/String, 6: store/int

        this.setDate(Long.parseLong(inputs[0]));
        this.setCategory(Integer.parseInt(inputs[2]), inputs[1]);
        this.setAmount(Float.parseFloat(inputs[3]));
        this.setNote(inputs[4]);
        this.setStore(Integer.parseInt(inputs[6]), inputs[5]);
    }

    /**
     * Builds mock payment for a tutorial.
     * @param month month
     * @param year  year
     * @return      new payment
     */
    public static Payment getMockPayment(int month, int year){
        Payment p = new Payment();
        p.setCategory(1, "Clothes", "@drawable/category_icon_clothes");
        p.setStore(5, "Tesco");
        p.setDate(new JodaCalendar().getLastDayOfMonth(month, year).getMillis());
        p.setAmount(128);
        p.setNote("Such a sale!");
        return p;
    }

    /**
     * Getter of the ID of a payment.
     * @return  the ID of a payment
     */
    public int getID() {
        return ID;
    }

    /**
     * Setter of the payment ID.
     * @param ID    ID of a payment
     */
    public void setID(int ID) {
        this.ID = ID;
    }

    /**
     * Getter of the category of a payment.
     * @return  the category of a payment
     */
    public Category getCategory() {
        return category;
    }


    /**
     * Setter of a category.
     * @param categoryID    category id
     * @param categoryName  category name
     * @param categoryIcon  category icon
     */
    public void setCategory(int categoryID, String categoryName, String categoryIcon) {
        this.category = new Category(categoryID, categoryName, categoryIcon);
    }

    /**
     * Setter of a category.
     * @param categoryID    category id
     * @param categoryName  category name
     */
    public void setCategory(int categoryID, String categoryName) {
        this.category = new Category(categoryID, categoryName);
    }

    /**
     * Getter of the store of a payment.
     * @return  the store of a payment
     */
    public Store getStore() {
        return this.store;
    }

    /**
     * Setter of a store.
     * @param storeID   store id
     * @param storeName store name
     */
    public void setStore(int storeID, String storeName) {
        this.store = new Store(storeID, storeName);
    }

    /**
     * Getter of the amount of a payment.
     * @return  the amount of a payment
     */
    public float getAmount() {
        return amount;
    }

    /**
     * Setter of an amount of a payment.
     * @param amount    amount of a payment
     */
    public void setAmount(float amount) {
        this.amount = amount;
    }

    /**
     * Getter of the date of a payment.
     * @return  the date of a payment
     */
    public DateTime getDate() {
        return date;
    }

    /**
     * Getter of the date in milliseconds of a payment.
     * @return  the date in milliseconds of a payment
     */
    public Long getDateLong() {
        return date.getMillis();
    }

    /**
     * Setter of a payment date.
     * @param date  date of a payment
     */
    public void setDate(Long date) {
        this.date = new DateTime(date);
    }

    /**
     * Getter of the note of a payment.
     * @return  the note of a payment
     */
    public String getNote() {
        return note;
    }

    /**
     * Setter of a note of a payment.
     * @param note  payment note
     */
    public void setNote(String note) {
        this.note = note;
    }

    public String toString(){
        return "ID: " + this.ID + ", CAT: " + this.category.getID() + ", STORE:" + this.store.getID() + ", DATE: " + this.date + ": " + this.amount + ",- (" + this.note + ")";
    }
}
