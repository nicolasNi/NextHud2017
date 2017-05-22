package com.lt.nexthud2017.music.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/5/12.
 */

public class MusicDBHelper extends SQLiteOpenHelper {
    private Context mContext;
    private static final String CREATE_MUSIC_TABLE = "CREATE TABLE if not exists music (_id INTEGER PRIMARY KEY AUTOINCREMENT, musciName VARCHAR,  airtistName VARCHAR,  albumName VARCHAR, No SMALLINT)";
    public MusicDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MUSIC_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

