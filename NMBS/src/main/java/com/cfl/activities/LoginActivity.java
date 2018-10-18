package com.cfl.activities;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;

import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.cfl.R;
import com.cfl.activities.view.DialogAlertError;
import com.cfl.activities.view.DialogErrorLogin;
import com.cfl.activities.view.DialogInfo;
import com.cfl.activities.view.DialogMyOptions;
import com.cfl.activity.BaseActivity;
import com.cfl.application.NMBSApplication;
import com.cfl.async.AutoRetrievalDossiersTask;
import com.cfl.async.CheckOptionAsyncTask;
import com.cfl.async.ForgotPwdAsyncTask;
import com.cfl.async.LoginAsyncTask;

import com.cfl.async.ProfileInfoAsyncTask;
import com.cfl.async.ResendAsyncTask;

import com.cfl.log.LogUtils;

import com.cfl.model.GeneralSetting;
import com.cfl.model.LogonInfo;
import com.cfl.services.IClickToCallService;
import com.cfl.services.IMasterService;

import com.cfl.services.impl.LoginService;
import com.cfl.util.GoogleAnalyticsUtil;
import com.cfl.util.NetworkUtils;
import com.cfl.util.TrackerConstant;
import com.cfl.util.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;


public class LoginActivity extends BaseActivity implements DialogErrorLogin.ButtonCallback {

	private IMasterService masterService;
	private EditText etEmail, etPwd;
	private String emailString, pwdString;
	private IClickToCallService clickToCallService;
	private boolean emailValid, pwdValid, showPwd = true;
	private RelativeLayout rlEmail, rlPwd;
	private TextView tvEmailError;
	private ProgressDialog progressDialog;
	private static final int LOGIN = 0;
	private static final int CTETATE = 1;
	private LoginService loginService;
	private LogonInfo logonInfo;
	private TextView tvMenuTicketCount, tvMenuRealtimeCount, tvMenuMessageCount;
	private DrawerLayout mDrawerLayout;
	private LinearLayout mDrawerList;

	private static final String EMAIL = "email";
	private static final String USER_POSTS = "user_posts";
	private static final String AUTH_TYPE = "rerequest";

	private CallbackManager mCallbackManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//setStatusBarColor(this, getResources().getColor(R.color.background_activity_title));
		Utils.setToolBarStyle(this);
		setContentView(R.layout.activity_login);
		emailString = getIntent().getStringExtra("email");
		clickToCallService = ((NMBSApplication) getApplication()).getClickToCallService();
		loginService = NMBSApplication.getInstance().getLoginService();
		bindAllViewElements();
		bindAllListeners();
		//FacebookSdk.sdkInitialize(getApplicationContext());
		FacebookSdk.isInitialized();
		//LoginManager.getInstance().logOut();
		try {
			PackageInfo info = getPackageManager().getPackageInfo(
					"com.cfl",
					PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (PackageManager.NameNotFoundException e) {

		} catch (NoSuchAlgorithmException e) {

		}


		mCallbackManager = CallbackManager.Factory.create();

		//LoginButton mLoginButton = findViewById(R.id.login_button);

		// Set the initial permissions to request from the user while logging in
		//mLoginButton.setReadPermissions(Arrays.asList(EMAIL, USER_POSTS));

		//mLoginButton.setAuthType(AUTH_TYPE);

		// Register a callback to respond to the user
		LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
			@Override
			public void onSuccess(LoginResult loginResult) {

				if(loginResult != null && loginResult.getAccessToken() != null){
					showWaitDialog();
					LogUtils.e("loginFacebook", "onSuccess------->" + loginResult.getAccessToken().getToken());
					LoginAsyncTask loginAsyncTask = new LoginAsyncTask(getApplicationContext(), loginHandler, emailString,
							etPwd.getText().toString(), LoginService.LOGIN_PROVIDER_FACEBOOK, loginResult.getAccessToken().getToken());
					loginAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
				setResult(RESULT_OK);
				//finish();
			}

			@Override
			public void onCancel() {
				setResult(RESULT_CANCELED);
				LogUtils.e("loginFacebook", "RESULT_CANCELED------->");
				DialogAlertError dialogAlertError = new DialogAlertError(LoginActivity.this,
						getApplicationContext().getResources().getString(R.string.general_information),
						getApplicationContext().getResources().getString(R.string.login_popup_cancelled));
				dialogAlertError.show();
				//finish();
			}

			@Override
			public void onError(FacebookException e) {
				// Handle exception
				LogUtils.e("loginFacebook", "RESULT_CANCELED------->");
			}
		});
	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(mCallbackManager != null){
			mCallbackManager.onActivityResult(requestCode, resultCode, data);
		}


		if (requestCode == 10000) {
			// The Task returned from this call is always completed, no need to attach
			// a listener.
			Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
			handleSignInResult(task);
		}
	}

	private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
		try {
			GoogleSignInAccount account = completedTask.getResult(ApiException.class);
			if(account != null){
				showWaitDialog();
				LogUtils.w("loginGoogle", "GoogleSignInAccount------->" + account.getId());
				LoginAsyncTask asyncTask = new LoginAsyncTask(getApplicationContext(), loginHandler, emailString,
						etPwd.getText().toString(), LoginService.LOGIN_PROVIDER_GOOGLE, account.getIdToken());
				asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}

			// Signed in successfully, show authenticated UI.
			//updateUI(account);
		} catch (ApiException e) {
			// The ApiException status code indicates the detailed failure reason.
			// Please refer to the GoogleSignInStatusCodes class reference for more information.
			LogUtils.w("loginGoogle", "signInResult:failed code=" + e.getStatusCode());
			//updateUI(null);
		}
	}
	private void bindAllViewElements() {
		etEmail = (EditText) findViewById(R.id.et_email);
		if(emailString == null || emailString.isEmpty()){
			if(NMBSApplication.getInstance().getLoginService().getLogonInfo() != null){
				emailString = NMBSApplication.getInstance().getLoginService().getLogonInfo().getEmail();
			}
		}
		etEmail.setText(emailString);
		etPwd = (EditText) findViewById(R.id.et_pwd);
		rlEmail = (RelativeLayout) findViewById(R.id.rl_email);
		rlPwd = (RelativeLayout) findViewById(R.id.rl_pwd);

		tvEmailError = (TextView) findViewById(R.id.tv_email_error);
		if(showPwd){
			//如果选中，显示密码
			etPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
			//showPwd = false;
			//etPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
		}else{
			//否则隐藏密码
			etPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
			//showPwd = true;
			//etPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
		}
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (LinearLayout) findViewById(R.id.ll_left_menus);
	}

	private void bindAllListeners() {
		/*etDnr.addTextChangedListener(new dnrChangedListener());
		etEmail.addTextChangedListener(new emailChangedListener());*/
		//etDnr.setOnFocusChangeListener(new UploadDossierActivity.DnrFocusChangeListener());
		etEmail.setOnFocusChangeListener(new LoginActivity.EmailFocusChangeListener());

		etPwd.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// et.getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片
				Drawable drawable = etPwd.getCompoundDrawables()[2];
				//如果右边没有图片，不再处理
				if (drawable == null)
					return false;
				//如果不是按下事件，不再处理
				if (event.getAction() != MotionEvent.ACTION_UP)
					return false;
				if (event.getX() > etPwd.getWidth()
						- etPwd.getPaddingRight()
						- drawable.getIntrinsicWidth()){
					if(showPwd){
						//如果选中，显示密码
						//etPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
						showPwd = false;
						etPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
					}else{
						//否则隐藏密码
						//etPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
						showPwd = true;
						etPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
					}
				}
				return false;
			}
		});
	}



	private class EmailFocusChangeListener implements View.OnFocusChangeListener{

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (!hasFocus){
				checkEmail();
			}
		}
	}

	private void checkPwd(){
		pwdString = etPwd.getText().toString();
		if(pwdString.isEmpty()){
			pwdValid = false;
			rlPwd.setBackground(getResources().getDrawable(R.drawable.group_error));
		}else{
			pwdValid = true;
			rlPwd.setBackground(getResources().getDrawable(R.drawable.group_default));
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

	public void forgotPwd(View view){
		checkEmail();

		if(emailValid){
			//settingService.setEmail(emailString);
			showWaitDialog();
			LogUtils.d("LoginActivity", "Onclick forgotPwd");
			ForgotPwdAsyncTask asyncTask = new ForgotPwdAsyncTask(getApplicationContext(), forgotPwdHandler, emailString);
			asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}


	public void login(View view){
		checkEmail();
		checkPwd();
		if(emailValid && pwdValid){
			//settingService.setEmail(emailString);
			loginService.setEmail(emailString);
            showWaitDialog();
			LogUtils.d("LoginActivity", "Onclick login");
			LoginAsyncTask loginAsyncTask = new LoginAsyncTask(getApplicationContext(), loginHandler, emailString, etPwd.getText().toString(), "CRIS", null);
			loginAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}

	public void loginFacebook(View view){
		AccessToken accessToken = AccessToken.getCurrentAccessToken();
		boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
		if(isLoggedIn){
			LogUtils.e("loginFacebook", "isLoggedIn------->");
			showWaitDialog();
			LoginAsyncTask loginAsyncTask = new LoginAsyncTask(getApplicationContext(), loginHandler, emailString,
					etPwd.getText().toString(), LoginService.LOGIN_PROVIDER_FACEBOOK, accessToken.getToken());
			loginAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		}else {
			LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
		}

	}

	public void loginGoogle(View view){

		GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
		if(account != null){
			LogUtils.d("loginGoogle", "account---Id---->" + account.getId());
			LogUtils.d("loginGoogle", "account---IdToken---->" + account.getIdToken());
			LogUtils.d("loginGoogle", "account---DisplayName---->" + account.getDisplayName());
			LogUtils.d("loginGoogle", "account---ExpirationTime---->" + new Date(account.getExpirationTimeSecs()));
			LogUtils.d("loginGoogle", "account---ObfuscatedIdentifier---->" + account.getObfuscatedIdentifier());
			LogUtils.d("loginGoogle", "account---Email---->" + account.getEmail());
			LogUtils.d("loginGoogle", "account---FamilyName---->" + account.getFamilyName());
			LogUtils.d("loginGoogle", "account---GivenName---->" + account.getGivenName());

			showWaitDialog();
			LoginAsyncTask loginAsyncTask = new LoginAsyncTask(getApplicationContext(), loginHandler, emailString,
					etPwd.getText().toString(), LoginService.LOGIN_PROVIDER_GOOGLE, account.getIdToken());
			loginAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}else{
			GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
					.requestIdToken(getResources().getString(R.string.server_client_id))
					.build();
			GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
			//mGoogleSignInClient.signOut();
			Intent signInIntent = mGoogleSignInClient.getSignInIntent();
			startActivityForResult(signInIntent, 10000);
		}

	}

/*	public void loginFacebook(View view){
		checkEmail();
		if(emailValid){
			//settingService.setEmail(emailString);
			showWaitDialog();
			LogUtils.d("LoginActivity", "Onclick login");
			LoginAsyncTask loginAsyncTask = new LoginAsyncTask(getApplicationContext(), loginHandler, emailString, etPwd.getText().toString(), "Facebook");
			loginAsyncTask.execute((Void) null);
		}
	}*/

	
	public static Intent createIntent(Context context, String email) {
		Intent intent = new Intent(context, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra("email", email);
		return intent;
	}


	public void register(View view){
		String url = "";
		GeneralSetting generalSetting = NMBSApplication.getInstance().getMasterService().loadGeneralSetting();
		if(generalSetting != null && generalSetting.getCreateProfileUrl() != null){
			url = Utils.getUrl( generalSetting.getCreateProfileUrl());
		}
		startActivity(WebViewCreateActivity.createIntent(LoginActivity.this, url));
	}

	private android.os.Handler loginHandler = new android.os.Handler() {
		public void handleMessage(Message msg) {
			hideWaitDialog();
			Bundle bundle = msg.getData();
			String errorMsg = bundle.getString(LoginAsyncTask.Intent_Key_Error);
			logonInfo = (LogonInfo) bundle.getSerializable(LoginAsyncTask.Intent_Key_Logoninfo);

			switch (msg.what) {
                case 0:
                	if(logonInfo != null){
                		if(LoginService.LOGIN_STATE_BLOCKED.equalsIgnoreCase(logonInfo.getState())){
							LogUtils.d("LoginActivity", "logonInfo------state---->" + LoginService.LOGIN_STATE_BLOCKED);
							showDialogAlertError(logonInfo.getStateDescription());
							etEmail.setText("");
							etPwd.setText("");
						}else if(LoginService.LOGIN_STATE_NOTFOUND.equalsIgnoreCase(logonInfo.getState())){
							LogUtils.d("LoginActivity", "logonInfo------state---->" + LoginService.LOGIN_STATE_NOTFOUND);
							showDialogAlertError(logonInfo.getStateDescription());
						}else if(LoginService.LOGIN_STATE_TECHNICALERROR.equalsIgnoreCase(logonInfo.getState())){
							LogUtils.d("LoginActivity", "logonInfo------state---->" + LoginService.LOGIN_STATE_TECHNICALERROR);
							showDialogAlertError(logonInfo.getStateDescription());
						}else if(LoginService.LOGIN_STATE_NOTACTIVATED.equalsIgnoreCase(logonInfo.getState())){
							LogUtils.d("LoginActivity", "logonInfo------state---->" + LoginService.LOGIN_STATE_NOTACTIVATED);
							DialogErrorLogin dialogAlertError = new DialogErrorLogin(LoginActivity.this, logonInfo.getStateDescription());
							dialogAlertError.setRefreshCallback(LoginActivity.this);
							dialogAlertError.show();
						}else if(LoginService.LOGIN_STATE_CUSTOMERBLOCKED.equalsIgnoreCase(logonInfo.getState())){
							LogUtils.d("LoginActivity", "logonInfo------state---->" + LoginService.LOGIN_STATE_CUSTOMERBLOCKED);
							showDialogAlertError(logonInfo.getStateDescription());
						}else if(LoginService.LOGIN_STATE_QUERYCUSTOMERFAILURE.equalsIgnoreCase(logonInfo.getState())){
							LogUtils.d("LoginActivity", "logonInfo------state---->" + LoginService.LOGIN_STATE_QUERYCUSTOMERFAILURE);
							showDialogAlertError(logonInfo.getStateDescription());
						}else if(LoginService.LOGIN_STATE_READCUSTOMERFAILURE.equalsIgnoreCase(logonInfo.getState())){
							LogUtils.d("LoginActivity", "logonInfo------state---->" + LoginService.LOGIN_STATE_READCUSTOMERFAILURE);
							showDialogAlertError(logonInfo.getStateDescription());
						}else if(LoginService.LOGIN_STATE_CREATECUSTOMERFAILURE.equalsIgnoreCase(logonInfo.getState())){
							LogUtils.d("LoginActivity", "logonInfo------state---->" + LoginService.LOGIN_STATE_CREATECUSTOMERFAILURE);
							showDialogAlertError(logonInfo.getStateDescription());
						}else if(LoginService.LOGIN_STATE_UPDATECUSTOMERFAILURE.equalsIgnoreCase(logonInfo.getState())){
							LogUtils.d("LoginActivity", "logonInfo------state---->" + LoginService.LOGIN_STATE_UPDATECUSTOMERFAILURE);
							showDialogAlertError(logonInfo.getStateDescription());
						}else if(LoginService.LOGIN_STATE_RESOLVELOGINFAILURE.equalsIgnoreCase(logonInfo.getState())){
							LogUtils.d("LoginActivity", "logonInfo------state---->" + LoginService.LOGIN_STATE_RESOLVELOGINFAILURE);
							showDialogAlertError(logonInfo.getStateDescription());
						}else if(LoginService.LOGIN_STATE_EMAILMISSING.equalsIgnoreCase(logonInfo.getState())){
							LogUtils.d("LoginActivity", "logonInfo------state---->" + LoginService.LOGIN_STATE_EMAILMISSING);
							showDialogAlertError(logonInfo.getStateDescription());
						}else if(LoginService.LOGIN_STATE_SUCCESS.equalsIgnoreCase(logonInfo.getState())){
							LogUtils.d("LoginActivity", "logonInfo------state---->" + LoginService.LOGIN_STATE_SUCCESS);
							loginService.getLogonInfo();
							if(NMBSApplication.getInstance().getLoginService().isLogon() && !AutoRetrievalDossiersTask.isWorking){
								NMBSApplication.getInstance().getDossierDetailsService().autoRetrievalDossiers();
							}
							if(NMBSApplication.getInstance().getLoginService().isLogon() && !CheckOptionAsyncTask.isChecking){
								CheckOptionAsyncTask asyncTask = new CheckOptionAsyncTask(getApplicationContext());
								asyncTask.execute((Void) null);
							}
                			finish();
						}
					}

                    break;
                case 1:
					LogUtils.d("LoginActivity", "errorMsg------->" + errorMsg);
					showDialogAlertError(errorMsg);
                    break;
			}
		}
	};

	private void showDialogAlertError(String errorMsg){
		DialogAlertError dialogAlertError = new DialogAlertError(LoginActivity.this,
				getResources().getString(R.string.error_view_title),
				errorMsg);
		dialogAlertError.show();
	}

	@Override
	public void callback() {
		//resend
		LogUtils.d("LoginActivity", "resend------->");
		showWaitDialog();
		ResendAsyncTask asyncTask = new ResendAsyncTask(getApplicationContext(), resendHandler, logonInfo, emailString);
		asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private android.os.Handler resendHandler = new android.os.Handler() {
		public void handleMessage(Message msg) {
			hideWaitDialog();
			switch (msg.what) {
				case 0:
					Bundle bundle = msg.getData();
					String errorMsg = bundle.getString(LoginAsyncTask.Intent_Key_Error);
					DialogErrorLogin dialogAlertError = new DialogErrorLogin(LoginActivity.this, errorMsg);
					dialogAlertError.setRefreshCallback(LoginActivity.this);
					dialogAlertError.show();
					break;
				case 1:
					//finish();
					DialogInfo dialogInfo = new DialogInfo(LoginActivity.this, getResources().getString(R.string.login_popup_resendemail_success));
					dialogInfo.show();
					break;
			}
		}
	};

	private android.os.Handler forgotPwdHandler = new android.os.Handler() {
		public void handleMessage(Message msg) {
			hideWaitDialog();
			switch (msg.what) {
				case 0:
					Bundle bundle = msg.getData();
					String errorMsg = bundle.getString(LoginAsyncTask.Intent_Key_Error);
					boolean resetLogin = bundle.getBoolean(ForgotPwdAsyncTask.Intent_Key_resetLogin);
					LogUtils.e("forgotPwd", "forgotPwd---resetLogin---->" + resetLogin);
					if(resetLogin){
						etPwd.setText("");
						etEmail.setText("");
					}
					showDialogAlertError(errorMsg);
					break;
				case 1:
					//finish();
					DialogInfo dialogInfo = new DialogInfo(LoginActivity.this, getResources().getString(R.string.login_popup_forgotpwd_success));
					dialogInfo.show();
					break;
			}
		}
	};
	// show progressDialog.
	private void showWaitDialog() {
		runOnUiThread(new Runnable() {
			public void run() {
				if(progressDialog == null){
					//Log.e(TAG, "Show Wait Dialog....");
					progressDialog = ProgressDialog.show(LoginActivity.this,
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
								startActivity(com.cfl.activities.MessageActivity.createIntent(getApplicationContext(),
										NMBSApplication.getInstance().getMessageService().getMessageResponse()));
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
								startActivity(com.cfl.activities.StationBoardActivity.createIntent(getApplicationContext(), null));
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
								startActivity(com.cfl.activities.StationInfoActivity.createIntent(getApplicationContext()));
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
								startActivity(com.cfl.activities.AlertActivity.createIntent(getApplicationContext()));
								isGoto = false;
								finish();
							}
						}
					});
		}
	}

	public void bookTicktes(View view){
		GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.HOME,TrackerConstant.HOME_BOOK_YOUR_TICKET,TrackerConstant.HOME_BOOK_YOUR_TICKET_LABEL);
		final GeneralSetting generalSetting = NMBSApplication.getInstance().getMasterService().loadGeneralSetting();

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
									//Utils.openProwser(SettingsActivity.this, generalSetting.getBookingUrl(), clickToCallService);
									if(NetworkUtils.isOnline(getApplicationContext())) {
										GoogleAnalyticsUtil.getInstance().sendScreen(LoginActivity.this, TrackerConstant.BOOKING);
										startActivity(WebViewActivity.createIntent(getApplicationContext(),
												Utils.getUrl(generalSetting.getBookingUrl()), WebViewActivity.BOOKING_FLOW, ""));
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
		final GeneralSetting generalSetting = NMBSApplication.getInstance().getMasterService().loadGeneralSetting();

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
									//Utils.openProwser(SettingsActivity.this, generalSetting.getLffUrl(), clickToCallService);
									if(NetworkUtils.isOnline(getApplicationContext())) {
										GoogleAnalyticsUtil.getInstance().sendScreen(LoginActivity.this, TrackerConstant.LLF);
										startActivity(WebViewActivity.createIntent(LoginActivity.this,
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
								startActivity(ScheduleSearchActivity.createIntent(getApplicationContext()));
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
								startActivity(SettingsActivity.createIntent(getApplicationContext()));
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
								startActivity(WizardActivity.createIntent(getApplicationContext(), WizardActivity.Wizard_Home));
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
								startActivity(MyTicketsActivity.createIntent(getApplicationContext()));
								isGoto = false;
								finish();
							}
						}
					});
		}
	}

	public void uploadTickets(View view){
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
								startActivity(UploadDossierActivity.createUploadDossierIntent(getApplicationContext(), NMBSApplication.PAGE_UploadTicket, null, null, null, null));
								isGoto = false;
								finish();
							}
						}
					});
		}
	}
	public void myOption(View view){
		//startActivity(LoginActivity.createIntent(MainActivity.this));
		final GeneralSetting generalSetting = NMBSApplication.getInstance().getMasterService().loadGeneralSetting();
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
									startActivity(WebViewActivity.createIntent(getApplicationContext(),
											Utils.getUrl(generalSetting.getCommercialTtlListUrl()), WebViewActivity.NORMAL_FLOW, ""));
								}else{
									DialogMyOptions dialogMyOptions = new DialogMyOptions(LoginActivity.this);
									dialogMyOptions.show();
								}
								isGoto = false;
							}
						}
					});
		}else{
			if(NMBSApplication.getInstance().getLoginService().isLogon()){
				startActivity(WebViewActivity.createIntent(getApplicationContext(),
						Utils.getUrl(generalSetting.getCommercialTtlListUrl()), WebViewActivity.NORMAL_FLOW, ""));
			}else{
				DialogMyOptions dialogMyOptions = new DialogMyOptions(this);
				dialogMyOptions.show();
			}
		}


	}

	public void loginOrManage(View view){
		//startActivity(LoginActivity.createIntent(MainActivity.this));
		final GeneralSetting generalSetting = NMBSApplication.getInstance().getMasterService().loadGeneralSetting();
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

								}else{
									//startActivity(LoginActivity.createIntent(getApplicationContext(), ""));
									mDrawerLayout.closeDrawer(GravityCompat.END);
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
			}else{
				//startActivity(LoginActivity.createIntent(getApplicationContext(), ""));
				mDrawerLayout.closeDrawer(GravityCompat.END);
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

		rlMenuMyOption = (RelativeLayout) findViewById(R.id.rl_menu_traintickets_content_myoptions);
		tvMenuOptionCount = (TextView) findViewById(R.id.tv_menu_option_count);
		tvMenuOptionCount.setText("(" + MainActivity.optionCount + ")");
		TextView tvMenuLogon = (TextView) findViewById(R.id.tv_menu_logon);
		if(!NMBSApplication.getInstance().getLoginService().isLogon()){
			rlMenuMyOption.setAlpha(0.3f);
			tvMenuOptionCount.setText("");
			tvMenuLogon.setText(getResources().getString(R.string.menu_content_loginorcreateprofile));
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
