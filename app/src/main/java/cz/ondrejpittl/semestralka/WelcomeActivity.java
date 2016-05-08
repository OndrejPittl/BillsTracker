package cz.ondrejpittl.semestralka;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import cz.ondrejpittl.semestralka.controllers.WelcomeUIController;
import cz.ondrejpittl.semestralka.partial.Designer;
import cz.ondrejpittl.semestralka.partial.SharedPrefs;


public class WelcomeActivity extends AppCompatActivity {

    /**
     * Controls Welcome/Login activity layout due to launch.
     */
    private WelcomeUIController controller;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Designer.setFullscreenActivity(this);

        //setting content layout
        this.setContentView(R.layout.activity_welcome);

        //initialization
        this.init();
    }

    protected void onResume() {
        super.onResume();
        Designer.updateDesign(this);

        if (SharedPrefs.isFirstTimeLaunch()) {
            this.displayWelcomeScreen();
        } else {
            this.displayLoginScreen();
        }
    }

    /**
     * Activity initialization.
     */
    private void init(){
        SharedPrefs.load(this);

        if(SharedPrefs.isPasswordRequireSet() && !SharedPrefs.isPasswordRequired()) {
            this.startHomeActivity();
        }

        Designer.updateDesign(this);
        this.controller = new WelcomeUIController(this);
    }

    /**
     * Displays Welcome screen for the first-time launch.
     */
    private void displayWelcomeScreen(){
        this.controller.displayWelcomeScreen();
    }

    /**
     * Displays Login screen for the other-time launches.
     */
    private void displayLoginScreen(){
        this.controller.displayLoginScreen();
    }

    /**
     * Starts activity with about-app info.
     * @param v Reference of view that fires event.
     */
    public void startAboutActivity(View v){
        Intent i = new Intent(this, AboutActivity.class);
        this.startActivity(i);
    }

    /**
     * Starts activity with main part of app.
     */
    public void startHomeActivity(){
        Intent i = new Intent(this, HomeActivity.class);
        this.startActivity(i);
    }
}
