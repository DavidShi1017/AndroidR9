package com.nmbs.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nmbs.R;
import com.nmbs.activities.view.DialogAlertError;
import com.nmbs.activity.BaseListActivity;
import com.nmbs.adapter.StationboardSearchAdapter;
import com.nmbs.application.NMBSApplication;
import com.nmbs.exceptions.NetworkError;
import com.nmbs.model.StationBoardQuery;
import com.nmbs.model.StationBoardResponse;
import com.nmbs.model.StationBoardRow;
import com.nmbs.model.TravelRequest;
import com.nmbs.services.IAssistantService;
import com.nmbs.services.impl.AsyncStationBoardResponse;
import com.nmbs.services.impl.ServiceConstant;
import com.nmbs.services.impl.SettingService;
import com.nmbs.util.ActivityConstant;
import com.nmbs.util.DateUtils;
import com.nmbs.util.GoogleAnalyticsUtil;
import com.nmbs.util.NetworkUtils;
import com.nmbs.util.RatingUtil;
import com.nmbs.util.TrackerConstant;
import com.nmbs.util.Utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Activity used for displaying the UI element, user can do some inner behavior with this.
 * 
 * User can select a category to see the details.
 */
public class StationboardSearchResultActivity extends BaseListActivity {
	private final static String TAG = StationboardSearchResultActivity.class.getSimpleName();
	private static final int MENU_REFRESH = 0;
	private static final String ISAUTOREQUEST = "isautorequest";
	private TextView tvStationName,tvDate, tvStationTitle;
	private LinearLayout llLaterTrain;
	//private ListView listView;
	private ExpandableListView listView;
	private View footerView;
	private ProgressDialog progressDialog;
	private List<StationBoardRow> stationBoardRowsBoards = null;
	private List<StationBoardRow> lastBoardRows = null;
	private MyState myState;
	private IAssistantService assistantService;

	private StationBoardQuery stationBoardQuery, selectedStationBoardQuery;
	private boolean isAutoRequest;
	private boolean isShowLaterState = false;
	private Map<String,List<StationBoardRow>> mapList = new LinkedHashMap<String,List<StationBoardRow>>();
	private boolean isRefresh = false;
	private StationboardSearchAdapter testAdapter;
	private SettingService settingService;
	public static boolean hasError;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Utils.setToolBarStyle(this);
		settingService = ((NMBSApplication) getApplication()).getSettingService();
		//settingService.initLanguageSettings();
		setContentView(R.layout.activity_stationboard_result);
		bindService();	
		getIntentValue(getIntent());
		selectedStationBoardQuery = stationBoardQuery;
		bindView();
		if(stationBoardQuery != null){			
			reEnableState(stationBoardQuery);			
		}
		GoogleAnalyticsUtil.getInstance().sendScreen(StationboardSearchResultActivity.this, TrackerConstant.STATIONBOARD_RESULTS);
	}

/*	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, MENU_REFRESH, 0, getResources().getString(R.string.general_refresh)).setIcon(
				android.R.drawable.ic_menu_set_as);
		return true;
	}*/

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_REFRESH:
			if(stationBoardQuery != null){			
				isAutoRequest = true;
				isShowLaterState = false;
				isRefresh = true;
				reEnableState(stationBoardQuery);			
			}

			break;
		

		}
		return true;
	}
	
	private void showView(){
		if(!isShowLaterState)
		bindAllViewElements();
		if (isShowLaterState) {
			if(myState.stationBoardResponse.getStationBoardRows() != null && myState.stationBoardResponse.getStationBoardRows().size() != 0){
				stationBoardRowsBoards = new ArrayList<StationBoardRow>();
				for (StationBoardRow boardRow : myState.stationBoardResponse.getStationBoardRows()) {
					boolean flag = false;
					for (StationBoardRow lastRow : lastBoardRows) {
						if (boardRow.getCarrier().equals(lastRow.getCarrier()) 
								&& boardRow.getTrainNr().equals(lastRow.getTrainNr())
								&& boardRow.getDateTime().toString().equals(lastRow.getDateTime().toString())) {
							flag = true;
							break;
						}
					}
					if (!flag){
						stationBoardRowsBoards.add(boardRow);
						//Log.d(TAG, "Adding StationBoards..........");
					}

				}
				//Log.d(TAG, "StationBoards count is.........." + stationBoardRowsBoards.size());
				if(stationBoardRowsBoards.size() != 0)
				lastBoardRows = stationBoardRowsBoards;
				addGroupListData(stationBoardRowsBoards);
				listView.setGroupIndicator(null);
				for(int i=0;i<mapList.keySet().size();i++){
					this.listView.expandGroup(i);
				}
				testAdapter.notifyDataSetChanged();
			}
			
		}else{
			lastBoardRows = myState.stationBoardResponse.getStationBoardRows();
			stationBoardRowsBoards = myState.stationBoardResponse.getStationBoardRows();
			setViewStateBasedOnModel();
		}
		
		
		//footerView.setEnabled(true);
		
	}
	
	public void addGroupListData(List<StationBoardRow> stations) {
		if (isRefresh) {
			mapList.clear();
			mapList.values().clear();
			mapList = new LinkedHashMap<>();
		}
		//Log.d(TAG, "mapList====addGroupListData===" + mapList.values().size());
		if(stations != null && stations.size() > 0){
			for(StationBoardRow board : stations){
				String dateString = DateUtils.dateToString(board.getDateTime());
				//Log.d(TAG, "StationBoards dateString is.........." + dateString);
				if(mapList.containsKey(dateString)){
					mapList.get(dateString).add(board);
					//Log.d(TAG, "Adding StationBoards..........");
				}else{
					List<StationBoardRow> tempBoard = new ArrayList<StationBoardRow>();
					tempBoard.add(board);
					mapList.put(dateString, tempBoard);
					//Log.d(TAG, "Putting StationBoards..........");
				}
			}
		}
	}
	
	private void setViewStateBasedOnModel(){

		addGroupListData(stationBoardRowsBoards);

		testAdapter = new StationboardSearchAdapter(this, mapList, listView);

		//listView = null;
		if(isRefresh){
			bindAllViewElements();
		}
		listView.setAdapter(testAdapter);
		isRefresh = false;
		listView.setGroupIndicator(null);
		for(int i=0;i<mapList.keySet().size();i++){
			this.listView.expandGroup(i);
		}
	}
	
	private void bindService() {
		assistantService = ((NMBSApplication)getApplication()).getAssistantService();
		settingService = ((NMBSApplication)getApplication()).getSettingService();
	}

	public void refresh(View view){
		if(stationBoardQuery != null){
			isAutoRequest = true;
			isShowLaterState = false;
			isRefresh = true;
			reEnableState(stationBoardQuery);
		}
	}

	public void back(View view){
		finish();
	}

	private void bindView(){
		tvStationName = (TextView) findViewById(R.id.tv_station_name);
		tvDate = (TextView) findViewById(R.id.tv_date);
		tvStationTitle = (TextView) findViewById(R.id.tv_station_title);

		if(selectedStationBoardQuery != null){
			TravelRequest.TimePreference timePreference = selectedStationBoardQuery.getTimePreference();
			if (timePreference == TravelRequest.TimePreference.DEPARTURE){
				tvStationName.setText(selectedStationBoardQuery.getName() + " - " + getResources().getString(R.string.stationboard_timepreference_departures));
				tvStationTitle.setText(getResources().getString(R.string.stationboard_result_destination));
			}else{
				tvStationName.setText(selectedStationBoardQuery.getName() + " - " + getResources().getString(R.string.stationboard_timepreference_arrivals));
				tvStationTitle.setText(getResources().getString(R.string.stationboard_result_origin));
			}
			tvDate.setText(DateUtils.dateTimeToString(selectedStationBoardQuery.getDateTime(), "EEEE dd MMMM yyyy"));
		}
	}
	
	private void bindAllViewElements(){
		listView = (ExpandableListView) findViewById(android.R.id.list);
		//footerView = getLayoutInflater().inflate(R.layout.planner_list_footer_view, null);
		llLaterTrain = (LinearLayout) findViewById(R.id.ll_later_train);

		if(!isRefresh){
			//listView.addFooterView(footerView);
		}


		//isRefresh = false;
		bindListeners();
	}
	
	private void bindListeners(){
		llLaterTrain.setOnClickListener(footerViewClickListener);
	}
	
	private OnClickListener footerViewClickListener = new OnClickListener() {
		public void onClick(View v) { 
			//Called when a view has been clicked.	
			//Log.d(TAG, "footerViewClickListener");
			
			//isNextOrPrevious = true;
			if(lastBoardRows != null){
				if(lastBoardRows.size() != 0){
					StationBoardRow stationBoard = lastBoardRows.get(lastBoardRows.size() - 1);
					StationBoardQuery laterStationBoardQuery = null;
					if (stationBoard != null && stationBoardQuery != null) {
						laterStationBoardQuery = new StationBoardQuery(stationBoardQuery.getStationRCode(), stationBoard.getDateTime(), 
								stationBoardQuery.getTimePreference(), null);
					}
					isShowLaterState = true;
					
					reEnableState(laterStationBoardQuery);
				}else{
					
				}
			}						
		}
	};
	
	public void getIntentValue(Intent intent){
		stationBoardQuery = (StationBoardQuery) intent.getSerializableExtra(ActivityConstant.STATION_BOARD_QUERY);
		isAutoRequest = intent.getBooleanExtra(ISAUTOREQUEST, false);
	}
	
    public static Intent createIntent(Context context,StationBoardQuery stationBoardQuery){
		Intent intent = new Intent(context, StationboardSearchResultActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		intent.putExtra(ActivityConstant.STATION_BOARD_QUERY, stationBoardQuery);
		return intent;
	}
    
    public static Intent createIntent(Context context, StationBoardQuery stationBoardQuery, boolean flag){
		Intent intent = new Intent(context, StationboardSearchResultActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		intent.putExtra(ActivityConstant.STATION_BOARD_QUERY, stationBoardQuery);
		intent.putExtra(ISAUTOREQUEST, flag);
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
	

	// show progressDialog.
	private void showProgressDialog(ProgressDialog progressDialog) {		
		if(progressDialog == null){			
			this.progressDialog = new ProgressDialog(this);
			this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			this.progressDialog.setMessage(this.getString(R.string.alert_loading));
			this.progressDialog.setCanceledOnTouchOutside(false);
			this.progressDialog.show();
			//Log.e(TAG, "showProgressDialog");
		}
		
	}
	//hide progressDialog
	private void hideProgressDialog(ProgressDialog progressDialog) {
		if (progressDialog != null) {				
			progressDialog.hide();
			progressDialog.dismiss();
			this.progressDialog = null;
			//Log.e(TAG, "hideProgressDialog");
		}
	}
		
	
	// private class which contains all fields and objects who need to be kept accross Activity re-creates (configuration changes).
	private class MyState {
		
		public AsyncStationBoardResponse asyncStationBoardResponse;
		public StationBoardResponse stationBoardResponse;
		private boolean isRefreshed;
		public void unRegisterHandler(){
			if (asyncStationBoardResponse != null){
				asyncStationBoardResponse.unregisterHandler();
			}
		}
		
		public void registerHandler(Handler handler){
			if (asyncStationBoardResponse != null){
				asyncStationBoardResponse.registerHandler(handler);
			}
		}
	}
	
	private void reEnableState(StationBoardQuery stationBoardQuery){
		
			//Initial call
			//Log.i(TAG, "myState is null");
			myState = new MyState();		
			int connectionStatus = NetworkUtils.getNetworkState(getApplicationContext());
			//if (connectionStatus > 0 ) {
				getStationBoardData(stationBoardQuery);
			//}else {
				//Toast.makeText(this, getString(R.string.alert_no_network),Toast.LENGTH_LONG).show();
				//responseReceived(myState.asyncstationDetailResponse.getstationDetailResponse());
			//}
		
		//Log.i(TAG, "Is Refreshed? " + myState.isRefreshed);

	}
	
	// refresh Data
	private void getStationBoardData(StationBoardQuery stationBoardQuery){
		myState.asyncStationBoardResponse = assistantService.searchStationBoard(stationBoardQuery, settingService);
		myState.asyncStationBoardResponse.registerHandler(mHandler);		
		myState.registerHandler(mHandler);
		showProgressDialog(progressDialog);
	}
	
	private void responseReceived(StationBoardResponse stationBoardResponse) {
		//Log.e(TAG, "responseReceived....");
		myState.stationBoardResponse = stationBoardResponse;
		/*if (footerView != null) {
			footerView.setClickable(true);
			footerView.setEnabled(true);
		}*/
		RatingUtil.saveStationboardSearch(getApplicationContext());
		myState.unRegisterHandler();
		//myState.asyncStationDetailResponse = null;	
		showView();
		myState.isRefreshed = true;
		
		//if(!isShowLaterState){
			hideProgressDialog(progressDialog);
		//}else{
		//}
		isShowLaterState = false;
		//Log.d(TAG, "show response");
	}
	
	//Handler which is called when the App is refreshed.
	// The what parameter of the message decides if there was an error or not.
    private Handler mHandler = new Handler(){   
        public void handleMessage(Message msg) {   
			switch (msg.what) {   
            case ServiceConstant.MESSAGE_WHAT_OK:
            	responseReceived(myState.asyncStationBoardResponse.getStationBoardResponse());                 
            	                 
            	break;    
			case ServiceConstant.MESSAGE_WHAT_ERROR:
				
				Bundle bundle = msg.getData();
				NetworkError error = (NetworkError) bundle.getSerializable(ServiceConstant.PARAM_OUT_ERROR);
				String responseErrorMessage = bundle.getString(ServiceConstant.PARAM_OUT_ERROR_MESSAGE);
				if(isShowLaterState){
					hideProgressDialog(progressDialog);
					DialogAlertError dialogError = new DialogAlertError(StationboardSearchResultActivity.this, getResources().getString(R.string.general_information),
							getResources().getString(R.string.general_server_unavailable));
					dialogError.show();
				}else{
					hasError = true;
					hideProgressDialog(progressDialog);
					finish();
				}


/*				switch (error) {
				case TIMEOUT:
					hideProgressDialog(progressDialog);

					if(isShowLaterState){
					}else{
						finish();
					}
					isShowLaterState = false;
					break;
				case CustomError:
					hideProgressDialog(progressDialog);
					
					if (responseErrorMessage == null) {
						responseErrorMessage = getString(R.string.general_server_unavailable);
					}
					
					//Toast.makeText(StationboardSearchResultActivity.this, responseErrorMessage, Toast.LENGTH_LONG).show();
					if(isShowLaterState){
					}else{
						finish();
					}
					isShowLaterState = false;
					break;
				default:
					break;			
				}*/
				//Log.d(TAG, "handler receive");
				break;
			}
		};
    };
	
}
