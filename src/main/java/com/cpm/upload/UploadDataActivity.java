package com.cpm.upload;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;


import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.Constants.CommonString;
import com.cpm.dailyentry.AttendenceActivity;
import com.cpm.database.GSKDatabase;
import com.cpm.delegates.CoverageBean;

import com.cpm.retrofit.RetrofitClass;
import com.cpm.voto.R;
import com.cpm.voto.MainMenuActivity;
import com.cpm.message.AlertMessage;
import com.cpm.voto.SupervisorAttendenceActivity;
import com.cpm.xmlGetterSetter.AditGetterSetter;
import com.cpm.xmlGetterSetter.FailureGetterSetter;
import com.cpm.xmlGetterSetter.ModelGetterSetter;
import com.cpm.xmlGetterSetter.SaleEntryGetterSetter;
import com.cpm.xmlGetterSetter.SupervisorAttendenceGetterSetter;
import com.cpm.xmlHandler.FailureXMLHandler;

@SuppressWarnings("deprecation")
public class UploadDataActivity extends Activity {

    private Dialog dialog;
    private ProgressBar pb;
    private TextView percentage, message;
    String app_ver;
    private String visit_date, username;
    private SharedPreferences preferences;
    private GSKDatabase database;
    private int factor, k;
    String datacheck = "";
    String[] words;
    String validity;
    int mid;
    Data data;
    private ArrayList<CoverageBean> coverageBeanlist = new ArrayList<CoverageBean>();
    ArrayList<SaleEntryGetterSetter> insertedlist_Data = new ArrayList<>();
    ArrayList<ModelGetterSetter> inserted_stock = new ArrayList<>();
    ArrayList<AditGetterSetter> inserted_auditData = new ArrayList<>();
    SupervisorAttendenceGetterSetter supervisorAttendenceGetterSetter;
    boolean uploaded_flag = false;
    boolean up_success_flag = true;
    boolean uploadstatusflag = false;
    boolean upload_status = false;
    String Path;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        app_ver = preferences.getString(CommonString.KEY_VERSION, "");
        database = new GSKDatabase(this);
        database.open();
        Path = CommonString.FILE_PATH;
        new UploadTask(this).execute();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        database.close();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub

        Intent i = new Intent(this, MainMenuActivity.class);
        startActivity(i);
        UploadDataActivity.this.finish();
    }

    private class UploadTask extends AsyncTask<Void, Data, String> {
        private Context context;

        UploadTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom_upload);
            dialog.setTitle("Uploading Data");
            dialog.setCancelable(false);
            dialog.show();
            pb = (ProgressBar) dialog.findViewById(R.id.progressBar1);
            percentage = (TextView) dialog.findViewById(R.id.percentage);
            message = (TextView) dialog.findViewById(R.id.message);
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {
                data = new Data();
                data.value = 20;
                data.name = "Uploading coverage data......";
                publishProgress(data);
                coverageBeanlist = database.getCoverageData(visit_date);
                if (coverageBeanlist.size() > 0) {
                    for (int i = 0; i < coverageBeanlist.size(); i++) {
                        String onXML = "[DATA][USER_DATA][STORE_CD]"
                                + coverageBeanlist.get(i).getStoreId()
                                + "[/STORE_CD]" + "[VISIT_DATE]"
                                + coverageBeanlist.get(i).getVisitDate()
                                + "[/VISIT_DATE][LATITUDE]"
                                + coverageBeanlist.get(i).getLatitude()
                                + "[/LATITUDE][APP_VERSION]"
                                + app_ver
                                + "[/APP_VERSION][LONGITUDE]"
                                + coverageBeanlist.get(i).getLongitude()
                                + "[/LONGITUDE][IN_TIME]"
                                + coverageBeanlist.get(i).getInTime()
                                + "[/IN_TIME][OUT_TIME]"
                                + coverageBeanlist.get(i).getOutTime()
                                + "[/OUT_TIME][UPLOAD_STATUS]"
                                + "N"
                                + "[/UPLOAD_STATUS][USER_ID]"
                                + username
                                + "[/USER_ID]" +
                                "[IMAGE_URL]"
                                + coverageBeanlist.get(i).getImage()
                                + "[/IMAGE_URL]"
                                +
                                "[IMAGE_URL1]"
                                + coverageBeanlist.get(i).getImage02()
                                + "[/IMAGE_URL1]"
                                + "[REASON_ID]"
                                + coverageBeanlist.get(i).getReasonid()
                                + "[/REASON_ID]" +
                                "[REASON_REMARK]"
                                + coverageBeanlist.get(i).getRemark()
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
                            database.updateCoverageStatus(coverageBeanlist.get(i).getMID(), CommonString.KEY_P);
                            database.updateStoreStatusOnLeave(coverageBeanlist.get(i).getStoreId(), coverageBeanlist.get(i).getVisitDate(), CommonString.KEY_P);
                        } else {
                            return CommonString.METHOD_UPLOAD_DR_STORE_COVERAGE;
                        }


                        mid = Integer.parseInt((words[1]));
                        data.value = 30;
                        data.name = "Uploaded coverage data";
                        publishProgress(data);


                        //sales data
                        String final_xml = "";
                        onXML = "";
                        insertedlist_Data = database.getinsertedSalesEntrydata(coverageBeanlist.get(i).getStoreId());
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
                                            long l = database.updateSaleDataStatus(coverageBeanlist.get(i).getStoreId(), insertedlist_Data.get(i1).getKey_id(),
                                                    CommonString.KEY_U);
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
                                            long l = database.updateSaleDataStatus(coverageBeanlist.get(i).getStoreId(), insertedlist_Data.get(i1).getKey_id(), CommonString.KEY_U);
                                        }
                                    }
                                    data.value = 40;
                                    data.name = "SALE_ENTRY";
                                    publishProgress(data);
                                }
                            }
                        }

                        inserted_stock = database.getinsertedStockEntryData(
                                coverageBeanlist.get(i).getVisitDate(), coverageBeanlist.get(i).getStoreId());
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
                                    long l = database.updateStockentryStatus(coverageBeanlist.get(i).getStoreId(), coverageBeanlist.get(i).getStoreId(), CommonString.KEY_U);

                                }
                                data.value = 50;
                                data.name = "Stock entry data";
                                publishProgress(data);
                            }
                        }

                        inserted_auditData = database.getinsertedDatafromDatabasedata(coverageBeanlist.get(i).getStoreId());
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
                                    long l = database.updateAuditStatus(coverageBeanlist.get(i).getStoreId(),
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

                                        /*+ "[REMARK]"
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
                                    result = RetrofitClass.UploadImageByRetrofit(UploadDataActivity.this, originalFile.getName(), "StoreImages");
                                    if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                        return result.toString();
                                    }
                                }
                                if (list.get(i1).contains("_OUTTIME_IMG_")) {
                                    File originalFile = new File(CommonString.FILE_PATH + list.get(i1));
                                    result = RetrofitClass.UploadImageByRetrofit(UploadDataActivity.this,
                                            originalFile.getName(), "StoreImages");
                                    if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                        return result.toString();
                                    }
                                }
                                if (list.get(i1).contains("_SUP_ATTENDENCE_")) {
                                    File originalFile = new File(CommonString.FILE_PATH + list.get(i1));
                                    result = RetrofitClass.UploadImageByRetrofit(UploadDataActivity.this, originalFile.getName(), "supervisorAttendance");
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
                                + coverageBeanlist.get(i).getStoreId()
                                + "[/STORE_ID]"
                                + "[VISIT_DATE]"
                                + coverageBeanlist.get(i).getVisitDate()
                                + "[/VISIT_DATE]"
                                + "[USER_ID]"
                                + coverageBeanlist.get(i).getUserId()
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
                            database.updateCoverageStatus(coverageBeanlist.get(i).getMID(), CommonString.KEY_U);
                            database.updateStoreStatusOnLeave(coverageBeanlist.get(i).getStoreId(), coverageBeanlist.get(i).getVisitDate(), CommonString.KEY_U);
                        } else {
                            return CommonString.MEHTOD_UPLOAD_COVERAGE_STATUS;
                        }
                    }
                }


            } catch (MalformedURLException e) {
                up_success_flag = false;
                final AlertMessage message = new AlertMessage(UploadDataActivity.this,
                        AlertMessage.MESSAGE_EXCEPTION, "download", e);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        message.showMessage();
                    }
                });

            } catch (IOException e) {
                up_success_flag = false;
            } catch (Exception e) {
                up_success_flag = false;
                final AlertMessage message = new AlertMessage(
                        UploadDataActivity.this,
                        AlertMessage.MESSAGE_EXCEPTION, "download", e);

                e.getMessage();
                e.printStackTrace();
                e.getCause();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        message.showMessage();
                    }
                });
            }
            if (up_success_flag) {
                database.deleteAllTables();
                return CommonString.KEY_SUCCESS;
            } else {
                return CommonString.KEY_FAILURE;
            }


        }

        @Override
        protected void onProgressUpdate(Data... values) {
            // TODO Auto-generated method stub
            pb.setProgress(values[0].value);
            percentage.setText(values[0].value + "%");
            message.setText(values[0].name);
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            dialog.dismiss();
            if (result.equals(CommonString.KEY_SUCCESS)) {
                if (upload_status) {
                    Toast.makeText(getApplicationContext(), "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    AlertMessage message = new AlertMessage(UploadDataActivity.this, AlertMessage.MESSAGE_UPLOAD_DATA, "success", null);
                    message.showMessage();
                }
            } else if (result.equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                AlertMessage message = new AlertMessage(UploadDataActivity.this, AlertMessage.MESSAGE_SOCKETEXCEPTION, "success", null);
                message.showMessage();
            } else {
                AlertMessage message = new AlertMessage(UploadDataActivity.this, AlertMessage.MESSAGE_SOCKETEXCEPTION, "success", null);
                message.showMessage();
            }
        }
    }

    class Data {
        int value;
        String name;
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
