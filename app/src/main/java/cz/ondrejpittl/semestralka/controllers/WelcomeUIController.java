package cz.ondrejpittl.semestralka.controllers;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import cz.ondrejpittl.semestralka.R;
import cz.ondrejpittl.semestralka.WelcomeActivity;
import cz.ondrejpittl.semestralka.factories.ButtonFactory;
import cz.ondrejpittl.semestralka.layout.MyShadowTextView;
import cz.ondrejpittl.semestralka.layout.PinCodeFields;
import cz.ondrejpittl.semestralka.partial.SharedPrefs;

/**
 * Created by OndrejPittl on 30.03.16.
 */
public class WelcomeUIController {

    /**
     * Activity that is being controlled.
     */
    private WelcomeActivity activity;

    private PinCodeFields pinCodeFields;

    private boolean firstTimeLaunch;

    /**
     * Number of PIN input fields.
     */
    private int pinFieldsCount;

    /**
     * PIN input field layout elements represented with EditText.
     */
    private EditText[] pinFields;


    /**
     * Constructor, controller initialization.
     */
    public WelcomeUIController(WelcomeActivity activity){
        this.activity = activity;
        this.pinFieldsCount = 4;
        this.firstTimeLaunch = SharedPrefs.isFirstTimeLaunch();
        this.pinFields = new EditText[this.pinFieldsCount];

        //this.pinCodeFields = new PinCodeFields(this.activity);
    }

    /**
     * Displays Welcome screen for the first-time launch.
     */
    public void displayWelcomeScreen(){
        //showWelcomeDivider();
        showAboutButton();
        showContinueButton();
    }

    /**
     * Displays Login screen for the other-time launches.
     */
    public void displayLoginScreen(){

        //login screen first-time
        if(SharedPrefs.isFirstTimeLaunch()) {

            //user sets the PIN code
            this.firstTimeLaunch = true;
            setLoginScreenRegisterTexts();
            showRegisterButton();

        } else {

            //user enters PIN code
            setLoginScreenTexts();
            showEnterButton();

        }

        //hideWelcomeDivider();
        showPinCodeFields();
        //setPinCodeListeners();
        this.pinCodeFields.setPinCodeListeners();
    }



    /**
     * Changes Login screen texts of layout elements.
     */
    private void setLoginScreenRegisterTexts(){
        MyShadowTextView hello = (MyShadowTextView) this.activity.findViewById(R.id.txtView_hello);
        hello.setText(this.activity.getString(R.string.loginSettingUp));

        MyShadowTextView msg = (MyShadowTextView) this.activity.findViewById(R.id.txtView_firstTime);
        msg.setText(this.activity.getString(R.string.loginLetsStart));
    }

    /**
     * Changes Login screen texts of layout elements.
     */
    private void setLoginScreenTexts(){
        MyShadowTextView greetings = (MyShadowTextView) this.activity.findViewById(R.id.txtView_hello),
                msg = (MyShadowTextView) this.activity.findViewById(R.id.txtView_firstTime);

        greetings.setTextSize(30);

        if(firstTimeLaunch) {
            greetings.setText(this.activity.getString(R.string.loginExcellent));
            msg.setText(this.activity.getString(R.string.loginShopping));
        } else {
            greetings.setText(this.activity.getString(R.string.loginWelcomeBack));
            msg.setText(this.activity.getString(R.string.loginOtherTime));
        }


    }

    /**
     * Shows PIN layout input fields for user PIN input.
     */
    private void showPinCodeFields(){
        hideUpperLayoutControls();

        FrameLayout upperControlWrapper = (FrameLayout) this.activity.findViewById(R.id.welcomeUpperControlWrapper);

        //if(upperControlWrapper == null) return;

        LayoutInflater layoutInflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.pinCodeFields = (PinCodeFields) layoutInflater.inflate(R.layout.pin_code, (ViewGroup) this.activity.findViewById(R.id.pinCodeFieldsWrapper));
        upperControlWrapper.addView(this.pinCodeFields);
    }

    private void showWelcomeDivider(){
        this.activity.findViewById(R.id.viewLine).setVisibility(View.VISIBLE);
    }

    private void hideWelcomeDivider(){
        this.activity.findViewById(R.id.viewLine).setVisibility(View.GONE);
    }


    /**
     * Displays About button.
     */
    private void showAboutButton(){
        hideUpperLayoutControls();


        FrameLayout container = (FrameLayout) this.activity.findViewById(R.id.welcomeUpperControlWrapper);


        //button creation
        MyShadowTextView aboutBtn = ButtonFactory.createMyShadowTxtView (
                this.activity.getString(R.string.loginTellMe),
                15,
                ContextCompat.getColor(this.activity.getApplicationContext(), R.color.appTextLightGold),
                new int[]{0, 0, 0, 15},
                new View.OnClickListener() {
                    public void onClick(View v) {
                        activity.startHelpActivity(v);
                    }
                },
                this.activity.getApplicationContext()
        );

        container.addView(aboutBtn);
    }

    /**
     * Displays Continue button.
     */
    private void showContinueButton(){
        hideLowerLayoutControls();

        FrameLayout container = (FrameLayout) this.activity.findViewById(R.id.welcomeLowerControlWrapper);

        //button creation
        MyShadowTextView continueBtn = ButtonFactory.createMyShadowTxtView (
                this.activity.getString(R.string.loginContinue),
                ContextCompat.getColor(this.activity.getApplicationContext(), R.color.appTextLightGold),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        displayLoginScreen();
                    }
                },
                this.activity.getApplicationContext()
        );

        container.addView(continueBtn);
    }

    /**
     * Hides useless layout elements from lower control part of screen/activity.
     */
    private void hideLowerLayoutControls(){
        FrameLayout container = (FrameLayout) this.activity.findViewById(R.id.welcomeLowerControlWrapper);
        container.removeAllViews();
    }

    /**
     * Hides useless layout elements from upper control part of screen/activity.
     */
    private void hideUpperLayoutControls(){
        FrameLayout container = (FrameLayout) this.activity.findViewById(R.id.welcomeUpperControlWrapper);
        container.removeAllViews();
    }

    /**
     * Shows Enter button.
     */
    private void showEnterButton(){
        hideLowerLayoutControls();


        FrameLayout container = (FrameLayout) this.activity.findViewById(R.id.welcomeLowerControlWrapper);

        MyShadowTextView enterBtn = ButtonFactory.createMyShadowTxtView (
                this.activity.getString(R.string.loginEnter),
                ContextCompat.getColor(this.activity.getApplicationContext(), R.color.appTextLightGold),
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if(pinCodeFields.checkPINCode()) {
                            activity.startHomeActivity();
                        }
                    }
                },
                this.activity.getApplicationContext()
        );

        container.addView(enterBtn);
    }

    /**
     * Shows Enter button.
     */
    private void showRegisterButton(){
        hideLowerLayoutControls();

        FrameLayout container = (FrameLayout) this.activity.findViewById(R.id.welcomeLowerControlWrapper);

        MyShadowTextView regBtn = ButtonFactory.createMyShadowTxtView (
                this.activity.getString(R.string.loginLogin),
                ContextCompat.getColor(this.activity.getApplicationContext(), R.color.appTextLightGold),
                new View.OnClickListener() {
                    public void onClick(View v) {
                        pinCodeFields.storePINCode();
                        displayLoginScreen();
                    }
                },
                this.activity.getApplicationContext()
        );

        container.addView(regBtn);
    }

}