package com.sample.calendar.mycalendar.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sample.calendar.mycalendar.R;
import com.sample.calendar.mycalendar.constants.Constants;


/**
 * author: 樊浩鹏
 * Created by fhp on 2017/3/20 10:02.
 * description: You share rose get fun. *_* *_*
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void startActivity(View view, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0以上，使用新动画
            //1. 不断的放大一个view，进而进行activity的过度，这个动画可以兼容4.X版本
            // 第1个参数是scale哪个view的大小，第2和3个参数是以view为基点，从哪开始动画，这里是该view的中心，4和5参数是新的activity从多大开始放大，这里是从无到有的过程。
//            ActivityOptionsCompat options = ActivityOptionsCompat.makeScaleUpAnimation(view, view.getWidth() / 2, view.getHeight() / 2, 0, 0);

            //2.该方法和上面的makeScaleUpAnimation非常相似，只不过，这里是通过放大一个图片，最后过度到一个新的activity，
            // 第2个参数是指那个图片要放大，3和4参数表示从哪开始动画，
//            ActivityOptionsCompat options = ActivityOptionsCompat.makeThumbnailScaleUpAnimation(view,
//                    BitmapFactory.decodeResource(getResources(), R.drawable.c), view.getWidth() / 2, view.getHeight() / 2);

            //3. 场景动画 ,单个View的场景动画
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, Constants.TRANSITION_ANIMATION_NEWS_PHOTOS);

            startActivity(intent, options.toBundle());
        } else {//旧版动画
            ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.bottom_slide_in, R.anim.anim_none);
            //让新的Activity从一个小的范围扩大到全屏
            //ActivityOptionsCompat options = ActivityOptionsCompat.makeScaleUpAnimation(view, view.getWidth() / 2, view.getHeight() / 2, 0, 0);
            ActivityCompat.startActivity(this, intent, options.toBundle());
        }
    }

    protected void startActivity(Intent intent, Pair<View, String>... viewPairs) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0以上，使用新动画
            //3. 场景动画 ,单个View的场景动画
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, viewPairs);
            startActivity(intent, options.toBundle());
        } else {//旧版动画
            ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.bottom_slide_in, R.anim.anim_none);
            //让新的Activity从一个小的范围扩大到全屏
            //ActivityOptionsCompat options = ActivityOptionsCompat.makeScaleUpAnimation(view, view.getWidth() / 2, view.getHeight() / 2, 0, 0);
            ActivityCompat.startActivity(this, intent, options.toBundle());
        }
    }

    /**
     * 不断的放大一个view，进而进行activity的过度，这个动画可以兼容4.X版本
     * 第1个参数是scale哪个view的大小，第2和3个参数是以view为基点，从哪开始动画，
     * 这里是该view的中心，4和5参数是新的activity从多大开始放大，这里是从无到有的过程。
     *
     * @param view
     * @param intent
     */
    protected void startActivityByMakeScaleUpAnimation(View view, Intent intent) {
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeScaleUpAnimation(view, view.getWidth() / 2, view.getHeight() / 2, 0, 0);
        ActivityCompat.startActivity(this, intent, activityOptionsCompat.toBundle());
    }

    /**
     * 该方法和上面的makeScaleUpAnimation非常相似，只不过，这里是通过放大一个图片，最后过度到一个新的activity，
     * 第2个参数是指那个图片要放大，3和4参数表示从哪开始动画，
     *
     * @param view
     * @param intent
     */
    protected void startActivityMakeThumbnailScaleUpAnimation(View view, Intent intent, int imagRes) {
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeThumbnailScaleUpAnimation(view, BitmapFactory.decodeResource(getResources(), imagRes), view.getWidth() / 2, view.getHeight() / 2);
        ActivityCompat.startActivity(this, intent, activityOptionsCompat.toBundle());
    }
}
