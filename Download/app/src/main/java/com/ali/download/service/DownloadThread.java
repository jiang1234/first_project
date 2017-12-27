package com.ali.download.service;

import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.ali.download.entites.ThreadInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2017/10/11.
 */

public class DownloadThread extends Thread {
    private final static int SEND = 1;
    private boolean isPause = false;
    private boolean isDelete = false;
    private ThreadInfo threadInfo;
    private long start, fileLength;
    private long sumread, finish;
    private Messenger Rmessenger;

    public void setPause(boolean isPause) {
        this.isPause = isPause;
    }

    public void setDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    public DownloadThread(ThreadInfo threadInfo, Messenger Rmeaaemger) {
        this.threadInfo = threadInfo;
        this.Rmessenger = Rmeaaemger;
    }

    @Override
    public void run() {

        Log.i("开始线程206", "开始线程");
        String sourceUrl = threadInfo.getUrl();
        try {
            URL url = new URL(sourceUrl);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();

            start = threadInfo.getStart() + threadInfo.getFinish();
            sumread = (int) start;
            //Log.i("start",String.valueOf(sourceUrl));
            //Log.i("fileLength1",String.valueOf(urlConn.getContentLength()));
            //取得的线程中是否有文件有关的信息，若有直接读取，否则初始化
            if (threadInfo.getLength() == 0) {
                fileLength = urlConn.getContentLength();
            } else {
                fileLength = threadInfo.getLength();
            }
            //Log.i("fileLength",String.valueOf(urlConn.getContentLength()));
            //
            //Log.i("getResponseCode",String.valueOf(urlConn.getResponseCode()));
            Log.i("start", String.valueOf(threadInfo.getStart()));
            Log.i("getFinish", String.valueOf(threadInfo.getFinish()));
            Log.i("fileLength", String.valueOf(fileLength));
            urlConn.setRequestProperty("Range", "bytes=" + start + "-" + fileLength);
            Log.i("getResponseCode", String.valueOf(urlConn.getResponseCode()));
            //urlConn调用之后函数的操作不能在setRequestProperty之前，要不然会说不能在conn连接打开之后进行setRequestProperty
            //调用了setRequestProperty后ResponseCode变为206
            if (urlConn.getResponseCode() == 206) {
                //连接正常

                Log.i("getResponseCode1", threadInfo.getPath());
                InputStream is = urlConn.getInputStream();
                File path = new File(threadInfo.getPath());
                URL absurl = urlConn.getURL();
                String fileName1 = absurl.getFile().substring(absurl.getFile().lastIndexOf("/")+1);
                String fileName = fileName1.substring(0,fileName1.lastIndexOf("?"));
                Log.i("fileName1…………",fileName1);
                Log.i("fileName2…………",fileName);
                File file = new File(path, fileName);
                Log.i("fileexistdonwload", String.valueOf(file.exists()));
                RandomAccessFile raf = new RandomAccessFile(file, "rwd");
                raf.seek(start);
                //finish = threadInfo.getFinish();
                byte buf[] = new byte[1024];

                while (!isPause && !isDelete) {
                    int numread = is.read(buf);
                   // Log.i("numread",String.valueOf(numread));
                    if (numread == -1) {
                        break;
                    } else {
                        raf.write(buf, 0, numread);
                        sumread += numread;
                        finish += numread;
                        //message.what = 1;
                        //message.arg1 = sumread*100/fileLength;
                        Message sMessage = Message.obtain();
                        sMessage.what = SEND;
                        //Log.i("sumread",String.valueOf(sumread));
                        sMessage.arg1 = (int)(sumread*100/fileLength);
                        sMessage.obj = fileName;
                        if(sumread == fileLength)
                        {
                            sMessage.arg2 = 1;
                            DownloadService.setHaveDownload(true);
                        }
                        //
                        try {
                            Rmessenger.send(sMessage);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                            ;
                        }
                        if (isPause ) {
                            //downloadDao.updateThreadInfo(start,finish,sourceUrl);
                            break;
                        }
                        if (isDelete ) {
                            //downloadDao.updateThreadInfo(start,finish,sourceUrl);
                            Message sDMessage = Message.obtain();
                            sDMessage.what = SEND;
                            sDMessage.arg1 = 0;

                            try {
                                Rmessenger.send(sDMessage);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                                ;
                            }
                            break;
                        }


                    }
                }


                is.close();
                urlConn.disconnect();


            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


