package com.cflint.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cflint.R;
import com.cflint.activity.BaseActivity;
import com.cflint.adapter.ConnectionDetailLegAdapter;
import com.cflint.adapter.ConnectionHafasMessageAdapter;
import com.cflint.application.NMBSApplication;
import com.cflint.async.RealTimeInfoAsyncTask;
import com.cflint.model.Connection;
import com.cflint.model.Dossier;
import com.cflint.model.RealTimeConnection;
import com.cflint.model.RealTimeInfoRequestForConnection;
import com.cflint.model.RealTimeInfoRequestParameter;
import com.cflint.model.RealTimeInfoResponse;
import com.cflint.model.TrainIcon;
import com.cflint.services.IScheduleService;
import com.cflint.services.impl.DossierDetailsService;
import com.cflint.services.impl.ServiceConstant;
import com.cflint.services.impl.SettingService;
import com.cflint.util.DateUtils;
import com.cflint.util.GoogleAnalyticsUtil;
import com.cflint.util.TrackerConstant;
import com.cflint.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity used for displaying the UI element, user can do some inner behavior with this.
 *
 * User can select a category to see the details.
 */
public class DossierConnectionDetailActivity extends BaseActivity {
	private final static String TAG = StationboardSearchResultActivity.class.getSimpleName();
	private final static String Intent_Key_Connection = "Connection";
	private final static String Intent_Key_Dossier = "Dossier";
	private final static String Intent_Key_ShouldRefresh = "Intent_Key_ShouldRefresh";
	private final static String Intent_Key_RealTimeConnection = "RealTimeConnection";

	private RealTimeConnection currentRealTimeConnection;
	private TextView stationNameView, departureTimeView, transferView, tvRealTimeResult;
	private LinearLayout hafasMessageLinearLayout, legLinearLayout, llRealTime;
	private ConnectionHafasMessageAdapter connectionHafasMessageAdapter;
	private ConnectionDetailLegAdapter connectionDetailLegAdapter;
	private ImageView triangleIcon;
	private IScheduleService scheduleService;
	private Connection connection;
	private ProgressDialog progressDialog;

	private SettingService settingService;
	private List<TrainIcon> trainIconList ;
	private Dossier dossier;
	private DossierDetailsService dossierDetailsService;
	private ProgressBar pbRealTime;
	private boolean shouldRefresh;
	private RealTimeConnection realTimeConnection;
	private Button btnRefresh;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Utils.setToolBarStyle(this);
		settingService = ((NMBSApplication) getApplication()).getSettingService();
		//settingService.initLanguageSettings();
		dossierDetailsService = ((NMBSApplication) getApplication()).getDossierDetailsService();
		setContentView(R.layout.activity_dossier_connection_detail);
		bindAllViewElement();
		initService();
		getIntentValue(getIntent());
		initData();
		GoogleAnalyticsUtil.getInstance().sendScreen(DossierConnectionDetailActivity.this, TrackerConstant.DOSSIER_CONNECTIONDETAILS);
	}

	private void initService(){
		scheduleService = ((NMBSApplication)getApplication()).getScheduleService();
		settingService = ((NMBSApplication)getApplication()).getSettingService();

	}

	private void showRealTime(){
		DossierDetailsService dossierDetailsService = NMBSApplication.getInstance().getDossierDetailsService();
		RealTimeInfoResponse realTimeInfoResponse = dossierDetailsService.readRealTimeInfoById(connection.getConnectionId(), getApplicationContext());

		currentRealTimeConnection = (RealTimeConnection)dossierDetailsService.getRealTime(realTimeInfoResponse);
		//Log.e(TAG, "showRealTime...isRefreshing..." +  RealTimeInfoAsyncTask.isRefreshing);
		if (shouldRefresh && RealTimeInfoAsyncTask.isRefreshing) {
			llRealTime.setVisibility(View.VISIBLE);
			pbRealTime.setVisibility(View.VISIBLE);
			tvRealTimeResult.setText(getResources().getString(R.string.general_refreshing_realtime));
			tvRealTimeResult.setTextColor(getResources().getColor(R.color.textcolor_thirdaction));
			llRealTime.setBackgroundColor(getResources().getColor(R.color.background_light_blue));

		} else {
			pbRealTime.setVisibility(View.GONE);
			if (realTimeInfoResponse != null) {
				if (realTimeInfoResponse.getIsSuccess()) {
					llRealTime.setVisibility(View.GONE);
				} else {
					llRealTime.setVisibility(View.VISIBLE);
					tvRealTimeResult.setText(getResources().getString(R.string.general_refreshing_realtime_failed));
					tvRealTimeResult.setTextColor(getResources().getColor(R.color.textcolor_error));
					llRealTime.setBackgroundColor(getResources().getColor(R.color.background_error));
				}
			}
		}
	}

	private void initData(){
		if(shouldRefresh){
			btnRefresh.setVisibility(View.VISIBLE);
		}else{
			btnRefresh.setVisibility(View.GONE);
		}
		showRealTime();
		trainIconList = scheduleService.getTrainIcons();
		if(this.connection != null){
			this.stationNameView.setText(this.connection.getOriginStationName() + " - " + this.connection.getDestinationStationName());
			this.departureTimeView.setText(DateUtils.dateTimeToString(this.connection.getDeparture(), "EEEE dd MMMM yyyy"));
			if(this.connection.getNumberOfTransfers() == 0){
				this.transferView.setText(this.getString(R.string.general_direct_train) + " - " + DateUtils.FormatToHrDate(connection.getDuration(), getApplicationContext()));
			}else{
				if(this.connection.getNumberOfTransfers() > 1){
					transferView.setText(connection.getNumberOfTransfers() + " " + getString(R.string.general_transfers) + " - " + DateUtils.FormatToHrDate(connection.getDuration(), getApplicationContext()));
				}else{
					transferView.setText(connection.getNumberOfTransfers() + " " + getString(R.string.general_transfer) + " - " + DateUtils.FormatToHrDate(connection.getDuration(), getApplicationContext()));
				}

			}

			this.legLinearLayout.removeAllViews();
			if(currentRealTimeConnection == null){
				connectionDetailLegAdapter = new ConnectionDetailLegAdapter(this, this.connection.getLegs(),trainIconList, null);
			}else{
				connectionDetailLegAdapter = new ConnectionDetailLegAdapter(this, this.connection.getLegs(),trainIconList, this.currentRealTimeConnection.getRealTimeInfoLegs());
			}

			for(int j=0; j <this.connection.getLegs().size(); j++){
				connectionDetailLegAdapter.getConnectionDetailLegView(j, this.legLinearLayout, dossier, connection);
			}
			if(this.currentRealTimeConnection != null){
				initHafasMessage();
			}
		}
	}

	private void initHafasMessage(){

		this.hafasMessageLinearLayout.removeAllViews();
/*					HafasMessage hafasMessage = new HafasMessage("DAVID", "Shi", "scheduleHafasMessageAdapter,", " currentRealTimeConnection", "2016-08-15", "2016-08-16");
			HafasMessage hafasMessage2 = new HafasMessage("DAVID", "Shi", "scheduleHafasMessageAdapter,", " currentRealTimeConnection", "2016-08-15", "2016-08-16");

			List<HafasMessage> hafasMessages = new ArrayList<>();
			hafasMessages.add(hafasMessage);
			hafasMessages.add(hafasMessage2);
			currentRealTimeConnection.setHafasMessages(hafasMessages);*/
		connectionHafasMessageAdapter = new ConnectionHafasMessageAdapter(this, this.currentRealTimeConnection.getHafasMessages());
		if(this.currentRealTimeConnection.getHafasMessages() != null && this.currentRealTimeConnection.getHafasMessages().size() > 0){
			this.triangleIcon.setVisibility(View.VISIBLE);
		}else{
			this.triangleIcon.setVisibility(View.GONE);
		}
		//Log.e("HafasMessage", "HafasMessage..." + this.currentRealTimeConnection.getHafasMessages().size());
		for(int i=0;i<this.currentRealTimeConnection.getHafasMessages().size();i++){
			connectionHafasMessageAdapter.getScheduleHafasMessageView(i, this.hafasMessageLinearLayout);
		}
	}

	private void bindAllViewElement(){
		this.stationNameView = (TextView) findViewById(R.id.tv_dossier_connect_detail_station_name);
		this.departureTimeView = (TextView) findViewById(R.id.tv_dossier_connect_detail_station_departure_time);
		this.transferView = (TextView) findViewById(R.id.tv_dossier_connect_detail_transfer);
		this.hafasMessageLinearLayout = (LinearLayout) findViewById(R.id.ll_dossier_connect_detail_hafas_message_layout);
		this.legLinearLayout = (LinearLayout) findViewById(R.id.ll_dossier_connect_detail_leg_layout);
		this.triangleIcon = (ImageView) findViewById(R.id.iv_dossier_connect_detail_triangle_icon);
		this.llRealTime = (LinearLayout) findViewById(R.id.ll_connections_detail_realtime);
		this.pbRealTime = (ProgressBar) findViewById(R.id.pb_connections_detail_realtime);
		this.tvRealTimeResult = (TextView) findViewById(R.id.tv_connections_detail_realtime_result);
		this.btnRefresh = (Button) findViewById(R.id.tv_refresh);
	}

	public void getIntentValue(Intent intent){
		connection = (Connection) intent.getSerializableExtra(Intent_Key_Connection);
		dossier = (Dossier) intent.getSerializableExtra(Intent_Key_Dossier);
		shouldRefresh = intent.getBooleanExtra(Intent_Key_ShouldRefresh, false);
		realTimeConnection = (RealTimeConnection) intent.getSerializableExtra(Intent_Key_RealTimeConnection);
		//Log.e(TAG, "realTimeConnection...is null?..." +  realTimeConnection);
	}

	public static Intent createIntent(Context context, Connection connection, Dossier dossier, boolean shouldRefresh, RealTimeConnection realTimeConnection){
		Intent intent = new Intent(context, DossierConnectionDetailActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		intent.putExtra(Intent_Key_Connection, connection);
		intent.putExtra(Intent_Key_Dossier, dossier);
		intent.putExtra(Intent_Key_RealTimeConnection, realTimeConnection);
		intent.putExtra(Intent_Key_ShouldRefresh, shouldRefresh);
		return intent;
	}

	@Override
	protected void onPause() {
		//Log.d(TAG, "onPause");
		super.onPause();
	}

	@Override
	protected void onResume() {
		//Log.d(TAG, "onResume");
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}


	public void refresh(View view){
		//Log.d(TAG, "refresh..." + connection);
		this.btnRefresh.setClickable(false);
		this.btnRefresh.setBackground(getResources().getDrawable(R.drawable.group_default_refresh_disable));
		this.btnRefresh.setTextColor(getResources().getColor(R.color.color_disable));
		RealTimeInfoAsyncTask.isRefreshing = true;
		if(connection != null){
			RealTimeInfoRequestForConnection realTimeInfoRequestForConnection = new RealTimeInfoRequestForConnection(connection.getConnectionId(),
					DossierDetailsService.Dossier_Realtime_Connection, connection.getReconCtx(), connection.getDeparture());
			List<Object> realTimeInfoRequests = new ArrayList<>();
			realTimeInfoRequests.add(realTimeInfoRequestForConnection);
			RealTimeInfoRequestParameter realTimeInfoRequestParameter = new RealTimeInfoRequestParameter(realTimeInfoRequests);
			RealTimeInfoAsyncTask asyncTask = new RealTimeInfoAsyncTask(getApplicationContext(), settingService, dossierDetailsService, realTimeHandler, realTimeInfoRequestParameter);
			asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		showRealTime();
	}

	private Handler realTimeHandler = new Handler() {
		public void handleMessage(Message msg) {
			RealTimeInfoAsyncTask.isRefreshing = false;
			btnRefresh.setClickable(true);
			btnRefresh.setBackground(getResources().getDrawable(R.drawable.group_default_refresh));
			btnRefresh.setTextColor(getResources().getColor(R.color.background_secondaryaction));
			//showRealTime();
			initData();
			switch (msg.what) {
				case RealTimeInfoAsyncTask.REALTIMEMESSAGE:

					//Log.d(TAG, "realTimeHandler...Finished RealTime..");
					break;
				case ServiceConstant.MESSAGE_WHAT_ERROR:
					break;
			}
		}
	};

	private void showWaitDialog() {
		runOnUiThread(new Runnable() {
			public void run() {
				if (progressDialog == null) {
					//Log.e(TAG, "Show Wait Dialog....");
					progressDialog = ProgressDialog.show(DossierConnectionDetailActivity.this, null,
							getString(R.string.message_view_loading), true);
					progressDialog.setCanceledOnTouchOutside(false);

				}
			}
		});
	}

	// hide progressDialog
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

	public void back(View view){
		finish();
	}

}
