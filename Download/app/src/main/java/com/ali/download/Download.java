package com.ali.download;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
    private Intent intent;
    private boolean flag = false;
    private Handler handler;
    public Button download, pause;
    private ProgressBar downloadBar;
    private int downloadState = 0;
    private String sourceUrl = "http://117.169.16.25/imtt.dd.qq.com/16891/1FA1EBDA2BCA25BD8A395DB91DF92B83.apk?mkey=597f642287b880d2&f=e301&c=0&fsname=com.snda.wifilocating_4.2.12_3132.apk&csr=1bbd&p=.apk";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        downloadBar = (ProgressBar)findViewById(R.id.downloadBar);

        handler = new Handler(){
            @Override
            public void handleMessage(Message m){
                switch(m.what){
                    case -2:
                        //Toast.makeText(Download.this,"读取文件失败",Toast.LENGTH_SHORT).show();
                        break;
                    case -1:
                        Toast.makeText(Download.this,"下载失败",Toast.LENGTH_SHORT).show();break;
                    case 0:
                        downloadBar.setProgress(downloadState);
                        //Log.i("Getdata",String.valueOf (downloadState));
                        break;
                    case 1:
                        downloadBar.setProgress(downloadState);
                        Toast.makeText(Download.this,"下载成功",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        //下载按钮点击事件
        download = (Button) findViewById(R.id.download);
        pause = (Button) findViewById(R.id.pause);
        download.setOnClickListener(this);
        pause.setOnClickListener(this);

        //获取文件名
        String fileName = Uri.parse(sourceUrl).getQueryParameter("fsname");
        FileInfo fileInfo = new FileInfo(sourceUrl,fileName,getApplicationContext().getCacheDir().getPath(),0,0);
        intent = new Intent();
        intent.putExtra("fileInfo",fileInfo);
        intent.setClass(Download.this, DownloadService.class);
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.download:
                intent.setAction(ACTION_START);
                startService(intent);
                break;
            case R.id.pause:
                intent.setAction(ACTION_PAUSE);
                startService(intent);
                break;
        }
    }

}



