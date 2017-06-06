package com.sample.calendar.calendar;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

/**
 * author: ${user}
 * Created by Mr.fan on 2017/6/6.
 * descrition: You share rose get fun. *_* *_*
 */

public class CalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<Date> mDataList;

    public CalendarAdapter(Context context, List<Date> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CalendarHolder(mLayoutInflater.inflate(R.layout.item_calendar, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        bindCalendarData((CalendarHolder) holder, position);
    }

    private void bindCalendarData(CalendarHolder holder, int position) {
        Date date = mDataList.get(position);
        holder.tv_date.setText(date.getDate() + "");
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public class CalendarHolder extends RecyclerView.ViewHolder {
        public TextView tv_date;

        public CalendarHolder(View itemView) {
            super(itemView);
            this.tv_date = (TextView) itemView.findViewById(R.id.tv_date);
        }
    }

}
