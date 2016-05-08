package cz.ondrejpittl.semestralka.layout;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import cz.ondrejpittl.semestralka.HomeActivity;
import cz.ondrejpittl.semestralka.listeners.PaymentRecordTouchListener;
import cz.ondrejpittl.semestralka.R;
import cz.ondrejpittl.semestralka.factories.AnimationFactory;
import cz.ondrejpittl.semestralka.models.Payment;
import cz.ondrejpittl.semestralka.partial.PaymentRecordStateEnum;
import cz.ondrejpittl.semestralka.partial.SharedPrefs;

/**
 * Created by OndrejPittl on 08.04.16.
 */
public class PaymentRecord extends LinearLayout {

    /**
     * ID of payment record.
     */
    private int paymentId;

    /**
     * Payment record reference.
     */
    private Payment payment;


    private int paymentCollapsedHeight = 45;
    private int paymentUnCollapsedHeight = 65;



    /**
     * Flag indicating whether or not is payment record collapsed – whether or not is note visible.
     */
    private boolean collapsed;

    private static boolean iconVisible;

    /**
     * Height of collapsed payment record.
     */
    private int collapsedHeight;


    /**
     * Width of action button hidden under a payment record and visible while swiping.
     */
    public static int ACTION_TAB_WIDTH = 200;


    private PaymentRecordTouchListener touchListener;


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
        this.collapsedHeight = -1;

        ACTION_TAB_WIDTH = displayWidth / 6;

        this.touchListener = new PaymentRecordTouchListener(this);
        this.setOnTouchListener(this.touchListener);

        //this.getLayoutParams().height = this.getCollapsedRecHeight();
    }

    public void update(){
        this.updateCollapsed();
        this.setIconVisibility();
    }

    public void updateCollapsed(){
        if(SharedPrefs.isPaymentNoteDisplaySet() && !SharedPrefs.getPaymentNoteDisplay()) {
            this.collapsed = true;
        } else {
            this.collapsed = false;
        }
    }

    private void setIconVisibility(){
        PaymentRecord.iconVisible = SharedPrefs.isPaymentIconSet() && SharedPrefs.getPaymentIcons();
    }

    public void updateIconVisibility(){
        FrameLayout ico = (FrameLayout) findViewById(R.id.recordImgHolder);

        if(PaymentRecord.iconVisible) {
            ico.setVisibility(VISIBLE);
        } else {
            ico.setVisibility(GONE);
        }
    }

    public void updateRecordHeight(HomeActivity activity){
        int h;
        if(this.collapsed)
            h = getCollapsedRecHeight(activity);
        else
            h = getUnCollapsedRecHeight(activity);

        this.getLayoutParams().height = h;
        this.requestLayout();
    }

    /**
     * Sets button given via input param width.
     * @param w Width to be set.
     * @param v Button reference.
     */
    public void setActionButtonWidth(int w, View v) {
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
    public void hideAnimateActionBtn(FrameLayout actionBtn, PaymentRecordStateEnum recState) {
        AnimationFactory.animateActionButton(actionBtn, this, 0, recState);
    }

    /**
     * Triggers action button animation showing it.
     * @param actionBtn Animated action button reference.
     * @param recState  State of payment record.
     */
    public void showAnimateActionBtn(FrameLayout actionBtn, PaymentRecordStateEnum recState) {
        AnimationFactory.animateActionButton(actionBtn, this, ACTION_TAB_WIDTH, recState);
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
        AnimationFactory.animateActionButton(editWrapper, this, 0, PaymentRecordStateEnum.EDIT_ACTIVE);
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

    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    private int dpToPx(int dp, Activity activity) {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public int getCollapsedRecHeight(){
        /*int height = this.getHeight();

        //note height
        TextView tvNote = (TextView) findViewById(R.id.txtViewRecordNote);
        tvNote.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int noteHeight = tvNote.getMeasuredHeight() + 10;

        if(!this.isCollapsed()) {
            height -= noteHeight;
        }
        */
        return dpToPx(this.paymentCollapsedHeight);
    }

    public int getUncollapsedRecHeight(){
        /*int height = this.getHeight();

        //note height
        TextView tvNote = (TextView) findViewById(R.id.txtViewRecordNote);
        tvNote.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int noteHeight = tvNote.getMeasuredHeight() + 10;

        if(this.isCollapsed()) {
            height += noteHeight;
        }*/

        return dpToPx(this.paymentUnCollapsedHeight);
    }

    public int getCollapsedRecHeight(HomeActivity activity){
        return dpToPx(this.paymentCollapsedHeight, activity);
    }

    public int getUnCollapsedRecHeight(HomeActivity activity){
        return dpToPx(this.paymentUnCollapsedHeight, activity);
    }

    public void updateHeight(HomeActivity activity){

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

    public ImageButton getRecordDeleteBtn(){
        return (ImageButton) this.findViewById(R.id.btnRecordDelete);
    }

    public ImageButton getRecordEditBtn(){
        return (ImageButton) this.findViewById(R.id.btnRecordEdit);
    }

    public int getActionButtonWidth() {
        return this.touchListener.getActionButtonWidth();
    }

    public boolean isActionBtnAnimating() {
        return actionBtnAnimating;
    }

    public void setActionBtnAnimating(boolean actionBtnAnimating) {
        this.actionBtnAnimating = actionBtnAnimating;
    }

    public PaymentRecordStateEnum getState() {
        return state;
    }

    public void setState(PaymentRecordStateEnum state) {
        this.state = state;
    }

    public int getCollapsedHeight() {
        return collapsedHeight;
    }

    public void setCollapsedHeight(int collapsedHeight) {
        this.collapsedHeight = collapsedHeight;
    }

    public static boolean isIconVisible() {
        return iconVisible;
    }
}