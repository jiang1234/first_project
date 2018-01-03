package com.ali.downloadfragment;

import android.widget.ImageView;

/**
 * Created by Administrator on 2018/1/3.
 */

public class AppItem {
    private String name;
    private String type;
    private int imageId;
    public AppItem(String name,String type,int imageId){
        this.name = name;
        this.type = type;
        this.imageId = imageId;
    }
    public String getName(){
        return this.name;
    }
    public String getType(){
        return this.type;
    }
    public int getImageId(){
        return this.imageId;
    }

}
