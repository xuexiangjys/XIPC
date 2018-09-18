package com.xuexiang.xipcdemo.activity;

import android.os.Bundle;

import com.xuexiang.xipcdemo.fragment.MainFragment;
import com.xuexiang.xpage.base.XPageActivity;

public class MainActivity extends XPageActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        openPage(MainFragment.class);
    }
}
