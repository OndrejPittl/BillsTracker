package cz.ondrejpittl.semestralka.layout;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import cz.ondrejpittl.semestralka.R;
import cz.ondrejpittl.semestralka.StatisticsActivity;
import cz.ondrejpittl.semestralka.models.Category;
import cz.ondrejpittl.semestralka.models.Store;

/**
 * Created by OndrejPittl on 22.04.16.
 */
public class CustomSpinner extends Spinner {

    /**
     *  A flag indicating just now initialized spinner.
     *  Prevents a select item callback on initialization.
     */
    protected boolean initialized = false;

    /**
     * An activity reference.
     */
    protected Activity activity;


    /**
     * Constructor. Basics initialization.
     * @param context   an activity context reference
     * @param attrs     xml attributes
     */
    public CustomSpinner(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    /**
     * Constructor. Basics initialization.
     * @param context   an activity context reference
     */
    public CustomSpinner(Context context) {
        super(context);
    }

    /**
     * Initialization.
     * @param activity  an activity reference
     * @param source    a source of a spinner
     */
    public void init(final Activity activity, ArrayList source){
        if(activity == null || source == null)
            return;

        this.activity = activity;

        ArrayAdapter adapter;
        adapter = new ArrayAdapter(activity, R.layout.spinner_item, source);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.setAdapter(adapter);
    }

    /**
     * Selects an item of a spinner defined with its text.
     * @param itemValue text of an item of a spinner
     */
    public void selectItem(String itemValue) {
        int index;

        Adapter a = this.getAdapter();
        if(a == null || a.getCount() <= 0)
            return;

        if(itemValue.length() <= 0) {
            index = 0;
        } else {
            index = this.findSpinnerItemIndex(itemValue);
        }
        this.setSelection(index);
    }

    /**
     * Finds an index of an item of a spinner
     * @param value  text of an item of a spinner
     * @return       index of a value
     */
    public int findSpinnerItemIndex(String value){
        for(int i = 0; i < this.getCount(); i++) {
            String val = this.getItemAtPosition(i).toString();
            if(val.equals(value)) return i;
        }
        return 0;
    }
}
