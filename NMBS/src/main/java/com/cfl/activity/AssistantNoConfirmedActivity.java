package com.cfl.activity;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cfl.R;
import com.cfl.application.NMBSApplication;
import com.cfl.exceptions.NetworkError;
import com.cfl.model.ClickToCallContext;
import com.cfl.model.ClickToCallParameter;
import com.cfl.model.ClickToCallScenario;
import com.cfl.model.Order;
import com.cfl.services.IClickToCallService;
import com.cfl.services.IMasterService;
import com.cfl.services.ISettingService;
import com.cfl.services.impl.AsyncClickToCallResponse;
import com.cfl.services.impl.ServiceConstant;
import com.cfl.util.ActivityConstant;
import com.cfl.util.DateUtils;
import com.cfl.util.LocaleChangedUtils;
import com.cfl.util.NetworkUtils;
import com.cfl.util.SharedPreferencesUtils;
import com.cfl.util.Utils;

/**
 * Activity used for displaying the UI element, user can do some interbehavior with this.
 *
 * User's order has been processed unsuccessfully.
 * this activity will be displayed.
 */
public class AssistantNoConfirmedActivity extends BaseActivity {

	//private static final String TAG = AssistantNoConfirmedActivity.class.getSimpleName();	
	private View hideView;
	private Button callButton;
	//private ToggleButton toggleButton;
	private TextView departureLocaleTextView, perNumberTextView, dnrTextView,
			departureDateTextView, pnrTextView, phoneNumberTextView, titleTextView;
	private Order order;

	private boolean isNeedCallService;
	private ClickToCallParameter clickToCallParameter;
	private ISettingService settingService;
	private ClickToCallScenario clickToCallScenario;
	private ProgressDialog progressDialog;
	private IMasterService masterService;
	private IClickToCallService clickToCallService;
	private MyState myState;
	private String currentLanguage;
	private LinearLayout switchLinearLayout;

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		LocaleChangedUtils.initLanguageSettings(getApplicationContext());//Setting application language	
		setContentView(R.layout.assistant_no_confirmed_view);
		settingService = ((NMBSApplication) getApplication()).getSettingService();
		masterService = ((NMBSApplication) getApplication()).getMasterService();
		clickToCallService = ((NMBSApplication) getApplication()).getClickToCallService();
		//Log.d(TAG, "onCreate");
		currentLanguage = settingService.getCurrentLanguagesKey();
		if (savedInstanceState != null) {
			currentLanguage = savedInstanceState.getString("CurrentLanguage");
		}
		getIntentValue();
		bindAllViewsElement();
		bindAllListeners();
		setViewStateBasedOnModel();
	}

	//Bind all view elements
	public void bindAllViewsElement() {
		//Bind view element
		/*rootView = (View) findViewById(R.id.RootLayout);
		
		rootView.setBackgroundDrawable(new BitmapDrawable(getResources(), ImageUtil.madeSmallBitmap(getApplicationContext(), R.drawable.ic_main_background_800, getWindowManager())));*/
		callButton = (Button) findViewById(R.id.assistant_no_confirmed_view_call_Button);
		departureLocaleTextView = (TextView) findViewById(R.id.assistant_no_confirmed_view_departure_locale_TextView);

		//toggleButton = (ToggleButton )findViewById(R.id.more_than_two_persons_view_mobile_number_switch_value);
		hideView = findViewById(R.id.more_than_two_persons_view_mobile_number_group_hide);
		phoneNumberTextView = (TextView) findViewById(R.id.more_than_two_persons_view_mobile_number_value);

		perNumberTextView = (TextView) findViewById(R.id.assistant_no_confirmed_view_pers_number_TextView);
		dnrTextView = (TextView) findViewById(R.id.assistant_no_confirmed_view_view_dnr_TextView);
		departureDateTextView = (TextView) findViewById(R.id.assistant_no_confirmed_view_departure_date_TextView);
		pnrTextView = (TextView) findViewById(R.id.assistant_adapter_view_pnr_value_TextView);
		titleTextView = (TextView) findViewById(R.id.ticket_no_confirmed_view_title);

		switchLinearLayout = (LinearLayout) findViewById(R.id.more_than_two_persons_view_switch_LinearLayout);
		if (Utils.getAndroidSDKVersion() > android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
			handleSwitchButton();
		} else {
			handleCheckBox();
		}
	}

	//Bind all Listeners
	public void bindAllListeners() {
		callButton.setOnClickListener(callBtnOnClickListener);
		//toggleButton.setOnCheckedChangeListener(toggleButtOnCheckedChangeListener);
	}

	private void setDataByButtonState(CompoundButton switchButton) {
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

	private void handleCheckBox() {

		android.widget.CheckBox checkBox = new android.widget.CheckBox(this);
		switchLinearLayout.addView(checkBox);
		//checkBox.setClickable(true);
		checkBox.setChecked(false);
		//checkBox.setFocusable(true);
		setDataByButtonState(checkBox);
	}

	private void handleSwitchButton() {
		/*android.widget.Switch switchButton = new android.widget.Switch(this);
		switchButton.setThumbResource(R.drawable.apptheme_switch_inner_holo_light);
		switchButton.setTrackResource(R.drawable.apptheme_switch_track_holo_light);*/
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		android.widget.Switch switchButton = (android.widget.Switch) layoutInflater.inflate(R.layout.custom_swith, null);
		switchLinearLayout.addView(switchButton);
		switchButton.setChecked(false);

		setDataByButtonState(switchButton);
	}

	private OnCheckedChangeListener switchButtonCheckedChangeListener = new OnCheckedChangeListener() {
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// track select toggle

			if (isChecked) {
				
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
			//Called when a view has been clicked.	
			//startActivity(CallCenterActivity.createIntent(MorePassengerActivity.this.getApplicationContext(), clickToCallScenarioId, null, null));					
			InputMethodManager imm = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE));
			if (AssistantNoConfirmedActivity.this.getCurrentFocus() != null) {
				imm.hideSoftInputFromWindow(AssistantNoConfirmedActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
			String phoneNumberString = phoneNumberTextView.getText().toString();
			if (StringUtils.equalsIgnoreCase(phoneNumberString, "0032")) {
				phoneNumberString = "";
			}
			if (hideView.isShown() && StringUtils.isEmpty(phoneNumberString)) {
				Toast.makeText(AssistantNoConfirmedActivity.this,
						getString(R.string.alert_clicktocall_phone_number_required),
						Toast.LENGTH_LONG).show();
			} else if (hideView.isShown() && phoneNumberString.toString().length() < 5) {
				Toast.makeText(AssistantNoConfirmedActivity.this,
						getString(R.string.alert_phonenumber_incorrect),
						Toast.LENGTH_LONG).show();
			} else {
				SharedPreferencesUtils.storeSharedPreferences(
						SettingsActivity.PREFS_PHONE_NUMBER,
						phoneNumberString,
						AssistantNoConfirmedActivity.this.getApplicationContext());
				createClickToCallParameterWithDossierAftersalesResponse();
				CallScenarioAsyncTask callScenarioAsyncTask = new CallScenarioAsyncTask();
				callScenarioAsyncTask.execute((Void) null);
				if (isNeedCallService) {
					reEnableState();
				}

			}

			//Log.d(TAG, "phoneNumber? " + phoneNumberTextView.getText().toString());
		}
	};

	private void createClickToCallParameterWithDossierAftersalesResponse() {
		clickToCallParameter = new ClickToCallParameter();
		ClickToCallContext clickToCallContext = new ClickToCallContext();
		String phoneInfo = android.os.Build.VERSION.RELEASE;
		String phoneModel = android.os.Build.MODEL;
		clickToCallContext.setScenario(String.valueOf(3));
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


	private void getIntentValue() {

		order = (Order) getIntent().getSerializableExtra(ActivityConstant.ORDER);
	}

	private void setViewStateBasedOnModel() {
		departureLocaleTextView.setText(order.getOrigin() + " - " + order.getDestination());
		String departureDate = DateUtils.dateToString(getApplicationContext(), order.getDepartureDate());

		String time = DateUtils.timeToString(this.getApplicationContext(), order.getDepartureDate());
		if (StringUtils.equalsIgnoreCase(time, "00:00")) {
			time = "";
		} else {
			time = time + " - ";
		}
		String trainNrString = order.getTrainNr();
		if (trainNrString == null) {
			trainNrString = "";
		} else {
			trainNrString = getString(R.string.general_Train) + ": " + trainNrString;
		}
		departureDateTextView.setText(departureDate + " " + time + trainNrString);

		String perString = order.getPersonNumber() == 1 ? getString(R.string.general_person) : getString(R.string.general_pers);
		perNumberTextView.setText(order.getPersonNumber() + " " + perString + " -");
		//viewHolder.persNumber.setText(this.getItem(position).getPersonNumber());
		dnrTextView.setText(getString(R.string.general_dnr) + " " + order.getDNR());
		String pnrString = "";

		if (order.getPnr() != null && !StringUtils.equalsIgnoreCase(order.getPnr(), "")) {
			pnrString = order.getPnr();
		} else {
			pnrString = "/";
		}

		pnrTextView.setText(pnrString);
		titleTextView.setText(getString(R.string.ticket_detail_title) + " " + order.getDNR());
	}

	public static Intent createConfirmationIntent(Context context, Order order) {
		Intent intent = new Intent(context, AssistantNoConfirmedActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(ActivityConstant.ORDER, order);
		return intent;
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
						AssistantNoConfirmedActivity.this,
						AssistantNoConfirmedActivity.this
								.getString(R.string.general_server_unavailable),
						Toast.LENGTH_SHORT).show();
			} else {


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

				clickToCallScenario = masterService.loadClickToCallScenarioById(3);
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
				if (progressDialog == null) {
					//Log.e(TAG, "Show Wait Dialog....");
					progressDialog = ProgressDialog.show(AssistantNoConfirmedActivity.this,
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
				// Log.e(TAG, "Hide Wait Dialog....");
				if (progressDialog != null) {
					//Log.e(TAG, "progressDialog is.... " + progressDialog);
					progressDialog.dismiss();
					progressDialog = null;
				}
			}
		});

	}

	private void call() {
		TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		String simOperator = tel.getNetworkOperatorName();
		String phoneNumber = clickToCallService.getPhoneNumber(clickToCallScenario, simOperator);
		//Log.d(TAG, "phoneNumber? " + phoneNumber);
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));

		//startActivity(intent);
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
			startActivityForResult(intent, 0);
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
	 * private class which contains all fields and objects who need to be kept		 
	 */
	private class MyState {		
		public AsyncClickToCallResponse asyncClickToCallResponse;

	
		//public boolean searchOngoing;
		private boolean isRefreshed;
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
	}
	
	private void reEnableState(){
		if (myState == null){
			//Log.i(TAG, "myState is null");		
			myState = new MyState();						
		}
		int connectionStatus = NetworkUtils.getNetworkState(AssistantNoConfirmedActivity.this.getApplicationContext());
		if (connectionStatus > 0 ) {
			clickToCall();		
			
		}else {
			Toast.makeText(AssistantNoConfirmedActivity.this, getString(R.string.alert_no_network),Toast.LENGTH_LONG).show();
			
		}		
		//Log.i(TAG, "Is Refreshed? " + myState.isRefreshed);		
	}
	
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
		myState.isRefreshed = true;
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
				NetworkError error=(NetworkError) bundle.getSerializable(ServiceConstant.PARAM_OUT_ERROR);
				
				
				switch (error) {
				case TIMEOUT:
					hideWaitDialog();
					Toast.makeText(AssistantNoConfirmedActivity.this,
							getString(R.string.general_server_unavailable),
							Toast.LENGTH_LONG).show();
					//finish();
					break;
			
				}
				//Log.d(TAG, "handler receive");
				break;
			}
		};
    };
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			//Log.e(TAG, "Call END");
			if (resultCode == RESULT_CANCELED) {
				//Log.e(TAG, "User click return when select station");
			} else {
				//Log.e(TAG, "Call END");
			}
			
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
    
	public void onSaveInstanceState(Bundle outState) {
    	//first saving my state, so the bundle wont be empty.
    	//http://code.google.com/p/android/issues/detail?id=19917
    	//outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
    	//outState.putSerializable("OfferService", offerService.getOfferResponse());
    	super.onSaveInstanceState(outState);
    	//bundle.putParcelableArrayList("list", list);
    	//Log.i(TAG, "onSaveInstanceState");			
    	
    	outState.putSerializable("CurrentLanguage", currentLanguage);
    	
    }
}
