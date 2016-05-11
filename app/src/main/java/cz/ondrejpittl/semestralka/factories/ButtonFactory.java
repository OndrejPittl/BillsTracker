package cz.ondrejpittl.semestralka.factories;

import android.content.Context;
import android.view.Gravity;
import android.view.View;

import cz.ondrejpittl.semestralka.layout.MyShadowTextView;

/**
 * Created by OndrejPittl on 30.03.16.
 */
public class ButtonFactory {

    /**
     * A default gravity of a button.
     */
    private static int txtGravity = Gravity.CENTER;

    /**
     * A default font size of a button.
     */
    private static int txtSize = 25;

    /**
     * A default padding of a button.
     */
    private static int[] padding = {7, 7, 7, 7};

    /**
     * Builds a new button with a shadow and configs given.
     * @param txt               a text of a button
     * @param txtSize           a font size
     * @param txtColor          a text color
     * @param gravity           a gravity
     * @param padding           a padding
     * @param onClickListener   handles on click event
     * @param appContext        an activity context
     * @return                  a new formatted button
     */
    public static MyShadowTextView createMyShadowTxtView(String txt, int txtSize, int txtColor, int gravity, int[] padding, View.OnClickListener onClickListener, Context appContext){
        MyShadowTextView btn = new MyShadowTextView(appContext);
        btn.setText(txt);
        btn.setGravity(gravity);
        btn.setTextColor(txtColor);
        btn.setTextSize(txtSize);
        btn.setPadding(padding[0], padding[1], padding[2], padding[3]);
        btn.setOnClickListener(onClickListener);
        return btn;
    }

    /**
     * Builds a new button with a shadow and configs given.
     * @param txt               a text of a button
     * @param txtColor          a text color
     * @param onClickListener   handles on click event
     * @param appContext        an activity context
     * @return                  a new formatted button
     */
    public static MyShadowTextView createMyShadowTxtView(String txt, int txtColor, View.OnClickListener onClickListener, Context appContext){
        return ButtonFactory.createMyShadowTxtView(txt, ButtonFactory.txtSize, txtColor, ButtonFactory.txtGravity, ButtonFactory.padding, onClickListener, appContext);
    }

    /**
     * Builds a new button with a shadow and configs given.
     * @param txt               a text of a button
     * @param txtSize           a font size
     * @param txtColor          a text color
     * @param padding           a padding
     * @param onClickListener   handles on click event
     * @param appContext        an activity context
     * @return                  a new formatted button
     */
    public static MyShadowTextView createMyShadowTxtView(String txt, int txtSize, int txtColor, int[] padding, View.OnClickListener onClickListener, Context appContext){
        return ButtonFactory.createMyShadowTxtView(txt, txtSize, txtColor, ButtonFactory.txtGravity, padding, onClickListener, appContext);
    }
}
