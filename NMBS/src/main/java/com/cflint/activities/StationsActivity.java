package com.cflint.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.cflint.R;
import com.cflint.activity.BaseActivity;
import com.cflint.adapter.FavoriteAndNormalStationsAdapter;
import com.cflint.application.NMBSApplication;
import com.cflint.model.Station;
import com.cflint.services.IAssistantService;
import com.cflint.services.impl.SettingService;
import com.cflint.services.impl.StationService;
import com.cflint.util.ComparatorStationName;
import com.cflint.util.GoogleAnalyticsUtil;
import com.cflint.util.TrackerConstant;
import com.cflint.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StationsActivity extends BaseActivity {
	private final static String TAG = StationsActivity.class
			.getSimpleName();
	public static final int REUTEST_CODE = 0;

	private IAssistantService assitantService;

	private List<Station> stationFavorite = new ArrayList<Station>();
	private List<Station> normalStations = new ArrayList<Station>();
	private ProgressDialog progressDialog;
	private EditText searchEditText;
	private FavoriteAndNormalStationsAdapter adapter;
	private ExpandableListView listView;
	private List<List<Station>> allStations = new ArrayList<List<Station>>();
    private Station station ;
    private Comparator<Station> comp = new ComparatorStationName();
	public static final  String PRMA_STATION = "Station";
	public static final  String PRMA_STATION_FROM_CODE = "Station_from_code";
	public static final  String PRMA_STATION_TO_CODE = "Station_to_code";
	public static final  String PRMA_STATION_VIA_CODE = "Station_via_code";
	public static final  String PRMA_SELECTION_FLAG = "Station_selection_flag";
	private static boolean isFromSchedule = false;
	private String lastSelectedFromStationCode = "";
	private String lastSelectedToStationCode = "";
	private String lastSelectedViaStationCode = "";
	private SettingService settingService;
	private StationService stationService;
	private int selectionFlag;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Utils.setToolBarStyle(this);
		settingService = ((NMBSApplication) getApplication()).getSettingService();
		//settingService.initLanguageSettings();
		setContentView(R.layout.activity_stations);
		bindAllViewElements();
		bindService();
		getIntentValue(getIntent());
		bindListeners();
		setViewStateBasedOnModel();

	}

	private void bindService() {
		assitantService = ((NMBSApplication) getApplication()).getAssistantService();
		settingService = ((NMBSApplication) getApplication()).getSettingService();
		stationService = ((NMBSApplication) getApplication()).getStationService();
	}

	private void bindAllViewElements() {
		progressDialog = new ProgressDialog(StationsActivity.this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage(getString(R.string.alert_loading));
		listView = (ExpandableListView) findViewById(R.id.ll_stations);
		searchEditText = (EditText) findViewById(R.id.et_search_station);
	}

	public void responseReceive() {
		
		adapter = new FavoriteAndNormalStationsAdapter(this, allStations,
				stationFavorite, normalStations, assitantService, stationService, listView, R.string.station_nofavorite);
		listView.setAdapter(adapter);
		listView.setOnChildClickListener(new OnChildClickListener(){

			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				station = (Station) adapter.getChild(groupPosition, childPosition);
				if (station != null) {
					saveData(0);
					finish();
				}
				
				return false;
			}
			
		});
		listView.setGroupIndicator(null);
		for (int i = 0; i < 2; i++)
			listView.expandGroup(i);
	}

	private void setViewStateBasedOnModel() {

		//new LoadStationCollectionServiceWithAsyncTask().execute(assitantService);
		//ExecutorService FULL_TASK_EXECUTOR = (ExecutorService) Executors.newCachedThreadPool();
		//new LoadStationCollectionServiceWithAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		try {

			List<Station> allStation = stationService.readHafasStations(settingService.getCurrentLanguagesKey());
			stationFavorite = stationService.getStationFavorite(allStation);
			normalStations = stationService.getStationWithoutFavorite(settingService.getCurrentLanguagesKey(), allStation);
			stationService.filterFavorite(stationFavorite, isFromSchedule, selectionFlag, lastSelectedFromStationCode,
					lastSelectedToStationCode, lastSelectedViaStationCode);


			Collections.sort(stationFavorite, comp);
			Collections.sort(normalStations, comp);
			allStations.add(stationFavorite);
			allStations.add(normalStations);
			assitantService.setAllStations(allStations);

		} catch (Exception e) {
			e.printStackTrace();
		}
		responseReceive();
	}

	public void back(View view){
		finish();
	}

	private class LoadStationCollectionServiceWithAsyncTask extends AsyncTask<IAssistantService, Void, Void> {

		@Override
		protected void onPreExecute() {
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(IAssistantService... params) {
			try {

				List<Station> allStation = stationService.readHafasStations(settingService.getCurrentLanguagesKey());
				stationFavorite = stationService.getStationFavorite(allStation);
				normalStations = stationService.getStationWithoutFavorite(settingService.getCurrentLanguagesKey(), allStation);
				stationService.filterFavorite(stationFavorite, isFromSchedule, selectionFlag, lastSelectedFromStationCode,
						lastSelectedToStationCode, lastSelectedViaStationCode);


			    Collections.sort(stationFavorite, comp);
			    Collections.sort(normalStations, comp);
				allStations.add(stationFavorite);
				allStations.add(normalStations);
				assitantService.setAllStations(allStations);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			hideProgressDialog();
			responseReceive();
		}

	}

	public void selectedStationValue(Station station){
		this.station = station;
		if (station != null) {
			saveData(1);
			finish();
		}
	}
	
	private void hideProgressDialog() {
		if (progressDialog != null) {
			progressDialog.hide();
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	private void bindListeners() {
		searchEditText.addTextChangedListener(new TextChangedListener());
	}

	private class TextChangedListener implements  TextWatcher{

		public void afterTextChanged(Editable s) {
			if (adapter != null) {
				adapter.getFilter().filter(s);
			}
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}
		
	}
	
	public void getIntentValue(Intent intent) {
		if(isFromSchedule){
			GoogleAnalyticsUtil.getInstance().sendScreen(StationsActivity.this, TrackerConstant.SCHEDULE_STATIONS06ELECTION);
			lastSelectedFromStationCode = intent.getStringExtra(PRMA_STATION_FROM_CODE);
			lastSelectedToStationCode = intent.getStringExtra(PRMA_STATION_TO_CODE);
			lastSelectedViaStationCode = intent.getStringExtra(PRMA_STATION_VIA_CODE);
			selectionFlag = intent.getIntExtra(PRMA_SELECTION_FLAG, 0);
		}else{
			GoogleAnalyticsUtil.getInstance().sendScreen(StationsActivity.this, TrackerConstant.STATIONBOARD_STATIONSELECTION);
		}
	}

	public static Intent createIntent(Context context) {
		Intent intent = new Intent(context, StationsActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		isFromSchedule = false;
		return intent;
	}

	public static Intent createIntentFromSchedule(Context context,String stationFromCode, String stationToCode, String viaCode, int selectionFlag) {
		Intent intent = new Intent(context, StationsActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		intent.putExtra(PRMA_STATION_FROM_CODE, stationFromCode);
		intent.putExtra(PRMA_STATION_TO_CODE, stationToCode);
		intent.putExtra(PRMA_STATION_VIA_CODE, viaCode);
		intent.putExtra(PRMA_SELECTION_FLAG, selectionFlag);
		isFromSchedule = true;
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
	
	public void saveData(int resultCode){
		Intent stationIntent = new Intent();
		stationIntent.putExtra(PRMA_STATION, station);
		this.setResult(1, stationIntent);
	}

}
