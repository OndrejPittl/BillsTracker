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

    private int id;

    private EditRecordType type;

    private SettingsDataController data;

    private SettingsUIController ui;


    public EditRecord(Context context) {
        super(context);
    }

    public EditRecord(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(int id, String title, EditRecordType type, SettingsDataController controller, SettingsUIController ui){
        this.id = id;
        this.type = type;
        this.data = controller;
        this.ui = ui;

        TextView tv = (TextView) this.findViewById(R.id.recTitle);
        tv.setText(title);

        this.setHandlers();
    }

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
