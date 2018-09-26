package com.nmbs.dataaccess.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sqlcipher.database.SQLiteDatabase;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;


import com.nmbs.model.MobileMessage;
import com.nmbs.util.HTTPRestServiceCaller;

public class MessageDatabaseService {
	private SQLiteDatabase sqLiteDatabase;
	private DatabaseHelper dbHelper;
	
	// Database fields
	public static final String DB_TABLE_MESSAGESV5 = "MessagesV5";
	public static final String ID = "_ID";
	public static final String MESSAGE_ID = "_MessageID";
	public static final String MESSAGE_TITLE = "Title";
	public static final String MESSAGE_DESCRIPTION = "Description";
	public static final String MESSAGE_VALIDITY = "Validity";
	public static final String MESSAGE_LOWRESICON = "LowResIcon";
	public static final String MESSAGE_HIGHRESICON = "HighResIcon";
	public static final String MESSAGE_LOWRESIMAGE = "LowResImage";
	public static final String MESSAGE_HIGHRESIMAGE = "HighResImage";
	public static final String MESSAGE_TYPE = "MessageType";
	public static final String MESSAGE_INCLUDEACTIONBUTTON = "IncludeActionButton";
	public static final String MESSAGE_ACTIONBUTTONTEXT = "ActionButtonText";
	public static final String MESSAGE_HYPERLINK = "Hyperlink";
	public static final String MESSAGE_DISPLAYINOVERLAY = "DisplayInOverlay";
	public static final String MESSAGE_REPEATDISPLAY = "RepeatDisplayInOverlay";
	public static final String MESSAGE_NEXTDISPLAY = "NextDisplayInOverlay";
	public static final String MESSAGE_NAVIGATION = "NavigationInNormalWebView";
	
	private static final String TAG = MessageDatabaseService.class.getSimpleName();
	public MessageDatabaseService(Context context) {
		dbHelper = DatabaseHelper.getInstance(context);
		sqLiteDatabase = dbHelper.getWritableDatabase();
	}
	
	/**
	 * Insert data to table.
	 *
	 * @param mobileMessage
	 *
	 * @return true means everything is OK, otherwise means failure
	 */
	public boolean insertOrder(MobileMessage mobileMessage) {
		if (mobileMessage != null) {
			ContentValues contentValues = new ContentValues();
			try {
				Log.e(TAG, "insert message..." + mobileMessage.isNavigationInNormalWebView());
				contentValues.put(MESSAGE_ID, mobileMessage.getId());
				contentValues.put(MESSAGE_TITLE, mobileMessage.getTitle());
				contentValues.put(MESSAGE_DESCRIPTION, mobileMessage.getDescription());
				contentValues.put(MESSAGE_VALIDITY, mobileMessage.getValidity());
				contentValues.put(MESSAGE_LOWRESICON, mobileMessage.getLowResIcon());
				contentValues.put(MESSAGE_HIGHRESICON, mobileMessage.getHighResIcon());
				contentValues.put(MESSAGE_LOWRESIMAGE, mobileMessage.getLowResImage());
				contentValues.put(MESSAGE_HIGHRESIMAGE,	mobileMessage.getHighResImage());
				contentValues.put(MESSAGE_TYPE, mobileMessage.getMessageType());
				contentValues.put(MESSAGE_INCLUDEACTIONBUTTON, String.valueOf(mobileMessage.isIncludeActionButton()));
				contentValues.put(MESSAGE_ACTIONBUTTONTEXT, mobileMessage.getActionButtonText());
				contentValues.put(MESSAGE_HYPERLINK, mobileMessage.getHyperlink());
				contentValues.put(MESSAGE_DISPLAYINOVERLAY, String.valueOf(mobileMessage.isDisplayInOverlay()));
				contentValues.put(MESSAGE_REPEATDISPLAY, mobileMessage.getRepeatDisplayInOverlay());
				contentValues.put(MESSAGE_NEXTDISPLAY, mobileMessage.getNextDisplay());
                contentValues.put(MESSAGE_REPEATDISPLAY, mobileMessage.getRepeatDisplayInOverlay());
                contentValues.put(MESSAGE_NAVIGATION, String.valueOf(mobileMessage.isNavigationInNormalWebView()));
				//Log.d(TAG, "MESSAGE_POPUP_DESC...." + mobileMessage.getPopupDescription());
				sqLiteDatabase.insert(DB_TABLE_MESSAGESV5, ID, contentValues);

				// Log.d(tag, "insertOrder....");
			} catch(Exception e){
				e.printStackTrace();
			}
			return true;
		} else {
			// Log.d(tag, "There is no data was inserted.");
			return false;
		}
	}
	
	public boolean insertMessageList(List<MobileMessage> messageList){
		sqLiteDatabase.beginTransaction();
		boolean flag = false;
		try {
			sqLiteDatabase.delete(DB_TABLE_MESSAGESV5, null, null);
			for(MobileMessage message : messageList){

				flag = insertOrder(message);
				Log.e(TAG, "insert is succeed????" + flag);
				if(!flag){
					return flag;
				}

			}
			sqLiteDatabase.setTransactionSuccessful();
		} catch (Exception e) {
			//sqLiteDatabase.endTransaction();
			e.printStackTrace();
		} finally {
			sqLiteDatabase.endTransaction();
		}
		return flag;
	}
	
	public void startTransacstion(){
		sqLiteDatabase.beginTransaction();
	}
	
	public void endTransaction(){
		sqLiteDatabase.setTransactionSuccessful();
		sqLiteDatabase.endTransaction();
	}
	
	public List<MobileMessage> readMessageList() {
		List<MobileMessage> messageList = new ArrayList<MobileMessage>();
		Cursor cursor = sqLiteDatabase.query(DB_TABLE_MESSAGESV5, null, null, null, null, null, null);
		//Log.d(tag, "Select all data.");
		int cursorNum = cursor.getCount();
		for (int i = 0; i < cursorNum; i++) {
			cursor.moveToPosition(i);
			String id = cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE_ID));
			String title = cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE_TITLE));

			String description = cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE_DESCRIPTION));
			String validity = cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE_VALIDITY));
			String lowResIcon = cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE_LOWRESICON));
			String highResIcon = cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE_HIGHRESICON));
			String lowResImage = cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE_LOWRESIMAGE));
			String highResImage = cursor.getString(cursor.getColumnIndex(MESSAGE_HIGHRESIMAGE));
			String messageType = cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE_TYPE));
			boolean includeActionButton = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE_INCLUDEACTIONBUTTON)));
			String actionButtonText = cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE_ACTIONBUTTONTEXT));
			String hyperLink = cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE_HYPERLINK));
			boolean displayInOverlay = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE_DISPLAYINOVERLAY)));
			int repeatDisplay = cursor.getInt(cursor.getColumnIndexOrThrow(MESSAGE_REPEATDISPLAY));

            boolean navigation = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE_NAVIGATION)));
			String messageNextDisplay = cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE_NEXTDISPLAY));
			MobileMessage mobileMessage = new MobileMessage(id, title, description, validity, lowResIcon, highResIcon,
					lowResImage, highResImage, messageType, includeActionButton, actionButtonText, hyperLink, displayInOverlay, repeatDisplay, navigation);
			mobileMessage.setNextDisplay(messageNextDisplay);
			//Log.e(TAG, "navigation..." + cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE_NAVIGATION)));
			//Log.e(TAG, "navigation..." + navigation);
			messageList.add(mobileMessage);
		}
		cursor.close();

		return messageList;
	}

	
	public Date getDate(Cursor cursor, int columnIndex){
		Long date = null;
		if (cursor.getString(columnIndex) != null)
			date = cursor.getLong(columnIndex);
		return date == null ? null : new Date(date);
	}

	public void deleteMessate(String messageId) {
		sqLiteDatabase.delete(DB_TABLE_MESSAGESV5, MESSAGE_ID + "='"
				+ messageId + "'", null);
	}

	public boolean updateMessageNextDisplay(String messageId, String messageNextDisplay) {

		ContentValues contentValues = new ContentValues();
		sqLiteDatabase.beginTransaction();
		try {
			contentValues.put(MESSAGE_NEXTDISPLAY, messageNextDisplay);

			sqLiteDatabase.update(DB_TABLE_MESSAGESV5, contentValues, MESSAGE_ID + " = '" + messageId + "'", null);

			// Log.d(tag, "Insert data to TABLE= "+DB_TABLE_STATION);
			sqLiteDatabase.setTransactionSuccessful();
		}catch (Exception e){
			return false;
		}finally {
			sqLiteDatabase.endTransaction();
		}
		return true;
	}

	public boolean deleteMessages() {
		int isDelete;
		// sqLiteDatabase.execSQL("delete from Orders where DNR = '+"
		// EXYFDTY'");
		isDelete = sqLiteDatabase.delete(DB_TABLE_MESSAGESV5, null, null);
		// Log.d(tag, "Delete all data in " + DB_TABLE_ORDERS);
		if (isDelete > 0) {
			return true;
		} else {
			return false;
		}
	}
}
