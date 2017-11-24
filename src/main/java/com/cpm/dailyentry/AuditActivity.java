package com.cpm.dailyentry;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.cpm.Constants.CommonString;
import com.cpm.database.GSKDatabase;
import com.cpm.voto.R;
import com.cpm.xmlGetterSetter.AditGetterSetter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class AuditActivity extends AppCompatActivity {
    FloatingActionButton fab;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor = null;
    ArrayList<AditGetterSetter> questionDataList = new ArrayList<>();
    RecyclerView audit_list;
    String _UserId, visit_date, store_id;
    AlertDialog alert;
    private GSKDatabase database;
    AuditAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        audit_list = (RecyclerView) findViewById(R.id.audit_list);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        _UserId = preferences.getString(CommonString.KEY_USERNAME, "");
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        store_id = preferences.getString(CommonString.KEY_STORE_CD, "");
        getSupportActionBar().setTitle("Checklist - " + visit_date);

        database = new GSKDatabase(this);
        database.open();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateCondition()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AuditActivity.this);
                    builder.setMessage("Do you want to save the data ").setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                                            database.open();
                                            database.insertAuditdata(store_id, visit_date, _UserId, questionDataList);
                                            Snackbar.make(fab, "Data has been saved", Snackbar.LENGTH_LONG).show();
                                            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                            finish();
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

    @Override
    protected void onResume() {
        super.onResume();
        validateData();
    }

    private void validateData() {
        database.open();
        questionDataList = database.getinsertedDatafromDatabasedata(store_id);
        if (questionDataList.size() > 0) {
        } else {
            questionDataList = database.getauditQuestion();
        }
        adapter = new AuditAdapter(this, questionDataList);
        audit_list.setAdapter(adapter);
        audit_list.setLayoutManager(new LinearLayoutManager(this));
    }

    private class AuditAdapter extends RecyclerView.Adapter<AuditAdapter.MyViewHolder> {
        private LayoutInflater inflator;
        Context context;
        ArrayList<AditGetterSetter> list;

        public AuditAdapter(Context context, ArrayList<AditGetterSetter> list) {
            inflator = LayoutInflater.from(context);
            this.context = context;
            this.list = list;
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflator.inflate(R.layout.audit_adapter, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.txt_audit.setText(list.get(position).getQuest().get(0));

            //for reason spinner
            final ArrayList<AditGetterSetter> reason_list = database.getauditAnswerData(questionDataList.get(position).getQuest_id().get(0));
            AditGetterSetter non = new AditGetterSetter();
            non.setAns("-Select Answer-");
            non.setAns_id("0");
            reason_list.add(0, non);
            holder.spin_auditQ.setAdapter(new ReasonSpinnerAdapter(context, R.layout.spinner_text_view, reason_list));

            for (int i = 0; i < reason_list.size(); i++) {
                if (reason_list.get(i).getAns_id().get(0).equals(list.get(position).getCurrectanswerCd())) {
                    holder.spin_auditQ.setSelection(i);
                    break;
                }
            }
            holder.spin_auditQ.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    if (pos != 0) {
                        AditGetterSetter ans = reason_list.get(pos);
                        list.get(position).setCurrectanswerCd(ans.getAns_id().get(0));
                        list.get(position).setCurrectanswer(ans.getAns().get(0));
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView txt_audit;
            Spinner spin_auditQ;

            public MyViewHolder(View convertView) {
                super(convertView);
                txt_audit = (TextView) convertView.findViewById(R.id.txt_audit);
                spin_auditQ = (Spinner) convertView.findViewById(R.id.spin_auditQ);

            }
        }

    }

    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE)
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                AuditActivity.this.finish();
                                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
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
                                    AuditActivity.this.finish();
                                    overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
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
        return super.onOptionsItemSelected(item);
    }

    public class ReasonSpinnerAdapter extends ArrayAdapter<AditGetterSetter> {
        List<AditGetterSetter> list;
        Context context;
        int resourceId;

        public ReasonSpinnerAdapter(Context context, int resourceId, ArrayList<AditGetterSetter> list) {
            super(context, resourceId, list);
            this.context = context;
            this.list = list;
            this.resourceId = resourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            LayoutInflater inflater = getLayoutInflater();
            view = inflater.inflate(resourceId, parent, false);
            AditGetterSetter cm = list.get(position);

            TextView txt_spinner = (TextView) view.findViewById(R.id.txt_sp_text);
            txt_spinner.setText(list.get(position).getAns().get(0));

            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            LayoutInflater inflater = getLayoutInflater();
            view = inflater.inflate(resourceId, parent, false);
            AditGetterSetter cm = list.get(position);
            TextView txt_spinner = (TextView) view.findViewById(R.id.txt_sp_text);
            txt_spinner.setText(cm.getAns().get(0));

            return view;
        }

    }

    boolean validateCondition() {
        boolean result = true;
        if (questionDataList.size() > 0) {
            for (int i = 0; i < questionDataList.size(); i++) {
                if (questionDataList.get(i).getCurrectanswerCd().equals("")) {
                    message("Please select answer");
                    result = false;
                    break;
                }
            }
        }
        return result;

    }

    private void message(String msg) {
        Snackbar.make(fab, msg, Snackbar.LENGTH_SHORT).show();
    }


}
