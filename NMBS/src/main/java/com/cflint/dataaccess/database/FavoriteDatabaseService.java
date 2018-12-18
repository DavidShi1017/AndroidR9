package com.cflint.dataaccess.database;


import java.util.ArrayList;
import java.util.List;


import com.cflint.application.NMBSApplication;
import com.cflint.model.City;
import com.cflint.model.Favorite;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import net.sqlcipher.database.SQLiteDatabase;



//for earlier sprint, it will be refactor
public class FavoriteDatabaseService {
	//private static final String TAG = FavoriteDatabaseService.class.getSimpleName();

	// Database fields   
	public static final String DB_TABLE_FAVORITE = "favorite";
	public static final String FAVORITE_ID = "_id";
	public static final String FAVORITE_CITYID = "CityId";
	public static final String FAVORITE_NAME = "name";


	public static final String FAVORITE_MAIN_IMAGE_HIGH_RESOLUTION = "MainImageHighResolution";
	public static final String FAVORITE_MAIN_IMAGE_LOW_RESOLUTION = "MainImageLowResolution";

	public static final String FAVORITE_ICON_HIGH_RESOLUTION = "IconHighResolution";
	public static final String FAVORITE_ICON_LOW_RESOLUTION = "IconLowResolution";

	public static final String FAVORITE_MAINSTATION = "mainStation";

	public static final String FAVORITE_EVENTIDS_NEW_ID_NUMBER = "EventNewIDNumber";
	public static final String FAVORITE_POIIDS_NEW_ID_NUMBER = "POIDNewIDNumber";
	public static final String FAVORITE_RESTOIDS_NEW_ID_NUMBER = "RestoNewIDNumber";
	private SQLiteDatabase sqLiteDatabase;
	private DatabaseHelper dbHelper;

	public FavoriteDatabaseService(Context context) {
		dbHelper = DatabaseHelper.getInstance(NMBSApplication.getInstance().getApplicationContext());
		sqLiteDatabase = dbHelper.getWritableDatabase();
	}



	/**
	 * insert data to DB_TABLE_FAVORITE, id is PRIMARY KEY.
	 */
	public boolean insertOneFavorite(City city) {
		if (city != null) {

			if(sqLiteDatabase == null){
				sqLiteDatabase = dbHelper.getWritableDatabase();
			}
			if(sqLiteDatabase == null){
				return false;
			}

			ContentValues contentValues = new ContentValues();
			// contentValues.put(FAVORITE_ID, id);
			//Log.d(TAG, "city.getId()======== " + city.getId());
			contentValues.put(FAVORITE_CITYID, city.getId());
			contentValues.put(FAVORITE_NAME, city.getName());
			contentValues.put(FAVORITE_MAIN_IMAGE_HIGH_RESOLUTION, city.getMainImageHighResolution());
			contentValues.put(FAVORITE_MAIN_IMAGE_LOW_RESOLUTION,city.getMainImageLowResolution());
			contentValues.put(FAVORITE_ICON_HIGH_RESOLUTION, city.getIconHighResolution());
			contentValues.put(FAVORITE_ICON_LOW_RESOLUTION,city.getIconLowResolution());
			contentValues.put(FAVORITE_MAINSTATION, city.getMainStation());
			contentValues.put(FAVORITE_EVENTIDS_NEW_ID_NUMBER, city.getEventnewIDNumber());
			contentValues.put(FAVORITE_POIIDS_NEW_ID_NUMBER, city.getPoiIDNumber());
			contentValues.put(FAVORITE_RESTOIDS_NEW_ID_NUMBER, city.getRestoIDNumber());
			sqLiteDatabase.insert(DB_TABLE_FAVORITE, FAVORITE_ID,
					contentValues);
			//Log.d(TAG, "insertData to TABLE= " + DB_TABLE_FAVORITE);


			return true;

		}
		return false;
	}

	public void deleteOneFavorite(City favorite) {
		//Log.d(TAG, "delete Favorite, id= "+favorite.getName());
		if(sqLiteDatabase == null){
			sqLiteDatabase = dbHelper.getWritableDatabase();
		}
		if(sqLiteDatabase == null){
			return;
		}
		sqLiteDatabase.delete(DB_TABLE_FAVORITE, FAVORITE_NAME + "='" + favorite.getName() + "'", null) ;
	}

	public List<City> queryAllFavorite() throws SQLException {
		List<City> favorites = new ArrayList<City>();
		if(sqLiteDatabase == null){
			sqLiteDatabase = dbHelper.getWritableDatabase();
		}
		if(sqLiteDatabase == null){
			return favorites;
		}
		City favorite = null;
		//Log.d(TAG, "queryAllFavorite ");
		Cursor cursor = sqLiteDatabase.query(DB_TABLE_FAVORITE, new String[] { FAVORITE_ID, FAVORITE_CITYID, FAVORITE_NAME,
						FAVORITE_MAIN_IMAGE_HIGH_RESOLUTION, FAVORITE_MAIN_IMAGE_LOW_RESOLUTION, FAVORITE_ICON_HIGH_RESOLUTION, FAVORITE_ICON_LOW_RESOLUTION, FAVORITE_MAINSTATION, FAVORITE_EVENTIDS_NEW_ID_NUMBER, FAVORITE_POIIDS_NEW_ID_NUMBER, FAVORITE_RESTOIDS_NEW_ID_NUMBER}
				, null, null, null, null, FAVORITE_NAME);
		//Log.d(TAG, "cursor count is." + cursor.getCount());
		int cursorNum = cursor.getCount();
		for (int i = 0; i < cursorNum; i++) {
			cursor.moveToPosition(i);
			String cityId = cursor.getString(cursor
					.getColumnIndexOrThrow(FavoriteDatabaseService.FAVORITE_CITYID));
			//Log.d(TAG, "cityId ==========" + cityId);
			String name = cursor.getString(cursor
					.getColumnIndexOrThrow(FavoriteDatabaseService.FAVORITE_NAME));


			String mainImage = cursor.getString(cursor.getColumnIndexOrThrow(FAVORITE_MAIN_IMAGE_HIGH_RESOLUTION));
			String mainImageLowResolution = cursor.getString(cursor.getColumnIndexOrThrow(FAVORITE_MAIN_IMAGE_LOW_RESOLUTION));
			String icon = cursor.getString(cursor.getColumnIndexOrThrow(FAVORITE_ICON_HIGH_RESOLUTION));
			String iconImageLowResolution = cursor.getString(cursor.getColumnIndexOrThrow(FAVORITE_ICON_LOW_RESOLUTION));
			String mainStation = cursor.getString(cursor.getColumnIndexOrThrow(FAVORITE_MAINSTATION));

			int eventNewIDsNumber = cursor.getInt(cursor.getColumnIndex(FAVORITE_EVENTIDS_NEW_ID_NUMBER));
			int poiNewIDsNumber = cursor.getInt(cursor.getColumnIndex(FAVORITE_POIIDS_NEW_ID_NUMBER));
			int restoNewIDsNumber = cursor.getInt(cursor.getColumnIndex(FAVORITE_RESTOIDS_NEW_ID_NUMBER));
			favorite = new City(cityId, name, mainImage, mainImageLowResolution, icon, iconImageLowResolution, mainStation, null, null, null, eventNewIDsNumber, poiNewIDsNumber, restoNewIDsNumber);
			favorites.add(favorite);
		}
		cursor.close();
		return favorites;
		//return cursor;
	}
	/**
	 * Insert data to table.
	 * @param  cities
	 * @return true means everything is OK, otherwise means failure
	 */
	public boolean insertStationCollection(List<City> cities,
										   List<City> favoritesOfCity) {
		if(sqLiteDatabase == null){
			sqLiteDatabase = dbHelper.getWritableDatabase();
		}
		if(sqLiteDatabase == null){
			return false;
		}
		if (cities != null) {
			ContentValues contentValues = new ContentValues();
			sqLiteDatabase.beginTransaction();

			try {

				for (City city : cities) {
					for (City favorite : favoritesOfCity) {
						//Log.d(TAG, "city.getId()================ " + city.getId());
						//Log.d(TAG, "favorite.getId()============= " + favorite.getId());
						if (city.getId().equalsIgnoreCase(favorite.getId())) {
							contentValues.put(FAVORITE_CITYID,city.getId() != null ? city.getId() : "");
							contentValues.put(FAVORITE_NAME,city.getName() != null ? city.getName() : "");
							contentValues.put(FAVORITE_MAIN_IMAGE_HIGH_RESOLUTION,city.getMainImageHighResolution());
							contentValues.put(FAVORITE_MAIN_IMAGE_LOW_RESOLUTION,city.getMainImageLowResolution());
							contentValues.put(FAVORITE_ICON_HIGH_RESOLUTION,city.getIconHighResolution());
							contentValues.put(FAVORITE_ICON_LOW_RESOLUTION,city.getIconLowResolution());
							contentValues.put(FAVORITE_MAINSTATION,city.getMainStation());
							contentValues.put(FAVORITE_EVENTIDS_NEW_ID_NUMBER,city.getEventnewIDNumber());
							contentValues.put(FAVORITE_POIIDS_NEW_ID_NUMBER,city.getPoiIDNumber());
							contentValues.put(FAVORITE_RESTOIDS_NEW_ID_NUMBER,city.getRestoIDNumber());
							sqLiteDatabase.insert(DB_TABLE_FAVORITE, FAVORITE_ID,contentValues);
						}
					}
				}
				//Log.d(TAG, "Insert data to TABLE= " + DB_TABLE_FAVORITE);
				sqLiteDatabase.setTransactionSuccessful();
			} finally {
				sqLiteDatabase.endTransaction();
			}
			return true;
		} else {
			//Log.d(TAG, "There is no data was inserted.");
			return false;
		}
	}
	public Cursor queryOneFavoriteById(int id) throws SQLException {
		//Log.d(TAG, "queryOneFavoriteById = "+id);
		Cursor cursor = sqLiteDatabase.query(true, DB_TABLE_FAVORITE, new String[] { FAVORITE_ID, FAVORITE_NAME}
				, FAVORITE_ID + "=" + id, null, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}



	/**
	 * Create all Favorites from the cursor, convert Cursor to the model
	 * @param
	 */
	public List<Favorite> selectFavoriteCollection() {
		List<Favorite> favorites = new ArrayList<Favorite>();

		Favorite favorite = null;

		String sql = "select * from favorite";
		//Log.d(TAG, "Select all data.");
		sqLiteDatabase.beginTransaction();
		Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
		//Log.d(TAG, "cursor count is." + cursor.getCount());
		int cursorNum = cursor.getCount();
		for (int i = 0; i < cursorNum; i++) {
			cursor.moveToPosition(i);
			int proId = cursor.getInt(cursor
					.getColumnIndexOrThrow(FavoriteDatabaseService.FAVORITE_ID));
			String name = cursor.getString(cursor
					.getColumnIndexOrThrow(FavoriteDatabaseService.FAVORITE_NAME));

			favorite = new Favorite(proId, name);
			favorites.add(favorite);
		}
		cursor.close();
		return favorites;
	}
	public City selectCityByMainStationCode(String stationCode) throws SQLException {
		City city = null;
		if(sqLiteDatabase == null){
			sqLiteDatabase = dbHelper.getWritableDatabase();
		}
		if(sqLiteDatabase == null){
			return city;
		}
		//Log.d(TAG, "Select City by MainStationCode.");
		Cursor cursor = sqLiteDatabase.query(DB_TABLE_FAVORITE,
				new String[] {FAVORITE_ID, FAVORITE_CITYID, FAVORITE_NAME, FAVORITE_MAIN_IMAGE_HIGH_RESOLUTION, FAVORITE_MAIN_IMAGE_LOW_RESOLUTION, FAVORITE_ICON_HIGH_RESOLUTION, FAVORITE_ICON_LOW_RESOLUTION,
						FAVORITE_MAINSTATION,FAVORITE_EVENTIDS_NEW_ID_NUMBER,FAVORITE_POIIDS_NEW_ID_NUMBER,FAVORITE_RESTOIDS_NEW_ID_NUMBER },
				FAVORITE_MAINSTATION + " = '"+stationCode+ "'", null, null, null, null);

		int cursorNum = cursor.getCount();
		//Log.d(TAG, "cursorNum...." + cursorNum);

		for (int i = 0; i < cursorNum; i++) {
			cursor.moveToPosition(i);
			String cityId = cursor.getString(cursor
					.getColumnIndexOrThrow(FavoriteDatabaseService.FAVORITE_CITYID));
			String name = cursor.getString(cursor
					.getColumnIndexOrThrow(FAVORITE_NAME));
			String mainImage = cursor.getString(cursor
					.getColumnIndexOrThrow(FAVORITE_MAIN_IMAGE_HIGH_RESOLUTION));
			String mainImageLowResolution = cursor.getString(cursor.getColumnIndexOrThrow(FAVORITE_MAIN_IMAGE_LOW_RESOLUTION));
			String icon = cursor.getString(cursor
					.getColumnIndexOrThrow(FAVORITE_ICON_HIGH_RESOLUTION));
			String iconImageLowResolution = cursor.getString(cursor.getColumnIndexOrThrow(FAVORITE_ICON_LOW_RESOLUTION));
			String mainStation = cursor.getString(cursor
					.getColumnIndexOrThrow(FAVORITE_MAINSTATION));
			int eventNewIDsNumber = cursor.getInt(cursor.getColumnIndex(FAVORITE_EVENTIDS_NEW_ID_NUMBER));
			int poiNewIDsNumber = cursor.getInt(cursor.getColumnIndex(FAVORITE_POIIDS_NEW_ID_NUMBER));
			int restoNewIDsNumber = cursor.getInt(cursor.getColumnIndex(FAVORITE_RESTOIDS_NEW_ID_NUMBER));
			city = new City(cityId, name, mainImage, mainImageLowResolution, icon, iconImageLowResolution, mainStation, null, null, null, eventNewIDsNumber, poiNewIDsNumber, restoNewIDsNumber);
		}
		cursor.close();

		//Log.d(TAG, "city is null?" + String.valueOf(city == null));
		return city;
	}
	/**
	 * Delete all data by different table name
	 * @param tableName
	 * @return true means everything is OK, otherwise means failure
	 */
	public boolean deleteMasterData(String tableName) {
		if(sqLiteDatabase == null){
			sqLiteDatabase = dbHelper.getWritableDatabase();
		}
		if(sqLiteDatabase == null){
			return false;
		}
		int isDelete;
		isDelete = sqLiteDatabase.delete(tableName, null, null) ;
		//Log.d(TAG, "Delete all data in " + tableName);
		if(isDelete > 0){
			return true;
		}else{
			return false;
		}
	}


}
