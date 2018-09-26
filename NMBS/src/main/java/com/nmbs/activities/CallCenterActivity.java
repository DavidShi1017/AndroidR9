package com.nmbs.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.SwitchCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nmbs.R;
import com.nmbs.activities.view.DialogAlertError;
import com.nmbs.activity.BaseActivity;
import com.nmbs.adapter.PhoneNumberDialogAdapter;
import com.nmbs.application.NMBSApplication;
import com.nmbs.exceptions.NetworkError;
import com.nmbs.log.LogUtils;
import com.nmbs.model.ClickToCallAftersalesParameter;
import com.nmbs.model.ClickToCallAftersalesResponse;
import com.nmbs.model.ClickToCallContext;
import com.nmbs.model.ClickToCallParameter;
import com.nmbs.model.ClickToCallScenario;
import com.nmbs.model.Dossier;
import com.nmbs.model.DossierAftersalesResponse;
import com.nmbs.model.GeneralSetting;
import com.nmbs.model.LogonInfo;
import com.nmbs.services.IClickToCallService;
import com.nmbs.services.IMasterService;
import com.nmbs.services.IOfferService;
import com.nmbs.services.ISettingService;
import com.nmbs.services.impl.AsyncClickToCallResponse;
import com.nmbs.services.impl.ClickToCallService;
import com.nmbs.services.impl.ServiceConstant;
import com.nmbs.services.impl.SettingService;
import com.nmbs.util.ActivityConstant;
import com.nmbs.util.GoogleAnalyticsUtil;
import com.nmbs.util.NetworkUtils;
import com.nmbs.util.ObjectToJsonUtils;
import com.nmbs.util.TrackerConstant;
import com.nmbs.util.Utils;

import org.apache.commons.lang.StringUtils;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * client clict more passengers call show the view.
 *@author:Richard
 */
public class CallCenterActivity extends BaseActivity {
	private static final String Intent_Key_dosssier = "Dossier";
	private Intent intent;
	private int clickToCallScenarioId;
	private IMasterService masterService;
	private IClickToCallService clickToCallService;
	private ClickToCallScenario clickToCallScenario;
	private ProgressDialog progressDialog;
	private MyState myState;
	private ClickToCallParameter clickToCallParameter;
	private IOfferService offerService;
	private TextView phoneNumberTextView;
	private boolean isNoBtnSelected;

	private View vLine;
	private Button intButton;
	private Button belgiumButton;
	private GeneralSetting generalSetting;
	private LinearLayout internationalView, belgiumView;

	private LinearLayout exchangeAndRefundLayout, phoneNumberEditLayout, llPhoneNumber;
	private RelativeLayout shareTravelLayout, phoneNumberLayout;
	private TextView exchangeTicketsTextView, refundTicketsTextView, tvDnr, introTextView, guidTextView, tvNumberError;
	private PhoneNumberDialogAdapter phoneNumberDialogAdapter;
	private SwitchCompat switchCompatView;
	private ImageView countryLogoImageView;
	private String callNumber;
	private String selectedPhoneNumberPrefixed;
	private SettingService settingService;
	private Dossier dossier;
	private int which;
	private DialogAlertError dialogError;
	private TextView tvOnlineAftersalesIntroduction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Utils.setToolBarStyle(this);
		settingService = ((NMBSApplication) getApplication()).getSettingService();
		//settingService.initLanguageSettings();
		setContentView(R.layout.activity_call_center_view);
		masterService = ((NMBSApplication) getApplication()).getMasterService();
		clickToCallService = ((NMBSApplication) getApplication()).getClickToCallService();

		getIntentValues();
		bindAllViewElements();
		bindAllListeners();
		initData();
	}

	private void bindAllViewElements() {
		phoneNumberTextView = (TextView) findViewById(R.id.et_call_center_phoneNumber);
		this.intButton = (Button) findViewById(R.id.btn_call_center_int);
		this.belgiumButton = (Button) findViewById(R.id.btn_call_center_belgium);
		this.internationalView = (LinearLayout) findViewById(R.id.ll_call_center_international_layout);
		this.belgiumView = (LinearLayout) findViewById(R.id.ll_call_center_belgium_layout);
		this.phoneNumberLayout = (RelativeLayout) findViewById(R.id.ll_call_center_phone_number_layout);
		this.shareTravelLayout = (RelativeLayout) findViewById(R.id.rl_call_center_share_travel_layout);
		this.refundTicketsTextView = (TextView) findViewById(R.id.tv_call_center_refund_tickets);
		this.exchangeTicketsTextView = (TextView) findViewById(R.id.tv_call_center_exchange_tickets);
		this.switchCompatView = (SwitchCompat) findViewById(R.id.sw_share_data);
		this.exchangeAndRefundLayout = (LinearLayout) findViewById(R.id.ll_call_center_exchange_refund_layout);
		this.countryLogoImageView = (ImageView) findViewById(R.id.iv_call_center_selected_country_logo);
		this.tvDnr = (TextView) findViewById(R.id.tv_dnr);
		this.introTextView = (TextView) findViewById(R.id.tv_call_center_intro);
		this.guidTextView = (TextView) findViewById(R.id.tv_call_center_guid);
		if (switchCompatView.isChecked()) {
			switchCompatView.setChecked(false);
			phoneNumberLayout.setVisibility(View.GONE);
		} else {
			switchCompatView.setChecked(true);
			phoneNumberLayout.setVisibility(View.VISIBLE);
		}
		this.vLine = findViewById(R.id.v_line);
		this.llPhoneNumber = (LinearLayout) findViewById(R.id.ll_call_center_phone_number_edit);
		this.tvNumberError = (TextView) findViewById(R.id.tv_number_error);

		this.tvOnlineAftersalesIntroduction = (TextView) findViewById(R.id.tv_online_aftersales_introduction);

		String onlineAftersales = getResources().getString(R.string.aftersales_introduction_boldtext);
		String onlineAftersalesIntroduction = getResources().getString(R.string.aftersales_introduction_boldtext) + " " + getResources().getString(R.string.aftersales_introduction_line2);
		int start = onlineAftersalesIntroduction.indexOf(onlineAftersales);
		int end = start + onlineAftersales.length();
		SpannableStringBuilder builder = new SpannableStringBuilder(onlineAftersalesIntroduction);
		builder.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tvOnlineAftersalesIntroduction.setText(builder);
	}

	public void back(View view) {
		finish();
	}


	private void initData() {
		if (dossier != null) {
			tvDnr.setText(dossier.getDossierId());
		}
		/*String phoneNumber = clickToCallService.getPhoneNumber();
		String prefix = clickToCallService.getPhoneNumberPrefix();*/
		String phoneNumber = "";
		String prefix = "";
		LogonInfo logonInfo = NMBSApplication.getInstance().getLoginService().getLogonInfo();
		if(logonInfo != null){
			phoneNumber = logonInfo.getPhoneNumber();
			prefix = logonInfo.getCode();
		}

		LogUtils.e("prefix", "Code----->" + prefix);
		final String prefixArray[] = this.getResources().getStringArray(R.array.phoneNumber_prefix);
		for(int i = 0; i < prefixArray.length; i++){
			LogUtils.e("prefix", "prefix----->" + prefixArray[i].toString());
			String prefixinArray = prefixArray[i].substring(1);
			if(prefixArray[i] != null && prefix.equalsIgnoreCase(prefixinArray)){
				which = i;
			}
		}
		LogUtils.e("prefix", "which----->" + which);
		TypedArray ar = this.getResources().obtainTypedArray(R.array.phoneNumber_country_logo);
		int len = ar.length();
		final int[] resIds = new int[len];
		for (int i = 0; i < len; i++)
			resIds[i] = ar.getResourceId(i, 0);
		ar.recycle();
		selectedPhoneNumberPrefixed = prefix;
		countryLogoImageView.setImageResource(resIds[which]);
		phoneNumberTextView.setText(phoneNumber);
		this.generalSetting = clickToCallService.getGeneralSetting();
		if (generalSetting != null) {
			if (this.generalSetting.getInternationalPhoneNumber() != null && !"".equals(this.generalSetting.getInternationalPhoneNumber())) {
				this.internationalView.setVisibility(View.VISIBLE);
				this.intButton.setText(this.generalSetting.getInternationalPhoneNumber());
			} else {
				this.internationalView.setVisibility(View.GONE);
			}

			if (this.generalSetting.getBelgiumPhoneNumber() != null && !"".equals(this.generalSetting.getBelgiumPhoneNumber())) {
				this.belgiumView.setVisibility(View.VISIBLE);
				this.belgiumButton.setText(this.generalSetting.getBelgiumPhoneNumber());
			} else {
				this.belgiumView.setVisibility(View.GONE);
			}
			switch (clickToCallScenarioId) {
				case ClickToCallService.DossierQuestion:
					this.exchangeAndRefundLayout.setVisibility(View.GONE);
					if (this.generalSetting.isAllowContextRegistration()) {
						this.guidTextView.setText(getString(R.string.call_center_dossier_guidance));
						this.introTextView.setText(getString(R.string.call_center_dossier_intro));
					} else {
						vLine.setVisibility(View.GONE);
						this.shareTravelLayout.setVisibility(View.GONE);
						this.phoneNumberLayout.setVisibility(View.GONE);
						this.guidTextView.setVisibility(View.GONE);
					}
					GoogleAnalyticsUtil.getInstance().sendScreen(CallCenterActivity.this, TrackerConstant.CLICK2CALL);
					break;
				case ClickToCallService.DossierIssue:
					this.exchangeAndRefundLayout.setVisibility(View.GONE);
					if (this.generalSetting.isAllowContextRegistration()) {
						this.guidTextView.setText(getString(R.string.call_center_dossier_guidance));
						this.introTextView.setText(getString(R.string.call_center_dossier_intro));
					} else {
						vLine.setVisibility(View.GONE);
						this.shareTravelLayout.setVisibility(View.GONE);
						this.phoneNumberLayout.setVisibility(View.GONE);
						this.guidTextView.setVisibility(View.GONE);
					}
					GoogleAnalyticsUtil.getInstance().sendScreen(CallCenterActivity.this, TrackerConstant.CLICK2CALL);
					break;
				case ClickToCallService.Exchange:
					GoogleAnalyticsUtil.getInstance().sendScreen(CallCenterActivity.this, TrackerConstant.AFTERSALES);
					this.exchangeAndRefundLayout.setVisibility(View.VISIBLE);
					if (this.generalSetting.isAllowContextRegistration()) {
						this.guidTextView.setText(getString(R.string.call_center_aftersales_guidance));
						this.introTextView.setText(getString(R.string.call_center_aftersales_intro));
					} else {
						vLine.setVisibility(View.GONE);
						this.shareTravelLayout.setVisibility(View.GONE);
						this.phoneNumberLayout.setVisibility(View.GONE);
						this.guidTextView.setVisibility(View.GONE);
					}
					this.exchangeTicketsTextView.setBackgroundResource(R.color.background_button_secondaction);
					this.exchangeTicketsTextView.setTextColor(getResources().getColor(R.color.textcolor_secondaction));
					this.refundTicketsTextView.setBackgroundResource(R.color.background_secondaryaction);
					this.refundTicketsTextView.setTextColor(getResources().getColor(R.color.textcolor_primaryaction));
					break;
				case 4:
					GoogleAnalyticsUtil.getInstance().sendScreen(CallCenterActivity.this, TrackerConstant.AFTERSALES);
					this.exchangeAndRefundLayout.setVisibility(View.VISIBLE);
					if (this.generalSetting.isAllowContextRegistration()) {
						this.guidTextView.setText(getString(R.string.call_center_aftersales_guidance));
						this.introTextView.setText(getString(R.string.call_center_aftersales_intro));
					} else {
						vLine.setVisibility(View.GONE);
						this.shareTravelLayout.setVisibility(View.GONE);
						this.phoneNumberLayout.setVisibility(View.GONE);
						this.guidTextView.setVisibility(View.GONE);
					}
					this.exchangeTicketsTextView.setBackgroundResource(R.color.background_secondaryaction);
					this.exchangeTicketsTextView.setTextColor(getResources().getColor(R.color.textcolor_primaryaction));
					this.refundTicketsTextView.setBackgroundResource(R.color.background_button_secondaction);
					this.refundTicketsTextView.setTextColor(getResources().getColor(R.color.textcolor_secondaction));
					break;
			}
		}
	}

	//Bind all Listeners
	public void bindAllListeners() {
		this.switchCompatView.setOnCheckedChangeListener(new SwitchCompat.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					switchCompatView.setChecked(true);
					phoneNumberLayout.setVisibility(View.VISIBLE);
					if (clickToCallScenarioId == 3 || clickToCallScenarioId == 4) {
						exchangeAndRefundLayout.setVisibility(View.VISIBLE);
					}
				} else {
					switchCompatView.setChecked(false);
					phoneNumberLayout.setVisibility(View.GONE);
					if (clickToCallScenarioId == 3 || clickToCallScenarioId == 4) {
						exchangeAndRefundLayout.setVisibility(View.GONE);
					}
				}
			}
		});
	}

	public void changePhoneNumberPrefix(View view) {
		TypedArray ar = this.getResources().obtainTypedArray(R.array.phoneNumber_country_logo);
		int len = ar.length();
		final int[] resIds = new int[len];
		for (int i = 0; i < len; i++)
			resIds[i] = ar.getResourceId(i, 0);
		ar.recycle();
		final String prefixArray[] = this.getResources().getStringArray(R.array.phoneNumber_prefix);
		phoneNumberDialogAdapter = new PhoneNumberDialogAdapter(this, prefixArray, resIds);
		AlertDialog.Builder builder = new AlertDialog.Builder(CallCenterActivity.this);

		builder.setTitle("country prefix")
				.setAdapter(phoneNumberDialogAdapter, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int index) {
						which = index;
						selectedPhoneNumberPrefixed = prefixArray[index];
						countryLogoImageView.setImageResource(resIds[index]);
					}
				}).show();
	}

	public void exchangeTickets(View view) {
		this.clickToCallScenarioId = 3;
		this.exchangeTicketsTextView.setBackgroundResource(R.color.background_button_secondaction);
		this.exchangeTicketsTextView.setTextColor(getResources().getColor(R.color.textcolor_secondaction));
		this.refundTicketsTextView.setBackgroundResource(R.color.background_secondaryaction);
		this.refundTicketsTextView.setTextColor(getResources().getColor(R.color.textcolor_primaryaction));
	}

	public void refundTickets(View view) {
		this.clickToCallScenarioId = 4;
		this.exchangeTicketsTextView.setBackgroundResource(R.color.background_secondaryaction);
		this.exchangeTicketsTextView.setTextColor(getResources().getColor(R.color.textcolor_primaryaction));
		this.refundTicketsTextView.setBackgroundResource(R.color.background_button_secondaction);
		this.refundTicketsTextView.setTextColor(getResources().getColor(R.color.textcolor_secondaction));
	}


	public void callBelgiumOrInternational(View view) {
		if (view.getId() == R.id.btn_call_center_belgium) {
			this.callNumber = this.generalSetting.getBelgiumPhoneNumber();
		} else {
			this.callNumber = this.generalSetting.getInternationalPhoneNumber();
		}
		if (clickToCallScenarioId == 3 || clickToCallScenarioId == 4) {
			GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.AFTERSALES_CATEGORY, TrackerConstant.CLICK2CALL_ACTION, this.callNumber + " " + TrackerConstant.CLICK2CALL_ACTION_LABEL);
		} else {
			GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.CLICK2CALL_CATEGORY, TrackerConstant.CLICK2CALL_ACTION, this.callNumber + " " + TrackerConstant.CLICK2CALL_ACTION_LABEL);
		}

		if (this.phoneNumberLayout.getVisibility() == View.VISIBLE) {
			if (StringUtils.isEmpty(phoneNumberTextView.getText().toString())) {
				this.llPhoneNumber.setBackground(getResources().getDrawable(R.drawable.group_error));
				tvNumberError.setVisibility(View.VISIBLE);
				tvNumberError.setText(getString(R.string.call_center_error_invalid_nr));
			} else if ((this.selectedPhoneNumberPrefixed + phoneNumberTextView.getText().toString()).length() < 7 || (this.selectedPhoneNumberPrefixed + phoneNumberTextView.getText().toString()).length() > 15) {
				this.llPhoneNumber.setBackground(getResources().getDrawable(R.drawable.group_error));
				tvNumberError.setVisibility(View.VISIBLE);
				tvNumberError.setText(getString(R.string.call_center_error_invalid_nr));
			} else {
				tvNumberError.setVisibility(View.GONE);
				this.llPhoneNumber.setBackground(getResources().getDrawable(R.drawable.station_info_tab_light_border));
				createClickToCallParameter();
				reEnableState();
			}
		} else {
			call();
			phoneNumberTextView.setText("");
		}

	}


	private void createClickToCallParameter() {
		clickToCallParameter = new ClickToCallParameter();
		createClickToCallParameterWithDossierAftersalesResponse();
	}


	private void createClickToCallParameterWithDossierAftersalesResponse() {
		ClickToCallContext clickToCallContext = new ClickToCallContext();
		clickToCallContext.setDnr(dossier.getDossierId());
		clickToCallContext.setScenario(String.valueOf(clickToCallScenarioId));
		clickToCallContext.setLanguage(settingService.getCurrentLanguagesKey());

		clickToCallContext.setTimeStamp(new Date());

		clickToCallParameter.setPhoneNumber(selectedPhoneNumberPrefixed + " " + phoneNumberTextView.getText().toString());
		clickToCallParameter.setClickToCallContext(clickToCallContext);
		ObjectToJsonUtils.getPostClickToCallParameterStr(clickToCallParameter);
	}


	// get intent object
	private void getIntentValues() {
		intent = getIntent();
		// get values( depart and return , maybe there is no return value)
		clickToCallScenarioId = intent.getIntExtra(ActivityConstant.CLICK_TO_CALL_SCENARIO_ID, 0);

		dossier = (Dossier) getIntent().getSerializableExtra(Intent_Key_dosssier);
	}

	public static Intent createIntent(Context context, int clickToCallScenarioId,
									  DossierAftersalesResponse dAftersalesResponse, Dossier dossier) {

		Intent intent = new Intent(context, CallCenterActivity.class);
		intent.putExtra(ActivityConstant.CLICK_TO_CALL_SCENARIO_ID, clickToCallScenarioId);
		intent.putExtra(Intent_Key_dosssier, dossier);
		return intent;
	}

	private final static int REQUEST_CODE_ASK_PERMISSIONS = 1002;

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case REQUEST_CODE_ASK_PERMISSIONS:
				if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
					Toast.makeText(CallCenterActivity.this, "READ_PHONE_STATE Denied", Toast.LENGTH_SHORT)
							.show();
				} else {
					Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + callNumber));
					if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

							int res = checkSelfPermission(Manifest.permission.CALL_PHONE);
							if (res != PackageManager.PERMISSION_GRANTED) {
								requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
										REQUEST_CODE_ASK_PERMISSIONS);
							}

						}
						return;
					}
					startActivity(intentCall);
				}
				break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}
	private void call() {
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + callNumber));
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

				int res = checkSelfPermission(Manifest.permission.CALL_PHONE);
				if (res != PackageManager.PERMISSION_GRANTED) {
					requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
							REQUEST_CODE_ASK_PERMISSIONS);
				}

			}
			return;
		}
		startActivity(intent);
	}

	// show progressDialog.
	private void showWaitDialog() {
		runOnUiThread(new Runnable() {
			public void run() {
				if(progressDialog == null){
					//Log.e(TAG, "Show Wait Dialog....");
					progressDialog = ProgressDialog.show(CallCenterActivity.this,
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
	}

	private void reEnableState(){
		if(generalSetting.isAllowContextRegistration()){
			if (myState == null){
				//Log.i(TAG, "myState is null");
				myState = new MyState();
			}
			clickToCall();
		}else {
			call();
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
		//clickToCallService.savePhoneNumberAndPrefix(this.selectedPhoneNumberPrefixed, phoneNumberTextView.getText().toString(), which);
		NMBSApplication.getInstance().getLoginService().setPhoneNumber(selectedPhoneNumberPrefixed, phoneNumberTextView.getText().toString());
		call();
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
					NetworkError error=(NetworkError) bundle.getSerializable(ServiceConstant.PARAM_OUT_ERROR);
					switch (error) {
						case TIMEOUT:
							hideWaitDialog();
							Toast.makeText(CallCenterActivity.this,
									getString(R.string.general_server_unavailable),
									Toast.LENGTH_LONG).show();
							//finish();
							dialogError = new DialogAlertError(CallCenterActivity.this, getResources().getString(R.string.general_information),
									getResources().getString(R.string.general_server_unavailable));
							dialogError.show();
							break;

					}
					//Log.d(TAG, "handler receive");
					break;
			}
		};
	};

	private int flow;

	public void exchange(View view) {
		flow = WebViewActivity.EXCHANGE_FLOW;
		if(dossier != null){
			String control = null;
			try {
				control = Utils.sha1((dossier.getDossierId() + "20187467"));
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			String returnUrlWithRefresh = "b-europe.nmbs://?screen=DossierDetail&DNR=" + dossier.getDossierId() + "&Refresh=YES&Navigation=InApp";
			String returnUrlWithoutRefresh = "b-europe.nmbs://?screen=DossierDetail&DNR=" + dossier.getDossierId() + "&Refresh=NO&Navigation=InApp";
			ClickToCallAftersalesParameter clickToCallAftersalesParameter = new ClickToCallAftersalesParameter(dossier.getDossierId(), control, "Exchange", returnUrlWithRefresh, returnUrlWithoutRefresh);
			ClickToCallAftersalesAsyncTask asyncTask = new ClickToCallAftersalesAsyncTask(clickToCallAftersalesParameter);
			asyncTask.execute((Void)null);
		}


	}

	public void refund(View view) {
		flow = WebViewActivity.REFUND_FLOW;
		if(dossier != null){
			String control = null;
			try {
				control = Utils.sha1((dossier.getDossierId() + "20187467"));
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			String returnUrlWithRefresh = "b-europe.nmbs://?screen=DossierDetail&DNR=" + dossier.getDossierId() + "&Refresh=YES&Navigation=InApp";
			String returnUrlWithoutRefresh = "b-europe.nmbs://?screen=DossierDetail&DNR=" + dossier.getDossierId() + "&Refresh=NO&Navigation=InApp";
			ClickToCallAftersalesParameter clickToCallAftersalesParameter = new ClickToCallAftersalesParameter(dossier.getDossierId(), control, "Refund", returnUrlWithRefresh, returnUrlWithoutRefresh);
			ClickToCallAftersalesAsyncTask asyncTask = new ClickToCallAftersalesAsyncTask(clickToCallAftersalesParameter);
			asyncTask.execute((Void)null);
		}
	}

	public class ClickToCallAftersalesAsyncTask extends AsyncTask<Void, Void, Void> {
		private ClickToCallAftersalesParameter clickToCallAftersalesParameter;
		private ISettingService settingService;
		private Context mContext;
		private String url;
		private String title = getApplicationContext().getString(R.string.aftersales_refund_unavailable);
		private String info = getResources().getString(R.string.aftersales_unavailable_generalinfo);
		public ClickToCallAftersalesAsyncTask(ClickToCallAftersalesParameter clickToCallAftersalesParameter){
			this.clickToCallAftersalesParameter = clickToCallAftersalesParameter;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			try {
				showWaitDialog();
				final ClickToCallAftersalesResponse clickToCallAftersalesResponse = clickToCallService.aftersales(clickToCallAftersalesParameter);
				if(clickToCallAftersalesResponse != null){
					Log.e("AftersalesResponse", "AftersalesResponse url..." + clickToCallAftersalesResponse.getUrl());
					if(!clickToCallAftersalesResponse.isActionAllowed()){

						if(flow == WebViewActivity.EXCHANGE_FLOW){
							title = getApplicationContext().getString(R.string.aftersales_exchange_unavailable);
						}
						info = clickToCallAftersalesResponse.getNotAllowedReason();
						if(info == null || info.isEmpty()){
							info = getResources().getString(R.string.aftersales_unavailable_generalinfo);
						}
						CallCenterActivity.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								dialogError = new DialogAlertError(CallCenterActivity.this, title, info);
								dialogError.show();
							}
						});
					}
					url = clickToCallAftersalesResponse.getUrl();
				}
			} catch (Exception e) {

				CallCenterActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						String title = getApplicationContext().getString(R.string.aftersales_refund_unavailable);
						if(flow == WebViewActivity.EXCHANGE_FLOW){
							title = getApplicationContext().getString(R.string.aftersales_exchange_unavailable);
						}
						dialogError = new DialogAlertError(CallCenterActivity.this, title, getApplicationContext().getString(R.string.aftersales_unavailable_generalinfo));
						dialogError.show();
					}
				});
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			//if(NetworkUtils.isOnline(CallCenterActivity.this)) {
			hideWaitDialog();
			if(url != null && NetworkUtils.isOnline(CallCenterActivity.this)){
				Log.e("AftersalesResponse", "DNR..." + dossier.getDossierId());
				if(flow == WebViewActivity.EXCHANGE_FLOW){
					GoogleAnalyticsUtil.getInstance().sendScreen(CallCenterActivity.this, TrackerConstant.EXCHANGE);
				}else {
					GoogleAnalyticsUtil.getInstance().sendScreen(CallCenterActivity.this,TrackerConstant.REFUND);
				}

				Utils.openWebView(CallCenterActivity.this, url, flow, dossier.getDossierId(), false);
			}

			//startActivity(WebViewActivity.createIntent(getApplicationContext(), Utils.getUrl(getApplicationContext(), url, clickToCallService)));
			//}
		}
	}

}

