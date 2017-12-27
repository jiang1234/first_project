package com.ali.download.db;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by jiang on 2017/8/20.
 * 建立一个数据库来存储下载线程
 * 新建继承自SQLiteOpenHelper的数据库帮助类
 */

public class dbHelper extends SQLiteOpenHelper {

    private final static String CreateTable = "create table ThreadInfo ("
            + "id integer primary key autoincrement,"
            + "url text,"
            + "path text,"
            + "start integer,"
            + "finish integer,"
            + "length integer,"
            + "state integer)";
    private final static String DropTable = "drop table if exists ThreadInfo";
    //1正在下载 0未下载 2暂停 3删除 4完成
    // 第一步，编写构造函数
    public dbHelper(Context context){
        /**
         * 第一个参数：上下文
         * 第二个参数：数据库名称
         * 第三个参数：null代表默认的游标工厂
         * 第四个参数：数据库版本号，只能升
         */
        super(context, "DownloadDb", null, 1);
    }
    /**第二部，复写onCreate函数.
     * onCreate()： onCreate是在数据库创建的时候调用的，主要用来初始化数据表结构和插入数据初始化的记录
     * 适合在这个方法中把表的结构定义出来
     */
    @Override
    public void onCreate(SQLiteDatabase db){
        Log.i("drop","________");
        db.execSQL(DropTable);
        db.execSQL(CreateTable);
    }
    /**
     * 第三步，复写onUpgrade
     * 数据库更新时调用
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion){
        db.execSQL(DropTable);
        db.execSQL(CreateTable);
    }
}
