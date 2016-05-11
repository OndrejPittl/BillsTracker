package cz.ondrejpittl.semestralka.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import cz.ondrejpittl.semestralka.R;
import cz.ondrejpittl.semestralka.controllers.SettingsDataController;
import cz.ondrejpittl.semestralka.controllers.SettingsUIController;
import cz.ondrejpittl.semestralka.partial.EditRecordType;

/**
 * Created by OndrejPittl on 09.05.16.
 */
public class EditRecord extends LinearLayout {

    /**
     * An ID of a category/store item.
     */
    private int id;

    /**
     * Differs category/store.
     */
    private EditRecordType type;

    /**
     * DataController reference.
     */
    private SettingsDataController data;

    /**
     * UIController reference.
     */
    private SettingsUIController ui;

    /**
     * A constructor. Basics initialization.
     * @param context   an activity context reference
     */
    public EditRecord(Context context) {
        super(context);
    }

    /**
     * A constructor. Basics initialization.
     * @param context   an activity context reference
     * @param attrs     xml attributes
     */
    public EditRecord(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Initialization.
     * @param id        an id of a category/store
     * @param title     a title of a category/store
     * @param type      a type of an input – category/store
     * @param controller    model layer reference
     * @param ui            view layer reference
     */
    public void init(int id, String title, EditRecordType type, SettingsDataController controller, SettingsUIController ui){
        this.id = id;
        this.type = type;
        this.data = controller;
        this.ui = ui;

        TextView tv = (TextView) this.findViewById(R.id.recTitle);
        tv.setText(title);

        this.setHandlers();
    }

    /**
     * Sets event handlers.
     */
    private void setHandlers(){
        ImageButton btn = (ImageButton) this.findViewById(R.id.btnRecDel);
        btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                data.deleteRecord(id, type);
                ui.redrawRecordList(type);
            }
        });
    }
}
