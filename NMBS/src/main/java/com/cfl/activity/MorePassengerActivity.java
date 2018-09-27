package com.cfl.activity;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cfl.R;


import com.cfl.application.NMBSApplication;
import com.cfl.exceptions.NetworkError;
import com.cfl.model.ClickToCallContext;
import com.cfl.model.ClickToCallParameter;
import com.cfl.model.ClickToCallScenario;
import com.cfl.model.DossierAftersalesResponse;
import com.cfl.model.OfferQuery;
import com.cfl.model.Order;
import com.cfl.model.DossierResponse.OrderItemStateType;
import com.cfl.services.IAssistantService;
import com.cfl.services.IClickToCallService;
import com.cfl.services.IMasterService;
import com.cfl.services.IOfferService;
import com.cfl.services.ISettingService;
import com.cfl.services.impl.AsyncClickToCallResponse;
import com.cfl.services.impl.AsyncDossierAfterSaleResponse;
import com.cfl.services.impl.DossierAfterSaleIntentService;
import com.cfl.services.impl.MasterIntentService;
import com.cfl.services.impl.ServiceConstant;
import com.cfl.services.impl.TrackerService;
import com.cfl.util.ActivityConstant;
import com.cfl.util.LocaleChangedUtils;
import com.cfl.util.NetworkUtils;
import com.cfl.util.SharedPreferencesUtils;
import com.cfl.util.TrackerConstant;
import com.cfl.util.Utils;

/**
 * client clict more passengers call show the view.
 *@author: DAVID
 */
public class MorePassengerActivity extends BaseActivity {
	private static final String TAG = MorePassengerActivity.class.getSimpleName();
	private View callBtn, hideView;
	private int clickToCallScenarioId;
	//private ToggleButton toggleButton;
	private TextView phoneNumberTextView, descTitleTextView, descTextView, title;
	private IMasterService masterService;
	private IClickToCallService clickToCallService;
	private ClickToCallScenario clickToCallScenario;
	private ProgressDialog progressDialog;
	private MyState myState;
	private ISettingService settingService;
	private IAssistantService assistantService;
	private ClickToCallParameter clickToCallParameter;
	private IOfferService offerService;
	private Intent intent;
	private OfferQuery offerQuery;
	private Order order;

	private boolean isNeedCallService;
	private String trackerAction;
	private String trackerActionCall;
	private String trackerActionIncorrectPhoneNumber;
	private String trackerActionNoPhoneNumber;
	private String trackerCategory;
	private String currentLanguage;
	private LinearLayout switchLinearLayout;
	private ServiceStateReceiver serviceStateReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		LocaleChangedUtils.initLanguageSettings(getApplicationContext());//Setting application language	
		setContentView(R.layout.more_than_two_persons_view);

		masterService = ((NMBSApplication) getApplication()).getMasterService();
		clickToCallService = ((NMBSApplication) getApplication()).getClickToCallService();
		settingService = ((NMBSApplication) getApplication()).getSettingService();
		offerService = ((NMBSApplication) getApplication()).getOfferService();
		assistantService = ((NMBSApplication) getApplication()).getAssistantService();
		offerQuery = offerService.getOfferQuery();
		currentLanguage = settingService.getCurrentLanguagesKey();
		if (savedInstanceState != null) {
			offerQuery = (OfferQuery) savedInstanceState.getSerializable(ActivityConstant.OFFER_QUERY);
			currentLanguage = savedInstanceState.getString("CurrentLanguage");
		}
		getIntentValues();
		bindAllViewElements();
		bindAllListeners();
		createClickToCallParameter();
	}

	private void bindAllViewElements() {
		descTitleTextView = (TextView) findViewById(R.id.more_than_two_persons_view_desc_title);
		descTextView = (TextView) findViewById(R.id.more_than_two_persons_view_desc);

		//toggleButton = (ToggleButton )findViewById(R.id.more_than_two_persons_view_mobile_number_switch_value);
		hideView = findViewById(R.id.more_than_two_persons_view_mobile_number_group_hide);
		phoneNumberTextView = (TextView) findViewById(R.id.more_than_two_persons_view_mobile_number_value);
		title = (TextView) findViewById(R.id.more_than_two_persons_view_title);

		switchLinearLayout = (LinearLayout) findViewById(R.id.more_than_two_persons_view_switch_LinearLayout);
		if (Utils.getAndroidSDKVersion() > android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
			handleSwitchButton();
		} else {
			handleCheckBox();
		}

		//historyView = (View) findViewById(R.id.assistant_view_history_RelativeLayout);
		/*rootView = (View) findViewById(R.id.RootLayout);
		
		rootView.setBackgroundDrawable(new BitmapDrawable(getResources(), ImageUtil.madeSmallBitmap(getApplicationContext(), R.drawable.ic_main_background_800, getWindowManager())));
		*/
		callBtn = findViewById(R.id.more_than_two_persons_view_call_btn);
	}

	private void handleCheckBox() {

		android.widget.CheckBox checkBox = new android.widget.CheckBox(this);
		switchLinearLayout.addView(checkBox);
		//checkBox.setClickable(true);
		checkBox.setChecked(false);
		//checkBox.setFocusable(true);
		String phoneNum = SharedPreferencesUtils.getSharedPreferencesByKey(SettingsActivity.PREFS_PHONE_NUMBER, this.getApplicationContext());
		//Log.d(TAG, "phoneNum? " + phoneNum);
		if (!StringUtils.equalsIgnoreCase("", phoneNum)) {
			isNeedCallService = true;
			phoneNumberTextView.setText(phoneNum);
			checkBox.setChecked(true);
			/*toggleButton.setChecked(true);
			toggleButton.setTextColor(getResources().getColor(R.color.text_white_color));
			toggleButton.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
			toggleButton.setPadding(0, 0, 15, 0);*/
			hideView.setVisibility(View.VISIBLE);
		}
		checkBox.setOnCheckedChangeListener(switchButtonCheckedChangeListener);
	}

	private void handleSwitchButton() {
		/*android.widget.Switch switchButton = new android.widget.Switch(this);
		switchButton.setThumbResource(R.drawable.apptheme_switch_inner_holo_light);
		switchButton.setTrackResource(R.drawable.apptheme_switch_track_holo_light);*/
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		android.widget.Switch switchButton = (android.widget.Switch) layoutInflater.inflate(R.layout.custom_swith, null);
		switchLinearLayout.addView(switchButton);
		switchButton.setChecked(false);


		String phoneNum = SharedPreferencesUtils.getSharedPreferencesByKey(SettingsActivity.PREFS_PHONE_NUMBER, this.getApplicationContext());
		//Log.d(TAG, "phoneNum? " + phoneNum);
		if (!StringUtils.equalsIgnoreCase("", phoneNum)) {
			isNeedCallService = true;
			phoneNumberTextView.setText(phoneNum);
			switchButton.setChecked(true);
			/*toggleButton.setChecked(true);
			toggleButton.setTextColor(getResources().getColor(R.color.text_white_color));
			toggleButton.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
			toggleButton.setPadding(0, 0, 15, 0);*/
			hideView.setVisibility(View.VISIBLE);
		}
		switchButton.setOnCheckedChangeListener(switchButtonCheckedChangeListener);
	}

	//Bind all Listeners
	public void bindAllListeners() {
		callBtn.setOnClickListener(callBtnOnClickListener);
		//toggleButton.setOnCheckedChangeListener(toggleButtOnCheckedChangeListener);

	}


	private CompoundButton.OnCheckedChangeListener switchButtonCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// track select toggle

			if (isChecked) {
				//TrackerService.getTrackerService().createEventTracker(trackerCategory, trackerAction);
				/*toggleButton.setTextColor(getResources().getColor(R.color.text_white_color));
				toggleButton.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
				toggleButton.setPadding(0, 0, 15, 0);*/
				hideView.setVisibility(View.VISIBLE);

				isNeedCallService = true;
			} else {
				/*toggleButton.setTextColor(getResources().getColor(R.color.color_blue_list_text));
				toggleButton.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
				toggleButton.setPadding(15, 0, 0, 0);*/
				hideView.setVisibility(View.GONE);

				isNeedCallService = false;
			}
		}

	};
	private OnClickListener callBtnOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			// track select Call our Contact Center

			//Called when a view has been clicked.	
			//startActivity(CallCenterActivity.createIntent(MorePassengerActivity.this.getApplicationContext(), clickToCallScenarioId, null, null));					
			InputMethodManager imm = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE));
			if (MorePassengerActivity.this.getCurrentFocus() != null) {
				imm.hideSoftInputFromWindow(MorePassengerActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
			String phoneNumberString = phoneNumberTextView.getText().toString();
			if (StringUtils.equalsIgnoreCase(phoneNumberString, "0032")) {
				phoneNumberString = "";
			}
			if (hideView.isShown() && StringUtils.isEmpty(phoneNumberString)) {

				// track select  error when no phone number is filled in
				/*TrackerService.getTrackerService().createEventTracker(
						trackerCategory, trackerActionNoPhoneNumber);*/
				Toast.makeText(MorePassengerActivity.this,
						getString(R.string.alert_clicktocall_phone_number_required),
						Toast.LENGTH_LONG).show();

			} else if (hideView.isShown() && phoneNumberString.length() < 5) {
				// track select  error when no phone number is filled in
				/*TrackerService.getTrackerService().createEventTracker(
						trackerCategory, trackerActionIncorrectPhoneNumber);*/
				Toast.makeText(MorePassengerActivity.this,
						getString(R.string.alert_phonenumber_incorrect),
						Toast.LENGTH_LONG).show();
			} else {

				SharedPreferencesUtils.storeSharedPreferences(
						SettingsActivity.PREFS_PHONE_NUMBER,
						phoneNumberString,
						MorePassengerActivity.this.getApplicationContext());

				createClickToCallParameter();
				CallScenarioAsyncTask callScenarioAsyncTask = new CallScenarioAsyncTask();
				callScenarioAsyncTask.execute((Void) null);
				if (isNeedCallService) {
					reEnableState();
				}

			}

			//Log.d(TAG, "phoneNumber? " + phoneNumberTextView.getText().toString());
		}
	};

	private void executeDossierAftersales() {

		if (!assistantService.hasExchangeable(order)) {
			Log.e(TAG, "RefreshDossier...to get Exchangeable");
			reEnableDossierAfterSaleState();
		}
	}

	private void createClickToCallParameter() {

		clickToCallParameter = new ClickToCallParameter();
		if (clickToCallScenarioId == 4) {
			/*trackerCategory = TrackerConstant.MYTICKETS;
			trackerAction = TrackerConstant.ACTION_MYTICKETS_EXCHANGE_YOUR_TICKETS_SHARE_TRAVEL_DETAIL;
			trackerActionCall = TrackerConstant.ACTION_MYTICKETS_EXCHANGE_YOUR_TICKETS_CALL_OUR_CONTACT_CENTER;
			trackerActionIncorrectPhoneNumber = TrackerConstant.ACTION_MYTICKETS_VALIDATION_TICKET_DETAIL_EXCHANGE_YOUR_TICKETS_INCORRECT_PHONENUMBER;
			trackerActionNoPhoneNumber = TrackerConstant.ACTION_MYTICKETS_VALIDATION_TICKET_DETAIL_EXCHANGE_YOUR_TICKETS_NO_PHONENUMBER;*/

			descTitleTextView.setText(getString(R.string.clicktocall_exchange_view_title));
			descTextView.setText(getString(R.string.clicktocall_exchange_view_info));
			title.setText(getString(R.string.ticket_detail_title) + " " + order.getDNR());
			createClickToCallParameterWithDossierAftersalesResponse();

			//executeDossierAftersales();						

		} else if (clickToCallScenarioId == 5) {
			/*trackerCategory = TrackerConstant.MYTICKETS;
			trackerAction = TrackerConstant.ACTION_MYTICKETS_CANCEL_YOUR_TICKETS_SHARE_TRAVEL_DETAIL;
			trackerActionCall = TrackerConstant.ACTION_MYTICKETS_CANCEL_YOUR_TICKETS_CALL_OUR_CONTACT_CENTER;
			trackerActionIncorrectPhoneNumber = TrackerConstant.ACTION_MYTICKETS_VALIDATION_TICKET_DETAIL_CANCEL_YOUR_TICKETS_INCORRECT_PHONENUMBER;
			trackerActionNoPhoneNumber = TrackerConstant.ACTION_MYTICKETS_VALIDATION_TICKET_DETAIL_CANCEL_YOUR_TICKETS_NO_PHONENUMBER;*/

			descTitleTextView.setText(getString(R.string.clicktocall_cancel_view_title));
			descTextView.setText(getString(R.string.clicktocall_cancel_view_info));
			title.setText(getString(R.string.ticket_detail_title) + " " + order.getDNR());
			createClickToCallParameterWithDossierAftersalesResponse();
			//executeDossierAftersales();
		} else if (clickToCallScenarioId == 3 && order != null) {
			createClickToCallParameterWithDossierAftersalesResponse();
		} else {
			/*trackerCategory = TrackerConstant.BOOKING;
			trackerAction = TrackerConstant.ACTION_BOOKING_PASSENGERS_OTHER_TRAVEL_PARTY_SHARE_TRAVEL_DETAILS;
			trackerActionCall = TrackerConstant.ACTION_BOOKING_PASSENGERS_OTHER_TRAVEL_PARTY_CALL_OUR_CONTACT_CENTER;
			trackerActionIncorrectPhoneNumber = TrackerConstant.ACTION_BOOKING_PASSENGERS_OTHER_TRAVEL_INCORRECT_PHONENUMBER;
			trackerActionNoPhoneNumber = TrackerConstant.ACTION_BOOKING_PASSENGERS_OTHER_TRAVEL_PARTY_NO_PHONENUMBER;*/

			descTitleTextView.setText(getString(R.string.passengers_selection_view_more_than));
			descTextView.setText(getString(R.string.othertravelpaty_view_desc));
			title.setText(getString(R.string.booking_flow_step1));
			createClickToCallParameterWithOfferQuery();
			Log.d(TAG, "ClickToCallFinished..." + MasterIntentService.isClickToCallFinished);
			if (!MasterIntentService.isClickToCallFinished) {
				showWaitDialog();
				if (serviceStateReceiver == null) {
					serviceStateReceiver = new ServiceStateReceiver();
					registerReceiver(serviceStateReceiver, new IntentFilter(ServiceConstant.CLICK_TO_CALL_SERVICE_ACTION));
				}
			}
		}
	}

	private void createClickToCallParameterWithDossierAftersalesResponse() {
		ClickToCallContext clickToCallContext = new ClickToCallContext();
		String phoneInfo = android.os.Build.VERSION.RELEASE;
		String phoneModel = android.os.Build.MODEL;
		clickToCallContext.setScenario(String.valueOf(clickToCallScenarioId));
		clickToCallContext.setLanguage(currentLanguage);
		//clickToCallContext.setOS("Android (" + phoneInfo + ")");
		//clickToCallContext.setDevice(phoneModel);
		clickToCallContext.setTimeStamp(new Date());
		if (order != null) {
			//clickToCallContext.setComfortClass(order.getTravelclass());
			clickToCallContext.setDnr(order.getDNR());
			/*clickToCallContext.setDestinationStationName(order.getDestination());
			clickToCallContext.setDestinationStationRCode(order.getDestinationCode());
			clickToCallContext.setOriginStationName(order.getOrigin());
			clickToCallContext.setOriginStationRCode(order.getOriginCode());*/
		}
		//clickToCallContext.setTravelType(null);
		String phoneNumberString = phoneNumberTextView.getText().toString();
		if (StringUtils.equalsIgnoreCase(phoneNumberString, "0032")) {
			phoneNumberString = "";
		}
		clickToCallParameter.setPhoneNumber(phoneNumberString);
		clickToCallParameter.setClickToCallContext(clickToCallContext);
		//ObjectToJsonUtils.getPostClickToCallParameterStr(clickToCallParameter);
	}

	private void createClickToCallParameterWithOfferQuery() {
		//clickToCallParameter = new ClickToCallParameter();

		clickToCallParameter = new ClickToCallParameter();
		String phoneInfo = android.os.Build.VERSION.RELEASE;
		String phoneModel = android.os.Build.MODEL;
		ClickToCallContext clickToCallContext = new ClickToCallContext();
		clickToCallContext.setDnr("");
		clickToCallContext.setScenario(String.valueOf(clickToCallScenarioId));
		clickToCallContext.setLanguage(currentLanguage);
		//clickToCallContext.setOS("Android (" + phoneInfo + ")");
		//clickToCallContext.setDevice(phoneModel);
		clickToCallContext.setTimeStamp(new Date());
		/*clickToCallContext.setComfortClass(offerQuery.getComforClass());
		clickToCallContext.setDepartureQueryParameters(offerQuery.getDepartureQueryParameters());
		clickToCallContext.setDestinationStationName(offerQuery.getDestinationStationDestinationName());
		clickToCallContext.setDestinationStationRCode(offerQuery.getDestinationStationRCode());
		clickToCallContext.setOriginStationName(offerQuery.getOriginStationDestinationName());
		clickToCallContext.setOriginStationRCode(offerQuery.getOriginStationRCode());
		clickToCallContext.setReturnQueryParameters(offerQuery.getReturnQueryParameters());
		clickToCallContext.setCorporateCards(offerQuery.getListCorporateCards());
		clickToCallContext.setTravelParty(offerQuery.getTravelPartyMembers());
		clickToCallContext.setGreenPointsNumber(offerQuery.getGreenPointsNumber());
		clickToCallContext.setTravelType(offerQuery.getTravelType());*/

		//clickToCallParameter.setPhoneNumber("123");
		String phoneNumberString = phoneNumberTextView.getText().toString();
		if (StringUtils.equalsIgnoreCase(phoneNumberString, "0032")) {
			phoneNumberString = "";
		}
		clickToCallParameter.setPhoneNumber(phoneNumberString);
		clickToCallParameter.setClickToCallContext(clickToCallContext);
		//ObjectToJsonUtils.getPostClickToCallParameterStr(clickToCallParameter);
		//ObjectToJsonUtils.getPostOfferQueryStr(offerService.getOfferQuery());
	}

	private void call() {
		
		/*TrackerService.getTrackerService().createEventTracker(
				trackerCategory, trackerActionCall);*/

		TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		String simOperator = tel.getNetworkOperatorName();
		String phoneNumber = clickToCallService.getPhoneNumber(clickToCallScenario, simOperator);
		//Log.d(TAG, "phoneNumber? " + phoneNumber);
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
			startActivity(intent);
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}

		
	}
	
	/**
	 * 	It's a Exception class, used for receiving exception.
	 */
	private class ExceptionReceiver {
		private Exception excp;
	}
	/**
	 * Create a AsyncTask, to search data
	 * 
	 *
	 */
	private class CallScenarioAsyncTask extends AsyncTask<Void, Void, Void> {

		private ExceptionReceiver exceptionReceiver;

		@Override
		protected void onPostExecute(Void result) {
			//hideWaitDialog();
			if (exceptionReceiver != null && exceptionReceiver.excp != null) {
				Toast.makeText(
						MorePassengerActivity.this,
						MorePassengerActivity.this
								.getString(R.string.alert_clicktocall_server_error),
						Toast.LENGTH_SHORT).show();
			}else {
			
				
				if (clickToCallScenario != null) {
					call();
				}
				
			}
		}
		@Override
		protected void onPreExecute() {
			//showWaitDialog();
			super.onPreExecute();
		}
		@Override
		protected Void doInBackground(Void... params) {			
			
			try {					
					
			clickToCallScenario = masterService.loadClickToCallScenarioById(clickToCallScenarioId);										
			} catch (Exception e) {
				e.printStackTrace();
				exceptionReceiver = new ExceptionReceiver();
				exceptionReceiver.excp = e;
				//Log.e(TAG, "ExceptionReceiver when searchCity.", e);
				throw new RuntimeException();
			}
			return null;
		}
	}
	
	// show progressDialog.
	private void showWaitDialog() {
		runOnUiThread(new Runnable() {
			   public void run() {
				   if(progressDialog == null){
						//Log.e(TAG, "Show Wait Dialog....");
						progressDialog = ProgressDialog.show(MorePassengerActivity.this,
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
	
	/**
	 * private class which contains all fields and objects who need to be kept		 
	 */
	private class MyState {		
		public AsyncClickToCallResponse asyncClickToCallResponse;

		public AsyncDossierAfterSaleResponse asyncDossierAfterSaleResponse;
		//public DossierAftersalesResponse dossierAftersalesResponse;
	
		//public boolean searchOngoing;
		
		public void unRegisterHandler(){
			if (asyncClickToCallResponse != null){
				asyncClickToCallResponse.unregisterHandler();
			}
		}		
		public void registerHandler(Handler handler){
			if (asyncClickToCallResponse != null){
				asyncClickToCallResponse.registerHandler(handler);
			}
		}
		
		public void unRegisterDossierAfterSaleHandler(){
			if (asyncDossierAfterSaleResponse != null){
				asyncDossierAfterSaleResponse.unregisterHandler();
			}
		}
		
		public void registerRegisterDossierAfterSaleHandler(Handler handler){
			if (asyncDossierAfterSaleResponse != null){
				asyncDossierAfterSaleResponse.registerHandler(handler);
			}
		}
	}
	
	private void reEnableState(){
		if (myState == null){
			//Log.i(TAG, "myState is null");		
			myState = new MyState();						
		}
		int connectionStatus = NetworkUtils.getNetworkState(MorePassengerActivity.this.getApplicationContext());
		if (connectionStatus > 0 ) {
			clickToCall();		
			
		}else {
			Toast.makeText(MorePassengerActivity.this, getString(R.string.alert_no_network),Toast.LENGTH_LONG).show();
			
		}		
		//Log.i(TAG, "Is Refreshed? " + myState.isRefreshed);		
	}
	
	private void reEnableDossierAfterSaleState(){
		if (myState == null){
			//Initial call
			Log.i(TAG, "myState is null");		
			myState = new MyState();					
				//getDossierData("MXMYQWZ"); Testing
															
		} 
		int connectionStatus = NetworkUtils.getNetworkState(MorePassengerActivity.this.getApplicationContext());
		if (connectionStatus > 0 ) {
			getDossierData(order, true);		
			
		}else {
			Toast.makeText(MorePassengerActivity.this, getString(R.string.alert_no_network),Toast.LENGTH_LONG).show();
			
		}				

	}
	
	private void getDossierData(Order order, boolean hasConnection){
		myState.asyncDossierAfterSaleResponse = assistantService.refrshDossierAfterSale(order, settingService);
		myState.asyncDossierAfterSaleResponse.registerHandler(dossierAfterSaleHandler);		
		myState.registerRegisterDossierAfterSaleHandler(dossierAfterSaleHandler);
		showWaitDialog();
	}

	
	//Handler which is called when the App is refreshed.
		// The what parameter of the message decides if there was an error or not.
	private Handler dossierAfterSaleHandler = new Handler() {
		public void handleMessage(Message msg) {
			hideWaitDialog();
			myState.unRegisterDossierAfterSaleHandler();
			
			switch (msg.what) {
			case ServiceConstant.MESSAGE_WHAT_OK:
				Log.d(TAG, "dossierAfterSaleHandler receive MESSAGE_WHAT_OK");
				break;
			case ServiceConstant.MESSAGE_WHAT_ERROR:
				/*Toast.makeText(MorePassengerActivity.this,
						getString(R.string.alert_exchangeorcancel_refresh_failed),
						Toast.LENGTH_LONG).show();*/
				default:
					break;
				}
			}
		
	};
	
	private void clickToCall(){		
		myState.asyncClickToCallResponse = clickToCallService.sendClickToCall(clickToCallParameter, settingService);
		myState.asyncClickToCallResponse.registerHandler(mHandler);
		//myState.searchOngoing = true;		
		myState.registerHandler(mHandler);
		showWaitDialog();
		
	}
	// receive right information, change myState status and hide progressDialog.
	private void clickToCallResponseReceived() {

		//call();	
		myState.unRegisterHandler();
		myState.asyncClickToCallResponse = null;
		
		//myState.searchOngoing = false;	
		
		hideWaitDialog();
		//Log.d(TAG, "show response");
	}

	//Handler which is called when the app is refreshed.
	// The what parameter of the message decides if there was an error or not.
    private Handler mHandler = new Handler(){   
        public void handleMessage(Message msg) {   
			switch (msg.what) {   
            case ServiceConstant.MESSAGE_WHAT_OK:
            	clickToCallResponseReceived();
           	           	
            	break;
			case ServiceConstant.MESSAGE_WHAT_ERROR:
				
				Bundle bundle = msg.getData();
				NetworkError error = (NetworkError) bundle.getSerializable(ServiceConstant.PARAM_OUT_ERROR);
				
				
				switch (error) {
				case TIMEOUT:
					hideWaitDialog();
					Toast.makeText(MorePassengerActivity.this,
							getString(R.string.alert_clicktocall_server_error),
							Toast.LENGTH_LONG).show();
					//finish();
					break;
				default:
					break;
			
				}
				//Log.d(TAG, "handler receive");
				break;
			}
		};
    };
    
	/**
	 *@Description: Show station view
	 *@param
	 *@param     clickToCallScenarioId
	 *@return    None
	 */
	public static Intent createIntent(Context context, int clickToCallScenarioId, 
			DossierAftersalesResponse dAftersalesResponse, Order order) {
		
		Intent intent = new Intent(context, MorePassengerActivity.class);		
		intent.putExtra(ActivityConstant.CLICK_TO_CALL_SCENARIO_ID, clickToCallScenarioId);
		intent.putExtra(TarrifActivity.DOSSIER_AFTERSALES_RESPONSE_SERIALIZABLE_KEY, dAftersalesResponse);
		intent.putExtra(ActivityConstant.ORDER, order);
		return intent;
	}
	
	// get intent object
	private void getIntentValues(){
		intent = getIntent();
		// get values( depart and return , maybe there is no return value)
		clickToCallScenarioId = intent.getIntExtra(ActivityConstant.CLICK_TO_CALL_SCENARIO_ID, 0);
		//dAftersalesResponse = (DossierAftersalesResponse)getIntent().getSerializableExtra(TarrifActivity.DOSSIER_AFTERSALES_RESPONSE_SERIALIZABLE_KEY);
		order = (Order) getIntent().getSerializableExtra(ActivityConstant.ORDER);	
	}	
	
	public void onSaveInstanceState(Bundle outState) {
    	//first saving my state, so the bundle wont be empty.
    	//http://code.google.com/p/android/issues/detail?id=19917
    	//outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
    	//outState.putSerializable("OfferService", offerService.getOfferResponse());
    	super.onSaveInstanceState(outState);
    	//bundle.putParcelableArrayList("list", list);
    	//Log.i(TAG, "onSaveInstanceState");			
    	outState.putSerializable(ActivityConstant.OFFER_QUERY, offerQuery);
    	outState.putSerializable("CurrentLanguage", currentLanguage);
    	
    }
	
	@Override
	protected void onDestroy() {
		//Log.i(TAG, "onDestroy");
		
		hideWaitDialog();
		if(serviceStateReceiver != null){
			unregisterReceiver(serviceStateReceiver);
		}
		super.onDestroy();
	}

	class ServiceStateReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "onReceive got CLICK_TO_CALL_SERVICE_ACTION broadcast");
			if (ServiceConstant.CLICK_TO_CALL_SERVICE_ACTION.equalsIgnoreCase(intent.getAction().toString())) {
				hideWaitDialog();
			}
		}
	}

}

