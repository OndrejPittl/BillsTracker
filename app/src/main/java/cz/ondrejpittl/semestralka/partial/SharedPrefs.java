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

        if(!SharedPrefs.isPasswordRequireSet())
            SharedPrefs.storePasswordRequire(true);

        if(!SharedPrefs.isPaymentNoteDisplaySet())
            SharedPrefs.storePaymentNoteDisplay(false);

        if(!SharedPrefs.isPaymentAnimationSet())
            SharedPrefs.storePaymentAnimation(true);

        if(!SharedPrefs.isPaymentIconSet())
            SharedPrefs.storePaymentIcons(true);

        if(!isTutorialDisplayedSet())
            storeTutorialDisplayed(false);

        //clear();
    }

    //clears shared prefs
    public static void clear(){
        prefs.edit().clear().commit();
    }

    /**
     * App was already launched.
     */
    public static void registerFirstTimeLaunched(){
        prefs.edit().putBoolean("firstLaunch", false).commit();
    }

    public static boolean isFirstTimeLaunch(){
        return prefs.getBoolean("firstLaunch", true);
        //return true;
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

    public static void storeDefaultCategory(String value){
        prefs.edit().putString("defaultCategory", value).commit();
    }

    public static String getDefaultCategory(){
        String str = prefs.getString("defaultCategory", "");
        return str;
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
        return prefs.getString("paymentNoteDisplay", "").length() > 0;
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





    /*private static String encryptIt(String value) {
        try {
            DESKeySpec keySpec = new DESKeySpec(secret.getBytes("UTF8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            byte[] clearText = value.getBytes("UTF8");
            // Cipher is not thread safe
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            String encrypedValue = Base64.encodeToString(cipher.doFinal(clearText), Base64.DEFAULT);
            Log.i("Ondra-prefs", "Encrypted: " + value + " -> " + encrypedValue);
            return encrypedValue;

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return value;
    };


    public static String decryptIt(String value) {
        try {
            DESKeySpec keySpec = new DESKeySpec(secret.getBytes("UTF8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            byte[] encrypedPwdBytes = Base64.decode(value, Base64.DEFAULT);
            // cipher is not thread safe
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decrypedValueBytes = (cipher.doFinal(encrypedPwdBytes));

            String decrypedValue = new String(decrypedValueBytes);
            Log.d("Ondra-prefs", "Decrypted: " + value + " -> " + decrypedValue);
            return decrypedValue;

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return value;
    }*/


}
