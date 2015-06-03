package com.example.matan.test;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

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
    private EditText emailEditText;
    private Button sendPostReqButton;
    private Button clearButton;
    private Button insert;
    private final String server_url="http://gpsport.byethost12.com/httprequest/test.php";
    private final String localhost_url="http://10.0.2.2/test/test.php";
    TableLayout tl ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameEditText = (EditText) findViewById(R.id.login_username_editText);
        passwordEditText = (EditText) findViewById(R.id.login_password_editText);
        emailEditText = (EditText) findViewById(R.id.editTextEmail);
        tl = (TableLayout)findViewById(R.id.table);

        sendPostReqButton = (Button) findViewById(R.id.login_sendPostReq_button);
        sendPostReqButton.setOnClickListener(this);

        clearButton = (Button) findViewById(R.id.login_clear_button);
        clearButton.setOnClickListener(this);
        insert = (Button) findViewById(R.id.buttonInsert);
        insert.setOnClickListener(this);
    }

    public void onClick(View v) {

        if (v.getId() == R.id.login_clear_button) {
            usernameEditText.setText("");
            passwordEditText.setText("");
            emailEditText.setText("");
            passwordEditText.setCursorVisible(true);
            passwordEditText.setFocusable(true);
            usernameEditText.setCursorVisible(true);
            usernameEditText.setFocusable(true);
        } else if (v.getId() == R.id.login_sendPostReq_button) {
            String givenUsername = usernameEditText.getEditableText().toString();
            String givenPassword = passwordEditText.getEditableText().toString();

            System.out.println("Givenname :" + givenUsername + " Given password :" + givenPassword);

            sendPostRequest(givenUsername, givenPassword);
        }
        else if (v.getId() == R.id.buttonInsert)
        {
            Log.d("insert function runing with params","user , emsil,password");
            String givenUsername = usernameEditText.getEditableText().toString();
            String givenPassword = passwordEditText.getEditableText().toString();
            String givenEmail = emailEditText.getEditableText().toString();
            insertToDB(givenUsername,givenPassword,givenEmail);
        }
    }

    private void insertToDB(String user,String email,String Password)
    {
        class insertToDBAsync extends  AsyncTask<String,Void,String>
        {

            @Override
            protected String doInBackground(String... params) {
                String res = "";
                String paramUsername = params[0];
                String paramPassword = params[1];
                String email = params[2];
                Log.d("insert method","insert in progress with"+" "+paramUsername+" "+paramPassword+" "+email);
                HttpClient httpClient = new DefaultHttpClient();

                // In a POST request, we don't pass the values in the URL.
                //Therefore we use only the web page URL as the parameter of the HttpPost argument
                HttpPost httpPost = new HttpPost(localhost_url);
                BasicNameValuePair insertBasicNameValuePair = new BasicNameValuePair("tag", "insert");
                BasicNameValuePair usernameBasicNameValuePair = new BasicNameValuePair("paramUsername", paramUsername);
                BasicNameValuePair passwordBasicNameValuePAir = new BasicNameValuePair("paramPassword", paramPassword);
                BasicNameValuePair emailBasicNameValuePAir = new BasicNameValuePair("paramEmail", email);
                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(insertBasicNameValuePair);
                nameValuePairList.add(usernameBasicNameValuePair);
                nameValuePairList.add(passwordBasicNameValuePAir);
                nameValuePairList.add(emailBasicNameValuePAir);
                try {
                    // UrlEncodedFormEntity is an entity composed of a list of url-encoded pairs.
                    //This is typically useful while sending an HTTP POST request.

                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);

                    // setEntity() hands the entity (here it is urlEncodedFormEntity) to the request.
                    httpPost.setEntity(urlEncodedFormEntity);

                    try {
                        // HttpResponse is an interface just like HttpPost.
                        //Therefore we can't initialize them
                        Log.d("httpPost","execute post");
                        HttpResponse httpResponse = httpClient.execute(httpPost);

                        // According to the JAVA API, InputStream constructor do nothing.
                        //So we can't initialize InputStream although it is not an interface
                        InputStream inputStream = httpResponse.getEntity().getContent();

                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                        Log.d("InputStreamReader","execute inputStreamReader");
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                        StringBuilder stringBuilder = new StringBuilder();
                        Log.d("StringBuilder","execute StringBuilder");
                        String bufferedStrChunk = null;

                        while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                            stringBuilder.append(bufferedStrChunk+"\n");
                        }
                        Log.d("inputStreamReader","execute close");
                        inputStreamReader.close();
                        res = stringBuilder.toString();
                        Log.d("json result",res);
                        JSONObject jarray = new JSONObject(res);

                        Log.d("json result",jarray.toString());
                        try {
                            Log.d("JSON Msg:", jarray.getString("msg"));
                        } catch (JSONException e) {
                            Log.d("JSon Failed" , jarray.toString());
                            e.printStackTrace();
                        }
                        return res;

                    } catch (ClientProtocolException cpe) {
                        System.out.println("Firstption caz of HttpResponese :" + cpe);
                        cpe.printStackTrace();
                    } catch (IOException ioe) {
                        System.out.println("Secondption caz of HttpResponse :" + ioe);
                        ioe.printStackTrace();
                    } catch (JSONException e) {
                        Log.d("JSON error:", "failed to create jarray");
                        e.printStackTrace();
                    }

                }  catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return null;
            }
            protected void onPostExecute(String result){
                super.onPostExecute(result);
                Log.d("on post:",result);

                try {
                    JSONObject jarray = new JSONObject(result);
                    switch (jarray.getString("msg").toString()) {
                        case "insert working":
                            Toast.makeText(getApplicationContext(), "Insert Successful", Toast.LENGTH_LONG).show();
                            break;
                        default:
                            Toast.makeText(getApplicationContext(), "Insert was decline", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        insertToDBAsync sendPostReqAsyncTask = new insertToDBAsync();
        sendPostReqAsyncTask.execute(user, email,Password);
    }


    private void sendPostRequest(String givenUsername, String givenPassword) {

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {


            @Override
            protected String doInBackground(String... params) {
                String res = "";

                String paramUsername = params[0];
                String paramPassword = params[1];

                System.out.println("*** doInBackground ** paramUsername " + paramUsername + " paramPassword :" + paramPassword);

                HttpClient httpClient = new DefaultHttpClient();

                // In a POST request, we don't pass the values in the URL.
                //Therefore we use only the web page URL as the parameter of the HttpPost argument
                HttpPost httpPost = new HttpPost(localhost_url);

                // Because we are not passing values over the URL, we should have a mechanism to pass the values that can be
                //uniquely separate by the other end.
                //To achieve that we use BasicNameValuePair
                //Things we need to pass with the POST request
                BasicNameValuePair loginBasicNameValuePair = new BasicNameValuePair("tag", "login");
                BasicNameValuePair usernameBasicNameValuePair = new BasicNameValuePair("paramUsername", paramUsername);
                BasicNameValuePair passwordBasicNameValuePAir = new BasicNameValuePair("paramPassword", paramPassword);

                // We add the content that we want to pass with the POST request to as name-value pairs
                //Now we put those sending details to an ArrayList with type safe of NameValuePair
                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(loginBasicNameValuePair);
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
                            stringBuilder.append(bufferedStrChunk+"\n");
                        }
                        inputStreamReader.close();
                            res = stringBuilder.toString();
                            Log.d("json result",res);
                            JSONObject jarray = new JSONObject(res);

                            Log.d("json result",jarray.toString());
                            try {
                                Log.d("JSON Msg:", jarray.getString("msg"));
                            } catch (JSONException e) {
                                Log.d("JSon Failed" , jarray.toString());
                                e.printStackTrace();
                            }
                        return res;

                    } catch (ClientProtocolException cpe) {
                        System.out.println("Firstption caz of HttpResponese :" + cpe);
                        cpe.printStackTrace();
                    } catch (IOException ioe) {
                        System.out.println("Secondption caz of HttpResponse :" + ioe);
                        ioe.printStackTrace();
                    } catch (JSONException e) {
                        Log.d("JSON error:", "failed to create jarray");
                        e.printStackTrace();
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
                Log.d("on post:",result);

                try {
                    JSONObject jarray = new JSONObject(result);
                   // JSONArray arr =  jarray.getJSONArray("user");
                    //Log.d("arr :",arr.toString());
                    switch (jarray.getString("msg").toString())
                    {
                        case "insert working":
                            Toast.makeText(getApplicationContext(), "HTTP POST is working...", Toast.LENGTH_LONG).show();
                            break;
                        case "working":
                            JSONObject temp = jarray.getJSONObject("user");
                            Log.d("temp :",temp.toString());

                            //////
                            TableRow row = new TableRow(MainActivity.this);
                            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                            lp.setMargins(2,2,2,2);
                            row.setLayoutParams(lp);
                            Log.i("Check2","parsing the json");
                            row.setBackgroundColor(0xff000000);
                            TextView id = new TextView(MainActivity.this);
                            TextView name = new TextView(MainActivity.this);
                            TextView email = new TextView(MainActivity.this);
                            TextView password = new TextView(MainActivity.this);
                            id.setBackground(getResources().getDrawable(R.drawable.border));
                            id.setTextColor(0xFFFFFFFF);
                            id.setText(temp.getString("id"));
                            name.setBackground(getResources().getDrawable(R.drawable.border));
                            name.setTextColor(0xFFFFFFFF);
                            name.setText(temp.getString("usern"));
                            email.setBackground(getResources().getDrawable(R.drawable.border));
                            email.setTextColor(0xFFFFFFFF);
                            email.setText(temp.getString("email"));
                            password.setBackground(getResources().getDrawable(R.drawable.border));
                            password.setTextColor(0xFFFFFFFF);
                            password.setText(temp.getString("password"));
                            Log.i("Check3","parsing the json");
                            row.addView(id);
                            row.addView(name);
                            row.addView(email);
                            row.addView(password);
                            //add the new row to table

                            tl.addView(row,new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT));




                            //////


                            emailEditText.setText(temp.getString("email"));
                            Log.d("json email:",temp.getString("email"));
                            Toast.makeText(getApplicationContext(), "HTTP POST is working...", Toast.LENGTH_LONG).show();
                            Intent k = new Intent(MainActivity.this, Hello.class);
                            k.putExtra("username",temp.getString("usern"));
                            startActivity(k);
                            break;
                         default:
                             Toast.makeText(getApplicationContext(), "Invalid POST req...", Toast.LENGTH_LONG).show();


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(givenUsername, givenPassword);
    }
}
