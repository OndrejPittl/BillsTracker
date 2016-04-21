package cz.ondrejpittl.semestralka.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

import cz.ondrejpittl.semestralka.Listeners.PaymentRecordTouchListener;

/**
 * Created by OndrejPittl on 21.04.16.
 */
public class CustomScrollView extends ScrollView {
    public CustomScrollView(Context context) {
        super(context);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private boolean isScrollable() {
        //Log.i("Ondra", "--------------scrollable: " + !PaymentRecordTouchListener.isActive());
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
