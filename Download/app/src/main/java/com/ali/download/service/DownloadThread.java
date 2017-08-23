package com.ali.download.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.ali.download.db.Dao;
import com.ali.download.entites.ThreadInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jiang on 2017/8/22.
 * 下载线程
 */

public class DownloadThread extends Thread{
    private ThreadInfo threadInfo;
    private Dao ThreadInfoDao;
    private boolean isPause = false;
    private int start;
    private int fileLength;
    private int finish;
    private int sumread = 0;
    public void setIsPause(boolean isPause){
        this.isPause = isPause;
    }
    public DownloadThread(ThreadInfo threadInfo, Context context){
        this.threadInfo = threadInfo;
        this.ThreadInfoDao = new Dao(context);
    }
    @Override
    public void run(){
        Log.i("开始线程","开始线程");
        String sourceUrl = threadInfo.getUrl();
        try {
            URL url = new URL(sourceUrl);
            HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
            start = threadInfo.getStart() + threadInfo.getFinish();
            Log.i("start",String.valueOf(sourceUrl));
            //Log.i("fileLength1",String.valueOf(urlConn.getContentLength()));
            //取得的线程中是否有文件有关的信息，若有直接读取，否则初始化
            if(threadInfo.getLength() == 0)
            {
                fileLength = urlConn.getContentLength();
            } else{
                fileLength = threadInfo.getLength();
            }
            //Log.i("fileLength",String.valueOf(urlConn.getContentLength()));
            //
            Log.i("getResponseCode",String.valueOf(urlConn.getResponseCode()));
            if(urlConn.getResponseCode() == 200){
                
            }
            if(urlConn.getResponseCode() == 206){
                //连接正常
                urlConn.setRequestProperty("Range","bytes=" + start + "-" + fileLength);
                Log.i("getResponseCode1",threadInfo.getPath());
                InputStream is = urlConn.getInputStream();
                File path = new File(threadInfo.getPath());
                URL absurl = urlConn.getURL();
                String fileName = absurl.getFile();
                Log.i("fileName",fileName);
                File file = new File(path, fileName);
                RandomAccessFile raf = new RandomAccessFile(file, "rwd") ;
                raf.seek(start);
                //finish = threadInfo.getFinish();
                byte buf[] = new byte[1024];

                while (!isPause){
                    int numread = is.read(buf);
                    Log.i("numread",String.valueOf(numread));
                    if(numread ==-1) {
                        break;
                    }else{
                        raf.write(buf,0,numread);
                        sumread += numread;
                    }
                }
                if(isPause){
                    ThreadInfoDao.updateThreadInfo(start,sumread,sourceUrl);
                }
                is.close();
                urlConn.disconnect();
            }


        }
        catch(MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.run();
    }
}
