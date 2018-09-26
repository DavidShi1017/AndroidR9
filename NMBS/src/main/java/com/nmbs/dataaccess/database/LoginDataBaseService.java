package com.nmbs.dataaccess.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.nmbs.log.LogUtils;

import com.nmbs.model.LogonInfo;

import com.nmbs.util.DateUtils;

import net.sqlcipher.database.SQLiteDatabase;

public class LoginDataBaseService {

    private SQLiteDatabase sqLiteDatabase;
    private DatabaseHelper dbHelper;

    public static final String DB_TABLE_LOGIN = "Login";
    public static final String ID = "_ID";
    public static final String LOGIN_CUSTOMER_ID = "CustomerId";
    public static final String LOGIN_FIRSTNAME = "FirstName";
    public static final String LOGIN_EMAIL = "Email";
    public static final String LOGIN_CODE = "Code";
    public static final String LOGIN_PHONENUMBER = "PhoneNumber";
    public static final String LOGIN_STATE = "State";
    public static final String LOGIN_STATE_DESCRIPTION = "StateDescription";
    public static final String LOGIN_PERSONID = "PersonId";
    public static final String LOGIN_LAST_UPDATE_TIMESTAMP_PASSWORD = "LastUpdateTimestampPassword";
    public static final String LOGIN_LOGINPROVIDER  = "LoginProvider";

    public LoginDataBaseService(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
        sqLiteDatabase = dbHelper.getWritableDatabase();
    }
    public boolean insertLogonInfo(LogonInfo logonInfo) {
        if (logonInfo != null) {
            LogUtils.d("LoginDataBaseService", "insertLogonInfo....");
            ContentValues contentValues = new ContentValues();
            if (sqLiteDatabase != null) {
                sqLiteDatabase.beginTransaction();
                try {
                    contentValues.put(LOGIN_CUSTOMER_ID, logonInfo.getCustomerId());
                    contentValues.put(LOGIN_FIRSTNAME, logonInfo.getFirstName());
                    contentValues.put(LOGIN_EMAIL, logonInfo.getEmail());
                    contentValues.put(LOGIN_CODE, logonInfo.getCode());
                    contentValues.put(LOGIN_PHONENUMBER, logonInfo.getPhoneNumber());
                    contentValues.put(LOGIN_STATE, logonInfo.getState());
                    contentValues.put(LOGIN_STATE_DESCRIPTION, logonInfo.getStateDescription());
                    contentValues.put(LOGIN_PERSONID, logonInfo.getPersonId());
                    contentValues.put(LOGIN_LAST_UPDATE_TIMESTAMP_PASSWORD, DateUtils.dateTimeToString(logonInfo.getLastUpdateTimestampPassword()));
                    contentValues.put(LOGIN_LOGINPROVIDER, logonInfo.getLoginProvider());
                    sqLiteDatabase.insert(DB_TABLE_LOGIN, ID, contentValues);
                    sqLiteDatabase.setTransactionSuccessful();
                    LogUtils.d("LoginDataBaseService", "insertLogonInfo---finished----->.");
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

    public LogonInfo getLogonInfo() {

        LogonInfo logonInfo = null;
        Cursor cursor = sqLiteDatabase.query(DB_TABLE_LOGIN, null, null, null, null, null, null);
        int cursorNum = cursor.getCount();
        if(cursorNum > 0){
            LogUtils.d("LoginDataBaseService", "getLogonInfo....");
            cursor.moveToPosition(0);
            String customerId = cursor.getString(cursor.getColumnIndexOrThrow(LOGIN_CUSTOMER_ID));
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow(LOGIN_FIRSTNAME));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(LOGIN_EMAIL));
            String code = cursor.getString(cursor.getColumnIndexOrThrow(LOGIN_CODE));
            String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(LOGIN_PHONENUMBER));
            String state = cursor.getString(cursor.getColumnIndexOrThrow(LOGIN_STATE));
            String stateDescription = cursor.getString(cursor.getColumnIndexOrThrow(LOGIN_STATE_DESCRIPTION));
            String personId = cursor.getString(cursor.getColumnIndexOrThrow(LOGIN_PERSONID));
            String lastUpdateTimestampPassword =  cursor.getString(cursor.getColumnIndexOrThrow(LOGIN_LAST_UPDATE_TIMESTAMP_PASSWORD));
            String loginProvider = cursor.getString(cursor.getColumnIndexOrThrow(LOGIN_LOGINPROVIDER));
            logonInfo = new LogonInfo(customerId, firstName, email, code, phoneNumber, state, stateDescription,
                    personId, DateUtils.stringToDateTime(lastUpdateTimestampPassword), loginProvider);
        }

        cursor.close();
        return logonInfo;
    }

    public boolean deleteLogonInfo() {
        int isDelete;
        isDelete = sqLiteDatabase.delete(DB_TABLE_LOGIN, null, null);
        if (isDelete > 0) {
            LogUtils.d("LoginDataBaseService", "deleteLogonInfo....");
            return true;
        } else {
            return false;
        }
    }
}
