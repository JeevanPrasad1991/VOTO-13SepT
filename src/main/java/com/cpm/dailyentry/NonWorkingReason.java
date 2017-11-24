package com.cpm.dailyentry;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialcamera.MaterialCamera;
import com.cpm.Constants.CommonString;
import com.cpm.database.GSKDatabase;
import com.cpm.delegates.CoverageBean;
import com.cpm.message.AlertMessage;
import com.cpm.retrofit.RetrofitClass;
import com.cpm.upload.UploadDataActivity;
import com.cpm.voto.R;
import com.cpm.xmlGetterSetter.AditGetterSetter;
import com.cpm.xmlGetterSetter.JourneyPlanGetterSetter;
import com.cpm.xmlGetterSetter.ModelGetterSetter;
import com.cpm.xmlGetterSetter.NonWorkingReasonGetterSetter;
import com.cpm.xmlGetterSetter.SaleEntryGetterSetter;
import com.cpm.xmlGetterSetter.SupervisorAttendenceGetterSetter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class NonWorkingReason extends AppCompatActivity
        implements OnItemSelectedListener, OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    ArrayList<NonWorkingReasonGetterSetter> reasondata = new ArrayList<NonWorkingReasonGetterSetter>();
    private Spinner reasonspinner;
    private GSKDatabase database;
    String reasonname, reasonid, entry_allow, image, reason_reamrk, intime, user_type, SUPERVISOR_JCP_TYPE;
    Button save;
    private ArrayAdapter<CharSequence> reason_adapter;
    ArrayList<SaleEntryGetterSetter> insertedlist_Data = new ArrayList<>();
    ArrayList<ModelGetterSetter> inserted_stock = new ArrayList<>();
    ArrayList<AditGetterSetter> inserted_auditData = new ArrayList<>();
    SupervisorAttendenceGetterSetter supervisorAttendenceGetterSetter;
    protected String _path, str;
    protected String _pathforcheck = "";
    private String image1 = "";
    private SharedPreferences preferences;
    String _UserId, visit_date, store_id, latitude = "0.0", longitude = "0.0";
    protected boolean status = true;
    EditText text;
    AlertDialog alert;
    ImageButton camera;
    private GoogleApiClient googleApiClient = null;
    RelativeLayout reason_lay, rel_cam;
    ArrayList<JourneyPlanGetterSetter> jcp;
    File saveDir = null;
    private final static int CAMERA_RQ = 6969;
    private SharedPreferences.Editor editor = null;
    ArrayList<CoverageBean> coverageList = new ArrayList<>();
    Data data;
    private TextView percentage, message;
    String datacheck = "", app_ver;
    String[] words;
    String validity;
    String mid;
    private Dialog dialog;
    private ProgressBar pb;
    boolean uploadflag = true;
    boolean uploaded_flag = false;
    boolean uploadstatusflag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nonworking);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        reasonspinner = (Spinner) findViewById(R.id.spinner2);
        camera = (ImageButton) findViewById(R.id.imgcam);
        save = (Button) findViewById(R.id.save);
        text = (EditText) findViewById(R.id.reasontxt);
        reason_lay = (RelativeLayout) findViewById(R.id.layout_reason);
        rel_cam = (RelativeLayout) findViewById(R.id.relimgcam);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        _UserId = preferences.getString(CommonString.KEY_USERNAME, "");
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        store_id = preferences.getString(CommonString.KEY_STORE_CD, "");
        app_ver = preferences.getString(CommonString.KEY_VERSION, "");
        user_type = preferences.getString(CommonString.KEY_USER_TYPE, null);
        SUPERVISOR_JCP_TYPE = preferences.getString(CommonString.KEY_SUPERVISOR_JCP_TYPE, null);

        getSupportActionBar().setTitle("Nonworking -" + visit_date);

        database = new GSKDatabase(this);
        database.open();
        jcp = database.getJCPData(visit_date);
        if (jcp.size() > 0) {
            try {
                for (int i = 0; i < jcp.size(); i++) {
                    boolean flag = false;
                    if (jcp.get(i).getUploadStatus().get(0).equals(CommonString.KEY_U)) {
                        flag = true;
                        reasondata.clear();
                        reasondata = database.getNonWorkingData(flag);
                        break;
                    } else {
                        reasondata = database.getNonWorkingData(flag);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        reason_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        reason_adapter.add("-Select Reason-");
        for (int i = 0; i < reasondata.size(); i++) {
            reason_adapter.add(reasondata.get(i).getReason().get(0));
        }
        reasonspinner.setAdapter(reason_adapter);
        reason_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        reasonspinner.setOnItemSelectedListener(this);
        camera.setOnClickListener(this);
        save.setOnClickListener(this);
        str = CommonString.FILE_PATH;
        intime = getCurrentTime();
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!user_type.equalsIgnoreCase("ISD") && !SUPERVISOR_JCP_TYPE.equalsIgnoreCase("JCP")) {
                database.deleteSupervisorJaurneyPlanData(store_id);
                //  database.deleteSpecificStoreData(store_id);
            }
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        if (!user_type.equalsIgnoreCase("ISD") && !SUPERVISOR_JCP_TYPE.equalsIgnoreCase("JCP")) {
            database.deleteSupervisorJaurneyPlanData(store_id);
        }
        this.finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
                               long arg3) {
        // TODO Auto-generated method stub

        switch (arg0.getId()) {
            case R.id.spinner2:
                if (position != 0) {
                    reasonname = reasondata.get(position - 1).getReason().get(0);
                    reasonid = reasondata.get(position - 1).getReason_cd().get(0);
                    entry_allow = reasondata.get(position - 1).getEntry_allow().get(0);
                    if (reasonname.equalsIgnoreCase("Store Closed")) {
                        rel_cam.setVisibility(View.VISIBLE);
                        image = "true";
                    } else {
                        rel_cam.setVisibility(View.GONE);
                        image = "false";
                    }
                    reason_reamrk = "true";
                    if (reason_reamrk.equalsIgnoreCase("true")) {
                        reason_lay.setVisibility(View.VISIBLE);
                    } else {
                        reason_lay.setVisibility(View.GONE);
                    }
                } else {
                    reasonname = "";
                    reasonid = "";
                }
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }


    @SuppressWarnings("deprecation")
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

    public boolean imageAllowed() {
        boolean result = true;
        if (image.equalsIgnoreCase("true")) {
            if (image1.equalsIgnoreCase("")) {
                result = false;
            }
        }
        return result;
    }

    public boolean textAllowed() {
        boolean result = true;
        if (text.getText().toString().trim().equals("")) {
            result = false;
        }
        return result;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        MaterialCamera materialCamera = new MaterialCamera(this)
                .saveDir(saveDir)
                .showPortraitWarning(true)
                .allowRetry(true)
                .defaultToFrontFacing(false)
                .allowRetry(true)
                .autoSubmit(false)
                .labelConfirm(R.string.mcam_use_video);
        if (v.getId() == R.id.imgcam) {
            _pathforcheck = store_id + "_NONWORKING_" + visit_date.replace("/", "") + "_" + getCurrentTime().replace(":", "") + ".jpg";
            editor = preferences.edit();
            editor.putString("imagename", _pathforcheck);
            editor.commit();
            materialCamera.stillShot().labelConfirm(R.string.mcam_use_stillshot);
            materialCamera.start(CAMERA_RQ);
        }
        if (v.getId() == R.id.save) {

            if (validatedata()) {

                if (imageAllowed()) {
                    if (textAllowed()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                NonWorkingReason.this);
                        builder.setMessage("Do you want to save the data ")
                                .setCancelable(false)
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int id) {
                                                alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                                                if (entry_allow.equals("0")) {
                                                    database.deleteAllTables();
                                                    jcp = database.getJCPData(visit_date);
                                                    for (int i = 0; i < jcp.size(); i++) {
                                                        String stoteid = jcp.get(i).getStore_cd().get(0);
                                                        CoverageBean cdata = new CoverageBean();
                                                        cdata.setStoreId(stoteid);
                                                        cdata.setVisitDate(visit_date);
                                                        cdata.setUserId(_UserId);
                                                        cdata.setInTime(intime);
                                                        cdata.setOutTime(getCurrentTime());
                                                        cdata.setReason(reasonname);
                                                        cdata.setReasonid(reasonid);
                                                        cdata.setLatitude(latitude);
                                                        cdata.setLongitude(longitude);
                                                        cdata.setImage(image1);
                                                        cdata.setImage02(image1);
                                                        cdata.setRemark(text.getText().toString().replaceAll("[&^<>{}'$]", " "));
                                                        cdata.setStatus(CommonString.STORE_STATUS_LEAVE);
                                                        database.InsertCoverageData(cdata);
                                                        database.updateStoreStatusOnLeave(store_id, visit_date, CommonString.STORE_STATUS_LEAVE);
                                                        SharedPreferences.Editor editor = preferences.edit();
                                                        editor.putString(CommonString.KEY_STOREVISITED_STATUS + stoteid, "No");
                                                        editor.putString(CommonString.KEY_STOREVISITED_STATUS, "");
                                                        editor.commit();
                                                    }
                                                    database.open();
                                                    coverageList = database.getCoverageData(visit_date);
                                                    new UploadingTask(NonWorkingReason.this, coverageList).execute();
                                                    dialog.dismiss();
                                                } else {
                                                    CoverageBean cdata = new CoverageBean();
                                                    cdata.setStoreId(store_id);
                                                    cdata.setVisitDate(visit_date);
                                                    cdata.setUserId(_UserId);
                                                    cdata.setInTime(intime);
                                                    cdata.setOutTime(getCurrentTime());
                                                    cdata.setReason(reasonname);
                                                    cdata.setReasonid(reasonid);
                                                    cdata.setLatitude(latitude);
                                                    cdata.setLongitude(longitude);
                                                    cdata.setImage(image1);
                                                    cdata.setImage02(image1);
                                                    cdata.setRemark(text.getText().toString().replaceAll("[&^<>{}'$]", " "));
                                                    cdata.setStatus(CommonString.STORE_STATUS_LEAVE);
                                                    database.InsertCoverageData(cdata);
                                                    database.updateStoreStatusOnLeave(store_id, visit_date,
                                                            CommonString.STORE_STATUS_LEAVE);
                                                    SharedPreferences.Editor editor = preferences.edit();
                                                    editor.putString(CommonString.KEY_STOREVISITED_STATUS + store_id, "No");
                                                    editor.putString(CommonString.KEY_STOREVISITED_STATUS, "");
                                                    editor.commit();
                                                    database.open();
                                                    coverageList = database.getCoverageStoreData(store_id);
                                                    new UploadingTask(NonWorkingReason.this, coverageList).execute();
                                                    dialog.dismiss();
                                                }
                                            }
                                        })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog,
                                            int id) {
                                        dialog.cancel();
                                    }
                                });
                        alert = builder.create();
                        alert.show();
                    } else {
                        Snackbar.make(save, "Please enter required remark reason", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(save, "Please Capture Image", Snackbar.LENGTH_LONG).show();
                }

            } else {
                Snackbar.make(save, "Please Select a Reason", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    public boolean validatedata() {
        boolean result = false;
        if (reasonid != null && !reasonid.equalsIgnoreCase("")) {
            result = true;
        }
        return result;
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
    public void onConnected(Bundle bundle) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (mLastLocation != null) {
            latitude = String.valueOf(mLastLocation.getLatitude());
            longitude = String.valueOf(mLastLocation.getLongitude());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    class Data {
        int value;
        String name;
    }

    private class UploadingTask extends AsyncTask<Void, Data, String> {
        private Context context;
        private ArrayList<CoverageBean> cdata;

        UploadingTask(Context context, ArrayList<CoverageBean> cdata) {
            this.context = context;
            this.cdata = cdata;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom_upload);
            dialog.setTitle("Sending Nonworking Store Data");
            dialog.setCancelable(false);
            dialog.show();
            pb = (ProgressBar) dialog.findViewById(R.id.progressBar1);
            percentage = (TextView) dialog.findViewById(R.id.percentage);
            message = (TextView) dialog.findViewById(R.id.message);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String onXML = "";
                if (cdata.size() > 0) {
                    data = new Data();
                    data.value = 20;
                    data.name = "Nonworking Uploading..";
                    publishProgress(data);
                    for (int i = 0; i < cdata.size(); i++) {
                        onXML = "[DATA][USER_DATA][STORE_CD]"
                                + cdata.get(i).getStoreId()
                                + "[/STORE_CD]" + "[VISIT_DATE]"
                                + visit_date
                                + "[/VISIT_DATE][LATITUDE]"
                                + cdata.get(i).getLatitude()
                                + "[/LATITUDE][APP_VERSION]"
                                + app_ver
                                + "[/APP_VERSION][LONGITUDE]"
                                + cdata.get(i).getLongitude()
                                + "[/LONGITUDE][IN_TIME]"
                                + cdata.get(i).getInTime()
                                + "[/IN_TIME][OUT_TIME]"
                                + cdata.get(i).getOutTime()
                                + "[/OUT_TIME][UPLOAD_STATUS]"
                                + "N"
                                + "[/UPLOAD_STATUS][USER_ID]"
                                + _UserId
                                + "[/USER_ID]" +
                                "[IMAGE_URL]"
                                + cdata.get(i).getImage()
                                + "[/IMAGE_URL]"
                                + "[IMAGE_URL1]"
                                + cdata.get(i).getImage02()
                                + "[/IMAGE_URL1]"
                                +
                                "[REASON_ID]"
                                + cdata.get(i).getReasonid()
                                + "[/REASON_ID]" +
                                "[REASON_REMARK]"
                                + cdata.get(i).getRemark()
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
                            mid = (words[1]);
                            data.value = 30;
                            data.name = "Uploading..";
                            publishProgress(data);
                        } else {
                            if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                return CommonString.METHOD_UPLOAD_DR_STORE_COVERAGE;
                            }
                        }


                        //sales data
                        String final_xml = "";
                        onXML = "";
                        insertedlist_Data = database.getinsertedSalesEntrydata(cdata.get(i).getStoreId());
                        if (insertedlist_Data.size() > 0) {
                            if (!insertedlist_Data.get(0).getImeino().equals("0")) {
                                uploadstatusflag = false;
                                for (int j = 0; j < insertedlist_Data.size(); j++) {
                                    if (!insertedlist_Data.get(j).getSatus().equals(CommonString.KEY_U)) {
                                        uploadstatusflag = true;
                                        onXML = "[SALE_ENTRY_DATA][MID]"
                                                + mid
                                                + "[/MID]"
                                                + "[CREATED_BY]"
                                                + _UserId
                                                + "[/CREATED_BY]"
                                                + "[IMEI_NO]"
                                                + insertedlist_Data.get(j).getImeino()
                                                + "[/IMEI_NO]"
                                                + "[MODEL_NO]"
                                                + insertedlist_Data.get(j).getModelno()
                                                + "[/MODEL_NO]"

                                                + "[/SALE_ENTRY_DATA]";
                                        final_xml = final_xml + onXML;
                                    }
                                }
                                if (uploadstatusflag) {
                                    final String sos_xml = "[DATA]" + final_xml + "[/DATA]";
                                    request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_XML);
                                    request.addProperty("XMLDATA", sos_xml);
                                    request.addProperty("KEYS", "SALE_ENTRY_DATA");
                                    request.addProperty("USERNAME", _UserId);
                                    request.addProperty("MID", mid);
                                    envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                                    envelope.dotNet = true;
                                    envelope.setOutputSoapObject(request);
                                    androidHttpTransport = new HttpTransportSE(CommonString.URL);
                                    androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_XML, envelope);
                                    result = (Object) envelope.getResponse();
                                    if (result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                        for (int i1 = 0; i1 < insertedlist_Data.size(); i1++) {
                                            long l = database.updateSaleDataStatus(cdata.get(i).getStoreId(), insertedlist_Data.get(i1).getKey_id(), CommonString.KEY_U);
                                        }
                                    }
                                    data.value = 40;
                                    data.name = "SALE_ENTRY";
                                    publishProgress(data);
                                }
                            } else {
                                if (!insertedlist_Data.get(0).getSatus().equals(CommonString.KEY_U)) {
                                    onXML = "[SALE_ENTRY_DATA][MID]"
                                            + mid
                                            + "[/MID]"
                                            + "[CREATED_BY]"
                                            + _UserId
                                            + "[/CREATED_BY]"
                                            + "[IMEI_NO]"
                                            + insertedlist_Data.get(0).getImeino()
                                            + "[/IMEI_NO]"
                                            + "[MODEL_NO]"
                                            + insertedlist_Data.get(0).getModelno()
                                            + "[/MODEL_NO]"

                                            + "[/SALE_ENTRY_DATA]";
                                    final_xml = final_xml + onXML;

                                    final String sos_xml = "[DATA]" + final_xml + "[/DATA]";
                                    request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_XML);
                                    request.addProperty("XMLDATA", sos_xml);
                                    request.addProperty("KEYS", "NO_SALE_DATA");
                                    request.addProperty("USERNAME", _UserId);
                                    request.addProperty("MID", mid);
                                    envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                                    envelope.dotNet = true;
                                    envelope.setOutputSoapObject(request);
                                    androidHttpTransport = new HttpTransportSE(CommonString.URL);
                                    androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_XML, envelope);
                                    result = (Object) envelope.getResponse();
                                    if (result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                        for (int i1 = 0; i1 < insertedlist_Data.size(); i1++) {
                                            long l = database.updateSaleDataStatus(cdata.get(i).getStoreId(),
                                                    insertedlist_Data.get(i1).getKey_id(), CommonString.KEY_U);
                                        }
                                    }
                                    data.value = 40;
                                    data.name = "SALE_ENTRY";
                                    publishProgress(data);
                                }
                            }
                        }

                        inserted_stock = database.getinsertedStockEntryData(
                                cdata.get(i).getVisitDate(), cdata.get(i).getStoreId());
                        if (inserted_stock.size() > 0) {
                            for (int j = 0; j < inserted_stock.size(); j++) {
                                uploaded_flag = false;
                                if (!inserted_stock.get(j).getStaus().equals(CommonString.KEY_U)) {
                                    uploaded_flag = true;
                                    onXML = "[STOCK_ENTRY_DATA][MID]"
                                            + mid
                                            + "[/MID]"
                                            + "[CREATED_BY]"
                                            + _UserId
                                            + "[/CREATED_BY]"
                                            + "[QUANTITY]"
                                            + inserted_stock.get(j).getStockQuantity()
                                            + "[/QUANTITY]"
                                            + "[MODEL_CD]"
                                            + inserted_stock.get(j).getModel_cd().get(0)
                                            + "[/MODEL_CD]"
                                            + "[/STOCK_ENTRY_DATA]";
                                    final_xml = final_xml + onXML;

                                }
                            }
                            if (uploaded_flag) {
                                final String sos_xml = "[DATA]" + final_xml + "[/DATA]";
                                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_XML);
                                request.addProperty("XMLDATA", sos_xml);
                                request.addProperty("KEYS", "STOCK_ENTRY_DATA");
                                request.addProperty("USERNAME", _UserId);
                                request.addProperty("MID", mid);
                                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                                envelope.dotNet = true;
                                envelope.setOutputSoapObject(request);
                                androidHttpTransport = new HttpTransportSE(CommonString.URL);
                                androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_XML, envelope);
                                result = (Object) envelope.getResponse();
                                if (result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                    long l = database.updateStockentryStatus(cdata.get(i).getStoreId(), cdata.get(i).getStoreId(), CommonString.KEY_U);

                                }
                                data.value = 50;
                                data.name = "Stock entry data";
                                publishProgress(data);
                            }
                        }

                        inserted_auditData = database.getinsertedDatafromDatabasedata(cdata.get(i).getStoreId());
                        onXML = "";
                        final_xml = "";
                        if (inserted_auditData.size() > 0) {
                            for (int j = 0; j < inserted_auditData.size(); j++) {
                                uploaded_flag = false;
                                if (!inserted_auditData.get(j).getStatus().equals(CommonString.KEY_U)) {
                                    uploaded_flag = true;
                                    onXML = "[SUP_AUDIT_DATA][MID]"
                                            + mid
                                            + "[/MID]"
                                            + "[CREATED_BY]"
                                            + _UserId
                                            + "[/CREATED_BY]"
                                            + "[ANSWER_CD]"
                                            + inserted_auditData.get(j).getCurrectanswerCd()
                                            + "[/ANSWER_CD]"
                                            + "[QUESTION_CD]"
                                            + inserted_auditData.get(j).getQuest_id().get(0)
                                            + "[/QUESTION_CD]"
                                            + "[/SUP_AUDIT_DATA]";
                                    final_xml = final_xml + onXML;

                                }
                            }
                            if (uploaded_flag) {
                                final String sos_xml = "[DATA]" + final_xml + "[/DATA]";
                                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_XML);
                                request.addProperty("XMLDATA", sos_xml);
                                request.addProperty("KEYS", "SUP_AUDIT_DATA");
                                request.addProperty("USERNAME", _UserId);
                                request.addProperty("MID", mid);
                                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                                envelope.dotNet = true;
                                envelope.setOutputSoapObject(request);
                                androidHttpTransport = new HttpTransportSE(CommonString.URL);
                                androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_XML, envelope);
                                result = (Object) envelope.getResponse();
                                if (result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                    long l = database.updateAuditStatus(cdata.get(i).getStoreId(),
                                            visit_date, CommonString.KEY_U);

                                }
                                data.value = 60;
                                data.name = "SUP_AUDIT_DATA data";
                                publishProgress(data);
                            }
                        }

                        supervisorAttendenceGetterSetter = database.getsupervisorAttendenceData(visit_date);
                        onXML = "";
                        final_xml = "";
                        if (supervisorAttendenceGetterSetter!=null && supervisorAttendenceGetterSetter.getReason_cd()!=null && !supervisorAttendenceGetterSetter.getReason_cd().equals("")) {
                            if (supervisorAttendenceGetterSetter.getStatus().equalsIgnoreCase("D")) {
                                onXML = "[SUP_ATTENDENCE_DATA][MID]"
                                        + mid
                                        + "[/MID]"
                                        + "[CREATED_BY]"
                                        + _UserId
                                        + "[/CREATED_BY]"
                                        + "[REASON_CD]"
                                        + supervisorAttendenceGetterSetter.getReason_cd()
                                        + "[/REASON_CD]"
                                       /* + "[REMARK]"
                                        + supervisorAttendenceGetterSetter.getRemark()
                                        + "[/REMARK]"*/
                                        + "[IMAGE]"
                                        + supervisorAttendenceGetterSetter.getImage()
                                        + "[/IMAGE]"
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



                        File dir = new File(CommonString.FILE_PATH);
                        ArrayList<String> list = new ArrayList();
                        list = getFileNames(dir.listFiles());
                        if (list.size() > 0) {
                            for (int i1 = 0; i1 < list.size(); i1++) {
                                if (list.get(i1).contains("_INTIME_IMG_") || list.get(i1).contains("_NONWORKING_")) {
                                    File originalFile = new File(CommonString.FILE_PATH + list.get(i1));
                                    result = RetrofitClass.UploadImageByRetrofit(NonWorkingReason.this,
                                            originalFile.getName(), "StoreImages");
                                    if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                        return result.toString();
                                    }
                                }

                                if (list.get(i1).contains("_OUTTIME_IMG_")) {
                                    File originalFile = new File(CommonString.FILE_PATH + list.get(i1));
                                    result = RetrofitClass.UploadImageByRetrofit(NonWorkingReason.this,
                                            originalFile.getName(), "StoreImages");
                                    if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                        return result.toString();
                                    }
                                }

                                if (list.get(i1).contains("_SUP_ATTENDENCE_")) {
                                    File originalFile = new File(CommonString.FILE_PATH + list.get(i1));
                                    result = RetrofitClass.UploadImageByRetrofit(NonWorkingReason.this, originalFile.getName(), "supervisorAttendance");
                                    if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                        return result.toString();
                                    }
                                }
                            }
                            data.value = 70;
                            data.name = "StoreImages";
                            publishProgress(data);
                        }
                        // SET COVERAGE STATUS
                        final_xml = "";
                        onXML = "";
                        onXML = "[COVERAGE_STATUS][STORE_ID]"
                                + cdata.get(i).getStoreId()
                                + "[/STORE_ID]"
                                + "[VISIT_DATE]"
                                + cdata.get(i).getVisitDate()
                                + "[/VISIT_DATE]"
                                + "[USER_ID]"
                                + cdata.get(i).getUserId()
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
                            database.updateStoreStatusOnLeave(cdata.get(i).getStoreId(), cdata.get(i).getVisitDate(), CommonString.KEY_U);
                            database.deleteSpecificStoreData(cdata.get(i).getStoreId());
                            data.value = 100;
                            data.name = "Uploading...";
                            publishProgress(data);
                        } else {
                            return CommonString.MEHTOD_UPLOAD_COVERAGE_STATUS;
                        }
                    }
                    return CommonString.KEY_SUCCESS;
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
                    AlertMessage message = new AlertMessage(NonWorkingReason.this, AlertMessage.MESSAGE_UPLOAD_DATA, "success_", null);
                    message.showMessage();
                } else if (!result.equals("")) {
                    Toast.makeText(NonWorkingReason.this,
                            "Complete data was not uploaded !\nPlease upload data manualy from main menu.",
                            Toast.LENGTH_LONG).show();
                    NonWorkingReason.this.finish();
                }
            } else {
                Snackbar.make(save, AlertMessage.MESSAGE_SOCKETEXCEPTION + "Please try again", Snackbar.LENGTH_LONG).show();
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
