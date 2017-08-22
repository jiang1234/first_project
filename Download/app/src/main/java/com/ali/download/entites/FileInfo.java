package com.ali.download.entites;

import java.io.File;
import java.io.Serializable;

/**
 * Created by jiang on 2017/8/21.
 * 传出跟下载文件有关的信息的类
 */

public class FileInfo implements Serializable{
    private String url;
    private String fileName;
    private String path;
    private int start;
    private int finish;
    private int length;

    public FileInfo(){

    }
    public FileInfo(String url,String fileName,String path,int start, int finish,int length){
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
    public int getFinish(){
        return this.finish;
    }
    public int getLength(){
        return this.length;
    }
    public int getStart(){
        return this.start;
    }
}
