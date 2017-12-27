package com.ali.download.entites;

import java.io.File;

/**
 * Created by jiang on 2017/8/20.
 */

public class ThreadInfo {
    private String url;
    private String path;
    private long start;
    private long finish;
    private long length;
    private int state;

    public ThreadInfo(){

    }
    public ThreadInfo(String url,String path,long start,long finish,long length,int state){
        this.url = url;
        this.path = path;
        this.start = start;
        this.finish = finish;
        this.length = length;
        this.state = state;
    }
    public void setUrl(String url){
        this.url = url;
    }
    public void setPath(String path) {this.path = path;}
    public void setStart(long start){this.start = start;}
    public void setFinish(long finish){this.finish = finish;}
    public void setLength(long length){this.length = length;}
    public void setState(int state){this.state = state;}
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
    public int getState(){
        return this.state;
    }
}

