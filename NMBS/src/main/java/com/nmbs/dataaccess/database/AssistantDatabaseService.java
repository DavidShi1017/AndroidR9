package com.nmbs.dataaccess.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang.StringUtils;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import android.util.Log;

import com.nmbs.application.NMBSApplication;
import com.nmbs.log.LogUtils;
import com.nmbs.model.OfferQuery.ComforClass;
import com.nmbs.model.Order;
import com.nmbs.model.StationBoardBulk;
import com.nmbs.model.StationBoardBulkResponse;
import com.nmbs.model.SeatLocationForOD.Direction;
import com.nmbs.util.DateUtils;

/**
 * Exposes methods to manage a SQLite database. It is used to manage order.
 */
public class AssistantDatabaseService {

	private SQLiteDatabase sqLiteDatabase;
	private DatabaseHelper dbHelper;
	private static final String tag =AssistantDatabaseService.class.getSimpleName();

	// Database fields
	public static final String DB_TABLE_ORDERS = "Orders";
	public static final String ORDERS_ID = "_OrderID";
	public static final String ORDERS_TRAIN_TYPE = "TrainType";
	public static final String ORDERS_PERSON_NUMBER = "PersonNumber";
	public static final String ORDERS_TRAINNR = "TrainNr";
	public static final String ORDERS_ORDER_STATE = "OrderState";
	public static final String ORDERS_DNR = "DNR";
	public static final String ORDERS_ORIGIN_STATION_NAME = "OriginStationName";
	public static final String ORDERS_ORIGIN_CODE = "OriginCode";
	public static final String ORDERS_DESTINATION_STATION_NAME = "DestinationStationName";
	public static final String ORDERS_DESTINATION_CODE = "DestinationCode";
	public static final String ORDERS_PNR = "PNR";
	public static final String ORDERS_DEPARTURE_DATE = "DepartureDate";
	public static final String DOSSIER_GUID = "DossierGUID";
	public static final String ORDERS_TRAVEL_SEGMENT_ID = "TravelSegmentID";
	public static final String ORDERS_INCLUDES_EBS = "IncludesEBS";
	public static final String ORDERS_TRAVEL_CLASS = "Travelclass";
	public static final String ORDERS_DIRECTION = "Direction";
	public static final String ORDERS_HAS_DEPARTURE_TIME = "hasDepartureTime";
	public static final String ORDERS_SORT_DEPARTURE_TIME = "SortDepartureDate";
	public static final String ORDERS_SORT_FIRST_CHILD_DEPARTURE_TIME = "SortFirstChildDepartureDate";
	public static final String ORDERS_CORRUPTED = "Corrupted";
	public static final String ORDERS_EMAIL = "Email";
	public static final String ORDERS_REFUNDABLE = "Refundable";
	public static final String ORDERS_EXCHANGEABLE = "Exchangeable";
	public static final String ORDERS_RULFILLMENTFAILED = "RulfillmentFailed";
	public static final String ORDERS_HAS_DUPLICATED_STATIONBOARD = "HasDuplicatedStationboard";
	public static final String ORDERS_DUPLICATED_STATIONBOARD_ID = "DuplicatedStationboardId";
	

	public static final String DB_TABLE_PDFS = "PDFs";
	public static final String PDFS_ID = "_PDFsID";
	public static final String PDFS_CONTENT = "PDFsContent";

	public static final String DB_TABLE_BARCODES = "BarCodes";
	public static final String BARCODES_ID = "_BarCodesID";
	public static final String BARCODES_CONTENT = "BarCodesContent";

	public AssistantDatabaseService(Context context) {
		dbHelper = DatabaseHelper.getInstance(NMBSApplication.getInstance().getApplicationContext());
		sqLiteDatabase = dbHelper.getWritableDatabase();
	}

	/**
	 * Insert data to table.
	 * 
	 * @param
	 *            order
	 * @return true means everything is OK, otherwise means failure
	 */
	public boolean insertOrder(Order order) {
		if (order != null) {
			ContentValues contentValues = new ContentValues();
			if(sqLiteDatabase == null){
				sqLiteDatabase = dbHelper.getWritableDatabase();
			}
			if(sqLiteDatabase == null){
				return false;
			}
			sqLiteDatabase.beginTransaction();
			try {
				contentValues.put(ORDERS_TRAIN_TYPE, order.getTrainType());
				contentValues.put(ORDERS_PERSON_NUMBER, order.getPersonNumber());
				contentValues.put(ORDERS_TRAINNR, order.getTrainNr());
				contentValues.put(ORDERS_ORDER_STATE, order.getOrderState());
				contentValues.put(ORDERS_DNR, order.getDNR());
				contentValues.put(ORDERS_ORIGIN_STATION_NAME, order.getOrigin());
				contentValues.put(ORDERS_ORIGIN_CODE, order.getOriginCode());
				contentValues.put(ORDERS_DESTINATION_STATION_NAME,order.getDestination());
				contentValues.put(ORDERS_DESTINATION_CODE,order.getDestinationCode());
				contentValues.put(ORDERS_PNR, order.getPnr());
				contentValues.put(ORDERS_DEPARTURE_DATE, DateUtils.dateTimeToString(order.getDepartureDate()));
				contentValues.put(DOSSIER_GUID, order.getDossierGUID());
				contentValues.put(ORDERS_TRAVEL_SEGMENT_ID, order.getTravelSegmentID());
				contentValues.put(ORDERS_DIRECTION, order.getDirection().ordinal());
				contentValues.put(ORDERS_INCLUDES_EBS, String.valueOf(order.isIncludesEBS()));
				contentValues.put(ORDERS_TRAVEL_CLASS, order.getTravelclass().ordinal());
				contentValues.put(ORDERS_HAS_DEPARTURE_TIME, String.valueOf(order.isHasDepartureTime()));
				contentValues.put(ORDERS_SORT_DEPARTURE_TIME, DateUtils.dateTimeToString(order.getSortDepartureDate()));
				contentValues.put(ORDERS_SORT_FIRST_CHILD_DEPARTURE_TIME, DateUtils.dateTimeToString(order.getSortFirstChildDepartureDate()));
				contentValues.put(ORDERS_CORRUPTED, String.valueOf(order.isCorrupted()));
				contentValues.put(ORDERS_EMAIL, order.getEmail());
				contentValues.put(ORDERS_REFUNDABLE, order.getRefundable());
				contentValues.put(ORDERS_EXCHANGEABLE, order.getExchangeable());
				contentValues.put(ORDERS_RULFILLMENTFAILED, order.getRulfillmentFailed());
				contentValues.put(ORDERS_HAS_DUPLICATED_STATIONBOARD, String.valueOf(order.isHasDuplicatedStationboard()));
				contentValues.put(ORDERS_DUPLICATED_STATIONBOARD_ID, order.getDuplicatedStationboardId());
				contentValues.put(ORDERS_RULFILLMENTFAILED, order.getRulfillmentFailed());
				// Log.d(tag, "insertOrder....");
				sqLiteDatabase.insert(DB_TABLE_ORDERS, ORDERS_ID, contentValues);

				// Log.d(tag, "insertOrder....");

				sqLiteDatabase.setTransactionSuccessful();
			} finally {
				sqLiteDatabase.endTransaction();
			}
			return true;
		} else {
			// Log.d(tag, "There is no data was inserted.");
			return false;
		}
	}

	/**
	 * Insert data to table.
	 * 
	 * @param barCodesId
	 *            pdfID
	 * @param barCodesContent
	 *            pdfContent
	 * @return true means everything is OK, otherwise means failure
	 */
	public boolean insertBarCodes(String barCodesId, byte[] barCodesContent) {

		if (!barCodesId.equals("") && barCodesContent != null
				&& barCodesContent.length > 0) {
			if (isExistBarCodes(barCodesId)) {
				deleteBarcodes(barCodesId);
			}
			ContentValues contentValues = new ContentValues();
			sqLiteDatabase.beginTransaction();
			try {
				contentValues.put(BARCODES_ID, barCodesId);
				contentValues.put(BARCODES_CONTENT, barCodesContent);
				sqLiteDatabase.insert(DB_TABLE_BARCODES, BARCODES_ID,
						contentValues);
				sqLiteDatabase.setTransactionSuccessful();
			} finally {
				sqLiteDatabase.endTransaction();
			}
		} else {
			return false;
		}
		return true;
	}

	/**
	 * Insert data to table.
	 * 
	 * @param pdfId
	 *            pdfID
	 * @param pdfContent
	 *            pdfContent
	 * @return true means everything is OK, otherwise means failure
	 */
	public boolean insertPDFs(String pdfId, byte[] pdfContent) {

		if (!pdfId.equals("") && pdfContent != null && pdfContent.length > 0) {
			if (isExistPDFs(pdfId)) {
				deletePDFs(pdfId);
			}
			ContentValues contentValues = new ContentValues();
			sqLiteDatabase.beginTransaction();
			try {
				contentValues.put(PDFS_ID, pdfId);
				contentValues.put(PDFS_CONTENT, pdfContent);
				LogUtils.d("TAG", "insertOrder...." + pdfId);
				sqLiteDatabase.insert(DB_TABLE_PDFS, PDFS_ID, contentValues);

				// Log.d(tag, "insertOrder....");

				sqLiteDatabase.setTransactionSuccessful();
			} finally {
				sqLiteDatabase.endTransaction();
			}
		} else {
			return false;  
		}
		return true;
	}

	public byte[] readPDFs(String pdfId) {
		byte[] pdfContent = null;
		Cursor cursor = sqLiteDatabase.query(DB_TABLE_PDFS, new String[] {
				PDFS_ID, PDFS_CONTENT, }, PDFS_ID + " = '" + pdfId + "'", null,
				null, null, null);
		
		int cursorNum = cursor.getCount();
		if (cursorNum > 0)
			cursor.moveToFirst();
		else
			return null;
		pdfContent = cursor.getBlob(cursor.getColumnIndexOrThrow(PDFS_CONTENT));
		//ZipInputStream sbs = new ByteArrayInputStream(pdfContent);
		//BufferedInputStream b = new BufferedInputStream(
          //            zipInputStream);


		return pdfContent;
	}
	
	public byte[] readBarCodes(String barCodesId) {
		
		byte[] barCodesContent = null;
		Cursor cursor = sqLiteDatabase.query(DB_TABLE_BARCODES, new String[] {
				BARCODES_ID, BARCODES_CONTENT, }, BARCODES_ID + " = '"
				+ barCodesId + "'", null, null, null, null);

		int cursorNum = cursor.getCount();
		if (cursorNum > 0)
			cursor.moveToFirst();
		else
			return null;
		barCodesContent = cursor.getBlob(cursor
				.getColumnIndexOrThrow(BARCODES_CONTENT));

		return barCodesContent;
	}

	/**
	 * Select all data from SQLite, add them to List.
	 * 
	 * @param flag
	 *            0 means: future order, 1 means: history order, 2 means:
	 *            upcoming order
	 * @return StationResponse
	 * @throws SQLException
	 */
	public List<Order> selectOrdersCollection(int flag,
			String dossierAftersalesLifetime) throws SQLException {

		String sql = selectSqlSentence(flag, dossierAftersalesLifetime);
		// Log.d(tag, "sql is : " + sql);
		// Log.d(tag, "Select all data.");

		// Log.d(tag, "order cursor count is:" + cursor.getCount());
		List<Order> listOrders = new ArrayList<Order>();
		if(sqLiteDatabase == null){
			sqLiteDatabase = dbHelper.getWritableDatabase();
		}
		if(sqLiteDatabase == null){
			return listOrders;
		}
		sqLiteDatabase.beginTransaction();
		Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
		// Log.d(tag, "order cursor is:");
		int cursorNum = cursor.getCount();
		for (int i = 0; i < cursorNum; i++) {

			cursor.moveToPosition(i);
			String trainType = cursor.getString(cursor.getColumnIndexOrThrow(ORDERS_TRAIN_TYPE));
			int personNumber = cursor.getInt(cursor.getColumnIndexOrThrow(ORDERS_PERSON_NUMBER));
			String trainNr = cursor.getString(cursor.getColumnIndexOrThrow(ORDERS_TRAINNR));
			int orderState = cursor.getInt(cursor
					.getColumnIndexOrThrow(ORDERS_ORDER_STATE));
			if (flag == 2 && orderState == 1) {
				continue;
			}
			String DNR = cursor.getString(cursor.getColumnIndexOrThrow(ORDERS_DNR));
			String origin = cursor.getString(cursor.getColumnIndexOrThrow(ORDERS_ORIGIN_STATION_NAME));
			String originCode = cursor.getString(cursor.getColumnIndexOrThrow(ORDERS_ORIGIN_CODE));
			String destination = cursor.getString(cursor.getColumnIndexOrThrow(ORDERS_DESTINATION_STATION_NAME));
			String destinationCode = cursor.getString(cursor.getColumnIndexOrThrow(ORDERS_DESTINATION_CODE));
			String pnr = cursor.getString(cursor.getColumnIndexOrThrow(ORDERS_PNR));
			String departureDate = cursor.getString(cursor.getColumnIndexOrThrow(ORDERS_DEPARTURE_DATE));

			String sortDepartureDate = cursor.getString(cursor.getColumnIndexOrThrow(ORDERS_SORT_DEPARTURE_TIME));
			String sortFirstChildDepartureDate = cursor.getString(cursor.getColumnIndexOrThrow(ORDERS_SORT_FIRST_CHILD_DEPARTURE_TIME));
			String dossierGUID = cursor.getString(cursor.getColumnIndexOrThrow(DOSSIER_GUID));
			String travelSegmentID = cursor.getString(cursor.getColumnIndexOrThrow(ORDERS_TRAVEL_SEGMENT_ID));
			boolean includesEBS = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(ORDERS_INCLUDES_EBS)));
			boolean isCorrupted = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(ORDERS_CORRUPTED)));

			String trvelClasString = cursor.getString(cursor.getColumnIndexOrThrow(ORDERS_TRAVEL_CLASS));
			ComforClass travelclass = null;
			if (StringUtils.equalsIgnoreCase("0", trvelClasString)) {
				travelclass = ComforClass.FIRST;
			} else {
				travelclass = ComforClass.SECOND;
			}

			int ordinal = cursor.getInt(cursor.getColumnIndexOrThrow(ORDERS_DIRECTION));
			Direction direction = null;
			switch (ordinal) {
			case 0:
				direction = Direction.Outward;
				break;
			case 1:
				direction = Direction.Return;
				break;
			case 2:
				direction = Direction.Roundtrip;
				break;
			case 3:
				direction = Direction.Single;
				break;
			case 4:
				direction = Direction.unknown;
				break;
			default:
				break;
			}
			String email = cursor.getString(cursor.getColumnIndexOrThrow(ORDERS_EMAIL));
			
			String refundable = cursor.getString(cursor.getColumnIndexOrThrow(ORDERS_REFUNDABLE));
			String exchangeable = cursor.getString(cursor.getColumnIndexOrThrow(ORDERS_EXCHANGEABLE));
			String rulfillmentFailed = cursor.getString(cursor.getColumnIndexOrThrow(ORDERS_RULFILLMENTFAILED));
			
			boolean hasDuplicatedStationboard = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(ORDERS_HAS_DUPLICATED_STATIONBOARD)));
			String duplicatedStationboardId = cursor.getString(cursor.getColumnIndexOrThrow(ORDERS_DUPLICATED_STATIONBOARD_ID));

			boolean hasDepartureTime = Boolean.valueOf(cursor.getString(cursor
					.getColumnIndexOrThrow(ORDERS_HAS_DEPARTURE_TIME)));
			Order order = new Order(trainType, personNumber, trainNr,orderState, DNR, origin, originCode, destination, destinationCode, pnr,
					DateUtils.stringToDateTime(departureDate), dossierGUID,travelSegmentID, includesEBS, travelclass, direction,
					hasDepartureTime,DateUtils.stringToDateTime(sortDepartureDate), DateUtils.stringToDateTime(sortFirstChildDepartureDate), 
					isCorrupted, email, refundable, exchangeable, rulfillmentFailed, hasDuplicatedStationboard, duplicatedStationboardId);
			listOrders.add(order);
		}
		sqLiteDatabase.endTransaction();
		cursor.close();

		return listOrders;
	}
	
	
	public boolean updateOrdersRelationStationboard(String travelSegmentID, String stationboardId) {

		ContentValues contentValues = new ContentValues();
		if(sqLiteDatabase == null){
			sqLiteDatabase = dbHelper.getWritableDatabase();
		}
		if(sqLiteDatabase == null){
			return false;
		}
		sqLiteDatabase.beginTransaction();
		try {

			// if (stationBoardBulk.isCallSuccessFul() == true) {

			contentValues.put(ORDERS_HAS_DUPLICATED_STATIONBOARD,String.valueOf(true));
			contentValues.put(ORDERS_DUPLICATED_STATIONBOARD_ID, stationboardId);

			LogUtils.d(tag, "updateOrdersRelationStationboard for...." + travelSegmentID + "====Stationboard id is...." + stationboardId);
			// Log.e(TAG, "CallSuccessFul=======" +
			// stationBoardBulk.isIsCancelled());
			sqLiteDatabase.update(DB_TABLE_ORDERS, contentValues, ORDERS_TRAVEL_SEGMENT_ID + " = '" + travelSegmentID + "'", null);
			

			// Log.d(tag, "Insert data to TABLE= "+DB_TABLE_STATION);
			sqLiteDatabase.setTransactionSuccessful();
		} finally {
			sqLiteDatabase.endTransaction();
		}
		return true;

		// Log.d(tag, "There is no data was inserted.");

	}

	// make a SQL sentence. 0 means: future order, 1 means: history order, 2
	// means: upcoming order
	private String selectSqlSentence(int flag, String dossierAftersalesLifetime) {

		String sql = "";
		Date nowTime = new Date();
		String nowTimeStr = DateUtils.dateToString(nowTime);

		// Log.d(tag, "nowTime is : " + nowTimeStr);
		switch (flag) {
		case 0:
			sql = "select * from Orders where Orders.DepartureDate >= '"
					+ nowTimeStr
					+ "' and Orders.OrderState!=0 order by Orders.SortDepartureDate asc, Orders.SortFirstChildDepartureDate asc, Orders.DNR";
			break;
		case 1:
			String theDayBeforeYesterday = DateUtils.dateToString(DateUtils
					.getTheDayBeforeYesterday(Integer.valueOf(dossierAftersalesLifetime)));
			sql = "select * from Orders where Orders.DepartureDate >= '"
					+ theDayBeforeYesterday
					+ "' and Orders.DepartureDate < '"
					+ nowTimeStr
					+ "' and Orders.OrderState!=0 order by Orders.SortDepartureDate desc, Orders.SortFirstChildDepartureDate desc, Orders.DNR";

			break;
		case 2:

			sql = "select * from Orders where substr(Orders.DepartureDate,0,11) = (select substr(Orders.DepartureDate,0,11) from Orders where Orders.DepartureDate >= '"
					+ nowTimeStr
					+ "' and Orders.OrderState!= 0 order by Orders.SortDepartureDate asc limit 1) order by Orders.SortDepartureDate asc, Orders.SortFirstChildDepartureDate asc, Orders.DNR";
			break;

		case 3:
			sql = "select * from Orders where Orders.OrderState==0 order by Orders.SortDepartureDate, Orders.SortFirstChildDepartureDate, Orders.DNR";
			break;
		case 4:
			String pastTime = DateUtils.dateToString(DateUtils
					.getTheDayBeforeYesterday(Integer.valueOf(dossierAftersalesLifetime)));
			sql = "select * from Orders where Orders.DepartureDate < '"
					+ pastTime + "' group by DNR";

			break;
		}
		return sql;
	}

	public boolean hasNotPastOrder(Order order) throws SQLException {

		String dnr = "";
		if (order != null) {
			dnr = order.getDNR();
		}
		Date nowTime = new Date();
		String nowTimeStr = DateUtils.dateToString(nowTime);
		String sql = "select * from Orders where DNR = '" + dnr
				+ "' and Orders.DepartureDate >= '" + nowTimeStr + "'";
		// Log.d(tag, "sql is : " + sql);
		// Log.d(tag, "Select all data.");
		if(sqLiteDatabase == null){
			sqLiteDatabase = dbHelper.getWritableDatabase();
		}
		if(sqLiteDatabase == null){
			return false;
		}
		Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
		// Log.d(tag, "order cursor is:");
		int cursorNum = cursor.getCount();
		if (cursorNum > 0) {
			return true;
		}

		cursor.close();
		return false;
	}

	/**
	 * Delete all data by table name
	 * 
	 * @return true means everything is OK, otherwise means failure
	 */
	public boolean deleteAllOrders() {
		int isDelete;
		// sqLiteDatabase.execSQL("delete from Orders where DNR = '+"
		// EXYFDTY'");
		isDelete = sqLiteDatabase.delete(DB_TABLE_ORDERS, null, null);
		// Log.d(tag, "Delete all data in " + DB_TABLE_ORDERS);
		if (isDelete > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Delete all data by table name
	 * 
	 * @return true means everything is OK, otherwise means failure
	 */
	public boolean deleteOrder(String orderId) {
		int isDelete = 0;
		if(sqLiteDatabase == null){
			sqLiteDatabase = dbHelper.getWritableDatabase();
		}
		if(sqLiteDatabase == null){
			return false;
		}
		if (orderId != null) {
			isDelete = sqLiteDatabase.delete(DB_TABLE_ORDERS, ORDERS_DNR + "='"
					+ orderId + "'", null);
		}

		// Log.d(tag, "Delete all data in " + DB_TABLE_ORDERS);
		if (isDelete > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isExistPDFs(String pdfId) {
		if(sqLiteDatabase == null){
			sqLiteDatabase = dbHelper.getWritableDatabase();
		}
		if(sqLiteDatabase == null){
			return false;
		}
		Cursor cursor = sqLiteDatabase.query(DB_TABLE_PDFS, new String[] {
				PDFS_ID, PDFS_CONTENT, }, PDFS_ID + " = '" + pdfId + "'", null,
				null, null, null);

		int cursorNum = cursor.getCount();

		if (cursorNum > 0)
			return true;
		else
			return false;
	}

	public void deletePDFs(String pdfId) {
		if(sqLiteDatabase == null){
			sqLiteDatabase = dbHelper.getWritableDatabase();
		}
		if(sqLiteDatabase == null){
			return;
		}
		sqLiteDatabase
				.delete(DB_TABLE_PDFS, PDFS_ID + "='" + pdfId + "'", null);
	}

	public boolean isExistBarCodes(String barCodesId) {
		if(sqLiteDatabase == null){
			sqLiteDatabase = dbHelper.getWritableDatabase();
		}
		if(sqLiteDatabase == null){
			return false;
		}
		Cursor cursor = sqLiteDatabase.query(DB_TABLE_BARCODES, new String[] {
				BARCODES_ID, BARCODES_CONTENT, }, BARCODES_ID + " = '"
				+ barCodesId + "'", null, null, null, null);

		int cursorNum = cursor.getCount();

		if (cursorNum > 0)
			return true;
		else
			return false;
	}

	public void deleteBarcodes(String barCodesId) {
		if(sqLiteDatabase == null){
			sqLiteDatabase = dbHelper.getWritableDatabase();
		}
		if(sqLiteDatabase == null){
			return;
		}
		sqLiteDatabase.delete(DB_TABLE_BARCODES, BARCODES_ID + "='"
				+ barCodesId + "'", null);
	}

}
