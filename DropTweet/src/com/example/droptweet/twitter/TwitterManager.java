package com.example.droptweet.twitter;

import com.example.droptweet.Const;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TwitterManager {
    private static Twitter mTwitter;

    public static void init(Account account) {
        mTwitter = TwitterFactory.getSingleton();
        mTwitter.setOAuthConsumer(Const.CONSUMER_KEY, Const.CONSUMER_SECRET);
        AccessToken token = new AccessToken(account.token, account.secret);
        mTwitter.setOAuthAccessToken(token);
    }

    public static Twitter getTwitter() {
        return mTwitter;
    }
}
