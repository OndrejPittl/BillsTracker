package cz.ondrejpittl.semestralka;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;

import cz.ondrejpittl.semestralka.controllers.SettingsDataController;
import cz.ondrejpittl.semestralka.controllers.SettingsUIController;
import cz.ondrejpittl.semestralka.database.DBManager;
import cz.ondrejpittl.semestralka.layout.CustomSpinner;
import cz.ondrejpittl.semestralka.layout.PinCodeFields;
import cz.ondrejpittl.semestralka.models.Category;
import cz.ondrejpittl.semestralka.models.Currency;
import cz.ondrejpittl.semestralka.models.Store;
import cz.ondrejpittl.semestralka.partial.Designer;
import cz.ondrejpittl.semestralka.partial.EditRecordType;
import cz.ondrejpittl.semestralka.partial.SharedPrefs;

public class SettingsActivity extends AppCompatActivity {

    /**
     * Intent identification.
     */
    public static final int INTENT_INDEX = 1;

    /**
     *
     */
    private SettingsUIController controllerUI;

    private SettingsDataController controllerData;

    /**
     * Activity model layer reference.
     */
    private DBManager dbManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Designer.setFullscreenActivity(this);
        this.setContentView(R.layout.activity_settings);

        this.init();
    }

    protected void onDestroy() {
        Log.i("Ondra-mem", "Settings destroyed.");
        super.onDestroy();
        Runtime.getRuntime().gc();
    }

    /**
     * Activity initialization.
     */
    private void init(){
        Designer.updateDesign(this);
        this.controllerUI = new SettingsUIController(this);
        this.dbManager = new DBManager(this);
        this.controllerData = new SettingsDataController(this, this.dbManager);
        this.loadStoredSettings();
    }

    /**
     * Sets flag: tutorial not displayed.
     * @param v UI element that triggered action.
     */
    public void replayTutorial(View v){
        Log.i("Ondra-settings", "replayed!");
        SharedPrefs.storePaymentAnimation(true);
        SharedPrefs.storeTutorialDisplayed(false);
        this.setResult(Activity.RESULT_OK);
        this.finish();
    }

    public void storeSettings(){
        this.controllerData.storeSettings();
    }

    public void erasePayments(){
        this.dbManager.eraseAllPayments();
    }

    /**
     * Starts activity with about-app info.
     * @param v Reference of view that fires event.
     */
    public void startAboutActivity(View v){
        Intent i = new Intent(this, AboutActivity.class);
        this.startActivity(i);
    }

    private void loadStoredSettings(){
        this.controllerUI.init(this.controllerData);
    }

    public void restoreDefaults(View v){
        this.controllerUI.restoreDefaults(v);
    }

    public void eraseAllPayments(View v){
        this.controllerUI.eraseAllPayments(v);
    }

    public SettingsDataController getDataController(){
        return this.controllerData;
    }

    public void createNewCategory(View v){
        EditText et = (EditText) findViewById(R.id.settNewCategry);

        String newCat = et.getText().toString();

        if(newCat.length() <= 0){
            et.setBackgroundResource(R.drawable.shape_thin_border_error);
            return;
        } else {
            et.setBackgroundResource(R.drawable.shape_thin_border);
        }

        this.dbManager.insertNewCategory(newCat);
        et.setText("");
        this.controllerUI.redrawRecordList(EditRecordType.CATEGORY);
    }

    public void createNewStore(View v){
        EditText et = (EditText) findViewById(R.id.settNewStore);

        String newStore = et.getText().toString();

        if(newStore.length() <= 0){
            et.setBackgroundResource(R.drawable.shape_thin_border_error);
            return;
        } else {
            et.setBackgroundResource(R.drawable.shape_thin_border);
        }

        this.dbManager.insertNewStore(et.getText().toString());
        et.setText("");
        this.controllerUI.redrawRecordList(EditRecordType.STORE);
    }


}
