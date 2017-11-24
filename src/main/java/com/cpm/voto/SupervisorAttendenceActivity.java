package com.cpm.voto;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialcamera.MaterialCamera;
import com.cpm.Constants.CommonString;
import com.cpm.dailyentry.SupervisorDailyEntry;
import com.cpm.database.GSKDatabase;
import com.cpm.download.CompleteDownloadActivity;
import com.cpm.message.AlertMessage;
import com.cpm.retrofit.RetrofitClass;
import com.cpm.upload.CheckoutNUpload;
import com.cpm.upload.UploadDataActivity;
import com.cpm.xmlGetterSetter.JourneyPlanGetterSetter;
import com.cpm.xmlGetterSetter.NonWorkingReasonGetterSetter;
import com.cpm.xmlGetterSetter.SupervisorAttendenceGetterSetter;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by jeevanp on 13-11-2017.
 */

public class SupervisorAttendenceActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    ArrayList<NonWorkingReasonGetterSetter> reasondata = new ArrayList<>();
    private ArrayAdapter<CharSequence> reason_adapter;
    SupervisorAttendenceGetterSetter supervisorAttendenceGetterSetter;
    ArrayList<JourneyPlanGetterSetter> jcpList = new ArrayList<>();
    private SharedPreferences.Editor editor = null;
    String _UserId, visit_date, user_type;
    private SharedPreferences preferences;
    LinearLayout layoutsup_u;
    LinearLayout  no_data_lay;
    String reasonname, reasonid, entry_allow, SUPERVISOR_JCP_TYPE, image;
    private Spinner reasonspinner;
    private GSKDatabase database;
    FloatingActionButton fab, download_fab;
    ImageButton camera;
    File saveDir = null;
    private final static int CAMERA_RQ = 6969;
    RelativeLayout rel_cam;
    protected String _path;
    protected String _pathforcheck = "";
    private String image1 = "";
    Data data;
    private TextView percentage, message;
    String datacheck = "", app_ver;
    String[] words;
    String validity;
    String mid;
    private Dialog dialog;
    private ProgressBar pb;
    boolean uploadflag = true;
    String remark;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.supervisorattentence_act);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        reasonspinner = (Spinner) findViewById(R.id.supAtten_spinner);
//        supervisor_remark = (EditText) findViewById(R.id.supervisor_remark);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        layoutsup_u = (LinearLayout) findViewById(R.id.layoutsup_u);
//        layout_reason = (LinearLayout) findViewById(R.id.layout_reason);
        download_fab = (FloatingActionButton) findViewById(R.id.download_fab);
        no_data_lay = (LinearLayout) findViewById(R.id.no_data_lay);
        camera = (ImageButton) findViewById(R.id.imgcam);
        rel_cam = (RelativeLayout) findViewById(R.id.relimgcam_1);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        _UserId = preferences.getString(CommonString.KEY_USERNAME, null);
        user_type = preferences.getString(CommonString.KEY_USER_TYPE, null);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        SUPERVISOR_JCP_TYPE = preferences.getString(CommonString.KEY_SUPERVISOR_JCP_TYPE, null);

        database = new GSKDatabase(this);
        database.open();
        reasondata = database.getNonWorkingDataforAttendence();
        reason_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        reason_adapter.add("-Select Reason-");
        for (int i = 0; i < reasondata.size(); i++) {
            reason_adapter.add(reasondata.get(i).getReason().get(0));
        }
        reasonspinner.setAdapter(reason_adapter);
        getSupportActionBar().setTitle("Attendence -" + visit_date);
        reason_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reasonspinner.setOnItemSelectedListener(this);
        download_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkNetIsAvailable()) {
                    if (database.isCoverageDataFilled(visit_date)) {
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SupervisorAttendenceActivity.this);
                        builder.setTitle("Parinaam");
                        builder.setMessage("Please Upload Previous Data First")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent startUpload = new Intent(SupervisorAttendenceActivity.this, CheckoutNUpload.class);
                                        startActivity(startUpload);
                                        finish();

                                    }
                                });
                        android.app.AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        try {
                            database.open();
                            database.deletePreviousUploadedData(visit_date);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Intent startDownload = new Intent(getApplicationContext(), CompleteDownloadActivity.class);
                        startActivity(startDownload);
                        finish();
                    }
                } else {
                    Snackbar.make(download_fab, "No Network Available", Snackbar.LENGTH_SHORT).setAction("Action", null).show();

                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SupervisorAttendenceActivity.this);
                    builder.setTitle("Parinaam").setMessage("Do you want to save data");
                    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            database.open();
                            if (entry_allow.equals("0")) {
                             //   remark = supervisor_remark.getText().toString().replaceAll("[&+!^?*#:<>{}'%$]", "");
                                database.insertsupervisorattendencedata(_UserId, visit_date, "",
                                        reasonname, reasonid, entry_allow, image1);
                                new UploadingTask(SupervisorAttendenceActivity.this).execute();
                            } else {
                              //  remark = supervisor_remark.getText().toString().replaceAll("[&+!^?*#:<>{}'%$]", "");
                                database.insertsupervisorattendencedata(_UserId, visit_date, "",
                                        reasonname, reasonid, entry_allow, image1);
                                Intent intent = new Intent(SupervisorAttendenceActivity.this, SupervisorDailyEntry.class);
                                startActivity(intent);
                                finish();
                            }

                        }
                    });
                    builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }

            }
        });
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            saveDir = new File(CommonString.FILE_PATH);
            saveDir.mkdirs();
        }
        camera.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        database.open();
        jcpList = database.getJCPData(visit_date);
        if (SUPERVISOR_JCP_TYPE.equalsIgnoreCase("JCP")) {
            if (jcpList.size() > 0 && reasondata.size() > 0) {
                layoutsup_u.setVisibility(View.VISIBLE);
               // layout_reason.setVisibility(View.VISIBLE);
                fab.setVisibility(View.VISIBLE);
                download_fab.setVisibility(View.GONE);
                no_data_lay.setVisibility(View.GONE);
            }
        } else {
            if (!database.isSupAttendenceDataFilled(visit_date) && reasondata.size() > 0) {
                layoutsup_u.setVisibility(View.VISIBLE);
               // layout_reason.setVisibility(View.VISIBLE);
                fab.setVisibility(View.VISIBLE);
                download_fab.setVisibility(View.GONE);
                no_data_lay.setVisibility(View.GONE);
            }
/*
            if (reasondata.size() > 0) {
                layoutsup_u.setVisibility(View.VISIBLE);
                layout_reason.setVisibility(View.VISIBLE);
                fab.setVisibility(View.VISIBLE);
                download_fab.setVisibility(View.GONE);
                no_data_lay.setVisibility(View.GONE);
            }
*/
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.supAtten_spinner:
                if (position != 0) {
                    reasonname = reasondata.get(position - 1).getReason().get(0);
                    reasonid = reasondata.get(position - 1).getReason_cd().get(0);
                    entry_allow = reasondata.get(position - 1).getEntry_allow().get(0);

                    if (entry_allow.equals("1")) {
                        rel_cam.setVisibility(View.VISIBLE);
                    } else {
                        rel_cam.setVisibility(View.GONE);
                    }
                }
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        this.finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    private boolean validate() {
        boolean result = true;
        if (reasonspinner.getSelectedItem().toString().equalsIgnoreCase("-Select Reason-")) {
            message("Please select a reason");
            result = false;
        } /*else if (supervisor_remark.getText().toString().isEmpty()) {
            message("Please enter a remark");
            result = false;
        }*/ else if (entry_allow.equals("1")) {
            if (image1.equalsIgnoreCase("")) {
                message("Please Capture image");
                result = false;
            }
        }

        return result;
    }

    private void message(String message) {
        Snackbar.make(fab, message, Snackbar.LENGTH_SHORT).show();
    }

    public boolean checkNetIsAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("MakeMachine", "resultCode: " + resultCode);
        switch (resultCode) {
            case 0:
                Log.i("MakeMachine", "User cancelled");
                break;

            case -1:
                if (requestCode == CAMERA_RQ) {
                    if (resultCode == RESULT_OK) {
                        File file = new File(data.getData().getPath());
                        _pathforcheck = file.getName();
                        camera.setImageDrawable(getResources().getDrawable(R.drawable.camera_list_tick));
                        image1 = _pathforcheck;
                    }
                }

                break;
        }
    }


    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        String hour_str = String.valueOf(m_cal.get(Calendar.HOUR_OF_DAY));
        hour_str = "00" + hour_str;
        hour_str = hour_str.substring(hour_str.length() - 2, hour_str.length());
        String minute_str = String.valueOf(m_cal.get(Calendar.MINUTE));
        minute_str = "00" + minute_str;
        minute_str = minute_str.substring(minute_str.length() - 2, minute_str.length());
        String second_str = String.valueOf(m_cal.get(Calendar.SECOND));
        second_str = "00" + second_str;
        second_str = second_str.substring(second_str.length() - 2, second_str.length());
        String intime = hour_str + ":" + minute_str + ":" + second_str;
        return intime;
    }

    @Override
    public void onClick(View v) {
        MaterialCamera materialCamera = new MaterialCamera(this)
                .saveDir(saveDir)
                .showPortraitWarning(true)
                .allowRetry(true)
                .defaultToFrontFacing(false)
                .allowRetry(true)
                .autoSubmit(false)
                .labelConfirm(R.string.mcam_use_video);
        if (v.getId() == R.id.imgcam) {
            _pathforcheck = "_SUP_ATTENDENCE_" + visit_date.replace("/", "") + "_" + getCurrentTime().replace(":", "") + ".jpg";
            editor = preferences.edit();
            editor.putString("imagename", _pathforcheck);
            editor.commit();
            materialCamera.stillShot().labelConfirm(R.string.mcam_use_stillshot);
            materialCamera.start(CAMERA_RQ);
        }
    }


    class Data {
        int value;
        String name;
    }

    private class UploadingTask extends AsyncTask<Void, Data, String> {
        private Context context;

        UploadingTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom_upload);
            dialog.setTitle("Sending Sup Attendence Data");
            dialog.setCancelable(false);
            dialog.show();
            pb = (ProgressBar) dialog.findViewById(R.id.progressBar1);
            percentage = (TextView) dialog.findViewById(R.id.percentage);
            message = (TextView) dialog.findViewById(R.id.message);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String onXML = "", final_xml = "";
                jcpList = database.getJCPData(visit_date);
                data = new Data();
                data.value = 20;
                data.name = "Uploading coverage data......";
                publishProgress(data);
                if (SUPERVISOR_JCP_TYPE.equalsIgnoreCase("JCP")) {
                    if (jcpList.size() > 0) {
                        for (int i = 0; i < jcpList.size(); i++) {
                            onXML = "[DATA][USER_DATA][STORE_CD]"
                                    + jcpList.get(i).getStore_cd().get(0)
                                    + "[/STORE_CD]" + "[VISIT_DATE]"
                                    + jcpList.get(i).getVISIT_DATE().get(0)
                                    + "[/VISIT_DATE][LATITUDE]"
                                    + "0.0"
                                    + "[/LATITUDE][APP_VERSION]"
                                    + app_ver
                                    + "[/APP_VERSION][LONGITUDE]"
                                    + "0.0"
                                    + "[/LONGITUDE][IN_TIME]"
                                    + getCurrentTime()
                                    + "[/IN_TIME][OUT_TIME]"
                                    + getCurrentTime()
                                    + "[/OUT_TIME][UPLOAD_STATUS]"
                                    + "N"
                                    + "[/UPLOAD_STATUS][USER_ID]"
                                    + _UserId
                                    + "[/USER_ID]" +
                                    "[IMAGE_URL]"
                                    + image1
                                    + "[/IMAGE_URL]"
                                    +
                                    "[IMAGE_URL1]"
                                    + image1
                                    + "[/IMAGE_URL1]"
                                    + "[REASON_ID]"
                                    + reasonid
                                    + "[/REASON_ID]" +
                                    "[REASON_REMARK]"
                                    + remark
                                    + "[/REASON_REMARK][/USER_DATA][/DATA]";

                            SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_DR_STORE_COVERAGE);
                            request.addProperty("onXML", onXML);
                            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                            envelope.dotNet = true;
                            envelope.setOutputSoapObject(request);
                            HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);
                            androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_DR_STORE_COVERAGE, envelope);
                            Object result = (Object) envelope.getResponse();
                            datacheck = result.toString();
                            datacheck = datacheck.replace("\"", "");
                            words = datacheck.split("\\;");
                            validity = (words[0]);
                            if (validity.equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                            } else {
                                return CommonString.METHOD_UPLOAD_DR_STORE_COVERAGE;
                            }
                            mid = words[1];
                            data.value = 30;
                            data.name = "Uploaded coverage data";
                            publishProgress(data);

                            //attendance
                            onXML = "";
                            final_xml = "";
                            supervisorAttendenceGetterSetter = database.getsupervisorAttendenceData(visit_date);
                            if (supervisorAttendenceGetterSetter!=null && supervisorAttendenceGetterSetter.getReason_cd() != null) {
                                if (!supervisorAttendenceGetterSetter.getReason_cd().equals("")) {
                                    if (supervisorAttendenceGetterSetter.getStatus().equalsIgnoreCase("D")) {
                                        onXML = "[SUP_ATTENDENCE_DATA]"
                                                + "[CREATED_BY]"
                                                + _UserId
                                                + "[/CREATED_BY]"
                                                + "[REASON_CD]"
                                                + supervisorAttendenceGetterSetter.getReason_cd()
                                                + "[/REASON_CD]"

                                                + "[IMAGE]"
                                                + supervisorAttendenceGetterSetter.getImage()
                                                + "[/IMAGE]"

                                               /* + "[REMARK]"
                                                + supervisorAttendenceGetterSetter.getRemark()
                                                + "[/REMARK]"*/
                                                + "[/SUP_ATTENDENCE_DATA]";
                                        final_xml = final_xml + onXML;

                                        final String sos_xml = "[DATA]" + final_xml + "[/DATA]";
                                        request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_XML);
                                        request.addProperty("XMLDATA", sos_xml);
                                        request.addProperty("KEYS", "SUP_ATTENDENCE_DATA");
                                        request.addProperty("USERNAME", _UserId);
                                        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                                        envelope.dotNet = true;
                                        envelope.setOutputSoapObject(request);
                                        androidHttpTransport = new HttpTransportSE(CommonString.URL);
                                        androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_XML, envelope);
                                        result = (Object) envelope.getResponse();
                                        if (result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                            database.updateSupAttendenceStatus(visit_date, CommonString.KEY_U);
                                        }
                                        data.value = 66;
                                        data.name = "Sup Attendence data";
                                        publishProgress(data);
                                    }
                                }

                            }


                            File dir = new File(CommonString.FILE_PATH);
                            ArrayList<String> list = new ArrayList();
                            list = getFileNames(dir.listFiles());
                            if (list.size() > 0) {
                                for (int i1 = 0; i1 < list.size(); i1++) {
                                    if (list.get(i1).contains("_SUP_ATTENDENCE_")) {
                                        File originalFile = new File(CommonString.FILE_PATH + list.get(i1));
                                        result = RetrofitClass.UploadImageByRetrofit(SupervisorAttendenceActivity.this,
                                                originalFile.getName(), "StoreImages");
                                        if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                            return result.toString();
                                        }
                                    }
                                }
                                data.value = 100;
                                data.name = "Sup Attendence Images";
                                publishProgress(data);
                            }


                            // SET COVERAGE STATUS
                            final_xml = "";
                            onXML = "";
                            onXML = "[COVERAGE_STATUS][STORE_ID]"
                                    + jcpList.get(i).getStore_cd().get(0)
                                    + "[/STORE_ID]"
                                    + "[VISIT_DATE]"
                                    + jcpList.get(i).getVISIT_DATE().get(0)
                                    + "[/VISIT_DATE]"
                                    + "[USER_ID]"
                                    + _UserId
                                    + "[/USER_ID]"
                                    + "[STATUS]"
                                    + CommonString.KEY_U
                                    + "[/STATUS]"
                                    + "[/COVERAGE_STATUS]";

                            final_xml = final_xml + onXML;
                            final String sos_xml = "[DATA]" + final_xml + "[/DATA]";
                            request = new SoapObject(CommonString.NAMESPACE, CommonString.MEHTOD_UPLOAD_COVERAGE_STATUS);
                            request.addProperty("onXML", sos_xml);
                            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                            envelope.dotNet = true;
                            envelope.setOutputSoapObject(request);
                            androidHttpTransport = new HttpTransportSE(CommonString.URL);
                            androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.MEHTOD_UPLOAD_COVERAGE_STATUS, envelope);
                            result = (Object) envelope.getResponse();
                            if (result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                database.open();
                                // database.updateCoverageStatus(coverageBeanlist.get(i).getMID(), CommonString.KEY_U);
                                database.updateStoreStatusOnLeave(jcpList.get(i).getStore_cd().get(0), jcpList.get(i).getVISIT_DATE().get(0), CommonString.KEY_U);
                            } else {
                                return CommonString.MEHTOD_UPLOAD_COVERAGE_STATUS;
                            }
                        }
                        return CommonString.KEY_SUCCESS;
                    }
                } else {
                    //attendance
                    onXML = "";
                    final_xml = "";
                    supervisorAttendenceGetterSetter = database.getsupervisorAttendenceData(visit_date);
                    if (supervisorAttendenceGetterSetter!=null && supervisorAttendenceGetterSetter.getReason_cd() != null) {
                        if (!supervisorAttendenceGetterSetter.getReason_cd().equals("")) {
                            if (supervisorAttendenceGetterSetter.getStatus().equalsIgnoreCase("D")) {
                                onXML = "[SUP_ATTENDENCE_DATA]"
                                        + "[CREATED_BY]"
                                        + _UserId
                                        + "[/CREATED_BY]"
                                        + "[REASON_CD]"
                                        + supervisorAttendenceGetterSetter.getReason_cd()
                                        + "[/REASON_CD]"

                                        + "[IMAGE]"
                                        + supervisorAttendenceGetterSetter.getImage()
                                        + "[/IMAGE]"

                                        + "[VISIT_DATE]"
                                        + visit_date
                                        + "[/VISIT_DATE]"

                                      /*  + "[REMARK]"
                                        + supervisorAttendenceGetterSetter.getRemark()
                                        + "[/REMARK]"*/
                                        + "[/SUP_ATTENDENCE_DATA]";
                                final_xml = final_xml + onXML;

                                final String sos_xml = "[DATA]" + final_xml + "[/DATA]";
                                SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_XML);
                                request.addProperty("XMLDATA", sos_xml);
                                request.addProperty("KEYS", "SUP_ATTENDENCE_DATA");
                                request.addProperty("USERNAME", _UserId);
                                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                                envelope.dotNet = true;
                                envelope.setOutputSoapObject(request);
                                HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);
                                androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_XML, envelope);
                                Object result = (Object) envelope.getResponse();
                                if (result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                    database.updateSupAttendenceStatus(visit_date, CommonString.KEY_U);
                                }
                                data.value = 66;
                                data.name = "Sup Attendence data";
                                publishProgress(data);
                            }
                        }

                        File dir = new File(CommonString.FILE_PATH);
                        ArrayList<String> list = new ArrayList();
                        list = getFileNames(dir.listFiles());
                        if (list.size() > 0) {
                            for (int i1 = 0; i1 < list.size(); i1++) {
                                if (list.get(i1).contains("_SUP_ATTENDENCE_")) {
                                    File originalFile = new File(CommonString.FILE_PATH + list.get(i1));
                                    Object result = RetrofitClass.UploadImageByRetrofit(SupervisorAttendenceActivity.this, originalFile.getName(), "supervisorAttendance");
                                    if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                        return result.toString();
                                    }
                                }
                            }
                            data.value = 100;
                            data.name = "Sup Attendence Images";
                            publishProgress(data);
                            // return CommonString.KEY_SUCCESS;
                        }
                        return CommonString.KEY_SUCCESS;
                    }
                }
            } catch (MalformedURLException e) {
                uploadflag = false;
                return AlertMessage.MESSAGE_EXCEPTION;

            } catch (final IOException e) {
                uploadflag = false;
                return AlertMessage.MESSAGE_SOCKETEXCEPTION;

            } catch (Exception e) {
                uploadflag = false;
                return AlertMessage.MESSAGE_EXCEPTION;

            }
            return "";
        }

        @Override
        protected void onProgressUpdate(Data... values) {
            // TODO Auto-generated method stub
            pb.setProgress(values[0].value);
            percentage.setText(values[0].value + "%");
            message.setText(values[0].name);
        }

        @Override
        protected void onPostExecute(final String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            dialog.dismiss();
            if (uploadflag) {
                if (result.equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                    AlertMessage message = new AlertMessage(SupervisorAttendenceActivity.this, AlertMessage.MESSAGE_UPLOAD_DATA, "success_", null);
                    message.showMessage();
                }
            } else {
                Snackbar.make(fab, AlertMessage.MESSAGE_SOCKETEXCEPTION + "Please try again", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    public ArrayList<String> getFileNames(File[] file) {
        ArrayList<String> arrayFiles = new ArrayList<String>();
        if (file.length > 0) {
            for (int i = 0; i < file.length; i++)
                arrayFiles.add(file[i].getName());
        }
        return arrayFiles;
    }
}
