package com.ali.download.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.ali.download.db.Dao;
import com.ali.download.entites.FileInfo;
import com.ali.download.entites.ThreadInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static android.R.id.message;

/**
 * Created by jiang on 2017/8/20.
 * 建立下载服务
 * 用服务主要是因为服务可以在后台进行长时间的运行操作
 */

public class DownloadService extends Service{
    private boolean isPause = false;
    private boolean isdelete = false;
    private final static String ACTION_START="start download";
    private final static String ACTION_PAUSE="pause download";
    private final static String ACTION_DELETE = "delete download";
    private final static String ACTION_REDOWNLOAD = "redownload";
    private final static int SEND = 1;
    private final static int REVICER = 2;
    private final static int INIT = 0;
    private Dao downloadDao;
    private ThreadInfo threadInfo;

    private FileInfo fileInfo;
    private int start,fileLength,sumread;
    private Dao ThreadInfoDao;
    Messenger Rmessenger;
    Messenger messenger = new Messenger(new Handler(){
        public void handleMessage(Message message){
            Log.i("message.what",String.valueOf(message.what));
            if(message.what == REVICER){
                Rmessenger = message.replyTo;
            }
        }
    });
    Handler handler = new Handler(){
        public void handleMessage(Message message){
            Log.i("what","0");
            if(message.what == INIT){
                DownloadThread();
            }
        }
    };
    @Override
    public IBinder onBind(Intent intent){
        //对外通信的接口
        return messenger.getBinder();
    }
    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        downloadDao = new Dao(this);
        fileInfo = (FileInfo)intent.getSerializableExtra("fileInfo");
        if(ACTION_START == intent.getAction()){
            //判断是否存在此线程
            List downloadList = downloadDao.findThreadInfo(fileInfo.getUrl());
            Log.i("线程数量",String.valueOf(downloadList.size()));
            Log.i("名字1",fileInfo.getPath());
            //如果不存在则添加
            if(downloadList.size() == 0){
                Log.i("添加失败","添加失败1");
                long addResult = downloadDao.insertThreadInfo(fileInfo.getUrl(),fileInfo.getPath(),fileInfo.getStart(),fileInfo.getFinish(),fileInfo.getLength());
                if(addResult == -1){
                    Toast.makeText(this,"添加失败",Toast.LENGTH_SHORT).show();
                    Log.i("添加失败","添加失败");
                } else{
                    Toast.makeText(this,"添加到第"+addResult+"行",Toast.LENGTH_SHORT).show();
                    Log.i("添加","添加");
                }
                threadInfo = new ThreadInfo(fileInfo.getUrl(),fileInfo.getPath(),fileInfo.getStart(),fileInfo.getFinish(),fileInfo.getLength());
            }
            //存在，则读取最新的一条
            else{
                threadInfo = (ThreadInfo) downloadList.get(downloadList.size()-1);
                Log.i("url",threadInfo.getUrl());
                Log.i("path",threadInfo.getPath());
            }
            InitDownloadThread initDownloadThread = new InitDownloadThread(threadInfo, handler);
            initDownloadThread.start();
        }
        if(ACTION_PAUSE == intent.getAction()){
            isPause = true;
            ThreadInfoDao = new Dao(DownloadService.this);
            ThreadInfoDao.updateThreadInfo(start,sumread,threadInfo.getUrl());
            isPause = false;
        }
        if(ACTION_DELETE == intent.getAction()){
            delete();
            sumread = 0;
        }
        if(ACTION_REDOWNLOAD == intent.getAction()){
            delete();
            long addResult = downloadDao.insertThreadInfo(fileInfo.getUrl(),fileInfo.getPath(),fileInfo.getStart(),fileInfo.getFinish(),fileInfo.getLength());
            threadInfo = new ThreadInfo(fileInfo.getUrl(),fileInfo.getPath(),fileInfo.getStart(),fileInfo.getFinish(),fileInfo.getLength());
            InitDownloadThread initDownloadThread = new InitDownloadThread(threadInfo, handler);
            initDownloadThread.start();
        }
        return super.onStartCommand(intent,flags,startId);
    }
    public void delete(){
        Log.i("delete","1");
        isdelete = true;
        int result =downloadDao.deleteThreadInfo(fileInfo.getUrl());
        Log.i("numdelete",String.valueOf(result));
        String sourceUrl = threadInfo.getUrl();
        try {
            URL url = new URL(sourceUrl);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            URL absurl = urlConn.getURL();
            String fileName = absurl.getFile().substring(absurl.getFile().lastIndexOf("/") + 1, absurl.getFile().length());
            File file = new File(fileInfo.getPath() +"/"+fileName);
            //Log.i("fileexist",Boolean.valueOf(file.exists()));
            if (file.exists() && file.isFile()) {
                Log.i("existfile","1");
                file.delete();
            }
            Message message = Message.obtain();
            message.what = SEND;
            message.arg1 = 0;

            try{
                Rmessenger.send(message);
            }catch (RemoteException e){
                e.printStackTrace();;
            }
        }catch(MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        isdelete = false;
    }

    public void DownloadThread (){
         new Thread(new Runnable() {
             @Override
             public void run() {
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
                     //Log.i("getResponseCode",String.valueOf(urlConn.getResponseCode()));
                     urlConn.setRequestProperty("Range","bytes=" + start + "-" + fileLength);
                     Log.i("getResponseCode",String.valueOf(urlConn.getResponseCode()));
                     //urlConn调用之后函数的操作不能在setRequestProperty之前，要不然会说不能在conn连接打开之后进行setRequestProperty
                     //调用了setRequestProperty后ResponseCode变为206
                     if(urlConn.getResponseCode() == 206){
                         //连接正常

                         Log.i("getResponseCode1",threadInfo.getPath());
                         InputStream is = urlConn.getInputStream();
                         File path = new File(threadInfo.getPath());
                         URL absurl = urlConn.getURL();
                         String fileName = absurl.getFile().substring(absurl.getFile().lastIndexOf("/")+1,absurl.getFile().length());
                         //String fileName = "1.apk";
                         Log.i("fileName",fileName);
                         File file = new File(path, fileName);
                         RandomAccessFile raf = new RandomAccessFile(file, "rwd") ;
                         raf.seek(start);
                         //finish = threadInfo.getFinish();
                         byte buf[] = new byte[1024];

                         while (!isPause&&!isdelete){
                             int numread = is.read(buf);
                             //Log.i("numread",String.valueOf(numread));
                             if(numread ==-1) {
                                 break;
                             }else{
                                 raf.write(buf,0,numread);
                                 sumread += numread;
                                 //message.what = 1;
                                 //message.arg1 = sumread*100/fileLength;
                                 Message Smessage = Message.obtain();
                                 Smessage.what = SEND;
                                 Smessage.arg1 = sumread*100/fileLength;
                                 //Log.i("sumread",String.valueOf(sumread));
                                 try{
                                     Rmessenger.send(Smessage);
                                 }catch (RemoteException e){
                                     e.printStackTrace();;
                                 }

                             }
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
             }
         }).start();


    }


}


