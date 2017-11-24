package com.cpm.dailyentry;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cpm.Constants.CommonString;
import com.cpm.database.GSKDatabase;
import com.cpm.voto.R;
import com.cpm.xmlGetterSetter.PerformanceGetterSetter;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PerformanceActivty extends AppCompatActivity {
    GSKDatabase db;
    TextView txt_target, mtd_sale_txt, todaysale_txt, txt_nodata, refress_date;
    ArrayList<PerformanceGetterSetter> list = new ArrayList<>();
    private SharedPreferences preferences = null;
    private String visit_date, performanceupdatetime = "";
    LinearLayout parentl;
    CardView cardV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance_activty);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        performanceupdatetime = preferences.getString(CommonString.KEY_PERFORMACE_TIME, null);
        refress_date = (TextView) findViewById(R.id.refress_date);
        txt_target = (TextView) findViewById(R.id.txt_target);
        mtd_sale_txt = (TextView) findViewById(R.id.mtd_sale_txt);
        todaysale_txt = (TextView) findViewById(R.id.todaysale_txt);
        cardV = (CardView) findViewById(R.id.cardV);
        parentl = (LinearLayout) findViewById(R.id.parentl);
        txt_nodata = (TextView) findViewById(R.id.txt_nodata);
        getSupportActionBar().setTitle("My Performance -" + visit_date);

        db = new GSKDatabase(this);
        db.open();
        list = db.getperformancedata();
        if (list.size() > 0) {
            txt_target.setText(list.get(0).getTarget().get(0));
            mtd_sale_txt.setText(list.get(0).getMtdSale().get(0));
            todaysale_txt.setText(list.get(0).getTodaySale().get(0));
        } else {
            cardV.setVisibility(View.INVISIBLE);
            parentl.setVisibility(View.INVISIBLE);
            txt_nodata.setVisibility(View.VISIBLE);
        }

        try {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();
            refress_date.setText("Last Refreshed :" + dateFormat.format(date) + "-" + performanceupdatetime);
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            PerformanceActivty.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        PerformanceActivty.this.finish();
    }
}
