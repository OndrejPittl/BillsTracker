package cz.ondrejpittl.semestralka.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by OndrejPittl on 29.04.16.
 */
public class CustomImageButton extends ImageButton {

    private boolean clicked;



    public CustomImageButton(Context context) {
        super(context);
        this.init();
    }

    public CustomImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init(){
        this.clicked = false;
    }

    public void updateBackground(){
        if(this.clicked) {

        }
    }
}
