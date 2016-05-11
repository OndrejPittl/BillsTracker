package cz.ondrejpittl.semestralka.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by OndrejPittl on 29.04.16.
 */
public class CustomImageButton extends ImageButton {

    /**
     * A flag indicating whether was a button clicked or not.
     */
    private boolean clicked;


    /**
     * A constructor. Basics initialization.
     * @param context   an activity context reference
     */
    public CustomImageButton(Context context) {
        super(context);
        this.init();
    }

    /**
     * A constructor. Basics initialization.
     * @param context   an activity context reference
     * @param attrs     attributes
     */
    public CustomImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    /**
     * Initialization.
     */
    private void init(){
        this.clicked = false;
    }
}
