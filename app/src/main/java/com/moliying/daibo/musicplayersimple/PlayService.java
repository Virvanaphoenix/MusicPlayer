package com.moliying.daibo.musicplayersimple;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by daibo on 16/7/12.
 */
public class PlayService extends Service {

    private MediaPlayer mediaPlayer; // 媒体播放器对象
    private String path;
    private int currentTime;        //当前播放进度
    private int duration;           //播放长度
    private int current = 0;        // 记录当前正在播放的音乐

    private boolean isPause;        // 暂停状态
    private int msg;


    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        path = intent.getStringExtra("url");        //歌曲路径
        current = intent.getIntExtra("listPosition", -1);   //当前播放歌曲的在mp3Infos的位置
        msg = intent.getIntExtra("MSG", 0);         //播放信息
        if (msg == Config.PLAY_MSG) {    //直接播放音乐
            play(0);
        } else if (msg == Config.PAUSE_MSG) {    //暂停
            pause();
        } else if (msg == Config.STOP_MSG) {     //停止
            stop();
        } else if (msg == Config.CONTINUE_MSG) { //继续播放
            resume();
        } else if (msg == Config.PRIVIOUS_MSG) { //上一首
            previous();
        } else if (msg == Config.NEXT_MSG) {     //下一首
            next();
        } else if (msg == Config.PROGRESS_CHANGE) {  //进度更新
            currentTime = intent.getIntExtra("progress", -1);
            play(currentTime);
        } else if (msg == Config.PLAYING_MSG) {
            handler.sendEmptyMessage(1);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 播放音乐
     *
     */
    private void play(int currentTime) {
        try {
            mediaPlayer.reset();// 把各项参数恢复到初始状态
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare(); // 进行缓冲
            mediaPlayer.start();


            if (currentTime > 0) { // 如果音乐不是从头播放
                mediaPlayer.seekTo(currentTime);
            }
            Intent intent = new Intent();
            intent.setAction(Config.MUSIC_DURATION);
            duration = mediaPlayer.getDuration();
            intent.putExtra("duration", duration);  //通过Intent来传递歌曲的总长度
            sendBroadcast(intent);


            handler.sendEmptyMessage(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止音乐
     */
    private void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            try {
                mediaPlayer.prepare(); // 在调用stop后如果需要再次通过start进行播放,需要之前调用prepare函数
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 暂停音乐
     */
    private void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPause = true;
        }
    }

    private void resume() {
        if (isPause) {
            mediaPlayer.start();
            isPause = false;
        }
    }

    /**
     * 上一首
     */
    private void previous() {
        Intent sendIntent = new Intent(Config.UPDATE_ACTION);
        sendIntent.putExtra("current", current);
        // 发送广播，将被Activity组件中的BroadcastReceiver接收到
        sendBroadcast(sendIntent);
        play(0);
    }

    /**
     * 下一首
     */
    private void next() {
        Intent sendIntent = new Intent(Config.UPDATE_ACTION);
        sendIntent.putExtra("current", current);
        // 发送广播，将被Activity组件中的BroadcastReceiver接收到
        sendBroadcast(sendIntent);
        play(0);
    }



    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

    }


    /**
     * handler用来接收消息，来发送广播更新播放时间
     */
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                if(mediaPlayer != null) {
                    currentTime = mediaPlayer.getCurrentPosition(); // 获取当前音乐播放的位置
                    Intent intent = new Intent();
                    intent.setAction(Config.MUSIC_CURRENT);
                    intent.putExtra("currentTime", currentTime);
                    sendBroadcast(intent); // 给PlayerActivity发送广播
                    handler.sendEmptyMessageDelayed(1,1000);
                }

            }
        };
    };



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
