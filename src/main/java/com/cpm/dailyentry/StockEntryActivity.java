package com.cpm.dailyentry;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cpm.Constants.CommonString;
import com.cpm.database.GSKDatabase;
import com.cpm.delegates.CoverageBean;
import com.cpm.message.AlertMessage;
import com.cpm.voto.R;
import com.cpm.xmlGetterSetter.ModelGetterSetter;
import com.cpm.xmlGetterSetter.SaleEntryGetterSetter;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class StockEntryActivity extends AppCompatActivity {
    RecyclerView stock_list;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor = null;
    String store_cd, visit_date, user_type, username;
    ArrayList<ModelGetterSetter> llist = new ArrayList<>();
    ArrayList<CoverageBean> cDatalist = new ArrayList<>();
    GSKDatabase db;
    FloatingActionButton save_fab;
    AlertDialog alert;
    boolean statusflag = false;
    boolean uploaded_flag = false;
    String[] words;
    String validity;
    private Dialog dialog;
    private ProgressBar pb;
    private TextView percentage, message, tv_title;
    Data data;
    String datacheck = "", app_ver;
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_entry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        stock_list = (RecyclerView) findViewById(R.id.stock_list);
        save_fab = (FloatingActionButton) findViewById(R.id.save_fab);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        store_cd = preferences.getString(CommonString.KEY_STORE_CD, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        app_ver = preferences.getString(CommonString.KEY_VERSION, "");
        getSupportActionBar().setTitle("Stock Entry -" + visit_date);

        db = new GSKDatabase(this);
        db.open();
        validate();
        stock_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                    save_fab.hide();
                else if (dy < 0)
                    save_fab.show();
            }
        });
        save_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stock_list.clearFocus();
                stock_list.invalidate();
                adapter.notifyDataSetChanged();
                if (checkvalidatation()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(StockEntryActivity.this);
                    builder.setMessage("Do you want to save and upload posm data").setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                                            db.open();
                                            long l = 0;
                                            if (!statusflag) {
                                                l = db.insertStockEntrydata(username, store_cd, visit_date, llist);
                                                Snackbar.make(save_fab, "Data has been saved", Snackbar.LENGTH_SHORT).show();
                                                if (checkNetIsAvailable()) {
                                                    new StockuploadTask(StockEntryActivity.this, llist).execute();
                                                    dialog.dismiss();
                                                } else {
                                                    overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                                    finish();
                                                }
                                            } else {
                                                if (llist.size() > 0) {
                                                    if (checkNetIsAvailable()) {
                                                        new StockuploadTask(StockEntryActivity.this, llist).execute();
                                                        dialog.dismiss();
                                                    } else {
                                                        Snackbar.make(save_fab, "No internet connection", Snackbar.LENGTH_LONG).show();
                                                        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                                        finish();
                                                    }
                                                }
                                            }


                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int id) {
                                            dialog.cancel();
                                        }
                                    });

                    alert = builder.create();
                    alert.show();


                }

            }
        });


    }


    public boolean checkNetIsAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private boolean checkvalidatation() {
        boolean flag = true;
        if (llist.size() > 0) {
            for (int i = 0; i < llist.size(); i++) {
                if (llist.get(i).getStockQuantity().equals("")) {
                    flag = false;
                    showMessage("Please fill stock entry quantity");
                    break;
                }
            }
        }
        return flag;
    }

    public void showMessage(String message) {
        Snackbar.make(stock_list, message, Snackbar.LENGTH_LONG).show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE)
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                StockEntryActivity.this.finish();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void validate() {
        db.open();
        llist = db.getinsertedStockEntryData(visit_date, store_cd);
        if (llist.size() > 0) {
        } else {
            llist = db.getmodeldata();
        }
        adapter = new MyAdapter(this, llist);
        stock_list.setAdapter(adapter);
        stock_list.setLayoutManager(new LinearLayoutManager(this));

    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private LayoutInflater inflator;
        Context context;
        ArrayList<ModelGetterSetter> list;

        MyAdapter(Context context, ArrayList<ModelGetterSetter> insertedlist_Data) {
            inflator = LayoutInflater.from(context);
            this.context = context;
            this.list = insertedlist_Data;

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflator.inflate(R.layout.secondary_adapter_new, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.modelname.setText(list.get(position).getModel().get(0));
            holder.model_quantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        final EditText Caption = (EditText) v;
                        String value1 = Caption.getText().toString().replaceFirst("^0+(?!$)", "");
                        if (!value1.equals("")) {
                            try {
                                list.get(position).setStockQuantity(value1);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        } else {
                            list.get(position).setStockQuantity("");
                        }
                    }
                }
            });

            holder.model_quantity.setText(list.get(position).getStockQuantity());
            holder.modelname.setId(position);
            holder.model_quantity.setId(position);
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView modelname;
            EditText model_quantity;

            public MyViewHolder(View convertView) {
                super(convertView);
                modelname = (TextView) convertView.findViewById(R.id.modelname);
                model_quantity = (EditText) convertView.findViewById(R.id.model_quantity);
            }
        }
    }

    private class StockuploadTask extends AsyncTask<Void, Data, String> {
        boolean flag = true;
        private Context context;
        ArrayList<ModelGetterSetter> list;

        StockuploadTask(Context context, ArrayList<ModelGetterSetter> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom_upload);
            dialog.setTitle("Uploading Stock Data..");
            dialog.setCancelable(false);
            dialog.show();
            pb = (ProgressBar) dialog.findViewById(R.id.progressBar1);
            percentage = (TextView) dialog.findViewById(R.id.percentage);
            message = (TextView) dialog.findViewById(R.id.message);
            tv_title = (TextView) dialog.findViewById(R.id.tv_title);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                data = new Data();
                data.value = 10;
                data.name = "Uploading";
                publishProgress(data);
                db.open();
                cDatalist = db.getCoverageSpecificData(store_cd);
                if (cDatalist.size() > 0) {
                    String onXML = "[DATA][USER_DATA][STORE_CD]"
                            + cDatalist.get(0).getStoreId()
                            + "[/STORE_CD]" + "[VISIT_DATE]"
                            + visit_date
                            + "[/VISIT_DATE][LATITUDE]"
                            + cDatalist.get(0).getLatitude()
                            + "[/LATITUDE][APP_VERSION]"
                            + app_ver
                            + "[/APP_VERSION][LONGITUDE]"
                            + cDatalist.get(0).getLongitude()
                            + "[/LONGITUDE][IN_TIME]"
                            + cDatalist.get(0).getInTime()
                            + "[/IN_TIME][OUT_TIME]"
                            + "00:00:00"
                            + "[/OUT_TIME][UPLOAD_STATUS]"
                            + "N"
                            + "[/UPLOAD_STATUS][USER_ID]"
                            + username
                            + "[/USER_ID]" +
                            "[IMAGE_URL]"
                            + cDatalist.get(0).getImage()
                            + "[/IMAGE_URL]"
                            +
                            "[IMAGE_URL1]"
                            + ""
                            + "[/IMAGE_URL1]"
                            +
                            "[REASON_ID]"
                            + "0"
                            + "[/REASON_ID]" +
                            "[REASON_REMARK]"
                            + ""
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
                        if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                        }
                    }
                    String final_xml = "";
                    final int mid = Integer.parseInt((words[1]));
                    data.value = 30;
                    data.name = "Coverage data Uploading";
                    publishProgress(data);
                    final_xml = "";
                    onXML = "";
                    if (mid > 0) {
                        if (list.size() > 0) {
                            for (int j = 0; j < list.size(); j++) {
                                uploaded_flag = false;
                                if (!list.get(j).getStaus().equals(CommonString.KEY_U)) {
                                    uploaded_flag = true;
                                    onXML = "[STOCK_ENTRY_DATA][MID]"
                                            + mid
                                            + "[/MID]"
                                            + "[CREATED_BY]"
                                            + username
                                            + "[/CREATED_BY]"
                                            + "[QUANTITY]"
                                            + list.get(j).getStockQuantity()
                                            + "[/QUANTITY]"
                                            + "[MODEL_CD]"
                                            + list.get(j).getModel_cd().get(0)
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
                                    statusflag = true;
                                    for (int k = 0; k < list.size(); k++) {
                                        list.get(k).setStaus(CommonString.KEY_U);
                                    }
                                }

                                data.value = 50;
                                data.name = "POSM_DATA";
                                publishProgress(data);
                            }
                        }
                    }

                }
                return CommonString.KEY_SUCCESS;

            } catch (IOException e) {
                flag = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showAlert(AlertMessage.MESSAGE_SOCKETEXCEPTION);
                    }
                });
            } catch (Exception e) {
                flag = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showAlert(AlertMessage.MESSAGE_EXCEPTION);
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
            if (flag) {
                if (result.contains(CommonString.KEY_SUCCESS)) {
                    db.open();
                    Snackbar.make(save_fab, "Stock entry data upload successfully", Snackbar.LENGTH_SHORT).show();
                    overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                    finish();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showAlert(AlertMessage.MESSAGE_SOCKETEXCEPTION);
                        }
                    });
                }
            }

        }

    }

    public void showAlert(String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(StockEntryActivity.this);
        builder.setTitle("Parinaam");
        builder.setMessage(str).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    class Data {
        int value;
        String name;
    }


}
