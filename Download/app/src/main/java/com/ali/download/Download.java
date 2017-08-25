package com.ali.download;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.ali.download.entites.FileInfo;
import com.ali.download.service.DownloadService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Jiang on 2017/7/31.
 */

public class Download extends AppCompatActivity implements OnClickListener{
    private final static String ACTION_START="start download";
    private final static String ACTION_PAUSE="pause download";
    private final static String ACTION_DELETE = "delete download";
    private final static String ACTION_REDOWNLOAD = "redownload";
    private Intent intent;
    private boolean flag = false;
    private Handler handler;
    public Button download, pause, delete, redownload;
    private ProgressBar downloadBar;
    private int downloadState = 0;
    private String sourceUrl = "http://app.mi.com/download/12339";
    private FileInfo fileInfo;
    private final static int SEND = 1;
    private final static int REVICER = 2;
    private final static int INIT = 0;

    Messenger Rmessenger = new Messenger(new Handler(){
        @Override
        public void handleMessage(Message message){
            if(message.what == SEND){
                downloadBar.setProgress(message.arg1);
            }
        }
    });
    Messenger Smessenger;
    //重写ServiceConnection
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Smessenger = new Messenger(iBinder);
            Message Rmessage = Message.obtain();
            Rmessage.what = REVICER;
            Rmessage.replyTo = Rmessenger;
            try{
                Smessenger.send(Rmessage);
            }catch (RemoteException e){
                e.printStackTrace();;
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i("disconnected","disconnected");
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        downloadBar = (ProgressBar)findViewById(R.id.downloadBar);


        //下载按钮点击事件
        download = (Button) findViewById(R.id.download);
        pause = (Button) findViewById(R.id.pause);
        delete = (Button) findViewById(R.id.delete);
        redownload = (Button) findViewById(R.id.redownload);
        download.setOnClickListener(this);
        pause.setOnClickListener(this);
        delete.setOnClickListener(this);
        redownload.setOnClickListener(this);

        //获取文件名
        String fileName = null;
        fileInfo = new FileInfo(sourceUrl,fileName,getApplicationContext().getCacheDir().getPath(),0,0,0);
        Log.i("名字",getApplicationContext().getCacheDir().getPath());
        intent = new Intent();

        intent.setClass(Download.this, DownloadService.class);
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.download:
                intent.putExtra("fileInfo",fileInfo);
                intent.setAction(ACTION_START);
                startService(intent);
                Log.i("开启服务","开启服务");
                bindService(intent,serviceConnection,Context.BIND_AUTO_CREATE);
                break;
            case R.id.pause:
                intent.putExtra("fileInfo",fileInfo);
                intent.setAction(ACTION_PAUSE);
                startService(intent);
                Log.i("暂停服务","暂停服务");
                bindService(intent,serviceConnection,Context.BIND_AUTO_CREATE);
                break;
            case R.id.delete:
                intent.putExtra("fileInfo",fileInfo);
                intent.setAction(ACTION_DELETE);
                startService(intent);
                bindService(intent,serviceConnection,Context.BIND_AUTO_CREATE);
                break;
            case R.id.redownload:
                intent.putExtra("fileInfo",fileInfo);
                intent.setAction(ACTION_REDOWNLOAD);
                startService(intent);
                bindService(intent,serviceConnection,Context.BIND_AUTO_CREATE);
                break;
        }
    }

}



