package com.example.playaudiotest;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;


public class MusicService extends Service {

    private MediaPlayer mediaPlayer;//媒体播放器对象
    private String path;//播放路径
    private int msg;//播放信息，如play，pause等
    private boolean isPause;
    private int musicPosition =1 ;
    private List<Mp3> mp3Infos;
//    private MyReceiver myReceiver;

//    public static final String UPDATE_ACTION = "com.example.action.UPDATE_ACTION";	//更新动作
//    public static final String MUSIC_DURATION = "com.example.action.MUSIC_DURATION";	//更新动作


    @Override
    public void onCreate(){ //  一个服务只会创建一次，销毁一次，但是会开始很多次。
        super.onCreate();
        Log.d("service", "service created");
        mediaPlayer = new MediaPlayer();
//        mp3Infos = MediaUtil.getMp3(MusicService.this);

        //音乐播放完成时的监听器,我们只写了顺序播放
//        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//
//                current++;//得到下一首歌曲的播放序号
//                if(current<=mp3Infos.size()-1){
//                    Intent sendIntent = new Intent(UPDATE_ACTION);
//                    sendIntent.putExtra("current",current);
//                    sendBroadcast(sendIntent);  //发送广播，会被活动中的BroadcastReceiver接收到。
//                    path=mp3Infos.get(current).getUrl();    //  读取即将要播放的这一首歌的存放地址
////                    play(0);
//                } else {
//                    current=0;
//                    Intent sendIntent = new Intent(UPDATE_ACTION);
//                    sendIntent.putExtra("current",current);
//                    sendBroadcast(sendIntent);
//                }
//
//            }
//        });

    }

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){     //  取代了之前版本的onStart

        path=intent.getStringExtra("url");//获取歌曲路径
        musicPosition=intent.getIntExtra("musicPosition",1);//获取歌曲位置
        msg=intent.getIntExtra("MSG",0);//获取播放信息

        if (msg == Constant.INIT_MSG){
            init();
            play();
        } else if (msg == Constant.PLAY_MSG) {	//直接播放音乐
            play();
        } else if (msg == Constant.PAUSE_MSG) {	//暂停
            pause();
//        } else if (msg == AppConstant.PlayerMsg.STOP_MSG) {		//停止
//            stop();
//        } else if (msg == AppConstant.PlayerMsg.CONTINUE_MSG) {	//继续播放
//            resume();
        } else if (msg == Constant.PREVIOUS_MSG) {	//上一首
            init();
            previous();
        } else if (msg == Constant.NEXT_MSG) {		//下一首
            init();
            next();
//        } else if (msg == AppConstant.PlayerMsg.PROGRESS_CHANGE) {	//进度更新
//            currentTime = intent.getIntExtra("progress", -1);
//            play(currentTime);
//        } else if (msg == AppConstant.PlayerMsg.PLAYING_MSG) {
//            handler.sendEmptyMessage(1);
        }

        return super.onStartCommand(intent,flags,startId);

    }

    private void init(){
        try{
            mediaPlayer.reset();// 把各项参数恢复到初始状态
            File file = new File(path);    //创建file来指定音频文件的路径
            FileInputStream fis = new FileInputStream(file);
            mediaPlayer.setDataSource(fis.getFD());//传入歌曲地址
            mediaPlayer.prepare(); // 进行缓冲
        }   catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放音乐
     */
    private void play() {
        if(mediaPlayer!=null && !mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }
    }

    private void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPause = true;
        }
    }

    private void previous() {
//        Intent sendIntent = new Intent(UPDATE_ACTION);
//        sendIntent.putExtra("current", current);
        // 发送广播，将被Activity组件中的BroadcastReceiver接收到
//        sendBroadcast(sendIntent);
        play();
    }


    private void next() {
//        Intent sendIntent = new Intent(UPDATE_ACTION);
//        sendIntent.putExtra("current", current);
//        // 发送广播，将被Activity组件中的BroadcastReceiver接收到
//        sendBroadcast(sendIntent);
        play();
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }



}
