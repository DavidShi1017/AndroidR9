package com.nmbs.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.conversiontracking.AdWordsConversionReporter;
import com.nmbs.R;
import com.nmbs.activities.view.AllCapTransformationMethod;
import com.nmbs.activities.view.DialogError;
import com.nmbs.activities.view.DialogMyOptions;
import com.nmbs.activity.BaseActivity;
import com.nmbs.application.NMBSApplication;
import com.nmbs.async.ProfileInfoAsyncTask;
import com.nmbs.dataaccess.database.DossierDatabaseService;
import com.nmbs.exceptions.NetworkError;
import com.nmbs.log.LogUtils;
import com.nmbs.model.DossierDetailParameter;
import com.nmbs.model.DossierDetailsResponse;
import com.nmbs.model.DossierSummary;
import com.nmbs.model.GeneralSetting;
import com.nmbs.model.LogonInfo;
import com.nmbs.model.Order.OrderParameterFeedbackTypes;
import com.nmbs.model.validation.IOrderParameterFeedback;
import com.nmbs.services.IAssistantService;
import com.nmbs.services.IClickToCallService;
import com.nmbs.services.IMasterService;
import com.nmbs.services.IMessageService;
import com.nmbs.services.IPushService;
import com.nmbs.services.impl.AsyncDossierAfterSaleResponse;
import com.nmbs.services.impl.DossierDetailsService;
import com.nmbs.services.impl.PushService;
import com.nmbs.services.impl.ServiceConstant;
import com.nmbs.services.impl.SettingService;

import com.nmbs.util.FunctionConfig;
import com.nmbs.util.GoogleAnalyticsUtil;
import com.nmbs.util.NetworkUtils;
import com.nmbs.util.RatingUtil;
import com.nmbs.util.TrackerConstant;
import com.nmbs.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class UploadDossierActivity extends BaseActivity implements IOrderParameterFeedback{

	private EditText etDnr, etEmail;
	private TextView tvDnrError, tvEmailError, tvError;
	private boolean isKnowDossier;
	private MyState myState;
	private ProgressDialog progressDialog;
	private final static String TAG = UploadDossierActivity.class.getSimpleName();
	private IAssistantService assistantService;
	private SettingService settingService;
	private IPushService pushService;
	private Button btnAdd;
	private int pageFlag;
	private Uri uri;
	private String dnrString = "";
	private String emailString = "";
	private RelativeLayout rlDnr, rlEmail;
	private boolean dnrValid, emailValid;
	//public static int ContextMyTickets = 1;
	private DossierDetailsService dossierDetailsService = NMBSApplication.getInstance().getDossierDetailsService();
	private TextView tvMenuTicketCount, tvMenuRealtimeCount, tvMenuMessageCount;
	private IMasterService masterService;
	private IClickToCallService clickToCallService;
	private IMessageService messageService;
	private DrawerLayout mDrawerLayout;
	private LinearLayout mDrawerList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Utils.setToolBarStyle(this);
		settingService = ((NMBSApplication) getApplication()).getSettingService();
		//settingService.initLanguageSettings();
		setContentView(R.layout.activity_upload_tickets);
		assistantService = ((NMBSApplication)getApplication()).getAssistantService();
		messageService = ((NMBSApplication) getApplication()).getMessageService();
		masterService = ((NMBSApplication) getApplication()).getMasterService();
		clickToCallService = ((NMBSApplication) getApplication()).getClickToCallService();
		pushService = ((NMBSApplication)getApplication()).getPushService();
		pageFlag = getIntent().getIntExtra("pageFlag", 0);
		if(pageFlag == 0){
			GoogleAnalyticsUtil.getInstance().sendScreen(UploadDossierActivity.this, TrackerConstant.UPLOADTICKET);
		}else{
			GoogleAnalyticsUtil.getInstance().sendScreen(UploadDossierActivity.this, TrackerConstant.UPLOADTICKET_DNR);
		}
		uri = (Uri) getIntent().getParcelableExtra("uri");
		bindAllViewsElement();
		bindAllListeners();
	}


	private void bindAllViewsElement() {
		etDnr = (EditText) findViewById(R.id.et_dnr);
		etDnr.setTransformationMethod(new AllCapTransformationMethod());
		etEmail = (EditText) findViewById(R.id.et_email);
		tvDnrError = (TextView) findViewById(R.id.tv_dnr_error);
		tvEmailError = (TextView) findViewById(R.id.tv_email_error);
		rlDnr = (RelativeLayout) findViewById(R.id.rl_dnr);
		rlEmail = (RelativeLayout) findViewById(R.id.rl_email);
		//tvError = (TextView) findViewById(R.id.tv_error);
		btnAdd = (Button) findViewById(R.id.btn_add);
		btnAdd.setText(getResources().getString(R.string.add_existing_ticket_addnewticket).toUpperCase());

		if (uri != null) {
			String dnr = uri.getQueryParameter("dnr");
			String email = uri.getQueryParameter("email");
			etDnr.setText(dnr);
			etEmail.setText(email);
			addTicket(etDnr);
		}else if(getIntent().getStringExtra("dnr") != null
				&& getIntent().getStringExtra("email") != null && !getIntent().getStringExtra("email").isEmpty()){
			String dnr = getIntent().getStringExtra("dnr");
			String email = getIntent().getStringExtra("email");
			etDnr.setText(dnr);
			etEmail.setText(email);
			if(getIntent().getStringExtra("errorMessage") != null){
				if(!isFinishing()){
					DialogError dialogError = new DialogError(UploadDossierActivity.this, getResources().getString(R.string.upload_tickets_failure_title), getIntent().getStringExtra("errorMessage"));
					dialogError.show();
				}

			}
		} else {
			setViewStateBasedOnModel();
		}
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (LinearLayout) findViewById(R.id.ll_left_menus);
	}

	public void addTicket(View view){
		dnrString = etDnr.getText().toString().toUpperCase();
		emailString = etEmail.getText().toString();
		//Log.e(TAG, "dnrString..." + dnrString);
		//Log.e(TAG, "emailString..." + emailString);
		if(dnrString.isEmpty()){
			tvDnrError.setVisibility(View.VISIBLE);
			rlDnr.setBackground(getResources().getDrawable(R.drawable.group_error));
			tvDnrError.setText(getString(R.string.upload_tickets_error_dnr_empty));
			return;
		}
		if(emailString.isEmpty()){
			//tvError.setVisibility(View.VISIBLE);
			rlEmail.setBackground(getResources().getDrawable(R.drawable.group_error));
			tvEmailError.setVisibility(View.VISIBLE);
			tvEmailError.setText(getString(R.string.upload_tickets_error_email_empty));
			return;
		}
		//dnrString = "GSFMPQX";
		//emailString = "juryq@163.com";
		checkDnr();
		checkEmail();
		//Log.e(TAG, "dnrValid..." + dnrValid);
		//Log.e(TAG, "emailValid..." + emailValid);
		if(dnrValid && emailValid){
			reEnableState();
			//settingService.setEmail(emailString);
			NMBSApplication.getInstance().getLoginService().setEmail(emailString);
		}

		if(pageFlag == 0){
			GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.UPLOAD_TICKET_CATEGORY,TrackerConstant.UPLOAD_TICKET_SELECT_ADD_NEW_TICKET,"");
		}else{
			GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.UPLOAD_TICKET_DNR_CATEGORY,TrackerConstant.UPLOAD_TICKET_SELECT_ADD_NEW_TICKET,"");
		}


	}

	private void bindAllListeners() {
		/*etDnr.addTextChangedListener(new dnrChangedListener());
		etEmail.addTextChangedListener(new emailChangedListener());*/
		etDnr.setOnFocusChangeListener(new DnrFocusChangeListener());
		etEmail.setOnFocusChangeListener(new EmailFocusChangeListener());
	}
	private class DnrFocusChangeListener implements View.OnFocusChangeListener{

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (!hasFocus){
				checkDnr();
			}
		}
	}


	private class dnrChangedListener implements TextWatcher {
		public void afterTextChanged(Editable s) {

			dnrString = s.toString();
			if(!dnrString.isEmpty() && !emailString.isEmpty()){
				//tvError.setVisibility(View.GONE);
			}else{
				//tvError.setVisibility(View.VISIBLE);
			}
			checkDnr();
		}
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
		public void onTextChanged(CharSequence s, int start, int before, int count) {}

	}

	private void checkDnr(){
		dnrString = etDnr.getText().toString();
		Pattern dnrPattern = Pattern.compile("^[a-zA-Z]{7}");
		if (!dnrPattern.matcher(dnrString).matches()) {
			dnrValid = false;
			tvDnrError.setVisibility(View.VISIBLE);
			tvDnrError.setText(getString(R.string.upload_tickets_error_dnr_invalid));
			rlDnr.setBackground(getResources().getDrawable(R.drawable.group_error));
			/*if(dnrString.isEmpty()){
				//tvDnrError.setVisibility(View.GONE);
				tvEmailError.setText(getString(R.string.upload_tickets_error_dnr_empty));
			}*/
		}else {
			dnrValid = true;
			rlDnr.setBackground(getResources().getDrawable(R.drawable.group_default));
			tvDnrError.setVisibility(View.GONE);
		}
	}

	private void checkEmail(){
		emailString = etEmail.getText().toString();
		if (!Utils.checkEmailPattern(emailString.toString())) {
			emailValid = false;
			tvEmailError.setVisibility(View.VISIBLE);
			tvEmailError.setText(getString(R.string.upload_tickets_error_email_invalid));
			rlEmail.setBackground(getResources().getDrawable(R.drawable.group_error));
			if(emailString.isEmpty()){
				//tvEmailError.setVisibility(View.GONE);
				tvEmailError.setText(getString(R.string.upload_tickets_error_email_empty));
			}
		}else {
			emailValid = true;
			rlEmail.setBackground(getResources().getDrawable(R.drawable.group_default));
			tvEmailError.setVisibility(View.GONE);
		}
	}
	private class EmailFocusChangeListener implements View.OnFocusChangeListener{

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (!hasFocus){
				checkEmail();
			}
		}
	}
	private class emailChangedListener implements TextWatcher {
		public void afterTextChanged(Editable s) {
			emailString = s.toString();
			if(!dnrString.isEmpty() && !emailString.isEmpty()){
				//tvError.setVisibility(View.GONE);
			}else{
				///tvError.setVisibility(View.VISIBLE);
			}
			checkEmail();
		}
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
		public void onTextChanged(CharSequence s, int start, int before, int count) {}

	}

	private void setViewStateBasedOnModel() {

		LogonInfo loginInfo = NMBSApplication.getInstance().getLoginService().getLogonInfo();
		String email = "";
		LogUtils.e("email", "setViewStateBasedOnModel email--------->" + email);
		if(loginInfo != null){
			email = loginInfo.getEmail();
		}
		etEmail.setText(email);
	}

	public void validationOrderParameterFeedback(
			OrderParameterFeedbackTypes validationFeedback) {
		switch (validationFeedback) {
		case EMAIL_INCORRECT:
			
			Toast.makeText(this,
					getString(R.string.alert_register_error_email_wrong),
					Toast.LENGTH_SHORT).show();

			break;
		case EMPTY_EMAIL:
			Toast.makeText(this,
					getString(R.string.alert_register_error_email_empty),
					Toast.LENGTH_SHORT).show();

			break;
		case EMPTY_DNR:
			Toast.makeText(this,
					getString(R.string.add_existing_ticket_no_dnr),
					Toast.LENGTH_SHORT).show();
			break;
		case DNR_INCORRECT:
			Toast.makeText(this,
					getString(R.string.add_existing_ticket_dnr_incorrect),
					Toast.LENGTH_SHORT).show();
			
			break;	
			
		default:
			break;
		}
		
	} 
	
	private class MyState {
		
		public AsyncDossierAfterSaleResponse asyncDossierAfterSaleResponse;
		public DossierDetailsResponse dossier;
		private boolean isRefreshed;
		public void unRegisterHandler(){
			if (asyncDossierAfterSaleResponse != null){
				asyncDossierAfterSaleResponse.unregisterHandler();
			}
		}
		
		public void registerHandler(Handler handler){
			if (asyncDossierAfterSaleResponse != null){
				asyncDossierAfterSaleResponse.registerHandler(handler);
			}
		}
	}
	
	private void reEnableState(){
		if (myState == null){
			//Initial call
			//Log.i(TAG, "myState is null");
			myState = new MyState();					
				//getDossierData("MXMYQWZ"); Testing
															
		} 
		getDossierData();
		//Log.i(TAG, "Is Refreshed? " + myState.isRefreshed);

	}
	
	// refresh Data
	private void getDossierData(){
		DossierSummary dossierSummary = dossierDetailsService.getDossier(dnrString);
		if(dossierSummary != null){
			isKnowDossier = true;
		}else{
			isKnowDossier = false;
		}

		DossierDetailParameter dossier = new DossierDetailParameter(dnrString, emailString);
		//Log.e(TAG, "emailString..." + emailString);
		List<DossierDetailParameter> dossiers = new ArrayList<DossierDetailParameter>();
		dossiers.add(dossier);
		myState.asyncDossierAfterSaleResponse = assistantService.refrshDossierDetail(dossiers, settingService, true, false);
		myState.asyncDossierAfterSaleResponse.registerHandler(mHandler);		
		myState.registerHandler(mHandler);
		showWaitDialog();
	}

	
	private void responseReceived(DossierDetailsResponse dossierResponse) {
		//Log.e(TAG, "responseReceived....");
		hideWaitDialog();
		if(dossierResponse != null){
			RatingUtil.saveDNRUpload(getApplicationContext());
			DossierDatabaseService dossierDatabaseService = new DossierDatabaseService(getApplicationContext());
			DossierSummary dossierSummary = dossierDatabaseService.selectDossier(dossierResponse.getDossier().getDossierId());
			if(dossierSummary != null){
				/*AdWordsConversionReporter.reportWithConversionId(this.getApplicationContext(),"1025893038","nPcPCKimmmQQrsWX6QM","0.00",true);
				AdWordsConversionReporter.reportWithConversionId(this.getApplicationContext(),"1024321798","eYz4COPUiGQQhtK36AM","0.00",true);
				AdWordsConversionReporter.reportWithConversionId(this.getApplicationContext(),"990257741","FcC6COaTk2QQzcSY2AM","0.00",true);
				AdWordsConversionReporter.reportWithConversionId(this.getApplicationContext(),"955368664","tJl3CLyommQQ2InHxwM","0.00",true);
				AdWordsConversionReporter.reportWithConversionId(this.getApplicationContext(),"939594800","z6JjCNeommQQsKiEwAM","0.00",true);
				AdWordsConversionReporter.reportWithConversionId(this.getApplicationContext(),"1018955333","mPCJCNnvhmQQxYzw5QM","0.00",true);*/
				//Log.e(TAG, "dossierSummary is not null....");
				//Log.e(TAG, "dossier details is===...." + dossierSummary.getDossierDetails());
				//if(!dossierSummary.isDossierPushEnabled()){
				dossierDetailsService.enableSubscription(dossierResponse.getDossier(), handlerEnabled, settingService.getCurrentLanguagesKey());
			}
			else{
				if(!isFinishing()){
					DialogError dialogError = new DialogError(UploadDossierActivity.this, getResources().getString(R.string.upload_tickets_failure_title), getResources().getString(R.string.upload_tickets_failure_notravelsegment));
					dialogError.show();
				}

			}
		}

		/*Toast.makeText(
				AddExistingTicketActivity.this,
				getString(R.string.alert_add_existing_ticket_success),
				Toast.LENGTH_LONG).show();*/

		
		myState.dossier = dossierResponse;
/*		if(myState.dossier != null){
			assistantService.setCurrentTicketDnr(myState.dossierAftersalesResponse.getDnrId());
		}*/
		//createDialog();

		myState.unRegisterHandler();
		//myState.asyncDossierAfterSaleResponse = null;		
		myState.isRefreshed = true;
		
		//hideProgressDialog(progressDialog);
		//Log.d(TAG, "show response");

		startActivity(MyTicketsActivity.createIntent(this));
		finish();
	}

	private Handler handlerEnabled = new Handler(){
		public void handleMessage(Message msg) {
			//Log.e(TAG, "handlerEnabled...");
			//Log.e(TAG, "msg.what..." + msg.what);
			DossierSummary dossierSummary = dossierDetailsService.getDossier(dnrString);
			//Log.e(TAG, "dossierSummaryt..." + dossierSummary);
			switch (msg.what) {
				case 0:
					if(dossierSummary != null){
						if(NMBSApplication.getInstance().getTestService().isCreateSubscription()){
							dossierSummary.setDossierPushEnabled(true);
							//Log.e(TAG, "Test mode...Not trigger create subscriptions call.." + msg.what);
							//Log.e(TAG, "Test mode...Not trigger create subscriptions call..setDossierPushEnabled...true");
						}
						dossierSummary.setDossierPushEnabled(false);
						dossierDetailsService.updateDossier(dossierSummary);
						dossierDetailsService.setCurrentDossierSummary(dossierSummary);
					}
					if(pageFlag == NMBSApplication.PAGE_Dossier_Details){
						GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.UPLOAD_TICKET_CATEGORY, TrackerConstant.DOSSIER_ERROR_NOTIFICATION, "");
					}else {
						TrackNotfication(TrackerConstant.UPLOAD_TICKET_ERROR_ENABLE_NOTIFICATION);
					}
					LogUtils.e(TAG, "UPLOAD_TICKET_ERROR_ENABLE_NOTIFICATION...");
					break;
				case 1:
					if(pageFlag == NMBSApplication.PAGE_Dossier_Details){
						GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.UPLOAD_TICKET_CATEGORY, TrackerConstant.DOSSIER_ERROR_NOTIFICATION, "");
					}else {
						TrackNotfication(TrackerConstant.UPLOAD_TICKET_ERROR_ENABLE_NOTIFICATION);
					}
					LogUtils.e(TAG, "UPLOAD_TICKET_ERROR_ENABLE_NOTIFICATION...");
					if (dossierSummary != null){
						dossierSummary.setDossierPushEnabled(true);
						dossierDetailsService.updateDossier(dossierSummary);
						dossierDetailsService.setCurrentDossierSummary(dossierSummary);
						//Log.d("Connection", "status..isPushEnabled..." + dossierDetailsService.isPushEnabled(dossier));
					}
					break;
				case 2:
					if(dossierSummary != null){
						dossierSummary.setDossierPushEnabled(true);
						dossierDetailsService.updateDossier(dossierSummary);
						dossierDetailsService.setCurrentDossierSummary(dossierSummary);
						LogUtils.d("Connection", "status..isPushEnabled..." + dossierSummary.isDossierPushEnabled());
					}
					break;
				case PushService.USER_ERROR:
					if(pageFlag == NMBSApplication.PAGE_Dossier_Details){
						GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.UPLOAD_TICKET_CATEGORY, TrackerConstant.DOSSIER_ERROR_NOTIFICATION, "");
					}else {
						TrackNotfication(TrackerConstant.UPLOAD_TICKET_ERROR_ENABLE_NOTIFICATION);
					}
					break;
			}
		};
	};

	private void TrackNotfication(String action){
		if(pageFlag == NMBSApplication.PAGE_UploadTicket){
			GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.UPLOAD_TICKET_CATEGORY, action, "");
		}else if(pageFlag == NMBSApplication.PAGE_UploadTicket_DNR){
			GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.UPLOAD_TICKET_DNR_CATEGORY, action, "");
		}else if(pageFlag == NMBSApplication.PAGE_Dossier_Details){
			GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.DOSSIER_CATEGORY, action, "");
		}
	}
	
	//Handler which is called when the App is refreshed.
		// The what parameter of the message decides if there was an error or not.
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ServiceConstant.MESSAGE_WHAT_OK:
				responseReceived(myState.asyncDossierAfterSaleResponse.getDossierAftersalesResponse());
				// showView();
				
				break;
			case ServiceConstant.MESSAGE_WHAT_ERROR:
				if(pageFlag == 0) {
					GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.UPLOAD_TICKET_CATEGORY, TrackerConstant.UPLOAD_TICKET_ERROR_ADD_NEW_TICKET, "");
				}else{
					GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.UPLOAD_TICKET_DNR_CATEGORY,TrackerConstant.UPLOAD_TICKET_ERROR_ADD_NEW_TICKET,"");
				}
				hideWaitDialog();
				Bundle bundle = msg.getData();
				NetworkError error = (NetworkError) bundle.getSerializable(ServiceConstant.PARAM_OUT_ERROR);
				String responseErrorMessage = bundle.getString(ServiceConstant.PARAM_OUT_ERROR_MESSAGE);
				switch (error) {
				case TIMEOUT:

					responseErrorMessage = getResources().getString(R.string.general_server_unavailable);

					// finish();
					break;
				case wrongCombination:
					responseErrorMessage = getResources().getString(R.string.upload_tickets_failure_parameter_mismatch);

				// finish();
				break;
				case donotContainTicke:
					responseErrorMessage = getResources().getString(R.string.upload_tickets_failure_notravelsegment);

					// finish();
					break;
				case journeyPast:
					responseErrorMessage = getResources().getString(R.string.alert_add_existing_ticket_journey_past_description);

					// finish();
					break;
				case CustomError:
					hideWaitDialog();
					if (responseErrorMessage == null) {
						responseErrorMessage = getString(R.string.general_server_unavailable);
					}

					break;
				default:
					break;
				}
				if(!isFinishing()){
					DialogError dialogError = new DialogError(UploadDossierActivity.this, getResources().getString(R.string.upload_tickets_failure_title), responseErrorMessage);
					dialogError.show();
				}

				//Log.d(TAG, "Upload dossier failure, the error msg is.........." + responseErrorMessage);
				break;
			}
		};
	};
	
	// show progressDialog.
	
	private void showWaitDialog() {
		if(isFinishing()){
			return;
		}
		if (progressDialog == null) {
			progressDialog = ProgressDialog.show(this,
					getString(R.string.alert_loading),
					getString(R.string.alert_waiting), true);
		}
	}

	// hide progressDialog
	private void hideWaitDialog() {
		try{
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}catch (Exception e){
			e.printStackTrace();
		}

	}
	
	public static Intent createUploadDossierIntent(Context context, int pageFlag, Uri uri, String dnr, String email, String errorMessage) {
		LogUtils.e("email", "email------>"+ email);
		Intent intent = new Intent(context, UploadDossierActivity.class);
		intent.putExtra("pageFlag", pageFlag);
		intent.putExtra("uri", uri);
		intent.putExtra("dnr", dnr);
		intent.putExtra("email", email);
		intent.putExtra("errorMessage", errorMessage);
		return intent;
	}





	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			if (uri != null) {
				Intent myIntent = new Intent(this.getApplicationContext().getApplicationContext(), MainActivity.class);
				myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				finish();
			}
		}
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		if(drawerOpen){
			mDrawerLayout.closeDrawer(GravityCompat.END);
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void back(View view){
		finish();
	}

	public void help(View view){
		view.requestFocus();
		startActivity(WizardActivity.createIntent(this, WizardActivity.Wizard_MyTickets));
		if(pageFlag == 0){
			GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.UPLOAD_TICKET_CATEGORY, TrackerConstant.UPLOAD_TICKET_SELECT_WIZARD,"");
		}else{
			GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.UPLOAD_TICKET_DNR_CATEGORY,TrackerConstant.UPLOAD_TICKET_SELECT_WIZARD,"");
		}
	}

	public void messages(View view) {

		isGoto = true;
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		if(drawerOpen){
			mDrawerLayout.closeDrawer(GravityCompat.END);
			mDrawerLayout.setDrawerListener(
					new DrawerLayout.SimpleDrawerListener() {
						@Override
						public void onDrawerClosed(View drawerView) {
							super.onDrawerClosed(drawerView);
							if(isGoto){
								startActivity(com.nmbs.activities.MessageActivity.createIntent(UploadDossierActivity.this, messageService.getMessageResponse()));
								isGoto = false;
								finish();
							}
						}
					});
		}
	}

	public void test(View view){
		startActivity(TestActivity.createIntent(this));
		finish();
	}

	public void stationBoard(View view){

		isGoto = true;
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		if(drawerOpen){
			mDrawerLayout.closeDrawer(GravityCompat.END);
			mDrawerLayout.setDrawerListener(
					new DrawerLayout.SimpleDrawerListener() {
						@Override
						public void onDrawerClosed(View drawerView) {
							super.onDrawerClosed(drawerView);
							if(isGoto){
								startActivity(com.nmbs.activities.StationBoardActivity.createIntent(UploadDossierActivity.this, null));
								isGoto = false;
								finish();
							}
						}
					});
		}
	}

	public void stations(View view){

		isGoto = true;
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		if(drawerOpen){
			mDrawerLayout.closeDrawer(GravityCompat.END);
			mDrawerLayout.setDrawerListener(
					new DrawerLayout.SimpleDrawerListener() {
						@Override
						public void onDrawerClosed(View drawerView) {
							super.onDrawerClosed(drawerView);
							if(isGoto){
								startActivity(com.nmbs.activities.StationInfoActivity.createIntent(UploadDossierActivity.this));
								isGoto = false;
								finish();
							}
						}
					});
		}
		//startActivity(com.nmbs.activities.CallCenterActivity.createIntent(MainActivity.this,0,null,null));
	}

	public void realtimeAlerts(View view){

		isGoto = true;
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		if(drawerOpen){
			mDrawerLayout.closeDrawer(GravityCompat.END);
			mDrawerLayout.setDrawerListener(
					new DrawerLayout.SimpleDrawerListener() {
						@Override
						public void onDrawerClosed(View drawerView) {
							super.onDrawerClosed(drawerView);
							if(isGoto){
								startActivity(com.nmbs.activities.AlertActivity.createIntent(UploadDossierActivity.this));
								isGoto = false;
								finish();
							}
						}
					});
		}
	}

	public void bookTicktes(View view){
		GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.HOME,TrackerConstant.HOME_BOOK_YOUR_TICKET,TrackerConstant.HOME_BOOK_YOUR_TICKET_LABEL);
		final GeneralSetting generalSetting = masterService.loadGeneralSetting();

		isGoto = true;
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		if(drawerOpen){
			mDrawerLayout.closeDrawer(GravityCompat.END);
			mDrawerLayout.setDrawerListener(
					new DrawerLayout.SimpleDrawerListener() {
						@Override
						public void onDrawerClosed(View drawerView) {
							super.onDrawerClosed(drawerView);
							if(isGoto){
								if(generalSetting != null && generalSetting.getBookingUrl() != null && !generalSetting.getBookingUrl().isEmpty()){
									//Utils.openProwser(UploadDossierActivity.this, generalSetting.getBookingUrl(), clickToCallService);
									if(NetworkUtils.isOnline(UploadDossierActivity.this)) {
										GoogleAnalyticsUtil.getInstance().sendScreen(UploadDossierActivity.this, TrackerConstant.BOOKING);
										startActivity(WebViewActivity.createIntent(UploadDossierActivity.this, Utils.getUrl(
												generalSetting.getBookingUrl()), WebViewActivity.BOOKING_FLOW, ""));
									}
								}
								isGoto = false;
								finish();
							}
						}
					});
		}
	}

	public void lowestFares(View view){
		GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.HOME,TrackerConstant.HOME_SELECT_LOWESTFARES,TrackerConstant.HOME_SELECT_LOWESTFARES_LABEL);
		final GeneralSetting generalSetting = masterService.loadGeneralSetting();

		isGoto = true;
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		if(drawerOpen){
			mDrawerLayout.closeDrawer(GravityCompat.END);
			mDrawerLayout.setDrawerListener(
					new DrawerLayout.SimpleDrawerListener() {
						@Override
						public void onDrawerClosed(View drawerView) {
							super.onDrawerClosed(drawerView);
							if(isGoto){
								if(generalSetting != null && generalSetting.getLffUrl()!= null && !generalSetting.getLffUrl().isEmpty()){
									//Utils.openProwser(UploadDossierActivity.this, generalSetting.getLffUrl(), clickToCallService);
									if(NetworkUtils.isOnline(UploadDossierActivity.this)) {
										GoogleAnalyticsUtil.getInstance().sendScreen(UploadDossierActivity.this, TrackerConstant.LLF);
										startActivity(WebViewActivity.createIntent(UploadDossierActivity.this,
												Utils.getUrl(generalSetting.getLffUrl()), WebViewActivity.BOOKING_FLOW, ""));
									}
								}
								isGoto = false;
								finish();
							}
						}
					});
		}
	}
	public void trainschedules(View view){
		isGoto = true;
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		if(drawerOpen){
			mDrawerLayout.closeDrawer(GravityCompat.END);
			mDrawerLayout.setDrawerListener(
					new DrawerLayout.SimpleDrawerListener() {
						@Override
						public void onDrawerClosed(View drawerView) {
							super.onDrawerClosed(drawerView);
							if(isGoto){
								startActivity(ScheduleSearchActivity.createIntent(UploadDossierActivity.this));
								isGoto = false;
								finish();
							}
						}
					});
		}
	}
	private boolean isGoto;
	public void settings(View view){
		isGoto = true;
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		if(drawerOpen){
			mDrawerLayout.closeDrawer(GravityCompat.END);
			mDrawerLayout.setDrawerListener(
					new DrawerLayout.SimpleDrawerListener() {
						@Override
						public void onDrawerClosed(View drawerView) {
							super.onDrawerClosed(drawerView);
							if(isGoto){
								startActivity(SettingsActivity.createIntent(UploadDossierActivity.this));
								isGoto = false;
								finish();
							}
						}
					});
		}
	}

	public void about(View view){

		isGoto = true;
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		if(drawerOpen){
			mDrawerLayout.closeDrawer(GravityCompat.END);
			mDrawerLayout.setDrawerListener(
					new DrawerLayout.SimpleDrawerListener() {
						@Override
						public void onDrawerClosed(View drawerView) {
							super.onDrawerClosed(drawerView);
							if(isGoto){
								startActivity(WizardActivity.createIntent(UploadDossierActivity.this, WizardActivity.Wizard_Home));
								isGoto = false;
								finish();
							}
						}
					});
		}
	}
	public void myTickets(View view){

		isGoto = true;
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		if(drawerOpen){
			mDrawerLayout.closeDrawer(GravityCompat.END);
			mDrawerLayout.setDrawerListener(
					new DrawerLayout.SimpleDrawerListener() {
						@Override
						public void onDrawerClosed(View drawerView) {
							super.onDrawerClosed(drawerView);
							if(isGoto){
								startActivity(MyTicketsActivity.createIntent(UploadDossierActivity.this));
								isGoto = false;
								finish();
							}
						}
					});
		}
	}

	public void uploadTickets(View view){
		mDrawerLayout.closeDrawer(GravityCompat.END);
	}
	public void myOption(View view){
		//startActivity(LoginActivity.createIntent(MainActivity.this));
		final GeneralSetting generalSetting = masterService.loadGeneralSetting();
		isGoto = true;
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		if(drawerOpen){
			mDrawerLayout.closeDrawer(GravityCompat.END);
			mDrawerLayout.setDrawerListener(
					new DrawerLayout.SimpleDrawerListener() {
						@Override
						public void onDrawerClosed(View drawerView) {
							super.onDrawerClosed(drawerView);
							if(isGoto){
								if(NMBSApplication.getInstance().getLoginService().isLogon()){
									if(generalSetting.getCommercialTtlListUrl() != null && !generalSetting.getCommercialTtlListUrl().isEmpty()){
										startActivity(WebViewActivity.createIntent(getApplicationContext(),
												Utils.getUrl(generalSetting.getCommercialTtlListUrl()), WebViewActivity.NORMAL_FLOW, ""));
										GoogleAnalyticsUtil.getInstance().sendScreen(UploadDossierActivity.this, TrackerConstant.CommercialTTLListUrl);
									}
								}else{
									if(!isFinishing()){
										DialogMyOptions dialogMyOptions = new DialogMyOptions(UploadDossierActivity.this);
										dialogMyOptions.show();
									}

								}
								isGoto = false;
							}
						}
					});
		}else{
			if(NMBSApplication.getInstance().getLoginService().isLogon()){
				if(generalSetting.getCommercialTtlListUrl() != null && !generalSetting.getCommercialTtlListUrl().isEmpty()){
					startActivity(WebViewActivity.createIntent(getApplicationContext(),
							Utils.getUrl(generalSetting.getCommercialTtlListUrl()), WebViewActivity.NORMAL_FLOW, ""));
					GoogleAnalyticsUtil.getInstance().sendScreen(UploadDossierActivity.this, TrackerConstant.CommercialTTLListUrl);
				}
			}else{
				if(!isFinishing()){
					DialogMyOptions dialogMyOptions = new DialogMyOptions(this);
					dialogMyOptions.show();
				}

			}
		}


	}

	public void loginOrManage(View view){
		//startActivity(LoginActivity.createIntent(MainActivity.this));
		final GeneralSetting generalSetting = masterService.loadGeneralSetting();
		isGoto = true;
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		if(drawerOpen){
			mDrawerLayout.closeDrawer(GravityCompat.END);
			mDrawerLayout.setDrawerListener(
					new DrawerLayout.SimpleDrawerListener() {
						@Override
						public void onDrawerClosed(View drawerView) {
							super.onDrawerClosed(drawerView);
							if(isGoto){
								if(NMBSApplication.getInstance().getLoginService().isLogon()){
									ProfileInfoAsyncTask profileInfoAsyncTask = new ProfileInfoAsyncTask(getApplicationContext(), null);
									profileInfoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
									startActivity(WebViewCreateActivity.createIntent(getApplicationContext(), Utils.getUrl(generalSetting.getProfileOverviewUrl())));
									GoogleAnalyticsUtil.getInstance().sendScreen(UploadDossierActivity.this, TrackerConstant.ProfileOverviewUrl);
								}else{
									startActivity(LoginActivity.createIntent(getApplicationContext(), ""));
								}
								isGoto = false;
							}
						}
					});
		}else{
			if(NMBSApplication.getInstance().getLoginService().isLogon()){
				ProfileInfoAsyncTask profileInfoAsyncTask = new ProfileInfoAsyncTask(getApplicationContext(), null);
				profileInfoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				startActivity(WebViewCreateActivity.createIntent(getApplicationContext(), Utils.getUrl(generalSetting.getProfileOverviewUrl())));
				GoogleAnalyticsUtil.getInstance().sendScreen(UploadDossierActivity.this, TrackerConstant.ProfileOverviewUrl);
			}else{
				startActivity(LoginActivity.createIntent(getApplicationContext(), ""));
			}
		}
	}
	private TextView tvMenuOptionCount;
	private RelativeLayout rlMenuMyOption;
	public void clickMenu(View view) {
		//startActivity(MenuActivity.createIntent(this, ticketCount, realtimeCount, messateCount));
		tvMenuTicketCount = (TextView) findViewById(R.id.tv_menu_ticket_count);
		tvMenuRealtimeCount = (TextView) findViewById(R.id.tv_menu_realtime_count);
		tvMenuMessageCount = (TextView) findViewById(R.id.tv_menu_message_count);
		tvMenuTicketCount.setText("(" + MainActivity.ticketCount + ")");
		tvMenuRealtimeCount.setText("(" + MainActivity.realtimeCount + ")");
		tvMenuMessageCount.setText("(" + MainActivity.messateCount + ")");
		RelativeLayout rlMenuAlerts = (RelativeLayout) findViewById(R.id.rl_menu_traintickets_content_realtimealerts);
		if(FunctionConfig.kFunManagePush){
			rlMenuAlerts.setVisibility(View.VISIBLE);
		}else {
			rlMenuAlerts.setVisibility(View.GONE);
		}
		rlMenuMyOption = (RelativeLayout) findViewById(R.id.rl_menu_traintickets_content_myoptions);
		tvMenuOptionCount = (TextView) findViewById(R.id.tv_menu_option_count);
		tvMenuOptionCount.setText("(" + MainActivity.optionCount + ")");
		TextView tvMenuLogon = (TextView) findViewById(R.id.tv_menu_logon);
		RelativeLayout rlMenuLogin = (RelativeLayout) findViewById(R.id.rl_menu_traintickets_content_login);
		if(!NMBSApplication.getInstance().getLoginService().isLogon()){
			rlMenuMyOption.setAlpha(0.3f);
			tvMenuOptionCount.setText("");
			tvMenuLogon.setText(getResources().getString(R.string.menu_content_loginorcreateprofile));
			if(NMBSApplication.getInstance().getMasterService().loadGeneralSetting() != null
					&& NMBSApplication.getInstance().getMasterService().loadGeneralSetting().getProfileOverviewUrl() != null
					&& !NMBSApplication.getInstance().getMasterService().loadGeneralSetting().getProfileOverviewUrl().isEmpty()){
				rlMenuLogin.setVisibility(View.VISIBLE);
			}else {
				rlMenuLogin.setVisibility(View.GONE);
			}
		}else {
			tvMenuLogon.setText(getResources().getString(R.string.menu_content_manageprofile));
		}
		if(NMBSApplication.getInstance().getMasterService().loadGeneralSetting() != null
				&& NMBSApplication.getInstance().getMasterService().loadGeneralSetting().getCommercialTtlListUrl() != null
				&& !NMBSApplication.getInstance().getMasterService().loadGeneralSetting().getCommercialTtlListUrl().isEmpty()){
			rlMenuMyOption.setVisibility(View.VISIBLE);
		}else {
			rlMenuMyOption.setVisibility(View.GONE);
		}
		mDrawerLayout.openDrawer(GravityCompat.END);
	}

	public void close(View view){
		finish();
	}
}
