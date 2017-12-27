package com.ali.download.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ali.download.entites.ThreadInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by jiang on 2017/8/20.
 * 数据库操作类，进行数据库的线程表的添加，删除，查找的工作
 */

public class Dao {
    private dbHelper dbhelper;
    /**
     * 第一步，在构造函数中实例化dbhelper帮助类，只有得到帮助类的对象才能实例化SQLiteDatabase
     * SQLiteDatabase 通过它可以实现数据库的创建或打开、创建表、插入数据、删除数据、查询数据、修改数据等操作
     *
     */
    public Dao(Context context){
        dbhelper = new dbHelper(context);
    }
    //第二部，编写对应的添加/删除/查找的方法
    public long insertThreadInfo (String url, String path, long start, long finish, long length, int state){
        SQLiteDatabase sqLiteDatabase = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("url",url);
        values.put("path",path);
        values.put("start",start);
        values.put("finish",finish);
        values.put("length",length);
        values.put("state",state);
        long result = sqLiteDatabase.insert("ThreadInfo",null,values);
        sqLiteDatabase.close();
        return result;
    }

    public int deleteThreadInfo (String url){
        SQLiteDatabase sqLiteDatabase = dbhelper.getWritableDatabase();
        int result = sqLiteDatabase.delete("ThreadInfo","url = ?",new String[]{url});
        sqLiteDatabase.close();
        return result;
    }
    public List<ThreadInfo> findThreadInfo (String url){
        List<ThreadInfo> list = new ArrayList<ThreadInfo>();
        SQLiteDatabase sqLiteDatabase = dbhelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query("ThreadInfo",null,"url = ?",new String[]{url},null,null,null);
        while (cursor.moveToNext()){
            ThreadInfo threadInfo = new ThreadInfo();
            threadInfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            threadInfo.setPath(cursor.getString(cursor.getColumnIndex("path")));
            threadInfo.setStart(cursor.getInt(cursor.getColumnIndex("start")));
            threadInfo.setFinish(cursor.getInt(cursor.getColumnIndex("finish")));
            threadInfo.setLength(cursor.getInt(cursor.getColumnIndex("length")));
            threadInfo.setState(cursor.getInt(cursor.getColumnIndex("state")));
            list.add(threadInfo);
        }
        cursor.close();
        sqLiteDatabase.close();
        return list;
    }
    public void updateThreadInfo (long start,long finish,String url,int state){
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        db.execSQL("update ThreadInfo set start = ?,state =? and finish = ? where url = ?",
                new Object[]{start,finish,url,state});
    }
    public int updateThreadInfoState (String url,int state){
        Log.i("gengxin",">>>>>>>>>>>>>>>>>");
        SQLiteDatabase sqLiteDatabase = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("state",state);

        int result = sqLiteDatabase.update("ThreadInfo",values,"url=?",new String[]{url});
        sqLiteDatabase.close();
        return result;
        //db.execSQL("update ThreadInfo set state =? where url = ?",
            //    new Object[]{url,state});

    }
}
