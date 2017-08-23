package com.ali.download.entites;

import java.io.File;

/**
 * Created by jiang on 2017/8/20.
 */

public class ThreadInfo {
    private String url;
    private String path;
    private int start;
    private int finish;
    private int length;

    public ThreadInfo(){

    }
    public ThreadInfo(String url,String path,int start,int finish,int length){
        this.url = url;
        this.path = path;
        this.start = start;
        this.finish = finish;
        this.length = length;
    }
    public void setUrl(String url){
        this.url = url;
    }
    public void setPath(String path) {this.path = path;}
    public void setStart(int start){this.start = start;}
    public void setFinish(int finish){this.finish = finish;}
    public void setLength(int length){this.length = length;}
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

