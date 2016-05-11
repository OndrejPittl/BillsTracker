package cz.ondrejpittl.semestralka.layout;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import cz.ondrejpittl.semestralka.R;
import cz.ondrejpittl.semestralka.partial.Designer;
import cz.ondrejpittl.semestralka.partial.SharedPrefs;

/**
 * Created by OndrejPittl on 22.04.16.
 */
public class PinCodeFields extends LinearLayout {

    /**
     * Activity context reference.
     */
    private Context context;

    /**
     * Activity reference.
     */
    private Activity activity;

    /**
     * PIN field count.
     */
    private int fieldCount = 4;

    /**
     * Pin fields.
     */
    private EditText[] fields;


    /**
     * A constructor. Basics initialization.
     * @param context   an activity context reference
     */
    public PinCodeFields(Context context) {
        super(context);
        this.context = context;
        this.activity = (Activity) context;
    }

    /**
     * A constructor. Basics initialization.
     * @param context   an activity context reference
     * @param attrs     xml attributes
     */
    public PinCodeFields(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.activity = (Activity) context;
    }

    /**
     * Adds all PIN input field listeners.
     */
    public void setPinCodeListeners(){
        this.fields = new EditText[fieldCount];

        ImageButton clearBtn = (ImageButton) findViewById(R.id.pinClearBtn);
        clearBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                delAndGoFirst();
            }
        });

        //iteration through all PIN layout elements
        for (int i = 0; i < 4; i++) {
            //Toast.makeText(this.activity.getApplicationContext(), "xxx", Toast.LENGTH_SHORT).show();

            //id of element
            int resID = getContext().getResources().getIdentifier("edTxt_pin" + (i+1), "id", getContext().getPackageName());
            fields[i] = (EditText) findViewById(resID);
            fields[i].setTag(i + 1);

            //text change listener
            this.setPinCodeTextChangeListener(this.fields[i]);
        }
    }

    /**
     * Sets black background and text color.
     */
    public void setFieldsColorBlack(){
        for (int i = 0; i < 4; i++) {
            this.fields[i].setBackgroundResource(R.drawable.pin_code_input_style_black);
            this.fields[i].setTextColor(ContextCompat.getColor(getContext(), R.color.appTextBlack));
        }

        this.setPadding(
                Designer.dpToPx(30, this.activity),
                Designer.dpToPx(5, this.activity),
                Designer.dpToPx(30, this.activity),
                Designer.dpToPx(5, this.activity)
        );

        ImageButton eraseBtn = (ImageButton) findViewById(R.id.pinClearBtn);
        eraseBtn.setImageResource(R.drawable.erase_black);
    }


    /**
     * Handles requesting focus of next element after entering a value into a PIN input field.
     *
     * @param field Field its text is being changed.
     */
    private void setPinCodeTextChangeListener(final EditText field){
        field.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.i("Ondra-pin", "code: " + keyCode);

                if (keyCode == KeyEvent.KEYCODE_DEL) {

                    delAndGoFirst();

                } else {

                    writeAndGoNext(field);

                }

                return false;
            }
        });
    }

    /**
     * Deletes all PIN fields and sets focus on the first one.
     */
    private void delAndGoFirst(){
        for (EditText e : this.fields) {
            e.setText("");
        }

        goFirst();
        return;
    }

    /**
     * Writes a character (numter) and sets focus on next field.
     * @param field current field
     */
    private void writeAndGoNext(EditText field){
        //field EMPTY -> nothing to do
        if (field.getText().length() <= 0) return;
        this.goNext(field);
    }

    /**
     * Sets a focus on the first field.
     */
    private void goFirst() {
        fields[0].requestFocus();
    }

    /**
     * Sets a focus on a next field.
     * @param field current field
     */
    private void goNext(EditText field){
        int tag = (int) field.getTag();
        if (tag < fieldCount) {
            fields[tag].requestFocus();
        }  else {

            InputMethodManager imm = (InputMethodManager) this.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(field.getWindowToken(), 0);
            field.clearFocus();
        }
    }

    /**
     * Collects a PIN code.
     * @return  PIN
     */
    public String collectPINDigits(){
        String output = "";

        for(int i = 0; i <this.fieldCount; i++) {
            output += this.fields[i].getText();
        }

        return output;
    }

    /**
     *  Stores a PIN code.
     */
    public void storePINCode(){
        String entered = collectPINDigits();
        SharedPrefs.storePINCode(entered);
        SharedPrefs.storeFirstTimeLaunched(false);
    }

    /**
     * Checks an entered PIN code with a stored one.
     * @return  true – PIN is ok, false – not
     */
    public boolean checkPINCode(){
        String entered = collectPINDigits();

        if(SharedPrefs.checkPINCode(entered)) {
            Toast.makeText (
                    this.context,
                    this.activity.getString(R.string.loginOK),
                    //"OK",
                    Toast.LENGTH_SHORT
            ).show();
            return true;
        } else {
            Toast.makeText (
                    this.context,
                    this.activity.getString(R.string.loginERR),
                    //"ERR",
                    Toast.LENGTH_SHORT
            ).show();
            return false;
        }
    }
}
