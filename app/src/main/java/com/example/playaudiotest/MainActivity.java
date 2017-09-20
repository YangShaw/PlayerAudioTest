package com.example.playaudiotest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.security.cert.TrustAnchor;
import java.util.List;


import static com.example.playaudiotest.R.id.button_musicList;
import static com.example.playaudiotest.R.id.button_pause;
import static com.example.playaudiotest.R.id.button_play;
import static com.example.playaudiotest.R.id.button_stop;



public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MediaPlayer mediaPlayer = new MediaPlayer();
//    private String string_musicPath = "musiclist/";
//    private String string_musicName = "惊天动地-金玟岐.mp3";
//    private TextView textView_musicName;
//    private String string_musicPlay="当前播放：";
//    private String string_musicPause="当前暂停：";
//    private String string_musicStop="歌曲名";
//    private boolean isPause;

    private Button button_play;
    private Button button_next;
    private Button button_previous;
    private Button button_musicList;
    private String title="无音乐";
    private String artist="佚名";
    private TextView textView_musicName;
    private TextView textView_artist;
    private int musicPosition=-1;
    private Boolean isPause;
    private Boolean isPlaying;

    List<Mp3> mp3Infos = null;


    private boolean isOwner=true;//从前面按钮中传递过来的信息，决定是不是房主。

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mp3Infos=MediaUtil.getMp3(getApplicationContext());


        findViewById();

        //        Button button_pause = (Button) findViewById(R.id.button_pause);
        //        Button button_stop = (Button) findViewById(R.id.button_stop);

        if(isOwner){
            setOnClickListener();

            //            button_pause.setOnClickListener(this);
            //            button_stop.setOnClickListener(this);
        }

        Intent intent=getIntent();
        if(intent!=null){
            title=intent.getStringExtra("title");
            artist=intent.getStringExtra("artist");
            musicPosition=intent.getIntExtra("musicPosition",-1);
        }
        textView_musicName.setText(title);
        textView_artist.setText(artist);

//        if(!title.equals(""))
//            textView_musicName.setText(title);
//        if(!artist.equals(""))
//            textView_artist.setText(artist);




        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);    //运行时权限申请，申请访问SD卡的权限
        } else {
//            Toast.makeText(this, "onCreate..", Toast.LENGTH_SHORT).show();
//            initMediaPlayer();  //初始化播放器
        }

    }

    private void findViewById(){
        button_play = (Button) findViewById(R.id.button_play);
        button_next=(Button) findViewById(R.id.button_next);
        button_previous=(Button)findViewById(R.id.button_previous);
        button_musicList = (Button) findViewById(R.id.button_musicList);
        textView_musicName = (TextView) findViewById(R.id.textView_musicName);
        textView_artist=(TextView)findViewById(R.id.textView_artist);
    }

    private void setOnClickListener(){
        button_play.setOnClickListener(this);
        button_previous.setOnClickListener(this);
        button_next.setOnClickListener(this);
        button_musicList.setOnClickListener(this);
    }

//    private void initMediaPlayer(){
//        try{
//            File file = new File(Environment.getExternalStorageDirectory(),string_musicPath+string_musicName);    //创建file来指定音频文件的路径
//            if(file==null){
//                Toast.makeText(this, "未找到该音乐", Toast.LENGTH_SHORT).show();
//            } else {
//                mediaPlayer.setDataSource(file.getPath());
//                mediaPlayer.prepare();
//            }
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onClick(View v){
        Intent intent=new Intent(this,MusicService.class);
        switch (v.getId()) {

            case R.id.button_play:
                if(musicPosition==-1){
                    Toast.makeText(this,"请先选择音乐",Toast.LENGTH_SHORT).show();
                } else if(isPause) {
                    //更改图标语句，下同
                    intent.putExtra("musicPosition",musicPosition);
                    intent.putExtra("MSG",Constant.PLAY_MSG);
                    startService(intent);
                    isPause=false;
                    isPlaying=true;
                } else if(isPlaying) {
                    intent.putExtra("url",mp3Infos.get(musicPosition).getUrl());
                    intent.putExtra("musicPosition",musicPosition);
                    intent.putExtra("MSG",Constant.PAUSE_MSG);
                    startService(intent);
                    isPause=true;
                    isPlaying=false;
                }
                break;
            case R.id.button_previous:
                if(musicPosition==-1) {
                    Toast.makeText(this, "请先选择音乐", Toast.LENGTH_SHORT).show();
                } else {
                    musicPosition=musicPosition-1;
                    intent.putExtra("url",mp3Infos.get(musicPosition).getUrl());
                    intent.putExtra("musicPosition",musicPosition);
                    intent.putExtra("MSG",Constant.PREVIOUS_MSG);
                    startService(intent);
                    isPause=false;
                    isPlaying=true;
                }
                break;
            case R.id.button_next:
                if(musicPosition==-1) {
                    Toast.makeText(this, "请先选择音乐", Toast.LENGTH_SHORT).show();
                } else {
                    musicPosition=musicPosition+1;
                    intent.putExtra("url",mp3Infos.get(musicPosition).getUrl());
                    intent.putExtra("musicPosition",musicPosition);
                    intent.putExtra("MSG",Constant.NEXT_MSG);
                    startService(intent);
                    isPause=false;
                    isPlaying=true;
                }
                break;

            case R.id.button_musicList:
                Intent intent2 = new Intent(MainActivity.this,MusicActivity.class);
                intent2.putExtra("title",title);
                intent2.putExtra("artist",artist);
                intent2.putExtra("musicPosition",musicPosition);
                startActivity(intent2);

            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
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

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}
