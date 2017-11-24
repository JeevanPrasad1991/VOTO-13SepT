package com.cpm.barcodereader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cpm.voto.R;

/**
 * Created by jeevanp on 04-10-2017.
 */
    public class FullScreenScannerFragmentActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_full_screen_scanner_fragment);
        getSupportActionBar().hide();
    }
}
