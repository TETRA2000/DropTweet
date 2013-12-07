package com.example.droptweet.twitter;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.droptweet.Const;

public class AccountManager {
    private SharedPreferences mPref;

    public AccountManager(Context context) {
        mPref = context.getSharedPreferences(Const.PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean hasAccount() {
        return mPref.contains(Const.KEY_USER_NAME);
    }

    public void setAccount(Account account) {
    	if(account==null)
    		return;
    	
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(Const.KEY_USER_NAME, account.name);
        editor.putString(Const.KEY_TOKEN, account.token);
        editor.putString(Const.KEY_TOKEN_SECRET, account.secret);
        editor.commit();
    }

    public Account getAccount() {
        String name = mPref.getString(Const.KEY_USER_NAME, null);
        String token = mPref.getString(Const.KEY_TOKEN, null);
        String tokenSecret = mPref.getString(Const.KEY_TOKEN_SECRET, null);
        
        return !(name==null && token==null && tokenSecret==null) ?
        		new Account(name, token, tokenSecret) : null;
    }

    public void removeAccount() {
        SharedPreferences.Editor editor = mPref.edit();
        editor.remove(Const.KEY_USER_NAME);
        editor.remove(Const.KEY_TOKEN);
        editor.remove(Const.KEY_TOKEN_SECRET);
        editor.commit();
    }
}