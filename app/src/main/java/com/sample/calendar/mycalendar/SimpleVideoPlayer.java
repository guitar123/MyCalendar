package com.sample.calendar.mycalendar;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;


import java.io.IOException;
import java.util.Map;

/**
 * 简单的视频播放器
 *
 * @author gasol
 */

public class SimpleVideoPlayer extends FrameLayout implements TextureView.SurfaceTextureListener {


    public static final int STATE_ERROR = -1;          // 播放错误
    public static final int STATE_IDLE = 0;            // 播放未开始
    public static final int STATE_PREPARING = 1;       // 播放准备中
    public static final int STATE_PREPARED = 2;        // 播放准备就绪
    public static final int STATE_PLAYING = 3;         // 正在播放
    public static final int STATE_PAUSED = 4;          // 暂停播放

    /**
     * 正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，缓冲区数据足够后恢复播放)
     **/
    public static final int STATE_BUFFERING_PLAYING = 5;
    /**
     * 正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，此时暂停播放器，继续缓冲，缓冲区数据足够后恢复暂停)
     **/
    public static final int STATE_BUFFERING_PAUSED = 6;
    public static final int STATE_COMPLETED = 7;       // 播放完成

    public static final int PLAYER_NORMAL = 10;        // 普通播放器
    public static final int PLAYER_FULL_SCREEN = 11;   // 全屏播放器
    public static final int PLAYER_TINY_WINDOW = 12;   // 小窗口播放器

    /**
     * 当前的从播放状态
     */
    private int mCurrentState = STATE_IDLE;
    /**
     * 当前播放器的状态
     */
    private int mPlayerState = PLAYER_NORMAL;

    private Context mContext;

    private FrameLayout mContainer;

    private TextureView mTextureView;//用于渲染视频

    private SurfaceTexture mSurfaceTexture;

    private String mUrl;

    private Map<String, String> mHeaders;

    private MediaPlayer mMediaPlayer;

    private int mBufferPercentage;//缓冲更新的百分比

    public SimpleVideoPlayer(@NonNull Context context) {
        this(context, null);
    }

    public SimpleVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    /**
     * 初始化操作
     */
    private void init() {
        mContainer = new FrameLayout(mContext);
        mContainer.setBackgroundColor(Color.BLACK);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //将黑色背景容器添加到布局中
        this.addView(mContainer, params);
    }

    /**
     * 设置播放的视频资源
     */
    public void setUp(String url, Map<String, String> headers) {
        mUrl = url;
        mHeaders = headers;
    }

    //重新播放视频，首先会释放资源
    public void start() {
        //播放之前首先释放资源，然后重新添加资源
        release();
        if (mCurrentState == STATE_IDLE || mCurrentState == STATE_ERROR || mCurrentState == STATE_COMPLETED) {
            //初始化MediaPlayer
            initMediaPlayer();
            //初始化TextureView
            initTextureView();
            addTextureView();
        }
    }

    //从暂停到播放
    public void restart() {
        if (mCurrentState == STATE_PAUSED) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
            Log.d("fhp", "STATE_PLAYING");
        }
        if (mCurrentState == STATE_BUFFERING_PAUSED) {
            mMediaPlayer.start();
            mCurrentState = STATE_BUFFERING_PLAYING;
            Log.d("fhp", "STATE_BUFFERING_PLAYING");
        }
    }

    //暂停播放
    public void pause() {
        if (mCurrentState == STATE_PLAYING) {
            mMediaPlayer.pause();
            mCurrentState = STATE_PAUSED;
            Log.d("fhp", "STATE_PAUSED");
        }
        if (mCurrentState == STATE_BUFFERING_PLAYING) {
            mMediaPlayer.pause();
            mCurrentState = STATE_BUFFERING_PAUSED;
            Log.d("fhp", "STATE_BUFFERING_PAUSED");
        }
    }


    public boolean isIdle() {
        return mCurrentState == STATE_IDLE;
    }

    public boolean isPreparing() {
        return mCurrentState == STATE_PREPARING;
    }

    public boolean isPrepared() {
        return mCurrentState == STATE_PREPARED;
    }

    public boolean isBufferingPlaying() {
        return mCurrentState == STATE_BUFFERING_PLAYING;
    }

    public boolean isBufferingPaused() {
        return mCurrentState == STATE_BUFFERING_PAUSED;
    }

    public boolean isPlaying() {
        return mCurrentState == STATE_PLAYING;
    }

    public boolean isPaused() {
        return mCurrentState == STATE_PAUSED;
    }

    public boolean isError() {
        return mCurrentState == STATE_ERROR;
    }

    public boolean isCompleted() {
        return mCurrentState == STATE_COMPLETED;
    }

    public boolean isFullScreen() {
        return mPlayerState == PLAYER_FULL_SCREEN;
    }

    public boolean isTinyWindow() {
        return mPlayerState == PLAYER_TINY_WINDOW;
    }

    public boolean isNormal() {
        return mPlayerState == PLAYER_NORMAL;
    }

    public int getDuration() {
        return mMediaPlayer != null ? mMediaPlayer.getDuration() : 0;
    }

    public int getCurrentPosition() {
        return mMediaPlayer != null ? mMediaPlayer.getCurrentPosition() : 0;
    }

    public int getBufferPercentage() {
        return mBufferPercentage;
    }

    /**
     * 初始化MediaPlayer
     */
    private void initMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            //设置音频流类型
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            //设置准备的监听
            mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
            //设置视频尺寸变化的监听
            mMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
            //设置播放完成的监听
            mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
            //设置播放错误的监听
            mMediaPlayer.setOnErrorListener(mOnErrorListener);
            //设置指示信息和警告信息的监听
            mMediaPlayer.setOnInfoListener(mOnInfoListener);
            //设置缓冲的监听
            mMediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        }
    }

    /**
     * 初始化TextureView，并设置监听
     */
    private void initTextureView() {
        if (mTextureView == null) {
            mTextureView = new TextureView(mContext);
            mTextureView.setSurfaceTextureListener(this);
        }
    }

    /**
     * 将TextureView添加到容器中
     */
    private void addTextureView() {
        mContainer.removeView(mTextureView);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.addView(mTextureView, 0, params);
    }

    //*********************** TextureView.SurfaceTextureListener 开始 **********************
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        if (mSurfaceTexture == null) {
            mSurfaceTexture = surfaceTexture;
            openMediaPlayer();
        } else {
            mTextureView.setSurfaceTexture(mSurfaceTexture);
        }
    }

    private void openMediaPlayer() {
        try {
            mMediaPlayer.setDataSource(mContext.getApplicationContext(), Uri.parse(mUrl), mHeaders);
            mMediaPlayer.setSurface(new Surface(mSurfaceTexture));
            mMediaPlayer.prepareAsync();
            mCurrentState = STATE_PREPARING;
            Log.d("fhp", "STATE_PREPARING");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("fhp", "打开播放器发生错误", e);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return mSurfaceTexture == null;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }
    //*********************** TextureView.SurfaceTextureListener 结束 **********************


    /**
     * 准备就绪的监听
     */
    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.start();
            mCurrentState = STATE_PREPARED;//修改当前状态为准备就绪的状态
            Log.d("fhp", "onPrepared ——> STATE_PREPARED");
        }
    };

    /**
     * 视频尺寸变化监听
     */
    private MediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            Log.d("fhp", "onVideoSizeChanged ——> width：" + width + "，height：" + height);
        }
    };

    /**
     * 播放完成的监听
     */
    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mCurrentState = STATE_COMPLETED;
            Log.d("fhp", "onCompletion ——> STATE_COMPLETED");
        }
    };

    /**
     * 播放出错的监听
     */
    private MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            mCurrentState = STATE_ERROR;
            Log.d("fhp", "onError ——> STATE_ERROR ———— what：" + what);
            return false;
        }
    };

    /**
     * 指示信息和警告信息监听
     */
    private MediaPlayer.OnInfoListener mOnInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {//(开始渲染)播放器渲染第一帧
                mCurrentState = STATE_PLAYING;
                Log.d("fhp", "onInfo ——> MEDIA_INFO_VIDEO_RENDERING_START：STATE_PLAYING");
            } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {//(开始缓冲)MediaPlayer暂时不播放，以缓冲更多的数据
                if (mCurrentState == STATE_PAUSED || mCurrentState == STATE_BUFFERING_PAUSED) {
                    mCurrentState = STATE_BUFFERING_PAUSED;
                    Log.d("fhp", "onInfo ——> MEDIA_INFO_BUFFERING_START：STATE_BUFFERING_PAUSED");
                } else {
                    mCurrentState = STATE_BUFFERING_PLAYING;
                    Log.d("fhp", "onInfo ——> MEDIA_INFO_BUFFERING_START：STATE_BUFFERING_PLAYING");
                }
            } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {//缓冲结束(填充缓冲区后)
                if (mCurrentState == STATE_BUFFERING_PLAYING) {//MediaPlayer恢复播放状态
                    mCurrentState = STATE_PLAYING;
                    Log.d("fhp", "onInfo ——> MEDIA_INFO_BUFFERING_END： STATE_PLAYING(继续播放)");
                }
                if (mCurrentState == STATE_BUFFERING_PAUSED) {//MediaPlayer恢复暂停状态
                    mCurrentState = STATE_PAUSED;
                    Log.d("fhp", "onInfo ——> MEDIA_INFO_BUFFERING_END： STATE_PAUSED(继续暂停)");
                }
            } else {
                Log.d("fhp", "onInfo ——> what：" + what);
            }
            return true;
        }
    };

    /**
     * 缓冲更新的监听
     */
    private MediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            //记录缓冲更新的百分比
            mBufferPercentage = percent;
        }
    };

    /**
     * 全屏，将mContainer(内部包含mTextureView和mController)从当前容器中移除，并添加到android.R.content中.
     */
    public void enterFullScreen() {
        //已经是全屏不需要操作
        if (mPlayerState == PLAYER_FULL_SCREEN) return;

        // 隐藏ActionBar、状态栏，并横屏
        VideoUtil.hideActionBar(mContext);
        VideoUtil.scanForActivity(mContext)
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        this.removeView(mContainer);
        ViewGroup contentView = (ViewGroup) VideoUtil.scanForActivity(mContext).findViewById(android.R.id.content);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        contentView.addView(mContainer, params);
        //修改播放器的状态
        mPlayerState = PLAYER_FULL_SCREEN;
        Log.d("fhp", "PLAYER_FULL_SCREEN");
    }

    /**
     * 退出全屏，移除mTextureView和mController，并添加到非全屏的容器中。
     *
     * @return true退出全屏.
     */
    public boolean exitFullScreen() {
        if (mPlayerState == PLAYER_FULL_SCREEN) {
            VideoUtil.showActionBar(mContext);
            VideoUtil.scanForActivity(mContext)
                    .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            ViewGroup contentView = (ViewGroup) VideoUtil.scanForActivity(mContext)
                    .findViewById(android.R.id.content);
            contentView.removeView(mContainer);
            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            this.addView(mContainer, params);
            //修改播放器的状态
            mPlayerState = PLAYER_NORMAL;
            Log.d("fhp", "PLAYER_NORMAL");
            return true;
        }
        return false;
    }

    /**
     * 进入小窗口播放，小窗口播放的实现原理与全屏播放类似。
     */
    public void enterTinyWindow() {
        if (mPlayerState == PLAYER_TINY_WINDOW) return;
        this.removeView(mContainer);

        ViewGroup contentView = (ViewGroup) VideoUtil.scanForActivity(mContext)
                .findViewById(android.R.id.content);
        // 小窗口的宽度为屏幕宽度的60%，长宽比默认为16:9，右边距、下边距为8dp。
        LayoutParams params = new LayoutParams(
                (int) (VideoUtil.getScreenWidth(mContext) * 0.6f),
                (int) (VideoUtil.getScreenWidth(mContext) * 0.6f * 9f / 16f));
        params.gravity = Gravity.BOTTOM | Gravity.END;
        params.rightMargin = VideoUtil.dp2px(mContext, 8f);
        params.bottomMargin = VideoUtil.dp2px(mContext, 8f);

        contentView.addView(mContainer, params);

        mPlayerState = PLAYER_TINY_WINDOW;
        Log.d("fhp", "PLAYER_TINY_WINDOW");
    }

    /**
     * 退出小窗口播放
     */
    public boolean exitTinyWindow() {
        if (mPlayerState == PLAYER_TINY_WINDOW) {//如果是小窗口模式的话，推出小窗口
            ViewGroup contentView = (ViewGroup) VideoUtil.scanForActivity(mContext)
                    .findViewById(android.R.id.content);
            contentView.removeView(mContainer);
            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            this.addView(mContainer, params);

            mPlayerState = PLAYER_NORMAL;
            Log.d("fhp", "PLAYER_NORMAL");
            return true;
        }
        return false;
    }


    /**
     * 释放资源
     */
    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mContainer.removeView(mTextureView);
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        mCurrentState = STATE_IDLE;
        mPlayerState = PLAYER_NORMAL;
    }


}
