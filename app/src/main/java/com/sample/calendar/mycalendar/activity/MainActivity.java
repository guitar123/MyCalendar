package com.sample.calendar.mycalendar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.sample.calendar.mycalendar.R;

/**
 *
 * @author gasol
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {

        mBtnAnim = (Button) findViewById(R.id.btnAnim);
        mBtnAnim.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAnim:
                startActivity(new Intent(this, AnimTestActivity.class));
                break;
        }
    }
}
