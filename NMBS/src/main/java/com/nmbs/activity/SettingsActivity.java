package com.nmbs.activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.SwitchPreference;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.nmbs.R;
import com.nmbs.application.NMBSApplication;
import com.nmbs.async.CheckUpdateAsyncTask;
import com.nmbs.async.OrderAsyncTask;
import com.nmbs.dataaccess.database.CollectionItemDatabaseService;
import com.nmbs.dataaccess.restservice.IMasterDataService;
import com.nmbs.dataaccess.restservice.impl.MasterDataService;
import com.nmbs.exceptions.NetworkError;
import com.nmbs.log.LogUtils;
import com.nmbs.model.CheckAppUpdate;
import com.nmbs.model.CollectionItem;
import com.nmbs.model.Currency;
import com.nmbs.model.Order;
import com.nmbs.model.ReleaseNote;
import com.nmbs.services.IAssistantService;
import com.nmbs.services.ICheckUpdateService;
import com.nmbs.services.IMasterService;
import com.nmbs.services.IMessageService;
import com.nmbs.services.IOfferService;
import com.nmbs.services.ISettingService;
import com.nmbs.services.impl.AsyncDossierAfterSaleResponse;
import com.nmbs.services.impl.AsyncMasterDataResponse;
import com.nmbs.services.impl.ServiceConstant;
import com.nmbs.util.AlertDialogUtils;
import com.nmbs.util.AlertDialogUtils.IBackPressListener;
import com.nmbs.util.EncryptEditTextPreference;
import com.nmbs.util.LocaleChangedUtils;
import com.nmbs.util.NetworkUtils;
import com.nmbs.util.SharedPreferencesUtils;
import com.nmbs.util.Utils;

import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * This activity is set language page. For further, will be change this, this is
 * used for set NMBS application language
 * 
 * @author David.Shi
 * 
 */
public class SettingsActivity extends BasePreferenceActivity implements OnPreferenceChangeListener {

	private final static String TAG = SettingsActivity.class.getSimpleName();
	public static final String PREFS_LANGUAGE = "language"; // it is same as prefermence.xml
	public static final String PREFS_CURRENCY = "currency"; // it is same as prefermence.xml
	public static final String PREFS_FIRST_NAME = "firstname"; // it is same as prefermence.xml
	public static final String PREFS_LAST_NAME = "lastname"; // it is same as prefermence.xml
	public static final String PREFS_EMAIL = "email"; // it is same as prefermence.xml
	public static final String PREFS_EXTRA_EMAIL = "extraemail"; // it is same as prefermence.xml
	public static final String PREFS_PHONE_NUMBER = "phonenumber"; // it is same as prefermence.xml
	public static final String PREFS_GENDER = "salutation"; // it is same as prefermence.xml
	public static final String PREFS_SWITCHCHECK = "appUpdateSwitchButton"; // it is same as prefermence.xml
	public static final String PREFS_IS_LANGUAGE_FIRST_UPDATE = "is.language.first.update"; // 
	public static final String PREFS_CHECK_FOR_UPDATE_NOW = "checkForUpdate";
	public static final String PREFS_RATE = "rate";
	public static final String PREFS_CHECK_FOR_UPDATE_NOT_NOW_FLAG = "checkForUpdateNotNowFlag";
	public static final String PREFS_CHECK_FOR_UPDATE_NOT_NOW_TIME = "checkForUpdateNotNowTime";
	public static final String PREFS_CHECK_FOR_UPDATE_NOT_NOW_VERSION = "checkForUpdateNotNowVersion";
	public static final String PREFS_DELETE_PERSONINFO = "deleteButtons";
	public final static String TAB_NAME = "SETTINGS_MYACCOUNT";
	private ISettingService settingService;;
	private IMasterService masterService;
	private IOfferService offerService;
	private ProgressDialog progressDialog;
	private MyState myState;
	List<CollectionItem> listCollectionItemsForLanguage = null;
	List<CollectionItem> listCollectionItemsForGender = null;
	List<Currency> listCurrencies = null;
	private String selectedNewLanguage;//user select new language, store it.
	private String languageBeforSetting;//user update fail, re-store it.
	private ListPreference languagePref, currencyPref, genderPref;
	private EncryptEditTextPreference firstNameEditTextPreference, lastNameEditTextPreference, phoneNumberEditTextPreference, emailEditTextPreference;
	private Preference checkUpdateTextPreference;
	private Preference pfRate;
	private DeleteButtonPreference deletePersonInfoButton;
	private SwitchPreference automaticUpdateSwitchPreference;
	private CheckBoxPreference automaticUpdateCheckBoxPreference;
	private IAssistantService assistantService;
	private ICheckUpdateService checkUpdateService;
	private IMessageService messageService;
	private OrderAsyncTask orderAsyncTask;
	private LayoutInflater layoutInflater;
	private View convertView;
	private AlertDialog checkUpdateDialog = null;
	private int version;
	EditText editText;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
  
		LocaleChangedUtils.initLanguageSettings(getApplicationContext());//Setting application language	
		version = Utils.getAndroidSDKVersion();
		if(version > 13){
			addPreferencesFromResource(R.xml.preferences);
		}else{
			addPreferencesFromResource(R.xml.preferences_low_level);
		}

		LogUtils.d(TAG, "onCreate");
		if(version < 11){
			setTheme(R.style.SettingsThemeForVersion2X);
		}
		getListView().setCacheColorHint(getResources().getColor(R.color.transparent_background));
		//getListView().setBackgroundResource(R.drawable.ic_main_background_800);
				
		bindAllPreferences();
		
		// get masterService
		masterService = ((NMBSApplication) getApplication()).getMasterService();		
		settingService = ((NMBSApplication) getApplication()).getSettingService();
		offerService = ((NMBSApplication) getApplication()).getOfferService();
		assistantService = ((NMBSApplication) getApplication()).getAssistantService();
		checkUpdateService = ((NMBSApplication) getApplication()).getCheckUpdateService();
		messageService = ((NMBSApplication) getApplication()).getMessageService();
		fillLanguageAndCurrencyPrefs();

		addEventListeners();		
		
		languageBeforSetting = SharedPreferencesUtils.getSharedPreferencesByKey(PREFS_LANGUAGE, getApplicationContext());
		
	}

	public void showMessageOverLay(){
		/*if (!getForeground()) {
			MobileMessageResponse mobileMessageResponse = messageService
					.getMobileMessagesByAutoOverlayDisplayLocation(TAB_NAME);
			boolean isShowed = messageService.isShowOverLay(TAB_NAME,mobileMessageResponse);
			if (isShowed == false) {
				if (mobileMessageResponse != null) {
					startActivity(MessageWizardActivity.createIntent(
							getApplicationContext(), mobileMessageResponse,
							TAB_NAME));
				}
			}
			//this.isShowed = true;
		}*/
	}
	
	private void bindSummary() throws Exception{
		
		for (CollectionItem item : listCollectionItemsForLanguage){
			if (item != null && StringUtils.equalsIgnoreCase(item.getKey(), languagePref.getValue())) {
				languagePref.setSummary(item.getLable());
			}
		}
		
		bindCurrency();
		for (CollectionItem item : listCollectionItemsForGender){
			//Log.d(TAG, "CurrencyCode: " + item.getKey());
			if (item != null && StringUtils.equalsIgnoreCase(item.getKey(), genderPref.getValue())) {
				genderPref.setSummary(item.getLable());
			}
		}		
		setNameEditTextPreference(firstNameEditTextPreference, PREFS_FIRST_NAME, getString(R.string.setting_view_first_name_alert));
		setNameEditTextPreference(lastNameEditTextPreference, PREFS_LAST_NAME, getString(R.string.setting_view_last_name_alert));
		setPhoneNumberEditTextPreference();
		setEmailEditTextPreference();
	}
	private void bindCurrency(){
		/*Currency currency = settingService.getUserPreferedCurrency(masterService);
		if (currency != null) {
			currencyPref.setSummary(currency.getCurrencyName());
			if (!StringUtils.equalsIgnoreCase("EUR", currency.getCurrencyCode())) {
				//Log.d(TAG, "CurrencyCode: " + currency.getCurrencyName());
				currencyPref.setSummary(currency.getCurrencyName() + "\n\n" + getString(R.string.setting_view_info));
			}
		}	*/
	}
	private void setNameEditTextPreference(
			EditTextPreference editTextPreference, String key, String defSummary)
			throws Exception {
		String summaryOfNameEditTextPreference = SharedPreferencesUtils
				.getSharedPreferencesByKey(key, this.getApplicationContext());
		if (StringUtils.equalsIgnoreCase("", summaryOfNameEditTextPreference)) {
			editTextPreference.setSummary(defSummary);
		} else {
			editTextPreference.setSummary(summaryOfNameEditTextPreference);
			editTextPreference.setText(summaryOfNameEditTextPreference);
		}
	}

	private void setPhoneNumberEditTextPreference() throws Exception {
		String summaryOfPhoneNumberEditTextPreference = SharedPreferencesUtils
				.getSharedPreferencesByKey(PREFS_PHONE_NUMBER,
						this.getApplicationContext());
		if (StringUtils.equalsIgnoreCase("",
				summaryOfPhoneNumberEditTextPreference)) {
			phoneNumberEditTextPreference
					.setSummary(getString(R.string.setting_view__mobile_number_alert));
		} else {
			phoneNumberEditTextPreference
					.setSummary(summaryOfPhoneNumberEditTextPreference);
			phoneNumberEditTextPreference
					.setText(summaryOfPhoneNumberEditTextPreference);
		}
	}

	private void setEmailEditTextPreference() throws Exception {
		String summaryOfEmailEditTextPreference = SharedPreferencesUtils
				.getSharedPreferencesByKey(PREFS_EMAIL,
						this.getApplicationContext());
//		Log.i("TAG", summaryOfEmailEditTextPreference);
		if (StringUtils.equalsIgnoreCase("", summaryOfEmailEditTextPreference)) {
			emailEditTextPreference
					.setSummary(getString(R.string.setting_view_email_alert));
		} else {
			emailEditTextPreference
					.setSummary(summaryOfEmailEditTextPreference);
			emailEditTextPreference.setText(summaryOfEmailEditTextPreference);
		}
	}
	private OnPreferenceClickListener textPreferenceClickListner = new OnPreferenceClickListener() {

		public boolean onPreferenceClick(Preference arg0) {
			showWaitDialog();
			checkUpdateService.setCheckAppManually(true);
			CheckUpdateAsyncTask checkUpdateAsyncTask = new CheckUpdateAsyncTask(checkUpdateHandler,checkUpdateService);
			checkUpdateAsyncTask.execute(settingService);
			return false;
		}

	};
	
	private OnPreferenceClickListener ratePreferenceClickListner = new OnPreferenceClickListener() {

		public boolean onPreferenceClick(Preference arg0) {
			Intent intent = new Intent();
        	intent.setAction(Intent.ACTION_VIEW);
        	//intent.setData(Uri.parse("market://details?id=" + context.getPackageName()));
        	intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
        	startActivity(intent);
			return false;
		}

	};
	public void showCheckAppResponse() {
		final CheckAppUpdate checkAppUpdate = checkUpdateService
				.getCheckAppUpdate();
		if(checkAppUpdate.isUpToDate()){
			Toast.makeText(SettingsActivity.this, getString(R.string.checkappversion_alert_no_update), Toast.LENGTH_LONG).show();
		}else{
			String description = "";
			if (checkAppUpdate != null) {
				for (ReleaseNote releaseNote : checkAppUpdate.getReleaseNotes()) {
					description += releaseNote.getDescription().replaceAll("\\n", "<br />") + "<br />";
				}
				description = Html.fromHtml(description)+"";
			}
			checkUpdateDialog = AlertDialogUtils.createCheckUpdateDialog(
					getString(R.string.checkappversion_alert_title),
					description, SettingsActivity.this,
					new View.OnClickListener() {
						public void onClick(View v) {
							SharedPreferencesUtils.storeSharedPreferences(
									PREFS_CHECK_FOR_UPDATE_NOT_NOW_FLAG, true,
									SettingsActivity.this);
							SharedPreferencesUtils
									.storeAppInfoSharedPreferences(
											PREFS_CHECK_FOR_UPDATE_NOT_NOW_TIME,
											new SimpleDateFormat(
													"yyyy-MM-dd HH:mm:ss")
													.format(new Date()),
											SettingsActivity.this);
							SharedPreferencesUtils.storeAppInfoSharedPreferences(
									PREFS_CHECK_FOR_UPDATE_NOT_NOW_VERSION,
									checkAppUpdate.getVersion(),
									SettingsActivity.this);
							checkUpdateDialog.dismiss();
						}

					}, new View.OnClickListener() {

						public void onClick(View v) {
							checkUpdateDialog.dismiss();
							try {
								Intent intent = new Intent(Intent.ACTION_VIEW);
								intent.setData(Uri
										.parse("market://details?id=com.nmbs"));
								startActivity(intent);
							} catch (Exception e) {
								// google play app is not installed
								Intent intent = new Intent(Intent.ACTION_VIEW);
								intent.setData(Uri
										.parse("https://play.google.com/store/apps/details?id=com.nmbs"));
								startActivity(intent);
							}
						}

					},new IBackPressListener(){
						public void onBackPress() {
							SharedPreferencesUtils
							.storeSharedPreferences(
									SettingsActivity.PREFS_CHECK_FOR_UPDATE_NOT_NOW_FLAG,
									true, SettingsActivity.this);
					SharedPreferencesUtils
							.storeAppInfoSharedPreferences(
									SettingsActivity.PREFS_CHECK_FOR_UPDATE_NOT_NOW_TIME,
									new SimpleDateFormat(
											"yyyy-MM-dd HH:mm:ss")
											.format(new Date()),
											SettingsActivity.this);
					SharedPreferencesUtils
							.storeSharedPreferences(
									SettingsActivity.PREFS_CHECK_FOR_UPDATE_NOT_NOW_VERSION,
									checkAppUpdate.getVersion(),
									SettingsActivity.this);
						}
					});
			checkUpdateDialog.setCanceledOnTouchOutside(false);
			checkUpdateDialog.show();
		}
	}
	
	private OnPreferenceChangeListener editTextPreferenceChangeListener = new OnPreferenceChangeListener() {

		public boolean onPreferenceChange(Preference preference, Object newValue) {

			if (preference.getKey().equals(PREFS_FIRST_NAME)) {

				if (((String) newValue).length() < 2
						|| ((String) newValue).length() > 15) {
					Toast.makeText(
							SettingsActivity.this,
							getString(R.string.alert_general_error_first_name_invalid),
							Toast.LENGTH_SHORT).show();
					return false;
				} else {
					if (StringUtils.equalsIgnoreCase((String) newValue, "")) {
						preference
								.setSummary(getString(R.string.setting_view_first_name_alert));
					} else {
						preference.setSummary((String) newValue);
						SharedPreferencesUtils.storeSharedPreferences(
								PREFS_FIRST_NAME, (String) newValue,
								getApplicationContext());
					}
					return false;
				}
			}

			if (preference.getKey().equals(PREFS_LAST_NAME)) {
				if (((String) newValue).length() < 2
						|| ((String) newValue).length() > 30) {
					Toast.makeText(
							SettingsActivity.this,
							getString(R.string.alert_general_error_last_name_invalid),
							Toast.LENGTH_SHORT).show();
					return false;
				} else {
					if (StringUtils.equalsIgnoreCase((String) newValue, "")) {
						preference
								.setSummary(getString(R.string.setting_view_last_name_alert));
					} else {
						preference.setSummary((String) newValue);
						SharedPreferencesUtils.storeSharedPreferences(
								PREFS_LAST_NAME, (String) newValue,
								getApplicationContext());
					}
					return false;
				}
			}
			if (preference.getKey().equals(PREFS_EMAIL)) {
				if (!Utils.checkEmailPattern((String) newValue)) {
					Toast.makeText(
							SettingsActivity.this,
							getString(R.string.alert_register_error_email_wrong),
							Toast.LENGTH_SHORT).show();

					return false;
				} else {
					if (StringUtils.equalsIgnoreCase((String) newValue, "")) {
						preference
								.setSummary(getString(R.string.setting_view_email_alert));
					} else {
						preference.setSummary((String) newValue);
						SharedPreferencesUtils.storeSharedPreferences(
								PREFS_EMAIL, (String) newValue,
								getApplicationContext());
					}

					return false;
				}
			}

			if (preference.getKey().equals(PREFS_PHONE_NUMBER)) {

				if (((String) newValue).length() > 0 && ((String) newValue).length() < 5
						&& !StringUtils.equalsIgnoreCase(((String) newValue),
								"0032")) {
					Toast.makeText(SettingsActivity.this,
							getString(R.string.alert_phonenumber_incorrect),
							Toast.LENGTH_SHORT).show();
					return false;
				} else {
					if (StringUtils.equalsIgnoreCase((String) newValue, "")) {
						preference
								.setSummary(getString(R.string.setting_view__mobile_number_alert));
					} else {
						preference.setSummary((String) newValue);
						SharedPreferencesUtils.storeSharedPreferences(
								PREFS_PHONE_NUMBER, (String) newValue,
								getApplicationContext());
					}

					return false;
				}

			}
			return false;
		}
	};
	
	// Bind all view elements
	@SuppressWarnings("deprecation")
	private void bindAllPreferences(){
		
		firstNameEditTextPreference = (EncryptEditTextPreference)findPreference(PREFS_FIRST_NAME);
		lastNameEditTextPreference = (EncryptEditTextPreference)findPreference(PREFS_LAST_NAME);
		phoneNumberEditTextPreference = (EncryptEditTextPreference)findPreference(PREFS_PHONE_NUMBER);
		checkUpdateTextPreference = findPreference(PREFS_CHECK_FOR_UPDATE_NOW);
		
		pfRate = findPreference(PREFS_RATE);
		if(version > 13){
			automaticUpdateSwitchPreference = (SwitchPreference)findPreference(PREFS_SWITCHCHECK);
		}else{
			automaticUpdateCheckBoxPreference = (CheckBoxPreference) findPreference(PREFS_SWITCHCHECK);
		}
		phoneNumberEditTextPreference.setDialogMessage(getString(R.string.settings_phonenumber_info));
		String phoneNum = SharedPreferencesUtils.getSharedPreferencesByKey(SettingsActivity.PREFS_PHONE_NUMBER, this.getApplicationContext());
		if (StringUtils.equalsIgnoreCase("", phoneNum)) {
			phoneNumberEditTextPreference.setText("0032");
		}				
		layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = layoutInflater.inflate(R.layout.custom_dialog, null);
		editText = (EditText)convertView.findViewById(R.id.editText1);
		
		
		emailEditTextPreference = (EncryptEditTextPreference)findPreference(PREFS_EMAIL);
		deletePersonInfoButton = (DeleteButtonPreference) findPreference(PREFS_DELETE_PERSONINFO);
		firstNameEditTextPreference.setOnPreferenceChangeListener(editTextPreferenceChangeListener);
		lastNameEditTextPreference.setOnPreferenceChangeListener(editTextPreferenceChangeListener);
		phoneNumberEditTextPreference.setOnPreferenceChangeListener(editTextPreferenceChangeListener);
		emailEditTextPreference.setOnPreferenceChangeListener(editTextPreferenceChangeListener);
		
		languagePref = (ListPreference) findPreference(PREFS_LANGUAGE);
		if (languagePref == null){
			throw new NullPointerException("Language Preference cannot be null");
		}
		currencyPref = (ListPreference) findPreference(PREFS_CURRENCY);
		if (currencyPref == null) {
			throw new NullPointerException("Currency Preference cannot be null");
		}	
		genderPref = (ListPreference) findPreference(PREFS_GENDER);
		if (genderPref == null){
			throw new NullPointerException("Gender Preference cannot be null");
		}

		// add button attribute info
		deletePersonInfoButton.setButtonInfo(SettingsActivity.this,firstNameEditTextPreference,lastNameEditTextPreference,phoneNumberEditTextPreference,emailEditTextPreference,genderPref);
		

	}
	
	// Bind all Listeners
	private void addEventListeners() {
		languagePref.setOnPreferenceChangeListener(this);
		currencyPref.setOnPreferenceChangeListener(this);
		genderPref.setOnPreferenceChangeListener(this);
		checkUpdateTextPreference.setOnPreferenceClickListener(textPreferenceClickListner);
		pfRate.setOnPreferenceClickListener(ratePreferenceClickListner);
		
	}
	
	private void fillLanguageAndCurrencyPrefs(){
		new LoadMyCollectionServiceAsyncTask().execute(masterService);
	}


	public boolean onPreferenceChange(Preference preference, Object newValue) {
		
		//Log.d(TAG, "User will update data in: " + newValue);
		/*if (preference.getKey().equals(PREFS_CURRENCY)) {
			preference.setSummary((CharSequence) newValue);
		}*/
		
		if (preference.getKey().equals(PREFS_LANGUAGE)) {
			LogUtils.d(TAG, "User will update data in: " + newValue);

			selectedNewLanguage = (String) newValue;
			
			if (languageBeforSetting != null && languageBeforSetting.equals(selectedNewLanguage)) {
				return true;
			}
			//settingService.getCurrentLanguage();
			 // Stored the select language, after refresh fail, change it to 'languageBeforSetting'
			
			//startRefresh(); // refresh data
			startRefresh();
			
			return false;
		}	
		
		if (preference.getKey().equals(PREFS_CURRENCY)) {
			//Log.d(TAG, "User changed currency: " + newValue);
			offerService.getOfferQuery().setPreferredCurrency((String)newValue);
			for (Currency currency : listCurrencies) {
				//Log.d(TAG, "CurrencyCode: " + currency.getCurrencyCode());
				if (currency != null && StringUtils.equalsIgnoreCase(currency.getCurrencyCode(), (String)newValue)) {
					preference.setSummary(currency.getCurrencyName());
					//bindCurrency();
					if (!StringUtils.equalsIgnoreCase("EUR", currency.getCurrencyCode())) {
						//Log.d(TAG, "CurrencyCode: " + currency.getCurrencyName());
						currencyPref.setSummary(currency.getCurrencyName() + "\n\n" + getString(R.string.setting_view_info));
					}

				}
			}
			
		}

		if (preference.getKey().equals(PREFS_GENDER)) {
			//Log.d(TAG, "User changed currency: " + newValue);
			
			for (CollectionItem item : listCollectionItemsForGender){
				//Log.d(TAG, "CurrencyCode: " + item.getKey());
				if (item != null && StringUtils.equalsIgnoreCase(item.getKey(), (String)newValue)) {
					preference.setSummary(item.getLable());
				}
			}
			
		}
		return true;
	}
	
	/**
	 * Utility method for every Activity who needs to start this Activity.
	 * 
	 * This keeps the construction of the Intent and the corresponding
	 * parameters also in this class.
	 * 
	 * @param context
	 */
	public static void showHomeSettings(Context context) {
		Intent intent = new Intent(context, SettingsActivity.class);
		context.startActivity(intent);
	}
	
	private void setLanguagePref() {
		try {
			bindSummary();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CharSequence[] entries = new CharSequence[listCollectionItemsForLanguage.size()];
		CharSequence[] entrieValues = new CharSequence[listCollectionItemsForLanguage.size()];						
		int i = 0;
		for (CollectionItem item : listCollectionItemsForLanguage){
			//Log.d(TAG, "LanguageCode: " + item.getKey());
			//Log.d(TAG, "LanguageLable: " + item.getLable());
			// entries used for displaying, will be change to 'item.getLable() = country name';
			entries[i] = item.getLable();
			entrieValues[i] = item.getKey();// used for committing to preference
			i++;
		}
		//Log.d(TAG,"languagePref filled with " + listCollectionItemsForLanguage.size() + " items.");
		languagePref.setEntries(entries);
		languagePref.setEntryValues(entrieValues);
	}
	
	private void setCurrencyPref(){
		CharSequence[] entryValues = new CharSequence[listCurrencies.size()];
		CharSequence[] entries = new CharSequence[listCurrencies.size()];
		int i = 0;
		for (Currency currency : listCurrencies) {
			//Log.d(TAG, "CurrencyCode: " + currency.getCurrencyCode());
			entryValues[i] = currency.getCurrencyName();
			entries[i] = currency.getCurrencyCode();
			i++;
		}
		//Log.d(TAG, "currencyPref filled with " + listCurrencies.size()+ " items.");
		currencyPref.setEntries(entryValues);
		currencyPref.setEntryValues(entries);
	}
	
	private void setGenderPref(){
		CharSequence[] entries = new CharSequence[listCollectionItemsForGender.size()];
		CharSequence[] entrieValues = new CharSequence[listCollectionItemsForGender.size()];						
		int i = 0;
		for (CollectionItem item : listCollectionItemsForGender){
			entries[i] = item.getLable();
			entrieValues[i] = item.getKey();// used for committing to preference
			i++;
		}
		//Log.d(TAG,"genderPref filled with " + listCollectionItemsForGender.size() + " items.");
		genderPref.setEntries(entries);
		genderPref.setEntryValues(entrieValues);
	}
	
	/**
	 * AsyncTask class For performing AsyncTask of operating SQLite. 
	 */
	private class LoadMyCollectionServiceAsyncTask extends AsyncTask<IMasterService, Void, MyCollection> {
		private IMasterService iMService;
		private ExceptionReceiver exceptionReceiver;
		MyCollection mCollection = null;		
		@Override
		protected void onPreExecute() {			
			showWaitDialog();// when the task start, show a progress dialog.
			
		}
		@Override
		protected MyCollection doInBackground(IMasterService... params) {
			iMService = params[0];
			try {
				listCollectionItemsForLanguage = iMService.loadCollectionResponse(CollectionItemDatabaseService.DB_TABLE_LANGUAGE);
				listCurrencies = iMService.loadCurrencyCollection();
				listCollectionItemsForGender = iMService.loadCollectionResponse(CollectionItemDatabaseService.DB_TABLE_TITLE);
				//new AssistantService(getApplicationContext()).searchDossierAfterSale("NDJOYWR", settingService);
			} catch (Exception e) {
				e.printStackTrace();
				exceptionReceiver = new ExceptionReceiver();
				exceptionReceiver.exceptionLoadCollection = e;
				throw new RuntimeException(e);
			}
			mCollection = new MyCollection(listCollectionItemsForLanguage, listCurrencies, listCollectionItemsForGender);

			return mCollection;
		}
		private void handleResult(MyCollection result) {
			//Ready data for CollectionItem Language
			listCollectionItemsForLanguage = result.getListCollectionItemsLanguage();
			if (listCollectionItemsForLanguage != null) {								
				setLanguagePref();
			} else {
				Toast.makeText(SettingsActivity.this, getString(R.string.general_server_unavailable), Toast.LENGTH_SHORT).show();
			}			
			//Ready data for Currency
			listCurrencies = result.getListCurrencies();
			if (listCurrencies != null) {
				setCurrencyPref();
			} else {
				Toast.makeText(SettingsActivity.this, getString(R.string.general_server_unavailable), Toast.LENGTH_SHORT).show();
			}	
			//Ready data for CollectionItem Gender
			listCollectionItemsForGender = result.getListCollectionItemsGender();
			if (listCollectionItemsForGender != null) {								
				setGenderPref();
			} else {
				Toast.makeText(SettingsActivity.this, getString(R.string.general_server_unavailable), Toast.LENGTH_SHORT).show();
			}
		}
		@Override
		protected void onPostExecute(MyCollection result) {
			hideWaitDialog();
			if (exceptionReceiver != null && exceptionReceiver.exceptionLoadCollection != null) {
				Toast.makeText(SettingsActivity.this, getString(R.string.general_server_unavailable), Toast.LENGTH_SHORT).show();
			} else {
				if (result != null) {
					handleResult(result);
				}
			}
		}
	}

	private class ExceptionReceiver {
		private Exception exceptionLoadCollection;
	}


	private void relaunchActivity(){
		//Log.e("restartActivity", "restartActivity......");
		// after save the select language , should start activity again, (otherwise tab-host language will not be changed)
		Intent intent = getIntent();
		finish();
		startActivity(intent);
		//finish();
	   /* Intent myIntent = new Intent(SettingsActivity.this.getApplicationContext(), Main.class);
	    NMBSApplication.tabSpec = 4;
	    myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
	    startActivity(myIntent);*/
	}
	

	/////////////////////////////////////////////////////////////////////////////////////////
	//Ready to download data from server
	/////////////////////////////////////////////////////////////////////////////////////////	
	
	// refresh Master Data
	private void refreshMasterData(){
		//Log.e(TAG, "refreshMasterData  -  refresh......");
		showWaitDialog();
		masterService.cleanLastModifiedTime();
		IMasterDataService masterDataService = new MasterDataService();
		masterDataService.storeDefaultData(getApplicationContext(), true);
		myState.asyncMasterDataResponse = masterService.getMasterData(settingService, false);
		myState.asyncMasterDataResponse.registerHandler(mHandler);
		myState.isSearchOngoing = true;		
		myState.registerHandler(mHandler);
		
		
	
	}
	
	private void getDossierData(boolean hasConnection){
		
		List<Order> orders = assistantService.uniteOrders(assistantService.getOrderList(), assistantService.getOrderHistoryList(), assistantService.getOrderCanceledList());
		myState.asyncDossierAfterSaleResponse = assistantService.searchDossierAfterSale(null, orders, settingService, hasConnection, true);
		myState.asyncDossierAfterSaleResponse.registerHandler(handler);		
		myState.registerHandler(handler);
		showWaitDialog();
	}
	// show progressDialog.
	private void showWaitDialog() {
		runOnUiThread(new Runnable() {
			   public void run() {
				   if(progressDialog == null){
						//Log.e(TAG, "Show Wait Dialog....");
						progressDialog = ProgressDialog.show(SettingsActivity.this,
								getString(R.string.alert_data_sending_to_server),
								getString(R.string.alert_waiting),
								true);
					}
			   }
			});
		
		
	}
	//hide progressDialog
	private void hideWaitDialog() {
		
		runOnUiThread(new Runnable() {
			   public void run() {
				   //Log.e(TAG, "Hide Wait Dialog....");
					if (progressDialog != null) {						
						//Log.e(TAG, "progressDialog is.... " + progressDialog);
						progressDialog.dismiss();
						progressDialog = null;
					}
			   }
			});
		
	}
	
	private void masterDatarRsponseReceived() {
		//Log.e(TAG, "responseReceived....");
		//if (assistantService.hasOrder()) {
		//	getDossierData(true);
		//}else {
			SharedPreferencesUtils.storeSharedPreferences(SettingsActivity.PREFS_LANGUAGE, selectedNewLanguage,getApplicationContext());
	    	//Log.d(TAG, "handler receive: successfully");
	    	LocaleChangedUtils.updateConfigurationLocale(getApplicationContext(), selectedNewLanguage);        
			relaunchActivity();//restart activity for init new language
		//}
		recycle();
		hideWaitDialog();
		
	}


	
	private void recycle(){
		
		System.gc();
		myState.unRegisterHandler();
		myState.asyncMasterDataResponse = null;
		myState.isSearchOngoing = false;	
	}

	//Handler which is called when the app is refreshed.
	// The what parameter of the message decides if there was an error or not.
    private Handler mHandler = new Handler(){   
        public void handleMessage(Message msg) {   
			switch (msg.what) {   
            case ServiceConstant.MESSAGE_WHAT_OK:
            	masterDatarRsponseReceived();    // received response successfully   

                break;    
			case ServiceConstant.MESSAGE_WHAT_ERROR:
				masterDatarRsponseReceived();
				/*Toast.makeText(SettingsActivity.this,getString(R.string.setting_error_service_not_available), Toast.LENGTH_LONG) .show();
				//Re-store the language when error.
				SharedPreferencesUtils.storeSharedPreferences(PREFS_LANGUAGE, languageBeforSetting, SettingsActivity.this.getApplicationContext());  
				hideWaitDialog();
				recycle();*/
				/*Bundle bundle = msg.getData();
				NetworkError error=(NetworkError) bundle.getSerializable(ServiceConstant.PARAM_OUT_ERROR);
				switch (error) {
				case TIMEOUT:
					masterDatarRsponseReceived();
					//Log.e(TAG, "handler receive: TIMEOUT");
					break;
				}
*/
				//Log.d(TAG, "handler receive");
				break;
			}
		};
    };

	// private class which contains all fields and objects who need to be kept
	// accross Activity re-creates (configuration changes).
	private class MyState {
		public AsyncMasterDataResponse asyncMasterDataResponse;
		public AsyncDossierAfterSaleResponse asyncDossierAfterSaleResponse;
		public boolean isSearchOngoing;

		// private boolean isWantedRefreshed;
		public void unRegisterHandler() {
			if (asyncMasterDataResponse != null) {
				asyncMasterDataResponse.unregisterHandler();
			}
		}

		public void registerHandler(Handler handler) {
			if (asyncMasterDataResponse != null) {
				asyncMasterDataResponse.registerHandler(handler);
			}
		}
	}

	// Maintain the myState object over configuration changes
	public Object onRetainNonConfigurationInstance() {
		//Log.d(TAG, "onRetainNonConfigurationInstance");
		return myState;
	}

	@Override
	protected void onPause() {
		//Log.d(TAG, "onPause");
		if (myState != null) {
			myState.unRegisterHandler();
		}
		hideWaitDialog();
		super.onPause();
	}

	@Override
	protected void onResume() {
		//Log.d(TAG, "onResume");
		//Main.tabSpec = 4;
		if(checkUpdateDialog!=null)
		checkUpdateDialog.dismiss();
		reEnableState();
		super.onResume();
		showMessageOverLay();
	}

	// when the activity run into onResume, continue to received data
	private void reEnableState() {
		if (myState == null) {
			myState = new MyState();
		}
		if (myState != null && myState.isSearchOngoing) {
			//Log.d(TAG, "isSearchOngoing...");
			if (myState.asyncMasterDataResponse.isReceiveResponse()) {
				masterDatarRsponseReceived();
			} else {
				myState.registerHandler(mHandler);
				showWaitDialog();
				
			}
			return;
		} else {
			hideWaitDialog();
		}
	}

	
	// Ready to start refreshing master data
	private void startRefresh() {
		int connectionStatus = NetworkUtils.getNetworkState(SettingsActivity.this.getApplicationContext());
		if (connectionStatus > 0) {
			 // Stored the select language, after refresh fail, change it to 'languageBeforSetting'
			offerService.clearOfferQuery();
			offerService.deleteLastOfferQuery();
			if (assistantService.hasOrder()) {
				showHasOrderAlertDialog();
			}else {
				SharedPreferencesUtils.storeSharedPreferences(SettingsActivity.PREFS_LANGUAGE, selectedNewLanguage,this);
				loadData();
			}
			
		} else {
			showNoNetWorkAlertDialog();
			//Toast.makeText(SettingsActivity.this, getString(R.string.alert_no_network), Toast.LENGTH_LONG).show();
		}
	}

	
    private void showHasOrderAlertDialog(){
    	
    	final AlertDialog.Builder builder = new AlertDialog.Builder(this);					 
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setMessage(getString(R.string.alert_refresh_stored_tickets));
         
         builder.setPositiveButton(getString(R.string.alert_continue), new DialogInterface.OnClickListener() {						
			public void onClick(DialogInterface dialog, int which) {
				SharedPreferencesUtils.storeSharedPreferences(SettingsActivity.PREFS_LANGUAGE, selectedNewLanguage,getApplicationContext());
				//offerService.clearOfferQuery();
				//offerService.deleteLastOfferQuery();
				//refreshMasterData();				
				getDossierData(true);
			}
		});
         builder.setNegativeButton(getString(R.string.general_cancel), new DialogInterface.OnClickListener() {						
			public void onClick(DialogInterface dialog, int which) {
				//finish();		
			}
		});
         builder.show();
    }
    
    private void showNoNetWorkAlertDialog(){
    	
    	final AlertDialog.Builder builder = new AlertDialog.Builder(this);					 
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setMessage(getString(R.string.alert_refresh_tickets_manually));
         
         builder.setPositiveButton(getString(R.string.general_ok), new DialogInterface.OnClickListener() {						
			public void onClick(DialogInterface dialog, int which) {
									
			}
		});

         builder.show();
    }
	
	/**
	 * Used for in AsyncTask
	 */
	private class MyCollection{		
		List<CollectionItem> mListCollectionItemsLangauge;
		List<CollectionItem> mListCollectionItemsGender;
		List<Currency> mListCurrencies;		
		public List<CollectionItem> getListCollectionItemsLanguage() {
			return mListCollectionItemsLangauge;
		}
		public List<Currency> getListCurrencies() {
			return mListCurrencies;
		}	
		public List<CollectionItem> getListCollectionItemsGender() {
			return mListCollectionItemsGender;
		}
		public MyCollection(List<CollectionItem> listCollectionItemsLanguage, List<Currency> listCurrencies ,List<CollectionItem> listCollectionItemsGender){
			this.mListCollectionItemsLangauge = listCollectionItemsLanguage;
			this.mListCurrencies = listCurrencies;
			this.mListCollectionItemsGender = listCollectionItemsGender;
		}	
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Check if the key event was the BACK key and if there's history
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			// webView.goBack();
			NMBSApplication.tabSpec = 0;
			//Log.d(TAG, "onKeyDown.. ");
			// return true;
			return false;
		}
		// If it wasn't the BACK key or there's no web page history, bubble up
		// to the default
		// system behavior (probably exit the activity)
		return true;
	}
	   
		private void loadData(){
			orderAsyncTask = new OrderAsyncTask(orderHandler, MyTicketsActivity.FLAG_FIND_ORDER, masterService);
			orderAsyncTask.execute(assistantService);
			//showWaitDialog();
			
		}
		
		private Handler orderHandler = new Handler(){   
	        public void handleMessage(Message msg) {   
				switch (msg.what) {   
	            case ServiceConstant.MESSAGE_WHAT_OK:
	            	
	            	//reEnableState();
	            	asyncTaskFinished();
	            	//Log.d(TAG, "handler receive: Success!");   
	                break;    
				}
			};
	    };
	    
	    private void asyncTaskFinished(){
	    	hideWaitDialog();
	    	if (assistantService.hasOrder()) {
				showHasOrderAlertDialog();
			}else {			
				MobileMessageAsyncTask mobileMessageAsyncTask = new MobileMessageAsyncTask();
        		mobileMessageAsyncTask.execute((Void)null);	
				//refreshMasterData();
			}
	    }
	    
	    
	    private Handler handler = new Handler(){   
	        public void handleMessage(Message msg) {   
				switch (msg.what) {   
	            case ServiceConstant.MESSAGE_WHAT_OK:
	            	//responseReceived(myState.asyncDossierAfterSaleResponse.getOrders());                 	
	            	// bind listeners and set view     
	            	//refreshMasterData();
	            	MobileMessageAsyncTask mobileMessageAsyncTask = new MobileMessageAsyncTask();
	        		mobileMessageAsyncTask.execute((Void)null);	
	            	break;    
				case ServiceConstant.MESSAGE_WHAT_ERROR:
					
					Bundle bundle = msg.getData();
					NetworkError error=(NetworkError) bundle.getSerializable(ServiceConstant.PARAM_OUT_ERROR);
									
					switch (error) {
					case TIMEOUT:
						
					}
					
					//responseReceived(myState.asyncDossierAfterSaleResponse.getOrders());   
					Toast.makeText(SettingsActivity.this,getString(R.string.setting_error_service_not_available), Toast.LENGTH_LONG) .show();
					//Re-store the language when error.
					SharedPreferencesUtils.storeSharedPreferences(PREFS_LANGUAGE, languageBeforSetting, SettingsActivity.this.getApplicationContext());  
					
					//hideWaitDialog();
					recycle();
					//Log.d(TAG, "handler receive");
					break;
				}
			};
	    };
	    
		// receive right information, change myState status and hide progressDialog.
		private void responseReceived(List<Order> orders) {
			//Log.e(TAG, "responseReceived....");		
			SharedPreferencesUtils.storeSharedPreferences(SettingsActivity.PREFS_LANGUAGE, selectedNewLanguage,getApplicationContext());
	    	//Log.d(TAG, "handler receive: successfully");
	    	LocaleChangedUtils.updateConfigurationLocale(getApplicationContext(), selectedNewLanguage);        
			relaunchActivity();//restart activity for init new language
			hideWaitDialog();
			//Log.d(TAG, "show response");
		}
		
		private Handler checkUpdateHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case ServiceConstant.MESSAGE_WHAT_OK:
					hideWaitDialog();
					try {
						showCheckAppResponse();
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case ServiceConstant.MESSAGE_WHAT_ERROR:
					hideWaitDialog();
					Bundle bundle = msg.getData();
					NetworkError error = (NetworkError) bundle
							.getSerializable(ServiceConstant.PARAM_OUT_ERROR);

					switch (error) {
					case CONNECTION:
						hideWaitDialog();
						//showCheckAppResponse();
						Toast.makeText(SettingsActivity.this,getString(R.string.setting_error_service_not_available), Toast.LENGTH_LONG).show();
						break;
					case TIMEOUT:
						hideWaitDialog();
						break;
					}

					break;
				}
			};
		};
		
		public class MobileMessageAsyncTask extends AsyncTask<Void, Void, Void>{    	
			private boolean hasError;
			@Override
			protected void onPreExecute() {
				showWaitDialog();
				super.onPreExecute();
			}
			
			@Override
			protected Void doInBackground(Void... params) {

				try {					
					//Thread.sleep(15000);
					messageService.getMessageData(settingService.getCurrentLanguagesKey(), true);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}		
			@Override
			protected void onPostExecute(Void result) {	
				if (hasError) {
					Toast.makeText(SettingsActivity.this,getString(R.string.setting_error_service_not_available), Toast.LENGTH_LONG) .show();
					//Re-store the language when error.
					SharedPreferencesUtils.storeSharedPreferences(PREFS_LANGUAGE, languageBeforSetting, SettingsActivity.this.getApplicationContext());  
					
					//hideWaitDialog();
					recycle();
				}else {
					refreshMasterData();
				}
				super.onPostExecute(result);
			}	
		}
}
