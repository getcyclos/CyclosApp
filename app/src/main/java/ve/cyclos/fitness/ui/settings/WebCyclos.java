package ve.cyclos.fitness.ui.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ve.cyclos.fitness.R;

public class WebCyclos extends AppCompatActivity {
    private WebView WebCyclos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_cyclos);

        WebCyclos = findViewById(R.id.Webview_main);
        WebCyclos.getSettings().setJavaScriptEnabled(true);
        WebCyclos.setWebViewClient(new WebViewClient());
        WebCyclos.loadUrl("https://getcyclos.com");
    }
}