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
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.Collections;

public class SaleEntryActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    GSKDatabase db;
    String app_ver;
    String datacheck = "";
    String[] words;
    String validity;
    private Dialog dialog;
    private ProgressBar pb;
    private TextView percentage, message, tv_title;
    ArrayList<CoverageBean> cDatalist = new ArrayList<>();
    String store_cd, visit_date, username, model = "", model_cd = "";
    private SharedPreferences preferences;
    private ArrayAdapter<CharSequence> model_adapter;
    ArrayList<SaleEntryGetterSetter> insertedlist_Data = new ArrayList<>();
    ArrayList<ModelGetterSetter> model_list = new ArrayList<>();
    boolean uploadstatusflag = false;
    FloatingActionButton btn_add, save_fab;
    RecyclerView salelist;
    MyAdapter adapter;
    NestedScrollView scroll;
    Spinner salespin;
    EditText imei_no;
    Data data;
    boolean saveflag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_entry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btn_add = (FloatingActionButton) findViewById(R.id.btn_add);
        save_fab = (FloatingActionButton) findViewById(R.id.save_fab);
        salelist = (RecyclerView) findViewById(R.id.demos_list);
        scroll = (NestedScrollView) findViewById(R.id.scroll);
        imei_no = (EditText) findViewById(R.id.imei_no);
        salespin = (Spinner) findViewById(R.id.models);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        store_cd = preferences.getString(CommonString.KEY_STORE_CD, null);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        app_ver = preferences.getString(CommonString.KEY_VERSION, "");
        db = new GSKDatabase(this);
        db.open();
        model_list = db.getmodeldata();
        model_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        model_adapter.add("-Select Model-");


        for (int i = 0; i < model_list.size(); i++) {
            model_adapter.add(model_list.get(i).getModel().get(0));
        }


        salespin.setAdapter(model_adapter);
        model_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        salespin.setOnItemSelectedListener(this);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SaleEntryActivity.this);
                    builder.setMessage("Are you sure you want to add")
                            .setCancelable(false)
                            .setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            db.open();
                                            SaleEntryGetterSetter secdata = new SaleEntryGetterSetter();
                                            secdata.setModelno(model_cd);
                                            secdata.setModel(model);
                                            secdata.setImeino(imei_no.getText().toString().trim());
                                            insertedlist_Data.add(secdata);
                                            Collections.reverse(insertedlist_Data);
                                            adapter = new MyAdapter(SaleEntryActivity.this, insertedlist_Data);
                                            salelist.setAdapter(adapter);
                                            salelist.setLayoutManager(new LinearLayoutManager(SaleEntryActivity.this));
                                            adapter.notifyDataSetChanged();
                                            Snackbar.make(btn_add, "Data has been saved", Snackbar.LENGTH_SHORT).show();
                                            imei_no.setText("");
                                            salespin.setSelection(0);
                                        }
                                    })
                            .setNegativeButton("No",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }

        });
        save_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = false;
                if (insertedlist_Data.size() > 0) {
                    for (int i = 0; i < insertedlist_Data.size(); i++) {
                        if (insertedlist_Data.get(i).getSatus().equals("N")) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SaleEntryActivity.this);
                        builder.setMessage("Are you sure you want to save and upload Sale Entry data")
                                .setCancelable(false)
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                db.open();
                                                long l = db.insertSalesEntrydata(store_cd, username, visit_date, insertedlist_Data);
                                                if (l > 0) {
                                                    saveflag = true;
                                                    boolean flag = false;
                                                    if (checkNetIsAvailable()) {
                                                        dialog.dismiss();
                                                        for (int i = 0; i < insertedlist_Data.size(); i++) {
                                                            if (insertedlist_Data.get(i).getSatus().equals("N")) {
                                                                flag = true;
                                                                break;
                                                            }
                                                        }
                                                        if (flag) {
                                                            new DemosuploadTask(SaleEntryActivity.this).execute();
                                                        } else {
                                                            Snackbar.make(btn_add, "No data upload", Snackbar.LENGTH_SHORT).show();
                                                        }

                                                    } else {
                                                        Snackbar.make(btn_add, "Data has been saved", Snackbar.LENGTH_SHORT).show();
                                                    }
                                                }


                                            }
                                        })
                                .setNegativeButton("No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                dialog.cancel();
                                            }
                                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }

                } else {
                    Snackbar.make(btn_add, "Please add sub sale entry data", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        setDataToListView();
        scroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    save_fab.hide();
                } else {
                    save_fab.show();
                }
            }
        });


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.models:
                if (position != 0) {
                    model = model_list.get(position - 1).getModel().get(0);
                    model_cd = model_list.get(position - 1).getModel_cd().get(0);
                } else {
                    model = "";
                    model_cd = "";
                }
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void showMessage(String message) {
        Snackbar.make(btn_add, message, Snackbar.LENGTH_SHORT).show();
    }

    public boolean validation() {
        boolean value = true;
        if (imei_no.getText().toString().isEmpty()) {
            value = false;
            showMessage("Please Enter IMEI Number");
        } else if (imei_no.getText().toString().length() != 15) {
            value = false;
            showMessage("Please Enter 15 Digit IMEI Number");
        } else if (salespin.getSelectedItem().toString().equalsIgnoreCase("-Select Model-")) {
            value = false;
            showMessage("Please Select Model Dropdown");
        }
        return value;
    }

    public void setDataToListView() {
        try {
            insertedlist_Data = db.getinsertedSalesEntrydata(store_cd);
            if (insertedlist_Data.size() > 0) {
                Collections.reverse(insertedlist_Data);
                adapter = new MyAdapter(this, insertedlist_Data);
                salelist.setAdapter(adapter);
                salelist.setLayoutManager(new LinearLayoutManager(SaleEntryActivity.this));
                adapter.notifyDataSetChanged();
            }

        } catch (Exception e) {
            Log.d("Exception when fetching", e.toString());
        }
    }


    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private LayoutInflater inflator;
        Context context;
        ArrayList<SaleEntryGetterSetter> insertedlist_Data;

        MyAdapter(Context context, ArrayList<SaleEntryGetterSetter> insertedlist_Data) {
            inflator = LayoutInflater.from(context);
            this.context = context;
            this.insertedlist_Data = insertedlist_Data;

        }

        @Override
        public int getItemCount() {
            return insertedlist_Data.size();

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflator.inflate(R.layout.secondary_adapter, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.status.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (insertedlist_Data.get(position).getKey_id() == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SaleEntryActivity.this);
                        builder.setMessage("Are you sure you want to Delete")
                                .setCancelable(false)
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                insertedlist_Data.remove(position);
                                                if (insertedlist_Data.size() > 0) {
                                                    MyAdapter adapter = new MyAdapter(SaleEntryActivity.this, insertedlist_Data);
                                                    salelist.setAdapter(adapter);
                                                    adapter.notifyDataSetChanged();
                                                }
                                                notifyDataSetChanged();
                                            }
                                        })
                                .setNegativeButton("No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                dialog.cancel();
                                            }
                                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SaleEntryActivity.this);
                        builder.setMessage("Are you sure you want to Delete")
                                .setCancelable(false)
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                String listid = insertedlist_Data.get(position).getKey_id();
                                                db.remove(listid);
                                                notifyDataSetChanged();
                                                insertedlist_Data.remove(position);
                                                if (insertedlist_Data.size() > 0) {
                                                    MyAdapter adapter = new MyAdapter(SaleEntryActivity.this, insertedlist_Data);
                                                    salelist.setAdapter(adapter);
                                                    adapter.notifyDataSetChanged();
                                                }
                                                notifyDataSetChanged();
                                            }
                                        })
                                .setNegativeButton("No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                dialog.cancel();
                                            }
                                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }


                }
            });
            if (insertedlist_Data.get(position).getSatus().equals(CommonString.KEY_U)) {
                holder.status.setImageResource(R.drawable.tick);
                holder.status.setEnabled(false);
            } else {
                holder.status.setEnabled(true);
                holder.status.setImageResource(R.drawable.close);
            }

            holder.txt_name.setText(insertedlist_Data.get(position).getImeino());
            holder.modelno.setText(insertedlist_Data.get(position).getModel());
            holder.txt_name.setId(position);
            holder.modelno.setId(position);
            holder.status.setId(position);
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView txt_name, modelno;
            ImageView status;

            public MyViewHolder(View convertView) {
                super(convertView);
                txt_name = (TextView) convertView.findViewById(R.id.txt_name);
                modelno = (TextView) convertView.findViewById(R.id.txt_modelno_txt);
                status = (ImageView) convertView.findViewById(R.id.imgDelRow);
            }
        }
    }

    public boolean checkNetIsAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE)
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                    SaleEntryActivity.this.finish();
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();

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
                                SaleEntryActivity.this.finish();
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

    private class DemosuploadTask extends AsyncTask<Void, Data, String> {
        boolean flag = true;
        private Context context;

        DemosuploadTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom_upload);
            dialog.setTitle("Uploading Sales Data..");
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
                            + ""
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
                    //demos data
                    final_xml = "";
                    onXML = "";
                    insertedlist_Data = db.getinsertedSalesEntrydata(store_cd);
                    if (insertedlist_Data.size() > 0) {
                        uploadstatusflag = false;
                        for (int j = 0; j < insertedlist_Data.size(); j++) {
                            String flag = "";
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
                                for (int i = 0; i < insertedlist_Data.size(); i++) {
                                    long l = db.updateSaleDataStatus(store_cd, insertedlist_Data.get(i).getKey_id(),
                                            CommonString.KEY_U);
                                }
                                return CommonString.KEY_SUCCESS;
                            }
                            data.value = 70;
                            data.name = "100";
                            publishProgress(data);
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
                    Snackbar.make(btn_add, "Demos data upload successfully", Snackbar.LENGTH_SHORT).show();
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

    class Data {
        int value;
        String name;
    }

    public void showAlert(String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SaleEntryActivity.this);
        builder.setTitle("Parinaam");
        builder.setMessage(str).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


}
