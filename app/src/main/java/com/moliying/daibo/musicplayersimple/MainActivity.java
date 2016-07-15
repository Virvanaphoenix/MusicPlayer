package com.moliying.daibo.musicplayersimple;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private int mListPosition;   //播放歌曲在mp3Infos的位置
    private int mCurrentTime;    //当前歌曲播放时间
    private int mDuration;       //歌曲长度
    private MediaPlayer player = new MediaPlayer();
    private String mUrl;

    private int flag;           //播放标识
    private boolean isPlaying;              // 正在播放
    private boolean isPause;                // 暂停

    private ProgressBar music_progressBar;
    private ListView list_music;
    private ImageView previousBtn;
    private ImageView playBtn;
    private ImageView nextBtn;
    private TextView txt_name;


    private PlayerReceiver playerReceiver;
    private List<String> path = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
        playerReceiver = new PlayerReceiver();
        initData();
        initListener();
    }

    private void initListener() {
        nextBtn.setOnClickListener(this);
        previousBtn.setOnClickListener(this);
        playBtn.setOnClickListener(this);
        list_music.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view.findViewById(R.id.text_name);
                String path = tv.getText().toString();
                player.reset();
                play(path, position);

            }
        });

    }


    /**
     * 在OnResume中初始化和接收Activity数据
     */
    @Override
    protected void onResume() {
        super.onResume();


        music_progressBar.setMax(mDuration);
        isPlaying = true;
        isPause = false;

        //注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.UPDATE_ACTION);
        filter.addAction(Config.MUSIC_CURRENT);
        filter.addAction(Config.MUSIC_DURATION);
        registerReceiver(playerReceiver, filter);

//        findView();
    }


    public void findView() {
        list_music = (ListView) findViewById(R.id.list_music);
        music_progressBar = (ProgressBar) findViewById(R.id.musicprogressbar);
        previousBtn = (ImageView) findViewById(R.id.imgBtn_last);
        playBtn = (ImageView) findViewById(R.id.imgBtn_play_pause);
        nextBtn = (ImageView) findViewById(R.id.imgBtn_next);
        txt_name = (TextView) findViewById(R.id.txt_name);
    }


    public void initData() {
        path.add("data/Beyond_chongshangyunxiao.mp3");
        path.add("data/Beyond_buxuyaotaidong.mp3");
        path.add("data/Beyond_chrx.mp3");
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.layout_item, path);
        list_music.setAdapter(adapter);
    }

    /**
     * 播放音乐
     */
    public void play(String url, int position) {
        Intent intent = new Intent(this, PlayService.class);
        intent.setAction("com.moliying.media.MUSIC_SERVICE");
        intent.putExtra("mUrl", url);
        intent.putExtra("mListPosition", mListPosition);
        intent.putExtra("MSG", Config.PLAY_MSG);
        startService(intent);
        isPlaying = true;
        txt_name.setText(path.get(position));
        mListPosition = position;
        playBtn.setImageResource(R.drawable.play_btn_pause);
    }

    /**
     * 反注册广播
     */
    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(playerReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //控件点击事件
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, PlayService.class);
        switch (v.getId()) {
            case R.id.imgBtn_play_pause:
                if (isPlaying) {
                    playBtn.setImageResource(R.drawable.play_btn_play);
                    intent.setAction("com.moliying.media.MUSIC_SERVICE");
                    intent.putExtra("MSG", Config.PAUSE_MSG);
                    startService(intent);
                    isPlaying = false;
                    isPause = true;

                } else if (isPause) {
                    playBtn.setImageResource(R.drawable.play_btn_pause);
                    intent.setAction("com.moliying.media.MUSIC_SERVICE");
                    intent.putExtra("MSG", Config.CONTINUE_MSG);
                    startService(intent);
                    isPause = false;
                    isPlaying = true;
                }
                break;
            case R.id.imgBtn_last:       //上一首歌曲
                previous_music();
                break;
            case R.id.imgBtn_next:           //下一首歌曲
                next_music();
                break;


        }

    }


    /**
     * 上一首
     */
    public void previous_music() {
        playBtn.setImageResource(R.drawable.play_btn_pause);
        if (mListPosition - 1 >= 0) {
            mListPosition = mListPosition - 1;
            txt_name.setText(path.get(mListPosition));
            Intent intent = new Intent(this, PlayService.class);
            intent.setAction("com.moliying.media.MUSIC_SERVICE");
            intent.putExtra("mListPosition", mListPosition);
            intent.putExtra("MSG", Config.PRIVIOUS_MSG);
            intent.putExtra("mUrl", path.get(mListPosition));
            startService(intent);
        } else {
            Toast.makeText(MainActivity.this, "没有上一首了", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 下一首
     */
    public void next_music() {
        playBtn.setImageResource(R.drawable.play_btn_pause);
        if (mListPosition + 1 <= path.size() - 1) {
            mListPosition = mListPosition + 1;
            txt_name.setText(path.get(mListPosition));
            Intent intent = new Intent(this, PlayService.class);
            intent.setAction("com.moliying.media.MUSIC_SERVICE");
            intent.putExtra("mListPosition", mListPosition);
            intent.putExtra("MSG", Config.NEXT_MSG);
            intent.putExtra("mUrl", path.get(mListPosition));
            startService(intent);
        } else {
            Toast.makeText(MainActivity.this, "没有下一首了", Toast.LENGTH_SHORT).show();
        }
    }

//
//    Handler mhandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            txt_name.setText(msg.arg1);
//        }
//    };

    /**
     * 用来接收从service传回来的广播的内部类
     *
     * @author wwj
     */
    public class PlayerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Config.MUSIC_CURRENT)) {
                mCurrentTime = intent.getIntExtra("mCurrentTime", -1);
                Log.i("ssssee", "receive--->" + mCurrentTime);
                music_progressBar.setProgress(mCurrentTime);
//                Message msg = new Message();
//                msg.arg1=mCurrentTime;
//                mhandler.sendMessage(msg);
            } else if (action.equals(Config.MUSIC_DURATION)) {
                int duration = intent.getIntExtra("mDuration", -1);
                music_progressBar.setMax(duration);
            } else if (action.equals(Config.UPDATE_ACTION)) {
                //获取Intent中的current消息，current代表当前正在播放的歌曲
                mListPosition = intent.getIntExtra("current", -1);
                mUrl = path.get(mListPosition);
                if (mListPosition == 0) {
                    playBtn.setBackgroundResource(R.drawable.pause_selector);
                    isPause = true;
                }
            }
        }

    }
}
