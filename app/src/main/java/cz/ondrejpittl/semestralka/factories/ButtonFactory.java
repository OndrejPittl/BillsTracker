package cz.ondrejpittl.semestralka.factories;

import android.content.Context;
import android.view.Gravity;
import android.view.View;

import cz.ondrejpittl.semestralka.layout.MyShadowTextView;

/**
 * Created by OndrejPittl on 30.03.16.
 */
public class ButtonFactory {

    private static int txtGravity = Gravity.CENTER;
    private static int txtSize = 25;
    private static int[] padding = {7, 7, 7, 7};

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

    public static MyShadowTextView createMyShadowTxtView(String txt, int txtColor, View.OnClickListener onClickListener, Context appContext){
        return ButtonFactory.createMyShadowTxtView(txt, ButtonFactory.txtSize, txtColor, ButtonFactory.txtGravity, ButtonFactory.padding, onClickListener, appContext);
    }

    public static MyShadowTextView createMyShadowTxtView(String txt, int txtSize, int txtColor, int[] padding, View.OnClickListener onClickListener, Context appContext){
        return ButtonFactory.createMyShadowTxtView(txt, txtSize, txtColor, ButtonFactory.txtGravity, padding, onClickListener, appContext);
    }

    /*public static MyShadowTextView createMyShadowTxtView(String txt, int txtSize, int txtColor, View.OnClickListener onClickListener, Context appContext){
        return ButtonFactory.createMyShadowTxtView(txt, txtSize, txtColor, ButtonFactory.txtGravity, ButtonFactory.padding, onClickListener, appContext);
    }*/

}
