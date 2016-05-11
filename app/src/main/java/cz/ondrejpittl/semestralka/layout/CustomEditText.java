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

    /**
     * A constructor. Basics initialization.
     * @param context   an activity context reference
     */
    public CustomEditText(Context context) {
        super(context);
        this.init();
    }

    /**
     * A constructor. Basics initialization.
     * @param context   an activity context reference
     * @param attrs     attributes from xml
     */
    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    /**
     * A constructor. Basics initialization.
     * @param context   an activity context reference
     * @param attrs     attributes from xml
     * @param defStyleAttr  default style attributes from xml
     */
    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    /**
     * Initialization.
     */
    private void init(){
        this.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        this.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
    }
}
