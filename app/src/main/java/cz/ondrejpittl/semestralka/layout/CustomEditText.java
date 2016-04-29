package cz.ondrejpittl.semestralka.layout;

import android.content.Context;
import android.content.res.Configuration;
import android.inputmethodservice.Keyboard;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;

/**
 * Created by OndrejPittl on 28.04.16.
 */
public class CustomEditText extends EditText {

    public CustomEditText(Context context) {
        super(context);
        this.init();
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init(){
        Log.i("Ondra-pin", "initializing pin fields");
        //this.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        //this.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        //this.setRawInputType(Configuration.KEYBOARD_NOKEYS);
        //this.setInputType(InputType.TYPE_CLASS_NUMBER);

        this.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        this.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
    }

/*
    public InputConnection onCreateInputConnection(final EditorInfo outAttrs) {
        BaseInputConnection inputConnection = new BaseInputConnection(this, false);
        return inputConnection;
    }
*/

    private void handleEditTextDeleteEvent(){
        Log.i("Ondra-pin", "DELETEEE");
    }


}
