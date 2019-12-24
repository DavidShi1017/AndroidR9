package com.cflint.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.cflint.exceptions.CrashHandler;
import com.cflint.util.MenuUtil;
import com.crashlytics.android.Crashlytics;
import com.facebook.appevents.AppEventsLogger;

import com.cflint.R;
import com.cflint.activities.view.DialogCheckUpdateNotification;
import com.cflint.activities.view.DialogLogout;
import com.cflint.activities.view.DialogMyOptions;
import com.cflint.activity.BaseActivity;
import com.cflint.adapter.DossierHomeAdapter;
import com.cflint.adapter.PagerViewAdapter;
import com.cflint.application.NMBSApplication;
import com.cflint.async.AsyncImageLoader;
import com.cflint.async.AutoRetrievalDossiersTask;
import com.cflint.async.CheckOptionAsyncTask;
import com.cflint.async.GetSubScriptionListAsyncTask;
import com.cflint.async.MobileMessageAsyncTask;
import com.cflint.async.ProfileInfoAsyncTask;
import com.cflint.async.RealTimeInfoAsyncTask;
import com.cflint.dataaccess.database.DossierDatabaseService;
import com.cflint.exceptions.InvalidJsonError;
import com.cflint.listeners.RatingListener;
import com.cflint.log.LogUtils;
import com.cflint.model.CheckAppUpdate;
import com.cflint.model.CheckOption;
import com.cflint.model.DossierDetailsResponse;
import com.cflint.model.DossierSummary;
import com.cflint.model.DossierTravelSegment;
import com.cflint.model.GeneralSetting;
import com.cflint.model.HomeBannerResponse;
import com.cflint.model.LogonInfo;
import com.cflint.model.MobileMessage;
import com.cflint.model.MobileMessageResponse;
import com.cflint.model.Order;
import com.cflint.model.RealTimeInfoRequestParameter;
import com.cflint.model.Subscription;
import com.cflint.preferences.SettingsPref;
import com.cflint.services.IAssistantService;
import com.cflint.services.ICheckUpdateService;
import com.cflint.services.IClickToCallService;
import com.cflint.services.IMasterService;
import com.cflint.services.IMessageService;
import com.cflint.services.IPushService;
import com.cflint.services.impl.DossierDetailsService;
import com.cflint.services.impl.DossiersUpToDateService;
import com.cflint.services.impl.LoginService;
import com.cflint.services.impl.RatingService;
import com.cflint.services.impl.ServiceConstant;
import com.cflint.services.impl.SettingService;
import com.cflint.util.ActivityConstant;
import com.cflint.util.AppLanguageUtils;
import com.cflint.util.AppUtil;
import com.cflint.util.DateUtils;
import com.cflint.util.DeviceUtil;
import com.cflint.util.FileManager;
import com.cflint.util.FunctionConfig;
import com.cflint.util.GoogleAnalyticsUtil;
import com.cflint.util.ImageUtil;
import com.cflint.util.NetworkUtils;
import com.cflint.util.RatingUtil;
import com.cflint.util.TrackerConstant;
import com.cflint.util.Utils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.fabric.sdk.android.Fabric;

@SuppressLint("NewApi")
public class MainActivity extends BaseActivity implements RatingListener {

    List<MobileMessage> showMessages = null;
    private SettingService settingService = null;
    private LinearLayout llHomeContent, llRating, llHomeDossier, llMigrateDossier, llMigratingDossier;
    private IMessageService messageService;
	private IMasterService masterService;
	private IPushService pushService;
    private static final String TAG = MainActivity.class.getSimpleName();
    private ProgressBar pbMessages,pbAlert, pbTickets;
    private ServiceStateReceiver serviceStateReceiver;
	private AlertServiceStateReceiver alertServiceStateReceiver;
    private TextView tvMessageCount, tvRatingInfo, tvTicketCount, tvAlertCount;
	private RatingService ratingService;
	private RatingService.RatingView whichView = RatingService.RatingView.QuestionDoYouLikeApp;
	private Button btnNo, btnYes;
	private HomeBannerResponse homeBannerResponse;
	private LayoutInflater inflater;
	private GestureDetector detector;
	private boolean isTraceFacebook;
	private List<DossierSummary> dossiers;
	private List<View> viewList;
	private DialogCheckUpdateNotification dialogCheckUpdateNotification;
	private ViewFlipper flipper;
	private PagerViewAdapter pagerViewAdapter;
	private ViewPager viewPager;
	private List<DossierSummary> dossiersActive;
	private DossierDetailsService dossierDetailsService;
	public static int ticketCount, realtimeCount, messateCount, optionCount;
	private IClickToCallService clickToCallService;
	private ICheckUpdateService checkUpdateService;
	private IAssistantService assistantService;
	private List<Order> listOrders;
	private List<DossierTravelSegment> dossierTravelSegments;
	private static final String Intent_Key_RealTimeInfoRequestParameter = "RealTimeInfoRequestParameter";
	private RealTimeInfoRequestParameter realTimeInfoRequestParameter;
	private RelativeLayout rlDossier, rlMyOption, rlMenuMyOption;
	private Button btnRefresh;
	private ServiceRealTimeReceiver serviceRealTimeReceiver;
	//private MigrateDossierReceiver migrateDossierReceiver;
	private TextView tvNationalApp;
	private Timer timer;
	private TimerTask timerTask;
	private View viewPlace;
	private DrawerLayout mDrawerLayout;
	private LinearLayout mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private TextView tvMenuTicketCount, tvMenuRealtimeCount, tvMenuMessageCount, tvMenuOptionCount, tvTitle, tvMenuLogin, tvMenuLogon;
	private LoginService loginService;
	private ImageView ivLogin, ivLogout;
	private LinearLayout llLogin, llMask;
	private RefreshMultipleDossierReceiver refreshMultipleDossierReceiver;
	private CheckOptionReceiver checkOptionReceiver;
	private TextView tvOptionCount;
	private ProgressBar pbOption;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//setStatusBarColor(this, getResources().getColor(R.color.background_activity_title));
		Utils.setToolBarStyle(this);

        settingService = ((NMBSApplication) getApplication()).getSettingService();
        messageService = ((NMBSApplication) getApplication()).getMessageService();
		masterService = ((NMBSApplication) getApplication()).getMasterService();
		pushService = ((NMBSApplication) getApplication()).getPushService();
		loginService = NMBSApplication.getInstance().getLoginService();
        //settingService.initLanguageSettings();
		ratingService = ((NMBSApplication) getApplication()).getRatingService();
		ratingService.setRatingListener(this);
		dossierDetailsService = ((NMBSApplication) getApplication()).getDossierDetailsService();
		clickToCallService = ((NMBSApplication) getApplication()).getClickToCallService();
		checkUpdateService = ((NMBSApplication) getApplication()).getCheckUpdateService();
		assistantService = ((NMBSApplication) getApplication()).getAssistantService();
		///btnRefresh.setVisibility(View.VISIBLE);
		setContentView(R.layout.activity_main);
        bindAllViewElements();
        if(FunctionConfig.kFunMobileBanner){
			showHomeBanner();
		}
		if(FunctionConfig.kFunLinkToNationalApp){
			bindNationalApp();
		}
		final Fabric fabric = new Fabric.Builder(this)
				.kits(new Crashlytics())
				.debuggable(true)  // Enables Crashlytics debugger
				.build();
		Fabric.with(fabric);
		/*Button crashButton = new Button(this);
		crashButton.setText("Crash!");
		crashButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Crashlytics.getInstance().crash(); // Force a crash
			}
		});

		addContentView(crashButton, new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));*/
		initValue();
		//System.out.println(s.equals("any string"));
		RelativeLayout rlTest = (RelativeLayout) findViewById(R.id.rl_test) ;
		if(FunctionConfig.kFunMyProfile){
			rlTest.setVisibility(View.GONE);
		}else{
			rlTest.setVisibility(View.INVISIBLE);
		}
		//Log.e(TAG, "savedInstanceState...sid....." + getIntent().getStringExtra(ActivityConstant.RECEIVE_PUSH_SUBSCRIPTION_ID));
		if(getIntent().getStringExtra(ActivityConstant.RECEIVE_PUSH_SUBSCRIPTION_ID)!=null){
			startActivity(PushNotificationErrorActivity.createIntent(this,
					getIntent().getStringExtra(ActivityConstant.RECEIVE_PUSH_SUBSCRIPTION_ID), NMBSApplication.PAGE_PUSH));
		}

		if(getIntent().getBooleanExtra(ActivityConstant.IS_RECEIVE_LOCAL_NOTIFICATION,false)){
			startActivity(MyTicketsActivity.createIntent(this));
		}
		if(settingService.getStartNotifiTime().isEmpty()){
			String[] time = getResources().getStringArray(R.array.settings_start_time);
			String changedStartTime = time[0];
			settingService.setStartNotifiTime(changedStartTime);
		}
		if(settingService.getDelayNotifiTime().isEmpty()){
			String[] time = getResources().getStringArray(R.array.settings_time);
			String changedDelayTime = time[0];
			settingService.setDelayNotifiTime(changedDelayTime);

		}
		refreshRealTime();
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						showAlertCount();
						showMessageCount();
						//Log.e(TAG, "TimerTask.....");
					}
				});
			}
		};
		timer.schedule(timerTask, 30000);
		if(loginService.isLogon()){
			ProfileInfoAsyncTask profileInfoAsyncTask = new ProfileInfoAsyncTask(getApplicationContext(), handler);
			profileInfoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
    }

	private void getIntentValues(){

	}
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, newBase.getString(R.string.app_language_pref_key)));
	}
/*	private void MigrateDossier(){
		//Log.e(TAG, "MigrateDossierAsyncTask.isMigrateing===" + MigrateDossierAsyncTask.isMigrateing);
		if (MigrateDossierAsyncTask.isMigrateing){
			llMigratingDossier.setVisibility(View.VISIBLE);
		}else{
			llMigratingDossier.setVisibility(View.GONE);
			GeneralSetting generalSetting = masterService.loadGeneralSetting();

			if(generalSetting != null){
				String dossierAftersalesLifetime = String.valueOf(generalSetting.getDossierAftersalesLifetime());
				listOrders = assistantService.searchOrders(0, dossierAftersalesLifetime);
				if(listOrders != null && listOrders.size() > 0){
					llMigrateDossier.setVisibility(View.VISIBLE);
					//Log.e(TAG, "The DNRs are not yet migrated to the new structure");
					//executeMigrating();
				}else{
					llMigrateDossier.setVisibility(View.GONE);
					//Log.e(TAG, "No DNRs should to migrated");
				}
			}
		}

	}*/

	/*private void executeMigrating(){
		MigrateDossierAsyncTask asyncTask = new MigrateDossierAsyncTask(migrateHandler, settingService.getCurrentLanguagesKey(),
				getApplicationContext(), listOrders);
		asyncTask.execute((Void) null);
		llMigratingDossier.setVisibility(View.VISIBLE);
		llMigrateDossier.setVisibility(View.GONE);
	}*/

	/*private Handler migrateHandler = new Handler() {
		public void handleMessage(Message msg) {
			llMigratingDossier.setVisibility(View.GONE);
			llMigrateDossier.setVisibility(View.GONE);
			switch (msg.what) {
				case MigrateDossierAsyncTask.MigrateSuccessful:
					//Log.d(TAG, "MigrateSuccessful...");
					break;
				case MigrateDossierAsyncTask.MigrateUnSuccessful:
					//Log.d(TAG, "MigrateUnSuccessful...");
					break;
			}
		}
	};*/

	private void initValue(){
		this.isTraceFacebook = settingService.isFacebookTrack();
		//Log.i(TAG, "Setting screen name: " + "Home");
		GoogleAnalyticsUtil.getInstance().sendScreen(MainActivity.this, TrackerConstant.HOME);
	}



	public void refreshRealTime(View view){
		refreshRealTime();
	}

	private void refreshRealTime(){
		this.btnRefresh.setClickable(false);
		this.btnRefresh.setBackground(getResources().getDrawable(R.drawable.group_default_refresh_disable));
		this.btnRefresh.setTextColor(getResources().getColor(R.color.color_disable));
		RealTimeInfoAsyncTask.isRefreshing = true;
		RealTimeInfoAsyncTask asyncTask = new RealTimeInfoAsyncTask(getApplicationContext(), settingService, dossierDetailsService,
				realTimeHandler, realTimeInfoRequestParameter);
		asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		//showDossier();
	}

	private void realTimeFinished(){
		RealTimeInfoAsyncTask.isRefreshing = false;
		btnRefresh.setClickable(true);
		btnRefresh.setBackground(getResources().getDrawable(R.drawable.group_default_refresh));
		btnRefresh.setTextColor(getResources().getColor(R.color.background_secondaryaction));
		showDossier();
	}

	private Handler realTimeHandler = new Handler() {
		public void handleMessage(Message msg) {
			realTimeFinished();
			switch (msg.what) {
				case RealTimeInfoAsyncTask.REALTIMEMESSAGE:
					LogUtils.d(TAG, "realTimeHandler...Finished RealTime..");
					break;
				case ServiceConstant.MESSAGE_WHAT_ERROR:
					break;
			}
		}
	};

	private void showDossier(){

		llHomeDossier.removeAllViews();
		dossiersActive = dossierDetailsService.getDossiers(true);
		if(dossiersActive != null && dossiersActive.size() > 0){
			llHomeDossier.setVisibility(View.VISIBLE);
			rlDossier.setVisibility(View.VISIBLE);
			//tvNoDnr.setVisibility(View.GONE);
			DossierHomeAdapter dossierHomeAdapter = new DossierHomeAdapter(this);
			dossierTravelSegments = dossierDetailsService.getDossierTravelSegment();
			//Log.d(TAG, "dossierTravelSegments ..." + dossierTravelSegments.size());
			realTimeInfoRequestParameter = dossierDetailsService.getRealTimeInfoRequestParameter();
			for(DossierTravelSegment dossierTravelSegment : dossierDetailsService.getActiveTravelSegment(true, dossierTravelSegments)){
				if(dossierTravelSegment != null) {
					//Log.d(TAG, "TravelSegment ..." + dossierTravelSegment.getTravelSegmentId());
					DossierSummary dossierSummary = dossierDetailsService.getDossier(dossierTravelSegment.getTravelSegmentId().substring(0, 7));
					DossierDetailsResponse dossierResponse = dossierDetailsService.getDossierDetail(dossierSummary);
					boolean shouldRefresh = dossierDetailsService.shouldRefresh(realTimeInfoRequestParameter, dossierTravelSegment, null);
					if(!shouldRefresh){
						List<DossierTravelSegment> childDossierTravelSegments = dossierResponse.getDossier().getChildTravelSegments(dossierTravelSegment);
						for(DossierTravelSegment childDossierTravelSegment : childDossierTravelSegments){
							shouldRefresh = dossierDetailsService.shouldRefresh(realTimeInfoRequestParameter, childDossierTravelSegment, null);
							if (shouldRefresh){
								break;
							}
						}
					}
					if(dossierResponse != null){
						dossierHomeAdapter.getHomeTravelSegmentView(dossierTravelSegment, llHomeDossier, dossierResponse.getDossier(), dossierSummary, shouldRefresh);
					}
					//Log.e("shouldRefresh", "shouldRefresh..." + shouldRefresh);
					if (shouldRefresh){
						btnRefresh.setVisibility(View.VISIBLE);
					}
				}
			}

		}else {
			llHomeDossier.setVisibility(View.GONE);
			rlDossier.setVisibility(View.GONE);
			//tvNoDnr.setVisibility(View.VISIBLE);
		}
	}

	private void showHomeBanner(){
		try {
			homeBannerResponse = masterService.loadHomeBannerResponse();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (InvalidJsonError invalidJsonError) {
			invalidJsonError.printStackTrace();
		}
		if (homeBannerResponse != null && homeBannerResponse.getHomeBanners() != null && homeBannerResponse.getHomeBanners().size() > 0){
			viewList = new ArrayList<View>();
			this.viewPager.setVisibility(View.VISIBLE);
			GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.HOME_CATEGORY,TrackerConstant.HOME_IMPRESSION_BANNER_ACTION,"");
			LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			//Log.e("HOMEBANNER", "size..." + homeBannerResponse.getHomeBanners().size());
			for (int i = 0; i < homeBannerResponse.getHomeBanners().size(); i++) {
				final RelativeLayout view = (RelativeLayout) layoutInflater.inflate(R.layout.vi_homebanner, null);
				final View viewPlace = view.findViewById(R.id.view_place);
				TextView tvBannerTitle = (TextView)view.findViewById(R.id.tv_banner_title);
				TextView tvBannerInfo = (TextView)view.findViewById(R.id.tv_banner_info);
				tvBannerTitle.setText(homeBannerResponse.getHomeBanners().get(i).getTitle().toUpperCase());
				tvBannerInfo.setText(homeBannerResponse.getHomeBanners().get(i).getSubTitle());
				TextView button = (TextView) view.findViewById(R.id.btn_book);
				String btnText = homeBannerResponse.getHomeBanners().get(i).getActionButtonText();
				if(btnText != null){
					btnText = btnText.toUpperCase();
				}
				button.setText(btnText);
				ImageView imageView = (ImageView)view.findViewById(R.id.iv_homebanner);
				TextView tvPageNr = (TextView)view.findViewById(R.id.tv_page_nr);
				int page = i + 1;
				if(homeBannerResponse.getHomeBanners().size() == 1){
					tvPageNr.setVisibility(View.GONE);
				}
				tvPageNr.setText(page + "/" + homeBannerResponse.getHomeBanners().size());
				if (homeBannerResponse.getHomeBanners().get(i).isIncludeActionButton()){
					button.setVisibility(View.VISIBLE);
				}else{
					button.setVisibility(View.GONE);
				}
				String imageUrl = ImageUtil.convertImageExtension(homeBannerResponse.getHomeBanners().get(i).getDetailImage());
				//Log.e("HOMEBANNER", "imageUrl..." + imageUrl);
				String fullImageUrl = getString(R.string.server_url_host) + imageUrl;
				//Log.e("HOMEBANNER", "fullImageUrl..." + fullImageUrl);
				AsyncImageLoader.getInstance().loadDrawable(
						this.getApplicationContext(), fullImageUrl, homeBannerResponse.getHomeBanners().get(i).getId(),
						imageView, FileManager.FOLDER_HOMEBANNER, new AsyncImageLoader.ImageCallback() {
							public void imageLoaded(Bitmap imageDrawable, String imageUrl, View view) {
								/*if(imageDrawable != null){
									Log.e("HOMEBANNER", "width..." + imageDrawable.getWidth());
									Log.e("HOMEBANNER", "height..." + imageDrawable.getHeight());
									RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) viewPlace.getLayoutParams();
									linearParams.height = imageDrawable.getHeight();
									viewPlace.setLayoutParams(linearParams);
								}else{
									RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) viewPlace.getLayoutParams();
									linearParams.height = 120;
									viewPlace.setLayoutParams(linearParams);
								}*/
								//Log.e("HOMEBANNER", "fullImageUrl..." + imageUrl);
								((ImageView) view).setImageBitmap(imageDrawable);
							}
						});
				final String url = homeBannerResponse.getHomeBanners().get(i).getHyperlink();
				final boolean isWebView = homeBannerResponse.getHomeBanners().get(i).isNavigationInNormalWebView();
				button.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if(url != null && !url.isEmpty()){
							Utils.openWebView(MainActivity.this, url, WebViewActivity.NORMAL_FLOW, "", isWebView);
							/*if(NetworkUtils.isOnline(MainActivity.this)) {
								if (url.contains("Navigation=WebView")) {
									startActivity(WebViewActivity.createIntent(getApplicationContext(), Utils.getUrl(getApplicationContext(), url, clickToCallService)));
								} else {
									startActivity(WebViewOverlayActivity.createIntent(getApplicationContext(), Utils.getUrl(getApplicationContext(), url, clickToCallService)));
								}
							}*/
							//Utils.openProwser(MainActivity.this, url, clickToCallService);
							GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.HOME_CATEGORY, TrackerConstant.HOME_SELECT_BANNER_ACTION,"");
						}
					}
				});
				viewList.add(view);
			}
		}else{
			this.viewPager.setVisibility(View.GONE);
		}
		//Log.d(TAG, "viewList===" + viewList.size());
		if(viewList != null){
			//Log.d(TAG, "viewList===" + viewList.size());
			this.pagerViewAdapter = new PagerViewAdapter(viewList);
			viewPager.setAdapter(pagerViewAdapter);
		}
	}

	private void initView(){
		this.viewPager = (ViewPager)findViewById(R.id.vf_ticket_detail_view_pager);

		llHomeContent = (LinearLayout) findViewById(R.id.ll_home_content);
		pbMessages = (ProgressBar) findViewById(R.id.pb_messages);
		pbAlert = (ProgressBar) findViewById(R.id.pb_alert);
		pbTickets = (ProgressBar) findViewById(R.id.pb_tickets);

		tvMessageCount = (TextView) findViewById(R.id.tv_message_count);
		llRating = (LinearLayout) findViewById(R.id.ll_rating);
		tvRatingInfo = (TextView) findViewById(R.id.tv_rating_info);
		tvAlertCount = (TextView) findViewById(R.id.tv_alert_subscription_count);
		btnNo = (Button) findViewById(R.id.btn_no);
		btnYes = (Button) findViewById(R.id.btn_yes);
		rlDossier = (RelativeLayout) findViewById(R.id.rl_dossier);
		tvTicketCount = (TextView) findViewById(R.id.tv_ticket_count);
		llHomeDossier = (LinearLayout) findViewById(R.id.ll_home_dossier);
		llMigrateDossier = (LinearLayout) findViewById(R.id.ll_migrate_dossier);
		llMigratingDossier = (LinearLayout) findViewById(R.id.ll_migrating_dossier);
		btnRefresh = (Button) findViewById(R.id.btn_refresh);
		tvNationalApp = (TextView) findViewById(R.id.tv_national_app);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (LinearLayout) findViewById(R.id.ll_left_menus);

		ivLogin = (ImageView) findViewById(R.id.iv_login);

		ivLogout = (ImageView) findViewById(R.id.iv_logout);
		llLogin = (LinearLayout) findViewById(R.id.ll_login);

		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvMenuLogin = (TextView) findViewById(R.id.tv_menu_login);
		rlMyOption = (RelativeLayout) findViewById(R.id.rl_my_option);

		llMask = (LinearLayout) findViewById(R.id.ll_mask);
		tvMenuLogon = (TextView) findViewById(R.id.tv_menu_logon);
		rlMenuMyOption = (RelativeLayout) findViewById(R.id.rl_menu_traintickets_content_myoptions);
		rlMainMenuLogin = (RelativeLayout) findViewById(R.id.rl_main_menu_login);

		rlAlerts = (RelativeLayout) findViewById(R.id.rl_alerts);
		if(!FunctionConfig.kFunManagePush){
			rlAlerts.setVisibility(View.GONE);
		}
		tvOptionCount = (TextView) findViewById(R.id.tv_option_count);
		pbOption = (ProgressBar) findViewById(R.id.pb_option);

		rlBook = (RelativeLayout) findViewById(R.id.rl_book);

		rlLowestFares = (RelativeLayout) findViewById(R.id.rl_lowestFares);
		rlMyTickets = (RelativeLayout) findViewById(R.id.rl_mytickets);
		rlUploadTickets = (RelativeLayout) findViewById(R.id.rl_uploadtickets);
		rlMainMenuMessage = (RelativeLayout) findViewById(R.id.rl_main_menu_message);
		rlMainMenuAbout = (RelativeLayout) findViewById(R.id.rl_main_menu_about);
		rlMainMenuStationboard = (RelativeLayout) findViewById(R.id.rl_main_menu_stationboard);
		rlMainMenuSchedules = (RelativeLayout) findViewById(R.id.rl_main_menu_schedules);
		rlMainMenuStations = (RelativeLayout) findViewById(R.id.rl_main_menu_stations);
	}

	private void bindAllViewElements() {
		initView();
        Drawable drawable = new BitmapDrawable(getResources(), ImageUtil.compressImage(getApplicationContext(), R.drawable.ic_home_background));
        llHomeContent.setBackground(drawable);


        if (!MobileMessageAsyncTask.isMessageFinished) {
            pbMessages.setVisibility(View.VISIBLE);
            tvMessageCount.setVisibility(View.GONE);
            if (serviceStateReceiver == null) {
				serviceStateReceiver = new ServiceStateReceiver();
                registerReceiver(serviceStateReceiver, new IntentFilter(ServiceConstant.MESSAGE_SERVICE_ACTION));
            }
        }else{
				showMessageCount();
		}
		if(alertServiceStateReceiver == null){
			alertServiceStateReceiver = new AlertServiceStateReceiver();
			registerReceiver(alertServiceStateReceiver, new IntentFilter(ServiceConstant.PUSH_GET_SUBSCRIPTION_LIST_ACTION));
		}
		if(GetSubScriptionListAsyncTask.isAlertSubscriptionFinished){
			showAlertCount();
		}



		if(!DeviceUtil.isTabletDevice(this.getApplicationContext())){
			//setRlView();
		}


		if(FunctionConfig.kFunMyProfile){
			ivLogin.setVisibility(View.VISIBLE);
		}else {
			ivLogin.setVisibility(View.GONE);
		}


		if(FunctionConfig.kFunMyProfile){
			llLogin.setVisibility(View.VISIBLE);
		}else {
			llLogin.setVisibility(View.GONE);
		}


		if(FunctionConfig.kFunMyProfile){
			rlMyOption.setVisibility(View.VISIBLE);
		}else {
			rlMyOption.setVisibility(View.INVISIBLE);
		}

		if(FunctionConfig.kFunMyProfile){
			rlMainMenuLogin.setVisibility(View.VISIBLE);
		}else {
			rlMainMenuLogin.setVisibility(View.GONE);
		}


		//if(!CheckOptionAsyncTask.isChecking){
			showOptionCount();
		//}


		if(FunctionConfig.kFunManagePush){
			rlAlerts.setVisibility(View.VISIBLE);
		}else {
			rlAlerts.setVisibility(View.GONE);
		}


		if(FunctionConfig.kFunBookingEntry){
			rlBook.setVisibility(View.VISIBLE);
			if(NMBSApplication.getInstance().getMasterService().loadGeneralSetting() != null
					&& NMBSApplication.getInstance().getMasterService().loadGeneralSetting().getBookingUrl() != null
					&& !NMBSApplication.getInstance().getMasterService().loadGeneralSetting().getBookingUrl().isEmpty()){
				rlBook.setVisibility(View.VISIBLE);
			}else {
				rlBook.setVisibility(View.GONE);
				rlMyOption.setVisibility(View.INVISIBLE);
			}
		}else {
			rlBook.setVisibility(View.GONE);
		}

		if(FunctionConfig.kFunLFFEntry){
			rlLowestFares.setVisibility(View.VISIBLE);
		}else {
			rlLowestFares.setVisibility(View.GONE);
		}

		if(FunctionConfig.kFunMyTickets){
			rlMyTickets.setVisibility(View.VISIBLE);
		}else {
			rlMyTickets.setVisibility(View.GONE);
		}


		if(FunctionConfig.kFunUploadDNR){
			rlUploadTickets.setVisibility(View.VISIBLE);
		}else {
			rlUploadTickets.setVisibility(View.GONE);
		}



		if(FunctionConfig.kFunMobileMessage){
			rlMainMenuMessage.setVisibility(View.VISIBLE);
		}else {
			rlMainMenuMessage.setVisibility(View.GONE);
		}

		if(FunctionConfig.kFunAboutThisApp){
			rlMainMenuAbout.setVisibility(View.VISIBLE);
		}else {
			rlMainMenuAbout.setVisibility(View.GONE);
		}

		if(FunctionConfig.kFunStationboard){
			rlMainMenuStationboard.setVisibility(View.VISIBLE);
		}else {
			rlMainMenuStationboard.setVisibility(View.GONE);
		}


		if(FunctionConfig.kFunSchedule){
			rlMainMenuSchedules.setVisibility(View.VISIBLE);
		}else {
			rlMainMenuSchedules.setVisibility(View.GONE);
		}


		if(FunctionConfig.kFunStationInfo){
			rlMainMenuStations.setVisibility(View.VISIBLE);
		}else {
			rlMainMenuStations.setVisibility(View.GONE);
		}
	}
	private RelativeLayout rlAlerts;
	private RelativeLayout rlBook;
	private RelativeLayout rlLowestFares;
	private RelativeLayout rlMyTickets;
	private RelativeLayout rlUploadTickets;
	private RelativeLayout rlMainMenuLogin;
	private RelativeLayout rlMainMenuMessage;
	private RelativeLayout rlMainMenuAbout;
	private RelativeLayout rlMainMenuStationboard;
	private RelativeLayout rlMainMenuSchedules;
	private RelativeLayout rlMainMenuStations;

	private void setRlView(){


		rlAlerts.measure(0, 0);
		//换成方法measureView (headView)也可以
		int headViewWidth = rlAlerts.getMeasuredWidth();

		Log.i(TAG, "headViewWidth-->" + headViewWidth + "  density-->" + getResources().getDisplayMetrics().densityDpi);
		if(getResources().getDisplayMetrics().densityDpi == 480 && getResources().getDisplayMetrics().densityDpi < 560){
			headViewWidth = 240;
		}else if(getResources().getDisplayMetrics().densityDpi == 320 && getResources().getDisplayMetrics().densityDpi < 480){
			headViewWidth = 170;
		}else if(getResources().getDisplayMetrics().densityDpi == 420 && getResources().getDisplayMetrics().densityDpi < 480){
			headViewWidth = 240;
		}else if(getResources().getDisplayMetrics().densityDpi == 560 && getResources().getDisplayMetrics().densityDpi < 640){
			headViewWidth = 320;
		}else if(getResources().getDisplayMetrics().densityDpi == 240 && getResources().getDisplayMetrics().densityDpi < 320){
			headViewWidth = 100;
		}else if(getResources().getDisplayMetrics().densityDpi >= 640){
			headViewWidth = 320;
		}
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(headViewWidth, (int)getResources().getDimension(R.dimen.home_menu_height));

		rlBook.setLayoutParams(new LinearLayout.LayoutParams(headViewWidth, (int)getResources().getDimension(R.dimen.home_menu_height)));
		rlDossier.measure(0, 0);
		int rlBookWidth = rlBook.getMeasuredWidth();
		Log.i(TAG, "rlBookWidth-->" + rlBookWidth);
		params.setMargins(10, 0, 0, 0);
		rlLowestFares.setLayoutParams(params);
		rlMyTickets.setLayoutParams(params);
		rlUploadTickets.setLayoutParams(params);
		rlMyOption.setLayoutParams(params);
	}
	/*public void clickMenu(View view){
		mDrawerLayout.openDrawer(GravityCompat.END);
	}*/
	public void close(View view){
		mDrawerLayout.closeDrawer(GravityCompat.END);
	}

    private void showMessageCount() {

        if (messageService.getMessageResponse() != null && messageService.getMessageResponse().getMobileMessages() != null
                && messageService.getMessageResponse().getMobileMessages().size() > 0) {

            if (tvMessageCount != null) {
                tvMessageCount.setVisibility(View.VISIBLE);
                pbMessages.setVisibility(View.GONE);
				messateCount = messageService.getMessageResponse().getMobileMessages().size();
                tvMessageCount.setText(String.valueOf(messateCount));
            }
        }else {
			tvMessageCount.setVisibility(View.VISIBLE);
			tvMessageCount.setText(String.valueOf(0));
			pbMessages.setVisibility(View.GONE);
        }
		if(timerTask != null){
			timerTask.cancel();
			timerTask = null;
		}
		if(timer != null){
			timer.cancel();
			timer = null;
		}
    }

	@Override
	public void changeRatingView(RatingService.RatingView whichView) {
		this.whichView = whichView;
		initRatingView();
	}

	@Override
	public void openAppStore() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		//intent.setData(Uri.parse("market://details?id=" + context.getPackageName()));
		intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
		startActivity(intent);	}

	@Override
	public void sendEmail() {
		Utils.sendEmail(getApplicationContext());
	}

	class ServiceStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.d(TAG, "onReceive got MESSAGE_SERVICE_ACTION broadcast");
            if (ServiceConstant.MESSAGE_SERVICE_ACTION.equalsIgnoreCase(intent.getAction().toString())) {
                //progressBar.setVisibility(View.GONE);
                //messageCount.setVisibility(View.VISIBLE);
                showMessageCount();
            }

        }
    }

	class AlertServiceStateReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (ServiceConstant.PUSH_GET_SUBSCRIPTION_LIST_ACTION.equalsIgnoreCase(intent.getAction().toString())) {
				showAlertCount();
			}

		}
	}

	class CheckOptionReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			LogUtils.e("CheckOption", "CheckOptionReceiver-------isChecking------>" + CheckOptionAsyncTask.isChecking);
				showOptionCount();

		}
	}

	private void showOptionCount(){
		if(loginService != null && loginService.isLogon()){
			CheckOption checkOption = loginService.getCheckOption(getApplicationContext());
			if(checkOption != null){
				optionCount = checkOption.getCount();
			}
			LogUtils.e("CheckOption", "CheckOptionAsyncTask-------isChecking------>" + CheckOptionAsyncTask.isChecking);
			if(CheckOptionAsyncTask.isChecking){
				if(pbOption != null){
					pbOption.setVisibility(View.VISIBLE);
				}
				if(tvOptionCount != null){
					tvOptionCount.setVisibility(View.GONE);
				}

			}else {
				if(pbOption != null){
					pbOption.setVisibility(View.GONE);
				}
				if(tvOptionCount != null){
					tvOptionCount.setVisibility(View.VISIBLE);
					tvOptionCount.setText(optionCount + "");
				}

			}
		}
	}

	private void showAlertCount(){
		List<Subscription> subscriptions = pushService.readAllSubscriptions();
		if(subscriptions != null && subscriptions.size() > 0){
			pbAlert.setVisibility(View.GONE);
			tvAlertCount.setVisibility(View.VISIBLE);
			tvAlertCount.setText(subscriptions.size()+"");
			realtimeCount = subscriptions.size();
		}else {
			tvAlertCount.setVisibility(View.VISIBLE);
			tvAlertCount.setText(String.valueOf(0));
			pbAlert.setVisibility(View.GONE);
			realtimeCount = 0;
		}
	}

	private void initRatingView(){
		if (FunctionConfig.kFunAppRating){
			int openTimes = RatingUtil.getCurrentTimes(getApplicationContext());
			//Log.d(TAG, "opened Times is....." + openTimes);
			boolean neverShowAgain = RatingUtil.getNeverShowAgain(getApplicationContext());
			//Log.d(TAG, "Never Show Again?....." + String.valueOf(neverShowAgain));
			//Log.d(TAG, "isPerformed....." + RatingUtil.isPerformed(getApplicationContext()));
			if(openTimes >= 5){
				if (!neverShowAgain && RatingUtil.isPerformed(getApplicationContext()) >= 2){
					llRating.setVisibility(View.VISIBLE);
				}else{
					llRating.setVisibility(View.GONE);
				}
			}
			if (whichView == RatingService.RatingView.NeverShowAgain){
				llRating.setVisibility(View.GONE);
				return;
			}
			if (whichView == RatingService.RatingView.QuestionDoYouLikeApp){
				tvRatingInfo.setText(getResources().getString(R.string.home_rating_enjoying));
				btnNo.setText(getResources().getString(R.string.general_not_really));
				btnYes.setText(getResources().getString(R.string.general_yes));
			}else if (whichView == RatingService.RatingView.QuestionWantToSubmitReview){
				tvRatingInfo.setText(getResources().getString(R.string.home_rating_on));
				btnNo.setText(getResources().getString(R.string.general_no_thanks));
				btnYes.setText(getResources().getString(R.string.general_ok_sure));
			}else{
				tvRatingInfo.setText(getResources().getString(R.string.home_rating_feedback));
				btnNo.setText(getResources().getString(R.string.general_no_thanks));
				btnYes.setText(getResources().getString(R.string.general_ok_sure));
			}
		}
	}

    @Override
    protected void onStart() {
        showMessagesWizard();
		whichView = RatingService.RatingView.QuestionDoYouLikeApp;
		initRatingView();
        super.onRestart();
    }




    private void showMessagesWizard() {
        //Log.d(TAG, " MessageResponse......" + messageService.getMessageResponse().getMobileMessages().size());
		boolean shouldShowWizard = masterService.shouldShowWizard();
		if(shouldShowWizard) {
			startActivity(WizardActivity.createIntent(this, WizardActivity.Wizard_Home));
		}else{
			if(messageService.getMessageResponse() != null){
				showMessages = messageService.getShowMessage(messageService.getMessageResponse().getMobileMessages());
				if (showMessages != null && showMessages.size() > 0) {
					//Log.d(TAG, "To showMessages......" + showMessages.size());
					MobileMessageResponse showMessagesMobileMessageResponse = new MobileMessageResponse(showMessages);
					startActivity(com.cflint.activities.MessageWizardActivity.createIntent(MainActivity.this, showMessagesMobileMessageResponse));
				} else {
					//Log.e(TAG, "No Messages should be shown......");
					try {
						if(FunctionConfig.kFunCheckForUpdate){
							if (checkUpdateService.isShowCheckAppUpdateInfo()) {
								showCheckUpdateAppResponse();
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		//if (uri == null) {
			AppUtil.saveAppVersionName(getApplicationContext());
			AppUtil.saveAppVersionCode(getApplicationContext());
		//}
	}

	private void showCheckUpdateAppResponse(){
		final CheckAppUpdate checkAppUpdate = checkUpdateService.getCheckAppUpdate();
		if(checkAppUpdate.isUpToDate()){
			//Toast.makeText(MainActivity.this, getString(R.string.checkappversion_alert_no_update), Toast.LENGTH_LONG).show();
		}else{
			dialogCheckUpdateNotification = new DialogCheckUpdateNotification(MainActivity.this,MainActivity.this,checkUpdateService,checkAppUpdate);
			dialogCheckUpdateNotification.setCanceledOnTouchOutside(false);
			if(!isFinishing()){
				dialogCheckUpdateNotification.show();
			}
		}
	}

	public void messages(View view) {
		MenuUtil.messages(this, mDrawerLayout, mDrawerList, messageService);
	}

	public void test(View view){
		startActivity(TestActivity.createIntent(this));
		finish();
	}

	public void stationBoard(View view){

		MenuUtil.stationBoard(this, mDrawerLayout, mDrawerList);
	}

	public void stations(View view){
		MenuUtil.stations(this, mDrawerLayout, mDrawerList);
	}

	public void realtimeAlerts(View view){
		MenuUtil.realtimeAlerts(this, mDrawerLayout, mDrawerList);
	}


	public void bookTicktes(View view){
		MenuUtil.bookTicktes(this, mDrawerLayout, mDrawerList, masterService);
	}

	public void lowestFares(View view){
		MenuUtil.lowestFares(this, mDrawerLayout, mDrawerList, masterService);
	}
	public void trainschedules(View view){
		MenuUtil.trainschedules(this, mDrawerLayout, mDrawerList);
	}

	public void settings(View view){
		MenuUtil.settings(this, mDrawerLayout, mDrawerList);
	}

	public void about(View view){
		MenuUtil.about(this, mDrawerLayout, mDrawerList);
	}
	public void myTickets(View view){
		MenuUtil.myTickets(this, mDrawerLayout, mDrawerList);
	}

	public void uploadTickets(View view){
		MenuUtil.uploadTickets(this, mDrawerLayout, mDrawerList);
	}
	public void myOption(View view){
		//startActivity(LoginActivity.createIntent(MainActivity.this));
		MenuUtil.myOption(this, mDrawerLayout, mDrawerList, masterService);
	}

	public void loginOrManage(View view){
		//startActivity(LoginActivity.createIntent(MainActivity.this));
		MenuUtil.loginOrManage(this, mDrawerLayout, mDrawerList, masterService);
	}

	public void clickMenu(View view) {
		//startActivity(MenuActivity.createIntent(this, ticketCount, realtimeCount, messateCount));
		MenuUtil.clickMenu(this);
		mDrawerLayout.openDrawer(GravityCompat.END);
	}

	public void ratingNo(View view){
		ratingService.changeRatingView(whichView, false);
	}
	public void ratingYes(View view){
		ratingService.changeRatingView(whichView, true);
	}

	public void openOldMyTickeActivity(View view){
		startActivity(com.cflint.activity.MyTicketsActivity.createIntent(this));
	}
	public void refresh(View view){
		//executeMigrating();
		//MigrateDossier();
	}
	private void changeRatingView(){

	}
	public void openNationalApp(View view){
		GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.HOME, TrackerConstant.HOME_SELECT_DownloadApp,"");
		/*Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=de.hafas.android.cfl"));*/
		//File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "17a62351-2ecf-45c5-bcad-f1ce09981828(1).pkpass");
		//System.out.println("file========" + file.exists());

		//intent.setData(Uri.parse("https://www.pass2u.net/d/DvQoznSPzdUj"));
		//Uri path = Uri.fromFile(file);
		//Intent intent = new Intent(Intent.ACTION_VIEW);
		//intent.setData(path);
		//intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.passesalliance.wallet&referrer=https://www.pass2u.net/d/DvQoznSPzdUj"));

		//intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=de.hafas.android.sncbnmbs"));

		/*String url = "cflmobile://";
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));*/

		PackageManager packageManager = getPackageManager();
		Intent intent= new Intent();
		intent = packageManager.getLaunchIntentForPackage("de.hafas.android.cfl");
		if(intent == null){
			Intent i = new Intent();
			i.setAction(Intent.ACTION_VIEW);
			i.setData(Uri.parse("https://play.google.com/store/apps/details?id=de.hafas.android.cfl"));
			startActivity(i);
		}else {
			startActivity(intent);
		}
	}

	private void bindNationalApp(){
		String nationalApp = getString(R.string.national_app_prefix)
				+ " " + getString(R.string.national_app_name) + " " + getString(R.string.national_app_suffix);
		String nationalAppName = getString(R.string.national_app_name);
		SpannableStringBuilder builder = new SpannableStringBuilder(nationalApp);
		ForegroundColorSpan orange = new ForegroundColorSpan(getResources().getColor(R.color.sncb_text));
		int start = nationalApp.indexOf(nationalAppName);
		int end = start + nationalAppName.length();
		builder.setSpan(orange, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tvNationalApp.setText(builder);
	}

    public static Intent createMainIntent(Context context, boolean flag, Uri uri, RealTimeInfoRequestParameter realTimeInfoRequestParameter, String sid) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("FIRSTTIMELAND", flag);
		intent.putExtra("uri", uri);
		intent.putExtra(Intent_Key_RealTimeInfoRequestParameter, realTimeInfoRequestParameter);
		//Log.e(TAG, "Intent_Key_RealTimeInfoRequestParameter===objectMap===" + realTimeInfoRequestParameter.getMap().size());
		intent.putExtra(ActivityConstant.RECEIVE_PUSH_SUBSCRIPTION_ID,sid);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        return intent;
    }


    @Override
    protected void onDestroy() {
        if (serviceStateReceiver != null) {
            unregisterReceiver(serviceStateReceiver);
        }

		if (alertServiceStateReceiver != null) {
			unregisterReceiver(alertServiceStateReceiver);
		}
		/*if(migrateDossierReceiver != null){
			dossierDetailsService.cleanRealtimeInfoRequest();
		}*/
		if(serviceRealTimeReceiver != null){
			unregisterReceiver(serviceRealTimeReceiver);
		}
		if(refreshMultipleDossierReceiver != null){
			unregisterReceiver(refreshMultipleDossierReceiver);
		}
		if(checkOptionReceiver != null){
			unregisterReceiver(checkOptionReceiver);
		}

        super.onDestroy();
    }

/*	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}*/
/*	*//**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 *//*
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}*/

	@Override
	protected void onPause() {
		super.onPause();
		if(this.isTraceFacebook){
			AppEventsLogger.deactivateApp(this);
		}
		//mDrawerLayout.closeDrawer(GravityCompat.END);
	}

	@Override
	protected void onStop() {
		super.onStop();
		//Log.d(TAG, "onStop...");
		DossiersUpToDateService.stop = true;
	}

	@Override
	protected void onResume() {
		// showWaitDialog();
		super.onResume();
		//LogUtils.d(TAG, "onResume...");
		initView();
		DossiersUpToDateService.stop = true;
		if(this.isTraceFacebook){
			AppEventsLogger.activateApp(this);
		}
		if(FunctionConfig.kFunMyProfile){
			showLogin();
		}

		dossier();
		messateCount = messageService.getMessageResponse().getMobileMessages().size();
		/*if (migrateDossierReceiver == null) {
			migrateDossierReceiver = new MigrateDossierReceiver();
			registerReceiver(migrateDossierReceiver, new IntentFilter(MigrateDossierAsyncTask.MigrateDossier_Broadcast));
		}*/
		if (serviceRealTimeReceiver == null) {
			serviceRealTimeReceiver = new ServiceRealTimeReceiver();
			registerReceiver(serviceRealTimeReceiver, new IntentFilter(RealTimeInfoAsyncTask.RealTime_Broadcast));
		}
		if (refreshMultipleDossierReceiver == null) {
			refreshMultipleDossierReceiver = new RefreshMultipleDossierReceiver();
			registerReceiver(refreshMultipleDossierReceiver, new IntentFilter(AutoRetrievalDossiersTask.RefreshDossier_Broadcast));
		}

		if (checkOptionReceiver == null) {
			checkOptionReceiver = new CheckOptionReceiver();
			registerReceiver(checkOptionReceiver, new IntentFilter(CheckOptionAsyncTask.CheckOption_Broadcast));
		}

		if(FunctionConfig.kFunAdWords){
			//adWords();
		}
	}

/*	private void adWords(){
		AdWordsAutomatedUsageReporter.enableAutomatedUsageReporting(getApplicationContext(), "1025893038");
		AdWordsAutomatedUsageReporter.enableAutomatedUsageReporting(getApplicationContext(), "1024321798");
		AdWordsAutomatedUsageReporter.enableAutomatedUsageReporting(getApplicationContext(), "990257741");
		AdWordsAutomatedUsageReporter.enableAutomatedUsageReporting(getApplicationContext(), "955368664");
		AdWordsAutomatedUsageReporter.enableAutomatedUsageReporting(getApplicationContext(), "939594800");
		AdWordsAutomatedUsageReporter.enableAutomatedUsageReporting(getApplicationContext(), "1018955333");
	}*/

	private void dossier(){
		DossierDatabaseService dossierDatabaseService = new DossierDatabaseService(getApplicationContext());
		dossiers = dossierDatabaseService.selectDossierAll();
		if(dossiers != null && dossiers.size() > 0){
			//Log.e(TAG, "dossiers.size()...." + dossiers.size());
			ticketCount = dossiers.size();
			tvTicketCount.setText(String.valueOf(ticketCount));
		}else{
			ticketCount = 0;
			tvTicketCount.setText(String.valueOf(ticketCount));
		}
		//showHomeBanner();
		//showDossier();
		//MigrateDossier();
	}

	class ServiceRealTimeReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			//Log.d("RealTime", "ServiceRealTimeReceiver...onReceive...");
			realTimeFinished();
		}
	}

	class RefreshMultipleDossierReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			LogUtils.d("RefreshossierReceiver", "RefreshMultipleDossierReceiver...onReceive...");
			if(AutoRetrievalDossiersTask.isWorking){
				pbTickets.setVisibility(View.VISIBLE);
				tvTicketCount.setVisibility(View.GONE);
			}else {
				pbTickets.setVisibility(View.GONE);
				tvTicketCount.setVisibility(View.VISIBLE);
				dossier();
				showDossier();
			}
		}
	}

/*	class MigrateDossierReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			//Log.d("MigrateDossier", "MigrateDossierReceiver...onReceive...");
			//realTimeFinished();
			llMigratingDossier.setVisibility(View.GONE);
			llMigrateDossier.setVisibility(View.GONE);
			dossier();
		}
	}*/

	public void login(View view){
		startActivity(LoginActivity.createIntent(MainActivity.this, ""));
	}

	public void logout(View view){
		//startActivity(LoginActivity.createIntent(MainActivity.this));
		if(!isFinishing()){
			DialogLogout dialogLogout = new DialogLogout(MainActivity.this, TAG);
			dialogLogout.show();
		}
	}

	public void closeLogin	(View view){
		//startActivity(LoginActivity.createIntent(MainActivity.this));
        Date date = DateUtils.getFewLaterDay(new Date(), 30);
        String dateStr = DateUtils.dateTimeToString(date);
        SettingsPref.saveLoginViewTime(getApplicationContext(), dateStr);
		llLogin.setVisibility(View.GONE);
	}


	private void showLogin(){
		if(rlMyOption == null || tvTitle == null || tvMenuLogin == null || llMask == null || rlMenuMyOption == null
				|| ivLogout == null || ivLogin == null || tvMenuLogon == null || pbOption == null){
			return;
		}
		if(NMBSApplication.getInstance().getMasterService().loadGeneralSetting() != null
				&& NMBSApplication.getInstance().getMasterService().loadGeneralSetting().getCommercialTtlListUrl() != null
				&& !NMBSApplication.getInstance().getMasterService().loadGeneralSetting().getCommercialTtlListUrl().isEmpty()){
			rlMyOption.setVisibility(View.VISIBLE);
		}else {
			rlMyOption.setVisibility(View.GONE);
		}
		if(loginService.isLogon()){
			//LogUtils.e("showLogin", "showLogin------->" + loginService.getLogonInfo());
			LogonInfo logonInfo = loginService.getLogonInfo();
			if(logonInfo != null && logonInfo.getFirstName() != null){
				tvTitle.setText(getResources().getString(R.string.home_title_welcome) + " " + logonInfo.getFirstName());
			}
			tvMenuLogin.setText(getResources().getString(R.string.menu_content_manageprofile));
			tvMenuLogon.setText(getResources().getString(R.string.menu_content_manageprofile));

			//rlMyOption.setBackgroundColor(getColor(R.color.background_my_option));
			//rlMyOption.set
			llMask.setVisibility(View.GONE);
            rlMenuMyOption.setAlpha(1f);
			ivLogin.setVisibility(View.GONE);
			ivLogout.setVisibility(View.VISIBLE);
			llLogin.setVisibility(View.GONE);
		}else {
			llMask.setVisibility(View.VISIBLE);
			ivLogin.setVisibility(View.VISIBLE);
			ivLogout.setVisibility(View.GONE);
			rlMenuMyOption.setAlpha(0.3f);
			tvMenuOptionCount = (TextView) findViewById(R.id.tv_menu_option_count);
			tvMenuOptionCount.setText("");
			tvMenuLogin.setText(getResources().getString(R.string.menu_content_loginorcreateprofile));
			tvMenuLogon.setText(getResources().getString(R.string.menu_content_loginorcreateprofile));
			if("".equalsIgnoreCase(SettingsPref.getLoginViewTime(getApplicationContext()))){
                LogUtils.e("showLogin", "Close login time is null------->");
                llLogin.setVisibility(View.VISIBLE);
            }else {
			    Date date = DateUtils.stringToDate(SettingsPref.getLoginViewTime(getApplicationContext()));
                LogUtils.e("showLogin", "Close login view is null------->" + date);
			    if(new Date().after(date)){
                    llLogin.setVisibility(View.VISIBLE);
                }else {
					llLogin.setVisibility(View.GONE);
				}
            }

			tvTitle.setText(getResources().getString(R.string.home_title_welcome));
			tvOptionCount.setVisibility(View.GONE);
			pbOption.setVisibility(View.GONE);

			//rlMyOption.setBackgroundColor(getColor(R.color.dot_active));
		}
	}

	private android.os.Handler handler = new android.os.Handler() {
		public void handleMessage(Message msg) {
		if(loginService.isLogon()){
			LogonInfo logonInfo = loginService.getLogonInfo();
			if(logonInfo != null && logonInfo.getFirstName() != null){
				tvTitle.setText(getResources().getString(R.string.home_title_welcome) + " " + logonInfo.getFirstName());
			}
		}
		}
	};
}
