package com.ali.download;

import android.content.Context;
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

public class Download extends AppCompatActivity {
    private boolean flag = false;
    private Handler handler;
    public Button download;
    private ProgressBar downloadBar;
    private int downloadState = 0;

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
        download.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建一个新线程，用于获取网上的文件
                Thread DownloadThread = new Thread(new Runnable(){
                    @Override
                    public void run(){
                        URL url;

                        try {
                            String sourceUrl = "http://117.169.16.25/imtt.dd.qq.com/16891/1FA1EBDA2BCA25BD8A395DB91DF92B83.apk?mkey=597f642287b880d2&f=e301&c=0&fsname=com.snda.wifilocating_4.2.12_3132.apk&csr=1bbd&p=.apk";
                            url = new URL(sourceUrl);
                            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                            InputStream is = urlConn.getInputStream();
                            if(is != null){
                                //获取文件名
                                String fileName = Uri.parse(sourceUrl).getQueryParameter("fsname");
                                //文件大小
                                int fileLength = urlConn.getContentLength();
                                Log.i("fileLength",String.valueOf (fileLength));
                                File file = new File(getApplicationContext().getCacheDir(), fileName);
                                FileOutputStream fos = new FileOutputStream(file);
                                byte buf[] = new byte[1024];
                                int sumRead = 0;
                                //读取文件到输出流
                                while(true) {
                                    int numread = is.read(buf);
                                    if(numread<=0){
                                        Message readFileWrongM = handler.obtainMessage();
                                        readFileWrongM.what = -2;
                                        handler.sendMessage(readFileWrongM);
                                        break;
                                    } else{
                                        fos.write(buf,0,numread);
                                        sumRead += numread;
                                        //Log.i("numread",String.valueOf (numread));
                                        downloadState = sumRead*100/fileLength;
                                        Log.i("downloadState",String.valueOf (downloadState));
                                        Message downloadStateM = handler.obtainMessage();
                                        downloadStateM.what = 0;
                                        handler.sendMessage(downloadStateM);
                                    }
                                }
                            }
                            is.close();
                            urlConn.disconnect();
                            Message downloadFinishM = handler.obtainMessage();
                            downloadFinishM.what = 1;
                            handler.sendMessage(downloadFinishM);
                        } catch (MalformedURLException e){
                            e.printStackTrace();
                            Message downloadWrongM = handler.obtainMessage();
                            downloadWrongM.what = -1;
                            handler.sendMessage(downloadWrongM);
                        } catch (IOException e){
                            e.printStackTrace();
                            Message downloadWrongM = handler.obtainMessage();
                            downloadWrongM.what = -1;
                            handler.sendMessage(downloadWrongM);
                        }

                    }
                });
                DownloadThread.start();
            }

        });
    }

}



