package com.ali.download.service;

import android.app.Service;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.ali.download.db.Dao;
import com.ali.download.entites.FileInfo;
import com.ali.download.entites.ThreadInfo;

import java.io.File;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.R.id.message;

/**
 * Created by jiang on 2017/8/20.
 * 建立下载服务
 * 用服务主要是因为服务可以在后台进行长时间的运行操作
 */

public class DownloadService extends Service{
    private final static String ACTION_START="start download";
    private final static String ACTION_PAUSE="pause download";
    private final static String ACTION_DELETE = "delete download";
    private final static String ACTION_REDOWNLOAD = "redownload";
    private final static int SEND = 1;
    private final static int REVICER = 2;
    private final static int INIT = 0;
    private Dao downloadDao = new Dao(this);;
    private static boolean haveDownload = false;
    private ThreadInfo threadInfo;
    private HashMap<String, Thread> map = new HashMap<String, Thread>();
    private DownloadThread d1;
    private InitDownloadThread d2;
    private FileInfo fileInfo;
    private int sumread,finish,downloadNum;
    private long start,fileLength;
    private Dao ThreadInfoDao;
    private ExecutorService downloadThreadPool = Executors.newFixedThreadPool(5);
    DownloadThread downloadThread;
    File file;
    String fileName;
    Messenger Rmessenger;
    public static void setHaveDownload(boolean a){
        haveDownload = a;
    }
    public static boolean getHaveDownload(){
        return haveDownload;
    }
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
                //threadInfo = (ThreadInfo) message.obj;
                //sumread = message.arg1;
                Map values = (Map)message.obj;
                fileName = (String)values.get("fileName");
                if(message.arg1 == 1){
                    haveDownload = true;
                    Toast.makeText(DownloadService.this, "文件已存在", Toast.LENGTH_SHORT).show();
                    String apkFile = fileInfo.getPath() + "/" + fileName;
                    installApk(DownloadService.this,apkFile); 
                }
                Log.i("进入下载线程", "ddddd");
               downloadThread = new DownloadThread((ThreadInfo)values.get("threadInfo"),Rmessenger);
                //downloadThreadPool.execute(downloadThread);
                downloadThread.start();
                map.put("downloadThread",downloadThread);
                d1 = downloadThread;
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
        fileInfo = (FileInfo)intent.getSerializableExtra("fileInfo");
      //  downloadNum = (int)intent.getSerializableExtra("downloadNum");
        if(ACTION_START == intent.getAction()){

            //判断是否存在此线程
                if(haveDownload){
                    Toast.makeText(this, "文件已存在", Toast.LENGTH_SHORT).show();
                    String apkFile = fileInfo.getPath() + "/" + fileName;
                    installApk(DownloadService.this,apkFile);
                }else {
                    List downloadList = downloadDao.findThreadInfo(fileInfo.getUrl());
                    Log.i("线程数量", String.valueOf(downloadList.size()));
                    //  Log.i("名字1", fileInfo.getPath());
                    //如果不存在则添加
                    if (downloadList.size() == 0) {
                        // Log.i("添加失败", "添加失败1");
                        long addResult = downloadDao.insertThreadInfo(fileInfo.getUrl(), fileInfo.getPath(), fileInfo.getStart(), fileInfo.getFinish(), fileInfo.getLength(), 0);
                        if (addResult == -1) {
                            Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show();
                            Log.i("添加失败", "添加失败");
                        } else {
                            Toast.makeText(this, "添加到第" + addResult + "行", Toast.LENGTH_SHORT).show();
                            Log.i("添加", "添加");
                        }
                        threadInfo = new ThreadInfo(fileInfo.getUrl(), fileInfo.getPath(), fileInfo.getStart(), fileInfo.getFinish(), fileInfo.getLength(), 0);
                    }
                    //存在，则读取最新的一条
                    else {
                        threadInfo = (ThreadInfo) downloadList.get(downloadList.size() - 1);
                        Log.i("url", threadInfo.getUrl());
                        Log.i("path", threadInfo.getPath());
                    }
                    if (threadInfo.getState() != 1) {

                        InitDownloadThread initDownloadThread = new InitDownloadThread(threadInfo, handler);
                        initDownloadThread.start();
                        Log.i("gengxin", "gengxin");

                        downloadDao.updateThreadInfoState(fileInfo.getUrl(), 1);
                        long addResult = downloadDao.updateThreadInfoState(fileInfo.getUrl(), 1);
                        Log.i("更新是否失败", String.valueOf(addResult));
                        map.put("initDownloadThread", initDownloadThread);
                        d2 = initDownloadThread;
                        //isDownload = true;
                    }
                }
        }
        if(ACTION_PAUSE == intent.getAction()){

            if(downloadThread!=null){
                downloadThread.setPause(true);
            }else{
                Toast.makeText(this, "没有该任务", Toast.LENGTH_SHORT).show();
            }
            downloadDao.updateThreadInfoState(fileInfo.getUrl(),2);
        }
        if(ACTION_DELETE == intent.getAction()){
           // DownloadThread downloadThread = d1;
            if(downloadThread!=null){
                downloadThread.setDelete(true);
                delete();
                haveDownload = false;
                sumread = 0;
            }else{
                delete();
                //Toast.makeText(this, "没有该任务", Toast.LENGTH_SHORT).show();
            }

        }
        if(ACTION_REDOWNLOAD == intent.getAction()){
           // DownloadThread downloadThread = d1;
            haveDownload = false;
            if(downloadThread!=null){
                downloadThread.setDelete(true);
            }
            delete();
            Log.i("delete","000000000000");
            int result =downloadDao.deleteThreadInfo(fileInfo.getUrl());
            Log.i("numdelete",String.valueOf(result));
            long addResult = downloadDao.insertThreadInfo(fileInfo.getUrl(),fileInfo.getPath(),fileInfo.getStart(),fileInfo.getFinish(),fileInfo.getLength(),0);
            threadInfo = new ThreadInfo(fileInfo.getUrl(),fileInfo.getPath(),fileInfo.getStart(),fileInfo.getFinish(),fileInfo.getLength(),0);
            InitDownloadThread initDownloadThread = new InitDownloadThread(threadInfo, handler);
            downloadDao.updateThreadInfoState(fileInfo.getUrl(),1);
            initDownloadThread.start();


    }
        return super.onStartCommand(intent,flags,startId);
    }

    public void delete(){
        String filePath = fileInfo.getPath() +"/"+ fileName;
        File file = new File(filePath);
        Log.i("delete",filePath);
        Log.i("fileexistdelete1", String.valueOf(file.exists()));

        file.delete();
        Log.i("fileexistdelete2", String.valueOf(file.exists()));
        Log.i("delete","2");
            Message message = Message.obtain();
        Log.i("delete","3");
            message.what = SEND;
        Log.i("delete","4");
            message.arg1 = 0;
        Log.i("delete","5");

            try{
                Rmessenger.send(message);
                Log.i("delete","6");
            }catch (RemoteException e){
                e.printStackTrace();;
                Log.i("delete","false");
            }
        //ThreadInfoDao = new Dao(DownloadService.this);
        downloadDao.updateThreadInfoState(fileInfo.getUrl(),3);
        //isdelete = false;
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




