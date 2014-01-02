package jp.tetra2000.droptweet.test.twitter;

import jp.tetra2000.droptweet.twitter.Account;

import android.test.AndroidTestCase;

public class AccountTest extends AndroidTestCase {
	public void testEqualsがTrueの場合() {
		Account a1 = new Account("Jhon", "abc", "def");
		Account a2 = new Account("Jhon", "abc", "def");
		
		assertEquals(a1, a2);
	}
	
	public void testEqualsがFalseの場合() {
		Account a1 = new Account("Jhon", "abc", "def");
		Account a2 = new Account("Jack", "123", "456");
		
		//TODO 他のパターンも追加
		
		assertFalse(a1.equals(a2));
		assertFalse(a2.equals(a1));
	}
}