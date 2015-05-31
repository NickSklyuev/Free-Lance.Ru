package trilodi.ru.free_lance.UI;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.google.android.gms.analytics.HitBuilders;

import trilodi.ru.free_lance.FreeLanceApplication;
import trilodi.ru.free_lanceru.R;

public class RegistrationActivity extends ActionBarActivity {

    ImageView backButton;

    private WebView webview;

    ProgressBarCircularIndeterminate progresser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        FreeLanceApplication.tracker().send(new HitBuilders.EventBuilder("ui", "open").setLabel("Регистрация пользователя").build());

        this.webview = (WebView)findViewById(R.id.webView);
        this.progresser = (ProgressBarCircularIndeterminate) findViewById(R.id.dialogProgress);

        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);



        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                progresser.setVisibility(View.GONE);
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                progresser.setVisibility(View.GONE);
            }
        });
        webview.loadUrl("https://www.fl.ru/registration/");

        backButton = (ImageView) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
