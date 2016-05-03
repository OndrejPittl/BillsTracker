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

import cz.ondrejpittl.semestralka.database.DBManager;
import cz.ondrejpittl.semestralka.layout.CustomSpinner;
import cz.ondrejpittl.semestralka.layout.PinCodeFields;
import cz.ondrejpittl.semestralka.models.Category;
import cz.ondrejpittl.semestralka.models.Currency;
import cz.ondrejpittl.semestralka.models.Store;
import cz.ondrejpittl.semestralka.partial.SharedPrefs;

public class SettingsActivity extends AppCompatActivity {

    public static final int INTENT_INDEX = 1;

    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        this.init();
    }

    @Override
    protected void onStop() {
        Log.i("Ondra-mem", "Settings stopped.");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i("Ondra-mem", "Settings destroyed.");
        super.onDestroy();
        Runtime.getRuntime().gc();
    }

    private void init(){
        this.dbManager = new DBManager(this);
        this.initControls();
    }

    public void replayTutorial(View v){
        Log.i("Ondra-settings", "replayed!");
        SharedPrefs.storeTutorialDisplayed(false);

        setResult(Activity.RESULT_OK);
        finish();
    }

    /**
     * Starts activity with about-app info.
     * @param v Reference of view that fires event.
     */
    public void startAboutActivity(View v){
        Intent i = new Intent(this, AboutActivity.class);
        startActivity(i);
    }

    private void initControls(){
        String defCat = SharedPrefs.getDefaultCategory(),
               defSto = SharedPrefs.getDefaultStore(),
               defCur = SharedPrefs.getDefaultCurrency();


        ArrayList<Category> categories = this.dbManager.getStoredCategories();
        ArrayList<Store> stores = this.dbManager.getStoredStores();
        ArrayList<Currency> currencies = this.dbManager.getStoredCurrencies();

        CustomSpinner spinCat = (CustomSpinner) findViewById(R.id.settingsCategorySpinner);
        spinCat.init(this, categories);
        spinCat.selectItem(defCat);
        spinCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                storeSettings();
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        CustomSpinner spinSto = (CustomSpinner) findViewById(R.id.settingsStoreSpinner);
        spinSto.init(this, stores);
        spinSto.selectItem(defSto);
        spinSto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                storeSettings();
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        CustomSpinner spinCur = (CustomSpinner) findViewById(R.id.settingsCurrencySpinner);
        spinCur.init(this, currencies);
        spinCur.selectItem(defCur);
        spinCur.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                storeSettings();
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        Switch swPassReq = (Switch) findViewById(R.id.settingsPassReq);
        if(SharedPrefs.isPasswordRequireSet()) {
            swPassReq.setChecked(SharedPrefs.isPasswordRequired());
        } else {
            swPassReq.setChecked(true);
        }
        swPassReq.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                storeSettings();
            }
        });

        Switch swNotesDisplay = (Switch) findViewById(R.id.settingsNoteSwitch);
        if(SharedPrefs.isPaymentNoteDisplaySet()) {
            swNotesDisplay.setChecked(SharedPrefs.getPaymentNoteDisplay());
        } else {
            swNotesDisplay.setChecked(false);
        }
        swNotesDisplay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                storeSettings();
            }
        });

        Switch swPaymentAnim = (Switch) findViewById(R.id.settingsAnimationSwitch);
        if(SharedPrefs.isPaymentAnimationSet()) {
            swPaymentAnim.setChecked(SharedPrefs.getPaymentAnimation());
        } else {
            swPaymentAnim.setChecked(true);
        }
        swPaymentAnim.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                storeSettings();
            }
        });

        Switch swPaymentIcons = (Switch) findViewById(R.id.settingsIconSwitch);
        if(SharedPrefs.isPaymentIconSet()) {
            swPaymentIcons.setChecked(SharedPrefs.getPaymentIcons());
        } else {
            swPaymentIcons.setChecked(true);
        }
        swPaymentIcons.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                storeSettings();
            }
        });
    }

    private void storeSettings(){

        Switch swPassReq = (Switch) findViewById(R.id.settingsPassReq),
               swNotesDisplay = (Switch) findViewById(R.id.settingsNoteSwitch),
               swPaymentAnim = (Switch) findViewById(R.id.settingsAnimationSwitch),
               swPaymentIcons = (Switch) findViewById(R.id.settingsIconSwitch);

        CustomSpinner spinCat = (CustomSpinner) findViewById(R.id.settingsCategorySpinner),
                      spinSto = (CustomSpinner) findViewById(R.id.settingsStoreSpinner),
                      spinCur = (CustomSpinner) findViewById(R.id.settingsCurrencySpinner);


        Boolean passReq = swPassReq.isChecked();
        SharedPrefs.storePasswordRequire(passReq);

        Boolean notesDisplay = swNotesDisplay.isChecked();
        SharedPrefs.storePaymentNoteDisplay(notesDisplay);

        Boolean paymentAnim = swPaymentAnim.isChecked();
        SharedPrefs.storePaymentAnimation(paymentAnim);

        Boolean paymentIcons = swPaymentIcons.isChecked();
        SharedPrefs.storePaymentIcons(paymentIcons);

        Currency cur = (Currency) spinCur.getSelectedItem();
        SharedPrefs.storeDefaultCurrency(cur.getName());

        Category cat = (Category) spinCat.getSelectedItem();
        SharedPrefs.storeDefaultCategory(cat.getName());

        Store store = (Store) spinSto.getSelectedItem();
        SharedPrefs.storeDefaultStore(store.getName());

    }

    public void restoreDefaults(View v){
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final PinCodeFields fields = (PinCodeFields) layoutInflater.inflate(R.layout.pin_code, (ViewGroup) this.findViewById(R.id.pinCodeFieldsWrapper));
        fields.setPinCodeListeners();
        fields.setFieldsColorBlack();

        final Activity activity = this;
        new AlertDialog.Builder(this)
        .setTitle("Are you sure?")
        .setMessage("You are going to restore app settings. All payment records won't be affected.")
        .setView(fields)
        .setPositiveButton("Restore.", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            if (SharedPrefs.checkPINCode(fields.collectPINDigits())) {
                Toast.makeText(activity, "App settings restored.", Toast.LENGTH_SHORT).show();
                SharedPrefs.clear();
            } else {
                Toast.makeText(activity, "Password incorrect.", Toast.LENGTH_SHORT).show();
            }
            }
        }).setNegativeButton("Hell, no!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        }).show();
    }

    public void eraseAllPayments(View v){
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final PinCodeFields fields = (PinCodeFields) layoutInflater.inflate(R.layout.pin_code, (ViewGroup) this.findViewById(R.id.pinCodeFieldsWrapper));
        fields.setPinCodeListeners();
        fields.setFieldsColorBlack();

        final Activity activity = this;
        new AlertDialog.Builder(this)
        .setTitle("Are you sure?")
        .setMessage("You are going to delete all payment records. App settings won't be affected.")
        .setView(fields)
        .setPositiveButton("Delete.", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (SharedPrefs.checkPINCode(fields.collectPINDigits())) {
                    Toast.makeText(activity, "Payments deleted.", Toast.LENGTH_SHORT).show();
                    dbManager.eraseAllPayments();
                } else {
                    Toast.makeText(activity, "Password incorrect.", Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton("Hell, no!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        }).show();
    }
}
