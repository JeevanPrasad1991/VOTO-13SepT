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
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.Constants.CommonString;
import com.cpm.retrofit.RetrofitClass;
import com.cpm.voto.R;

import com.cpm.database.GSKDatabase;
import com.cpm.delegates.CoverageBean;
import com.cpm.message.AlertMessage;

import com.cpm.xmlGetterSetter.FailureGetterSetter;
import com.cpm.xmlGetterSetter.JourneyPlanGetterSetter;
import com.cpm.xmlGetterSetter.ModelGetterSetter;
import com.cpm.xmlGetterSetter.SaleEntryGetterSetter;
import com.cpm.xmlHandler.FailureXMLHandler;

public class CheckoutNUpload extends Activity {
    ArrayList<JourneyPlanGetterSetter> jcplist;
    GSKDatabase db;
    private SharedPreferences preferences;
    private String username, visit_date, prev_date;
    private Dialog dialog;
    private ProgressBar pb;
    private TextView percentage, message;
    private Data data;
    private ArrayList<CoverageBean> coverageBeanlist = new ArrayList<CoverageBean>();
    String app_ver;
    String datacheck = "";
    String[] words;
    String validity;
    int mid;
    String errormsg = "";
    private GSKDatabase database;
    private FailureGetterSetter failureGetterSetter = null;
    ArrayList<SaleEntryGetterSetter> insertedlist_Data = new ArrayList<>();
    ArrayList<ModelGetterSetter> inserted_stock = new ArrayList<>();
    boolean uploaded_flag = false;
    boolean uploadstatusflag = false;
    boolean upload_status = false;
    String Path;
    boolean up_success_flag = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_n_upload);
        db = new GSKDatabase(this);
        db.open();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        username = preferences.getString(CommonString.KEY_USERNAME, "");
        app_ver = preferences.getString(CommonString.KEY_VERSION, "");
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        Path = CommonString.FILE_PATH;
        database = new GSKDatabase(this);
        database.open();
        jcplist = db.getAllJCPData();
        for (int j = 0; j < jcplist.size(); j++) {
            if (!jcplist.get(j).getVISIT_DATE().get(0).equals(visit_date)) {
                prev_date = jcplist.get(j).getVISIT_DATE().get(0);

            }
        }
        new UploadTask(this).execute();

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
                data.name = "Uploading data....";
                publishProgress(data);
                coverageBeanlist = database.getCoverageData(prev_date);
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
                                + "[IMAGE_URL1]"
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
                                        long l = database.updateSaleDataStatus(coverageBeanlist.get(i).getStoreId(),
                                                insertedlist_Data.get(i1).getKey_id(),
                                                CommonString.KEY_U);
                                    }
                                }
                            }

                            data.value = 40;
                            data.name = "SALE_ENTRY_DATA";
                            publishProgress(data);
                        }
//stock data
                        inserted_stock = database.getinsertedStockEntryData(coverageBeanlist.get(i).getVisitDate(), coverageBeanlist.get(i).getStoreId());
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
                        File dir = new File(CommonString.FILE_PATH);
                        ArrayList<String> list = new ArrayList();
                        list = getFileNames(dir.listFiles());
                        if (list.size() > 0) {
                            for (int i1 = 0; i1 < list.size(); i1++) {
                                if (list.get(i1).contains("_INTIME_IMG_") || list.get(i1).contains("_NONWORKING_")) {
                                    File originalFile = new File(CommonString.FILE_PATH + list.get(i1));
                                    result = RetrofitClass.UploadImageByRetrofit(CheckoutNUpload.this,
                                            originalFile.getName(), "StoreImages");
                                    if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                        return result.toString();
                                    }
                                }
                                if (list.get(i1).contains("_OUTTIME_IMG_")) {
                                    File originalFile = new File(CommonString.FILE_PATH + list.get(i1));
                                    result = RetrofitClass.UploadImageByRetrofit(CheckoutNUpload.this,
                                            originalFile.getName(), "StoreImages");
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
                final AlertMessage message = new AlertMessage(
                        CheckoutNUpload.this,
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
                final AlertMessage message = new AlertMessage(CheckoutNUpload.this, AlertMessage.MESSAGE_EXCEPTION, "download", e);
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
                    AlertMessage message = new AlertMessage(CheckoutNUpload.this, AlertMessage.MESSAGE_UPLOAD_DATA, "success", null);
                    message.showMessage();
                }
            } else if (result.equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                AlertMessage message = new AlertMessage(CheckoutNUpload.this, AlertMessage.MESSAGE_SOCKETEXCEPTION, "success", null);
                message.showMessage();
            } else {
                AlertMessage message = new AlertMessage(CheckoutNUpload.this, AlertMessage.MESSAGE_SOCKETEXCEPTION, "success", null);
                message.showMessage();
            }
        }
    }


    public String UploadAssetImage(String path) throws Exception {
        errormsg = "";
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(Path + path, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;

        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Bitmap bitmap = BitmapFactory.decodeFile(
                Path + path, o2);

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        String ba1 = Base64.encodeBytes(ba);

        SoapObject request = new SoapObject(CommonString.NAMESPACE,
                CommonString.METHOD_UPLOAD_IMAGE);

        String[] split = path.split("/");
        String path1 = split[split.length - 1];

        request.addProperty("img", ba1);
        request.addProperty("name", path1);
        request.addProperty("FolderName", "StoreImages");

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                CommonString.URL);

        androidHttpTransport
                .call(CommonString.SOAP_ACTION_UPLOAD_IMAGE,
                        envelope);
        Object result = (Object) envelope.getResponse();

        if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {

            if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                return CommonString.KEY_FALSE;
            }

            SAXParserFactory saxPF = SAXParserFactory.newInstance();
            SAXParser saxP = saxPF.newSAXParser();
            XMLReader xmlR = saxP.getXMLReader();

            // for failure
            FailureXMLHandler failureXMLHandler = new FailureXMLHandler();
            xmlR.setContentHandler(failureXMLHandler);

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(result.toString()));
            xmlR.parse(is);

            failureGetterSetter = failureXMLHandler
                    .getFailureGetterSetter();

            if (failureGetterSetter.getStatus().equalsIgnoreCase(
                    CommonString.KEY_FAILURE)) {
                errormsg = failureGetterSetter.getErrorMsg();
                return CommonString.KEY_FAILURE;
            }
        } else {
            new File(Path + path).delete();
        }

        return "";
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
