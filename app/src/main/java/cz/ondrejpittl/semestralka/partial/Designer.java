package cz.ondrejpittl.semestralka.partial;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import cz.ondrejpittl.semestralka.HomeActivity;
import cz.ondrejpittl.semestralka.R;
import cz.ondrejpittl.semestralka.layout.CustomSpinner;

import static cz.ondrejpittl.semestralka.R.color.appError;
import static cz.ondrejpittl.semestralka.R.drawable.shape_thin_border;
import static cz.ondrejpittl.semestralka.R.drawable.shape_thin_border_error;
import static cz.ondrejpittl.semestralka.R.id.edTxt_amount;
import static cz.ondrejpittl.semestralka.R.id.homeAmountContainer;

/**
 * Created by OndrejPittl on 08.05.16.
 */
public class Designer {

    /**
     * Array of a changeable backgrounds.
     */
    private static final String[] BG = {
            "bg1", "bg2", "bg3", "bg4", "bg5"
    };

    /**
     * Getter of a resource.
     * @param name  resource name
     * @param activity  activity reference
     * @return  resource
     */
    private static int getResource(String name, Activity activity){
        Resources resources = activity.getResources();
        return resources.getIdentifier(name, "drawable", activity.getPackageName());
    }

    /**
     * Loads a stored design ID and sets a background.
     * @param activity  activity reference
     */
    public static void updateDesign(Activity activity) {
        int designIndex = SharedPrefs.getDefaultDesign();
        Designer.setBackground(designIndex, activity);
    }

    /**
     * Sets background od an activity.
     * @param designIndex   design index
     * @param activity      activity reference
     */
    private static void setBackground(int designIndex, Activity activity){
        ImageView bg = (ImageView) activity.findViewById(R.id.imgViewBG);
        bg.setImageResource(Designer.getResource(Designer.BG[designIndex], activity));
    }

    /**
     * Sets full screen activity.
     * @param activity  activity reference
     */
    public static void setFullscreenActivity(Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * Getter of a display width in pixels.
     * @param activity  activity reference
     * @return  width of a device screen in pixels
     */
    public static int getDisplayWidthInPx(Activity activity){
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels ;
    }

    /**
     * Mark an error input element.
     * @param t         type of an input element
     * @param activity  activity where is an element placed
     */
    public static void markInputError(InputFieldType t, Activity activity){
        int errColor = ContextCompat.getColor(activity, appError);

        switch(t) {
            case EDIT_TEXT_AMOUNT:
                EditText et = (EditText) activity.findViewById(edTxt_amount);
                et.setTextColor(errColor);
                et.setHintTextColor(errColor);

                LinearLayout layout = (LinearLayout) activity.findViewById(homeAmountContainer);
                layout.setBackgroundResource(shape_thin_border_error);

                break;

            case CUSTOM_SPINNER_CATEGORY:

                CustomSpinner spinCat = (CustomSpinner) activity.findViewById(R.id.spinner_category);
                spinCat.setBackgroundResource(shape_thin_border_error);

                break;

            case CUSTOM_SPINNER_STORE:

                CustomSpinner spinSt = (CustomSpinner) activity.findViewById(R.id.spinner_store);
                spinSt.setBackgroundResource(shape_thin_border_error);

                break;
        }
    }

    /**
     * Unarks an error input element.
     * @param t         type of an input element
     * @param activity  activity where is an element placed
     */
    public static void unmarkInputError(InputFieldType t, Activity activity){
        int okColor = ContextCompat.getColor(activity, R.color.appWhite);

        switch(t) {
            case EDIT_TEXT_AMOUNT:

                EditText et = (EditText) activity.findViewById(edTxt_amount);
                et.setTextColor(okColor);
                et.setHintTextColor(okColor);

                LinearLayout layout = (LinearLayout) activity.findViewById(homeAmountContainer);
                layout.setBackgroundResource(shape_thin_border);

                break;

            case CUSTOM_SPINNER_CATEGORY:

                CustomSpinner spinCat = (CustomSpinner) activity.findViewById(R.id.spinner_category);
                spinCat.setBackgroundResource(shape_thin_border);

                break;

            case CUSTOM_SPINNER_STORE:

                CustomSpinner spinSt = (CustomSpinner) activity.findViewById(R.id.spinner_store);
                spinSt.setBackgroundResource(shape_thin_border);

                break;
        }
    }

    /**
     * Unmarks all input elements.
     */
    public static void unmarkInputErrorAll(Activity activity){
        int okColor = ContextCompat.getColor(activity, R.color.appWhite);

        //amount
        EditText et = (EditText) activity.findViewById(edTxt_amount);
        et.setTextColor(okColor);
        et.setHintTextColor(okColor);

        LinearLayout layout = (LinearLayout) activity.findViewById(homeAmountContainer);
        layout.setBackgroundResource(shape_thin_border);


        //category
        CustomSpinner spinCat = (CustomSpinner) activity.findViewById(R.id.spinner_category);
        spinCat.setBackgroundResource(shape_thin_border);


        //store
        CustomSpinner spinSt = (CustomSpinner) activity.findViewById(R.id.spinner_store);
        spinSt.setBackgroundResource(shape_thin_border);
    }

    /**
     * Calculates dp -> px.
     * @param dp        dp to be calculated
     * @param context   activity context reference
     * @return          pixels
     */
    public static int dpToPx(int dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}
