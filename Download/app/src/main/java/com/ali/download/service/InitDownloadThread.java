package com.ali.download.service;

import android.content.ContentValues;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.ali.download.Download;
import com.ali.download.db.Dao;
import com.ali.download.entites.FileInfo;
import com.ali.download.entites.ThreadInfo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static android.R.id.message;

/**
 * Created by jiang on 2017/8/23.
 * 下载初始化线程
 */

public class InitDownloadThread extends Thread {
    private ThreadInfo threadInfo;
    private long fileLength;
    private Handler handler;
    public InitDownloadThread(ThreadInfo threadInfo, Handler handler){
        this.threadInfo = threadInfo;
        this.handler = handler;
    }
    private long start = 0;
    private long sumread = 0;


    @Override
    public void run(){
        try{
            Log.i("init_thread","200");
            String sourceUel = threadInfo.getUrl();
            URL url = new URL(sourceUel);
            HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
            Log.i("getResponseCode",String.valueOf(urlConn.getResponseCode()));
            if(urlConn.getResponseCode() == 200){
                fileLength = urlConn.getContentLength();
                threadInfo.setLength(fileLength);
                File path = new File(threadInfo.getPath());
                URL absurl = urlConn.getURL();
                //String fileName = absurl.getFile();
                String fileName1 = absurl.getFile().substring(absurl.getFile().lastIndexOf("/")+1);
                String fileName = fileName1.substring(0,fileName1.lastIndexOf("?"));
                //Log.i("fileName1…………",fileName1);
                //Log.i("fileName2…………",fileName);
                File file = new File(path, fileName);
                Log.i("fileexistInit",String.valueOf(file.exists()));
                if(file.exists()){
                    start = file.length();

                    Log.i("start init",String.valueOf(file.length()));
                    sumread = start;
                }
                threadInfo.setStart(start);
                //RandomAccessFile raf = new RandomAccessFile(file,"rwd");
                //raf.setLength(fileLength);
                //raf.close();
                urlConn.disconnect();
                Message message = Message.obtain();
                Map values = new HashMap();
                values.put("threadInfo",threadInfo);
                values.put("fileName",fileName);
                message.what = 0;
                //message.arg1 = sumread;
                //message.arg2 = fileLength;
                if(sumread == fileLength)
                {
                    message.arg1 = 1;
                    DownloadService.setHaveDownload(true);
                }
                message.obj = values;
                handler.sendMessage(message);

            }
        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }



}
