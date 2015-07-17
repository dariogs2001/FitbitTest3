package dariogonzalez.fitbittest3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.Verifier;
import org.scribe.model.Token;


public class AuthenticationActivity extends ActionBarActivity {

    WebView wvAuthorise;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        wvAuthorise = (WebView) findViewById(R.id.wvAuthorise);
        wvAuthorise.getSettings().setJavaScriptEnabled(true);

        wvAuthorise.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                final String url2 = url;

                if(url.startsWith("http://localhost")) {
                    Uri uri = Uri.parse(url);
                    String verifier = uri.getQueryParameter("oauth_verifier");
                    final Verifier v = new Verifier(verifier);

                    GetTokens(v);
                }

                return super.shouldOverrideUrlLoading(view, url);
            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//                String hola = url;
//            }
//
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
//                if(url.startsWith("http://localhost")) {
//                        Uri uri = Uri.parse(url);
//                        String verifier = uri.getQueryParameter("oauth_verifier");
//                        final Verifier v = new Verifier(verifier);
//
//                        Test(v);
//                }
//            }
        });

        // Replace these with your own api key and secret


        MainActivity.service = new ServiceBuilder().provider(FitbitApi.class).apiKey(MainActivity.apiKey)
                .apiSecret(MainActivity.apiSecret).callback("http://localhost").build();

        // network operation shouldn't run on main thread
        new Thread(new Runnable() {
            public void run() {
                MainActivity.requestToken = MainActivity.service.getRequestToken();
                final String authURL = MainActivity.service.getAuthorizationUrl(MainActivity.requestToken);

                // Webview nagivation should run on main thread again...
                wvAuthorise.post(new Runnable() {
                    @Override
                    public void run() {
                        wvAuthorise.loadUrl(authURL);
                    }
                });
            }
        }).start();


    }

    private void GetTokens(final Verifier v)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MainActivity.accessToken = MainActivity.service.getAccessToken(MainActivity.requestToken, v);

                    String eee = "Dario";

                    Intent myIntent = new Intent(AuthenticationActivity.this, MainActivity.class);
//                    myIntent.putExtra("accesstoken ", MainActivity.accessToken.getToken()); //Optional parameters
//                    myIntent.putExtra("secret", MainActivity.accessToken.getSecret()); //Optional parameters
                    AuthenticationActivity.this.startActivity(myIntent);
                } catch (Exception ex) {
                    String eee = ex.getMessage();
                }
            }
        }).start();

    }
}
