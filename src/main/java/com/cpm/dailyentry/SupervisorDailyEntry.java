package com.cpm.dailyentry;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.Constants.CommonString;
import com.cpm.database.GSKDatabase;
import com.cpm.delegates.CoverageBean;
import com.cpm.message.AlertMessage;
import com.cpm.voto.R;
import com.cpm.xmlGetterSetter.JourneyPlanGetterSetter;
import com.cpm.xmlGetterSetter.SupTeamGetterSetter;
import com.cpm.xmlHandler.XMLHandlers;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by jeevanp on 14-11-2017.
 */
public class SupervisorDailyEntry extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
    String merchandisorname = "", merchandisor_id = "", merchandisor_user, SUPERVISOR_JCP_TYPE, visit_date;
    private ArrayAdapter<CharSequence> isd_adapter;
    ArrayList<SupTeamGetterSetter> selectedISDList = new ArrayList<>();
    ArrayList<SupTeamGetterSetter> isdlist = new ArrayList<>();
    ArrayList<JourneyPlanGetterSetter> jcplist;
    ArrayList<CoverageBean> coverage;
    String store_cd;
    boolean result_flag = false, leaveflag = false;
    JourneyPlanGetterSetter jcpData;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor = null;
    private Dialog dialog, dialog1;
    private ProgressBar pb;
    private TextView percentage, message;
    RelativeLayout nodata_rl;
    ListView lv, dialog_list;
    private Data data;
    int eventType;
    Button go;
    Spinner spin_isd;
    CardView card;
    GSKDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.supervisordailyentry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        SUPERVISOR_JCP_TYPE = preferences.getString(CommonString.KEY_SUPERVISOR_JCP_TYPE, null);
        getSupportActionBar().setTitle("Store List - " + visit_date);

    }

    public boolean checkNetIsAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
//////////////////////////////////////
        ui();

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.go:
                if (checkNetIsAvailable()) {
                    if (validateSpiValueisd()) {
                        new BackgroundTask(this, merchandisor_user).execute();
                    }
                } else {
                    Toast.makeText(SupervisorDailyEntry.this, "Please check internet connection", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public boolean validateSpiValueisd() {
        boolean flag = true;
        if (spin_isd.getSelectedItem().toString().equalsIgnoreCase("-Select ISD-")) {
            flag = false;
            Toast.makeText(SupervisorDailyEntry.this, "Please select any ISD ", Toast.LENGTH_LONG).show();
        }
        return flag;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        this.finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spin_isd:
                if (position != 0) {
                    merchandisorname = isdlist.get(position).getEmp().get(0);
                    merchandisor_id = isdlist.get(position).getEmp_cd().get(0);
                    merchandisor_user = isdlist.get(position).getUserN().get(0);
                } else {
                    merchandisorname = "";
                    merchandisor_id = "";
                    merchandisor_user = "";
                }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private class BackgroundTask extends AsyncTask<Void, Data, String> {
        private Context context;
        private String merchan;
        private boolean flag = true;

        BackgroundTask(Context context, String merchan) {
            this.merchan = merchan;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_upload);
            dialog.setTitle("downloading Data");
            dialog.setCancelable(false);
            dialog.show();
            pb = (ProgressBar) dialog.findViewById(R.id.progressBar1);
            percentage = (TextView) dialog.findViewById(R.id.percentage);
            message = (TextView) dialog.findViewById(R.id.message);
        }

        @SuppressWarnings("deprecation")
        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            XmlPullParserFactory factory = null;
            try {
                data = new Data();
                data.value = 10;
                data.name = "downloading  Data";
                publishProgress(data);
                factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                // JCP Master
                SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", merchan);
                request.addProperty("Type", "NON_JOURNEY_PLAN_SUP");
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
                    jcpData = XMLHandlers.JCPXMLHandler(xpp, eventType);

                    db.jcpjourneyPlan("CREATE TABLE IF NOT EXISTS JOURNEY_PLAN_SUP(STORE_CD INTEGER, EMP_CD INTEGER, VISIT_DATE TEXT, KEYACCOUNT TEXT, STORENAME TEXT, CITY TEXT, STORETYPE TEXT, UPLOAD_STATUS TEXT, CHECKOUT_STATUS TEXT)");
                }
                if (jcpData.getStore_cd().size() == 0) {
                    return "No jcp found";
                } else {
                    data.value = 20;
                    data.name = "downloading  jcp data";
                    publishProgress(data);
                }

                db.open();
                db.InsertTEMPJCPDATA(jcpData);
                data.value = 90;
                data.name = "downloading  mapping promotion data";
                publishProgress(data);
                return CommonString.KEY_SUCCESS;
            } catch (IOException e) {
                flag = false;
                final AlertMessage message = new AlertMessage(
                        SupervisorDailyEntry.this,
                        AlertMessage.MESSAGE_SOCKETEXCEPTION + " (" + e.toString() + ")", "merchandiser_downlaod", e);

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        message.showMessage();

                    }
                });
            } catch (Exception e) {
                flag = false;
                final AlertMessage message = new AlertMessage(
                        SupervisorDailyEntry.this,
                        AlertMessage.MESSAGE_EXCEPTION + " (" + e.toString() + ")", "merchandiser_downlaod", e);

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
                if (result.equals(CommonString.KEY_SUCCESS)) {
                    db.open();
                    ArrayList<JourneyPlanGetterSetter> tempStorelistData = new ArrayList<>();
                    tempStorelistData = db.getJourneyPlanTempData(visit_date);
                    Toast.makeText(SupervisorDailyEntry.this, AlertMessage.MESSAGE_DOWNLOAD, Toast.LENGTH_LONG).show();
                    searchStorePopup(SupervisorDailyEntry.this, tempStorelistData);
                } else if (!result.equals("")) {
                    Toast.makeText(SupervisorDailyEntry.this, result.toString(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SupervisorDailyEntry.this, AlertMessage.MESSAGE_SOCKETEXCEPTION, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    class Data {
        int value;
        String name;
    }

    public void searchStorePopup(final SupervisorDailyEntry context, final ArrayList<JourneyPlanGetterSetter> listItems) {
        dialog1 = new Dialog(SupervisorDailyEntry.this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.search_store_popup);
        dialog_list = (ListView) dialog1.findViewById(R.id.dialog_list);
        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog1.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        wlp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        wlp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        dialog1.setCanceledOnTouchOutside(true);
        dialog1.show();
        DialogMyAdapter adapter = new DialogMyAdapter(context, listItems);
        dialog_list.setAdapter(adapter);
        listItems.size();
        dialog_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<JourneyPlanGetterSetter> jcpGetterSetterArrayList;
                db.open();
                store_cd = listItems.get(position).getStore_cd().get(0);
                jcpGetterSetterArrayList = db.getJCPData(visit_date);
                if (jcpGetterSetterArrayList.size() > 0) {
                    for (int k = 0; k < jcpGetterSetterArrayList.size(); k++) {
                        // jcpparentList
                        if (store_cd.equals(jcpGetterSetterArrayList.get(k).getStore_cd().get(0))) {
                            Toast.makeText(SupervisorDailyEntry.this, "This Store Already Checked " + "Please Visit Another Store", Toast.LENGTH_LONG).show();
                            break;
                        } else {
                            boolean flag = true;
                            if (listItems.get(0).getUploadStatus().equals("D") ||
                                    listItems.get(0).getUploadStatus().equals(CommonString.KEY_U)) {
                                flag = false;
                                Toast.makeText(SupervisorDailyEntry.this, "This Store Already Uploaded " + "Please Visit Another Store", Toast.LENGTH_LONG).show();
                                break;
                            } else {
                                if (flag) {
                                    showMyDialogFortemporary(listItems);
                                }
                            }
                        }
                    }
                } else {
                    showMyDialogFortemporary(listItems);
                }
            }
        });

    }

    void showMyDialogFortemporary(final ArrayList<JourneyPlanGetterSetter> object) {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogbox);
        RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radiogrpvisit);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.yes) {
                    dialog.dismiss();
                    db.open();
                    db.insertJCPDataforSup(object);
                    Intent in = new Intent(SupervisorDailyEntry.this, AttendenceActivity.class);
                    startActivity(in);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(CommonString.KEY_STORE_CD, object.get(0).getStore_cd().get(0));
                    editor.putString(CommonString.KEY_STOREVISITED_STATUS, "Yes");
                    editor.putString(CommonString.KEY_STORE_NAME, object.get(0).getStore_name().get(0));
                    editor.putString(CommonString.KEY_EMP_CD, object.get(0).getEmp_cd().get(0));
                    editor.commit();
                    overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                    SupervisorDailyEntry.this.finish();
                } else if (checkedId == R.id.no) {
                    db.insertJCPDataforSup(object);
                    Intent in = new Intent(SupervisorDailyEntry.this, NonWorkingReason.class);
                    startActivity(in);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(CommonString.KEY_STORE_CD, object.get(0).getStore_cd().get(0));
                    editor.putString(CommonString.KEY_STORE_NAME, object.get(0).getStore_name().get(0));
                    editor.putString(CommonString.KEY_EMP_CD, object.get(0).getEmp_cd().get(0));
                    editor.commit();
                    overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                    dialog.dismiss();
                    SupervisorDailyEntry.this.finish();
                }
            }
        });
        dialog.show();
    }


    class DialogMyAdapter extends BaseAdapter {
        Context context;
        ArrayList<JourneyPlanGetterSetter> parentlist;

        DialogMyAdapter(Context context, ArrayList<JourneyPlanGetterSetter> list_parent) {
            this.context = context;
            this.parentlist = list_parent;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return parentlist.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolderfotTemp holder = null;
            if (convertView == null) {
                holder = new ViewHolderfotTemp();
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.dialogstorelistrow, null);
                holder.storename = (TextView) convertView.findViewById(R.id.storelistviewxml_storename_d);
                holder.city = (TextView) convertView.findViewById(R.id.storelistviewxml_name_d);
                holder.keyaccount = (TextView) convertView.findViewById(R.id.storelistviewxml_storeaddress_d);
                holder.card_view = (CardView) convertView.findViewById(R.id.card_view);
                holder.img = (ImageView) convertView.findViewById(R.id.storelistviewxml_storeico_d);
                holder.storenamelistview_layout = (RelativeLayout) convertView.findViewById(R.id.storenamelistview_layout1);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolderfotTemp) convertView.getTag();
            }
            holder.storename.setText(parentlist.get(position).getStore_name().get(0));
            holder.city.setText(parentlist.get(position).getCity().get(0));
            holder.keyaccount.setText(parentlist.get(position).getKey_account().get(0));
            holder.img.setVisibility(View.VISIBLE);
            holder.img.setBackgroundResource(R.drawable.store);
            holder.card_view.setId(position);
            return convertView;
        }

        class ViewHolderfotTemp {
            TextView storename, city, keyaccount;
            CardView card_view;
            ImageView img;
            RelativeLayout storenamelistview_layout;
        }
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return jcplist.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            MyAdapter.ViewHolder holder = null;
            if (convertView == null) {
                holder = new MyAdapter.ViewHolder();
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.storelistrow, null);
                holder.storename = (TextView) convertView.findViewById(R.id.storelistviewxml_storename);
                holder.city = (TextView) convertView.findViewById(R.id.storelistviewxml_name);
                holder.keyaccount = (TextView) convertView.findViewById(R.id.storelistviewxml_storeaddress);
                holder.img = (ImageView) convertView.findViewById(R.id.storelistviewxml_storeico);
                holder.checkout = (Button) convertView.findViewById(R.id.chkout);
                holder.card_view = (CardView) convertView.findViewById(R.id.card_view);
                convertView.setTag(holder);
            } else {
                holder = (MyAdapter.ViewHolder) convertView.getTag();
            }
            holder.checkout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (checkNetIsAvailable()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SupervisorDailyEntry.this);
                        builder.setTitle("Parinaam").setMessage(R.string.alertmessage);
                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(SupervisorDailyEntry.this, AttendenceActivity.class);
                                startActivity(i);
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    } else {
                        Snackbar.make(lv, "Check internet connection", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    }
                }
            });
            String storecd = jcplist.get(position).getStore_cd().get(0);
            ArrayList<CoverageBean> coverage_data = db.getCoverageSpecificData(storecd);
            if (jcplist.get(position).getUploadStatus().get(0).equals(CommonString.KEY_U)) {
                holder.img.setVisibility(View.VISIBLE);
                holder.img.setBackgroundResource(R.drawable.tick);
                holder.checkout.setVisibility(View.INVISIBLE);
            } else if (coverage_data.size() > 0) {
                if (coverage_data.get(0).getStatus().equals(CommonString.STORE_STATUS_LEAVE)) {
                    holder.img.setBackgroundResource(R.drawable.leave_tick);
                    holder.img.setVisibility(View.VISIBLE);
                    holder.checkout.setVisibility(View.INVISIBLE);
                } else if (!coverage_data.get(0).getOutTime().equals("")) {
                    holder.img.setVisibility(View.VISIBLE);
                    holder.img.setBackgroundResource(R.drawable.exclamation);
                    holder.checkout.setVisibility(View.INVISIBLE);
                } else if (coverage_data.get(0).getStatus().equals(CommonString.KEY_INVALID)) {
                    holder.checkout.setVisibility(View.GONE);
                    holder.checkout.setEnabled(true);
                    holder.img.setBackgroundResource(R.drawable.store);
                    holder.img.setVisibility(View.VISIBLE);
                    holder.card_view.setCardBackgroundColor(Color.GREEN);
                } else if (coverage_data.get(0).getStatus().equals(CommonString.KEY_VALID)) {
                    holder.checkout.setBackgroundResource(R.drawable.checkout);
                    holder.checkout.setVisibility(View.VISIBLE);
                    holder.checkout.setEnabled(true);
                    holder.img.setBackgroundResource(R.drawable.store);
                    holder.img.setVisibility(View.VISIBLE);
                    holder.card_view.setCardBackgroundColor(Color.GREEN);
                } else {
                    holder.card_view.setCardBackgroundColor(Color.parseColor("#FFE0B2"));
                }
            } else {
                holder.checkout.setEnabled(false);
                holder.checkout.setVisibility(View.INVISIBLE);
                holder.img.setVisibility(View.VISIBLE);
                holder.img.setBackgroundResource(R.drawable.store);
                holder.card_view.setCardBackgroundColor(Color.parseColor("#FFE0B2"));
            }
            holder.storename.setText(jcplist.get(position).getStore_name().get(0));
            holder.city.setText(jcplist.get(position).getCity().get(0));
            holder.keyaccount.setText(jcplist.get(position).getKey_account().get(0));
            return convertView;
        }

        private class ViewHolder {
            TextView storename, city, keyaccount;
            ImageView img;
            Button checkout;
            CardView card_view;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        // TODO Auto-generated method stub
        store_cd = jcplist.get(position).getStore_cd().get(0);
        final String upload_status = jcplist.get(position).getUploadStatus().get(0);
        final String checkoutstatus = jcplist.get(position).getCheckOutStatus().get(0);
        final String storename = jcplist.get(position).getStore_name().get(0);
        editor = preferences.edit();
        editor.putString(CommonString.KEY_STORE_CD, store_cd);
        editor.commit();
        if (upload_status.equals(CommonString.KEY_U)) {
            Snackbar.make(lv, "All Data Uploaded", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        } else if (leavesetflag(store_cd)) {
            Snackbar.make(lv, " Store Closed", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        } else if (!setcheckedmenthod(store_cd)) {
            boolean enteryflag = true;
            if (coverage.size() > 0) {
                int i;
                for (i = 0; i < coverage.size(); i++) {
                    if (coverage.get(i).getInTime() != null) {
                        if (coverage.get(i).getOutTime().equals("")) {
                            if (!store_cd.equals(coverage.get(i).getStoreId())) {
                                Snackbar.make(lv, "Please checkout from current store", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                                enteryflag = false;
                            }
                            break;
                        }
                    }
                }
            }
            if (enteryflag) {
                showMyDialog(store_cd, jcplist.get(position).getStore_name().get(0), "Yes",
                        jcplist.get(position).getVISIT_DATE().get(0), jcplist.get(position).getCheckOutStatus().get(0));
            }
        } else {
            Snackbar.make(lv, "Store already checked out", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        }


    }

    void showMyDialog(final String storeCd, final String storeName, final String status, final String visitDate, final String checkout_status) {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogbox);
        RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radiogrpvisit);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.yes) {
                    if (SUPERVISOR_JCP_TYPE.equalsIgnoreCase("JCP")) {
                        db.updateStoreStatusOnCheckout(storeCd, visit_date, CommonString.KEY_INVALID);
                        editor = preferences.edit();
                        editor.putString(CommonString.KEY_STOREVISITED_STATUS, "Yes");
                        editor.putString(CommonString.KEY_STORE_NAME, storeName);
                        editor.putString(CommonString.KEY_STORE_CD, store_cd);
                        editor.commit();
                    }

                    dialog.cancel();
                    boolean flag = true;
                    if (coverage.size() > 0) {
                        for (int i = 0; i < coverage.size(); i++) {
                            if (store_cd.equals(coverage.get(i).getStoreId())) {
                                flag = false;
                                break;
                            }
                        }
                    }
                    if (!flag) {
                        Intent in = new Intent(SupervisorDailyEntry.this, StoreEntryActivity.class);
                        startActivity(in);
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    } else {
                        Intent in = new Intent(SupervisorDailyEntry.this, AttendenceActivity.class);
                        startActivity(in);
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    }
                } else if (checkedId == R.id.no) {
                    dialog.cancel();
                    if (checkout_status.equals(CommonString.KEY_INVALID) || checkout_status.equals(CommonString.KEY_VALID)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SupervisorDailyEntry.this);
                        builder.setMessage(CommonString.DATA_DELETE_ALERT_MESSAGE)
                                .setCancelable(false)
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                if (SUPERVISOR_JCP_TYPE.equalsIgnoreCase("JCP")) {
                                                    UpdateData(storeCd);
                                                    SharedPreferences.Editor editor = preferences.edit();
                                                    editor.putString(CommonString.KEY_STORE_CD, storeCd);
                                                    editor.putString(CommonString.KEY_STOREVISITED_STATUS, "");
                                                    editor.commit();
                                                    Intent in = new Intent(SupervisorDailyEntry.this, NonWorkingReason.class);
                                                    startActivity(in);
                                                } else {
                                                    UpdateData(storeCd);
                                                    Intent in = new Intent(SupervisorDailyEntry.this, NonWorkingReason.class);
                                                    startActivity(in);
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
                    } else {
                        if (SUPERVISOR_JCP_TYPE.equalsIgnoreCase("JCP")) {
                            UpdateData(storeCd);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(CommonString.KEY_STORE_CD, storeCd);
                            editor.putString(CommonString.KEY_STOREVISITED_STATUS, "");
                            editor.commit();
                            Intent in = new Intent(SupervisorDailyEntry.this, NonWorkingReason.class);
                            startActivity(in);
                        } else {
                            UpdateData(storeCd);
                            Intent in = new Intent(SupervisorDailyEntry.this, NonWorkingReason.class);
                            startActivity(in);
                        }
                    }
                }
            }

        });
        dialog.show();
    }

    public boolean setcheckedmenthod(String store_cd) {
        for (int i = 0; i < coverage.size(); i++) {
            if (store_cd.equals(coverage.get(i).getStoreId())) {
                if (!coverage.get(i).getOutTime().equals("")) {
                    result_flag = true;
                    break;
                }
            } else {
                result_flag = false;
            }
        }
        return result_flag;
    }

    public boolean leavesetflag(String store_cd) {
        for (int i = 0; i < coverage.size(); i++) {
            if (store_cd.equals(coverage.get(i).getStoreId())) {
                if (!coverage.get(i).getReasonid().equalsIgnoreCase("0")) {
                    leaveflag = true;
                    break;
                }
            } else {
                leaveflag = false;
            }

        }
        return leaveflag;
    }

    public void UpdateData(String storeCd) {
        db.open();
        db.deleteSpecificStoreDataDailyE(storeCd);
        db.updateStoreStatusOnCheckout(storeCd, jcplist.get(0).getVISIT_DATE().get(0), "N");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void ui() {
        lv = (ListView) findViewById(R.id.list_sup);
        card = (CardView) findViewById(R.id.card);
        spin_isd = (Spinner) findViewById(R.id.spin_isd);
        go = (Button) findViewById(R.id.go);
        nodata_rl = (RelativeLayout) findViewById(R.id.nodata_rl);
        db = new GSKDatabase(this);
        db.open();
        isdlist = db.getISDlist();
        coverage = db.getCoveragDailyEntry(visit_date);
        jcplist = db.getJCPData(visit_date);
        //for isd
        isd_adapter = new ArrayAdapter<>(this, R.layout.spinner_custom_item);
        if (isdlist.size() > 0) {
            SupTeamGetterSetter ch = new SupTeamGetterSetter();
            ch.setEmp_cd("0");
            ch.setEmp("-Select ISD-");
            isdlist.add(0, ch);
        }
        for (int i = 0; i < isdlist.size(); i++) {
            isd_adapter.add(isdlist.get(i).getEmp().get(0));
        }
        isd_adapter.setDropDownViewResource(R.layout.spinner_custom_item);
        spin_isd.setAdapter(isd_adapter);

        //for jcp
        if (SUPERVISOR_JCP_TYPE.equalsIgnoreCase("JCP") && jcplist.size() > 0) {
            card.setVisibility(View.GONE);
            nodata_rl.setVisibility(View.GONE);
            lv.setVisibility(View.VISIBLE);
        } else {
            if (coverage.size() > 0 && isdlist.size() > 0) {
                String isdEmp_cd = "";
                for (int k = 0; k < coverage.size(); k++) {
                    if (coverage.get(k).getOutTime().equals("")) {
                        isdEmp_cd = coverage.get(k).getEmp_cd();
                        break;
                    } else {
                        isdEmp_cd = "";
                    }
                }
                selectedISDList = db.getselectedISDSpinData(isdEmp_cd);
                if (selectedISDList.size() > 0) {
                    for (int l = 0; l < isdlist.size(); l++) {
                        if (isdlist.get(l).getEmp().get(0).equalsIgnoreCase(selectedISDList.get(0).getEmp().get(0))) {
                            spin_isd.setSelection(l);
                            spin_isd.setEnabled(false);
                            go.setEnabled(false);
                            break;

                        } else {
                            spin_isd.setEnabled(true);
                            go.setEnabled(true);
                        }
                    }
                }else {
                    spin_isd.setEnabled(true);
                    go.setEnabled(true);
                }
            } else {
                if (isdlist.size() > 0 && jcplist.size() > 0) {
                    spin_isd.setSelection(0);
                    spin_isd.setEnabled(true);
                    go.setEnabled(true);
                } else {
                    if (isdlist.size() > 0) {
                        spin_isd.setSelection(0);
                        spin_isd.setEnabled(true);
                        go.setEnabled(true);
                        nodata_rl.setVisibility(View.VISIBLE);
                    }
                }
            }
        }

        lv.invalidateViews();
        if (jcplist.size() > 0) {
            nodata_rl.setVisibility(View.GONE);
            lv.setAdapter(new MyAdapter());
            lv.setOnItemClickListener(this);
            lv.setVisibility(View.VISIBLE);
            //lv.invalidateViews();
        }
        go.setOnClickListener(this);
        spin_isd.setOnItemSelectedListener(this);
        lv.setOnItemClickListener(this);
    }

}
