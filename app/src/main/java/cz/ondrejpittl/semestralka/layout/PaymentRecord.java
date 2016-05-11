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
import cz.ondrejpittl.semestralka.partial.Designer;
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

    /**
     * Collapsed payment record height.
     */
    private static int paymentCollapsedHeight = 45;

    /**
     * UnCollapsed payment record height.
     */
    private static int paymentUnCollapsedHeight = 65;

    /**
     * Flag indicating whether or not is payment record collapsed – whether or not is note visible.
     */
    private boolean collapsed;

    /**
     * Category icon visibility flag.
     */
    private static boolean iconVisible;

    /**
     * Height of collapsed payment record.
     */
    private int collapsedHeight;

    /**
     * Width of action button hidden under a payment record and visible while swiping.
     */
    public static int ACTION_TAB_WIDTH = 200;

    /**
     * OnTouch listener.
     */
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


    /**
     * A constructor. Basics initialization.
     * @param context   an activity context reference
     */
    public PaymentRecord(Context context) {
        super(context);
        this.init();
    }

    /**
     * A constructor. Basics initialization.
     * @param context   an activity context reference
     * @param attrs     xml attributes
     */
    public PaymentRecord(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    /**
     * A constructor. Basics initialization.
     * @param context   an activity context reference
     * @param attrs     xml attributes
     * @param defStyleAttr  default xml styles
     */
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
    }

    /**
     * Updates a payment record.
     */
    public void update(){
        this.updateCollapsed();
        this.setIconVisibility();
    }

    /**
     * Updates a collapsed flag.
     */
    public void updateCollapsed(){
        if(SharedPrefs.isPaymentNoteDisplaySet() && !SharedPrefs.getPaymentNoteDisplay()) {
            this.collapsed = true;
        } else {
            this.collapsed = false;
        }
    }

    /**
     * Updates an icon visibility flag.
     */
    private void setIconVisibility(){
        PaymentRecord.iconVisible = SharedPrefs.isPaymentIconSet() && SharedPrefs.getPaymentIcons();
    }

    /**
     * Sets an icon visibility flag.
     */
    public void updateIconVisibility(){
        FrameLayout ico = (FrameLayout) findViewById(R.id.recordImgHolder);

        if(PaymentRecord.iconVisible) {
            ico.setVisibility(VISIBLE);
        } else {
            ico.setVisibility(GONE);
        }
    }

    /**
     * Updates a payment record height.
     * @param activity  an activity reference
     * @return          final height dependent on collapsed flag
     */
    public int updateRecordHeight(HomeActivity activity){
        int h;
        if(this.collapsed)
            h = getCollapsedRecHeight(activity);
        else
            h = getUnCollapsedRecHeight(activity);

        this.getLayoutParams().height = h;
        this.requestLayout();
        return h;
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

    /**
     * Getter of a collapsed payment record height.
     * @return  a collapsed payment record height
     */
    public int getCollapsedRecHeight(){
        return Designer.dpToPx(this.paymentCollapsedHeight, getContext());
    }

    /**
     * Getter of a uncollapsed payment record height.
     * @return  a uncollapsed payment record height
     */
    public int getUncollapsedRecHeight(){
        return Designer.dpToPx(this.paymentUnCollapsedHeight, getContext());
    }

    /**
     * Getter of a collapsed payment record height.
     * @return  a collapsed payment record height
     */
    public int getCollapsedRecHeight(HomeActivity activity){
        return Designer.dpToPx(this.paymentCollapsedHeight, activity);
    }

    /**
     * Getter of a uncollapsed payment record height.
     * @return  a uncollapsed payment record height
     */
    public int getUnCollapsedRecHeight(HomeActivity activity){
        return Designer.dpToPx(this.paymentUnCollapsedHeight, activity);
    }

    /**
     * Getter of a payment ID.
     * @return  payment ID
     */
    public int getPaymentId() {
        return this.payment.getID();
    }

    /**
     *  Setter of a payment.
     * @param p payment
     */
    public void setPayment(Payment p) {
        this.payment = p;
    }

    /**
     * Getter of a payment.
     * @return  payment
     */
    public Payment getPayment() {
        return this.payment;
    }

    /**
     * Getter of a collapsed flag
     * @return  a collapsed flag
     */
    public boolean isCollapsed() {
        return collapsed;
    }

    /**
     * Setter of a collapsed flag.
     * @param collapsed
     */
    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
    }

    /**
     * Getter of a record delete button.
     * @return  button
     */
    public ImageButton getRecordDeleteBtn(){
        return (ImageButton) this.findViewById(R.id.btnRecordDelete);
    }

    /**
     * Getter of a record edit button.
     * @return  button
     */
    public ImageButton getRecordEditBtn(){
        return (ImageButton) this.findViewById(R.id.btnRecordEdit);
    }

    /**
     * Getter of an action button width.
     * @return  width
     */
    public int getActionButtonWidth() {
        return this.touchListener.getActionButtonWidth();
    }

    /**
     * Getter of an actionBtnAnimating flag.
     * @return  flag
     */
    public boolean isActionBtnAnimating() {
        return actionBtnAnimating;
    }

    /**
     * Setter of an actionBtnAnimating flag.
     * @param actionBtnAnimating    flag
     */
    public void setActionBtnAnimating(boolean actionBtnAnimating) {
        this.actionBtnAnimating = actionBtnAnimating;
    }

    /**
     * Getter of a payment record state.
     * @return  state
     */
    public PaymentRecordStateEnum getState() {
        return state;
    }

    /**
     * Getter of a payment record state.
     * @param state state
     */
    public void setState(PaymentRecordStateEnum state) {
        this.state = state;
    }

    /**
     * Getter of a collapsed height in dp.
     * @return  a collapsed height in dp
     */
    public int getCollapsedHeight() {
        return collapsedHeight;
    }

    /**
     * Setter of a collapsed height in dp.
     * @param collapsedHeight   a collapsed height in dp
     */
    public void setCollapsedHeight(int collapsedHeight) {
        this.collapsedHeight = collapsedHeight;
    }
}