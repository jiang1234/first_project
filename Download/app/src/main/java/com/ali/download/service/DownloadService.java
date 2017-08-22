package com.ali.download.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.ali.download.db.Dao;
import com.ali.download.entites.FileInfo;
import com.ali.download.entites.ThreadInfo;

import java.util.List;

/**
 * Created by jiang on 2017/8/20.
 * 建立下载服务
 * 用服务主要是因为服务可以在后台进行长时间的运行操作
 */

public class DownloadService extends Service{
    private final static String ACTION_START="start download";
    private final static String ACTION_PAUSE="pause download";
    private Dao downloadDao;
    private ThreadInfo threadInfo, iniThreadInfo;
    private DownloadThread downloadThread;
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }
    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        downloadDao = new Dao(this);
        FileInfo fileInfo = (FileInfo)intent.getSerializableExtra("fileInfo");
        if(ACTION_START == intent.getAction()){
            //判断是否存在此线程
            List downloadList = downloadDao.findThreadInfo(fileInfo.getUrl());
            Log.i("线程数量",String.valueOf(downloadList.size()));
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
            }
            downloadThread = new DownloadThread(threadInfo, this);
            downloadThread.start();
        }
        if(ACTION_PAUSE == intent.getAction()){
            downloadThread.setIsPause(true);
        }
        return super.onStartCommand(intent,flags,startId);
    }

}


