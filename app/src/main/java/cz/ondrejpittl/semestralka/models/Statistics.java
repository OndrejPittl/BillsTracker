package cz.ondrejpittl.semestralka.models;

/**
 * Created by OndrejPittl on 07.04.16.
 */
public class Statistics {

    private int todayAmount;

    private int weekAmount;

    private int monthAmount;

    private int yearAmount;

    private int totalAmount;


    public int getTodayAmount() {
        return todayAmount;
    }

    public void setTodayAmount(int todayAmount) {
        this.todayAmount = todayAmount;
    }

    public int getWeekAmount() {
        return weekAmount;
    }

    public void setWeekAmount(int weekAmount) {
        this.weekAmount = weekAmount;
    }

    public int getMonthAmount() {
        return monthAmount;
    }

    public void setMonthAmount(int monthAmount) {
        this.monthAmount = monthAmount;
    }

    public int getYearAmount() {
        return yearAmount;
    }

    public void setYearAmount(int yearAmount) {
        this.yearAmount = yearAmount;
    }

    public int getTotalAmount() { return totalAmount; }

    public void setTotalAmount(int total) {
        this.totalAmount = total;
    }
}
