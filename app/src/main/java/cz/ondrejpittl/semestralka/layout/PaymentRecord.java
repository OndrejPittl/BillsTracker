package cz.ondrejpittl.semestralka.layout;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import cz.ondrejpittl.semestralka.R;
import cz.ondrejpittl.semestralka.controllers.HomeDataController;
import cz.ondrejpittl.semestralka.models.Payment;
import cz.ondrejpittl.semestralka.partial.PaymentRecordStateEnum;

/**
 * Created by OndrejPittl on 08.04.16.
 */
public class PaymentRecord extends LinearLayout {

    private HomeDataController contorller;


    /**
     * ID of payment record.
     */
    private int paymentId;

    /**
     * Payment record reference.
     */
    private Payment payment;


    /**
     * Flag indicating whether or not is payment record collapsed – whether or not is note visible.
     */
    private boolean collapsed;

    /**
     * Height of collapsed payment record.
     */
    private int collapsedHeight;


    /**
     * Width of action button hidden under a payment record and visible while swiping.
     */
    private int ACTION_TAB_WIDTH = 200;

    /**
     * Minimal finger move to be categorized as swipe.
     * Every move smaller is marked as tap/touch.
     */
    private final int MIN_SWIPE_DISTANCE = 70;


    /**
     * Width of hidden delete button.
     */
    //private int deleteActionWidth = 0;
    private int actionButtonWidth = 0;

    /**
     *  Flag indicating whether or not is action button animating.
     */
    private boolean actionBtnAnimating = false;



    /**
     *  State of payment record.
     *  State can acquire NORMAL (no action is being triggered/none is visible),
     *                    DELETE_VISIBLE (delete action is being triggered/delete action btn is visible),
     *                    EDIT_VISIBLE (edit action is being triggered/edit action btn is visible).
     */
    private PaymentRecordStateEnum state = PaymentRecordStateEnum.NORMAL;

    /**
     *  Flag indicating whether or not is edit action button visible.
     */
    private boolean editActionVisible = false;

    /**
     * Width of device display.
     */
    private static int displayWidth;



    public PaymentRecord(Context context) {
        super(context);
        this.init();
    }

    public PaymentRecord(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public PaymentRecord(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    /**
     * Payment record initialization.
     */
    private void init() {
        this.collapsed = true;
        this.collapsedHeight = -1;

        ACTION_TAB_WIDTH = displayWidth / 6;


        this.setOnTouchListener(new OnTouchListener() {
            int start = 0, end = 0, current = 0, delta = 0;

            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {

                    //finger moving
                    case MotionEvent.ACTION_MOVE:
                        current = (int) event.getRawX();

                        delta = current - start;
                        Log.i("Ondra", "delta: " + delta);

                        if (!actionBtnAnimating)
                            doSwipe(delta, view);

                        break;

                    //finger release
                    case MotionEvent.ACTION_UP:
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
                        break;
                }
                return true;
            }
        });
    }

    /**
     * Handles process triggered with tap event.
     * @param v View where was the event triggered.
     */
    private void onTap(View v) {
        collapseToggle(v);
    }

    /**
     * Handles process triggered with swipe event.
     * @param delta Distance of trajectory made with finger.
     * @param v View where was the event triggered.
     */
    private void doSwipe(int delta, View v) {
        int deleteActionBtnW = 0, editActionBtnW = 0;

        if (delta < 0) {

            //swipe left
            if (state == PaymentRecordStateEnum.DELETE_ACTIVE || state == PaymentRecordStateEnum.EDIT_ACTIVE) {
                if (isSwipe(delta)) onSwipeFinished(delta, v);
                return;
            }

            actionButtonWidth = deleteActionBtnW = normalizeActionBtnWidth(Math.abs(delta) - MIN_SWIPE_DISTANCE);
            editActionBtnW = 0;


        } else {

            //swipe right
            if (state == PaymentRecordStateEnum.DELETE_ACTIVE || state == PaymentRecordStateEnum.EDIT_ACTIVE) {
                if (isSwipe(delta)) onSwipeFinished(delta, v);
                return;
            }

            actionButtonWidth = editActionBtnW = normalizeActionBtnWidth(delta - MIN_SWIPE_DISTANCE);
            deleteActionBtnW = 0;

        }

        Log.i("Ondra", "action button width: " + actionButtonWidth);

        setActionButtonWidth(deleteActionBtnW, v.findViewById(R.id.recordDeleteWrapper));
        setActionButtonWidth(editActionBtnW, v.findViewById(R.id.recordEditWrapper));

    }

    /**
     * Handles process triggered at the end of swipe event with finger up.
     * @param delta Distance of trajectory made with finger.
     * @param v View where was the event triggered.
     */
    private void onSwipeFinished(int delta, View v) {
        FrameLayout actionBtn;

        if (delta < 0) {

            //swipe left
            if (state == PaymentRecordStateEnum.NORMAL) {
                actionBtn = (FrameLayout) v.findViewById(R.id.recordDeleteWrapper);

                if (actionBtn.getWidth() > ACTION_TAB_WIDTH / 2) {
                    //show btn
                    showAnimateActionBtn(actionBtn, PaymentRecordStateEnum.DELETE_ACTIVE);
                } else {
                    //hide btn
                    hideAnimateActionBtn(actionBtn, PaymentRecordStateEnum.DELETE_ACTIVE);
                }

            } else if (state == PaymentRecordStateEnum.EDIT_ACTIVE) {
                actionBtn = (FrameLayout) v.findViewById(R.id.recordEditWrapper);
                hideAnimateActionBtn(actionBtn, PaymentRecordStateEnum.EDIT_ACTIVE);
            }

        } else {

            //swipe right
            if (state == PaymentRecordStateEnum.NORMAL) {
                actionBtn = (FrameLayout) v.findViewById(R.id.recordEditWrapper);

                if (actionBtn.getWidth() > ACTION_TAB_WIDTH / 2) {
                    showAnimateActionBtn(actionBtn, PaymentRecordStateEnum.EDIT_ACTIVE);
                } else {
                    hideAnimateActionBtn(actionBtn, PaymentRecordStateEnum.EDIT_ACTIVE);
                }

            } else if (state == PaymentRecordStateEnum.DELETE_ACTIVE) {
                actionBtn = (FrameLayout) v.findViewById(R.id.recordDeleteWrapper);
                hideAnimateActionBtn(actionBtn, PaymentRecordStateEnum.DELETE_ACTIVE);
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
        if (w > ACTION_TAB_WIDTH) w = ACTION_TAB_WIDTH;
        return w;
    }

    /**
     * Sets button given via input param width.
     * @param w Width to be set.
     * @param v Button reference.
     */
    private void setActionButtonWidth(int w, View v) {
        FrameLayout wrapper = (FrameLayout) v;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) wrapper.getLayoutParams();

        params.width = w;
        wrapper.setLayoutParams(params);
        wrapper.requestLayout();
    }

    /**
     * Triggers action button animation hiding it.
     * @param actionBtn Animated action button reference.
     * @param recState  State of payment record.
     */
    private void hideAnimateActionBtn(FrameLayout actionBtn, PaymentRecordStateEnum recState) {
        animateActionButton(actionBtn, 0, recState);
    }

    /**
     * Triggers action button animation showing it.
     * @param actionBtn Animated action button reference.
     * @param recState  State of payment record.
     */
    private void showAnimateActionBtn(FrameLayout actionBtn, PaymentRecordStateEnum recState) {
        animateActionButton(actionBtn, ACTION_TAB_WIDTH, recState);
    }

    /**
     * Compares finger trajectory distance with minimal distance to trigger swiping.
     * @param delta  Distance of trajectory made with finger.
     * @return  True – finger trajectory is long enough; false – not enough.
     */
    private boolean isSwipe(int delta){
        return Math.abs(delta) > MIN_SWIPE_DISTANCE;
    }

    /**
     * Setter storing device display width.
     * @param width Device display width.
     */
    public static void setDisplayWidth(int width){
        PaymentRecord.displayWidth = width;
    }

    /**
     * Function hiding edit action button.
     */
    public void hideEditActionButton(){
        FrameLayout editWrapper = (FrameLayout) this.findViewById(R.id.recordEditWrapper);
        animateActionButton(editWrapper, 0, PaymentRecordStateEnum.EDIT_ACTIVE);
    }

    /**
     *  Marks payment record being edited.
     * @param editing   True - record is being edited; false – it's not.
     */
    public void setEditing(boolean editing){
        if(editing){
            this.findViewById(R.id.recordMainWrapper).setBackgroundColor(ContextCompat.getColor(getContext(), R.color.appWhite50));
        } else {
            this.findViewById(R.id.recordMainWrapper).setBackgroundColor(ContextCompat.getColor(getContext(), R.color.appWhite25));
        }
    }


    public int getPaymentId() {
        return this.payment.getID();
    }

    public void setPayment(Payment p) {
        this.payment = p;
    }

    public Payment getPayment() {
        return this.payment;
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
    }

    public void setHomeController(HomeDataController dControl){
        this.contorller = dControl;
    }

    public ImageButton getRecordDeleteBtn(){
        return (ImageButton) this.findViewById(R.id.btnRecordDelete);
    }

    public ImageButton getRecordEditBtn(){
        return (ImageButton) this.findViewById(R.id.btnRecordEdit);
    }

    /**
     * Note collapse animation handler.
     * @param v View being collapsed.
     */
    private void collapseToggle(final View v) {
        final int from, to, diff;

        if(this.collapsedHeight < 0)
            this.collapsedHeight = v.getHeight() - 5;

        //note height
        TextView tvNote = (TextView) v.findViewById(R.id.txtViewRecordNote);
        tvNote.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final int noteHeight = tvNote.getMeasuredHeight() + 10;

        //base height
        //v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final int targetHeight = this.collapsedHeight;

        if(this.isCollapsed()) {
            from = targetHeight;
            to = from + noteHeight;
            diff = to - from;
            this.setCollapsed(false);
        } else {
            to = targetHeight;
            from = to + noteHeight;
            diff = to - from;
            this.setCollapsed(true);
        }

        Animation a = new Animation() {
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1 ? to : (int)(from + diff * interpolatedTime);
                v.requestLayout();
            }
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight + noteHeight / v.getContext().getResources().getDisplayMetrics().density));
        a.setFillEnabled(true);
        a.setFillAfter(true);
        v.startAnimation(a);
    }


    /**
     * Function animating edit/delete action button.
     * @param v         Action button being animated.
     * @param to        Final action button width-state.
     * @param recState  Payment record state.
     */
    private void animateActionButton(final View v, final int to, final PaymentRecordStateEnum recState) {
        final int from, diff;

        actionBtnAnimating = true;

        from = actionButtonWidth;
        diff = Math.abs(to - from);

        Log.i("Ondra", "from: " + from);
        Log.i("Ondra", "to: " + to);

        Animation a = new Animation() {
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                float interTime;
                int base, realWidth;

                if(to < from) {
                    interTime = (1 - interpolatedTime);
                    base = 0;
                } else {
                    interTime = interpolatedTime;
                    base = from;
                }

                if(interpolatedTime == 1) {
                    realWidth = to;
                }  else {
                    realWidth = (int)(base + diff * interTime);
                }

                Log.i("Ondra", "inter-time: " + interTime);
                Log.i("Ondra", "w: " + (int)(diff * interTime));
                Log.i("Ondra", "realWidth: " + realWidth);

                v.getLayoutParams().width = realWidth;
                v.requestLayout();

                Log.i("Ondra", "nastaveno: " + v.getLayoutParams().width);


            }
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setAnimationListener(new Animation.AnimationListener(){
            public void onAnimationStart(Animation arg0) {}
            public void onAnimationRepeat(Animation arg0) {}
            public void onAnimationEnd(Animation arg0) {

                if(recState == PaymentRecordStateEnum.DELETE_ACTIVE) {

                    if(to < from) {
                        //deleteActionVisible = false;
                        state = PaymentRecordStateEnum.NORMAL;
                    } else {
                        //deleteActionVisible = true;
                        state = PaymentRecordStateEnum.DELETE_ACTIVE;
                    }

                } else if(recState == PaymentRecordStateEnum.EDIT_ACTIVE) {

                    if(to < from) {
                        state = PaymentRecordStateEnum.NORMAL;
                    } else {
                        state = PaymentRecordStateEnum.EDIT_ACTIVE;
                    }

                }

                actionBtnAnimating = false;

            }
        });

        a.setDuration(300);
        v.startAnimation(a);
    }
}