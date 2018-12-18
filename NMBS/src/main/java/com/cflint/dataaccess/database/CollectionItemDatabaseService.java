package com.cflint.dataaccess.database;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;
import net.sqlcipher.database.SQLiteDatabase;

import com.cflint.R;
import com.cflint.application.NMBSApplication;
import com.cflint.log.LogUtils;
import com.cflint.model.CollectionItem;
import com.cflint.model.PaymentOption;

/**
 * Exposes methods to manage a SQLite database.
 * It is used to manage CollectionItem, it contains:language,title and country.
 * language, title, country : there are same attribute in them.
 */
public class CollectionItemDatabaseService {

	private static final String tag = "CollectionItemDatabaseService";

	// Database fields   
	public static final String DB_TABLE_LANGUAGE = "language";
	public static final String DB_TABLE_TITLE = "title";
	public static final String DB_TABLE_COUNTRY = "country";
	public static final String DB_TABLE_PAYMENTMETHOD = "PaymentMethod";
	public static final String DB_TABLE_REDUCTION_CARD = "reductionCard";

	public static final String COLLECTION_ID = "_id";
	public static final String COLLECTION_KEY = "key";
	public static final String COLLECTION_LABEL = "label";

	private SQLiteDatabase sqLiteDatabase;
	private DatabaseHelper dbHelper;
	private Context context;
	public CollectionItemDatabaseService(Context context) {
		this.context = context;
		dbHelper = DatabaseHelper.getInstance(NMBSApplication.getInstance().getApplicationContext());
		sqLiteDatabase = dbHelper.getWritableDatabase();
	}

	/**
	 * Insert data to DB_TABLE_LANGUAGE, DB_TABLE_TITLE, DB_TABLE_COUNTRY
	 * @param
	 * @param tableName must be DB_TABLE_LANGUAGE or DB_TABLE_TITLE or DB_TABLE_COUNTRY
	 * @return true means everything is OK, otherwise means failure
	 */
	public boolean insertCollectionResponse(List<CollectionItem> languageCollectionItem, String tableName) {
		if (languageCollectionItem != null ) {
			ContentValues contentValues = new ContentValues();
			if(sqLiteDatabase == null){
				sqLiteDatabase = dbHelper.getWritableDatabase();
			}
			if(sqLiteDatabase == null){
				return false;
			}
			sqLiteDatabase.beginTransaction();
			try {
				for (CollectionItem collectionItem : languageCollectionItem) {
					contentValues.put(COLLECTION_KEY, collectionItem.getKey());
					contentValues.put(COLLECTION_LABEL, collectionItem.getLable());

					LogUtils.d(tag, "Insert data to TABLE= "+tableName + "========KEY is=====" + collectionItem.getKey() + "========LableLable is=====" + collectionItem.getLable());
					sqLiteDatabase.insert(tableName, COLLECTION_ID, contentValues);
					//Log.d(tag, "Insert data to TABLE= "+tableName);
				}
				//Log.d(tag, "Insert data to TABLE= "+tableName);
				sqLiteDatabase.setTransactionSuccessful();
			} finally {
				sqLiteDatabase.endTransaction();
			}
			return true;
		}else {
			//Log.d(tag, "There is no data was inserted.");
			return false;
		}
	}


	/**
	 * Select all data from SQLite, add them to ArrayList
	 * @param tableName must be DB_TABLE_LANGUAGE or DB_TABLE_TITLE or DB_TABLE_COUNTRY or DB_TABLE_PAYMENTMETHOD
	 * @return CollectionResponse
	 * @throws SQLException
	 */
	public List<CollectionItem> selectCollectionResponse(String tableName) throws SQLException {
		List<CollectionItem> items = new ArrayList<CollectionItem>();
		Cursor cursor = getCollectionItemsCursor(tableName);
		if(cursor == null){
			return items;
		}
		int cursorNum = cursor.getCount();


		if(sqLiteDatabase == null){
			sqLiteDatabase = dbHelper.getWritableDatabase();
		}
		if(sqLiteDatabase == null){
			return items;
		}
		CollectionItem collectionItem = null;
		if (StringUtils.equalsIgnoreCase(tableName, DB_TABLE_REDUCTION_CARD)) {
			collectionItem = new CollectionItem("", context.getString(R.string.reduction_card_view_none));
			items.add(collectionItem);
		}

		for (int i = 0; i < cursorNum; i++) {
			cursor.moveToPosition(i);
			String key = cursor.getString(cursor.getColumnIndexOrThrow(COLLECTION_KEY));
			String label = cursor.getString(cursor.getColumnIndexOrThrow(COLLECTION_LABEL));

			collectionItem = new CollectionItem(key, label);
			items.add(collectionItem);
		}
		cursor.close();

		return items;
	}

	/**
	 * Select data from SQLite, add them to ArrayList
	 * @param tableName must be DB_TABLE_LANGUAGE or DB_TABLE_TITLE or DB_TABLE_COUNTRY or DB_TABLE_PAYMENTMETHOD
	 * @return CollectionResponse
	 * @throws SQLException
	 */
	public List<CollectionItem> selectCollectionResponseByKey(String tableName, List<String> keyList) throws SQLException {
		List<CollectionItem> items = new ArrayList<CollectionItem>();
		CollectionItem collectionItem = null;
		if(sqLiteDatabase == null){
			sqLiteDatabase = dbHelper.getWritableDatabase();
		}
		if(sqLiteDatabase == null){
			return items;
		}
		if(keyList != null){
			for(int j = 0; j < keyList.size(); j++){
				String key = keyList.get(j);
				Cursor cursor = sqLiteDatabase.query(tableName
						, new String[] { COLLECTION_ID, COLLECTION_KEY, COLLECTION_LABEL  }
						, COLLECTION_KEY + "='" + key +"'", null, null, null, COLLECTION_LABEL);

				int cursorNum = cursor.getCount();

				for (int i = 0; i < cursorNum; i++) {
					cursor.moveToPosition(i);
					String CollectionKey = cursor.getString(cursor.getColumnIndexOrThrow(COLLECTION_KEY));
					String label = cursor.getString(cursor.getColumnIndexOrThrow(COLLECTION_LABEL));

					collectionItem = new CollectionItem(CollectionKey, label);
					items.add(collectionItem);
				}
				cursor.close();
			}
		}

		return items;
	}
	/**
	 * Select all data from SQLite, add them to ArrayList
	 * @param tableName must be DB_TABLE_PAYMENTMETHOD
	 * @return CollectionResponse
	 * @throws SQLException
	 */
	public List<CollectionItem> selectCollectionResponseByMethodOfPaymentOptions(String tableName, List<PaymentOption> paymentOptions) throws SQLException {

		int paymentOptionCount = 0;
		List<CollectionItem> items = new ArrayList<CollectionItem>();
		CollectionItem collectionItem = null;
		if(sqLiteDatabase == null){
			sqLiteDatabase = dbHelper.getWritableDatabase();
		}
		if(sqLiteDatabase == null){
			return items;
		}
		if(paymentOptions != null){
			paymentOptionCount = paymentOptions.size();
		}
		for(int j = 0; j < paymentOptionCount; j++){
			Cursor cursor = sqLiteDatabase.query(tableName
					, new String[] { COLLECTION_ID, COLLECTION_KEY, COLLECTION_LABEL  }
					, COLLECTION_KEY + " = '" + paymentOptions.get(j).getMethod() + "'", null, null, null, COLLECTION_LABEL);
			int cursorNum = cursor.getCount();

			for (int i = 0; i < cursorNum; i++) {
				cursor.moveToPosition(i);
				String key = cursor.getString(cursor.getColumnIndexOrThrow(COLLECTION_KEY));
				String label = cursor.getString(cursor.getColumnIndexOrThrow(COLLECTION_LABEL));
				if (StringUtils.equalsIgnoreCase("", label)) {
					label = key;
				}
				collectionItem = new CollectionItem(key, label);
				items.add(collectionItem);
			}
			cursor.close();
		}

		return items;
	}

	/**
	 * Get the cursor of CollectionItem
	 * @param tableName must be DB_TABLE_LANGUAGE or DB_TABLE_TITLE or DB_TABLE_COUNTRY
	 * @return Cursor
	 * @throws SQLException
	 */
	public Cursor getCollectionItemsCursor(String tableName) throws SQLException {
		if(sqLiteDatabase == null){
			sqLiteDatabase = dbHelper.getWritableDatabase();
		}
		if(sqLiteDatabase == null){
			return null;
		}
		Cursor cursor = sqLiteDatabase.query(tableName
				, new String[] { COLLECTION_ID, COLLECTION_KEY, COLLECTION_LABEL  }
				, null, null, null, null, COLLECTION_LABEL);
		return cursor;
	}

	/**
	 * Delete all data by different table name
	 * @param tableName
	 * @return true means everything is OK, otherwise means failure
	 */
	public boolean deleteMasterData(String tableName) {
		int isDelete;
		if(sqLiteDatabase == null){
			sqLiteDatabase = dbHelper.getWritableDatabase();
		}
		if(sqLiteDatabase == null){
			return false;
		}
		isDelete = sqLiteDatabase.delete(tableName, null, null) ;
		//Log.d(tag, "Delete all data in " + tableName);
		if(isDelete > 0){
			return true;
		}else{
			return false;
		}
	}
}
