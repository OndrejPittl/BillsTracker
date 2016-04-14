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
     *  Flag indicating whether or not is delete action button visible.
     */
    //private boolean deleteActionVisible = false;


    /**
     * Width of hidden edit button.
     */
    //private int editActionWidth = 0;

    /**
     *  Flag indicating whether or not is edit action button visible.
     */
    private boolean editActionVisible = false;


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

    private void init(){
        this.collapsed = true;
        this.collapsedHeight = -1;
        /*this.expandedHeight = -1;*/

        ACTION_TAB_WIDTH = displayWidth / 6;


        this.setOnTouchListener(new OnTouchListener() {

            /**
             * Minimal finger move to be categorized as swipe.
             * Every move smaller is marked as tap/touch.
             */
            private final int MIN_SWIPE_DISTANCE = 70;

            //boolean swiping = false;

            int start = 0,
                    end = 0,
                    current = 0,
                    delta = 0;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {

                    //finger moving
                    case MotionEvent.ACTION_MOVE:
                        current = (int) event.getRawX();
                        delta = current - start;

                        Log.i("Ondra", "delta: " + delta);

                        if (Math.abs(delta) > MIN_SWIPE_DISTANCE) {
                            if (delta < 0) {
                                doSwipeLeft(delta, view);
                            } else {
                                doSwipeRight(delta, view);
                            }
                        }

                        break;

                    //finger up
                    case MotionEvent.ACTION_UP:
                        end = (int) event.getRawX();
                        delta = end - start;

                        /*Log.i("Ondra", "X-start: " + start);
                        Log.i("Ondra", "X-end: " + end);
                        Log.i("Ondra", "delta: " + delta);*/

                        if (Math.abs(delta) < MIN_SWIPE_DISTANCE) {
                            //tap event
                            onTap(view);
                        } else {
                            //swipe event
                            if (delta < 0) {
                                onSwipeLeftFinished(view);
                            } else {
                                onSwipeRightFinished(view);
                            }
                        }

                        break;

                    case MotionEvent.ACTION_DOWN:
                        start = (int) event.getRawX();
                        break;
                }

                return true;
            }

            private boolean onTap(View v) {
                collapseToggle(v);
                return true;
            }

            private void setActionButtonWidth(View v) {
                FrameLayout wrapper = (FrameLayout) v;
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) wrapper.getLayoutParams();

                params.width = actionButtonWidth;
                wrapper.setLayoutParams(params);
                wrapper.requestLayout();
            }

            /*private void setEditActionButtonWidth(View v) {
                FrameLayout editWrapper = (FrameLayout) v.findViewById(R.id.recordEditWrapper);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) editWrapper.getLayoutParams();

                params.width = actionButtonWidth;
                editWrapper.setLayoutParams(params);
                editWrapper.requestLayout();
            }*/

            private boolean doSwipeLeft(int delta, View v) {
                //Log.i("Ondra", "swiping left");

                //if (deleteActionVisible) return true;
                //if(state == PaymentRecordStateEnum.DELETE_ACTIVE) return true;

                if (actionBtnAnimating)
                    return false;


                if (state == PaymentRecordStateEnum.NORMAL) {

                    int d = Math.abs(Math.abs(delta) - MIN_SWIPE_DISTANCE);
                    if (d < ACTION_TAB_WIDTH) actionButtonWidth = d;
                    Log.i("Ondra", "delete width: " + actionButtonWidth);
                    setActionButtonWidth(v.findViewById(R.id.recordDeleteWrapper));

                } else if (state == PaymentRecordStateEnum.EDIT_ACTIVE) {

                    int d = ACTION_TAB_WIDTH + (delta + MIN_SWIPE_DISTANCE);
                    if (d > 0) actionButtonWidth = d;
                    Log.i("Ondra", "edit width: " + actionButtonWidth);
                    setActionButtonWidth(v.findViewById(R.id.recordEditWrapper));

                }

                return true;
            }

            private boolean doSwipeRight(int delta, View v) {
                //Log.i("Ondra", "swiping right");

                //if (!deleteActionVisible) return true;
                //if(state != PaymentRecordStateEnum.DELETE_ACTIVE) return true;

                if (actionBtnAnimating)
                    return false;

                if (state == PaymentRecordStateEnum.NORMAL) {

                    int d = Math.abs(Math.abs(delta) - MIN_SWIPE_DISTANCE);
                    if (d < ACTION_TAB_WIDTH) actionButtonWidth = d;
                    Log.i("Ondra", "edit width: " + actionButtonWidth);
                    setActionButtonWidth(v.findViewById(R.id.recordEditWrapper));

                } else if (state == PaymentRecordStateEnum.DELETE_ACTIVE) {

                    int d = ACTION_TAB_WIDTH - (delta - MIN_SWIPE_DISTANCE);
                    if (d > 0) actionButtonWidth = d;
                    Log.i("Ondra", "delete width: " + actionButtonWidth);
                    setActionButtonWidth(v.findViewById(R.id.recordDeleteWrapper));

                }

                return true;
            }


            private boolean onSwipeLeftFinished(View v) {
                Log.i("Ondra", "swipe left finished");

                //if (deleteActionVisible) return true;
                //if(state == PaymentRecordStateEnum.DELETE_ACTIVE) return true;

                if (actionBtnAnimating)
                    return false;

                if (state == PaymentRecordStateEnum.NORMAL) {

                    FrameLayout deleteWrapper = (FrameLayout) v.findViewById(R.id.recordDeleteWrapper);

                    if (deleteWrapper.getWidth() > ACTION_TAB_WIDTH / 2) {
                        animateActionButton(deleteWrapper, ACTION_TAB_WIDTH, PaymentRecordStateEnum.DELETE_ACTIVE);
                    } else {
                        animateActionButton(deleteWrapper, 0, PaymentRecordStateEnum.DELETE_ACTIVE);
                    }

                } else if (state == PaymentRecordStateEnum.EDIT_ACTIVE) {

                    FrameLayout editWrapper = (FrameLayout) v.findViewById(R.id.recordEditWrapper);
                    animateActionButton(editWrapper, 0, PaymentRecordStateEnum.EDIT_ACTIVE);

                }


                return true;
            }

            private boolean onSwipeRightFinished(View v) {
                Log.i("Ondra", "swipe right finished");

                //if (!deleteActionVisible) return true;
                //if(state != PaymentRecordStateEnum.DELETE_ACTIVE) return true;

                if (actionBtnAnimating)
                    return false;

                if (state == PaymentRecordStateEnum.NORMAL) {

                    FrameLayout editWrapper = (FrameLayout) v.findViewById(R.id.recordEditWrapper);

                    if (editWrapper.getWidth() > ACTION_TAB_WIDTH / 2) {
                        animateActionButton(editWrapper, ACTION_TAB_WIDTH, PaymentRecordStateEnum.EDIT_ACTIVE);
                    } else {
                        animateActionButton(editWrapper, 0, PaymentRecordStateEnum.EDIT_ACTIVE);
                    }

                } else if (state == PaymentRecordStateEnum.DELETE_ACTIVE) {

                    FrameLayout deleteWrapper = (FrameLayout) v.findViewById(R.id.recordDeleteWrapper);
                    animateActionButton(deleteWrapper, 0, PaymentRecordStateEnum.DELETE_ACTIVE);

                }

                return true;
            }
        });
    }

    public static void setDisplayWidth(int width){
        PaymentRecord.displayWidth = width;
    }

    public void hideEditActionButton(){
        FrameLayout editWrapper = (FrameLayout) this.findViewById(R.id.recordEditWrapper);
        animateActionButton(editWrapper, 0, PaymentRecordStateEnum.EDIT_ACTIVE);
    }

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
