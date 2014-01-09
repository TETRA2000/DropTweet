package jp.tetra2000.droptweet.ui;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import jp.tetra2000.droptweet.R;

public class CopyrightActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_copyright);

        WebView webView = (WebView) findViewById(R.id.webViewCopyright);
        webView.loadUrl("file:///android_asset/copyright/index.html");
    }
}
