package com.sample.calendar.mycalendar.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sample.calendar.mycalendar.CustomImage;
import com.sample.calendar.mycalendar.R;
import com.sample.calendar.mycalendar.adapter.ImageItemAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class ImageActivity extends BaseActivity {

    private static final String TYPE = "type";

    public static final int TYPE_IMAGE = 852;
    public static final int TYPE_ANIM = 885;

    private int[] imageResource = new int[]{R.drawable.anim1,
            R.drawable.anim1, R.drawable.anim2, R.drawable.anim3, R.drawable.anim4,
            R.drawable.anim5, R.drawable.anim1, R.drawable.anim2, R.drawable.anim3,
            R.drawable.anim4, R.drawable.anim5, R.drawable.anim6};

    private RecyclerView mRvList;
    private int mType;


    public static void showActivity(Context context, int type) {
        Intent intent = new Intent(context, ImageActivity.class);
        intent.putExtra(TYPE, type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        initData();
        initView();
    }

    private void initData() {
        mType = getIntent().getIntExtra(TYPE, -1);
    }

    private void initView() {
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
        mRvList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRvList.setHasFixedSize(true);
        mRvList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRvList.setItemAnimator(new DefaultItemAnimator());

        ArrayList<Integer> imageList = new ArrayList<>();
        for (int i = 0; i < imageResource.length; i++) {
            imageList.add(imageResource[i]);
        }
        ImageItemAdapter adapter = new ImageItemAdapter(this, imageList);
        mRvList.setAdapter(adapter);

        adapter.setOnItemClickListener(new ImageItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, int imageResource) {
                switch (mType) {
                    case TYPE_IMAGE:
                        Intent intent = new Intent(ImageActivity.this, ImageDetailActivity.class);
                        intent.putExtra(ImageDetailActivity.IMAGE_RESOURCE, imageResource);

                        startActivity(view, intent);
                        break;
                    case TYPE_ANIM:
                        imageClick(view, imageResource);
                        break;
                }

            }
        });
    }


    public void imageClick(View view, int imageResource) {
        Intent intent = new Intent(this, ImageDetailActivity.class);
        // 创建一个 rect 对象来存储共享元素位置信息
        Rect rect = new Rect();
        // 获取元素位置信息
        view.getGlobalVisibleRect(rect);
        // 将位置信息附加到 intent 上
        intent.setSourceBounds(rect);

        intent.putExtra(ImageDetailActivity.IMAGE_RESOURCE, imageResource);
        startActivity(intent);
        // 屏蔽 Activity 默认转场效果
        overridePendingTransition(0, 0);
    }
}
