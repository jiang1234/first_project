package com.ali.download;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2017/7/31.
 */

public class Download extends AppCompatActivity {
    private boolean flag = false;
    private Handler handler;
    public Button download;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        handler = new Handler(){
            @Override
            public void handleMessage(Message m){
                if(flag = false){
                    Toast.makeText(Download.this,"下载失败",Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(Download.this,"下载成功",Toast.LENGTH_SHORT).show();
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
                                //String expandName = sourceUrl.substring(sourceUrl.lastIndexOf(".")+1,sourceUrl.length()).toLowerCase();
                                //String expandName="apk";
                                //获取文件名
                                String fileName = Uri.parse(sourceUrl).getQueryParameter("fsname");
                                File file = new File(getApplicationContext().getCacheDir(), fileName);
                                FileOutputStream fos = new FileOutputStream(file);
                                byte buf[] = new byte[128];
                                //读取文件到输出流
                                while(true) {
                                    int numread = is.read(buf);
                                    if(numread<=0){
                                        break;
                                    } else{
                                        fos.write(buf,0,numread);
                                    }
                                }
                            }
                            is.close();
                            urlConn.disconnect();
                            flag = true;
                        } catch (MalformedURLException e){
                            e.printStackTrace();
                            flag = false;
                        } catch (IOException e){
                            e.printStackTrace();
                            flag = false;
                        }
                        Message m = handler.obtainMessage();
                        handler.sendMessage(m);
                    }
                });
                DownloadThread.start();
            }

        });
    }

}



