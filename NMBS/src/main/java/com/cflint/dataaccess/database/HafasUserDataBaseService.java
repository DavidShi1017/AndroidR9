package com.cflint.dataaccess.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.cflint.application.NMBSApplication;
import com.cflint.log.LogUtils;
import com.cflint.model.HafasUser;

import net.sqlcipher.database.SQLiteDatabase;

/**
 * Created by Richard on 4/6/16.
 */
public class HafasUserDataBaseService {
    private SQLiteDatabase sqLiteDatabase;
    private DatabaseHelper dbHelper;

    // Database fields
    public static final String DB_TABLE_HAFAS_USER = "Hafas_user_table";
    public static final String HAFAS_USER_ID = "user_id";
    public static final String HAFAS_USER_language = "user_language";
    public static final String HAFAS_REGISTER_ID = "register_id";

    public HafasUserDataBaseService(Context context) {
        dbHelper = DatabaseHelper.getInstance(NMBSApplication.getInstance().getApplicationContext());
        sqLiteDatabase = dbHelper.getWritableDatabase();
    }

    /**
     * Insert data to table.
     *
     *            order
     * @return true means everything is OK, otherwise means failure
     */
    public boolean insertHafasUser(HafasUser hafasUser) {
        LogUtils.e("insertHafasUser", " insertHafasUser...." + hafasUser);
        if(sqLiteDatabase == null){
            sqLiteDatabase = dbHelper.getWritableDatabase();
        }
        if(sqLiteDatabase == null){
            return false;
        }
        sqLiteDatabase.beginTransaction();
        ContentValues contentValues = new ContentValues();
        contentValues.put(HAFAS_USER_ID, hafasUser.getUserId());
        contentValues.put(HAFAS_USER_language, hafasUser.getLanguage());
        contentValues.put(HAFAS_REGISTER_ID, hafasUser.getRegisterId());

        try {
            sqLiteDatabase.insert(DB_TABLE_HAFAS_USER, hafasUser.getUserId(),
                    contentValues);
            sqLiteDatabase.setTransactionSuccessful();
            LogUtils.e("insertHafasUser", " insertHafasUser....Successful");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;

        }finally{
            sqLiteDatabase.endTransaction();
        }

    }

    public boolean deleteHafasUser(String userId) {
        if(sqLiteDatabase == null){
            sqLiteDatabase = dbHelper.getWritableDatabase();
        }
        if(sqLiteDatabase == null){
            return false;
        }
        sqLiteDatabase.beginTransaction();
        try {
            sqLiteDatabase.delete(DB_TABLE_HAFAS_USER, HAFAS_USER_ID+"=?", new String[]{userId});
            sqLiteDatabase.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;

        }finally{
            sqLiteDatabase.endTransaction();
        }

    }

    public void startTransacstion(){
        sqLiteDatabase.beginTransaction();
    }

    public void endTransaction(){
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
    }

    public HafasUser readHafasUser() {
        HafasUser hafasUser = null;
        if(sqLiteDatabase == null){
            sqLiteDatabase = dbHelper.getWritableDatabase();
        }
        if(sqLiteDatabase == null){
            return hafasUser;
        }
        Cursor cursor = sqLiteDatabase.query(DB_TABLE_HAFAS_USER, null, null, null, null, null, null);
        int cursorNum = cursor.getCount();
        for (int i = 0; i < cursorNum; i++) {
            cursor.moveToPosition(i);
            String userId = cursor.getString(cursor.getColumnIndexOrThrow(HAFAS_USER_ID));
            String language = cursor.getString(cursor.getColumnIndexOrThrow(HAFAS_USER_language));
            String registerId = cursor.getString(cursor.getColumnIndexOrThrow(HAFAS_REGISTER_ID));
            hafasUser = new HafasUser(userId,registerId,language);
        }
        cursor.close();
        return hafasUser;
    }
}
