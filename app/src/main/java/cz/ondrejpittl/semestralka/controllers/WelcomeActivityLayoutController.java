package cz.ondrejpittl.semestralka.controllers;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import cz.ondrejpittl.semestralka.R;
import cz.ondrejpittl.semestralka.WelcomeActivity;
import cz.ondrejpittl.semestralka.factories.ButtonFactory;
import cz.ondrejpittl.semestralka.partial.MyShadowTextView;

/**
 * Created by OndrejPittl on 30.03.16.
 */
public class WelcomeActivityLayoutController {

    /**
     * Activity that is being controlled.
     */
    private WelcomeActivity activity;

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
    public WelcomeActivityLayoutController(WelcomeActivity activity){
        this.activity = activity;
        this.pinFieldsCount = 4;
        this.pinFields = new EditText[this.pinFieldsCount];
    }

    /**
     * Displays Welcome screen for the first-time launch.
     */
    public void displayWelcomeScreen(){
        showAboutButton();
        showContinueButton();
    }

    /**
     * Displays Login screen for the other-time launches.
     */
    public void displayLoginScreen(){

        //login screen first-time
        if(this.activity.isFirstTimeLaunch()) {

            //user sets the PIN code
            setLoginScreenRegisterTexts();
            showRegisterButton();

        } else {

            //user enters PIN code
            setLoginScreenTexts();
            showEnterButton();

        }

        showPinCodeFields();
        setPinCodeListeners();
    }



    /**
     * Changes Login screen texts of layout elements.
     */
    private void setLoginScreenRegisterTexts(){
        MyShadowTextView msg = (MyShadowTextView) this.activity.findViewById(R.id.txtView_firstTime);
        msg.setText(this.activity.getString(R.string.loginPleaseLogin));
    }

    /**
     * Changes Login screen texts of layout elements.
     */
    private void setLoginScreenTexts(){
        MyShadowTextView greetings = (MyShadowTextView) this.activity.findViewById(R.id.txtView_hello),
                msg = (MyShadowTextView) this.activity.findViewById(R.id.txtView_firstTime);

        greetings.setText(this.activity.getString(R.string.loginWelcomeBack));
        greetings.setTextSize(30);

        msg.setText(this.activity.getString(R.string.loginOtherTime));
    }

    /**
     * Shows PIN layout input fields for user PIN input.
     */
    private void showPinCodeFields(){
        hideUpperLayoutControls();

        FrameLayout upperControlWrapper = (FrameLayout) this.activity.findViewById(R.id.welcomeUpperControlWrapper);

        //if(upperControlWrapper == null) return;

        LayoutInflater layoutInflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        upperControlWrapper.addView(layoutInflater.inflate(R.layout.pin_code, (ViewGroup) this.activity.findViewById(R.id.pinCodeFieldsWrapper)));
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
                        String stored = activity.getPINCode();
                        if(stored.equals(collectPINDigits())) {
                            Toast.makeText(activity.getApplicationContext(), "PASSED!", Toast.LENGTH_SHORT).show();
                            activity.startHomeActivity();
                        } else {
                            Toast.makeText(activity.getApplicationContext(), "ERR!", Toast.LENGTH_SHORT).show();
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
                        //store PIN code
                        activity.storePINCode(collectPINDigits());
                        activity.registerLoginScreenDisplayed();
                        displayLoginScreen();
                    }
                },
                this.activity.getApplicationContext()
        );

        container.addView(regBtn);
    }

    private String collectPINDigits(){
        String output = "";

        for(int i = 0; i <this.pinFieldsCount; i++) {
            output += this.pinFields[i].getText();
        }

        return output;
    }

    /**
     * Adds all PIN input field listeners.
     */
    private void setPinCodeListeners(){

        //iteration through all PIN layout elements
        for (int i = 0; i < 4; i++) {
            //Toast.makeText(this.activity.getApplicationContext(), "xxx", Toast.LENGTH_SHORT).show();

            //id of element
            int resID = this.activity.getResources().getIdentifier("edTxt_pin" + (i+1), "id", this.activity.getPackageName());
            this.pinFields[i] = (EditText) this.activity.findViewById(resID);
            this.pinFields[i].setTag(i + 1);

            //text change listener
            this.setPinCodeTextChangeListener(this.pinFields[i]);
        }
    }

    /**
     * Handles requesting focus of next element after entering a value into a PIN input field.
     *
     * @param field Field its text is being changed.
     */
    private void setPinCodeTextChangeListener(final EditText field){
        field.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {

                //field EMPTY -> nothing to do
                if (s.toString().length() <= 0) return;

                //index of element
                int tag = (int) field.getTag();

                if (tag < pinFieldsCount) {

                    //je-li index < počet fieldů, zažádej o focus následující field v řadě (o indexu o 1 vyšší)
                    pinFields[tag].requestFocus();

                } else {

                    //je-li index roven počtu fieldů (tj. vyplňujeme poslední), skryj klávesnici
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    //imm.toggleSoftInput (InputMethodManager.SHOW_FORCED, InputMethodManager.RESULT_HIDDEN);

                    //skryj soft klávesnici
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                    //zobraz soft klávestnici
                    //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
            }
        });
    }
}