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


    //private static float defaultTextSize = 19;
    //private static int defaultTextColor = Color.WHITE;

    private static boolean defaultTextShadow = true;


    /*
    public MyTextView(Context context, AttributeSet attrs){
        this (  context,
                attrs,
                MyTextView.defaultTextSize,
                MyTextView.defaultTextColor,
                MyTextView.defaultTextShadow
        );
    }

    public MyTextView(Context context, AttributeSet attrs, float textSize){
        this (  context,
                attrs,
                textSize,
                MyTextView.defaultTextColor,
                MyTextView.defaultTextShadow
        );
    }

    public MyTextView(Context context, AttributeSet attrs,float textSize, int textColor){
        this(context, attrs, textSize, textColor, MyTextView.defaultTextShadow);

        //attrs = a set of XML attributes
    }

    public MyTextView(Context context, AttributeSet attrs, float textSize, int textColor, boolean shadowEnabled) {
        super(context, attrs);
        this.setCustomFont(context);

        this.setTextSize(textSize);
        this.setTextColor(textColor);

        if(shadowEnabled)
            this.setShadowLayer(0, 0f, 1f, Color.BLACK);
    }

    */


    public MyShadowTextView(Context context){
        super(context);
        this.setCustomFont(context);
        this.setShadowLayer(1, 0f, 2f, Color.BLACK);
    }

    public MyShadowTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setCustomFont(context);
        this.setShadowLayer(1, 0f, 2f, Color.BLACK);
    }

    private void setCustomFont(Context context){
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/lora.ttf"));
    }
}
