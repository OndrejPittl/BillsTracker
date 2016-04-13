package cz.ondrejpittl.semestralka.layout;

import android.content.Context;
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

/**
 * Created by OndrejPittl on 08.04.16.
 */
public class PaymentRecord extends LinearLayout {

    private HomeDataController contorller;


    private int paymentId;

    private boolean collapsed;

    private int collapsedHeight;


    private final int deleteExpandedWidth = 200;

    private int deleteWrapperWidth = 0;

    private boolean deleteWrapperExpanded = false;

    private int padding = 0;



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

        this.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                collapseToggle(v);
            }
        });


        this.setOnTouchListener(new OnTouchListener() {

            private final int MIN_SWIPE_DISTANCE = 100;

            boolean swiping = false;

            int start = 0,
                    end = 0,
                    current = 0,
                    delta = 0;

            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getAction()) {

                    case MotionEvent.ACTION_MOVE:
                        current = (int) event.getRawX();
                        delta = current - start;

                        Log.i("Ondra", "delta: " + delta);

                        if (/*swiping ||*/Math.abs(delta) > MIN_SWIPE_DISTANCE) {
                            swiping = true;

                            if (delta < 0) {
                                doSwipeLeft(delta, view);
                            } else {
                                doSwipeRight(delta, view);
                            }
                        }


                        //params.rightMargin = (int) event.getRawX() - (view.getWidth() / 2);
                        //view.setLayoutParams(params);
                        break;

                    case MotionEvent.ACTION_UP:
                        swiping = false;

                        end = (int) event.getRawX();
                        delta = end - start;

                        Log.i("Ondra", "X-start: " + start);
                        Log.i("Ondra", "X-end: " + end);
                        Log.i("Ondra", "delta: " + delta);

                        if (Math.abs(delta) < MIN_SWIPE_DISTANCE) {
                            onTap(view);
                        } else {
                            if (delta < 0) {
                                onSwipeLeftFinished(view);
                            } else {
                                onSwipeRightFinished(view);

                            }

                        }

                        //params.rightMargin = (int) event.getRawX() - (view.getWidth() / 2);
                        //view.setLayoutParams(params);
                        break;

                    case MotionEvent.ACTION_DOWN:
                        start = (int) event.getRawX();
                        //Log.i("Ondra", "X: " + start);

                        //view.setLayoutParams(params);
                        break;
                }

                return true;
            }

            private boolean onTap(View v) {
                Log.i("Ondra", "tap");
                collapseToggle(v);
                return true;
            }

            private void setDeleteWrapperWidth(View v) {
                FrameLayout deleteWrapper = (FrameLayout) v.findViewById(R.id.recordDeleteWrapper);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) deleteWrapper.getLayoutParams();

                params.width = deleteWrapperWidth;
                deleteWrapper.setLayoutParams(params);
                deleteWrapper.requestLayout();
            }

            private boolean doSwipeLeft(int delta, View v) {
                Log.i("Ondra", "swiping left");

                if (deleteWrapperExpanded) return true;

                int d = Math.abs(Math.abs(delta) - MIN_SWIPE_DISTANCE);
                if (d < deleteExpandedWidth) deleteWrapperWidth = d;
                Log.i("Ondra", "width: " + deleteWrapperWidth);

                setDeleteWrapperWidth(v);

                return true;
            }

            private boolean doSwipeRight(int delta, View v) {
                Log.i("Ondra", "swiping right");

                if (!deleteWrapperExpanded) return true;

                int d = deleteExpandedWidth - (delta - MIN_SWIPE_DISTANCE);
                if (d > 0) deleteWrapperWidth = d;

                Log.i("Ondra", "width: " + deleteWrapperWidth);

                setDeleteWrapperWidth(v);

                return true;
            }


            private boolean onSwipeLeftFinished(View v) {
                //Log.i("Ondra", "swipe left");

                if (deleteWrapperExpanded) return true;

                FrameLayout deleteWrapper = (FrameLayout) v.findViewById(R.id.recordDeleteWrapper);


                if (deleteWrapper.getWidth() > deleteExpandedWidth / 2) {
                    animateDeleteWrapper(deleteWrapper, deleteExpandedWidth);
                } else {
                    animateDeleteWrapper(deleteWrapper, 0);
                }


                return true;
            }

            private boolean onSwipeRightFinished(View v) {
                Log.i("Ondra", "swipe right");

                if (!deleteWrapperExpanded) return true;

                FrameLayout deleteWrapper = (FrameLayout) v.findViewById(R.id.recordDeleteWrapper);
                animateDeleteWrapper(deleteWrapper, 0);

                return true;
            }



            /*public boolean onTouch(View v, MotionEvent event) {

                float startPoint = 0,
                      currentPoint = 0,
                      previouspoint = 0;

                Log.i("Ondra", "MOVING!!!!!!!!!!");

                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startPoint = event.getX();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        currentPoint = event.getX();

                        if(currentPoint < startPoint)
                            v.setPadding(0, 0, (int)currentPoint, 0);
                            v.requestLayout();
                        break;

                    case MotionEvent.ACTION_CANCEL:
                        previouspoint=event.getX();
                        if(previouspoint > startPoint){
                            //Right side swipe
                        }else{
                            // Left side swipe
                        }
                        break;
                }

                return false;
            }*/
        });
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
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


    private void animateDeleteWrapper(final View v, final int to) {
        final int from, diff;

        //base height
        from = deleteWrapperWidth;
        diff = Math.abs(to - from);

        Animation a = new Animation() {
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                float interTime;
                int base;

                if(to < from) {
                    interTime = (1 - interpolatedTime);
                    base = 0;
                } else {
                    interTime = interpolatedTime;
                    base = from;
                }

                Log.i("Ondra", "inter-time: " + interTime);
                Log.i("Ondra", "w: " + (int)(diff * interTime));

                v.getLayoutParams().width = interpolatedTime == 1 ? to : (int)(base + diff * interTime);
                v.requestLayout();
            }
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        //a.setDuration((int) (deleteExpandedWidth / v.getContext().getResources().getDisplayMetrics().density));

        a.setAnimationListener(new Animation.AnimationListener(){
            public void onAnimationStart(Animation arg0) {}
            public void onAnimationRepeat(Animation arg0) {}
            public void onAnimationEnd(Animation arg0) {
                if(to < from) {
                    deleteWrapperExpanded = false;
                } else {
                    deleteWrapperExpanded = true;
                }
            }
        });

        a.setDuration(700);
        v.startAnimation(a);
    }
}
