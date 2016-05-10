package cz.ondrejpittl.semestralka.controllers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

    private LayoutInflater layoutInflater;

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
        this.layoutInflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //this.pinCodeFields = new PinCodeFields(this.activity);
    }

    /**
     * Displays Welcome screen for the first-time launch.
     */
    public void displayWelcomeScreen(){
        //showWelcomeDivider();
        showAboutButton();
        showContinueButton();
        this.hideSecretPIN();
    }

    /**
     * Displays Login screen for the other-time launches.
     */
    public void displayLoginScreen(){

        //this.hideSecretPIN();

        //login screen first-time
        if(SharedPrefs.isFirstTimeLaunch()) {

            //user sets the PIN code
            this.firstTimeLaunch = true;
            this.setLoginScreenRegisterTexts();
            this.showRegisterButton();
            this.hideSecretPIN();

        } else {

            //user enters PIN code
            this.setLoginScreenTexts();
            this.showEnterButton();
            this.displaySecretPIN();

        }

        //hideWelcomeDivider();
        showPinCodeFields();
        //setPinCodeListeners();
        this.pinCodeFields.setPinCodeListeners();
    }


    private void displaySecretPIN(){
        TextView tv = (TextView) this.activity.findViewById(R.id.tvSecretPIN);
        tv.setVisibility(View.VISIBLE);
    }

    private void hideSecretPIN(){
        TextView tv = (TextView) this.activity.findViewById(R.id.tvSecretPIN);
        tv.setVisibility(View.INVISIBLE);
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
                        activity.startAboutActivity(v);
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

                        //set secret passwd for reset pin
                        handleSecretPasswdSet();
                    }
                },
                this.activity.getApplicationContext()
        );

        container.addView(regBtn);
    }





    public void handleResetPrefs(View v) {
        LinearLayout container = (LinearLayout) layoutInflater.inflate(R.layout.secret_input, null);
        final EditText et = (EditText) container.findViewById(R.id.secretInput);

        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        builder.setTitle("PIN reset.");
        builder.setMessage("You are going to reset your password. Please, enter the secret PIN you were provided at first launch.");
        builder.setView(container);
        builder.setPositiveButton("Reset PIN", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}});
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }});
        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String secret = et.getText().toString();
                Log.i("Ondra-secret", "secret: " + secret);

                if (SharedPrefs.checkSecretPINCode(secret)) {
                    SharedPrefs.resetPINCode();
                    Toast.makeText(activity, "Reset successful.", Toast.LENGTH_SHORT).show();
                    Log.i("Ondra-secret", "secret stored");
                    dialog.dismiss();
                    activity.recreate();
                } else {
                    Toast.makeText(activity, "Secret password incorrect.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void handleSecretPasswdSet() {
        LinearLayout container = (LinearLayout) layoutInflater.inflate(R.layout.secret_input, null);
        final EditText et = (EditText) container.findViewById(R.id.secretInput);

        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        builder.setTitle(R.string.secretHeading);
        builder.setMessage(this.activity.getResources().getString(R.string.secretDescription));
        builder.setCancelable(false);
        builder.setView(container);
        builder.setPositiveButton("Set secret password.", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}});
        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String secret = et.getText().toString();
                Log.i("Ondra-secret", "secret: " + secret);

                if(secret.length() <= 0){
                    et.setBackgroundResource(R.drawable.shape_thin_border_error);
                } else {
                    Toast.makeText(activity, "Secret password was set.", Toast.LENGTH_SHORT).show();
                    Log.i("Ondra-secret", "secret stored");
                    SharedPrefs.storeSecretPINCode(secret);
                    dialog.dismiss();
                }
            }
        });
    }

}