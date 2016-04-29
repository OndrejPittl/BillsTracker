package cz.ondrejpittl.semestralka.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import cz.ondrejpittl.semestralka.StatisticsActivity;
import cz.ondrejpittl.semestralka.models.Category;
import cz.ondrejpittl.semestralka.models.Statistics;

/**
 * Created by OndrejPittl on 24.04.16.
 */
public class CategoryActionSpinner extends CustomSpinner {

    public CategoryActionSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setItemSelectCallback();
    }

    public CategoryActionSpinner(Context context) {
        super(context);
        this.setItemSelectCallback();
    }

    private void setItemSelectCallback(){
        this.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //orientation change fix
                if(!initialized) {
                    initialized = true;
                    return;
                }

                Log.i("Ondra-spinner", "ITEM SELECTED!");
                StatisticsActivity ac = (StatisticsActivity) activity;
                ac.handleDisplayCategoryChange((Category) getSelectedItem());
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}
