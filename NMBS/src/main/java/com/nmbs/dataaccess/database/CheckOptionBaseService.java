package com.nmbs.dataaccess.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.nmbs.application.NMBSApplication;
import com.nmbs.log.LogUtils;
import com.nmbs.model.CheckOption;
import com.nmbs.model.LogonInfo;
import com.nmbs.util.DateUtils;

import net.sqlcipher.database.SQLiteDatabase;

public class CheckOptionBaseService {

    private SQLiteDatabase sqLiteDatabase;
    private DatabaseHelper dbHelper;

    public static final String DB_TABLE_CHECKOPTION = "CheckOption";
    public static final String ID = "_ID";
    public static final String CHECK_OPTION_COUNT = "CheckOptionCount";
    public static final String CHECK_OPTION_EXPIRATION = "Expiration";

    public CheckOptionBaseService(Context context) {
        dbHelper = DatabaseHelper.getInstance(NMBSApplication.getInstance().getApplicationContext());
        sqLiteDatabase = dbHelper.getWritableDatabase();
    }
    public boolean insertCheckOption(CheckOption checkOption) {
        if (checkOption != null) {
            LogUtils.d("CheckOptionBaseService", "insertCheckOption....");
            deleteCheckOption();
            ContentValues contentValues = new ContentValues();
            if (sqLiteDatabase != null) {
                sqLiteDatabase.beginTransaction();
                try {
                    contentValues.put(CHECK_OPTION_COUNT, checkOption.getCount());
                    contentValues.put(CHECK_OPTION_EXPIRATION, DateUtils.dateTimeToString(checkOption.getExpiration()));

                    sqLiteDatabase.insert(DB_TABLE_CHECKOPTION, ID, contentValues);
                    sqLiteDatabase.setTransactionSuccessful();
                    LogUtils.d("CheckOptionBaseService", "insertCheckOption---finished----->.");
                    return true;
                } catch (Exception e) {
                    return false;
                } finally {
                    sqLiteDatabase.endTransaction();
                    return false;
                }

            } else {
                //Log.e(TAG, "DossierSummary is not inserted.");
                return false;
            }
        }
        return false;
    }

    public CheckOption getCheckOption() {

        CheckOption checkOption = null;
        if(sqLiteDatabase == null){
            sqLiteDatabase = dbHelper.getWritableDatabase();
        }
        if(sqLiteDatabase == null){
            return checkOption;
        }
        Cursor cursor = sqLiteDatabase.query(DB_TABLE_CHECKOPTION, null, null, null, null, null, null);
        int cursorNum = cursor.getCount();
        if(cursorNum > 0){
            LogUtils.d("CheckOptionBaseService", "getCheckOption....");
            cursor.moveToPosition(0);
            int count = cursor.getInt(cursor.getColumnIndexOrThrow(CHECK_OPTION_COUNT));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(CHECK_OPTION_EXPIRATION));

            checkOption = new CheckOption(count, DateUtils.stringToDateTime(date));
        }

        cursor.close();
        return checkOption;
    }

    public boolean deleteCheckOption() {
        int isDelete;
        if(sqLiteDatabase == null){
            sqLiteDatabase = dbHelper.getWritableDatabase();
        }
        if(sqLiteDatabase == null){
            return false;
        }
        isDelete = sqLiteDatabase.delete(DB_TABLE_CHECKOPTION, null, null);
        if (isDelete > 0) {
            LogUtils.d("CheckOptionBaseService", "deleteCheckOption....");
            return true;
        } else {
            return false;
        }
    }
}
