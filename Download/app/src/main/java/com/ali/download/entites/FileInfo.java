package com.ali.download.entites;

import java.io.File;
import java.io.Serializable;

/**
 * Created by jiang on 2017/8/21.
 * 传出跟下载文件有关的信息的类
 */

public class FileInfo implements Serializable{
    private String url;
    private String fileName = "aaa";
    private String path;
    private long start;
    private long finish;
    private long length;

    public FileInfo(){

    }
    public FileInfo(String url,String fileName,String path,long start, long finish,long length){
        this.url = url;
        this.fileName = fileName;
        this.path = path;
        this.start = start;
        this.finish = finish;
        this.length = length;
    }
    public String getUrl(){
        return this.url;
    }
    public String getPath(){
        return this.path;
    }
    public long getFinish(){
        return this.finish;
    }
    public long getLength(){
        return this.length;
    }
    public long getStart(){
        return this.start;
    }
    public String getFileName(){return this.fileName;}
    public void setLength(long length){this.length = length;}
    public void setFileName(String fileName){this.fileName = fileName;}
}
