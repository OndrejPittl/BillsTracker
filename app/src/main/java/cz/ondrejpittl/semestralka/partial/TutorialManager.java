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

    /**
     * Interface of a single tutorial step.
     */
    interface TutorialStep {
        void start();
    }

    /**
     * Activity reference.
     */
    private HomeActivity activity;

    /**
     * Empty overlay view catches the click/tap events.
     */
    private View emptyOverlay;

    /**
     * A collection of previously displayed views.
     */
    private View[] lastViews = null;

    /**
     * A flag indicating whether is a steb being animated or not.
     */
    private boolean animating;


    /**
     * Controls overlay – payment list is viewed.
     */
    private View controlsPaymentListOverlay;

    /**
     * Controls overlay – buttons are viewed.
     */
    private View controlsButtonsOverlay;

    /**
     * Controls overlay – full-size dark overlay.
     */
    private View controlsClearOverlay;


    /**
     * Payments overlay – controls are viewed.
     */
    private View paymentsControlsOverlay;

    /**
     * Payments overlay – payment is viewed.
     */
    private View paymentsPaymentOverlay;

    /**
     * Payments overlay – stats are viewed.
     */
    private View paymentsStatisticsOverlay;

    /**
     * Payments overlay – full-size dark overlay.
     */
    private View paymentsClearOverlay;

    /**
     * Statistics overlay – full-size dark overlay.
     */
    private View statisticsClearOverlay;

    /**
     * Current step of a tutorial.
     */
    private int step;

    /**
     * Steps of a tutorial.
     */
    private TutorialStep[] steps = {
        new TutorialStep() { public void start() { explainControls(); }},
        new TutorialStep() { public void start() { explainActivityButtons(); }},
        new TutorialStep() { public void start() { explainPaymentList(); }},
        new TutorialStep() { public void start() { explainPayment(); }},
        new TutorialStep() { public void start() { explainStats(); }},
        new TutorialStep() {public void start() { hideAll(); }}
    };

    /**
     *  Consructor. Basics initialization.
     * @param activity  acrivity reference
     */
    public TutorialManager(HomeActivity activity) {
        this.activity = activity;
        this.init();
        this.setListeners();
    }

    /**
     * Tutorial manager initialization.
     */
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

    /**
     * Sets click handler on an empty overlaying view catching events.
     */
    private void setListeners(){
        this.emptyOverlay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!animating)
                    goNext();
            }
        });
    }

    /**
     * Go next tutorial step.
     */
    private void goNext(){
        this.animating = true;
        this.steps[this.step++].start();
    }

    /**
     * Start a tutorial.
     */
    public void start(){
        this.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        this.emptyOverlay.setVisibility(View.VISIBLE);
        this.goNext();
    }

    /**
     * Getter of an animation delay.
     * @return  animation delay
     */
    private int getAnimationDelay(){
        int delay = 0;

        if(SharedPrefs.isPaymentAnimationSet())
            delay = 500;

        return delay;
    }

    /**
     * Controls explaining tutorial step.
     */
    public void explainControls(){
        this.hideVisible();

        View[] overlays = new View[]{
            paymentsControlsOverlay,
            statisticsClearOverlay
        };

        this.animateIn(overlays);
    }

    /**
     * Activity buttons explaining tutorial step.
     */
    public void explainActivityButtons(){
        this.hideVisible();

        View[] overlays = new View[]{
            controlsButtonsOverlay,
            paymentsClearOverlay,
            statisticsClearOverlay,
        };

        this.animateIn(overlays);
    }

    /**
     * Payment list explaining tutorial step.
     */
    public void explainPaymentList(){
        this.hideVisible();

        View[] overlays = new View[]{
            controlsPaymentListOverlay,
            statisticsClearOverlay
        };

        this.animateIn(overlays);
    }

    /**
     * Payment explaining tutorial step.
     */
    public void explainPayment(){
        this.hideVisible();

        View[] overlays = new View[]{
            controlsClearOverlay,
            paymentsPaymentOverlay,
            statisticsClearOverlay
        };

        this.animateIn(overlays);
    }

    /**
     * Compact stats explaining tutorial step.
     */
    public void explainStats(){
        this.hideVisible();

        View[] overlays = new View[]{
            controlsClearOverlay,
            paymentsStatisticsOverlay
        };

        this.animateIn(overlays);
    }

    /**
     * Intro animation of all views given.
     * @param overlays  views to be animated
     */
    private void animateIn(View[] overlays){
        this.resetAlpha(overlays, this.getAnimationDelay());
        this.show(overlays, this.getAnimationDelay() + 200);
        this.fadeIn(overlays, getAnimationDelay() + 300);
        this.lastViews = overlays;
    }

    /**
     * Fades out all overlays given after a delay.
     * @param overlays  views given
     * @param delay     animation delay
     */
    private void fadeOut(View[] overlays, int delay){
        for (View v : overlays) {
            AnimationFactory.fadeOut(v, delay);
        }
    }

    /**
     * Fades in all overlays given after a delay.
     * @param overlays  views given
     * @param delay     animation delay
     */
    private void fadeIn(View[] overlays, int delay){
        for (View v : overlays) {
            AnimationFactory.fadeIn(v, delay);
        }

        new Handler().postDelayed(new Runnable() {
            public void run() {
                animating = false;
            }
        }, delay + getAnimationDelay() + 300);
    }

    /**
     * Hides all overlays given after a delay.
     * @param overlays  views given
     * @param delay     animation delay
     */
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

    /**
     * Shows all overlays given after a delay.
     * @param overlays  views given
     * @param delay     animation delay
     */
    private void show(final View[] overlays, int delay){
        new Handler().postDelayed(new Runnable() {
            public void run() {
                for (View v : overlays) {
                    v.setVisibility(View.VISIBLE);
                }
            }
        }, delay);

    }

    /**
     * Hides all overlays immediately.
     */
    private void hideAll(){
        this.step = 0;
        this.emptyOverlay.setVisibility(View.GONE);
        hideVisible();

        SharedPrefs.storeTutorialDisplayed(true);
        this.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    /**
     * Fades out last visible collection of views.
     */
    private void hideVisible(){
        if(this.lastViews == null) return;

        this.fadeOut(this.lastViews, 0);
        this.hide(this.lastViews, getAnimationDelay());
    }

    /**
     * Resets alpha of overlays given after a delay.
     * @param overlays  views given
     * @param delay     animation delay
     */
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
