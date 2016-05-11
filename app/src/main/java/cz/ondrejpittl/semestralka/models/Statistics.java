package cz.ondrejpittl.semestralka.models;

/**
 * Created by OndrejPittl on 07.04.16.
 */
public class Statistics {

    /**
     * Today payment amount.
     */
    private int todayAmount;

    /**
     * Week payment amount.
     */
    private int weekAmount;

    /**
     * Month payment amount.
     */
    private int monthAmount;

    /**
     * Year payment amount.
     */
    private int yearAmount;

    /**
     * Total payment amount.
     */
    private int totalAmount;


    /**
     * Getter of a today payment amount.
     * @return  today payment amount
     */
    public int getTodayAmount() {
        return todayAmount;
    }

    /**
     * Setter of a today payment amount.
     * @param todayAmount today payment amount
     */
    public void setTodayAmount(int todayAmount) {
        this.todayAmount = todayAmount;
    }

    /**
     * Getter of a week payment amount.
     * @return  week payment amount
     */
    public int getWeekAmount() {
        return weekAmount;
    }

    /**
     * Setter of a week payment amount.
     * @param weekAmount week payment amount
     */
    public void setWeekAmount(int weekAmount) {
        this.weekAmount = weekAmount;
    }

    /**
     * Getter of a month payment amount.
     * @return  month payment amount
     */
    public int getMonthAmount() {
        return monthAmount;
    }


    /**
     * Setter of a month payment amount.
     * @param monthAmount month payment amount
     */
    public void setMonthAmount(int monthAmount) {
        this.monthAmount = monthAmount;
    }

    /**
     * Setter of a year payment amount.
     * @param yearAmount year payment amount
     */
    public void setYearAmount(int yearAmount) {
        this.yearAmount = yearAmount;
    }

    /**
     * Setter of a total payment amount.
     * @param total total payment amount
     */
    public void setTotalAmount(int total) {
        this.totalAmount = total;
    }
}
