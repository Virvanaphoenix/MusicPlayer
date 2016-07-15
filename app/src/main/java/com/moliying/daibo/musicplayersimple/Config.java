package com.moliying.daibo.musicplayersimple;

/**
 * Created by daibo on 16/7/12.
 */
public class Config {

    public static final String UPDATE_ACTION =
            "com.moliying.action.UPDATE_ACTION";  //更新动作
    public static final String CTL_ACTION =
            "com.moliying.action.CTL_ACTION";        //控制动作
    public static final String MUSIC_CURRENT =
            "com.moliying.action.MUSIC_CURRENT";  //当前音乐播放时间更新动作
    public static final String MUSIC_DURATION =
            "com.moliying.action.MUSIC_DURATION";//新音乐长度更新动作



    public static final int PAUSE_MSG = 0x0;    //暂停
    public static final int STOP_MSG = 0x1;    //停止
    public static final int CONTINUE_MSG = 0x2;    //继续
    public static final int PRIVIOUS_MSG = 0x3;    //上一首
    public static final int NEXT_MSG = 0x4;    //下一首
    public static final int PROGRESS_CHANGE = 0x5;    //进度更新
    public static final int PLAYING_MSG = 0x6;    //正在播放
    public static final int PLAY_MSG = 0x7;    //直接播放
}
