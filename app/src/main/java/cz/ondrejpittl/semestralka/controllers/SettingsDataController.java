package cz.ondrejpittl.semestralka.controllers;

import android.widget.Spinner;
import android.widget.Switch;

import java.util.ArrayList;

import cz.ondrejpittl.semestralka.R;
import cz.ondrejpittl.semestralka.SettingsActivity;
import cz.ondrejpittl.semestralka.StatisticsActivity;
import cz.ondrejpittl.semestralka.database.DBManager;
import cz.ondrejpittl.semestralka.layout.CustomSpinner;
import cz.ondrejpittl.semestralka.models.Category;
import cz.ondrejpittl.semestralka.models.Currency;
import cz.ondrejpittl.semestralka.models.Store;
import cz.ondrejpittl.semestralka.partial.EditRecordType;
import cz.ondrejpittl.semestralka.partial.SharedPrefs;

/**
 * Created by OndrejPittl on 07.05.16.
 */
public class SettingsDataController {

    private DBManager dbManager;

    /**
     * Activity its UI is being controlled.
     */
    private SettingsActivity activity;


    public SettingsDataController(SettingsActivity activity, DBManager dbManager) {
        this.activity = activity;
        this.dbManager = dbManager;
    }

    public void storeSettings(){

        Switch swPassReq = (Switch) this.activity.findViewById(R.id.settingsPassReq),
                swNotesDisplay = (Switch) this.activity.findViewById(R.id.settingsNoteSwitch),
                swPaymentAnim = (Switch) this.activity.findViewById(R.id.settingsAnimationSwitch),
                swPaymentIcons = (Switch) this.activity.findViewById(R.id.settingsIconSwitch);

        CustomSpinner spinCat = (CustomSpinner) this.activity.findViewById(R.id.settingsCategorySpinner),
                spinSto = (CustomSpinner) this.activity.findViewById(R.id.settingsStoreSpinner),
                spinCur = (CustomSpinner) this.activity.findViewById(R.id.settingsCurrencySpinner),
                designSelect = (CustomSpinner) this.activity.findViewById(R.id.bgSpinner);


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
        if(cat != null)
            SharedPrefs.storeDefaultCategory(cat.getName());

        int designIndex = designSelect.getSelectedItemPosition();
        SharedPrefs.storeDefaultDesign(designIndex);

        Store store = (Store) spinSto.getSelectedItem();
        if(store != null)
            SharedPrefs.storeDefaultStore(store.getName());
    }

    public void deleteRecord(int id, EditRecordType type){
        switch (type){
            case CATEGORY:
                this.dbManager.deleteCategory(id);
                break;

            case STORE:
                this.dbManager.deleteStore(id);
                break;

            default:
                break;
        }
    }

    public ArrayList<Store> getStoredStores(){
        return this.dbManager.getStoredStores();
    }

    public ArrayList<Category> getStoredCategories(){
        return this.dbManager.getStoredCategories();
    }

    public ArrayList<Currency> getStoredCurrencies(){
        return this.dbManager.getStoredCurrencies();
    }
}
