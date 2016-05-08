package cz.ondrejpittl.semestralka.controllers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import cz.ondrejpittl.semestralka.R;
import cz.ondrejpittl.semestralka.SettingsActivity;
import cz.ondrejpittl.semestralka.StatisticsActivity;
import cz.ondrejpittl.semestralka.layout.CustomSpinner;
import cz.ondrejpittl.semestralka.layout.PinCodeFields;
import cz.ondrejpittl.semestralka.models.Category;
import cz.ondrejpittl.semestralka.models.Currency;
import cz.ondrejpittl.semestralka.models.Store;
import cz.ondrejpittl.semestralka.partial.Designer;
import cz.ondrejpittl.semestralka.partial.SharedPrefs;

/**
 * Created by OndrejPittl on 07.05.16.
 */
public class SettingsUIController {

    /**
     * Activity its UI is being controlled.
     */
    private SettingsActivity activity;


    public SettingsUIController(SettingsActivity activity) {
        this.activity = activity;
    }


    /**
     * Initializes UI controls.
     */
    public void init(ArrayList<Store> stores, ArrayList<Category> categories, ArrayList<Currency> currencies){
        String defCat = SharedPrefs.getDefaultCategory(),
                defSto = SharedPrefs.getDefaultStore(),
                defCur = SharedPrefs.getDefaultCurrency();
        
        CustomSpinner spinCat = (CustomSpinner) this.activity.findViewById(R.id.settingsCategorySpinner);
        spinCat.init(this.activity, categories);
        spinCat.selectItem(defCat);
        spinCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                activity.storeSettings();
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        CustomSpinner spinSto = (CustomSpinner) this.activity.findViewById(R.id.settingsStoreSpinner);
        spinSto.init(this.activity, stores);
        spinSto.selectItem(defSto);
        spinSto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                activity.storeSettings();
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        CustomSpinner spinCur = (CustomSpinner) this.activity.findViewById(R.id.settingsCurrencySpinner);
        spinCur.init(this.activity, currencies);
        spinCur.selectItem(defCur);
        spinCur.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                activity.storeSettings();
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        Switch swPassReq = (Switch) this.activity.findViewById(R.id.settingsPassReq);
        if(SharedPrefs.isPasswordRequireSet()) {
            swPassReq.setChecked(SharedPrefs.isPasswordRequired());
        } else {
            swPassReq.setChecked(true);
        }
        swPassReq.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                activity.storeSettings();
            }
        });

        Switch swNotesDisplay = (Switch) this.activity.findViewById(R.id.settingsNoteSwitch);
        if(SharedPrefs.isPaymentNoteDisplaySet()) {
            swNotesDisplay.setChecked(SharedPrefs.getPaymentNoteDisplay());
        } else {
            swNotesDisplay.setChecked(false);
        }
        swNotesDisplay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                activity.storeSettings();
            }
        });

        Switch swPaymentAnim = (Switch) this.activity.findViewById(R.id.settingsAnimationSwitch);
        if(SharedPrefs.isPaymentAnimationSet()) {
            swPaymentAnim.setChecked(SharedPrefs.getPaymentAnimation());
        } else {
            swPaymentAnim.setChecked(true);
        }
        swPaymentAnim.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                activity.storeSettings();
            }
        });

        Switch swPaymentIcons = (Switch) this.activity.findViewById(R.id.settingsIconSwitch);
        if(SharedPrefs.isPaymentIconSet()) {
            swPaymentIcons.setChecked(SharedPrefs.getPaymentIcons());
        } else {
            swPaymentIcons.setChecked(true);
        }
        swPaymentIcons.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                activity.storeSettings();
            }
        });

        CustomSpinner bgSelect = (CustomSpinner) this.activity.findViewById(R.id.bgSpinner);
        bgSelect.init(this.activity, new ArrayList<>(Arrays.asList(this.activity.getResources().getStringArray(R.array.backgrounds))));
        if(SharedPrefs.isDefaultDesignSet()) {
            bgSelect.setSelection(SharedPrefs.getDefaultDesign());
        } else {
            bgSelect.setSelection(0);
        }
        bgSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("Ondra-sett", "REGISTERED!");
                activity.storeSettings();
                Designer.updateDesign(activity);
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

    }

    public void restoreDefaults(View v){
        LayoutInflater layoutInflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final PinCodeFields fields = (PinCodeFields) layoutInflater.inflate(R.layout.pin_code, (ViewGroup) this.activity.findViewById(R.id.pinCodeFieldsWrapper));
        fields.setPinCodeListeners();
        fields.setFieldsColorBlack();

        final SettingsActivity activity = this.activity;
        new AlertDialog.Builder(activity)
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
        LayoutInflater layoutInflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final PinCodeFields fields = (PinCodeFields) layoutInflater.inflate(R.layout.pin_code, (ViewGroup) this.activity.findViewById(R.id.pinCodeFieldsWrapper));
        fields.setPinCodeListeners();
        fields.setFieldsColorBlack();

        final SettingsActivity activity = this.activity;
        new AlertDialog.Builder(activity)
                .setTitle("Are you sure?")
                .setMessage("You are going to delete all payment records. App settings won't be affected.")
                .setView(fields)
                .setPositiveButton("Delete.", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (SharedPrefs.checkPINCode(fields.collectPINDigits())) {
                            Toast.makeText(activity, "Payments deleted.", Toast.LENGTH_SHORT).show();
                            activity.erasePayments();
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
