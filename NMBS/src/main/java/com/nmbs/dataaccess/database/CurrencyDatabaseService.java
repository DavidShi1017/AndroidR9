package com.nmbs.dataaccess.database;

import java.util.ArrayList;
import java.util.List;

import com.nmbs.model.Currency;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import net.sqlcipher.database.SQLiteDatabase;



/**
 * Exposes methods to manage a SQLite database.
 * It is used to manage Currency.
 */
public class CurrencyDatabaseService {
	//private static final String tag = CurrencyDatabaseService.class.getSimpleName();
	
	// Database fields   
	public static final String DB_TABLE_CURRENCY = "currency";
	public static final String CURRENCY_ID = "_id";
	public static final String CURRENCY_NAME = "currecnyName";	
	public static final String CURRENCY_CODE = "currencyCode";
	public static final String CURRENCY_SYMBOL = "currencySymbol";
	public static final String NUMBER_OF_DECIMALS = "numberOfDecimals";

    private SQLiteDatabase sqLiteDatabase;  
    private DatabaseHelper dbHelper;  
  
    public CurrencyDatabaseService(Context context) {  
        dbHelper = DatabaseHelper.getInstance(context);  
        sqLiteDatabase = dbHelper.getWritableDatabase();
    }  
        
    /**
     * Insert data to table.
     * @param List<Currency> currencies
   	 * @return true means everything is OK, otherwise means failure
     */
    public boolean insertCurrencyCollection(List<Currency> currencies) {
		if (currencies != null ) {
			ContentValues contentValues = new ContentValues();	
			sqLiteDatabase.beginTransaction();
			   try {
				   for (Currency currency : currencies) {
						contentValues.put(CURRENCY_NAME, currency.getCurrencyName());
						contentValues.put(CURRENCY_CODE, currency.getCurrencyCode());
						contentValues.put(CURRENCY_SYMBOL, currency.getCurrencySymbol());
						contentValues.put(NUMBER_OF_DECIMALS, currency.getNumberOfDecimals());
						sqLiteDatabase.insert(DB_TABLE_CURRENCY, CURRENCY_ID, contentValues);
					}						
					//Log.d(tag, "Insert data to TABLE= "+DB_TABLE_CURRENCY);
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
     * @return CurrencyResponse
     * @throws SQLException
     */
	public List<Currency> selectCurrencyCollection() throws SQLException {	
		Cursor cursor = sqLiteDatabase.query(DB_TABLE_CURRENCY
				, new String[] { CURRENCY_ID, CURRENCY_NAME, CURRENCY_CODE , CURRENCY_SYMBOL , NUMBER_OF_DECIMALS  }
				, null, null, null, null, null);	
		int cursorNum = cursor.getCount();
		List<Currency> currencies = new ArrayList<Currency>();
		Currency currency = null;
		for (int i = 0; i < cursorNum; i++) {			
			cursor.moveToPosition(i);		
			String currencyName = cursor.getString(cursor.getColumnIndexOrThrow(CURRENCY_NAME));
			String currencyCode = cursor.getString(cursor.getColumnIndexOrThrow(CURRENCY_CODE));
			String currencySymbol = cursor.getString(cursor.getColumnIndexOrThrow(CURRENCY_SYMBOL));
			int numberOfDecimals = cursor.getInt(cursor.getColumnIndexOrThrow(NUMBER_OF_DECIMALS));
			
			currency = new Currency(currencyCode, currencyName, currencySymbol, numberOfDecimals);
			currencies.add(currency);
		}
		cursor.close();
		return currencies;		
	}	
	

    /**
     * Select one currency by a code
     * @return Currency
     * @throws SQLException
     */
	public Currency selectCurrencyCollectionByCode(String currencyCode) throws SQLException {	
		Cursor cursor = sqLiteDatabase.query(DB_TABLE_CURRENCY
				, new String[] { CURRENCY_ID, CURRENCY_NAME, CURRENCY_CODE , CURRENCY_SYMBOL , NUMBER_OF_DECIMALS  }
				,CURRENCY_CODE+"='"+currencyCode+"'" , null, null, null, null);	
		int cursorNum = cursor.getCount();
		
		Currency currency = null;
		for (int i = 0; i < cursorNum; i++) {			
			cursor.moveToPosition(i);		
			String currencyName = cursor.getString(cursor.getColumnIndexOrThrow(CURRENCY_NAME));
			String code = cursor.getString(cursor.getColumnIndexOrThrow(CURRENCY_CODE));
			String currencySymbol = cursor.getString(cursor.getColumnIndexOrThrow(CURRENCY_SYMBOL));
			int numberOfDecimals = cursor.getInt(cursor.getColumnIndexOrThrow(NUMBER_OF_DECIMALS));
			
			currency = new Currency(code, currencyName, currencySymbol, numberOfDecimals);
			
		}
		cursor.close();
		return currency;		
	}	
	
	/**
	 * Delete all data by different table name
	 * @param tableName
	 * @return true means everything is OK, otherwise means failure
	 */
	public boolean deleteMasterData(String tableName) {
		int isDelete;
		isDelete = sqLiteDatabase.delete(DB_TABLE_CURRENCY, null, null) ;
		//Log.d(tag, "Delete all data in " + tableName);
		if(isDelete > 0){
			return true;
		}else{
			return false;
		}
	}   

	public Cursor getCurrencyCollectionCursor() throws SQLException {	
		Cursor cursor = sqLiteDatabase.query(DB_TABLE_CURRENCY
				, new String[] { CURRENCY_ID, CURRENCY_NAME, CURRENCY_CODE , CURRENCY_SYMBOL , NUMBER_OF_DECIMALS  }
				, null, null, null, null, null);			
		return cursor;		
	}	
}
