package com.example.playaudiotest;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.bitmap;
//import static com.example.playaudiotest.MusicService.MUSIC_DURATION;
//import static com.example.playaudiotest.MusicService.UPDATE_ACTION;
import static com.example.playaudiotest.R.id.imageView_next;
import static com.example.playaudiotest.R.id.imageView_play;
import static com.example.playaudiotest.R.id.imageView_previous;
import static com.example.playaudiotest.R.id.textView_musicName;
import static com.example.playaudiotest.R.id.textView_title;
import static com.example.playaudiotest.R.id.title;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {


    private ListView listView_musicList;//物理上的列表
    List<Mp3> mp3Infos = null;
    private List<HashMap<String, String>> mp3List;
    // 是一个hashmap类型的list。所以我只要有一个hashmap就行，所以我在数据库里读取文件，从而获得hashmap。
    private String string_url;
    private SimpleAdapter mAdapter;
    //    private MediaPlayer mediaPlayer = new MediaPlayer();
    private ImageView imageView_play;
    private ImageView imageView_next;
    private ImageView imageView_previous;
    private MarqueeTextView marqueeTextView_musicTitle;

    private String title="无音乐";
    private String artist="无歌手";

    private MarqueeTextView marqueeTextView_musicArtist;
    private boolean isFirstTime = true;//是否是第一次播放
    private boolean isPlaying; // 正在播放

    private boolean isPause; // 暂停

    private Button button_back;

    private int duration;//时长

    private int musicPosition=1;//播放的音乐在列表中的序号

//    private HomeReceiver homeReceiver;//自定义的广播接收器

//    public static final String UPDATE_ACTION = "com.example.action.UPDATE_ACTION";	//更新动作


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);


        mp3Infos = MediaUtil.getMp3(getApplicationContext());   //  MP3类的列表，存放具体的音乐文件对象信息
        mp3List = MediaUtil.getMusicMaps(mp3Infos);             //  哈希对类型的列表
        listView_musicList = (ListView) findViewById(R.id.listView_musicList);
        listView_musicList.setOnItemClickListener(this);
        setListAdpter(mp3List);         //  显示歌曲列表
        findViewById();
        setOnClickListener();

//        homeReceiver = new HomeReceiver();
        // 创建IntentFilter
//        IntentFilter filter = new IntentFilter();
        // 指定BroadcastReceiver可以监听的Action，这两个action来自MusicService
//        filter.addAction(UPDATE_ACTION);
//        filter.addAction(MUSIC_DURATION);
        // 注册BroadcastReceiver
//        registerReceiver(homeReceiver, filter);

        Intent intent=getIntent();
        if(intent!=null){
            title=intent.getStringExtra("title");
            artist=intent.getStringExtra("artist");
            musicPosition=intent.getIntExtra("musicPosition",1);
        }

        marqueeTextView_musicTitle.setText(title);
        marqueeTextView_musicArtist.setText(artist);



        if (ContextCompat.checkSelfPermission(MusicActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MusicActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);    //运行时权限申请，申请访问SD卡的权限
        } else {
//            Toast.makeText(this, "onCreate..", Toast.LENGTH_SHORT).show();
        }


    }

    // 自定义的BroadcastReceiver，负责监听从Service传回来的广播，分别对oncreate中注册的两个action进行处理。
//    public class HomeReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals(UPDATE_ACTION)) {  //  这个action来自musicService
//                // 获取Intent中的current消息，current代表当前正在播放的歌曲,这样就可以刷新这边显示的歌曲名称
//                musicPosition = intent.getIntExtra("current", -1);
//                if (musicPosition >= 0) {
//                    marqueeTextView_musicTitle.setText(mp3Infos.get(musicPosition).getTitle());
//                }
//            }
//        }
//
//    }

    @Override
    public void onBackPressed(){
        Intent intent=new Intent(this,MainActivity.class);
        intent.putExtra("title",mp3Infos.get(musicPosition).getTitle());
        intent.putExtra("artist",mp3Infos.get(musicPosition).getArtist());
        intent.putExtra("musicPosition",musicPosition);
        startActivity(intent);
//        finish();
    }

    private void findViewById(){
        imageView_play = (ImageView) findViewById(R.id.imageView_play);
        imageView_next=(ImageView)findViewById(R.id.imageView_next);
        imageView_previous=(ImageView)findViewById(R.id.imageView_previous);
        marqueeTextView_musicTitle = (MarqueeTextView) findViewById(R.id.music_title);
        marqueeTextView_musicArtist = (MarqueeTextView) findViewById(R.id.music_artist);
        button_back=(Button)findViewById(R.id.button_back);
    }

    private void setOnClickListener(){
        imageView_play.setOnClickListener(this);
        imageView_next.setOnClickListener(this);
        imageView_previous.setOnClickListener(this);
        button_back.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        ListView listView = (ListView) parent;
        musicPosition=position; //  获取列表序号就可以，知道是哪一首在播放
        isFirstTime=false;
        isPlaying=true;
        isPause=false;
        itemPlay(musicPosition);
//        setPosition(musicPosition,listView_musicList);
    }


    /**
     * 此方法通过传递列表点击位置来获取mp3Info对象
     *
     * @param musicPosition
     */
    public void itemPlay(int musicPosition) {
        if (mp3Infos != null) {
            Mp3 mp3Info = mp3Infos.get(musicPosition);
            title=mp3Info.getTitle();
            artist=mp3Info.getArtist();
            marqueeTextView_musicTitle.setText(title); // 这里显示标题
            marqueeTextView_musicArtist.setText(artist);
           // Intent intent = new Intent(MusicActivity.this, MusicService.class); // 定义Intent对象，跳转到PlayerActivity
            Intent intent = new Intent(this,MusicService.class); // 定义Intent对象，跳转到PlayerActivity
            // 添加一系列要传递的数据
            intent.putExtra("title", title);
            intent.putExtra("url", mp3Info.getUrl());
            intent.putExtra("artist", artist);
            intent.putExtra("musicPosition", musicPosition);
            intent.putExtra("MSG", Constant.INIT_MSG);
            startService(intent);
        }
    }

//    private void initMediaPlayer() {
//        try {
//            File file = new File(string_url);    //创建file来指定音频文件的路径
//            FileInputStream fis = new FileInputStream(file);
//            if (file == null) {
//                Toast.makeText(this, "未找到该音乐", Toast.LENGTH_SHORT).show();
//            } else {
//                mediaPlayer.setDataSource(fis.getFD());
//                mediaPlayer.prepare();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void setListAdpter(List<HashMap<String, String>> mp3List) {
        mAdapter = new SimpleAdapter(this, mp3List,
                R.layout.music_item, new String[]{"number", "title",
                "artist"}, new int[]{R.id.number, R.id.textView_title,
                R.id.textView_artist});
        listView_musicList.setAdapter(mAdapter);

    }

    @Override
    public void onClick(View v) {
        Intent intent=new Intent(this,MusicService.class);

        /*
        这里MusicService这个服务仅仅起到播放音乐的作用。我们看不见也摸不着，它会藏在后面把音乐给播放出来。
        我们需要做的仅仅是告诉服务要播放哪一首音乐（url，musicPosition，和播放信息（暂停，播放，下一首，上一首等））。
         */
        switch (v.getId()) {
            case R.id.imageView_play:
                if(isFirstTime){
                    musicPlay();
                    isFirstTime=false;
                    isPlaying=true;
                    isPause=false;
                } else {
                    if(isPlaying){  //播放改暂停
                        imageView_play.setImageResource(R.drawable.stop);
                        intent.putExtra("MSG",Constant.PLAY_MSG);

                        startService(intent);   //  启动musicService，下同
                        isPlaying=false;
                        isPause=true;
                    } else if(isPause) { //暂停改播放
                        imageView_play.setImageResource(R.drawable.play);
                        intent.putExtra("MSG",Constant.PAUSE_MSG);

                        startService(intent);
                        isPlaying=true;
                        isPause=false;
                    } else {
                        Toast.makeText(this,"有bug！！！",Toast.LENGTH_LONG).show();
                    }
                }
                break;

            case R.id.imageView_next:
                isFirstTime=false;
                isPlaying=true;
                isPause=false;
                next();
                break;
            case R.id.imageView_previous:
                isFirstTime=false;
                isPlaying=true;
                isPause=false;
                previous();
                break;
            case R.id.button_back:
                Intent intent2=new Intent(this,MainActivity.class);
                intent2.putExtra("title",title);
                intent2.putExtra("artist",artist);
                intent2.putExtra("musicPosition",musicPosition);
                startActivity(intent2);

            default:
                break;
        }
    }

    private void musicPlay() {  //只在第一次播放的时候创建。
        imageView_play.setImageResource(R.drawable.play);

        Mp3 mp3Info = mp3Infos.get(musicPosition);
        // musicPosition是当前要播放的歌曲的位置，然后从MP3infos 中找到这个对象，将这个对象的信息传递到intent中，发送到服务里。


        title=mp3Info.getTitle();
        artist=mp3Info.getArtist();
        marqueeTextView_musicTitle.setText(title); // 这里显示标题
        marqueeTextView_musicArtist.setText(artist);
        Intent intent = new Intent(this,MusicService.class);

        intent.putExtra("musicPosition", 0);
        intent.putExtra("url", mp3Info.getUrl());
        intent.putExtra("MSG", Constant.PLAY_MSG);
        startService(intent);
//        if (string_url == null) {
//            Toast.makeText(this, "请先选择歌曲", Toast.LENGTH_SHORT).show();
//        } else if (!mediaPlayer.isPlaying()) {
//            Toast.makeText(this, "will play", Toast.LENGTH_SHORT).show();
//            mediaPlayer.start();
//            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer arg0) {
//                    next();//如果当前歌曲播放完毕,自动播放下一首.
//                }
//            });
//        }
    }



    private void next(){
        musicPosition=musicPosition+1;
        Mp3 mp3Info = mp3Infos.get(musicPosition);
        title=mp3Info.getTitle();
        artist=mp3Info.getArtist();
        marqueeTextView_musicTitle.setText(title); // 这里显示标题
        marqueeTextView_musicArtist.setText(artist);
        Intent intent = new Intent(this,MusicService.class);
        intent.putExtra("musicPosition", musicPosition);
        intent.putExtra("url", mp3Info.getUrl());
        intent.putExtra("MSG", Constant.NEXT_MSG);
        startService(intent);

        Toast.makeText(this, "即将播放下一首", Toast.LENGTH_SHORT).show();
//        setPosition(musicPosition,listView_musicList);
    }

    private void previous(){
        musicPosition=musicPosition-1;
        Mp3 mp3Info = mp3Infos.get(musicPosition);
        title=mp3Info.getTitle();
        artist=mp3Info.getArtist();
        marqueeTextView_musicTitle.setText(title); // 这里显示标题
        marqueeTextView_musicArtist.setText(artist);
        Intent intent = new Intent(this,MusicService.class);
        intent.putExtra("musicPosition", musicPosition);
        intent.putExtra("url", mp3Info.getUrl());
        intent.putExtra("MSG", Constant.PREVIOUS_MSG);
        startService(intent);

        Toast.makeText(this, "即将播放上一首", Toast.LENGTH_SHORT).show();
//        setPosition(musicPosition,listView_musicList);
    }

//    private void setPosition(int musicPosition,ListView listView){
//        HashMap<String, String> map = (HashMap<String, String>) listView_musicList.getItemAtPosition(musicPosition);
//
//        String title = map.get("title");
//        String artist = map.get("artist");
//        string_url = map.get("url");
//
//        marqueeTextView_musicTitle.setText(title);
//        marqueeTextView_musicArtist.setText(artist);
//        mediaPlayer.reset();
//        initMediaPlayer();
//        musicPlay();
//    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "onRequestPermissionsResult..", Toast.LENGTH_SHORT).show();
//                    initMediaPlayer();
                } else {
                    Toast.makeText(this, "拒绝权限无法使用程序", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }



    //    @Override
//    protected void onDestroy(){
//        super.onDestroy();
//        if (mediaPlayer != null){
//            mediaPlayer.stop();
//            mediaPlayer.release();
//        }
//    }


}
