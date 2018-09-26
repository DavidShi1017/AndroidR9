package com.nmbs.dataaccess.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;


public class OfferQueryDataBaseService {

	private SQLiteDatabase sqLiteDatabase;
	private DatabaseHelper dbHelper;
	// private static final String tag =
	// AssistantDatabaseService.class.getSimpleName();

	// Database fields

	public static final String DB_TABLE_OFFERQUERY = "OfferQuery";
	public static final String OFFERQUERY_ID = "_OfferQueryID";
	public static final String OFFERQUERY_CONTENT = "OfferQueryContent";


	public OfferQueryDataBaseService(Context context) {
		dbHelper = DatabaseHelper.getInstance(context);
		sqLiteDatabase = dbHelper.getWritableDatabase();
	}

	/**
	 * Insert data to table.
	 * 
	 * @param OfferQueryId
	 *            pdfID
	 * @param OfferQueryContent
	 *            pdfContent
	 * @return true means everything is OK, otherwise means failure
	 */
	public boolean insertOfferQuery(String OfferQueryId, String OfferQueryContent) {
		if (!OfferQueryId.equals("") && OfferQueryContent != null && OfferQueryContent !=null) {
			if(isExistOfferQuery(OfferQueryId))
				deleteLastQueryById(OfferQueryId);
			ContentValues contentValues = new ContentValues();
			sqLiteDatabase.beginTransaction();
			try {
				contentValues.put(OFFERQUERY_ID, OfferQueryId);
				contentValues.put(OFFERQUERY_CONTENT, OfferQueryContent);
				sqLiteDatabase.insert(DB_TABLE_OFFERQUERY, OFFERQUERY_ID, contentValues);


				sqLiteDatabase.setTransactionSuccessful();
			} finally {
				sqLiteDatabase.endTransaction();
			}
		} else {
			return false;
		}
		return true;
	}

	public String readOfferQuery(String OfferQueryId) {
	
		String OfferQueryContent = null;
		Cursor cursor = sqLiteDatabase.query(DB_TABLE_OFFERQUERY, new String[] {
				OFFERQUERY_ID, OFFERQUERY_CONTENT, }, OFFERQUERY_ID + " = '"
				+ OfferQueryId + "'", null, null, null, null);
		if (cursor.getCount() == 0) {
			cursor.close();
		}else {			
			cursor.moveToFirst();	
			OfferQueryContent = cursor.getString(cursor
					.getColumnIndexOrThrow(OFFERQUERY_CONTENT));
			cursor.close();
		}
		
		return OfferQueryContent;
	}
	
	
	public boolean isExistOfferQuery(String OfferQueryId) {
		Cursor cursor = sqLiteDatabase.query(DB_TABLE_OFFERQUERY, new String[] {
				OFFERQUERY_ID, OFFERQUERY_CONTENT, }, OFFERQUERY_ID + " = '"
				+ OfferQueryId + "'", null, null, null, null);

		int cursorNum = cursor.getCount();
		cursor.close();
		if (cursorNum > 0)
			return true;
		else
			return false;
	}

	public void deleteLastQueryById(String OfferQueryId) {
		sqLiteDatabase.delete(DB_TABLE_OFFERQUERY, OFFERQUERY_ID + "='"
				+ OfferQueryId + "'", null);
	}

}
