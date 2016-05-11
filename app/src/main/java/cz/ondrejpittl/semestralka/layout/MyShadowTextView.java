package cz.ondrejpittl.semestralka.layout;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by OndrejPittl on 23.03.16.
 */
public class MyShadowTextView extends TextView {

    /**
     * A constructor. Basics initialization.
     * @param context   an activity context reference
     */
    public MyShadowTextView(Context context){
        super(context);
        this.setCustomFont(context);
        this.setShadowLayer(1, 0f, 2f, Color.BLACK);
    }

    /**
     * A constructor. Basics initialization.
     * @param context   an activity context reference
     * @param attrs     xml attributes
     */
    public MyShadowTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setCustomFont(context);
        this.setShadowLayer(1, 0f, 2f, Color.BLACK);
    }

    /**
     * Sets custom font.
     * @param context   an activity context reference
     */
    private void setCustomFont(Context context){
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/lora.ttf"));
    }
}
