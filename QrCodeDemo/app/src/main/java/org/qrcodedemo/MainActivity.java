package org.qrcodedemo;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFragment();
    }

    protected void initFragment() {
        QrCodeFragment qrCodeFragment = new QrCodeFragment();
        FragmentManager spm = getSupportFragmentManager();
        spm.beginTransaction().replace(R.id.fl_container, qrCodeFragment).commit();
    }
}
