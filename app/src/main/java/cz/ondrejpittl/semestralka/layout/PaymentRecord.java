package cz.ondrejpittl.semestralka.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import cz.ondrejpittl.semestralka.R;

/**
 * Created by OndrejPittl on 08.04.16.
 */
public class PaymentRecord extends LinearLayout {


    private int paymentId;

    private boolean collapsed;

    private int collapsedHeight;

    private int expandedHeight;

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
        /*this.collapsedHeight = -1;
        this.expandedHeight = -1;*/

        this.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                collapseToggle(v);
            }
        });


        this.setOnTouchListener(new OnTouchListener() {

            private final int MIN_SWIPE_DISTANCE = 100;
            private final int MIN_SWIPE_VELOCITY = 200;


            int start = 0,
                end = 0,
                delta = 0;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();



                switch (event.getAction()) {

                    case MotionEvent.ACTION_MOVE:
                        //params.rightMargin = (int) event.getRawX() - (view.getWidth() / 2);
                        //view.setLayoutParams(params);
                        break;

                    case MotionEvent.ACTION_UP:
                        end = (int) event.getRawX();
                        delta = end - start;

                        Log.i("Ondra", "X-start: " + start);
                        Log.i("Ondra", "X-end: " + end);
                        Log.i("Ondra", "delta: " + delta);

                        if(Math.abs(delta) < MIN_SWIPE_DISTANCE) {
                            onTap(view);
                        } else {
                            if(delta < 0) {
                                onSwipeLeft();
                            } else {
                                onSwipeRight();

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

            private boolean onTap(View v){
                Log.i("Ondra", "tap");
                collapseToggle(v);
                return true;
            }

            private boolean onSwipeLeft(){
                Log.i("Ondra", "swipe left");
                return true;
            }

            private boolean onSwipeRight(){
                Log.i("Ondra", "swipe right");
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

    private void collapseToggle(final View v) {
        final int from, to, diff;

        //note height
        TextView tvNote = (TextView) v.findViewById(R.id.txtViewRecordNote);
        tvNote.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final int noteHeight = tvNote.getMeasuredHeight() + 10;

        //base height
        v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight() - 5;

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
}
