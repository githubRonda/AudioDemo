package com.ronda.audiodemo.ui;

import android.os.Bundle;

import com.ronda.audiodemo.R;

/**
 * Created by Ronda on 2017/12/28.
 */

public class PlaceholderActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placeholder);
        initializeToolbar();
    }
}
