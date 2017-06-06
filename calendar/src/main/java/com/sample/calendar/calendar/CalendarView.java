package com.sample.calendar.calendar;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.media.CamcorderProfile.get;

/**
 * author: ${user}
 * Created by Mr.fan on 2017/6/6.
 * descrition: You share rose get fun. *_* *_*
 * <p>
 * 自定义日历
 */

public class CalendarView extends FrameLayout {

    private ImageView mIvPre;
    private ImageView mIvNext;
    private TextView mTvCurrentTime;
    private LayoutInflater mLayoutInflater;
    private RecyclerView mRvList;
    private Calendar mCalendar;


    public CalendarView(@NonNull Context context) {
        this(context, null);
    }

    public CalendarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mLayoutInflater = LayoutInflater.from(context);
        mCalendar = Calendar.getInstance();
        initControl(context);
    }


    private void initControl(Context context) {
        bindControl(context);
        bindControlEvent();
        renderCalendar();
    }


    private void bindControl(Context context) {
        View view = mLayoutInflater.inflate(R.layout.layout_calendar, this);
        mIvPre = (ImageView) view.findViewById(R.id.iv_pre);
        mIvNext = (ImageView) view.findViewById(R.id.iv_next);
        mTvCurrentTime = (TextView) view.findViewById(R.id.tv_current_time);
        mRvList = (RecyclerView) view.findViewById(R.id.rv_list);
        mRvList.setItemAnimator(new DefaultItemAnimator());
        mRvList.setHasFixedSize(true);
        mRvList.setLayoutManager(new GridLayoutManager(context, 7, GridLayoutManager.VERTICAL, false));
    }

    private void bindControlEvent() {
        mIvPre.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {//上一月
                mCalendar.add(Calendar.MONTH, -1);
                //重新绑定数据
                renderCalendar();
            }
        });

        mIvNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {//下一月
                mCalendar.add(Calendar.MONTH, 1);
                //重新绑定数据
                renderCalendar();
            }
        });
    }

    //绑定日历数据
    private void renderCalendar() {
        setCurrentTime();

        ArrayList<Date> cells = new ArrayList<>();

        //克隆一个日历对象，防止破坏成员类的数据
        Calendar cloneCalendar = (Calendar) mCalendar.clone();

        //首先将月份归于当月的第一天
        cloneCalendar.set(Calendar.DAY_OF_MONTH, 1);
        //计算上一个月剩余的天数
        int preDays = cloneCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        cloneCalendar.add(Calendar.DAY_OF_MONTH, -preDays);

        int maxCellCount = 6 * 7;

        while (cells.size() < maxCellCount) {
            //填充数据
            cells.add(cloneCalendar.getTime());
            //往后加1天
            cloneCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        CalendarAdapter adapter = new CalendarAdapter(getContext(), cells);
        mRvList.setAdapter(adapter);
    }

    private void setCurrentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM yyy");
        mTvCurrentTime.setText(simpleDateFormat.format(mCalendar.getTime()));
    }

}
