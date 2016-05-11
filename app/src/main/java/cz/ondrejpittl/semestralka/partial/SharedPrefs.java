package cz.ondrejpittl.semestralka.partial;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import cz.ondrejpittl.semestralka.models.Category;
import cz.ondrejpittl.semestralka.models.Currency;
import cz.ondrejpittl.semestralka.models.Store;

/**
 * Created by OndrejPittl on 22.04.16.
 */
public class SharedPrefs {

    /**
     * Default value of a password require.
     */
    public static boolean DEFAULT_PASSWD_REQ = true;

    /**
     * Default value of a fist-time-launch.
     */
    public static boolean DEFAULT_FIRST_TIME_LAUNCHED = true;

    /**
     * Default value of an animations allowed.
     */
    public static boolean DEFAULT_ANIMATIONS = true;

    /**
     * Default value of payment icons allowed.
     */
    public static boolean DEFAULT_ICON_DISPLAY = true;

    /**
     * Default value of a payment note display.
     */
    public static boolean DEFAULT_NOTE_DISPLAY = false;

    /**
     * Default value of a tutorial displayed.
     */
    public static boolean DEFAULT_TUTORIAL_DISPLAYED = false;

    /**
     * Default value of a store.
     */
    public static String DEFAULT_STORE = "---";

    /**
     * Default value of a category.
     */
    public static String DEFAULT_CATEGORY = "Car";

    /**
     * Default value of a design selection.
     */
    public static int DEFAULT_DESIGN = 0;

    /**
     * Shared preferences reference.
     */
    private static SharedPreferences prefs;

    /**
     * Key of encryption function SHA-1 for flag meaning password is required.
     * Flag 0/1 could be changed by curious user with rooted android phone.
     */
    private static String passwdReq = "pSSwDR3Quir3D";

    /**
     * Key of encryption function SHA-1 for flag meaning password is NOT required.
     * Flag 0/1 could be changed by curious user with rooted android phone.
     */
    private static String passwdNotReq = "pSSwDnotR3Quir3D";


    /**
     * Loads Shared preferences and sets default values.
     * @param activity  activity reference
     */
    public static void load(Activity activity){
        prefs = activity.getSharedPreferences("cz.ondrejpittl.semestralka", activity.MODE_PRIVATE);

        SharedPrefs.restoreDefaults();
    }

    /**
     *  Clears shared prefs and sets default values.
     */
    public static void clear(){
        prefs.edit().clear().commit();
        SharedPrefs.restoreDefaults();
    }

    /**
     * Sets default settings.
     */
    private static void restoreDefaults(){

        if(!SharedPrefs.isPasswordRequireSet())
            SharedPrefs.storePasswordRequire(SharedPrefs.DEFAULT_PASSWD_REQ);

        if(!isFirstTimeLaunchSet())
            SharedPrefs.storeFirstTimeLaunched(SharedPrefs.DEFAULT_FIRST_TIME_LAUNCHED);

        if(!SharedPrefs.isPaymentAnimationSet())
            SharedPrefs.storePaymentAnimation(SharedPrefs.DEFAULT_ANIMATIONS);

        if(!SharedPrefs.isPaymentIconSet())
            SharedPrefs.storePaymentIcons(SharedPrefs.DEFAULT_ICON_DISPLAY);

        if(!SharedPrefs.isPaymentNoteDisplaySet())
            SharedPrefs.storePaymentNoteDisplay(SharedPrefs.DEFAULT_NOTE_DISPLAY);

        if(!isTutorialDisplayedSet())
            SharedPrefs.storeTutorialDisplayed(SharedPrefs.DEFAULT_TUTORIAL_DISPLAYED);

        if(!isDefaultCategorySet())
            SharedPrefs.storeDefaultCategory(SharedPrefs.DEFAULT_CATEGORY);

        if(!isDefaultStoreSet())
            SharedPrefs.storeDefaultStore(SharedPrefs.DEFAULT_STORE);

        if(!isDefaultDesignSet())
            SharedPrefs.storeDefaultDesign(SharedPrefs.DEFAULT_DESIGN);
    }

    /**
     * App was already launched.
     */
    public static void storeFirstTimeLaunched(boolean firstLaunch){
        //prefs.edit().putBoolean("firstLaunch", firstLaunch).commit();
        prefs.edit().putString("firstLaunch", firstLaunch == true ? "1" : "0").commit();
    }

    /**
     * Determines whether is the app launched for the first time.
     * @return  true – first time, false – other times
     */
    public static boolean isFirstTimeLaunch(){
        int rs = Integer.parseInt(prefs.getString("firstLaunch", ""));

        if(rs == 0)
            return false;
        else
            return true;
    }

    /**
     * Determines whether is the property set.
     * @return true – it is, false – not
     */
    public static boolean isFirstTimeLaunchSet(){
        return prefs.getString("firstLaunch", "").length() > 0;
    }

    /**
     * Stores PIN code in SharedPreferences.
     * @param pin   PIN code from user input to store.
     */
    public static void storePINCode(String pin){

        prefs.edit().putString("pin", encryptIt(pin)).commit();

    }

    /**
     *
     * Loads PIN code from SharedPreferences.
     */
    public static String getPINCode(){

        return prefs.getString("pin", "");

    }

    /**
     * Checks the equality of an encrypted entered PIN and the encrypted stored one.
     * @param entered   entered PIN by user
     * @return          true – same, false – not
     */
    public static boolean checkPINCode(String entered){
        return getPINCode().equals(encryptIt(entered));
    }

    /**
     * Resets the default value of first-time-alunch flag to enable user re-set the PIN.
     */
    public static void resetPINCode(){
        SharedPrefs.storeFirstTimeLaunched(DEFAULT_FIRST_TIME_LAUNCHED);
    }

    /**
     * Stores encrypted PIN with SHA-1.
     * @param pin   entered PIN
     */
    public static void storeSecretPINCode(String pin){
        prefs.edit().putString("secretpin", encryptIt(pin)).commit();
    }

    /**
     * Getter of stored encrypted PIN.
     * @return  encrypted PIN
     */
    public static String getSecretPINCode(){
        return prefs.getString("secretpin", "");

    }

    /**
     * Check the equality of an encrypted entered secret password and the encrypted stored one.
     * @param entered   entered secret password
     * @return  true – same, false – not
     */
    public static boolean checkSecretPINCode(String entered){
        return getSecretPINCode().equals(encryptIt(entered));
    }

    /**
     * Determines whether is the property set.
     * @return true – it is, false – not
     */
    public static boolean isDefaultCategorySet(){
        if(SharedPrefs.isNotNull())
            return prefs.getString("defaultCategory", "").length() > 0;
        return  false;
    }

    /**
     * Stores default category property.
     * @param value value to be stored
     */
    public static void storeDefaultCategory(String value){
        prefs.edit().putString("defaultCategory", value).commit();
    }

    /**
     * Getter of the stored default category.
     * @return  stored property
     */
    public static String getDefaultCategory(){
        String str = prefs.getString("defaultCategory", "");
        return str;
    }

    /**
     * Determines whether is the property set.
     * @return true – it is, false – not
     */
    public static boolean isDefaultStoreSet(){
        if(SharedPrefs.isNotNull())
            return prefs.getString("defaultStore", "").length() > 0;
        return  false;
    }

    /**
     * Stores default store property.
     * @param value value to be stored
     */
    public static void storeDefaultStore(String value){
        prefs.edit().putString("defaultStore", value).commit();
    }

    /**
     * Getter of the stored default store.
     * @return  stored property
     */
    public static String getDefaultStore(){
        return prefs.getString("defaultStore", "");
    }

    /**
     * Stores default currency property.
     * @param value value to be stored
     */
    public static void storeDefaultCurrency(String value){
        prefs.edit().putString("defaultCurrency", value).commit();
    }

    /**
     * Getter of the stored default currency.
     * @return  stored property
     */
    public static String getDefaultCurrency(){
        return prefs.getString("defaultCurrency", "");
    }

    /**
     * Stores payment default note display property.
     * @param value value to be stored
     */
    public static void storePaymentNoteDisplay(boolean value){
        prefs.edit().putString("paymentNoteDisplay", value == true ? "1" : "0").commit();
    }

    /**
     * Determines whether is the property set.
     * @return true – it is, false – not
     */
    public static boolean isPaymentNoteDisplaySet(){
        if(SharedPrefs.isNotNull())
            return prefs.getString("paymentNoteDisplay", "").length() > 0;
        return  false;
    }

    /**
     * Getter of the stored default note display.
     * @return  stored property
     */
    public static boolean getPaymentNoteDisplay(){

        int rs = Integer.parseInt(prefs.getString("paymentNoteDisplay", ""));

        if(rs == 0)
            return false;
        else
            return true;
    }

    /**
     * Stores payment animation allowed property.
     * @param value value to be stored
     */
    public static void storePaymentAnimation(boolean value){
        prefs.edit().putString("paymentAnimation", value == true ? "1" : "0").commit();
    }

    /**
     * Determines whether is the property set.
     * @return true – it is, false – not
     */
    public static boolean isPaymentAnimationSet(){
        return prefs.getString("paymentAnimation", "").length() > 0;
    }

    /**
     * Getter of the stored payment animation allowed.
     * @return  stored property
     */
    public static boolean getPaymentAnimation(){

        int rs = Integer.parseInt(prefs.getString("paymentAnimation", ""));

        if(rs == 0)
            return false;
        else
            return true;
    }

    /**
     * Stores payment icons allowed property.
     * @param value value to be stored
     */
    public static void storePaymentIcons(boolean value){
        prefs.edit().putString("paymentIcons", value == true ? "1" : "0").commit();
    }

    /**
     * Determines whether is the property set.
     * @return true – it is, false – not
     */
    public static boolean isPaymentIconSet(){
        return prefs.getString("paymentIcons", "").length() > 0;
    }

    /**
     * Getter of the stored payment icons.
     * @return  stored property
     */
    public static boolean getPaymentIcons(){

        int rs = Integer.parseInt(prefs.getString("paymentIcons", ""));

        if(rs == 0)
            return false;
        else
            return true;
    }

    /**
     * Stores password require property.
     * @param value value to be stored
     */
    public static void storePasswordRequire(boolean value){

        //value == true -> store hashed "pSSwDR3Quir3D"
        //value == false -> store hashed "pSSwDnotR3Quir3D"

        prefs.edit().putString("passwordRequire", value == true ? encryptIt(passwdReq) : encryptIt(passwdNotReq)).commit();
    }

    /**
     * Determines whether is the property set.
     * @return true – it is, false – not
     */
    public static boolean isPasswordRequireSet(){
        return prefs.getString("passwordRequire", "").length() > 0;
    }

    /**
     * Determines whether is the password required or not.
     * @return  true – required, not – not
     */
    public static boolean isPasswordRequired(){
        String stored = prefs.getString("passwordRequire", "");

        //only in case of equality of hashed key and stored hashed key for NOT requiring passwd
        if(stored.equals(encryptIt(passwdNotReq)))
            return false;
        else
            return true;
    }

    /**
     * Determines whether is the property set.
     * @return true – it is, false – not
     */
    public static boolean isTutorialDisplayedSet(){
        return prefs.getString("tutorialDisplayed", "").length() > 0;
    }

    /**
     * Stores tutorial displayed property.
     * @param value value to be stored
     */
    public static void storeTutorialDisplayed(boolean value){
        prefs.edit().putString("tutorialDisplayed", value == true ? "1" : "0").commit();
    }

    /**
     * Determines whether was a tutorial displayed or not.
     * @return  true – displayed, not – not
     */
    public static boolean wasTutorialDisplayed(){

        int rs = Integer.parseInt(prefs.getString("tutorialDisplayed", ""));

        if(rs == 0)
            return false;
        else
            return true;
    }

    /**
     * Determines whether is the property set.
     * @return true – it is, false – not
     */
    public static boolean isDefaultDesignSet(){
        return prefs.getString("defaultDesign", "").length() > 0;
    }

    /**
     * Stores default design property.
     * @param value value to be stored
     */
    public static void storeDefaultDesign(int value){
        prefs.edit().putString("defaultDesign", String.valueOf(value)).commit();
    }

    /**
     * Getter of the stored default design.
     * @return  stored property
     */
    public static int getDefaultDesign(){
        return Integer.parseInt(prefs.getString("defaultDesign", ""));
    }


    /**
     * SHA-1 string password hash.
     * @param input Input string.
     * @return      MD5 hash.
     */
    private static String encryptIt(String input){
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-1");
            digest.update(input.getBytes(Charset.forName("UTF-8")), 0, input.length());
            byte[] magnitude = digest.digest();
            BigInteger bi = new BigInteger(1, magnitude);
            String hash = String.format("%0" + (magnitude.length << 1) + "x", bi);
            return hash;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";

    }

    /**
     * Determines whether the shared prefs was already loaded.
     * @return  true – loaded, not – not
     */
    public static boolean isNotNull(){
        return prefs != null;
    }

}
