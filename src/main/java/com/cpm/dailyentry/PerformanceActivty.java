package com.cpm.dailyentry;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import java.util.ArrayList;

public class PerformanceActivty extends AppCompatActivity {
    GSKDatabase db;
    TextView txt_target, mtd_sale_txt, todaysale_txt,txt_nodata;
    ArrayList<PerformanceGetterSetter> list = new ArrayList<>();
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
        txt_target = (TextView) findViewById(R.id.txt_target);
        mtd_sale_txt = (TextView) findViewById(R.id.mtd_sale_txt);
        todaysale_txt = (TextView) findViewById(R.id.todaysale_txt);
        cardV= (CardView) findViewById(R.id.cardV);
        parentl= (LinearLayout) findViewById(R.id.parentl);
        txt_nodata= (TextView) findViewById(R.id.txt_nodata);
        db = new GSKDatabase(this);
        db.open();
        list = db.getperformancedata();
        if (list.size() > 0) {
            txt_target.setText(list.get(0).getTarget().get(0));
            mtd_sale_txt.setText(list.get(0).getMtdSale().get(0));
            todaysale_txt.setText(list.get(0).getTodaySale().get(0));
        }else {
            cardV.setVisibility(View.INVISIBLE);
            parentl.setVisibility(View.INVISIBLE);
            txt_nodata.setVisibility(View.VISIBLE);
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
