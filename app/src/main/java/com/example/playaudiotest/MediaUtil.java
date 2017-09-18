package com.example.playaudiotest;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 豫 on 2017/9/13.
 */

public class MediaUtil {

    public static List<Mp3> getMp3User(Context context){
       List<Mp3> mp3Infos=new ArrayList<Mp3>();
        String titles[]=new String[]{"生生","惊天动地","失落沙洲"};
        String artists[]=new String[]{"林俊杰","金玟岐","徐佳莹"};
        String urls[]=new String[]{"生生-林俊杰.mp3","惊天动地-金玟岐.mp3","失落沙洲-徐佳莹.mp3"};
        for(int i=0;i<3;i++){
            Mp3 thisMp3=new Mp3();
            thisMp3.setTitle(titles[i]);
            thisMp3.setArtist(artists[i]);
            thisMp3.setUrl(urls[i]);
            mp3Infos.add(thisMp3);
            Log.v("MediaUtil",thisMp3.toString());

        }

        return mp3Infos;
    }

    public static List<Mp3> getMp3(Context context){
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,null,null,MediaStore.Audio.Media.DEFAULT_SORT_ORDER);  //  读取手机文件代码

        List<Mp3> mp3Infos=new ArrayList<Mp3>();

        for(int i=0;i<cursor.getCount();i++){
            cursor.moveToNext();
            Mp3 thisMp3=new Mp3();
            int isMusic=cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));//判断是否为音乐文件
            if(isMusic!=0){ //只有音乐文件才加入
                long id = cursor.getLong(cursor
                        .getColumnIndex(MediaStore.Audio.Media._ID));               //音乐id
                String title = cursor.getString((cursor
                        .getColumnIndex(MediaStore.Audio.Media.TITLE)));            //音乐标题
                String artist = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Audio.Media.ARTIST));            //艺术家
                long duration = cursor.getLong(cursor
                        .getColumnIndex(MediaStore.Audio.Media.DURATION));          //时长
                long size = cursor.getLong(cursor
                        .getColumnIndex(MediaStore.Audio.Media.SIZE));              //文件大小
                String url = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Audio.Media.DATA));              //文件路径
                thisMp3.setArtist(artist);
                thisMp3.setId(id);
                thisMp3.setTitle(title);
                thisMp3.setDuration(duration);
                thisMp3.setSize(size);
                thisMp3.setUrl(url);
                mp3Infos.add(thisMp3);

            }

        }
        return mp3Infos;

    }

    public static List<HashMap<String,String>> getMusicMaps(List<Mp3> mp3Infos){
        List<HashMap<String,String>> mp3List = new ArrayList<HashMap<String, String>>();
        int i=0;    //  定义歌曲的序号
        for(Iterator iterator=mp3Infos.iterator();iterator.hasNext();){
            i++;
            Mp3 thisMp3=(Mp3)iterator.next();   // mp3Infos集合的下一个元素，每个元素都是一个MP3类型的对象
            HashMap<String,String> map=new HashMap<String,String>();
            map.put("number",String.valueOf(i));
            map.put("title",String.valueOf(thisMp3.getTitle()));
            map.put("artist",String.valueOf(thisMp3.getArtist()));
            map.put("url",String.valueOf(thisMp3.getUrl()));
            map.put("id",String.valueOf(thisMp3.getId()));
            map.put("duration",String.valueOf(thisMp3.getDuration()));
            map.put("size",String.valueOf(thisMp3.getSize()));
            mp3List.add(map);
        }
        return mp3List;
    }

    public static String formatTime(long time) {
        String min = time / (1000 * 60) + "";
        String sec = time % (1000 * 60) + "";
        if (min.length() < 2) {
            min = "0" + time / (1000 * 60) + "";
        } else {
            min = time / (1000 * 60) + "";
        }
        if (sec.length() == 4) {
            sec = "0" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 3) {
            sec = "00" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 2) {
            sec = "000" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 1) {
            sec = "0000" + (time % (1000 * 60)) + "";
        }
        return min + ":" + sec.trim().substring(0, 2);
    }
}
