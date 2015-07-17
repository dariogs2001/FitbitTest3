package dariogonzalez.fitbittest3;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import java.util.Scanner;


public class MainActivity extends ActionBarActivity {

    static OAuthService service;
    static Token requestToken;
    static Token accessToken;

    Button btnUserInfo;
    Button btnActivities;
    Button btnRecent;
    Button btnGoals;
    Button btnSteps;

    static String apiKey = "a2f813cf8c7420eff5629382ae6a25a4";
    static String apiSecret = "0129ef7d53df74e1bb428fdaec8df9c1";

    EditText etToken;
    EditText etSecret;
    EditText etUserID;
    String token;
    String secret;
    String userID;

    TextView tvOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        etToken = (EditText) findViewById(R.id.access_token);
        etSecret = (EditText) findViewById(R.id.secret);
        etUserID = (EditText) findViewById(R.id.user_id);

        token = etToken.getText().toString();
        secret = etSecret.getText().toString();
        userID = etUserID.getText().toString();

        tvOutput = (TextView) findViewById(R.id.results);

        btnUserInfo = (Button) findViewById(R.id.btnAction1);
        btnActivities = (Button) findViewById(R.id.btnActivities);
        btnActivities.setVisibility(View.GONE);
        btnRecent = (Button) findViewById(R.id.btnRecent);
        btnGoals = (Button) findViewById(R.id.btnGoals);
        btnSteps = (Button) findViewById(R.id.btnSteps);

        btnUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeAction("https://api.fitbit.com/1/user/-/profile.json");
            }
        });

        btnActivities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeAction("https://api.fitbit.com/1/user/-/activities/date/2015-05-19.json");
            }
        });

        btnRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                executeAction("https://api.fitbit.com/1/user/-/activities/list.json?beforeDate=2015-05-19&offset=0&limit=100&sort=desc");
                executeAction("https://api.fitbit.com/1/user/-/activities/calories/date/2015-05-19/1d/15min/time/12:20/12:45.json");

            }
        });

        btnGoals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeAction("https://api.fitbit.com/1/user/-/activities/goals/daily.json");
            }
        });

        btnSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeAction("https://api.fitbit.com/1/user/-/activities/steps/date/today/1m.json");
//                executeAction("https://api.fitbit.com/1/user/-/activities/steps/date/2015-05-19/1d/15min/time/00:01/23:59.json");
//                executeAction("https://api.fitbit.com/1/user/-/activities/calories/date/2015-05-19/1d/15min/time/12:20/12:45.json");

            }
        });

    }

    public void btnTryAgain(View view){
        startActivity(new Intent(this, AuthenticationActivity.class));
    }

    public void executeAction(final String url) {

        if (service == null)
        {
            service = new ServiceBuilder().provider(FitbitApi.class).apiKey(apiKey)
                    .apiSecret(apiSecret).callback("http://localhost").build();
            accessToken = new Token(token, secret);
        }
        // network operation shouldn't run on main thread
        new Thread(new Runnable() {
            public void run() {

                OAuthRequest request = new OAuthRequest(Verb.GET, url);
                service.signRequest(accessToken, request); // the access token from step
                // 4
                final Response response = request.send();
                final String result = response.getBody();

                // Visual output should run on main thread again...
                tvOutput.post(new Runnable() {
                    @Override
                    public void run() {
                        tvOutput.setText(result);
                    }
                });
            }
        }).start();
    }
}
