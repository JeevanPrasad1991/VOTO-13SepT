package com.cpm.download;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cpm.Constants.CommonString;
import com.cpm.database.GSKDatabase;
import com.cpm.delegates.TableBean;
import com.cpm.message.AlertMessage;
import com.cpm.pdfviewer.DocumentActivityActivity;
import com.cpm.voto.R;
import com.cpm.xmlGetterSetter.DocumentGetterSetter;
import com.cpm.xmlHandler.XMLHandlers;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

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

/**
 * Created by jeevanp on 05-10-2017.
 */

public class DownloadHrDocumentActivty extends AppCompatActivity {
    private Dialog dialog;
    private ProgressBar pb;
    private TextView percentage, message;
    private Data data;
    int eventType;
    GSKDatabase db;
    TableBean tb;
    String _UserId;
    private SharedPreferences preferences;
    DocumentGetterSetter document;
    private SharedPreferences.Editor editor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        _UserId = preferences.getString(CommonString.KEY_USERNAME, "");
        tb = new TableBean();
        db = new GSKDatabase(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        new BackgroundTask(this).execute();
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
                data.value = 50;
                data.name = "Downloding document.....";
                publishProgress(data);
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", "testmer");
                request.addProperty("Type", "HR_DOCUMENTS");
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
                    document = XMLHandlers.DocumentHandler(xpp, eventType);
                    if (document.getDocument_id().size() > 0) {
                        resultHttp = CommonString.KEY_SUCCESS;
                        String document_Table = document.getTable_HR_DOCUMENTS();
                        TableBean.setHrDocuments(document_Table);
                    } else {
                        return "HR_DOCUMENTS";
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
                db.insertDocumentData(document);
                data.value = 100;
                data.name = "Finishing";
                publishProgress(data);
                return resultHttp;
            } catch (MalformedURLException e) {
                //  final AlertMessage message = new AlertMessage(Compl.this, AlertMessage.MESSAGE_EXCEPTION, "download", e);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
//                        message.showMessage();
                    }
                });
            } catch (IOException e) {
                //  final AlertMessage message = new AlertMessage(CompleteDownloadActivity.this, AlertMessage.MESSAGE_SOCKETEXCEPTION, "socket", e);

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        //  message.showMessage();

                    }
                });

            } catch (Exception e) {
                //  final AlertMessage message = new AlertMessage(CompleteDownloadActivity.this, AlertMessage.MESSAGE_EXCEPTION + e, "download", e);

                e.getMessage();
                e.printStackTrace();
                e.getCause();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //  message.showMessage();
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
            if (result.equals(CommonString.KEY_SUCCESS)) {
              finish();
            } else {
              finish();
            }


        }

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
