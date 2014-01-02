package jp.tetra2000.droptweet.test.twitter;

import jp.tetra2000.droptweet.Const;
import jp.tetra2000.droptweet.twitter.Account;
import jp.tetra2000.droptweet.twitter.AccountManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.InstrumentationTestCase;

public class AccountManagerTest extends InstrumentationTestCase {
	private SharedPreferences mPref;
	private AccountManager sut;
	
	@Override
	public void setUp() {
		Context context = getInstrumentation().getContext();
		mPref = context.getSharedPreferences(Const.PREF_NAME, Context.MODE_PRIVATE);		
		resetPref();
		
		sut = new AccountManager(context);
	}
	
	@Override
	public void tearDown() {
		resetPref();
	}
	
	private void resetPref() {
		SharedPreferences.Editor editor = mPref.edit();
		editor.remove(Const.KEY_USER_NAME);
		editor.remove(Const.KEY_TOKEN);
		editor.remove(Const.KEY_TOKEN_SECRET);
		editor.commit();
	}
	
	public void test正しくアカウントが保存出来る() {
		Account expected = new Account("Android", "key", "secret");
		
		sut.setAccount(expected);
		Account actual = sut.getAccount();
		
		assertEquals(expected, actual);
	}
	
	public void hasAccountが正しい値を返す() {
		fail();
	}
	
	public void removeAccountが正しい値を返す() {
		fail();
	}
}
