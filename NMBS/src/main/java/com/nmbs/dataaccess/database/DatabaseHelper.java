package com.nmbs.dataaccess.database;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.nmbs.log.LogUtils;
import com.nmbs.services.impl.ServiceConstant;
import com.nmbs.util.DeviceUtil;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {
	//private static final String tag = DatabaseHelper.class.getSimpleName();
	public static final String DATABASE_NAME = "dhanmbs.db";
	public static final String NEW_DATABASE_NAME = "nmbs.db";

	private static final int DATABASE_VERSION = 34;  //Added station board in 22 for 5.0.
	private SQLiteDatabase SQLiteDatabase;

	private static DatabaseHelper instance = null; ;
	public static final String PASSWORD = "dhanmbs";
	private Context context;
	// Database creation sql statement

	private static final String DB_CREATE_LAST_SCHEDULE_QUERY = "CREATE TABLE IF NOT EXISTS " + ScheduleQueryDataBaseService.DB_TABLE_SCHEDULE_LAST_QUERY + " ("
			+ ScheduleQueryDataBaseService.LAST_QUERY_ID + " TEXT PRIMARY KEY, "
			+ ScheduleQueryDataBaseService.LAST_QUERY_ORIGIN_CODE + " TEXT, "
			+ ScheduleQueryDataBaseService.LAST_QUERY_DESTINATION_CODE + " TEXT, "
			+ ScheduleQueryDataBaseService.LAST_QUERY_VIA_CODE + " TEXT, "
			+ ScheduleQueryDataBaseService.LAST_QUERY_TIME_PREFERENCE + " TEXT, "
			+ ScheduleQueryDataBaseService.LAST_QUERY_TRAIN_NR + " TEXT, "
			+ ScheduleQueryDataBaseService.LAST_QUERY_DATE_TIME + " TEXT, "
			+ ScheduleQueryDataBaseService.LAST_QUERY_DES_NAME + " TEXT, "
			+ ScheduleQueryDataBaseService.LAST_QUERY_VIA_NAME + " TEXT, "
			+ ScheduleQueryDataBaseService.LAST_QUERY_ORIGIN_NAME + " TEXT "
			+ ");" ;

	private static final String DB_CREATE_TRAVEL_SEGMENT_FOR_NOTIFICATION = "CREATE TABLE IF NOT EXISTS " + TravelSegmentDatabaseService.DB_Dossier_TravelSegment + " ("
			+ TravelSegmentDatabaseService.DB_Dossier_TravelSegment_Date + " TEXT PRIMARY KEY, "
			+ TravelSegmentDatabaseService.Column_notification_id + " TEXT, "
			+ TravelSegmentDatabaseService.Column_is_cancel + " TEXT"
			+ ");" ;

	private static final String DB_CREATE_REALTIMEINFO = "CREATE TABLE IF NOT EXISTS " + RealTimeInfoDatabaseService.DB_TABLE_REAL_TIME_INFO + " ("
			+ RealTimeInfoDatabaseService.REAL_TIME_INFO_ID + " TEXT PRIMARY KEY, "
			+ RealTimeInfoDatabaseService.REAL_TIME_INFO_SUCCESS + " TEXT, "
			+ RealTimeInfoDatabaseService.REAL_TIME_INFO_CONTENT + " TEXT, "
			+ RealTimeInfoDatabaseService.REAL_TIME_INFO_FLAG + " TEXT"
			+ ");" ;

	private static final String DB_CREATE_CURRENCY = "CREATE TABLE IF NOT EXISTS " + CurrencyDatabaseService.DB_TABLE_CURRENCY + " ("
		+ CurrencyDatabaseService.CURRENCY_ID + " INTEGER PRIMARY KEY, "
		+ CurrencyDatabaseService.CURRENCY_NAME + " TEXT, "
		+ CurrencyDatabaseService.CURRENCY_CODE + " TEXT, "
		+ CurrencyDatabaseService.CURRENCY_SYMBOL + " TEXT, "
		+ CurrencyDatabaseService.NUMBER_OF_DECIMALS + " INTEGER " 
		+ ");" ;

	private static final String DB_CREATE_STATION = "CREATE TABLE IF NOT EXISTS " + StationDatabaseService.DB_TABLE_STATION + " ("
		+ StationDatabaseService.STATION_ID + " INTEGER PRIMARY KEY, "
		+ StationDatabaseService.STATION_CODE + " TEXT, "
		+ StationDatabaseService.STATION_NAME + " TEXT, "
		+ StationDatabaseService.STATION_DETAIL_INFO_PATH + " TEXT, "
		+ StationDatabaseService.STATION_SYNONIEM + " TEXT, "
		
		+ StationDatabaseService.STATION_DESTINATION + " TEXT "
		+ ");" ;
		
	private static final String DB_CREATE_LANGUAGE = "CREATE TABLE IF NOT EXISTS " + CollectionItemDatabaseService.DB_TABLE_LANGUAGE + " ("
		+ CollectionItemDatabaseService.COLLECTION_ID + " INTEGER PRIMARY KEY, "
		+ CollectionItemDatabaseService.COLLECTION_KEY + " TEXT, "
		+ CollectionItemDatabaseService.COLLECTION_LABEL + " TEXT "
		+ ");" ;
	
	private static final String DB_CREATE_REDUCTION_CARD = "CREATE TABLE IF NOT EXISTS " + CollectionItemDatabaseService.DB_TABLE_REDUCTION_CARD + " ("
		+ CollectionItemDatabaseService.COLLECTION_ID + " INTEGER PRIMARY KEY, "
		+ CollectionItemDatabaseService.COLLECTION_KEY + " TEXT, "
		+ CollectionItemDatabaseService.COLLECTION_LABEL + " TEXT "
		+ ");" ;
	
	private static final String DB_CREATE_STATIONMATRIX = "CREATE TABLE IF NOT EXISTS " + OriginDestinationRuleDatabaseService.DB_TABLE_STATIONMATRIX + " ("
		+ OriginDestinationRuleDatabaseService.STATIONMATRIX_ID + " INTEGER PRIMARY KEY, "
		+ OriginDestinationRuleDatabaseService.STATIONMATRIX_STATION_FROM_CODE + " TEXT, "
		+ OriginDestinationRuleDatabaseService.STATIONMATRIX_STATION_TO_CODE + " TEXT "
		+ ");" ;
	private static final String DB_CREATE_TITLE = "CREATE TABLE IF NOT EXISTS " + CollectionItemDatabaseService.DB_TABLE_TITLE + " ("
		+ CollectionItemDatabaseService.COLLECTION_ID + " INTEGER PRIMARY KEY, "
		+ CollectionItemDatabaseService.COLLECTION_KEY + " TEXT, "
		+ CollectionItemDatabaseService.COLLECTION_LABEL + " TEXT "
		+ ");" ;
	
	private static final String DB_CREATE_COUNTRY = "CREATE TABLE IF NOT EXISTS " + CollectionItemDatabaseService.DB_TABLE_COUNTRY + " ("
		+ CollectionItemDatabaseService.COLLECTION_ID + " INTEGER PRIMARY KEY, "
		+ CollectionItemDatabaseService.COLLECTION_KEY + " TEXT, "
		+ CollectionItemDatabaseService.COLLECTION_LABEL + " TEXT "
		+ ");" ;
	private static final String DB_CREATE_PAYMENTMETHOD = "CREATE TABLE IF NOT EXISTS " + CollectionItemDatabaseService.DB_TABLE_PAYMENTMETHOD + " ("
	+ CollectionItemDatabaseService.COLLECTION_ID + " INTEGER PRIMARY KEY, "
	+ CollectionItemDatabaseService.COLLECTION_KEY + " TEXT, "
	+ CollectionItemDatabaseService.COLLECTION_LABEL + " TEXT "
	+ ");" ;
	
	// for earlier sprint, it will be refactor
	private static final String DB_CREATE_FAVORITE = "CREATE TABLE IF NOT EXISTS " + FavoriteDatabaseService.DB_TABLE_FAVORITE + " ("
		+ FavoriteDatabaseService.FAVORITE_ID + " INTEGER PRIMARY KEY, " 
		+ FavoriteDatabaseService.FAVORITE_CITYID + " TEXT, " 		
		+ FavoriteDatabaseService.FAVORITE_NAME + " TEXT, " 
		+ FavoriteDatabaseService.FAVORITE_MAIN_IMAGE_HIGH_RESOLUTION + " TEXT, " 
		+ FavoriteDatabaseService.FAVORITE_MAIN_IMAGE_LOW_RESOLUTION + " TEXT, " 
		+ FavoriteDatabaseService.FAVORITE_ICON_HIGH_RESOLUTION + " TEXT, " 
		+ FavoriteDatabaseService.FAVORITE_ICON_LOW_RESOLUTION + " TEXT, " 
		+ FavoriteDatabaseService.FAVORITE_MAINSTATION + " TEXT, " 
		+ FavoriteDatabaseService.FAVORITE_EVENTIDS_NEW_ID_NUMBER + " TEXT, " 
		+ FavoriteDatabaseService.FAVORITE_POIIDS_NEW_ID_NUMBER + " TEXT, " 
		+ FavoriteDatabaseService.FAVORITE_RESTOIDS_NEW_ID_NUMBER + " TEXT " 
		+ ");" ;

	
	private static final String DB_CREATE_DELIVERY_METHODS = "CREATE TABLE IF NOT EXISTS " + DeliveryMethodDatabaseService.DB_TABLE_DELIVERY_METHODS + " ("
	+ DeliveryMethodDatabaseService.DELIVERY_METHODS_ID + " INTEGER PRIMARY KEY, " 
	+ DeliveryMethodDatabaseService.DELIVERY_METHODS_METHOD + " TEXT, " 
	+ DeliveryMethodDatabaseService.DELIVERY_METHODS_NAME + " TEXT, " 
	+ DeliveryMethodDatabaseService.DELIVERY_METHODS_DISPLAYMETHOD + " TEXT " 
	+ ");" ;
	
	private static final String DB_CREATE_ALLOWEDSTATIONSFORPICKUP = "CREATE TABLE IF NOT EXISTS " + DeliveryMethodDatabaseService.DB_TABLE_ALLOWEDSTATIONSFORPICKUP+ " ("
	+ DeliveryMethodDatabaseService.ALLOWEDSTATIONSFORPICKUP_ID + " INTEGER PRIMARY KEY, " 
	+ DeliveryMethodDatabaseService.ALLOWEDSTATIONSFORPICKUP_DELIVERYMETHOD + " TEXT, " 
	+ DeliveryMethodDatabaseService.ALLOWEDSTATIONSFORPICKUP_STATIONNAME + " TEXT, " 	
	+ DeliveryMethodDatabaseService.ALLOWEDSTATIONSFORPICKUP_STATIONCODE + " TEXT "
	+ ");" ;
	
	private static final String DB_CREATE_ALLOWEDCOUNTRIESFORMAIL = "CREATE TABLE IF NOT EXISTS " + DeliveryMethodDatabaseService.DB_TABLE_ALLOWEDCOUNTRIESFORMAIL + " ("
	+ DeliveryMethodDatabaseService.ALLOWEDCOUNTRIESFORMAIL_ID + " INTEGER PRIMARY KEY, " 
	+ DeliveryMethodDatabaseService.ALLOWEDCOUNTRIESFORMAIL_DELIVERYMETHOD + " TEXT, " 
	+ DeliveryMethodDatabaseService.ALLOWEDCOUNTRIESFORMAIL_COUNTRYCODE + " TEXT "
	+ ");" ;
	
	private static final String DB_CREATE_ORDER = "CREATE TABLE IF NOT EXISTS " + AssistantDatabaseService.DB_TABLE_ORDERS + " ("
	+ AssistantDatabaseService.ORDERS_ID + " INTEGER PRIMARY KEY, " 
	+ AssistantDatabaseService.ORDERS_TRAIN_TYPE + " TEXT, " 
	+ AssistantDatabaseService.ORDERS_PERSON_NUMBER + " INTEGER, " 
	+ AssistantDatabaseService.ORDERS_TRAINNR + " TEXT, " 
	+ AssistantDatabaseService.ORDERS_ORDER_STATE + " INTEGER, " 
	+ AssistantDatabaseService.ORDERS_DNR + " TEXT, " 
	+ AssistantDatabaseService.ORDERS_ORIGIN_STATION_NAME + " TEXT, " 
	+ AssistantDatabaseService.ORDERS_ORIGIN_CODE + " TEXT, " 
	+ AssistantDatabaseService.ORDERS_DESTINATION_STATION_NAME + " TEXT, " 
	+ AssistantDatabaseService.ORDERS_DESTINATION_CODE + " TEXT, " 
	+ AssistantDatabaseService.ORDERS_PNR + " TEXT, " 
	+ AssistantDatabaseService.ORDERS_DEPARTURE_DATE + " TEXT, " 
	+ AssistantDatabaseService.DOSSIER_GUID + " TEXT, "
	+ AssistantDatabaseService.ORDERS_TRAVEL_SEGMENT_ID + " TEXT, "
	+ AssistantDatabaseService.ORDERS_INCLUDES_EBS + " TEXT, "
	+ AssistantDatabaseService.ORDERS_DIRECTION + " INTEGER, "	
	+ AssistantDatabaseService.ORDERS_HAS_DEPARTURE_TIME + " TEXT, "	
	+ AssistantDatabaseService.ORDERS_SORT_DEPARTURE_TIME + " TEXT, "		
	+ AssistantDatabaseService.ORDERS_SORT_FIRST_CHILD_DEPARTURE_TIME + " TEXT, "		
	
	+ AssistantDatabaseService.ORDERS_CORRUPTED + " TEXT, "		
	+ AssistantDatabaseService.ORDERS_EMAIL + " TEXT, "
	+ AssistantDatabaseService.ORDERS_REFUNDABLE + " TEXT, "
	+ AssistantDatabaseService.ORDERS_EXCHANGEABLE + " TEXT, "
	+ AssistantDatabaseService.ORDERS_RULFILLMENTFAILED + " TEXT, "
	+ AssistantDatabaseService.ORDERS_HAS_DUPLICATED_STATIONBOARD + " TEXT, "
	+ AssistantDatabaseService.ORDERS_DUPLICATED_STATIONBOARD_ID + " TEXT, "
	  
	+ AssistantDatabaseService.ORDERS_TRAVEL_CLASS + " TEXT "
	+ ");" ;
	
	private static final String DB_CREATE_GENERAL_SETTINGS = "CREATE TABLE IF NOT EXISTS " + GeneralSettingDatabaseService.DB_GENERAL_SETTINGS + " ("
		+ GeneralSettingDatabaseService.GENERAL_SETTING_ID + " INTEGER PRIMARY KEY, "
		+ GeneralSettingDatabaseService.Column_RestSalt + " TEXT, "
		+ GeneralSettingDatabaseService.Column_AutoLogonSalt + " TEXT, "
		+ GeneralSettingDatabaseService.Column_FacebookAppId + " TEXT, "
		+ GeneralSettingDatabaseService.Column_GoogleAppId + " TEXT, "
		+ GeneralSettingDatabaseService.Column_CreateProfileUrl + " TEXT, "
		+ GeneralSettingDatabaseService.Column_ProfileOverviewUrl + " TEXT, "
		+ GeneralSettingDatabaseService.Column_CommercialTtlListUrl + " TEXT, "
		+ GeneralSettingDatabaseService.Column_PrivacyPolicyUrl + " TEXT, "
		+ GeneralSettingDatabaseService.Column_Domain + " TEXT, "
		+ GeneralSettingDatabaseService.Column_CheckLastUpdateTimestampPassword + " TEXT, "
		+ GeneralSettingDatabaseService.Column_MaxRealTimeInfoHorizon + " INTEGER, "
		+ GeneralSettingDatabaseService.Column_DossierAftersalesLifetime + " INTEGER, "
		+ GeneralSettingDatabaseService.Column_BookingUrl + " TEXT, "
		+ GeneralSettingDatabaseService.Column_LffUrl + " TEXT, "
		+ GeneralSettingDatabaseService.Column_BelgiumPhoneNumber + " TEXT, "
		+ GeneralSettingDatabaseService.Column_InternationalPhoneNumber + " TEXT, "
		+ GeneralSettingDatabaseService.Column_AllowContextRegistration + " TEXT, "
		+ GeneralSettingDatabaseService.Column_InsuranceConditionsPdf + " TEXT "
		+ ");" ;
	

	
	private static final String DB_CREATE_CLICK_TO_CALL_SCENARIO = "CREATE TABLE IF NOT EXISTS " + ClickToCallScenarioDatabaseService.DB_TABLE_CLICK_TO_CALL_SCENARIO + " ("
	+ ClickToCallScenarioDatabaseService._ID + " INTEGER PRIMARY KEY, " 
	+ ClickToCallScenarioDatabaseService.CLICK_TO_CALL_SCENARIO_ID + " TEXT, " 
	+ ClickToCallScenarioDatabaseService.CLICK_TO_CALL_SCENARIO_DEFAULE_PHONENUMBER + " TEXT " 
	+ ");" ;
	
	
	private static final String DB_CREATE_PROVIDER_SETTING = "CREATE TABLE IF NOT EXISTS " + ClickToCallScenarioDatabaseService.DB_TABLE_PROVIDER_SETTING + " ("
	+ ClickToCallScenarioDatabaseService.PROVIDER_SETTING_ID + " INTEGER PRIMARY KEY, " 
	+ ClickToCallScenarioDatabaseService.PROVIDER_SETTING_PROVIDER + " TEXT, " 
	+ ClickToCallScenarioDatabaseService.PROVIDER_SETTING_PHONENUMBER + " TEXT, " 
	+ ClickToCallScenarioDatabaseService.PROVIDER_SETTING_SCENARIO_ID + " TEXT " 
	+ ");" ;
	private static final String DB_CREATE_OFFERQUERY = "CREATE TABLE IF NOT EXISTS "
			+ OfferQueryDataBaseService.DB_TABLE_OFFERQUERY + " ("
			+ OfferQueryDataBaseService.OFFERQUERY_ID + "  TEXT,"
			+ OfferQueryDataBaseService.OFFERQUERY_CONTENT + " TEXT" + ");";
	
	
	private static final String DB_CREATE_MESSAGES = "CREATE TABLE IF NOT EXISTS "
			+ MessageDatabaseService.DB_TABLE_MESSAGESV5 + " ("
			+ MessageDatabaseService.ID + " INTEGER PRIMARY KEY, "
			+ MessageDatabaseService.MESSAGE_ID +" TEXT, "
			+ MessageDatabaseService.MESSAGE_TITLE + " TEXT, "
			+ MessageDatabaseService.MESSAGE_DESCRIPTION + " TEXT, "
			+ MessageDatabaseService.MESSAGE_VALIDITY +" TEXT, "
			+ MessageDatabaseService.MESSAGE_LOWRESICON + " TEXT, "
			+ MessageDatabaseService.MESSAGE_HIGHRESICON + " TEXT, "
			+ MessageDatabaseService.MESSAGE_LOWRESIMAGE + " TEXT, "
			+ MessageDatabaseService.MESSAGE_HIGHRESIMAGE + " TEXT, "
			+ MessageDatabaseService.MESSAGE_TYPE + " TEXT, "
			+ MessageDatabaseService.MESSAGE_INCLUDEACTIONBUTTON + " TEXT, "
			+ MessageDatabaseService.MESSAGE_ACTIONBUTTONTEXT + " TEXT, "
			+ MessageDatabaseService.MESSAGE_HYPERLINK + " TEXT, "
			+ MessageDatabaseService.MESSAGE_DISPLAYINOVERLAY + " TEXT, "
			+ MessageDatabaseService.MESSAGE_NEXTDISPLAY + " TEXT, "
			+ MessageDatabaseService.MESSAGE_NAVIGATION + " TEXT, "
			+ MessageDatabaseService.MESSAGE_REPEATDISPLAY + " INTEGER" +");";
	
	
	private static final String DB_CREATE_STATIONBOARD = "CREATE TABLE IF NOT EXISTS "
			+ StationBoardDatabaseService.DB_TABLE_STATIONBOARD + " ("
			+ StationBoardDatabaseService.ID +" INTEGER PRIMARY KEY, "
			+ StationBoardDatabaseService.STATIONBOARD_ID + " TEXT, "
			+ StationBoardDatabaseService.STATIONBOARD_CODE + " TEXT, "
			+ StationBoardDatabaseService.STATIONBOARD_DATETIME +" TEXT, "
			+ StationBoardDatabaseService.STATIONBOARD_TIMEPREFERENCE + " INTEGER, "
			+ StationBoardDatabaseService.STATIONBOARD_TRAINCATEGORY + " TEXT, "
			+ StationBoardDatabaseService.STATIONBOARD_TRAINNUMBER + " TEXT, "
			+ StationBoardDatabaseService.STATIONBOARD_SORTDATE + " TEXT, "			
			+ StationBoardDatabaseService.STATIONBOARD_DNR + " TEXT, "	
			+ StationBoardDatabaseService.STATIONBOARD_ORIGIN_NAME + " TEXT, "	
			+ StationBoardDatabaseService.STATIONBOARD_DESTINATION_NAME + " TEXT, "	
			+ StationBoardDatabaseService.STATIONBOARD_REALTIME_CALLSUCCESSFUL + " TEXT, "	
			+ StationBoardDatabaseService.STATIONBOARD_REALTIME_DELAY + " TEXT, "	
			+ StationBoardDatabaseService.STATIONBOARD_REALTIME_ISCANCELLED + " TEXT, "	
			+ StationBoardDatabaseService.STATIONBOARD_PARENT_ID + " TEXT, "	
			+ StationBoardDatabaseService.STATIONBOARD_TS_ID + " TEXT, "	
			+ StationBoardDatabaseService.STATIONBOARD_TYPE + " TEXT" +");";
	
	private static final String DB_CREATE_STATIONBOARD_REALTIME = "CREATE TABLE IF NOT EXISTS "
			+ StationBoardBulkRealTimeDatabaseService.DB_TABLE_STATIONBOARD_REALTIME + " ("
			+ StationBoardBulkRealTimeDatabaseService.ID +" INTEGER PRIMARY KEY, "
			+ StationBoardBulkRealTimeDatabaseService.STATIONBOARD_REALTIME_ID + " TEXT, "
			+ StationBoardBulkRealTimeDatabaseService.STATIONBOARD_REALTIME_CALLSUCCESSFUL + " TEXT, "
			+ StationBoardBulkRealTimeDatabaseService.STATIONBOARD_REALTIME_DELAY +" TEXT, "
	
			+ StationBoardBulkRealTimeDatabaseService.STATIONBOARD_REALTIME_ISCANCELLED + " TEXT" +");";


	private static final String DB_CREATE_HAFAS_USER = "CREATE TABLE IF NOT EXISTS "
			+HafasUserDataBaseService.DB_TABLE_HAFAS_USER + " ("
			+ HafasUserDataBaseService.HAFAS_USER_ID + " TEXT, "
			+ HafasUserDataBaseService.HAFAS_REGISTER_ID + " TEXT, "
			+HafasUserDataBaseService.HAFAS_USER_language + " TEXT"+");";

	private static final String DB_CREATE_SUBSCRIPTION = "CREATE TABLE IF NOT EXISTS "
			+SubscriptionDataBaseService.DB_TABLE_SUBSCRIPTION + " ("
			+ SubscriptionDataBaseService.SUBSCRIPTION_ID + " TEXT, "
			+ SubscriptionDataBaseService.SUBSCRIPTION_RECONCTX + " TEXT, "
			+ SubscriptionDataBaseService.SUBSCRIPTION_ORIGINSTATIONRCODE + " TEXT, "
			+ SubscriptionDataBaseService.SUBSCRIPTION_DESTINATIONSTATIONRCODE + " TEXT, "
			+ SubscriptionDataBaseService.SUBSCRIPTION_ORIGIN_NAME + " TEXT, "
			+ SubscriptionDataBaseService.SUBSCRIPTION_DESNAME + " TEXT, "
			+ SubscriptionDataBaseService.SUBSCRIPTION_RECONCTX_HASH_CODE + " TEXT, "
			+ SubscriptionDataBaseService.SUBSCRIPTION_dnr + " TEXT, "
			+ SubscriptionDataBaseService.SUBSCRIPTION_Connection_id + " TEXT, "
			+SubscriptionDataBaseService.SUBSCRIPTION_DEPARTURE + " TEXT"+");";

	private static final String DB_CREATE_DOSSIER = "CREATE TABLE IF NOT EXISTS " + DossierDatabaseService.DB_Dossier+ " ("
			+ DossierDatabaseService.Column_Id + " INTEGER PRIMARY KEY, "
			+ DossierDatabaseService.Column_Dossier_Id + " TEXT, "
			+ DossierDatabaseService.Column_Dossier_Details + " TEXT, "
			+ DossierDatabaseService.Column_Dossier_Date + " TEXT, "
			+ DossierDatabaseService.Column_Dossier_PushEnabled + " TEXT, "
			+ DossierDatabaseService.Column_PDF_Successfully + " TEXT, "
			+ DossierDatabaseService.Column_Barcode_Successfully + " TEXT, "
			+ DossierDatabaseService.Column_TravelSegment_Available + " TEXT, "
			+ DossierDatabaseService.Column_Display_Overlay + " TEXT, "
			+ DossierDatabaseService.Column_LatestTravelDate + " TEXT, "
			+ DossierDatabaseService.Column_EarliestTravelDate + " TEXT "

			+ ");" ;

	private static final String DB_CREATE_TRAINICONS = "CREATE TABLE IF NOT EXISTS " + TrainIconsDatabaseService.DB_TRAIN_ICONS + " ("
			+ TrainIconsDatabaseService.Column_TrainIconsId + " INTEGER PRIMARY KEY, "
			+ TrainIconsDatabaseService.Column_BrandName + " TEXT, "
			+ TrainIconsDatabaseService.Column_LinkedHafasCodes + " TEXT, "
			+ TrainIconsDatabaseService.Column_LinkedTariffGroups + " TEXT, "
			+ TrainIconsDatabaseService.Column_LinkedTrainBrands + " TEXT, "
			+ TrainIconsDatabaseService.Column_HighResImage + " TEXT, "
			+ TrainIconsDatabaseService.Column_LowResImage + " TEXT "
			+ ");" ;
	private static final String DB_CREATE_DOSSIERUPTODATE = "CREATE TABLE IF NOT EXISTS " + DossiersUpToDateDatabaseService.DB_DossiersUpToDate + " ("
			+ DossiersUpToDateDatabaseService.Column_Id + " INTEGER PRIMARY KEY, "
			+ DossiersUpToDateDatabaseService.Column_Dnr + " TEXT, "
			+ DossiersUpToDateDatabaseService.Column_LastUpdated + " TEXT "
			+ ");" ;

	private static final String DB_CREATE_STATION_FAVORITE = "CREATE TABLE IF NOT EXISTS " + FavoriteStationsDatabaseService.DB_TABLE_STATION_FAVORITE + " ("
			+ FavoriteStationsDatabaseService.Column_Id + " INTEGER PRIMARY KEY, "
			+ FavoriteStationsDatabaseService.STATION_CODE + " TEXT "
			+ ");" ;


	private static final String DB_CREATE_LOGIN = "CREATE TABLE IF NOT EXISTS "
			+ LoginDataBaseService.DB_TABLE_LOGIN + " ("
			+ LoginDataBaseService.ID + " INTEGER PRIMARY KEY, "
			+ LoginDataBaseService.LOGIN_CUSTOMER_ID +" TEXT, "
			+ LoginDataBaseService.LOGIN_FIRSTNAME + " TEXT, "
			+ LoginDataBaseService.LOGIN_EMAIL + " TEXT, "
			+ LoginDataBaseService.LOGIN_CODE +" TEXT, "
			+ LoginDataBaseService.LOGIN_PHONENUMBER +" TEXT, "
			+ LoginDataBaseService.LOGIN_STATE + " TEXT, "
			+ LoginDataBaseService.LOGIN_STATE_DESCRIPTION + " TEXT, "
			+ LoginDataBaseService.LOGIN_PERSONID + " TEXT, "
			+ LoginDataBaseService.LOGIN_LOGINPROVIDER + " TEXT, "
			+ LoginDataBaseService.LOGIN_LAST_UPDATE_TIMESTAMP_PASSWORD + " TEXT "
			+");";

	private static final String DB_CREATE_CHECK_OPTIONS = "CREATE TABLE IF NOT EXISTS " + CheckOptionBaseService.DB_TABLE_CHECKOPTION + " ("
			+ CheckOptionBaseService.ID + " INTEGER PRIMARY KEY, "
			+ CheckOptionBaseService.CHECK_OPTION_COUNT+ " INTEGER, "
			+ CheckOptionBaseService.CHECK_OPTION_EXPIRATION + " TEXT "
			+ ");" ;

	public static DatabaseHelper getInstance(Context mContext){
		if(instance==null) {
			
			/*File databaseFile = mContext.getDatabasePath(DATABASE_NAME);
	        databaseFile.mkdirs();
	        databaseFile.delete();
	        
	        //Opening or Creating the database with our specified password
	        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(databaseFile, PASSWORD, null);
	        db.close();*/
			instance = new DatabaseHelper(mContext);
		}
		return instance;
	}
	
	//Close the SQLiteDatabase, should be called when when application is termination. 
	public void closeSQLiteDatabase() {
		if (instance != null) {
			instance.close();
			instance = null;
		}
	}
	
	private DatabaseHelper(Context context) {
		
		super(context, NEW_DATABASE_NAME, null, DATABASE_VERSION);
		this.context =  context;
	}

	// Method is called during creation of the database
	@Override
	public void onCreate(SQLiteDatabase db) {
		//Log.w(tag, "Database  onCreate");
		//Log.w(tag, "onCreate ==" + context.getDatabasePath(DATABASE_NAME));
		
		//db.execSQL("ATTACH DATABASE '" + context.getDatabasePath(DATABASE_NAME) + "' AS plaintext KEY ''");
		db.execSQL(DB_CREATE_LAST_SCHEDULE_QUERY);
		db.execSQL(DB_CREATE_CURRENCY);				
		db.execSQL(DB_CREATE_STATION);
		db.execSQL(DB_CREATE_LANGUAGE);
		db.execSQL(DB_CREATE_TITLE);
		//db.execSQL(DB_CREATE_COUNTRY);
		db.execSQL(DB_CREATE_REDUCTION_CARD);		
		db.execSQL(DB_CREATE_STATIONMATRIX);	
		db.execSQL(DB_CREATE_ORDER);
		//db.execSQL(DB_CREATE_FAVORITE);
		//db.execSQL(DB_CREATE_CITY);
		db.execSQL(DB_CREATE_DELIVERY_METHODS);
		db.execSQL(DB_CREATE_ALLOWEDSTATIONSFORPICKUP);
		db.execSQL(DB_CREATE_ALLOWEDCOUNTRIESFORMAIL);
		db.execSQL(DB_CREATE_PAYMENTMETHOD);
		db.execSQL(DB_CREATE_GENERAL_SETTINGS);
		//db.execSQL(DB_CREATE_FAQ);
		//db.execSQL(DB_CREATE_EVENTIDS);
		//db.execSQL(DB_CREATE_POIIDS);
		//db.execSQL(DB_CREATE_RESTOIDS);
		db.execSQL(DB_CREATE_CLICK_TO_CALL_SCENARIO);
		db.execSQL(DB_CREATE_PROVIDER_SETTING);
		db.execSQL(DB_CREATE_OFFERQUERY);
		db.execSQL(DB_CREATE_MESSAGES);

		db.execSQL(DB_CREATE_STATIONBOARD);
		db.execSQL(DB_CREATE_STATIONBOARD_REALTIME);

		db.execSQL(DB_CREATE_STATION_FAVORITE);
		db.execSQL(DB_CREATE_HAFAS_USER);
		db.execSQL(DB_CREATE_DOSSIER);
		db.execSQL(DB_CREATE_SUBSCRIPTION);
		db.execSQL(DB_CREATE_TRAINICONS);
		db.execSQL(DB_CREATE_REALTIMEINFO);
		db.execSQL(DB_CREATE_TRAVEL_SEGMENT_FOR_NOTIFICATION);
		db.execSQL(DB_CREATE_DOSSIERUPTODATE);
		db.execSQL(DB_CREATE_LOGIN);
		db.execSQL(DB_CREATE_CHECK_OPTIONS);


		if (context != null) {
			Intent broadcastIntent = new Intent(ServiceConstant.CREATE_VERSION_ACTION);
			context.sendBroadcast(broadcastIntent);
		}
		
	}

	// Method is called during an upgrade of the database, e.g. if you increase
	// the database version
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		LogUtils.w(DatabaseHelper.class.getName(), "onUpgrade database:" +  ", from v"+ oldVersion + " to v" + newVersion + ", which will destroy all old data");
		db.execSQL(DB_CREATE_GENERAL_SETTINGS);
		db.execSQL(DB_CREATE_MESSAGES);
		db.execSQL(DB_CREATE_DOSSIER);
		db.execSQL(DB_CREATE_LAST_SCHEDULE_QUERY);
		db.execSQL(DB_CREATE_SUBSCRIPTION);
		db.execSQL(DB_CREATE_TRAINICONS);
		db.execSQL(DB_CREATE_REALTIMEINFO);
		db.execSQL(DB_CREATE_TRAVEL_SEGMENT_FOR_NOTIFICATION);
		db.execSQL(DB_CREATE_DOSSIERUPTODATE);
		db.execSQL(DB_CREATE_HAFAS_USER);
		db.execSQL(DB_CREATE_STATION_FAVORITE);
		db.execSQL("ALTER TABLE GeneralSettings ADD RestSalt TEXT");
		db.execSQL("ALTER TABLE GeneralSettings ADD AutoLogonSalt TEXT");
		db.execSQL("ALTER TABLE GeneralSettings ADD FacebookAppId TEXT");
		db.execSQL("ALTER TABLE GeneralSettings ADD GoogleAppId TEXT");
		db.execSQL("ALTER TABLE GeneralSettings ADD CreateProfileUrl TEXT");
		db.execSQL("ALTER TABLE GeneralSettings ADD ProfileOverviewUrl TEXT");
		db.execSQL("ALTER TABLE GeneralSettings ADD CommercialTtlListUrl TEXT");
		db.execSQL("ALTER TABLE GeneralSettings ADD PrivacyPolicyUrl TEXT");
		db.execSQL("ALTER TABLE GeneralSettings ADD Domain TEXT");
		db.execSQL("ALTER TABLE GeneralSettings ADD CheckLastUpdateTimestampPassword TEXT");

		db.execSQL(DB_CREATE_CHECK_OPTIONS);
		if(!checkColumnExists(db, "MessagesV5", "NavigationInNormalWebView")){
			db.execSQL("ALTER TABLE MessagesV5 ADD NavigationInNormalWebView TEXT");
		}



		//R9

		db.execSQL(DB_CREATE_LOGIN);
		//db.execSQL("DROP TABLE IF EXISTS " + FavoriteStationsDatabaseService.DB_TABLE_STATION_FAVORITE );
		//db.execSQL("ALTER TABLE subscription_table ADD subscription_Connection_id TEXT");
		/*db.execSQL("ALTER TABLE station ADD Synoniem TEXT");
		db.execSQL("ALTER TABLE Orders ADD Refundable TEXT");	
		db.execSQL("ALTER TABLE Orders ADD Exchangeable TEXT");	
		db.execSQL("ALTER TABLE Orders ADD RulfillmentFailed TEXT");	
		db.execSQL("ALTER TABLE Orders ADD HasDuplicatedStationboard TEXT");
		db.execSQL("ALTER TABLE Orders ADD DuplicatedStationboardId TEXT");
		
		//db.execSQL("ALTER TABLE AllowedStationsForPickUp ADD StationName TEXT");	
		db.execSQL("ALTER TABLE Orders ADD SortFirstChildDepartureDate TEXT");	
		//db.execSQL("ALTER TABLE Orders ADD Email TEXT");	
		db.execSQL(DB_CREATE_OFFERQUERY);
		db.execSQL(DB_CREATE_MESSAGES);
		db.execSQL(DB_CREATE_STATIONBOARD);
		db.execSQL(DB_CREATE_STATIONBOARD_REALTIME);
		db.execSQL(DB_CREATE_FAVORITE_STATIONS);*/
		
		
		//db.execSQL("ALTER TABLE EventIDs ADD CityId TEXT");
		//db.execSQL("ALTER TABLE POIIDs ADD CityId TEXT");
		//db.execSQL("ALTER TABLE RestoIDs ADD CityId TEXT");
		//db.execSQL("ALTER TABLE Orders ADD Corrupted TEXT");	
		
		//ALTER TABLE GeneralSetting ADD DossierAftersalesLifetime TEXT
		/*db.execSQL("DROP TABLE IF EXISTS " + CurrencyDatabaseService.DB_TABLE_CURRENCY);
		db.execSQL("DROP TABLE IF EXISTS " + StationDatabaseService.DB_TABLE_STATION);		
		db.execSQL("DROP TABLE IF EXISTS " + CollectionItemDatabaseService.DB_TABLE_LANGUAGE);
		db.execSQL("DROP TABLE IF EXISTS " + CollectionItemDatabaseService.DB_TABLE_TITLE);
		db.execSQL("DROP TABLE IF EXISTS " + CollectionItemDatabaseService.DB_TABLE_COUNTRY);
		db.execSQL("DROP TABLE IF EXISTS " + CollectionItemDatabaseService.DB_TABLE_REDUCTION_CARD);
		db.execSQL("DROP TABLE IF EXISTS " + CollectionItemDatabaseService.DB_TABLE_PAYMENTMETHOD);
		db.execSQL("DROP TABLE IF EXISTS " + OriginDestinationRuleDatabaseService.DB_TABLE_STATIONMATRIX);
		db.execSQL("DROP TABLE IF EXISTS " + FavoriteDatabaseService.DB_TABLE_FAVORITE);	
		db.execSQL("DROP TABLE IF EXISTS " + AssistantDatabaseService.DB_TABLE_ORDERS);
		db.execSQL("DROP TABLE IF EXISTS " + CityDatabaseService.DB_TABLE_CITY);
		db.execSQL("DROP TABLE IF EXISTS " + DeliveryMethodDatabaseService.DB_TABLE_DELIVERY_METHODS);
		db.execSQL("DROP TABLE IF EXISTS " + DeliveryMethodDatabaseService.DB_TABLE_ALLOWEDSTATIONSFORPICKUP);
		db.execSQL("DROP TABLE IF EXISTS " + DeliveryMethodDatabaseService.DB_TABLE_ALLOWEDCOUNTRIESFORMAIL);
		db.execSQL("DROP TABLE IF EXISTS " + GeneralSettingDatabaseService.DB_GENERAL_SETTING);
		db.execSQL("DROP TABLE IF EXISTS " + FAQDatabaseService.DB_TABLE_FAQ );
		db.execSQL("DROP TABLE IF EXISTS " + CityDatabaseService.DB_TABLE_EVENTIDS );
		db.execSQL("DROP TABLE IF EXISTS " + CityDatabaseService.DB_TABLE_POIIDS );
		db.execSQL("DROP TABLE IF EXISTS " + CityDatabaseService.DB_TABLE_RESTOIDS );
		db.execSQL("DROP TABLE IF EXISTS " + ClickToCallScenarioDatabaseService.DB_TABLE_CLICK_TO_CALL_SCENARIO );
		db.execSQL("DROP TABLE IF EXISTS " + ClickToCallScenarioDatabaseService.DB_TABLE_PROVIDER_SETTING );*/
		//onCreate(db);
		//Preparing the database directories and file to be opened
        /*File databaseFile = context.getDatabasePath(DATABASE_NAME);
        databaseFile.mkdirs();
        databaseFile.delete();
        
        //Opening or Creating the database with our specified password
        db = SQLiteDatabase.openOrCreateDatabase(databaseFile, PASSWORD, null);*/
		//Log.d(tag, "onUpgrade......");
	}

	public synchronized SQLiteDatabase getWritableDatabase(){

		LogUtils.e("LogUtils", " getWritableDatabase from service, create database------>");
		//if (SQLiteDatabase == null) {
		try{
			if(SQLiteDatabase == null){
				SQLiteDatabase = this.getWritableDatabase(getDatabasePassword());
			}
		}catch (Exception e){
			try{
				LogUtils.e("LogUtils", " getWritableDatabase from service, create database Exception------>" + e.getMessage());
				SQLiteDatabase = this.getWritableDatabase("dhanmbs");
			}catch (Exception ee){
				LogUtils.e("LogUtils", " getWritableDatabase from service, create database Exception again------>" + e.getMessage());
				ee.printStackTrace();
			}
		}
		//}
		/*if(SQLiteDatabase == null){
			SQLiteDatabase = this.getWritableDatabase();
		}
*/
		return SQLiteDatabase;
	}
	public synchronized SQLiteDatabase getWritableDatabaseTest() throws Exception {

		/*if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			LogUtils.e("LogUtils", " No WRITE_EXTERNAL_STORAGE permission....");
			throw new Exception();
		}*/

		try{
			if(SQLiteDatabase == null){
				SQLiteDatabase = this.getWritableDatabase(getDatabasePassword());
			}
		}catch (Exception e){
			try{
				LogUtils.e("LogUtils", " getMessageResponse from service, create database Exception------>" + e.getMessage());
				SQLiteDatabase = this.getWritableDatabase("dhanmbs");
			}catch (Exception ee){
				LogUtils.e("LogUtils", " getMessageResponse from service, create database Exception again------>" + e.getMessage());
				throw new Exception();
			}
		}
		return SQLiteDatabase;
	}
	public String getDatabasePassword(){
		String deviceId = "dhanmbs";
		try {
			deviceId = DeviceUtil.getDeviceId(context);
		} catch (Exception e) {
			deviceId = "dhanmbs";
			e.printStackTrace();
		}
		if(deviceId == null){
			deviceId = "dhanmbs";
		}
		LogUtils.e("LogUtils", " DatabasePassword------>" + deviceId);
		//Log.e("DatabasePassword", "DatabasePassword..." + deviceId);
		return deviceId;
	}


	private boolean checkColumnExists(SQLiteDatabase db, String tableName
			, String columnName) {
		boolean result = false;
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("select * from sqlite_master where name = ? and sql like ?"
					, new String[]{tableName, "%" + columnName + "%"});
			result = null != cursor && cursor.moveToFirst();
		} catch (Exception e) {
			LogUtils.e("checkColumnExists","checkColumnExists2..." + e.getMessage()) ;
		} finally {
			if (null != cursor && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return result;

	}
}