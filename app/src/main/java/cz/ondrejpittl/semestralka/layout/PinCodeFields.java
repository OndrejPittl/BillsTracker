package cz.ondrejpittl.semestralka.layout;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cz.ondrejpittl.semestralka.HomeActivity;
import cz.ondrejpittl.semestralka.R;
import cz.ondrejpittl.semestralka.WelcomeActivity;
import cz.ondrejpittl.semestralka.partial.SharedPrefs;

/**
 * Created by OndrejPittl on 22.04.16.
 */
public class PinCodeFields extends LinearLayout {

    private Context context;

    private Activity activity;

    private int fieldCount = 4;

    private EditText[] fields;


    public PinCodeFields(Context context) {
        super(context);
        this.context = context;
        this.activity = (Activity) context;
        //this.activity = (WelcomeActivity) context;
    }

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

    public void setFieldsColorBlack(){
        for (int i = 0; i < 4; i++) {
            //this.fields[i].setBackground(this.activity.getResources().getDrawable(R.drawable.pin_code_input_style_black));
            this.fields[i].setBackgroundResource(R.drawable.pin_code_input_style_black);
            this.fields[i].setTextColor(ContextCompat.getColor(getContext(), R.color.appTextBlack));
            //this.fields[i].setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.pin_code_input_style_black));
        }

        //LinearLayout container = (LinearLayout) findViewById(R.id.pinCodeFieldsWrapperContainer);
        this.setPadding(dpToPx(30, this.activity), dpToPx(5, this.activity), dpToPx(30, this.activity), dpToPx(5, this.activity));

        ImageButton eraseBtn = (ImageButton) findViewById(R.id.pinClearBtn);
        eraseBtn.setImageResource(R.drawable.erase_black);
    }

    private int dpToPx(int dp, Activity activity) {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    /**
     * Handles requesting focus of next element after entering a value into a PIN input field.
     *
     * @param field Field its text is being changed.
     */
    private void setPinCodeTextChangeListener(final EditText field){

        /*field.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Log.d("Ondra-pin", "after");
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("Ondra-pin", "before");
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("Ondra-pin", "on");
            }
        });*/



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

    private void delAndGoFirst(){
        //Log.i("Ondra-pin", "BACKSPACED");

        for (EditText e : this.fields) {
            e.setText("");
        }

        goFirst();

        return;
    }

    private void writeAndGoNext(EditText field){

        //field EMPTY -> nothing to do
        if (field.getText().length() <= 0) return;

        this.goNext(field);

    }

    private void goFirst() {
        fields[0].requestFocus();
    }

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

    private void goPrev(EditText field){
        int tag = (int) field.getTag();
        if (tag > 1) {
            //-1 -> id, -1 1 lower
            fields[tag - 2].requestFocus();
        }
    }

    public String collectPINDigits(){
        String output = "";

        for(int i = 0; i <this.fieldCount; i++) {
            output += this.fields[i].getText();
        }

        return output;
    }


    public void storePINCode(){
        String entered = collectPINDigits();
        //String encrypted = encryptIt(entered);

        /*Log.i("Ondra-prefs", "– storing PIN code –");
        Log.i("Ondra-prefs", "entered: " + entered + " (" + encrypted + ")");*/

        SharedPrefs.storePINCode(entered);
        SharedPrefs.registerFirstTimeLaunched();
    }

    public boolean checkPINCode(){

        String entered = collectPINDigits();

        /*Log.i("Ondra-prefs", "– checking PIN code –");
        Log.i("Ondra-prefs", "entered PIN: " + entered + "(" + encrypted + ")");
        Log.i("Ondra-prefs", "stored PIN: " + stored);*/


        if(SharedPrefs.checkPINCode(entered)) {
            Toast.makeText (
                    this.context,
                    //activity.getApplicationContext(),
                    this.activity.getString(R.string.loginOK),
                    //"OK",
                    Toast.LENGTH_SHORT
            ).show();
            return true;
        } else {
            Toast.makeText (
                    //activity.getApplicationContext(),
                    this.context,
                    this.activity.getString(R.string.loginERR),
                    //"ERR",
                    Toast.LENGTH_SHORT
            ).show();
            return false;
        }
    }
}
