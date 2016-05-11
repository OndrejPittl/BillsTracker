package cz.ondrejpittl.semestralka.partial;

import android.util.Log;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import cz.ondrejpittl.semestralka.models.Statistics;

/**
 * Created by OndrejPittl on 08.04.16.
 *
 * Custom Joda-time calendar.
 */
public class JodaCalendar {

    /**
     * Date time.
     */
    private DateTime date;

    /**
     * Origin locale.
     */
    private static Locale prevLocale;


    /**
     * Constructor. Basics initialization.
     */
    public JodaCalendar() {
        this.date = new DateTime();
    }

    /**
     * Setter of the start of a day.
     * @return  beginning of a current day
     */
    private DateTime setStartTime(){
        return date.hourOfDay().withMinimumValue()
                   .minuteOfDay().withMinimumValue()
                   .secondOfDay().withMinimumValue()
                   .millisOfDay().withMinimumValue();
    }

    /**
     * Setter of the end of a day.
     * @return  end of a current day
     */
    private DateTime setEndTime(){
        return date.hourOfDay().withMaximumValue()
                   .minuteOfDay().withMaximumValue()
                   .secondOfDay().withMaximumValue()
                   .millisOfDay().withMaximumValue();
    }

    /**
     * Getter of the start of a day.
     * @return  beginning of a current day
     */
    public DateTime getStartOfDay(){
        return setStartTime();
    }

    /**
     * Getter of the end of a day.
     * @return  end of a current day
     */
    public DateTime getEndOfDay(){
        return setEndTime();
    }

    /**
     * Getter of the first day of a week.
     * @return  the first day of a week
     */
    public DateTime getFirstDayOfWeek(){
        return setStartTime().dayOfWeek().withMinimumValue();
    }

    /**
     * Getter of the last day of a week
     * @return  the last day of a week
     */
    public DateTime getLastDayOfWeek(){
        return setEndTime().dayOfWeek().withMaximumValue();
    }

    /**
     * Getter of the first day of a month
     * @return  the first day of a month
     */
    public DateTime getFirstDayOfMonth(){
        return setStartTime().dayOfMonth().withMinimumValue();
    }

    /**
     * Getter of the last day of a month
     * @return  the last day of a month
     */
    public DateTime getLastDayOfMonth(){
        return setEndTime().dayOfMonth().withMaximumValue();
    }

    /**
     * Getter of the first day of a month.
     * @param month month
     * @param year  year
     * @return  date with the first day of a month
     */
    public DateTime getFirstDayOfMonth(int month, int year){
        return setStartTime()
                .withYear(year)
                .withMonthOfYear(month)
                .dayOfMonth().withMinimumValue();
    }

    /**
     * Getter of the last day of a month.
     * @param month month
     * @param year  year
     * @return  date with the last day of a month
     */
    public DateTime getLastDayOfMonth(int month, int year){
        return setEndTime()
                .withYear(year)
                .withMonthOfYear(month)
                .dayOfMonth().withMaximumValue();
    }

    /**
     * Getter of the first day of a year
     * @return  the first day of a year
     */
    public DateTime getFirstDayOfYear(){
        return setStartTime().dayOfMonth().withMinimumValue().monthOfYear().withMinimumValue();
    }

    /**
     * Getter of the last day of a year
     * @return  the last day of a year
     */
    public DateTime getLastDayOfYear(){
        return setEndTime().dayOfMonth().withMaximumValue().monthOfYear().withMaximumValue();
    }

    /**
     * Getter of the first day of a year
     * @return  the first day of a year
     */
    public DateTime getFirstDayOfYear(int year){
        return setStartTime()
                .withYear(year)
                .dayOfYear().withMinimumValue();
    }

    /**
     * Getter of the last day of a year
     * @return  the last day of a year
     */
    public DateTime getLastDayOfYear(int year){
        return setEndTime()
                .withYear(year)
                .dayOfYear().withMaximumValue();
    }

    /**
     * Compares a month and a year of two dates given.
     * @param j1    first date
     * @param j2    second date
     * @return  true – same, false – not
     */
    public static boolean compareMonthYear(DateTime j1, DateTime j2){
        int j1M = j1.getMonthOfYear(),
                j1Y = j1.getYearOfEra(),
                j2M = j2.getMonthOfYear(),
                j2Y = j2.getYearOfEra();

        return j1M == j2M && j1Y == j2Y;
    }

    /**
     * Compares a day, a month and a year of two dates given.
     * @param j1    first date
     * @param j2    second date
     * @return  true – same, false – not
     */
    public static boolean compareDateTimes(DateTime j1, DateTime j2){
        int     j1D = j1.getDayOfMonth(),
                j1M = j1.getMonthOfYear(),
                j1Y = j1.getYearOfEra(),
                j2D = j2.getDayOfMonth(),
                j2M = j2.getMonthOfYear(),
                j2Y = j2.getYearOfEra();

        return j1D == j2D && j1M == j2M && j1Y == j2Y;
    }

    /**
     * Clones datetime.
     * @param orig  original datetime
     * @return  a clone
     */
    public static DateTime clone(DateTime orig){
        return new DateTime()
                .withYearOfEra(orig.getYearOfEra())
                .withMonthOfYear(orig.getMonthOfYear())
                .withDayOfMonth(orig.getDayOfMonth());
    }

    /**
     * Getter of a month name in a string representation.
     * @param month index of a month
     * @return  string represented month
     */
    public static String getMonthName(int month) {
        String m;

        JodaCalendar.changeLocaleUS();
        m = new DateTime().withMonthOfYear(month).toString("MMMM");
        JodaCalendar.changeLocaleDefault();

        return m;
    }

    /**
     * Labels a day with a suffix.
     * @param day   day to be labeled
     * @return      labeled day
     */
    public static String getDayLabeled(int day) {
        String suffix;

        switch (day) {
            case 1:
                suffix = "st";
                break;

            case 2:
                suffix = "nd";
                break;

            case 3:
                suffix = "rd";
                break;

            default:
                suffix = "th";
                break;

        }

        return String.valueOf(day) + suffix;
    }

    /**
     * Getter of a week day number.
     * @param month month
     * @param day   day
     * @return      week day index
     */
    public static int getWeekDayNum(int month, int day) {
        return new DateTime().withMonthOfYear(month).withDayOfMonth(day).getDayOfWeek();
    }

    /**
     * Builds array of week days.
     * @param collapsed collapsed/uncollapsed labels
     * @return  array of week days
     */
    public static String[] buildDayWeekArray(boolean collapsed){
        if(collapsed)
            return new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        return new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    }

    /**
     * Get number of days in a month.
     * @param month month
     * @return      number of days
     */
    public static int getDayCountInMonth(int month){
        return new DateTime().withMonthOfYear(month).dayOfMonth().withMaximumValue().getDayOfMonth();
    }

    /**
     * Changes locale to origin one.
     */
    public static void changeLocaleDefault(){
        Locale.setDefault(JodaCalendar.prevLocale);
    }

    /**
     * Changes locale to american one.
     */
    public static void changeLocaleUS(){
        JodaCalendar.prevLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);
    }
}
