package cz.ondrejpittl.semestralka.partial;

import android.animation.ObjectAnimator;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import cz.ondrejpittl.semestralka.HomeActivity;
import cz.ondrejpittl.semestralka.R;
import cz.ondrejpittl.semestralka.factories.AnimationFactory;

/**
 * Created by OndrejPittl on 02.05.16.
 */
public class TutorialManager {

    interface TutorialStep {
        void start();
    }

    private HomeActivity activity;

    private View emptyOverlay;

    private View[] lastViews = null;

    private boolean animating;


    //overlay1
    private View controlsPaymentListOverlay;
    private View controlsButtonsOverlay;
    private View controlsClearOverlay;

    //overlay2
    private View paymentsControlsOverlay;
    private View paymentsPaymentOverlay;
    private View paymentsStatisticsOverlay;
    private View paymentsClearOverlay;

    //overlay3
    private View statisticsClearOverlay;

    private int step;

    private TutorialStep[] steps = {
        new TutorialStep() { public void start() { explainControls(); }},
        new TutorialStep() { public void start() { explainActivityButtons(); }},
        new TutorialStep() { public void start() { explainPaymentList(); }},
        new TutorialStep() { public void start() { explainPayment(); }},
        new TutorialStep() { public void start() { explainStats(); }},
        new TutorialStep() {public void start() { hideAll(); }}
    };


    public TutorialManager(HomeActivity activity) {
        this.activity = activity;
        this.init();
        this.setListeners();
    }

    private void init(){
        this.step = 0;
        this.animating = false;

        this.emptyOverlay = this.activity.findViewById(R.id.overlayEmpty);

        this.controlsPaymentListOverlay = this.activity.findViewById(R.id.overlay1Light);
        this.controlsButtonsOverlay = this.activity.findViewById(R.id.overlay1Light2);
        this.controlsClearOverlay = this.activity.findViewById(R.id.overlay1Light3);

        this.paymentsControlsOverlay = this.activity.findViewById(R.id.overlay2Light);
        this.paymentsStatisticsOverlay = this.activity.findViewById(R.id.overlay2Light2);
        this.paymentsPaymentOverlay = this.activity.findViewById(R.id.overlay2Light3);
        this.paymentsClearOverlay = this.activity.findViewById(R.id.overlay2Light4);

        this.statisticsClearOverlay = this.activity.findViewById(R.id.overlay3Light);
    }

    private void setListeners(){
        this.emptyOverlay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!animating)
                    goNext();
            }
        });
    }

    private void goNext(){
        this.animating = true;
        this.steps[this.step++].start();
    }

    public void start(){
        this.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        this.emptyOverlay.setVisibility(View.VISIBLE);
        this.goNext();
    }

    private int getAnimationDelay(){
        int delay = 0;

        if(SharedPrefs.isPaymentAnimationSet())
            delay = 500;

        return delay;
    }

    public void explainControls(){
        this.hideVisible();

        View[] overlays = new View[]{
            paymentsControlsOverlay,
            statisticsClearOverlay
        };

        this.animateIn(overlays);
    }

    public void explainActivityButtons(){
        this.hideVisible();

        View[] overlays = new View[]{
            controlsButtonsOverlay,
            paymentsClearOverlay,
            statisticsClearOverlay,
        };

        this.animateIn(overlays);
    }

    public void explainPaymentList(){
        this.hideVisible();

        View[] overlays = new View[]{
            controlsPaymentListOverlay,
            statisticsClearOverlay
        };

        this.animateIn(overlays);
    }

    public void explainPayment(){
        this.hideVisible();

        View[] overlays = new View[]{
            controlsClearOverlay,
            paymentsPaymentOverlay,
            statisticsClearOverlay
        };

        this.animateIn(overlays);
    }

    public void explainStats(){
        this.hideVisible();

        View[] overlays = new View[]{
            controlsClearOverlay,
            paymentsStatisticsOverlay
        };

        this.animateIn(overlays);
    }

    private void animateIn(View[] overlays){
        this.resetAlpha(overlays, this.getAnimationDelay());
        this.show(overlays, this.getAnimationDelay() + 200);
        this.fadeIn(overlays, getAnimationDelay() + 300);
        this.lastViews = overlays;
    }

    private void fadeOut(View[] overlays, int delay){
        for (View v : overlays) {
            Log.i("Ondra-tut", "fading out: " + v);
            AnimationFactory.fadeOut(v, delay);
        }
    }

    private void fadeIn(View[] overlays, int delay){
        for (View v : overlays) {
            Log.i("Ondra-tut", "fading in: " + v);
            AnimationFactory.fadeIn(v, delay);
        }

        new Handler().postDelayed(new Runnable() {
            public void run() {
                animating = false;
            }
        }, delay + getAnimationDelay() + 300);
    }

    private void hide(final View[] overlays, int delay){
        new Handler().postDelayed(new Runnable() {
            public void run() {
                for (View v : overlays) {
                    if(v.getVisibility() == View.VISIBLE)
                        v.setVisibility(View.GONE);
                }
            }
        }, delay);
    }

    private void show(final View[] overlays, int delay){
        new Handler().postDelayed(new Runnable() {
            public void run() {
                for (View v : overlays) {
                    v.setVisibility(View.VISIBLE);
                }
            }
        }, delay);

    }

    private void hideAll(){
        this.step = 0;
        this.emptyOverlay.setVisibility(View.GONE);
        hideVisible();

        SharedPrefs.storeTutorialDisplayed(true);
        this.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    private void hideVisible(){
        Log.i("Ondra-tut", "hiding visible");
        if(this.lastViews == null) return;

        this.fadeOut(this.lastViews, 0);
        this.hide(this.lastViews, getAnimationDelay());
    }

    private void resetAlpha(final View[] overlays, int delay){
        new Handler().postDelayed(new Runnable() {
            public void run() {
                for (View v : overlays) {
                    v.setAlpha(0f);
                }
            }
        }, delay);
    }
}
