package com.cpm.dailyentry;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialcamera.MaterialCamera;
import com.cpm.Constants.CommonString;
import com.cpm.retrofit.RetrofitClass;
import com.cpm.upload.UploadDataActivity;
import com.cpm.voto.R;
import com.cpm.database.GSKDatabase;
import com.cpm.delegates.CoverageBean;
import com.cpm.message.AlertMessage;
import com.cpm.xmlGetterSetter.AditGetterSetter;
import com.cpm.xmlGetterSetter.ModelGetterSetter;
import com.cpm.xmlGetterSetter.SaleEntryGetterSetter;
import com.cpm.xmlGetterSetter.SupervisorAttendenceGetterSetter;
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

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AttendenceActivity extends AppCompatActivity
        implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    ImageView img_cam, img_clicked;
    Button btn_save, btn_preview;
    String _pathforcheck = null, _path = null, str, app_ver, emp_cd;
    String pathnew;
    String store_cd, visit_date, username, intime, _UserId, latitude = "0.0", longitude = "0.0", user_type, SUPERVISOR_JCP_TYPE;
    Uri outputFileUri;
    private SharedPreferences preferences;
    final static int CAMERA_OUTPUT = 0;
    AlertDialog alert;
    GSKDatabase db;
    File file1 = null;
    File file = null, file3, file4;
    TextView textview;
    private ArrayList<CoverageBean> list1 = new ArrayList<>();
    ArrayList<SaleEntryGetterSetter> insertedlist_Data = new ArrayList<>();
    ArrayList<ModelGetterSetter> inserted_stock = new ArrayList<>();
    ArrayList<AditGetterSetter> inserted_auditData = new ArrayList<>();
    SupervisorAttendenceGetterSetter supervisorAttendenceGetterSetter;
    boolean uploadstatusflag = false;
    boolean flagimage = false;
    String datacheck = "";
    String[] words;
    String validity;
    boolean previewima = false;
    private Dialog dialog;
    private TextView percentage, message;
    private ProgressBar pb;
    Data data;
    String mid;
    Boolean uploadStatus = true;
    boolean uploaded_flag = false;
    private GoogleApiClient googleApiClient;
    private static final int REQUEST_LOCATION = 1;
    File saveDir = null;
    private final static int CAMERA_RQ = 6969;
    private SharedPreferences.Editor editor = null;
    static int counter = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = new GSKDatabase(this);
        db.open();
        img_cam = (ImageView) findViewById(R.id.img_selfie);
        img_clicked = (ImageView) findViewById(R.id.img_cam_selfie);
        textview = (TextView) findViewById(R.id.testvi);
        btn_save = (Button) findViewById(R.id.btn_save_selfie);
        btn_preview = (Button) findViewById(R.id.btn_Preview);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        store_cd = preferences.getString(CommonString.KEY_STORE_CD, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        app_ver = preferences.getString(CommonString.KEY_VERSION, "");

        _UserId = preferences.getString(CommonString.KEY_USER_ID, null);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        emp_cd = preferences.getString(CommonString.KEY_EMP_CD, null);
        user_type = preferences.getString(CommonString.KEY_USER_TYPE, null);
        SUPERVISOR_JCP_TYPE = preferences.getString(CommonString.KEY_SUPERVISOR_JCP_TYPE, null);
        getSupportActionBar().setTitle("Attendance -" + visit_date);

        intime = getCurrentTime();
        str = CommonString.FILE_PATH;
        checkInTimemethod();
        checkgpsEnableDevice();
        img_clicked.setOnClickListener(this);
        img_cam.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        btn_preview.setOnClickListener(this);
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

    protected void checkInTimemethod() {
        list1 = db.getCoverageDataReason(visit_date, store_cd);
        // btn_preview.setVisibility(ImageView.INVISIBLE);
        if (list1.size() > 0) {
            for (int i = 0; i < list1.size(); i++) {
                if (list1.get(i).getInTime() != null) {
                    btn_save.setText("Take Out Time ");
                    flagimage = true;
                    //  btn_preview.setVisibility(ImageView.VISIBLE);
                    break;
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!user_type.equalsIgnoreCase("ISD") && !SUPERVISOR_JCP_TYPE.equalsIgnoreCase("JCP") && db.getCoverageSpecificData(store_cd).size() == 0) {
                db.deleteSupervisorJaurneyPlanData(store_cd);
            }
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }


    public boolean checkNetIsAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        MaterialCamera materialCamera = new MaterialCamera(this)
                .saveDir(saveDir)
                .showPortraitWarning(true)
                .allowRetry(true)
                .defaultToFrontFacing(false)
                .allowRetry(true)
                .autoSubmit(false)
                .labelConfirm(R.string.mcam_use_video);
        switch (id) {
            case R.id.img_cam_selfie:
                if (flagimage == true) {
                    _pathforcheck = store_cd + "_OUTTIME_IMG_" + visit_date.replace("/", "") + "_" + getCurrentTime().replace(":", "") + ".jpg";
                    editor = preferences.edit();
                    editor.putString("imagename", _pathforcheck);
                    editor.commit();
                    materialCamera.stillShot().labelConfirm(R.string.mcam_use_stillshot);
                    materialCamera.start(CAMERA_RQ);
                    /*_path = CommonString.FILE_PATH + _pathforcheck;
                    startCameraActivity();*/
                    break;
                } else {
                    _pathforcheck = store_cd + "_INTIME_IMG_" + visit_date.replace("/", "") + "_" + getCurrentTime().replace(":", "") + ".jpg";
                    editor = preferences.edit();
                    editor.putString("imagename", _pathforcheck);
                    editor.putString("pathimage", str + _pathforcheck);
                    editor.commit();
                    materialCamera.stillShot().labelConfirm(R.string.mcam_use_stillshot);
                    materialCamera.start(CAMERA_RQ);
                    /*_path = CommonString.FILE_PATH + _pathforcheck;
                    startCameraActivity();*/
                    break;
                }

            case R.id.img_selfie:
                if (flagimage == true) {
                    _pathforcheck = store_cd + "_OUTTIME_IMG_" + visit_date.replace("/", "") + "_" + getCurrentTime().replace(":", "") + ".jpg";
                    editor = preferences.edit();
                    editor.putString("imagename", _pathforcheck);
                    editor.commit();
                    materialCamera.stillShot().labelConfirm(R.string.mcam_use_stillshot);
                    materialCamera.start(CAMERA_RQ);
                   /* _path = CommonString.FILE_PATH + _pathforcheck;
                    startCameraActivity();*/
                    break;
                } else {
                    _pathforcheck = store_cd + "_INTIME_IMG_" + visit_date.replace("/", "") + "_" + getCurrentTime().replace(":", "") + ".jpg";
                    editor = preferences.edit();
                    editor.putString("imagename", _pathforcheck);
                    editor.putString("pathimage", str + _pathforcheck);
                    editor.commit();
                    materialCamera.stillShot().labelConfirm(R.string.mcam_use_stillshot);
                    materialCamera.start(CAMERA_RQ);
                    break;
                }
            case R.id.btn_save_selfie:
                if (checkgpsEnableDevice()) {
                    if (checkNetIsAvailable()) {
                        if (file3 != null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    AttendenceActivity.this);
                            builder.setMessage("Do you want to save the data ")
                                    .setCancelable(false)
                                    .setPositiveButton("OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                                                    db.open();
                                                    ArrayList<CoverageBean> specific_dada = new ArrayList<>();
                                                    specific_dada = db.getCoverageSpecificData(store_cd);
                                                    if (specific_dada.size() > 0) {
                                                        CoverageBean cdata = new CoverageBean();
                                                        cdata.setStoreId(store_cd);
                                                        cdata.setVisitDate(visit_date);
                                                        cdata.setUserId(username);
                                                        cdata.setInTime(specific_dada.get(0).getInTime());
                                                        cdata.setReason("");
                                                        cdata.setReasonid("0");
                                                        cdata.setLatitude(specific_dada.get(0).getLatitude());
                                                        cdata.setLongitude(specific_dada.get(0).getLongitude());
                                                        cdata.setImage(specific_dada.get(0).getImage());
                                                        cdata.setOutTime(getCurrentTime());
                                                        cdata.setImage02(_pathforcheck);
                                                        cdata.setStatus(CommonString.KEY_VALID);
                                                        db.updateOutTime(cdata, store_cd, visit_date);
                                                        new UploadingTask(AttendenceActivity.this, cdata).execute();
                                                        dialog.dismiss();
                                                    }
                                                }
                                            })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            alert = builder.create();
                            alert.show();
                            break;
                        } else if (file4 != null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    AttendenceActivity.this);
                            builder.setMessage("Do you want to save the data ")
                                    .setCancelable(false)
                                    .setPositiveButton("OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                                                    CoverageBean cdata = new CoverageBean();
                                                    cdata.setStoreId(store_cd);
                                                    cdata.setVisitDate(visit_date);
                                                    cdata.setUserId(username);
                                                    cdata.setInTime(getCurrentTime());
                                                    cdata.setOutTime("");
                                                    cdata.setReason("");
                                                    cdata.setRemark("");
                                                    cdata.setReasonid("0");
                                                    cdata.setLatitude(latitude);
                                                    cdata.setLongitude(longitude);
                                                    cdata.setImage(_pathforcheck);
                                                    //new change
                                                    cdata.setEmp_cd(emp_cd);
                                                    cdata.setStatus(CommonString.KEY_INVALID);
                                                    new UploadingTask(AttendenceActivity.this, cdata).execute();
                                                    dialog.dismiss();
                                                }
                                            })
                                    .setNegativeButton("Cancel",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,
                                                                    int id) {
                                                    dialog.cancel();
                                                }
                                            });
                            alert = builder.create();
                            alert.show();
                            break;
                        } else {
                            Snackbar.make(btn_save, "Please Take A Selfie", Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        Snackbar.make(btn_save, "Check internet connection", Snackbar.LENGTH_SHORT).show();
                    }
                }

                break;

            case R.id.btn_Preview:
                pathnew = preferences.getString("pathimage", null);
                if (previewima == true) {
                    btn_save.setVisibility(View.VISIBLE);
                    // btn_preview.setText("Preview In Time");
                    textview.setText("Click your selfie");
                    img_clicked.setVisibility(ImageView.VISIBLE);
                    img_cam.setVisibility(ImageView.INVISIBLE);
                    previewima = false;
                } else {
                    //  btn_preview.setText("Cancel");
                    textview.setText("Preview In-Time Image");
                    btn_save.setVisibility(View.INVISIBLE);
                    previewimage(pathnew);
                }
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i("MakeMachine", "resultCode: " + resultCode);
        switch (resultCode) {
            case 0:
                Log.i("MakeMachine", "User cancelled");
                break;

            case -1:
                img_clicked.setVisibility(ImageView.INVISIBLE);
                img_cam.setVisibility(ImageView.VISIBLE);
                if (requestCode == CAMERA_RQ) {
                    if (resultCode == RESULT_OK) {
                        if (flagimage == true) {
                            File file = new File(data.getData().getPath());
                            _pathforcheck = file.getName();
                            file3 = new File(str + _pathforcheck);
                            Uri uri = Uri.fromFile(file3);
                            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                            //  bitmap = crupAndScale(bitmap, 800); // if you mind scaling
                            img_cam.setImageBitmap(bitmap);
                            img_clicked.setVisibility(ImageView.INVISIBLE);
                            img_cam.setVisibility(ImageView.VISIBLE);

                        } else {
                            File file = new File(data.getData().getPath());
                            _pathforcheck = file.getName();
                            file4 = new File(str + _pathforcheck);
                            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                            //  bitmap = crupAndScale(bitmap, 800); // if you mind scaling
                            img_cam.setImageBitmap(bitmap);
                            img_clicked.setVisibility(ImageView.INVISIBLE);
                            img_cam.setVisibility(ImageView.VISIBLE);

                        }
                    }
                    break;


                }
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_CANCELED: {
                        googleApiClient = null;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static Bitmap crupAndScale(Bitmap source, int scale) {
        int factor = source.getHeight() <= source.getWidth() ? source.getHeight() : source.getWidth();
        int longer = source.getHeight() >= source.getWidth() ? source.getHeight() : source.getWidth();
        int x = source.getHeight() >= source.getWidth() ? 0 : (longer - factor) / 2;
        int y = source.getHeight() <= source.getWidth() ? 0 : (longer - factor) / 2;
        source = Bitmap.createBitmap(source, x, y, factor, factor);
        source = Bitmap.createScaledBitmap(source, scale, scale, false);
        return source;
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
    public void onBackPressed() {
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        if (!user_type.equalsIgnoreCase("ISD") && !SUPERVISOR_JCP_TYPE.equalsIgnoreCase("JCP") && db.getCoverageSpecificData(store_cd).size() == 0) {
            db.deleteSupervisorJaurneyPlanData(store_cd);
        }
        this.finish();
    }

    public void previewimage(String pathimage) {
        if (pathimage != null) {
            file1 = new File(pathimage);
            Uri uri = Uri.fromFile(file1);
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                bitmap = crupAndScale(bitmap, 800); // if you mind scaling
                img_cam.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            img_clicked.setVisibility(ImageView.INVISIBLE);
            img_cam.setVisibility(ImageView.VISIBLE);
            previewima = true;
        } else {
            Snackbar.make(btn_save, "NO In Time Preview", Snackbar.LENGTH_SHORT).show();
        }
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
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
        super.onStart();
    }

    protected void onStop() {
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    class Data {
        int value;
        String name;
    }

    private class UploadingTask extends AsyncTask<Void, Data, String> {
        private Context context;
        private CoverageBean cdata;

        UploadingTask(Context context, CoverageBean cdata) {
            this.context = context;
            this.cdata = cdata;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom_upload);
            if (file3 != null) {
                dialog.setTitle("Sending Outtime Store Data");
            } else {
                dialog.setTitle("Sending Intime Store Data");
            }
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
                if (file3 != null) {
                    data = new Data();
                    data.value = 20;
                    data.name = "outtime Uploading..";
                    publishProgress(data);
                    onXML = "[DATA][USER_DATA][STORE_CD]"
                            + cdata.getStoreId()
                            + "[/STORE_CD]" + "[VISIT_DATE]"
                            + visit_date
                            + "[/VISIT_DATE][LATITUDE]"
                            + cdata.getLatitude()
                            + "[/LATITUDE][APP_VERSION]"
                            + app_ver
                            + "[/APP_VERSION][LONGITUDE]"
                            + cdata.getLongitude()
                            + "[/LONGITUDE][IN_TIME]"
                            + cdata.getInTime()
                            + "[/IN_TIME][OUT_TIME]"
                            + cdata.getOutTime()
                            + "[/OUT_TIME][UPLOAD_STATUS]"
                            + "N"
                            + "[/UPLOAD_STATUS][USER_ID]"
                            + username
                            + "[/USER_ID]" +
                            "[IMAGE_URL]"
                            + cdata.getImage()
                            + "[/IMAGE_URL]"
                            +
                            "[IMAGE_URL1]"
                            + cdata.getImage02()
                            + "[/IMAGE_URL1]"
                            +
                            "[REASON_ID]"
                            + "0"
                            + "[/REASON_ID]" +
                            "[REASON_REMARK]"
                            + ""
                            + "[/REASON_REMARK][/USER_DATA][/DATA]";
                } else {
                    onXML = "";
                    data = new Data();
                    data.value = 20;
                    data.name = "Intime Uploading";
                    publishProgress(data);
                    onXML = "[DATA][USER_DATA][STORE_CD]"
                            + cdata.getStoreId()
                            + "[/STORE_CD]" + "[VISIT_DATE]"
                            + visit_date
                            + "[/VISIT_DATE][LATITUDE]"
                            + cdata.getLatitude()
                            + "[/LATITUDE][APP_VERSION]"
                            + app_ver
                            + "[/APP_VERSION][LONGITUDE]"
                            + cdata.getLongitude()
                            + "[/LONGITUDE][IN_TIME]"
                            + cdata.getInTime()
                            + "[/IN_TIME][OUT_TIME]"
                            + "00:00:00"
                            + "[/OUT_TIME][UPLOAD_STATUS]"
                            + "N"
                            + "[/UPLOAD_STATUS][USER_ID]"
                            + username
                            + "[/USER_ID]" +
                            "[IMAGE_URL]"
                            + cdata.getImage()
                            + "[/IMAGE_URL]"
                            + "[IMAGE_URL1]"
                            + ""
                            + "[/IMAGE_URL1]"
                            +
                            "[REASON_ID]"
                            + "0"
                            + "[/REASON_ID]" +
                            "[REASON_REMARK]"
                            + ""
                            + "[/REASON_REMARK][/USER_DATA][/DATA]";
                }
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

                if (mid == null) {
                    return "midnull";
                }
                //SALE_DATA data
                if (file3 != null) {
                    String final_xml = "";
                    onXML = "";
                    insertedlist_Data = db.getinsertedSalesEntrydata(cdata.getStoreId());
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
                                            + username
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
                                request.addProperty("USERNAME", username);
                                request.addProperty("MID", mid);
                                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                                envelope.dotNet = true;
                                envelope.setOutputSoapObject(request);
                                androidHttpTransport = new HttpTransportSE(CommonString.URL);
                                androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_XML, envelope);
                                result = (Object) envelope.getResponse();
                                if (result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                    for (int i1 = 0; i1 < insertedlist_Data.size(); i1++) {
                                        long l = db.updateSaleDataStatus(cdata.getStoreId(), insertedlist_Data.get(i1).getKey_id(), CommonString.KEY_U);
                                    }
                                }
                                data.value = 40;
                                data.name = "SALE_ENTRY";
                                publishProgress(data);
                            }
                        } else {
                            onXML = "";
                            final_xml = "";
                            if (!insertedlist_Data.get(0).getSatus().equals(CommonString.KEY_U)) {
                                onXML = "[SALE_ENTRY_DATA][MID]"
                                        + mid
                                        + "[/MID]"
                                        + "[CREATED_BY]"
                                        + username
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
                                request.addProperty("USERNAME", username);
                                request.addProperty("MID", mid);
                                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                                envelope.dotNet = true;
                                envelope.setOutputSoapObject(request);
                                androidHttpTransport = new HttpTransportSE(CommonString.URL);
                                androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_XML, envelope);
                                result = (Object) envelope.getResponse();
                                if (result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                    for (int i1 = 0; i1 < insertedlist_Data.size(); i1++) {
                                        long l = db.updateSaleDataStatus(cdata.getStoreId(), insertedlist_Data.get(i1).getKey_id(), CommonString.KEY_U);
                                    }
                                }
                                data.value = 40;
                                data.name = "SALE_ENTRY";
                                publishProgress(data);
                            }
                        }
                    }


                    inserted_stock = db.getinsertedStockEntryData(visit_date, store_cd);
                    onXML = "";
                    final_xml = "";
                    if (inserted_stock.size() > 0) {
                        for (int j = 0; j < inserted_stock.size(); j++) {
                            uploaded_flag = false;
                            if (!inserted_stock.get(j).getStaus().equals(CommonString.KEY_U)) {
                                uploaded_flag = true;
                                onXML = "[STOCK_ENTRY_DATA][MID]"
                                        + mid
                                        + "[/MID]"
                                        + "[CREATED_BY]"
                                        + username
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
                            request.addProperty("USERNAME", username);
                            request.addProperty("MID", mid);
                            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                            envelope.dotNet = true;
                            envelope.setOutputSoapObject(request);
                            androidHttpTransport = new HttpTransportSE(CommonString.URL);
                            androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_XML, envelope);
                            result = (Object) envelope.getResponse();
                            if (result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                long l = db.updateStockentryStatus(store_cd, visit_date, CommonString.KEY_U);

                            }
                            data.value = 50;
                            data.name = "Stock entry data";
                            publishProgress(data);
                        }
                    }

                    inserted_auditData = db.getinsertedDatafromDatabasedata(store_cd);
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
                                        + username
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
                            request.addProperty("USERNAME", username);
                            request.addProperty("MID", mid);
                            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                            envelope.dotNet = true;
                            envelope.setOutputSoapObject(request);
                            androidHttpTransport = new HttpTransportSE(CommonString.URL);
                            androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_XML, envelope);
                            result = (Object) envelope.getResponse();
                            if (result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                long l = db.updateAuditStatus(store_cd, visit_date, CommonString.KEY_U);

                            }
                            data.value = 60;
                            data.name = "SUP_AUDIT_DATA data";
                            publishProgress(data);
                        }
                    }

                    supervisorAttendenceGetterSetter = db.getsupervisorAttendenceData(visit_date);
                    onXML = "";
                    final_xml = "";
                    if (supervisorAttendenceGetterSetter.getReason_cd()!=null && !supervisorAttendenceGetterSetter.getReason_cd().equals("")) {
                        if (supervisorAttendenceGetterSetter.getStatus().equalsIgnoreCase("D")) {
                            onXML = "[SUP_ATTENDENCE_DATA]"
                                    + "[CREATED_BY]"
                                    + username
                                    + "[/CREATED_BY]"
                                    + "[REASON_CD]"
                                    + supervisorAttendenceGetterSetter.getReason_cd()
                                    + "[/REASON_CD]"

                                    + "[VISIT_DATE]"
                                    + visit_date
                                    + "[/VISIT_DATE]"
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
                            request.addProperty("USERNAME", username);
                            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                            envelope.dotNet = true;
                            envelope.setOutputSoapObject(request);
                            androidHttpTransport = new HttpTransportSE(CommonString.URL);
                            androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_XML, envelope);
                            result = (Object) envelope.getResponse();
                            if (result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                db.updateSupAttendenceStatus(visit_date, CommonString.KEY_U);
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
                                result = RetrofitClass.UploadImageByRetrofit(AttendenceActivity.this, originalFile.getName(), "StoreImages");
                                if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                    return result.toString();
                                }
                            }
                            if (list.get(i1).contains("_OUTTIME_IMG_")) {
                                File originalFile = new File(CommonString.FILE_PATH + list.get(i1));
                                result = RetrofitClass.UploadImageByRetrofit(AttendenceActivity.this, originalFile.getName(), "StoreImages");
                                if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                    return result.toString();
                                }
                            }

                            if (list.get(i1).contains("_SUP_ATTENDENCE_")) {
                                File originalFile = new File(CommonString.FILE_PATH + list.get(i1));
                                result = RetrofitClass.UploadImageByRetrofit(AttendenceActivity.this, originalFile.getName(), "supervisorAttendance");
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
                            + cdata.getStoreId()
                            + "[/STORE_ID]"
                            + "[VISIT_DATE]"
                            + cdata.getVisitDate()
                            + "[/VISIT_DATE]"
                            + "[USER_ID]"
                            + cdata.getUserId()
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
                        data.value = 100;
                        data.name = "Uploading..";
                        publishProgress(data);
                        db.open();
                        db.updateStoreStatusOnLeave(cdata.getStoreId(), cdata.getVisitDate(), CommonString.KEY_U);
                    } else {
                        return CommonString.MEHTOD_UPLOAD_COVERAGE_STATUS;
                    }
                } else {
                    File dir = new File(CommonString.FILE_PATH);
                    ArrayList<String> list = new ArrayList();
                    list = getFileNames(dir.listFiles());
                    if (list.size() > 0) {
                        for (int i1 = 0; i1 < list.size(); i1++) {
                            if (list.get(i1).contains(cdata.getImage())) {
                                File originalFile = new File(CommonString.FILE_PATH + list.get(i1));
                                result = RetrofitClass.UploadImageByRetrofit(AttendenceActivity.this, originalFile.getName(), "StoreImages");
                                if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                    return result.toString();
                                }
                            }
                        }
                    }
                    db.open();
                    db.InsertCoverageData(cdata);
                    db.updateJaurneyPlanSpecificStoreStatus(cdata.getStoreId(), cdata.getVisitDate(), cdata.getStatus());
                    data.value = 100;
                    data.name = "Uploading..";
                    publishProgress(data);
                    return CommonString.KEY_SUCCESS;
                }

            } catch (MalformedURLException e) {
                uploadStatus = false;
                if (file3 != null) {
                } else {
                    return AlertMessage.MESSAGE_EXCEPTION;
                }
            } catch (final IOException e) {
                uploadStatus = false;
                if (file3 != null) {
                } else {
                    return AlertMessage.MESSAGE_SOCKETEXCEPTION;
                }
            } catch (Exception e) {
                uploadStatus = false;
                if (file3 != null) {
                } else {
                    return AlertMessage.MESSAGE_EXCEPTION;
                }
            }
            if (file3 != null && uploadStatus) {
                db.open();
                db.deleteSpecificStoreData(cdata.getStoreId());
                return CommonString.KEY_SUCCESS;
            } else if (file3 != null && !uploadStatus) {
                return AlertMessage.MESSAGE_SOCKETEXCEPTION;
            } else if (file3 == null && !uploadStatus) {
                counter++;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        if (counter < 3) {
                            new UploadingTask(AttendenceActivity.this, cdata).execute();
                        }
                    }
                });
            } else if (file3 == null && uploadStatus) {
                return CommonString.KEY_SUCCESS;
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
            if (result.equalsIgnoreCase(CommonString.KEY_SUCCESS) && file3 != null) {
                AlertMessage message = new AlertMessage(AttendenceActivity.this, AlertMessage.MESSAGE_UPLOAD_DATA, "success_", null);
                message.showMessage();
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(CommonString.KEY_STOREVISITED_STATUS, "");
                editor.commit();
            } else if (result.equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                AlertMessage message = new AlertMessage(AttendenceActivity.this, "Intime upload successfully", "success_", null);
                message.showMessage();
            } else if (result.toString().equals("midnull")) {
                Snackbar.make(btn_save, "Please try again", Snackbar.LENGTH_LONG).show();
            } else if (file3 != null && !result.equals("")) {
                AlertMessage message = new AlertMessage(AttendenceActivity.this, "Complete data was not uploaded !\nPlease upload data manualy from main menu.", "success_", null);
                message.showMessage();
            } else if (!result.equals("")) {
                Snackbar.make(btn_save, "Please try again", Snackbar.LENGTH_LONG).show();
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

    private boolean checkgpsEnableDevice() {
        boolean flag = true;
        googleApiClient = null;
        if (!hasGPSDevice(AttendenceActivity.this)) {
            Toast.makeText(AttendenceActivity.this, "Gps not Supported", Toast.LENGTH_SHORT).show();
        }
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(AttendenceActivity.this)) {
            enableLoc();
            flag = false;
        } else if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(AttendenceActivity.this)) {
            flag = true;
        }
        return flag;
    }


    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
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
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
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
                                status.startResolutionForResult(AttendenceActivity.this, REQUEST_LOCATION);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }

    }


}
