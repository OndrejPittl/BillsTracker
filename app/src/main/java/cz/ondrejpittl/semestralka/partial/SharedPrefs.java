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

    public static boolean DEFAULT_PASSWD_REQ = true;
    public static boolean DEFAULT_FIRST_TIME_LAUNCHED = true;
    public static boolean DEFAULT_ANIMATIONS = true;
    public static boolean DEFAULT_ICON_DISPLAY = true;
    public static boolean DEFAULT_NOTE_DISPLAY = false;
    public static boolean DEFAULT_TUTORIAL_DISPLAYED = false;
    public static String DEFAULT_STORE = "---";
    public static String DEFAULT_CATEGORY = "Car";
    public static int DEFAULT_DESIGN = 0;

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




    public static void load(Activity activity){
        prefs = activity.getSharedPreferences("cz.ondrejpittl.semestralka", activity.MODE_PRIVATE);

        SharedPrefs.restoreDefaults();

        //clear();
    }

    //clears shared prefs
    public static void clear(){
        prefs.edit().clear().commit();
        SharedPrefs.restoreDefaults();
    }

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

    public static boolean isFirstTimeLaunch(){
        //return prefs.getBoolean("firstLaunch", true);
        int rs = Integer.parseInt(prefs.getString("firstLaunch", ""));

        if(rs == 0)
            return false;
        else
            return true;
    }

    public static boolean isFirstTimeLaunchSet(){
        return prefs.getString("firstLaunch", "").length() > 0;
    }

    /**
     * @TODO DOCASNE!
     *
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


    public static boolean checkPINCode(String entered){
        return getPINCode().equals(encryptIt(entered));
    }

    public static void reset(){
        prefs.edit().clear();
        prefs.edit().putBoolean("firstLaunch", true).commit();
    }

    public static boolean isDefaultCategorySet(){
        if(SharedPrefs.isNotNull())
            return prefs.getString("defaultCategory", "").length() > 0;
        return  false;
    }

    public static void storeDefaultCategory(String value){
        prefs.edit().putString("defaultCategory", value).commit();
    }

    public static String getDefaultCategory(){
        String str = prefs.getString("defaultCategory", "");
        return str;
    }

    public static boolean isDefaultStoreSet(){
        if(SharedPrefs.isNotNull())
            return prefs.getString("defaultStore", "").length() > 0;
        return  false;
    }

    public static void storeDefaultStore(String value){
        prefs.edit().putString("defaultStore", value).commit();
    }

    public static String getDefaultStore(){
        return prefs.getString("defaultStore", "");
    }

    public static void storeDefaultCurrency(String value){
        prefs.edit().putString("defaultCurrency", value).commit();
    }

    public static String getDefaultCurrency(){
        return prefs.getString("defaultCurrency", "");
    }

    public static void storePaymentNoteDisplay(boolean value){
        prefs.edit().putString("paymentNoteDisplay", value == true ? "1" : "0").commit();
    }

    public static boolean isPaymentNoteDisplaySet(){
        if(SharedPrefs.isNotNull())
            return prefs.getString("paymentNoteDisplay", "").length() > 0;
        return  false;
    }

    public static boolean getPaymentNoteDisplay(){

        int rs = Integer.parseInt(prefs.getString("paymentNoteDisplay", ""));

        if(rs == 0)
            return false;
        else
            return true;
    }

    public static void storePaymentAnimation(boolean value){
        prefs.edit().putString("paymentAnimation", value == true ? "1" : "0").commit();
    }

    public static boolean isPaymentAnimationSet(){
        return prefs.getString("paymentAnimation", "").length() > 0;
    }

    public static boolean getPaymentAnimation(){

        int rs = Integer.parseInt(prefs.getString("paymentAnimation", ""));

        if(rs == 0)
            return false;
        else
            return true;
    }

    public static void storePaymentIcons(boolean value){
        prefs.edit().putString("paymentIcons", value == true ? "1" : "0").commit();
    }

    public static boolean isPaymentIconSet(){
        return prefs.getString("paymentIcons", "").length() > 0;
    }

    public static boolean getPaymentIcons(){

        int rs = Integer.parseInt(prefs.getString("paymentIcons", ""));

        if(rs == 0)
            return false;
        else
            return true;
    }

    public static void storePasswordRequire(boolean value){

        //value == true -> store hashed "pSSwDR3Quir3D"
        //value == false -> store hashed "pSSwDnotR3Quir3D"

        prefs.edit().putString("passwordRequire", value == true ? encryptIt(passwdReq) : encryptIt(passwdNotReq)).commit();
    }

    public static boolean isPasswordRequireSet(){
        return prefs.getString("passwordRequire", "").length() > 0;
    }

    public static boolean isPasswordRequired(){
        String stored = prefs.getString("passwordRequire", "");

        //only in case of equality of hashed key and stored hashed key for NOT requiring passwd
        if(stored.equals(encryptIt(passwdNotReq)))
            return false;
        else
            return true;
    }

    public static boolean isTutorialDisplayedSet(){
        return prefs.getString("tutorialDisplayed", "").length() > 0;
    }

    public static void storeTutorialDisplayed(boolean value){
        prefs.edit().putString("tutorialDisplayed", value == true ? "1" : "0").commit();
    }

    public static boolean wasTutorialDisplayed(){

        int rs = Integer.parseInt(prefs.getString("tutorialDisplayed", ""));

        if(rs == 0)
            return false;
        else
            return true;
    }

    public static boolean isDefaultDesignSet(){
        return prefs.getString("defaultDesign", "").length() > 0;
    }

    public static void storeDefaultDesign(int defaultDesign){
        prefs.edit().putString("defaultDesign", String.valueOf(defaultDesign)).commit();
    }

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

    public static boolean isNotNull(){
        return prefs != null;
    }

}
