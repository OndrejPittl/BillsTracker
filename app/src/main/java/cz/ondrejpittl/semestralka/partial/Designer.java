package cz.ondrejpittl.semestralka.partial;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import cz.ondrejpittl.semestralka.HomeActivity;
import cz.ondrejpittl.semestralka.R;

/**
 * Created by OndrejPittl on 08.05.16.
 */
public class Designer {

    private static final String[] BG = {
            "bg1", "bg2", "bg3", "bg4", "bg5"
    };

    private static final String[] textColors = {
        "appTextPaymentRecord1",
        "appTextPaymentRecord2",
        "appTextPaymentRecord2",
        "appTextPaymentRecord2",
        "appTextPaymentRecord2"
    };


    /*private static int getColor(String name, Activity activity){
        //Resources resources = activity.getResources();
        //return resources.getIdentifier(name, "color", activity.getPackageName());
        return ContextCompat.getColor(activity, R.color.appWhite);
    }*/

    private static int getResource(String name, Activity activity){
        Resources resources = activity.getResources();
        return resources.getIdentifier(name, "drawable", activity.getPackageName());
        //final int resourceId = resources.getIdentifier(name, "drawable", activity.getPackageName());
        //return resources.getDrawable(resourceId);
    }

    public static void updateDesign(Activity activity) {
        int designIndex = SharedPrefs.getDefaultDesign();
        Designer.setBackground(designIndex, activity);
    }


    private static void setBackground(int designIndex, Activity activity){
        ImageView bg = (ImageView) activity.findViewById(R.id.imgViewBG);
        bg.setImageResource(Designer.getResource(Designer.BG[designIndex], activity));
    }

    public static void setFullscreenActivity(Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /*public static void updatePaymentRecordLabelColor(TextView tv, Activity activity){
        tv.setTextColor(Designer.getColor(Designer.textColors[SharedPrefs.getDefaultDesign()], activity));
    }*/
}
