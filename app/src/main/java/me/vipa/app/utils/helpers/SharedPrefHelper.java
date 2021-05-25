package me.vipa.app.utils.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.security.CryptUtil;
import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.security.CryptUtil;

public class SharedPrefHelper {
    private static final String PREF_FILE = "Session";
    private final SharedPreferences mSharedPreferences;
    private final SharedPreferences.Editor mEditor;
    private static final int MODE_PRIVATE = 0;
    private CryptUtil cryptUtil;

    @SuppressLint("CommitPrefEdits")
    public SharedPrefHelper(Context context) {
        mSharedPreferences = context.getSharedPreferences(PREF_FILE, MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        cryptUtil = CryptUtil.getInstance();
    }

    @SuppressLint("CommitPrefEdits")
    public void clear() {
        mSharedPreferences.edit();
        mEditor.clear();
        mEditor.commit();
    }

   /* public String getString(String key, String defValue) {
        return mSharedPreferences.getString(key, defValue);
    }

    public void setString(String key, String value) {
        mEditor.putString(key, value);
        mEditor.commit();
    }*/

    public String getString(String key, String defValue) {
        String decryptedValue = cryptUtil.decrypt(mSharedPreferences.getString(key, defValue), AppConstants.MY_MVHUB_ENCRYPTION_KEY);
        if (decryptedValue == null || decryptedValue.equalsIgnoreCase("")||key.equalsIgnoreCase("DMS_Response")) {
            decryptedValue = mSharedPreferences.getString(key, defValue);
        }
        return decryptedValue;
    }

    public void setString(String key, String value) {
        String encryptedValue;
        encryptedValue = cryptUtil.encrypt(value, AppConstants.MY_MVHUB_ENCRYPTION_KEY);
        if (key.equalsIgnoreCase("DMS_Response")||value.equalsIgnoreCase("")) {
            mEditor.putString(key, value);
        } else {
            mEditor.putString(key, encryptedValue);
        }
        mEditor.commit();
    }

    public int getInt(String key, int defValue) {
        return mSharedPreferences.getInt(key, defValue);
    }

    public void setInt(String key, int value) {
        mEditor.putInt(key, value);
        mEditor.commit();
    }

    protected boolean getBoolean(String key, boolean defValue) {
        return mSharedPreferences.getBoolean(key, defValue);
    }

    protected void setBoolean(String key, boolean value) {
        mEditor.putBoolean(key, value);
        mEditor.commit();
    }
    public void saveDataGenre(List<String> data) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(data);
        editor.putString("genre", json);
        editor.apply();     // This line is IMPORTANT !!!

    }

    public List<String> getDataGenreList(){
        Gson gson = new Gson();
        String json = mSharedPreferences.getString("genre", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }
    public void saveDataGenreKeyValue(List<String> data) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(data);
        editor.putString("genreKey", json);
        editor.apply();     // This line is IMPORTANT !!!

    }

    public List<String> getDataGenreListKeyValue(){
        Gson gson = new Gson();
        String json = mSharedPreferences.getString("genreKey", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void saveDataSort(List<String> data) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(data);
        editor.putString("sort", json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public List<String> getDataSortList(){
        Gson gson = new Gson();
        String json = mSharedPreferences.getString("sort", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }


    public void saveDataSortKeyValue(List<String> data) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(data);
        editor.putString("sortKey", json);
        editor.apply();     // This line is IMPORTANT !!!

    }

    public List<String> getDataSortListKeyValue(){
        Gson gson = new Gson();
        String json = mSharedPreferences.getString("sortKey", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public  void saveKidsMode( boolean kidsMode) {

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean("kidMode", kidsMode);
        editor.apply();
    }

    public  boolean getKidsMode() {
        boolean text = mSharedPreferences.getBoolean("kidMode", false);
        return text;
    }

}
