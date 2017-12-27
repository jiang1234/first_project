package com.ali.download;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
    //private String sourceUrl = "http://202.38.196.91/cache/8/40/imtt.dd.qq.com/8756969d85b5ff63e3b95a4917d9d3de/A9CDC3CD230A39954FC11C4487F07FDA.apk?fsname=com.qzone_7.5.1.288_105.apk&csr=1bbd";
    private String sourceUrl = "http://imtt.dd.qq.com/16891/82F7A39727F95BDDB5F59246DDFCE043.apk?fsname=com.snda.wifilocating_4.2.25_3155.apk&csr=1bbd";
    private FileInfo fileInfo;
    private final static int SEND = 1;
    private final static int REVICER = 2;
    private final static int INIT = 0;
    private int downloadNum = 0;


    Messenger Rmessenger = new Messenger(new Handler(){
        @Override
        public void handleMessage(Message message){
            if(message.what == SEND){

                downloadBar.setProgress(message.arg1);
                String fileName = (String)message.obj;

                if(DownloadService.getHaveDownload()) {
                    Toast.makeText(Download.this, "下载完成!", Toast.LENGTH_SHORT).show();
                    Log.i("anzhuangkaishi","!!!!!!!!!!");
                   installApk(Download.this,getApplicationContext().getCacheDir().getPath() + "/" + fileName);
                }
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
        cleanDatabases(this);
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
        String fileName = "aaaaaaaaaa";
        fileInfo = new FileInfo(sourceUrl,fileName,getApplicationContext().getCacheDir().getPath(),0,0,0);
        Log.i("名字",getApplicationContext().getCacheDir().getPath());
        intent = new Intent();

        intent.setClass(Download.this, DownloadService.class);
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.download:
                downloadNum++;
                intent.putExtra("fileInfo",fileInfo);
                intent.putExtra("downloadNum",downloadNum);
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
    public static void cleanDatabases(Context context) {
        deleteFilesByDirectory(new File("/data/data/"
                + context.getPackageName() + "/databases"));
    }
    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }
    public static void installApk(Context context, String apkFile) {
        File file = new File("" + apkFile);
        if (!file.exists()) {
            Log.i(" not found ","file");
            return;
        }
        chmod("777", apkFile);
        try {
            if (Build.VERSION.SDK_INT >= 24) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + file.toString()), "application/vnd.android.package-archive");
            context.startActivity(intent);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            onInstallFailure(context, apkFile);
        }
    }
    private static void onInstallFailure(Context context, String apkFile) {
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if(!TextUtils.isEmpty(apkFile)) {
                String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                String sdApkFile = apkFile.replace(sdPath, "");
                Toast.makeText(context, "鎵撳紑瀹夎澶辫触銆傛偍鍙互鍒癝D鍗＄洰褰曚笅:" + "\n" + sdApkFile + " 瀹夎鏂扮増娓告垙鍖厏", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, "鎵撳紑瀹夎澶辫触銆傛偍鍙互鍜ㄨ瀹㈡湇锛屾垨鍒板畼缃戜笂涓嬭浇鏂扮増娓告垙鍖厏", Toast.LENGTH_LONG).show();
        }
    }

    public  static void chmod(String permission, String path) {
        try {
            String command = "chmod " + permission + " " + path;
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}



