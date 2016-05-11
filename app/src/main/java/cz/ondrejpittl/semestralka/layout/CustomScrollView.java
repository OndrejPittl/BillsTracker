package cz.ondrejpittl.semestralka.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import cz.ondrejpittl.semestralka.listeners.PaymentRecordTouchListener;

/**
 * Created by OndrejPittl on 21.04.16.
 */
public class CustomScrollView extends ScrollView {

    /**
     * A constructor. Basics initialization.
     * @param context   an activity context reference
     */
    public CustomScrollView(Context context) {
        super(context);
    }

    /**
     * A constructor. Basics initialization.
     * @param context   an activity context reference
     * @param attrs     xml attributes
     */
    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * A constructor. Basics initialization.
     * @param context   an activity context reference
     * @param attrs     xml attributes
     * @param defStyleAttr  default xml styles
     */
    public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Checks whether is a scrollview scrollable.
     * @return  true – is scrollable, false – not
     */
    private boolean isScrollable() {
        return !PaymentRecordTouchListener.isActive();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:

                //if scroll is possible
                if (isScrollable()) return super.onTouchEvent(ev);

                return false;
            default:
                return super.onTouchEvent(ev);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isScrollable()) return false;
        else return super.onInterceptTouchEvent(ev);
    }

}
