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

import static android.R.attr.button;
import static com.example.playaudiotest.R.id.button_list1;
import static com.example.playaudiotest.R.id.button_list2;
import static com.example.playaudiotest.R.id.button_list3;
import static com.example.playaudiotest.R.id.button_musicList;
import static com.example.playaudiotest.R.id.button_pause;
import static com.example.playaudiotest.R.id.button_play;
import static com.example.playaudiotest.R.id.button_stop;
import static com.example.playaudiotest.R.id.textView_musicName;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private String string_musicPath = "musiclist/";
    private String string_musicName = "惊天动地-金玟岐.mp3";
    private TextView textView_musicName;
    private Button button_list1;
    private Button button_list2;
    private Button button_list3;
    private String string_musicPlay="当前播放：";
    private String string_musicPause="当前暂停：";
    private String string_musicStop="歌曲名";
    private boolean isPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button_play = (Button) findViewById(R.id.button_play);
        Button button_pause = (Button) findViewById(R.id.button_pause);
        Button button_stop = (Button) findViewById(R.id.button_stop);
        Button button_musicList = (Button) findViewById(R.id.button_musicList);

        button_list1=(Button) findViewById(R.id.button_list1);
        button_list2=(Button) findViewById(R.id.button_list2);
        button_list3=(Button) findViewById(R.id.button_list3);

        button_play.setOnClickListener(this);
        button_pause.setOnClickListener(this);
        button_stop.setOnClickListener(this);
        button_list1.setOnClickListener(this);
        button_list2.setOnClickListener(this);
        button_list3.setOnClickListener(this);
        button_musicList.setOnClickListener(this);


        textView_musicName = (TextView) findViewById(R.id.textView_musicName);


        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);    //运行时权限申请，申请访问SD卡的权限
        } else {
            Toast.makeText(this, "onCreate..", Toast.LENGTH_SHORT).show();
            initMediaPlayer();  //初始化播放器
        }

    }

    private void initMediaPlayer(){
        try{
            File file = new File(Environment.getExternalStorageDirectory(),string_musicPath+string_musicName);    //创建file来指定音频文件的路径
            if(file==null){
                Toast.makeText(this, "未找到该音乐", Toast.LENGTH_SHORT).show();
            } else {
                mediaPlayer.setDataSource(file.getPath());
                mediaPlayer.prepare();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "onRequestPermissionsResult..", Toast.LENGTH_SHORT).show();
                    initMediaPlayer();
                } else {
                    Toast.makeText(this, "拒绝权限无法使用程序", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {

            case button_pause:
                if(mediaPlayer.isPlaying()){
                    Toast.makeText(this, "will pause", Toast.LENGTH_SHORT).show();
                    textView_musicName.setText(string_musicPause+string_musicName);
                    mediaPlayer.pause();
                    isPause=true;
                }
                break;
            case button_stop:
                if(mediaPlayer.isPlaying()||isPause){
                    Toast.makeText(this, "will stop", Toast.LENGTH_SHORT).show();
                    textView_musicName.setText(string_musicStop);
                    mediaPlayer.reset();
                    Toast.makeText(this, "onClick_STOP", Toast.LENGTH_SHORT).show();
                    initMediaPlayer();
                }
                break;


            case button_play:
                if(!mediaPlayer.isPlaying()){
                    Toast.makeText(this, "will play", Toast.LENGTH_SHORT).show();
                    textView_musicName.setText(string_musicPlay+string_musicName);
                    mediaPlayer.start();
                    isPause=false;
                }
                break;
            case button_musicList:
                Intent intent_main_music = new Intent(MainActivity.this,MusicActivity.class);
                startActivity(intent_main_music);

            default:
                break;
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
