package cz.ondrejpittl.semestralka.listeners;

import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import cz.ondrejpittl.semestralka.R;
import cz.ondrejpittl.semestralka.factories.AnimationFactory;
import cz.ondrejpittl.semestralka.layout.PaymentRecord;
import cz.ondrejpittl.semestralka.partial.PaymentRecordStateEnum;

/**
 * Created by OndrejPittl on 15.04.16.
 */
public class PaymentRecordTouchListener implements View.OnTouchListener {

    private static boolean isActive;

    /**
     * Minimal finger move to be categorized as swipe.
     * Every move smaller is marked as tap/touch.
     */
    private static final int MIN_SWIPE_DISTANCE = 70;

    private PaymentRecord rec;

    /**
     * Width of hidden delete button.
     */
    //private int deleteActionWidth = 0;
    private int actionButtonWidth = 0;


    public PaymentRecordTouchListener(PaymentRecord rec) {
        super();
        this.rec = rec;
        PaymentRecordTouchListener.isActive = false;
    }

    int start = 0, end = 0, current = 0, delta = 0;
    Rect rect;

    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {

            //finger moving
            case MotionEvent.ACTION_MOVE:
                current = (int) event.getRawX();

                delta = current - start;
                Log.i("Ondra", "delta: " + delta);

                if (!rec.isActionBtnAnimating()) {
                    /*if(!rect.contains(rec.getLeft() + (int) event.getX(), rec.getTop() + (int) event.getY())){
                        Log.i("Ondra", "OUT OF BOUNDS!!!!!!!!!");
                    } else {
                        doSwipe(delta, view);
                    }*/

                    doSwipe(delta, view);
                }


                break;

            //finger release
            case MotionEvent.ACTION_UP:
                PaymentRecordTouchListener.isActive = false;
                end = (int) event.getRawX();
                delta = end - start;

                if (Math.abs(delta) < MIN_SWIPE_DISTANCE) {
                    //tap event
                    onTap(view);
                } else {
                    //swipe event finished
                    onSwipeFinished(delta, view);
                }

                break;

            //finger touch
            case MotionEvent.ACTION_DOWN:
                start = (int) event.getRawX();
                rect = new Rect(rec.getLeft(), rec.getTop(), rec.getRight(), rec.getBottom());

                break;
        }
        return true;
    }

    /**
     * Handles process triggered with tap event.
     * @param v View where was the event triggered.
     */
    public void onTap(View v) {

        FrameLayout actionButton = null;

        if(this.rec.getState() == PaymentRecordStateEnum.DELETE_ACTIVE) {
            actionButton = (FrameLayout) v.findViewById(R.id.recordDeleteWrapper);
        } else if(this.rec.getState() == PaymentRecordStateEnum.EDIT_ACTIVE) {
            actionButton = (FrameLayout) v.findViewById(R.id.recordEditWrapper);
        }

        if(actionButton != null)
            this.rec.hideAnimateActionBtn(actionButton, PaymentRecordStateEnum.NORMAL);

        AnimationFactory.animateCollapseToggle(v, this.rec);
    }

    /**
     * Handles process triggered with swipe event.
     * @param delta Distance of trajectory made with finger.
     * @param v View where was the event triggered.
     */
    public void doSwipe(int delta, View v) {
        int deleteActionBtnW = 0, editActionBtnW = 0;

        if(isSwipe(delta) && !this.rec.isCollapsed())
            onTap(v);

        if(isSwipe(delta)) PaymentRecordTouchListener.isActive = true;

        if (delta < 0) {

            //swipe left
            if (this.rec.getState() == PaymentRecordStateEnum.DELETE_ACTIVE || this.rec.getState() == PaymentRecordStateEnum.EDIT_ACTIVE) {
                if (isSwipe(delta)) onSwipeFinished(delta, v);
                return;
            }

            actionButtonWidth = deleteActionBtnW = normalizeActionBtnWidth(Math.abs(delta) - MIN_SWIPE_DISTANCE);
            editActionBtnW = 0;


        } else {

            //swipe right
            if (this.rec.getState() == PaymentRecordStateEnum.DELETE_ACTIVE || this.rec.getState() == PaymentRecordStateEnum.EDIT_ACTIVE) {
                if (isSwipe(delta)) onSwipeFinished(delta, v);
                return;
            }

            actionButtonWidth = editActionBtnW = normalizeActionBtnWidth(delta - MIN_SWIPE_DISTANCE);
            deleteActionBtnW = 0;

        }

        Log.i("Ondra", "action button width: " + actionButtonWidth);

        this.rec.setActionButtonWidth(deleteActionBtnW, v.findViewById(R.id.recordDeleteWrapper));
        this.rec.setActionButtonWidth(editActionBtnW, v.findViewById(R.id.recordEditWrapper));

    }

    /**
     * Handles process triggered at the end of swipe event with finger up.
     * @param delta Distance of trajectory made with finger.
     * @param v View where was the event triggered.
     */
    public void onSwipeFinished(int delta, View v) {
        FrameLayout actionBtn;

        if (delta < 0) {

            //swipe left
            if (this.rec.getState() == PaymentRecordStateEnum.NORMAL) {
                actionBtn = (FrameLayout) v.findViewById(R.id.recordDeleteWrapper);

                if (actionBtn.getWidth() > PaymentRecord.ACTION_TAB_WIDTH / 2) {
                    //show btn
                    this.rec.showAnimateActionBtn(actionBtn, PaymentRecordStateEnum.DELETE_ACTIVE);
                } else {
                    //hide btn
                    this.rec.hideAnimateActionBtn(actionBtn, PaymentRecordStateEnum.DELETE_ACTIVE);
                }

            } else if (this.rec.getState() == PaymentRecordStateEnum.EDIT_ACTIVE) {
                actionBtn = (FrameLayout) v.findViewById(R.id.recordEditWrapper);
                this.rec.hideAnimateActionBtn(actionBtn, PaymentRecordStateEnum.EDIT_ACTIVE);
            }

        } else {

            //swipe right
            if (this.rec.getState() == PaymentRecordStateEnum.NORMAL) {
                actionBtn = (FrameLayout) v.findViewById(R.id.recordEditWrapper);

                if (actionBtn.getWidth() > PaymentRecord.ACTION_TAB_WIDTH / 2) {
                    this.rec.showAnimateActionBtn(actionBtn, PaymentRecordStateEnum.EDIT_ACTIVE);
                } else {
                    this.rec.hideAnimateActionBtn(actionBtn, PaymentRecordStateEnum.EDIT_ACTIVE);
                }

            } else if (this.rec.getState() == PaymentRecordStateEnum.DELETE_ACTIVE) {
                actionBtn = (FrameLayout) v.findViewById(R.id.recordDeleteWrapper);
                this.rec.hideAnimateActionBtn(actionBtn, PaymentRecordStateEnum.DELETE_ACTIVE);
            }
        }
    }

    /**
     * Keeps button width in range.
     * @param btnWidth  Button width.
     * @return  Normalized width.
     */
    private int normalizeActionBtnWidth(int btnWidth) {
        int w = btnWidth;
        if (w < 0) w = 0;
        if (w > PaymentRecord.ACTION_TAB_WIDTH) w = PaymentRecord.ACTION_TAB_WIDTH;
        return w;
    }

    /**
     * Compares finger trajectory distance with minimal distance to trigger swiping.
     * @param delta  Distance of trajectory made with finger.
     * @return  True – finger trajectory is long enough; false – not enough.
     */
    private boolean isSwipe(int delta){
        return Math.abs(delta) > MIN_SWIPE_DISTANCE;
    }


    public int getActionButtonWidth(){
        return this.actionButtonWidth;
    }

    public static boolean isActive(){
        return PaymentRecordTouchListener.isActive;
    }
}
