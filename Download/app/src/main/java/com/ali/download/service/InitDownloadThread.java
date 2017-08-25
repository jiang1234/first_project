package com.ali.download.service;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ali.download.entites.FileInfo;
import com.ali.download.entites.ThreadInfo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.R.id.message;

/**
 * Created by jiang on 2017/8/23.
 * 下载初始化线程
 */

public class InitDownloadThread extends Thread {
    private ThreadInfo threadInfo;
    private int fileLength;
    private Handler handler;
    public InitDownloadThread(ThreadInfo threadInfo, Handler handler){
        this.threadInfo = threadInfo;
        this.handler = handler;
    }
    @Override
    public void run(){
        try{
            Log.i("init_thread","1");
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
                String fileName = absurl.getFile().substring(absurl.getFile().lastIndexOf("/")+1,absurl.getFile().length());
                Log.i("fileName",fileName);
                File file = new File(path, fileName);
                RandomAccessFile raf = new RandomAccessFile(file,"rwd");
                raf.setLength(fileLength);
                raf.close();
                urlConn.disconnect();
                Message message = Message.obtain();
                message.obj = threadInfo;
                message.what = 0;
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
