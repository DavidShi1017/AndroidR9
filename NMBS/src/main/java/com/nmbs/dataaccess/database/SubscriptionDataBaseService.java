package com.nmbs.dataaccess.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.nmbs.log.LogUtils;
import com.nmbs.model.Subscription;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Richard on 4/6/16.
 */
public class SubscriptionDataBaseService {
    private SQLiteDatabase sqLiteDatabase;
    private DatabaseHelper dbHelper;

    // Database fields
    public static final String DB_TABLE_SUBSCRIPTION = "subscription_table";
    public static final String SUBSCRIPTION_ID = "subscription_id";
    public static final String SUBSCRIPTION_RECONCTX = "subscription_reconCtx";
    public static final String SUBSCRIPTION_RECONCTX_HASH_CODE = "subscription_reconCtx_hash_code";
    public static final String SUBSCRIPTION_ORIGINSTATIONRCODE = "subscription_OriginStationRcode";
    public static final String SUBSCRIPTION_DESTINATIONSTATIONRCODE = "subscription_DestinationStationRcode";
    public static final String SUBSCRIPTION_DEPARTURE = "subscription_Departure";
    public static final String SUBSCRIPTION_ORIGIN_NAME = "subscription_origin_name";
    public static final String SUBSCRIPTION_DESNAME = "subscription_des_name";
    public static final String SUBSCRIPTION_dnr = "subscription_Departure_dnr";
    public static final String SUBSCRIPTION_Connection_id = "subscription_Connection_id";

    public SubscriptionDataBaseService(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
        sqLiteDatabase = dbHelper.getWritableDatabase();
    }

    /**
     * Insert data to table.
     *
     *            order
     * @return true means everything is OK, otherwise means failure
     */
    public boolean insertSubscription(Subscription subscription) {
        sqLiteDatabase.beginTransaction();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SUBSCRIPTION_ID, subscription.getSubscriptionId());
        contentValues.put(SUBSCRIPTION_RECONCTX, subscription.getReconCtx());
        contentValues.put(SUBSCRIPTION_ORIGINSTATIONRCODE, subscription.getOriginStationRcode());
        contentValues.put(SUBSCRIPTION_DESTINATIONSTATIONRCODE, subscription.getDestinationStationRcode());
        contentValues.put(SUBSCRIPTION_DEPARTURE, subscription.getDeparture());
        contentValues.put(SUBSCRIPTION_RECONCTX_HASH_CODE, subscription.getOriginStationRcodeHashCocde());
        contentValues.put(SUBSCRIPTION_ORIGIN_NAME, subscription.getOriginStationName());
        contentValues.put(SUBSCRIPTION_DESNAME, subscription.getDestinationStationName());
        contentValues.put(SUBSCRIPTION_dnr, subscription.getDnr());
        contentValues.put(SUBSCRIPTION_Connection_id, subscription.getConnectionId());

        try {
            sqLiteDatabase.insert(DB_TABLE_SUBSCRIPTION, subscription.getSubscriptionId(),
                    contentValues);
            sqLiteDatabase.setTransactionSuccessful();
            LogUtils.d("insertSubscription", "insert Subscription Successful...");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;

        }finally{
            sqLiteDatabase.endTransaction();
        }

    }

    public boolean deleteSubscription(String subscriptionId) {
        LogUtils.e("deleteSubscription", "deleteSubscription....");
        sqLiteDatabase.beginTransaction();
        try {
            sqLiteDatabase.delete(DB_TABLE_SUBSCRIPTION, SUBSCRIPTION_ID + "=?", new String[]{subscriptionId});
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

    public List<Subscription> readSubscriptions() {
        List<Subscription> subscriptions = new ArrayList<Subscription>();
        Subscription subscription = null;
        Cursor cursor = sqLiteDatabase.query(DB_TABLE_SUBSCRIPTION, null, null, null, null, null, SUBSCRIPTION_DEPARTURE + " asc");
        int cursorNum = cursor.getCount();
        for (int i = 0; i < cursorNum; i++) {
            cursor.moveToPosition(i);
            String id = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_ID));
            String ctx = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_RECONCTX));
            String originCode = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_ORIGINSTATIONRCODE));
            String desCode = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_DESTINATIONSTATIONRCODE));
            String departureDate = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_DEPARTURE));
            String originName = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_ORIGIN_NAME));
            String desName = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_DESNAME));
            String dnr = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_dnr));
            String connectionId = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_Connection_id));
            subscription = new Subscription(id,ctx,originCode,desCode,departureDate,originName,desName,dnr, connectionId);
            subscriptions.add(subscription);
            LogUtils.d("readSubscription", "reconctx..." + ctx);
            LogUtils.d("readSubscription", "date..." + departureDate);
        }
        LogUtils.d("readSubscription", "subscriptions size is..." + subscriptions.size());
        cursor.close();
        return subscriptions;
    }

    public boolean readSubscriptionByReconctx(String reconctx) {
        List<Subscription> subscriptions = new ArrayList<Subscription>();
        Subscription subscription = null;
        Cursor cursor = sqLiteDatabase.query(DB_TABLE_SUBSCRIPTION, null, SUBSCRIPTION_RECONCTX_HASH_CODE+"='"+reconctx+"'", null, null, null, null);
        int cursorNum = cursor.getCount();
        for (int i = 0; i < cursorNum; i++) {
            cursor.moveToPosition(i);
            String id = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_ID));
            String ctx = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_RECONCTX));
            String originCode = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_ORIGINSTATIONRCODE));
            String desCode = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_DESTINATIONSTATIONRCODE));
            String departureDate = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_DEPARTURE));
            String originName = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_ORIGIN_NAME));
            String desName = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_DESNAME));
            String dnr = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_dnr));
            String connectionId = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_Connection_id));
            subscription = new Subscription(id,ctx,originCode,desCode,departureDate,originName,desName,dnr, connectionId);
            subscriptions.add(subscription);

        }
        cursor.close();
        if(subscriptions.size()>0){
            return true;
        }else{
            return false;
        }
    }

    public boolean isDossierPushEnabled(String dossierId) {
        List<Subscription> subscriptions = new ArrayList<Subscription>();
        Subscription subscription = null;
        Cursor cursor = sqLiteDatabase.query(DB_TABLE_SUBSCRIPTION, null, SUBSCRIPTION_dnr+"='"+dossierId+"'", null, null, null, null);
        int cursorNum = cursor.getCount();
        for (int i = 0; i < cursorNum; i++) {
            cursor.moveToPosition(i);
            String id = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_ID));
            String ctx = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_RECONCTX));
            String originCode = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_ORIGINSTATIONRCODE));
            String desCode = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_DESTINATIONSTATIONRCODE));
            String departureDate = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_DEPARTURE));
            String originName = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_ORIGIN_NAME));
            String desName = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_DESNAME));
            String dnr = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_dnr));
            String connectionId = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_Connection_id));
            subscription = new Subscription(id,ctx,originCode,desCode,departureDate,originName,desName,dnr, connectionId);
            subscriptions.add(subscription);

        }
        cursor.close();
        if(subscriptions.size()>0){
            return true;
        }else{
            return false;
        }
    }


    public List<Subscription> readSubscriptionByDnr(String dossierId) {
        List<Subscription> subscriptions = new ArrayList<Subscription>();
        Subscription subscription = null;
        Cursor cursor = sqLiteDatabase.query(DB_TABLE_SUBSCRIPTION, null, SUBSCRIPTION_dnr+"='"+dossierId+"'", null, null, null, null);
        int cursorNum = cursor.getCount();
        for (int i = 0; i < cursorNum; i++) {
            cursor.moveToPosition(i);
            String id = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_ID));
            String ctx = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_RECONCTX));
            String originCode = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_ORIGINSTATIONRCODE));
            String desCode = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_DESTINATIONSTATIONRCODE));
            String departureDate = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_DEPARTURE));
            String originName = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_ORIGIN_NAME));
            String desName = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_DESNAME));
            String dnr = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_dnr));
            String connectionId = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_Connection_id));
            subscription = new Subscription(id,ctx,originCode,desCode,departureDate,originName,desName,dnr, connectionId);
            subscriptions.add(subscription);

        }
        cursor.close();
        return subscriptions;
    }

    public boolean readSubscriptionByReconctxAndDate(String reconctx, String date) {
        List<Subscription> subscriptions = new ArrayList<Subscription>();
        Subscription subscription = null;
        Cursor cursor = sqLiteDatabase.query(DB_TABLE_SUBSCRIPTION, null,
                SUBSCRIPTION_RECONCTX_HASH_CODE + "='" + reconctx +"' and " + SUBSCRIPTION_DEPARTURE + "='" + date + "'",
                null, null, null, null);
        int cursorNum = cursor.getCount();
        for (int i = 0; i < cursorNum; i++) {
            cursor.moveToPosition(i);
            String id = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_ID));
            String ctx = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_RECONCTX));
            String originCode = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_ORIGINSTATIONRCODE));
            String desCode = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_DESTINATIONSTATIONRCODE));
            String departureDate = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_DEPARTURE));
            String originName = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_ORIGIN_NAME));
            String desName = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_DESNAME));
            String dnr = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_dnr));
            String connectionId = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_Connection_id));
            subscription = new Subscription(id,ctx,originCode,desCode,departureDate,originName,desName,dnr, connectionId);
            subscriptions.add(subscription);
        }
        cursor.close();
        LogUtils.e("subscriptions", "subscriptions size is==" + subscriptions.size());
        if(subscriptions.size() > 0){
            return true;
        }else{
            return false;
        }
    }

    public boolean readSubscriptionByCodeAndDate(String originStationRcode, String destinationStationRcode, String date) {
        List<Subscription> subscriptions = new ArrayList<Subscription>();
        Subscription subscription = null;
        Cursor cursor = sqLiteDatabase.query(DB_TABLE_SUBSCRIPTION, null,
                SUBSCRIPTION_ORIGINSTATIONRCODE + "='" + originStationRcode +"' and " + SUBSCRIPTION_DESTINATIONSTATIONRCODE + "='"+ destinationStationRcode +"' and "+ SUBSCRIPTION_DEPARTURE + "='" + date + "'",
                null, null, null, null);
        int cursorNum = cursor.getCount();
        for (int i = 0; i < cursorNum; i++) {
            cursor.moveToPosition(i);
            String id = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_ID));
            String ctx = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_RECONCTX));
            String originCode = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_ORIGINSTATIONRCODE));
            String desCode = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_DESTINATIONSTATIONRCODE));
            String departureDate = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_DEPARTURE));
            String originName = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_ORIGIN_NAME));
            String desName = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_DESNAME));
            String dnr = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_dnr));
            String connectionId = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_Connection_id));
            subscription = new Subscription(id,ctx,originCode,desCode,departureDate,originName,desName,dnr, connectionId);
            subscriptions.add(subscription);
        }
        cursor.close();
        LogUtils.e("subscriptions", "subscriptions size is==" + subscriptions.size());
        if(subscriptions.size() > 0){
            return true;
        }else{
            return false;
        }
    }

    public Subscription readSubscriptionByReconctxAndDepartureDate(String reconctx, String date) {
        LogUtils.d("cursorNum", "reconctx..." + reconctx);
        LogUtils.d("cursorNum", "date..." + date);
        Subscription subscription = null;
        Cursor cursor = sqLiteDatabase.query(DB_TABLE_SUBSCRIPTION, null,
                SUBSCRIPTION_RECONCTX_HASH_CODE + "='" + reconctx +"' and " + SUBSCRIPTION_DEPARTURE + "='" + date + "'",
                null, null, null, null);
        int cursorNum = cursor.getCount();
        LogUtils.d("cursorNum", "cursorNum..." + cursorNum);
        if(cursorNum > 0){
            cursor.moveToPosition(0);
            String id = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_ID));
            String ctx = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_RECONCTX));
            String originCode = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_ORIGINSTATIONRCODE));
            String desCode = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_DESTINATIONSTATIONRCODE));
            String departureDate = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_DEPARTURE));
            String originName = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_ORIGIN_NAME));
            String desName = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_DESNAME));
            String dnr = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_dnr));
            String connectionId = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_Connection_id));
            subscription = new Subscription(id,ctx,originCode,desCode,departureDate,originName,desName,dnr, connectionId);
        }
        cursor.close();
        return subscription;
    }

    public Subscription readSubscriptionByConnectionId(String conId) {
        LogUtils.d("cursorNum", "connectionId..." + conId);

        Subscription subscription = null;
        Cursor cursor = sqLiteDatabase.query(DB_TABLE_SUBSCRIPTION, null,
                SUBSCRIPTION_Connection_id + "='" + conId + "'",
                null, null, null, null);
        int cursorNum = cursor.getCount();
        LogUtils.d("cursorNum", "cursorNum..." + cursorNum);
        if(cursorNum > 0){
            cursor.moveToPosition(0);
            String id = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_ID));
            String ctx = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_RECONCTX));
            String originCode = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_ORIGINSTATIONRCODE));
            String desCode = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_DESTINATIONSTATIONRCODE));
            String departureDate = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_DEPARTURE));
            String originName = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_ORIGIN_NAME));
            String desName = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_DESNAME));
            String dnr = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_dnr));
            String connectionId = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_Connection_id));
            subscription = new Subscription(id,ctx,originCode,desCode,departureDate,originName,desName,dnr, connectionId);
        }
        cursor.close();
        return subscription;
    }

    public boolean isLinkedWithDnr(String dnr) {

        Cursor cursor = sqLiteDatabase.query(DB_TABLE_SUBSCRIPTION, null,
                SUBSCRIPTION_dnr + "='" + dnr + "'",
                null, null, null, null);
        if(cursor.getCount() > 0){
            return true;
        }
        cursor.close();
        return false;
    }

    public boolean clearSubscriptionDnrById(String subscriptionId) {
        sqLiteDatabase.beginTransaction();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SUBSCRIPTION_dnr, "");
        try {
            sqLiteDatabase.update(DB_TABLE_SUBSCRIPTION, contentValues,
                    SUBSCRIPTION_ID+"='"+subscriptionId+"'",null);
            sqLiteDatabase.setTransactionSuccessful();
            LogUtils.d("updateSubscription", "update Subscription Successful...");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally{
            sqLiteDatabase.endTransaction();
        }
    }
    public boolean deleteSubscriptionByDnr(String dnr) {
        LogUtils.e("deleteSubscription", "deleteSubscription....");
        sqLiteDatabase.beginTransaction();
        try {
            sqLiteDatabase.delete(DB_TABLE_SUBSCRIPTION, SUBSCRIPTION_dnr + "=?", new String[]{dnr});
            sqLiteDatabase.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;

        }finally{
            sqLiteDatabase.endTransaction();
        }

    }
    public Subscription readSubscriptionById(String subscriptionId) {
        Subscription subscription = null;
        Cursor cursor = sqLiteDatabase.query(DB_TABLE_SUBSCRIPTION, null, SUBSCRIPTION_ID+"='"+subscriptionId+"'", null, null, null, null);
        int cursorNum = cursor.getCount();
        for (int i = 0; i < cursorNum; i++) {
            cursor.moveToPosition(i);
            String id = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_ID));
            String ctx = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_RECONCTX));
            String originCode = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_ORIGINSTATIONRCODE));
            String desCode = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_DESTINATIONSTATIONRCODE));
            String departureDate = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_DEPARTURE));
            String originName = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_ORIGIN_NAME));
            String desName = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_DESNAME));
            String dnr = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_dnr));
            String connectionId = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_Connection_id));
            subscription = new Subscription(id,ctx,originCode,desCode,departureDate,originName,desName,dnr, connectionId);
        }
        cursor.close();
        return subscription;
    }

    public Subscription readSubscriptionByAll(Subscription sub) {
        //Log.d("cursorNum", "reconctx..." + reconctx);
        //Log.d("cursorNum", "date..." + date);
        if(sub == null){
            return null;
        }
        Subscription subscription = null;
        Cursor cursor = sqLiteDatabase.query(DB_TABLE_SUBSCRIPTION, null,
                SUBSCRIPTION_DEPARTURE + "='" + sub.getDeparture() + "' " +
                        "and " + SUBSCRIPTION_ID + "='" + sub.getSubscriptionId() + "' and " + SUBSCRIPTION_ORIGINSTATIONRCODE + "='" + sub.getOriginStationRcode() + "' " +
                        "and " + SUBSCRIPTION_DESTINATIONSTATIONRCODE + "='" + sub.getDestinationStationRcode() + "' " +
                        "and " + SUBSCRIPTION_dnr + "='" + sub.getDnr() + "'",
                null, null, null, null);
        int cursorNum = cursor.getCount();
        LogUtils.d("cursorNum", "cursorNum..." + cursorNum);
        if(cursorNum > 0){
            cursor.moveToPosition(0);
            String id = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_ID));
            String ctx = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_RECONCTX));
            String originCode = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_ORIGINSTATIONRCODE));
            String desCode = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_DESTINATIONSTATIONRCODE));
            String departureDate = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_DEPARTURE));
            String originName = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_ORIGIN_NAME));
            String desName = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_DESNAME));
            String dnr = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_dnr));
            String connectionId = cursor.getString(cursor.getColumnIndexOrThrow(SUBSCRIPTION_Connection_id));
            subscription = new Subscription(id,ctx,originCode,desCode,departureDate,originName,desName,dnr, connectionId);
        }
        cursor.close();
        return subscription;
    }
}
