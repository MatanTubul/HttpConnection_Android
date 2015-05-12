package com.example.matan.test;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button sendPostReqButton;
    private Button clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameEditText = (EditText) findViewById(R.id.login_username_editText);
        passwordEditText = (EditText) findViewById(R.id.login_password_editText);

        sendPostReqButton = (Button) findViewById(R.id.login_sendPostReq_button);
        sendPostReqButton.setOnClickListener(this);

        clearButton = (Button) findViewById(R.id.login_clear_button);
        clearButton.setOnClickListener(this);
    }

    public void onClick(View v) {

        if (v.getId() == R.id.login_clear_button) {
            usernameEditText.setText("");
            passwordEditText.setText("");
            passwordEditText.setCursorVisible(false);
            passwordEditText.setFocusable(false);
            usernameEditText.setCursorVisible(true);
            passwordEditText.setFocusable(true);
        } else if (v.getId() == R.id.login_sendPostReq_button) {
            String givenUsername = usernameEditText.getEditableText().toString();
            String givenPassword = passwordEditText.getEditableText().toString();

            System.out.println("Givenname :" + givenUsername + " Given password :" + givenPassword);

            sendPostRequest(givenUsername, givenPassword);
        }
    }


    private void sendPostRequest(String givenUsername, String givenPassword) {

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String paramUsername = params[0];
                String paramPassword = params[1];

                System.out.println("*** doInBackground ** paramUsername " + paramUsername + " paramPassword :" + paramPassword);

                HttpClient httpClient = new DefaultHttpClient();

                // In a POST request, we don't pass the values in the URL.
                //Therefore we use only the web page URL as the parameter of the HttpPost argument
                HttpPost httpPost = new HttpPost("http://10.0.2.2/test/index.php");

                // Because we are not passing values over the URL, we should have a mechanism to pass the values that can be
                //uniquely separate by the other end.
                //To achieve that we use BasicNameValuePair
                //Things we need to pass with the POST request
                BasicNameValuePair usernameBasicNameValuePair = new BasicNameValuePair("paramUsername", paramUsername);
                BasicNameValuePair passwordBasicNameValuePAir = new BasicNameValuePair("paramPassword", paramPassword);

                // We add the content that we want to pass with the POST request to as name-value pairs
                //Now we put those sending details to an ArrayList with type safe of NameValuePair
                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(usernameBasicNameValuePair);
                nameValuePairList.add(passwordBasicNameValuePAir);

                try {
                    // UrlEncodedFormEntity is an entity composed of a list of url-encoded pairs.
                    //This is typically useful while sending an HTTP POST request.
                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);

                    // setEntity() hands the entity (here it is urlEncodedFormEntity) to the request.
                    httpPost.setEntity(urlEncodedFormEntity);

                    try {
                        // HttpResponse is an interface just like HttpPost.
                        //Therefore we can't initialize them
                        HttpResponse httpResponse = httpClient.execute(httpPost);

                        // According to the JAVA API, InputStream constructor do nothing.
                        //So we can't initialize InputStream although it is not an interface
                        InputStream inputStream = httpResponse.getEntity().getContent();

                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                        StringBuilder stringBuilder = new StringBuilder();

                        String bufferedStrChunk = null;

                        while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                            stringBuilder.append(bufferedStrChunk);
                        }

                        return stringBuilder.toString();

                    } catch (ClientProtocolException cpe) {
                        System.out.println("Firstption caz of HttpResponese :" + cpe);
                        cpe.printStackTrace();
                    } catch (IOException ioe) {
                        System.out.println("Secondption caz of HttpResponse :" + ioe);
                        ioe.printStackTrace();
                    }

                } catch (UnsupportedEncodingException uee) {
                    System.out.println("Anption given because of UrlEncodedFormEntity argument :" + uee);
                    uee.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                if (result.equals("working")) {
                    Toast.makeText(getApplicationContext(), "HTTP POST is working...", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid POST req...", Toast.LENGTH_LONG).show();
                }
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(givenUsername, givenPassword);
    }
}
