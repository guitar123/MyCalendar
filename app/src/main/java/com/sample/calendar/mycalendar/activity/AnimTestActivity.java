package com.sample.calendar.mycalendar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.sample.calendar.mycalendar.R;

public class AnimTestActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtn4Anim;
    private Button mBtn5Anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anim_test);
        initView();
    }

    private void initView() {
        mBtn4Anim = (Button) findViewById(R.id.btn_4_anim);
        mBtn5Anim = (Button) findViewById(R.id.btn_5_anim);

        mBtn4Anim.setOnClickListener(this);
        mBtn5Anim.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_4_anim:
                ImageActivity.showActivity(this, ImageActivity.TYPE_ANIM);
                break;
            case R.id.btn_5_anim:
                ImageActivity.showActivity(this, ImageActivity.TYPE_IMAGE);
                break;
        }
    }
}
