package cz.ondrejpittl.semestralka.layout;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
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

    protected boolean initialized = false;

    protected Activity activity;


    public CustomSpinner(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public CustomSpinner(Context context) {
        super(context);
    }

    //public void init(final StatisticsActivity activity, ArrayList<Category> source){
    public void init(final Activity activity, ArrayList source){
        if(activity == null || source == null)
            return;

        this.activity = activity;

        ArrayAdapter adapter;
        adapter = new ArrayAdapter(activity, R.layout.spinner_item, source);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.setAdapter(adapter);
    }

    /*public void init(final Activity activity, List<String> source){
        ArrayList<String> arrayList = new ArrayList<>(source);
    }*/


    public void selectItem(String itemValue) {
        int index;

        if(itemValue.length() > 0) {
            index = this.findSpinnerItemIndex(itemValue);
        } else {
            index = 0;
        }

        this.setSelection(index);
    }

    public int findSpinnerItemIndex(String value){
        for(int i = 0; i < this.getCount(); i++) {
            String val = this.getItemAtPosition(i).toString();
            if(val.equals(value)) return i;
        }

        return 0;
    }

}
