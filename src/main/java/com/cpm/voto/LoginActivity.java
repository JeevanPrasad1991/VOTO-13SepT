package com.cpm.voto;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import java.net.MalformedURLException;

import java.util.Calendar;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;


import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.Constants.CommonString;
import com.cpm.Get_IMEI_number.ImeiNumberClass;
import com.cpm.autoupdate.AutoupdateActivity;

import com.cpm.database.GSKDatabase;
import com.cpm.delegates.TableBean;
import com.cpm.message.AlertMessage;
import com.cpm.xmlGetterSetter.FailureGetterSetter;
import com.cpm.xmlGetterSetter.LoginGetterSetter;
import com.cpm.xmlGetterSetter.QuestionGetterSetter;
import com.cpm.xmlHandler.XMLHandlers;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import static com.cpm.voto.R.layout.main;

public class LoginActivity extends Activity implements OnClickListener {
    private EditText mUsername, mPassword;
    private Button mLogin;
    private String username, password, p_username, p_password;
    private String latitude = "0.0", longitude = "0.0";
    private int versionCode;
    private boolean isChecked;
    private SharedPreferences preferences = null;
    private SharedPreferences.Editor editor = null;
    private Intent intent = null;
    GSKDatabase database;
    static int counter = 1;
    private QuestionGetterSetter questionGetterSetter;
    String app_ver;
    int eventType;
    String right_answer, rigth_answer_cd = "", qns_cd, ans_cd;
    LoginGetterSetter lgs = null;
    String imei1 = "", imei2 = "";
    private GoogleApiClient googleApiClient;
    private static final int REQUEST_LOCATION = 1;
    TelephonyManager telephonyManager = null;
    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 999;
    String[] imeiNumbers;
    ImeiNumberClass imei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(main);
        ContentResolver.setMasterSyncAutomatically(false);
        mUsername = (EditText) findViewById(R.id.login_usertextbox);
        mPassword = (EditText) findViewById(R.id.login_locktextbox);
       /* mUsername.setText("testmer");
        mPassword.setText("cpm123");*/
        mLogin = (Button) findViewById(R.id.login_loginbtn);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        p_username = preferences.getString(CommonString.KEY_USERNAME, null);
        p_password = preferences.getString(CommonString.KEY_PASSWORD, null);
        isChecked = preferences.getBoolean(CommonString.KEY_REMEMBER, false);
        TextView tv_version = (TextView) findViewById(R.id.tv_version_code);
        try {
            app_ver = String.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        tv_version.setText("Version " + app_ver);
        database = new GSKDatabase(this);
        if (!isChecked) {
        } else {
            mUsername.setText(p_username);
            mPassword.setText(p_password);
        }
        mLogin.setOnClickListener(this);
        telephonyManager = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        // Create a Folder for Images
        File file = new File(Environment.getExternalStorageDirectory(), "VotoImages");
        if (!file.isDirectory()) {
            file.mkdir();
        }
        imei = new ImeiNumberClass(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSIONS_REQUEST_READ_PHONE_STATE);
        } else {
            imeiNumbers = imei.getDeviceImei();
            if (imeiNumbers.length==2){
                imei1 = imeiNumbers[0];
                imei2 = imeiNumbers[1];
            }else {
                imei1 = imeiNumbers[0];
                imei2="";
            }

        }
    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        username = mUsername.getText().toString().trim();
        password = mPassword.getText().toString().trim();
        switch (v.getId()) {
            case R.id.login_loginbtn:
                if (username.length() == 0) {
                    showToast("Please enter username");
                } else if (password.length() == 0) {
                    showToast("Please enter password");
                } else {
                    p_username = preferences.getString(CommonString.KEY_USERNAME, null);
                    p_password = preferences.getString(CommonString.KEY_PASSWORD, null);
                    // If no preferences are stored
                    if (p_username == null && p_password == null) {
                        if (CheckNetAvailability()) {
                            if (checkgpsEnableDevice()) {
                                new AuthenticateTask().execute();
                            }
                        } else {
                            showToast("No Network and first login");
                        }
                    }
                    // If preferences are stored
                    else {
                        if (username.equals(p_username)) {
                            if (CheckNetAvailability()) {
                                if (checkgpsEnableDevice()) {
                                    new AuthenticateTask().execute();
                                }
                            } else if (password.equals(p_password)) {
                                intent = new Intent(this, MainMenuActivity.class);
                                startActivity(intent);
                                this.finish();
                                showToast("No Network and offline login");
                            } else {
                                showToast("Incorrect Password");
                            }
                        } else {
                            showToast("Incorrect Username");
                        }
                    }
                }
                break;
        }
    }

    private class AuthenticateTask extends AsyncTask<Void, Void, String> {
        private ProgressDialog dialog = null;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = new ProgressDialog(LoginActivity.this);
            dialog.setTitle("Login");
            dialog.setMessage("Authenticating....");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                String resultHttp = "";

                versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
                String userauth_xml = "[DATA]" + "[USER_DATA][USER_ID]"
                        + username + "[/USER_ID]" + "[Password]" + password
                        + "[/Password]" + "[IN_TIME]" + getCurrentTime()
                        + "[/IN_TIME]" + "[LATITUDE]" + latitude
                        + "[/LATITUDE]" + "[LONGITUDE]" + longitude
                        + "[/LONGITUDE]" + "[APP_VERSION]" + app_ver
                        + "[/APP_VERSION]" + "[ATT_MODE]OnLine[/ATT_MODE]"
                        + "[IMEI1]" + imei1 + "[/IMEI1]"
                        + "[IMEI2]" + imei2 + "[/IMEI2]"
                        + "[/USER_DATA][/DATA]";

                SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_LOGIN);
                request.addProperty("onXML", userauth_xml);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);
                androidHttpTransport.call(CommonString.SOAP_ACTION_LOGIN, envelope);
                Object result = (Object) envelope.getResponse();
                if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                    final AlertMessage message = new AlertMessage(LoginActivity.this, AlertMessage.MESSAGE_FAILURE, "login", null);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            message.showMessage();
                        }
                    });

                } else if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {

                    final AlertMessage message = new AlertMessage(LoginActivity.this, AlertMessage.MESSAGE_FALSE, "login", null);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            message.showMessage();
                        }
                    });

                } else if (result.toString().equalsIgnoreCase(CommonString.KEY_CHANGED)) {
                    final AlertMessage message = new AlertMessage(LoginActivity.this, AlertMessage.MESSAGE_CHANGED, "login", null);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            message.showMessage();
                        }
                    });

                } else {
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    FailureGetterSetter failureGetterSetter = XMLHandlers.failureXMLHandler(xpp, eventType);
                    if (failureGetterSetter.getStatus().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                        final AlertMessage message = new AlertMessage(LoginActivity.this, CommonString.METHOD_LOGIN
                                + failureGetterSetter.getErrorMsg(), "login", null);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                message.showMessage();
                            }
                        });
                    } else {
                        try {
                            // For String source
                            xpp.setInput(new StringReader(result.toString()));
                            xpp.next();
                            eventType = xpp.getEventType();
                            lgs = XMLHandlers.loginXMLHandler(xpp, eventType);
                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // PUT IN PREFERENCES
                        editor.putString(CommonString.KEY_USERNAME, username);
                        editor.putString(CommonString.KEY_PASSWORD, password);
                        editor.putString(CommonString.KEY_VERSION, lgs.getVERSION());
                        editor.putString(CommonString.KEY_PATH, lgs.getPATH());
                        editor.putString(CommonString.KEY_DATE, lgs.getDATE());
                        editor.putString(CommonString.KEY_USER_TYPE, lgs.getRIGHTNAME());
                        editor.commit();


                    }
                    //Question download
                    request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                    request.addProperty("UserName", username);
                    request.addProperty("Type", "TODAY_QUESTION");
                    envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    androidHttpTransport = new HttpTransportSE(CommonString.URL);

                    androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                    result = (Object) envelope.getResponse();
                    if (result.toString() != null) {
                        xpp.setInput(new StringReader(result.toString()));
                        xpp.next();
                        eventType = xpp.getEventType();
                        questionGetterSetter = XMLHandlers.QuestionXMLHandler(xpp, eventType);
                        if (questionGetterSetter.getQuestion_cd().size() > 0) {
                            resultHttp = CommonString.KEY_SUCCESS;
                            String qnsTable = questionGetterSetter.getTable_question_today();
                            TableBean.setQuestiontable(qnsTable);
                        } else {
                            return CommonString.KEY_SUCCESS;
                        }

                    }
                    return resultHttp;

                }
                return "";

            } catch (MalformedURLException e) {

                final AlertMessage message = new AlertMessage(
                        LoginActivity.this, AlertMessage.MESSAGE_EXCEPTION,
                        "acra_login", e);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        message.showMessage();
                    }
                });

            } catch (IOException e) {
                final AlertMessage message = new AlertMessage(
                        LoginActivity.this,
                        AlertMessage.MESSAGE_SOCKETEXCEPTION, "socket_login", e);

                counter++;
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        if (counter < 3) {
                            new AuthenticateTask().execute();
                        } else {
                            message.showMessage();
                            counter = 1;
                        }
                    }
                });
            } catch (Exception e) {
                final AlertMessage message = new AlertMessage(
                        LoginActivity.this, AlertMessage.MESSAGE_EXCEPTION,
                        "acra_login", e);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        message.showMessage();
                    }
                });
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            dialog.dismiss();
            if (result.equals(CommonString.KEY_SUCCESS)) {
                if (preferences.getString(CommonString.KEY_VERSION, "").equals(Integer.toString(versionCode))) {
                    String visit_date = preferences.getString(CommonString.KEY_DATE, "");
                    if (questionGetterSetter.getAnswer_cd().size() > 0 && questionGetterSetter.getStatus().get(0).equals("N") &&
                            !preferences.getBoolean(CommonString.KEY_IS_QUIZ_DONE + visit_date, false)) {
                        for (int i = 0; i < questionGetterSetter.getRight_answer().size(); i++) {
                            if (questionGetterSetter.getRight_answer().get(i).equals("1")) {
                                right_answer = questionGetterSetter.getAnswer().get(i);
                                rigth_answer_cd = questionGetterSetter.getAnswer_cd().get(i);
                                break;
                            }
                        }
                        final AnswerData answerData = new AnswerData();
                        final Dialog customD = new Dialog(LoginActivity.this);
                        customD.setTitle("Todays Question");
                        customD.setCancelable(false);
                        customD.setContentView(R.layout.show_answer_layout);
                        customD.setContentView(R.layout.todays_question_layout);
                        ((TextView) customD.findViewById(R.id.tv_qns)).setText(questionGetterSetter.getQuestion().get(0));
                        Button btnsubmit = (Button) customD.findViewById(R.id.btnsubmit);
                        final TextView txt_timer = (TextView) customD.findViewById(R.id.txt_timer);
                        RadioGroup radioGroup = (RadioGroup) customD.findViewById(R.id.radiogrp);
                        new CountDownTimer(30000, 1000) {
                            public void onTick(long millisUntilFinished) {
                                txt_timer.setText("seconds remaining: " + millisUntilFinished / 1000);
                                //here you can have your logic to set text to edittext
                            }

                            public void onFinish() {
                                if (answerData.getAnswer_id() == null || answerData.getAnswer_id().equals("")) {
                                    txt_timer.setText("done!");
                                    customD.cancel();
                                    String ansisright = "";
                                    ansisright = "Your Time is over";
                                    final Dialog ans_dialog = new Dialog(LoginActivity.this);
                                    ans_dialog.setTitle("Answer");
                                    ans_dialog.setCancelable(false);
                                    //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    ans_dialog.setContentView(R.layout.show_answer_layout);
                                    ((TextView) ans_dialog.findViewById(R.id.tv_ans)).setText(ansisright);
                                    Button btnok = (Button) ans_dialog.findViewById(R.id.btnsubmit);
                                    btnok.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            answerData.setQuestion_id(questionGetterSetter.getQuestion_cd().get(0));
                                            answerData.setUsername(username);
                                            answerData.setVisit_date(lgs.getDATE());
                                            if (CheckNetAvailability()) {
                                                ans_dialog.cancel();
                                                new AnswerTodayTask().execute(answerData);
                                            } else {
                                                showToast("No internet connection");
                                            }
                                        }
                                    });
                                    ans_dialog.show();
                                }
                            }
                        }.start();

                        for (int i = 0; i < questionGetterSetter.getAnswer_cd().size(); i++) {
                            RadioButton rdbtn = new RadioButton(LoginActivity.this);
                            rdbtn.setId(i);
                            rdbtn.setText(questionGetterSetter.getAnswer().get(i));
                            radioGroup.addView(rdbtn);
                        }

                        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {

                                answerData.setAnswer_id(questionGetterSetter.getAnswer_cd().get(checkedId));
                                answerData.setRight_answer(questionGetterSetter.getRight_answer().get(checkedId));


                            }
                        });

                        btnsubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (answerData.getAnswer_id() == null || answerData.getAnswer_id().equals("")) {
                                    Snackbar.make(mLogin, "First select an answer", Snackbar.LENGTH_SHORT).show();
                                } else {
                                    customD.cancel();
                                    String ansisright = "";
                                    if (answerData.getRight_answer().equals("1")) {
                                        ansisright = "Your Answer Is Right!";
                                    } else {
                                        ansisright = "Your Answer is Wrong! Right Answer Is :- " + right_answer;
                                    }
                                    final Dialog ans_dialog = new Dialog(LoginActivity.this);
                                    ans_dialog.setTitle("Answer");
                                    ans_dialog.setCancelable(false);
                                    ans_dialog.setContentView(R.layout.show_answer_layout);
                                    ((TextView) ans_dialog.findViewById(R.id.tv_ans)).setText(ansisright);
                                    Button btnok = (Button) ans_dialog.findViewById(R.id.btnsubmit);
                                    btnok.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            answerData.setQuestion_id(questionGetterSetter.getQuestion_cd().get(0));
                                            answerData.setUsername(username);
                                            answerData.setVisit_date(lgs.getDATE());
                                            if (CheckNetAvailability()) {
                                                new AnswerTodayTask().execute(answerData);
                                                ans_dialog.cancel();
                                            } else {
                                                showToast("No internet connection");
                                            }
                                        }
                                    });
                                    ans_dialog.show();
                                }
                            }
                        });
                        customD.show();
                    } else {
                        intent = new Intent(getBaseContext(), MainMenuActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    intent = new Intent(getBaseContext(), AutoupdateActivity.class);
                    intent.putExtra(CommonString.KEY_PATH, preferences.getString(CommonString.KEY_PATH, ""));
                    startActivity(intent);
                    finish();
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    public boolean CheckNetAvailability() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            // we are connected to a network
            connected = true;
        }

        return connected;
    }

    private void showToast(String message) {
        Snackbar.make(mLogin, message, Snackbar.LENGTH_LONG).show();
    }

    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        String intime = m_cal.get(Calendar.HOUR_OF_DAY) + ":" + m_cal.get(Calendar.MINUTE) + ":" + m_cal.get(Calendar.SECOND);
        return intime;
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);

    }

    class AnswerData {
        public String question_id, answer_id, username, visit_date, right_answer;

        public String getQuestion_id() {
            return question_id;
        }

        public void setQuestion_id(String question_id) {
            this.question_id = question_id;
        }

        public String getAnswer_id() {
            return answer_id;
        }

        public void setAnswer_id(String answer_id) {
            this.answer_id = answer_id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getVisit_date() {
            return visit_date;
        }

        public void setVisit_date(String visit_date) {
            this.visit_date = visit_date;
        }

        public String getRight_answer() {
            return right_answer;
        }

        public void setRight_answer(String right_answer) {
            this.right_answer = right_answer;
        }
    }

    class AnswerTodayTask extends AsyncTask<AnswerData, Void, String> {
        private ProgressDialog dialog = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(LoginActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setTitle("Todays Question");
            dialog.setMessage("Submitting Answer..");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(AnswerData... params) {

            try {
                AnswerData answerData = params[0];
                if (answerData.getAnswer_id() == null) {
                    answerData.setAnswer_id("0");
                }
                String resultHttp = "";
                versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;

                qns_cd = answerData.getQuestion_id();
                ans_cd = answerData.getAnswer_id();

                String userauth_xml = "[DATA]" + "[TODAY_ANSWER][USER_ID]"
                        + answerData.getUsername() + "[/USER_ID]" + "[QUESTION_ID]" + answerData.getQuestion_id()
                        + "[/QUESTION_ID]" + "[ANSWER_ID]" + answerData.getAnswer_id()
                        + "[/ANSWER_ID]" + "[VISIT_DATE]" + answerData.getVisit_date()
                        + "[/VISIT_DATE]"
                        + "[/TODAY_ANSWER][/DATA]";
                SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_XML);
                request.addProperty("XMLDATA", userauth_xml);
                request.addProperty("KEYS", "TODAYS_ANSWER");
                request.addProperty("USERNAME", answerData.getUsername());
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);
                androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_XML, envelope);
                Object result = (Object) envelope.getResponse();
                if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                } else if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                } else {
                    String visit_date = preferences.getString(CommonString.KEY_DATE, null);
                    editor.putBoolean(CommonString.KEY_IS_QUIZ_DONE + visit_date, true);
                    editor.commit();
                    return CommonString.KEY_SUCCESS;
                }

                return "";

            } catch (MalformedURLException e) {

                final AlertMessage message = new AlertMessage(
                        LoginActivity.this, AlertMessage.MESSAGE_EXCEPTION,
                        "acra_login", e);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        message.showMessage();
                    }
                });

            } catch (IOException e) {
                final AlertMessage message = new AlertMessage(
                        LoginActivity.this,
                        AlertMessage.MESSAGE_SOCKETEXCEPTION, "socket_login", e);

                counter++;
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        if (counter < 3) {
                            new AuthenticateTask().execute();
                        } else {
                            message.showMessage();
                            counter = 1;
                        }
                    }
                });
            } catch (Exception e) {
                final AlertMessage message = new AlertMessage(
                        LoginActivity.this, AlertMessage.MESSAGE_EXCEPTION,
                        "acra_login", e);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        message.showMessage();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            super.onPostExecute(result);
            if (result.equals(CommonString.KEY_SUCCESS)) {
                intent = new Intent(getBaseContext(), MainMenuActivity.class);
                startActivity(intent);
                finish();
            } else {
                //Save question cd and ans cd here for upload
                String visit_date = preferences.getString(CommonString.KEY_DATE, null);
                editor.putString(CommonString.KEY_QUESTION_CD + visit_date, qns_cd);
                editor.putString(CommonString.KEY_ANSWER_CD + visit_date, ans_cd);
                editor.commit();
                intent = new Intent(getBaseContext(), MainMenuActivity.class);
                startActivity(intent);
                finish();
            }

        }

    }

    private boolean checkgpsEnableDevice() {
        boolean flag = true;
        if (!hasGPSDevice(LoginActivity.this)) {
            Toast.makeText(LoginActivity.this, "Gps not Supported", Toast.LENGTH_SHORT).show();
        }
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(LoginActivity.this)) {
            enableLoc();
            flag = false;
        } else if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(LoginActivity.this)) {
            flag = true;
        }
        return flag;
    }

    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {
                            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                            if (mLastLocation != null) {
                                latitude = String.valueOf(mLastLocation.getLatitude());
                                longitude = String.valueOf(mLastLocation.getLongitude());
                            }
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(LoginActivity.this, REQUEST_LOCATION);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_CANCELED: {
                        finish();
                    }

                    default: {
                        break;
                    }
                }
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            imeiNumbers = imei.getDeviceImei();
        }
    }


    public String getIMEI(Activity activity) {
        TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

}
