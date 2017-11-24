package com.cpm.dailyentry;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cpm.Constants.CommonString;
import com.cpm.GetterSetter.NavMenuItemGetterSetter;
import com.cpm.database.GSKDatabase;
import com.cpm.voto.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StoreEntryActivity extends AppCompatActivity {
    ValueAdapter adapter;
    RecyclerView recyclerView;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor = null;
    String store_cd, visit_date, user_type, username;
    GSKDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_entry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.drawer_layout_recycle);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        store_cd = preferences.getString(CommonString.KEY_STORE_CD, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        user_type = preferences.getString(CommonString.KEY_USER_TYPE, null);
        getSupportActionBar().setTitle("Store Entry -" + visit_date);
        db = new GSKDatabase(this);
        db.open();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (!user_type.equalsIgnoreCase("ISD")) {
            if (db.isAuditEntryFilled(store_cd)) {

                db.updateCoverageStoreStatus(store_cd, visit_date, CommonString.KEY_VALID);
            }
        } else {
            if (db.isStockEntryWithFilled(store_cd) && db.isMiddayDataFilled(store_cd)) {
                db.updateCoverageStoreStatus(store_cd, visit_date, CommonString.KEY_VALID);
            }
        }

        recyclerView = (RecyclerView) findViewById(R.id.drawer_layout_recycle);
        adapter = new ValueAdapter(getApplicationContext(), getdata());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        this.finish();
    }


    public class ValueAdapter extends RecyclerView.Adapter<ValueAdapter.MyViewHolder> {

        private LayoutInflater inflator;
        List<NavMenuItemGetterSetter> data = Collections.emptyList();

        public ValueAdapter(Context context, List<NavMenuItemGetterSetter> data) {
            inflator = LayoutInflater.from(context);
            this.data = data;

        }

        @Override
        public ValueAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            View view = inflator.inflate(R.layout.custom_row, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ValueAdapter.MyViewHolder viewHolder, final int position) {
            final NavMenuItemGetterSetter current = data.get(position);
            viewHolder.icon.setImageResource(current.getIconImg());
            viewHolder.icon_txtname.setText(current.getIconName());
            viewHolder.icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (current.getIconImg() == R.drawable.audit || current.getIconImg() == R.drawable.audit_done) {
                        Intent in7 = new Intent(getApplicationContext(), AuditActivity.class);
                        startActivity(in7);
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    }

                    if (db.isNoSaleSEntryFilled(store_cd) && current.getIconImg() == R.drawable.sales_entry_done) {
                        Snackbar.make(recyclerView, "You have already click no sale for today. You are not allowed enter more sale today.", Snackbar.LENGTH_LONG).show();
                    } else {
                        if (current.getIconImg() == R.drawable.sales_entry || current.getIconImg() == R.drawable.sales_entry_done) {
                            Intent in7 = new Intent(getApplicationContext(), SaleEntryActivity.class);
                            startActivity(in7);
                            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                        }
                    }

                    if (db.isStockEntryFilled(store_cd) && current.getIconImg() == R.drawable.stock_entry || current.getIconImg() == R.drawable.stock_entry_done) {
                        Snackbar.make(recyclerView, "Stock entry data already uploaded", Snackbar.LENGTH_LONG).show();
                    } else if (db.isStockEntryWithFilled(store_cd) && current.getIconImg() == R.drawable.stock_entry || current.getIconImg() == R.drawable.stock_entry_done) {
                        startActivity(new Intent(getApplicationContext(), StockEntryActivity.class));
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

                    } else {
                        if (current.getIconImg() == R.drawable.stock_entry || current.getIconImg() == R.drawable.stock_entry_done) {
                            startActivity(new Intent(getApplicationContext(), StockEntryActivity.class));
                            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                        }
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView icon;
            TextView icon_txtname;

            public MyViewHolder(View itemView) {
                super(itemView);
                icon = (ImageView) itemView.findViewById(R.id.list_icon);
                icon_txtname = (TextView) itemView.findViewById(R.id.icon_txtname);
            }
        }

    }

    public List<NavMenuItemGetterSetter> getdata() {
        List<NavMenuItemGetterSetter> data = new ArrayList<>();
        int audit, saleentry, stock_entry;
        if (db.isStockEntryFilled(store_cd)) {
            stock_entry = R.drawable.stock_entry_done;
        } else {
            if (db.isStockEntryWithFilled(store_cd)) {
                stock_entry = R.drawable.stock_entry_done;
            } else {
                stock_entry = R.drawable.stock_entry;
            }
        }
        if (db.isAuditEntryFilled(store_cd)) {
            audit = R.drawable.audit_done;
        } else {
            audit = R.drawable.audit;
        }
        if (db.isMiddayDataFilled(store_cd)) {
            saleentry = R.drawable.sales_entry_done;
        } else {
            saleentry = R.drawable.sales_entry;
        }
        if (user_type.equalsIgnoreCase("ISD")) {
            int img[] = {saleentry, stock_entry};
            String name[] = {"Sale Entry", "Stock Entry"};
            for (int i = 0; i < img.length; i++) {
                NavMenuItemGetterSetter recData = new NavMenuItemGetterSetter();
                recData.setIconImg(img[i]);
                recData.setIconName(name[i]);
                data.add(recData);
            }
        } else if (!user_type.equalsIgnoreCase("ISD")) {
            int img[] = {audit};
            String name[] = {"Audit"};
            for (int i = 0; i < img.length; i++) {
                NavMenuItemGetterSetter recData = new NavMenuItemGetterSetter();
                recData.setIconImg(img[i]);
                recData.setIconName(name[i]);
                data.add(recData);
            }
        }
        return data;
    }


}
