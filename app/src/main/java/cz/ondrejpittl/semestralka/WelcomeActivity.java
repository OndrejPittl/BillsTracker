package cz.ondrejpittl.semestralka;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cz.ondrejpittl.semestralka.controllers.WelcomeActivityLayoutController;


public class WelcomeActivity extends AppCompatActivity {

    /**
     * Controls Welcome/Login activity layout due to launch.
     */
    private WelcomeActivityLayoutController controller;

    private SharedPreferences prefs;

    /**
     *
     */
    private boolean activityLoaded;







    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setting content layout
        setContentView(R.layout.activity_welcome);

        //initialization
        this.init();

        //displaying welcome screen for first-time launch
        //displayWelcomeScreen();
        //displayLoginScreen();

        //app loaded flag
        activityLoaded = true;
    }

    protected void onResume() {
        super.onResume();

        if (this.isFirstTimeLaunch()) {
            displayWelcomeScreen();
        } else {
            displayLoginScreen();
        }
    }

    /**
     * Activity initialization.
     */
    private void init(){
        this.activityLoaded = false;
        this.prefs = getSharedPreferences("cz.ondrejpittl.semestralka", MODE_PRIVATE);
        this.controller = new WelcomeActivityLayoutController(this);
    }

    /**
     * Displays Welcome screen for the first-time launch.
     */
    private void displayWelcomeScreen(){
        //if(this.activityLoaded) return;
        this.controller.displayWelcomeScreen();
    }

    /**
     * Displays Login screen for the other-time launches.
     */
    private void displayLoginScreen(){
        //if(this.activityLoaded) return;
        this.controller.displayLoginScreen();
    }

    /**
     * App was already launched.
     */
    public void registerLoginScreenDisplayed(){
        this.prefs.edit().putBoolean("firstLaunch7", false).commit();
    }

    public boolean isFirstTimeLaunch(){
        return this.prefs.getBoolean("firstLaunch7", true);
        //return true;
    }

    /**
     * @TODO DOCASNE!
     *
     * Stores PIN code in SharedPreferences.
     * @param pin   PIN code from user input to store.
     */
    public void storePINCode(String pin){
        this.prefs.edit().putString("pin", pin).commit();
    }

    /**
     * @TODO DOCASNE!
     *
     * Loads PIN code from SharedPreferences.
     */
    public String getPINCode(){
        return this.prefs.getString("pin", "");
    }

    /**
     * Starts activity with about-app info.
     * @param v Reference of view that fires event.
     */
    public void startHelpActivity(View v){
        Intent i = new Intent(this, AboutActivity.class);
        startActivity(i);
    }

    /**
     * Starts activity with main part of app.
     */
    public void startHomeActivity(){
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
    }








    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */


}
