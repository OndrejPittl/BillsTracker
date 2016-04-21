package cz.ondrejpittl.semestralka.partial;

import android.util.Log;

import org.joda.time.DateTime;

/**
 * Created by OndrejPittl on 08.04.16.
 *
 * Custom Joda-time calendar.
 */
public class JodaCalendar {

    private DateTime date;




    public JodaCalendar() {
        this.date = new DateTime();
    }

    private DateTime setStartTime(){
        return date.hourOfDay().withMinimumValue()
                   .minuteOfDay().withMinimumValue()
                   .secondOfDay().withMinimumValue()
                   .millisOfDay().withMinimumValue();
    }

    private DateTime setEndTime(){
        return date.hourOfDay().withMaximumValue()
                   .minuteOfDay().withMaximumValue()
                   .secondOfDay().withMaximumValue()
                   .millisOfDay().withMaximumValue();
    }

    public DateTime getStartOfDay(){
        return setStartTime();
    }

    public DateTime getEndOfDay(){
        return setEndTime();
    }

    public DateTime getFirstDayOfWeek(){
        return setStartTime().dayOfWeek().withMinimumValue();
    }

    public DateTime getLastDayOfWeek(){
        return setEndTime().dayOfWeek().withMaximumValue();
    }

    public DateTime getFirstDayOfMonth(){
        return setStartTime().dayOfMonth().withMinimumValue();
    }

    public DateTime getLastDayOfMonth(){
        return setEndTime().dayOfMonth().withMaximumValue();
    }

    public DateTime getFirstDayOfMonth(int month, int year){
        Log.i("Ondra", "mesic: " + month);
        return setStartTime()
                .withYear(year)
                .withMonthOfYear(month)
                .dayOfMonth().withMinimumValue();
    }

    public DateTime getLastDayOfMonth(int month, int year){
        return setEndTime()
                .withYear(year)
                .withMonthOfYear(month)
                .dayOfMonth().withMaximumValue();
    }

    public DateTime getFirstDayOfYear(){
        return setStartTime().dayOfMonth().withMinimumValue().monthOfYear().withMinimumValue();
    }

    public DateTime getLastDayOfYear(){
        return setEndTime().dayOfMonth().withMaximumValue().monthOfYear().withMaximumValue();
    }

    public DateTime getFirstDayOfYear(int year){
        Log.i("Ondra", "rok: " + year);
        return setStartTime()
                .withYear(year)
                .dayOfYear().withMinimumValue();
    }

    public DateTime getLastDayOfYear(int year){
        return setEndTime()
                .withYear(year)
                .dayOfYear().withMaximumValue();
    }

    public DateTime getDate(){
        return this.date;
    }

    public static boolean compareMonthYear(DateTime j1, DateTime j2){
        int j1M = j1.getMonthOfYear(),
                j1Y = j1.getYearOfEra(),
                j2M = j2.getMonthOfYear(),
                j2Y = j2.getYearOfEra();

        return j1M == j2M && j1Y == j2Y;
    }

    public static boolean compareDateTimes(DateTime j1, DateTime j2){
        int     j1D = j1.getDayOfMonth(),
                j1M = j1.getMonthOfYear(),
                j1Y = j1.getYearOfEra(),
                j2D = j2.getDayOfMonth(),
                j2M = j2.getMonthOfYear(),
                j2Y = j2.getYearOfEra();

        return j1D == j2D && j1M == j2M && j1Y == j2Y;
    }

    public static DateTime clone(DateTime orig){
        return new DateTime()
                .withYearOfEra(orig.getYearOfEra())
                .withMonthOfYear(orig.getMonthOfYear())
                .withDayOfMonth(orig.getDayOfMonth());
    }

}
