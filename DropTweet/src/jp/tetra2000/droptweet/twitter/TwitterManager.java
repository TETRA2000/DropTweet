package jp.tetra2000.droptweet.twitter;

import jp.tetra2000.droptweet.Const;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TwitterManager {
    private static Twitter mTwitter;

    public static void init(Account account) {
        if(mTwitter==null) {
            mTwitter = TwitterFactory.getSingleton();
            mTwitter.setOAuthConsumer(Const.CONSUMER_KEY, Const.CONSUMER_SECRET);
        }
        AccessToken token = new AccessToken(account.token, account.secret);
        mTwitter.setOAuthAccessToken(token);
    }

    public static Twitter getTwitter() {
        return mTwitter;
    }
}
