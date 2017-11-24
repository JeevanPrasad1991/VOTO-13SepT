package com.cpm.download;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import com.cpm.Constants.CommonString;
import com.cpm.dailyentry.PerformanceActivty;
import com.cpm.dailyentry.StockEntryActivity;
import com.cpm.database.GSKDatabase;
import com.cpm.delegates.TableBean;
import com.cpm.fragment.MainFragment;
import com.cpm.message.AlertMessage;
import com.cpm.voto.R;
import com.cpm.xmlGetterSetter.PerformanceGetterSetter;
import com.cpm.xmlHandler.XMLHandlers;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.Calendar;

/**
 * Created by jeevanp on 05-10-2017.
 */

public class DownloadPerformanceActivity extends AppCompatActivity {
    private Dialog dialog;
    private ProgressBar pb;
    private TextView percentage, message, tv_title;
    private Data data;
    int eventType;
    PerformanceGetterSetter performanceGetterSetter;
    GSKDatabase db;
    TableBean tb;
    String _UserId;
    private SharedPreferences preferences;
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
            dialog.setCancelable(false);
            dialog.show();
            pb = (ProgressBar) dialog.findViewById(R.id.progressBar1);
            percentage = (TextView) dialog.findViewById(R.id.percentage);
            message = (TextView) dialog.findViewById(R.id.message);
            tv_title = (TextView) dialog.findViewById(R.id.tv_title);
            tv_title.setText("Updating performance data");
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            String resultHttp = "";
            try {
                data = new Data();
                data.value = 50;
                data.name = "Updating performance data....";
                publishProgress(data);
                //  MY_PERFORMANCE data
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", _UserId);
                request.addProperty("Type", "MY_PERFORMANCE");
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);
                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                Object resultnonworking = (Object) envelope.getResponse();
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
                        data.name = "MY_PERFORMANCE Updating data";
                        publishProgress(data);
                    }
                }
                db.open();
                db.insertMyPerformanceData(performanceGetterSetter);
                data.value = 100;
                data.name = "Finishing";
                publishProgress(data);
                editor.putString(CommonString.KEY_PERFORMACE_TIME, getCurrentTime());
                editor.commit();
                return resultHttp;
            } catch (MalformedURLException e) {
               /* runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        showAlert(AlertMessage.MESSAGE_EXCEPTION);
                    }
                });*/
            } catch (IOException e) {

            } catch (XmlPullParserException e) {
                e.printStackTrace();
               /* runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        showAlert(AlertMessage.MESSAGE_EXCEPTION);
                    }
                });*/
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
                Toast.makeText(DownloadPerformanceActivity.this, "Performance updated.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DownloadPerformanceActivity.this, PerformanceActivty.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(DownloadPerformanceActivity.this, "Performance updated.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DownloadPerformanceActivity.this, PerformanceActivty.class);
                startActivity(intent);
                finish();
            }
        }

    }

    public void showAlert(String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DownloadPerformanceActivity.this);
        builder.setTitle("Parinaam");
        builder.setMessage(str).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
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


}
