package com.example.playaudiotest;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;

import static com.example.playaudiotest.R.id.imageView_play;
import static com.example.playaudiotest.R.id.textView_musicName;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private String[] data = {"apple", "banana", "orange", "watermelon"};//这里数据应该从手机中读取音乐文件名称

    private MediaPlayer mediaPlayer = new MediaPlayer();

    private ListView listView_musicList;
    private SimpleAdapter mAdapter;
    List<Mp3> mp3Infos = null;
    private List<HashMap<String, String>> mp3List;
    private HashMap<String, Object> map;
    private String string_url;
    private MarqueeTextView marqueeTextView_musicTitle;
    private MarqueeTextView marqueeTextView_musicArtist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        //        ArrayAdapter<String> musicAdapter=new ArrayAdapter<String>(MusicActivity.this,
        //                android.R.layout.simple_list_item_1,data);  //第二个参数指定了放在listview中子项布局的id
        //        ListView listView_musicList=(ListView)findViewById(R.id.listView_musicList);
        //        listView_musicList.setAdapter(musicAdapter);
        listView_musicList = (ListView) findViewById(R.id.listView_musicList);
        listView_musicList.setOnItemClickListener(this);
        mp3Infos = MediaUtil.getMp3(getApplicationContext());
        mp3List = MediaUtil.getMusicMaps(mp3Infos);
        setListAdpter(mp3List); //  显示歌曲列表
        final ImageView imageView_play = (ImageView) findViewById(R.id.imageView_play);
        imageView_play.setOnClickListener(this);

        marqueeTextView_musicTitle = (MarqueeTextView) findViewById(R.id.music_title);
        marqueeTextView_musicArtist = (MarqueeTextView) findViewById(R.id.music_artist);

        if (ContextCompat.checkSelfPermission(MusicActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MusicActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);    //运行时权限申请，申请访问SD卡的权限
        } else {
            Toast.makeText(this, "onCreate..", Toast.LENGTH_SHORT).show();
        }


        //        listView_musicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        //            @Override
        //            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        //                int realPosition=(int)id;
        //                String title=view.findViewById(R.id.textView_title).toString();
        //                marqueeTextView_musicTitle.setText(title);
        //                Log.v("MusicActivity",title);
        //                string_url=view.findViewById(R.id.textView_url).toString();
        //
        ////                Intent intent_music_main=new Intent(MusicActivity.this,MainActivity.class);
        ////                intent_music_main.putExtra("title",title);
        //                initMediaPlayer();  //初始化播放器
        //                musicPlay();
        //
        //
        //
        //            }
        //        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListView listView = (ListView) parent;
        HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(position);
        String title = map.get("title");
        string_url = map.get("url");
        String artist = map.get("artist");

        marqueeTextView_musicTitle.setText(title);
        marqueeTextView_musicArtist.setText(artist);
        mediaPlayer.reset();
        initMediaPlayer();
//        musicPause();
        musicPlay();
    }

    private void initMediaPlayer() {

        try {
            File file = new File(string_url);    //创建file来指定音频文件的路径
            FileInputStream fis = new FileInputStream(file);
            if (file == null) {
                Toast.makeText(this, "未找到该音乐", Toast.LENGTH_SHORT).show();
            } else {
                mediaPlayer.setDataSource(fis.getFD());
                mediaPlayer.prepare();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setListAdpter(List<HashMap<String, String>> mp3List) {
        mAdapter = new SimpleAdapter(this, mp3List,
                R.layout.music_item, new String[]{"number", "title",
                "artist"}, new int[]{R.id.number, R.id.textView_title,
                R.id.textView_artist});
        listView_musicList.setAdapter(mAdapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case imageView_play:
                if(mediaPlayer.isPlaying())
                    musicPause();
                else
                    musicPlay();
                break;
//            case imageView_pause:
//                musicPause();
        }
    }

    private void musicPlay() {
        if (string_url == null) {
            Toast.makeText(this, "请先选择歌曲", Toast.LENGTH_SHORT).show();
        } else if (!mediaPlayer.isPlaying()) {
            Toast.makeText(this, "will play", Toast.LENGTH_SHORT).show();
            mediaPlayer.start();
        }
    }

    private void musicPause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
    protected void onDestroy(){
        super.onDestroy();
        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }


}
