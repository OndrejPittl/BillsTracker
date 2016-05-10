package cz.ondrejpittl.semestralka.partial;

import android.app.DatePickerDialog;
import android.content.Context;

/**
 * Created by OndrejPittl on 10.05.16.
 */
public class DynamicDatePicker extends DatePickerDialog {

    public DynamicDatePicker(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
    }

    public DynamicDatePicker(Context context, int theme, OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth) {
        super(context, theme, listener, year, monthOfYear, dayOfMonth);
    }

    
}
