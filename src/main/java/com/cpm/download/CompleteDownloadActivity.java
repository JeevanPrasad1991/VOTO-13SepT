package com.cpm.download;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Calendar;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cpm.Constants.CommonString;
import com.cpm.voto.R;
import com.cpm.database.GSKDatabase;
import com.cpm.delegates.TableBean;

import com.cpm.fragment.MainFragment;
import com.cpm.message.AlertMessage;
import com.cpm.xmlGetterSetter.AditGetterSetter;
import com.cpm.xmlGetterSetter.DocumentGetterSetter;
import com.cpm.xmlGetterSetter.JourneyPlanGetterSetter;
import com.cpm.xmlGetterSetter.ModelGetterSetter;
import com.cpm.xmlGetterSetter.NonWorkingReasonGetterSetter;

import com.cpm.xmlGetterSetter.PerformanceGetterSetter;
import com.cpm.xmlGetterSetter.SupTeamGetterSetter;
import com.cpm.xmlHandler.XMLHandlers;

public class CompleteDownloadActivity extends AppCompatActivity {
    private Dialog dialog;
    private ProgressBar pb;
    private TextView percentage, message;
    private Data data;
    int eventType;
    JourneyPlanGetterSetter jcpgettersetter;
    NonWorkingReasonGetterSetter nonworkinggettersetter;
    ModelGetterSetter modelGetterSetter;
    PerformanceGetterSetter performanceGetterSetter;
    SupTeamGetterSetter supTeamGetterSetter;
    AditGetterSetter aditGetterSetter;
    GSKDatabase db;
    TableBean tb;
    String _UserId, user_type, SUPERVISOR_JCP_TYPE;
    private SharedPreferences preferences;
    DocumentGetterSetter document;
    private SharedPreferences.Editor editor = null;
    boolean networkfaillerflag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        _UserId = preferences.getString(CommonString.KEY_USERNAME, "");
        user_type = preferences.getString(CommonString.KEY_USER_TYPE, null);
        SUPERVISOR_JCP_TYPE = preferences.getString(CommonString.KEY_SUPERVISOR_JCP_TYPE, null);
        tb = new TableBean();
        db = new GSKDatabase(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        new BackgroundTask(this).execute();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        FragmentManager fragmentManager = getSupportFragmentManager();
        MainFragment cartfrag = new MainFragment();
        fragmentManager.beginTransaction().replace(R.id.frame_layout, cartfrag).commit();
    }

    class Data {
        int value;
        String name;
    }

    private class BackgroundTask extends AsyncTask<Void, Data, String> {
        private Context context;

        BackgroundTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom);
            //dialog.setTitle("Download Files");
            dialog.setCancelable(false);
            dialog.show();
            pb = (ProgressBar) dialog.findViewById(R.id.progressBar1);
            percentage = (TextView) dialog.findViewById(R.id.percentage);
            message = (TextView) dialog.findViewById(R.id.message);

        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            String resultHttp = "";
            try {

                data = new Data();
                data.name = "Downloading data..";
                data.value = 10;
                publishProgress(data);
                // JCP
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);

                /////for tl
                if (!user_type.equalsIgnoreCase("ISD")) {
                    if (SUPERVISOR_JCP_TYPE.equalsIgnoreCase("JCP")) {
                        request.addProperty("UserName", _UserId);
                        request.addProperty("Type", "JOURNEY_PLAN_SUP");
                        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                        envelope.dotNet = true;
                        envelope.setOutputSoapObject(request);
                        HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);
                        androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                        Object result = (Object) envelope.getResponse();
                        if (result.toString() != null) {
                            xpp.setInput(new StringReader(result.toString()));
                            xpp.next();
                            jcpgettersetter = XMLHandlers.JCPXMLHandler(xpp, eventType);
                            String jcpTable = jcpgettersetter.getTable_journey_plan();
                            TableBean.setjcptable(jcpTable);
                            if (jcpgettersetter.getStore_cd().size() > 0) {
                                resultHttp = CommonString.KEY_SUCCESS;
                                data.value = 20;
                                data.name = "JCP Data Downloading";
                                publishProgress(data);
                            } else {
                                return "JOURNEY_PLAN_SUP";
                            }
                            data.value = 10;
                            data.name = "JOURNEY_PLAN_SUP Data Downloading";
                            publishProgress(data);
                        }
                    }
                }


                /////for isd JOURNEY_PLAN
                if (jcpgettersetter == null){
                    request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", _UserId);
                request.addProperty("Type", "JOURNEY_PLAN");
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);
                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                Object result = (Object) envelope.getResponse();
                if (result.toString() != null) {
                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    jcpgettersetter = XMLHandlers.JCPXMLHandler(xpp, eventType);
                    String jcpTable = jcpgettersetter.getTable_journey_plan();
                    TableBean.setjcptable(jcpTable);
                    if (jcpgettersetter.getStore_cd().size() > 0) {
                        resultHttp = CommonString.KEY_SUCCESS;
                        data.value = 20;
                        data.name = "JCP Data Downloading";
                        publishProgress(data);
                    } else {
                        if (user_type.equalsIgnoreCase("ISD")) {
                            return "JOURNEY_PLAN";
                        }

                    }
                }
            }

            ///for TEAM_LIST_SUP
                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", _UserId);
                request.addProperty("Type", "TEAM_LIST_SUP");
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);
                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                Object result1 = (Object) envelope.getResponse();
                if (result1.toString() != null) {
                    xpp.setInput(new StringReader(result1.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    supTeamGetterSetter = XMLHandlers.SupTeamXML(xpp, eventType);
                    String jcpTable = supTeamGetterSetter.getSupteamTable();
                    TableBean.setSupteamtable(jcpTable);
                    if (supTeamGetterSetter.getEmp_cd().size() > 0) {
                        data.value = 10;
                        data.name = "TEAM_LIST_SUP Data Downloading";
                        publishProgress(data);
                        resultHttp = CommonString.KEY_SUCCESS;
                    } else {
                        if (!SUPERVISOR_JCP_TYPE.equalsIgnoreCase("JCP") && !user_type.equalsIgnoreCase("ISD")) {
                            return "TEAM_LIST_SUP";
                        }
                    }

                }


                //  MODEL_MASTER data
                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", _UserId);
                request.addProperty("Type", "MODEL_MASTER");
                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                androidHttpTransport = new HttpTransportSE(CommonString.URL);
                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                Object resultnonworking = (Object) envelope.getResponse();
                if (resultnonworking.toString() != null) {
                    xpp.setInput(new StringReader(resultnonworking.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    modelGetterSetter = XMLHandlers.ModelXML(xpp, eventType);
                    String modeltable = modelGetterSetter.getModeltable();
                    TableBean.setModeltable(modeltable);
                    if (modelGetterSetter.getModel_cd().size() > 0) {
                        resultHttp = CommonString.KEY_SUCCESS;
                        data.value = 30;
                        data.name = "MODEL_MASTER Downloading";
                        publishProgress(data);
                    } else {
                        if (user_type.equalsIgnoreCase("ISD")) {
                            return "MODEL_MASTER";
                        }

                    }

                }


                //  MY_PERFORMANCE data
                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", _UserId);
                request.addProperty("Type", "MY_PERFORMANCE");
                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                androidHttpTransport = new HttpTransportSE(CommonString.URL);
                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                resultnonworking = (Object) envelope.getResponse();
                if (resultnonworking.toString() != null) {
                    xpp.setInput(new StringReader(resultnonworking.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    performanceGetterSetter = XMLHandlers.PerformanceDataXML(xpp, eventType);
                    String modeltable = performanceGetterSetter.getPerformanceTable();
                    TableBean.setPerformancetable(modeltable);
                    if (performanceGetterSetter.getTarget().size() > 0) {
                        resultHttp = CommonString.KEY_SUCCESS;
                        data.value = 60;
                        data.name = "MY_PERFORMANCE Downloading";
                        publishProgress(data);
                    }
                }


                //Non Working Reason data
                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", _UserId);
                request.addProperty("Type", "NON_WORKING_REASON_NEW");
                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                androidHttpTransport = new HttpTransportSE(CommonString.URL);
                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                resultnonworking = (Object) envelope.getResponse();
                if (resultnonworking.toString() != null) {
                    xpp.setInput(new StringReader(resultnonworking.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    nonworkinggettersetter = XMLHandlers.nonWorkinReasonXML(xpp, eventType);
                    String nonworkingtable = nonworkinggettersetter.getNonworking_table();
                    TableBean.setNonworkingtable(nonworkingtable);
                    if (nonworkinggettersetter.getReason_cd().size() > 0) {
                        resultHttp = CommonString.KEY_SUCCESS;
                    } else {
                        return "NON_WORKING_REASON_NEW";
                    }
                    data.value = 70;
                    data.name = "Non Working Reason Downloading";
                    publishProgress(data);

                }


                //AUDIT_QUESTION_SUP data
                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", _UserId);
                request.addProperty("Type", "AUDIT_QUESTION_SUP");
                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                androidHttpTransport = new HttpTransportSE(CommonString.URL);
                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                resultnonworking = (Object) envelope.getResponse();
                if (resultnonworking.toString() != null) {
                    xpp.setInput(new StringReader(resultnonworking.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    aditGetterSetter = XMLHandlers.auditQuestionXML(xpp, eventType);
                    String table = aditGetterSetter.getAuditTable();
                    TableBean.setAuditquestiontable(table);
                    if (aditGetterSetter.getAns_id().size() > 0) {
                        resultHttp = CommonString.KEY_SUCCESS;
                        data.value = 77;
                        data.name = "AUDIT_QUESTION_SUP Downloading";
                        publishProgress(data);
                    } else {
                        if (user_type.equalsIgnoreCase("Supervisor") || user_type.equalsIgnoreCase("Team Leader")) {
                            return "AUDIT_QUESTION_SUP";
                        }
                    }
                }

                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", "testmer");
                request.addProperty("Type", "HR_DOCUMENTS");
                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                androidHttpTransport = new HttpTransportSE(CommonString.URL);
                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                resultnonworking = (Object) envelope.getResponse();
                if (resultnonworking.toString() != null) {
                    xpp.setInput(new StringReader(resultnonworking.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    document = XMLHandlers.DocumentHandler(xpp, eventType);
                    String document_Table = document.getTable_HR_DOCUMENTS();
                    TableBean.setHrDocuments(document_Table);
                    if (document.getDocument_id().size() > 0) {
                        resultHttp = CommonString.KEY_SUCCESS;
                        data.value = 80;
                        data.name = "HR_DOCUMENTS Downloading";
                        publishProgress(data);
                    } else {
                        // return "HR_DOCUMENTS";
                    }

                    for (int i = 0; i < document.getDocument_id().size(); i++) {
                        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                        File folder = new File(extStorageDirectory, "Hr_Documents");
                        folder.mkdir();
                        File pdfFile = new File(folder, document.getDocument_name().get(i) + ".pdf");
                        try {
                            pdfFile.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String path = extStorageDirectory + "Hr_Documents/";
                        downloadFile(document.getDocument_url().get(i), document.getDocument_name().get(i) + ".pdf", folder);
                    }
                }

                publishProgress(data);
                db.open();
                if (!user_type.equalsIgnoreCase("ISD")) {
                    if (SUPERVISOR_JCP_TYPE.equalsIgnoreCase("JCP")) {
                        db.insertJCPData(jcpgettersetter);
                        db.insertauditQuestionData(aditGetterSetter);
                    } else {
                        db.insertSupTeamData(supTeamGetterSetter);
                        db.insertauditQuestionData(aditGetterSetter);

                    }
                } else {
                    db.insertJCPData(jcpgettersetter);
                }

                db.insertNonWorkingReasonData(nonworkinggettersetter);
                db.insertModelData(modelGetterSetter);
                db.insertMyPerformanceData(performanceGetterSetter);
                db.insertDocumentData(document);
                data.value = 100;
                data.name = "Finishing";
                publishProgress(data);
                editor.putString(CommonString.KEY_PERFORMACE_TIME, getCurrentTime());
                editor.commit();

                return resultHttp;
            } catch (MalformedURLException e) {
                networkfaillerflag = false;
                final AlertMessage message = new AlertMessage(CompleteDownloadActivity.this, AlertMessage.MESSAGE_EXCEPTION, "download", e);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        message.showMessage();
                    }
                });

            } catch (IOException e) {
                networkfaillerflag = false;
                final AlertMessage message = new AlertMessage(CompleteDownloadActivity.this, AlertMessage.MESSAGE_SOCKETEXCEPTION, "socket", e);

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        message.showMessage();

                    }
                });

            } catch (Exception e) {
                networkfaillerflag = false;
                final AlertMessage message = new AlertMessage(CompleteDownloadActivity.this, AlertMessage.MESSAGE_EXCEPTION + e, "download", e);
                e.getMessage();
                e.printStackTrace();
                e.getCause();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        message.showMessage();
                    }
                });
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
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            dialog.dismiss();
            if (networkfaillerflag) {
                if (result.equals(CommonString.KEY_SUCCESS)) {
                    AlertMessage message = new AlertMessage(CompleteDownloadActivity.this, AlertMessage.MESSAGE_DOWNLOAD, "success", null);
                    message.showMessage();
                } else {
                    AlertMessage message = new AlertMessage(CompleteDownloadActivity.this, AlertMessage.MESSAGE_JCP_FALSE + result, "success", null);
                    message.showMessage();
                }
            }


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


    public void downloadFile(String fileUrl, String directory, File folder_path) {
        try {
            final int MEGABYTE = 1024 * 1024;
            URL url = new URL(fileUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.getResponseCode();
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                int length = urlConnection.getContentLength();
                String size = new DecimalFormat("##.##").format((double) ((double) length / 1024)) + " KB";
                if (!new File(folder_path.getPath() + directory).exists() && !size.equalsIgnoreCase("0 KB")) {
                    File outputFile = new File(folder_path, directory);
                    FileOutputStream fos = new FileOutputStream(outputFile);
                    InputStream is1 = (InputStream) urlConnection.getInputStream();
                    int bytes = 0;
                    byte[] buffer = new byte[1024];
                    int len1 = 0;
                    while ((len1 = is1.read(buffer)) != -1) {
                        bytes = (bytes + len1);
                        fos.write(buffer, 0, len1);

                    }
                    fos.close();
                    is1.close();

                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
