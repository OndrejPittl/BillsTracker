package cz.ondrejpittl.semestralka.factories;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cz.ondrejpittl.semestralka.R;
import cz.ondrejpittl.semestralka.layout.PaymentRecord;
import cz.ondrejpittl.semestralka.partial.PaymentRecordStateEnum;
import cz.ondrejpittl.semestralka.partial.PaymentRecordsAnimation;
import cz.ondrejpittl.semestralka.partial.SharedPrefs;

/**
 * Created by OndrejPittl on 15.04.16.
 */
public class AnimationFactory {



    public static void fadeInPaymentRecords(final ArrayList<PaymentRecord> records){
        boolean animationAllowed = SharedPrefs.isPaymentAnimationSet() && SharedPrefs.getPaymentAnimation();

        int y = 0;
        for (final PaymentRecord rec : records) {
            final int i = y++;

            if(!animationAllowed) {
                rec.setAlpha(1f);
                continue;
            }

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    ObjectAnimator anim = ObjectAnimator.ofFloat(rec, "alpha", 0f, 1f);
                    anim.setDuration(300);
                    anim.start();
                }
            }, i * 150);
        }
    }

    public static void fadeOutPaymentRecords(final ArrayList<PaymentRecord> records){
        if(records == null || records.size() <= 0)
            return;

        boolean animationAllowed = SharedPrefs.isPaymentAnimationSet() && SharedPrefs.getPaymentAnimation();

        for (final PaymentRecord rec : records) {

            if(!animationAllowed) {
                rec.setAlpha(0f);
                continue;
            }

            Animation fadeout = new AlphaAnimation(1.f, 0.f);
            fadeout.setDuration(250);
            rec.startAnimation(fadeout);


        }
    }



    /**
     * Function animating edit/delete action button.
     * @param actionButton         Action button being animated.
     * @param to        Final action button width-state.
     * @param recState  Payment record state.
     */
    public static void animateActionButton(final FrameLayout actionButton, final PaymentRecord rec, final int to, final PaymentRecordStateEnum recState) {
        final int from, diff;

        rec.setActionBtnAnimating(true);
        //actionBtnAnimating = true;

        //from = actionButtonWidth;
        from = rec.getActionButtonWidth();
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

                actionButton.getLayoutParams().width = realWidth;
                actionButton.requestLayout();

                Log.i("Ondra", "nastaveno: " + actionButton.getLayoutParams().width);


            }
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setAnimationListener(new Animation.AnimationListener(){
            public void onAnimationStart(Animation arg0) {}
            public void onAnimationRepeat(Animation arg0) {}
            public void onAnimationEnd(Animation arg0) {

                if(to < from) {

                    //hiding action button
                    rec.setState(PaymentRecordStateEnum.NORMAL);

                } else {

                    //showing action button
                    rec.setState(recState);

                }

                //actionBtnAnimating = false;
                rec.setActionBtnAnimating(false);


                /*if(recState == PaymentRecordStateEnum.DELETE_ACTIVE) {

                    if(to < from) {
                        //deleteActionVisible = false;
                        //state = PaymentRecordStateEnum.NORMAL;
                        rec.setState(PaymentRecordStateEnum.NORMAL);

                    } else {
                        //deleteActionVisible = true;
                        //state = PaymentRecordStateEnum.DELETE_ACTIVE;
                        rec.setState(PaymentRecordStateEnum.DELETE_ACTIVE);
                    }

                } else if(recState == PaymentRecordStateEnum.EDIT_ACTIVE) {

                    if(to < from) {
                        //state = PaymentRecordStateEnum.NORMAL;
                        rec.setState(PaymentRecordStateEnum.NORMAL);
                    } else {
                        //state = PaymentRecordStateEnum.EDIT_ACTIVE;
                        rec.setState(PaymentRecordStateEnum.EDIT_ACTIVE);
                    }

                }*/
            }
        });

        a.setDuration(300);
        actionButton.startAnimation(a);
    }


    /**
     * Note collapse animation handler.
     * @param v View being collapsed.
     */
    public static void animateCollapseToggle(final View v, final PaymentRecord rec) {
        final int from, to, diff;

        if(rec.getCollapsedHeight() < 0)
            rec.setCollapsedHeight(v.getHeight() - 5);

        /*//note height
        TextView tvNote = (TextView) v.findViewById(R.id.txtViewRecordNote);
        tvNote.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int noteHeight = tvNote.getMeasuredHeight() + 10;*/

        //base height
        final int targetHeight = rec.getCollapsedHeight();

        if(rec.isCollapsed()) {
            from = rec.getCollapsedRecHeight();
            to = rec.getUncollapsedRecHeight();
            diff = to - from;
            rec.setCollapsed(false);
        } else {
            to = rec.getCollapsedRecHeight();
            from = rec.getUncollapsedRecHeight();
            diff = to - from;
            rec.setCollapsed(true);
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

        boolean animationAllowed = SharedPrefs.isPaymentAnimationSet() && SharedPrefs.getPaymentAnimation();
        int dur = (int) (to / v.getContext().getResources().getDisplayMetrics().density);

        if(!animationAllowed) {
            dur = 1;
        }

        a.setDuration(dur);
        a.setFillEnabled(true);
        a.setFillAfter(true);
        v.startAnimation(a);
    }











}
