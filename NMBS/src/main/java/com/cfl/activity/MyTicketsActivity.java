package com.cfl.activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;

import com.cfl.R;
import com.cfl.activities.view.DialogError;
import com.cfl.activities.view.DialogSuccessful;
import com.cfl.adapter.TicketsAdapter.ReloadCallback;
import com.cfl.adapter.TicketsTitleAdapter;
import com.cfl.adapter.TicketsTitleAdapter.RefreshCallback;
import com.cfl.application.NMBSApplication;
import com.cfl.async.OrderAsyncTask;
import com.cfl.dataaccess.database.AssistantDatabaseService;
import com.cfl.dataaccess.restservice.impl.DossierDetailDataService;
import com.cfl.listeners.RefreshOldDossierListener;
import com.cfl.model.DossierDetailsResponse;
import com.cfl.model.Order;
import com.cfl.services.IAssistantService;
import com.cfl.services.IMasterService;
import com.cfl.services.IMessageService;
import com.cfl.services.impl.ServiceConstant;
import com.cfl.services.impl.SettingService;
import com.cfl.util.LocaleChangedUtils;

import java.util.List;

/**
 * 
 * An activity that displays a list of Assistant data source such as an array or
 * Cursor, and exposes event handlers when the user selects an item.
 */
public class MyTicketsActivity extends BaseActivity implements RefreshOldDossierListener{

	// //////////////////////////////////////////////////////////////////////
	/**
	 * Note: The activity is a history activity, so we will refactor it when new
	 * requirement is confirmed.
	 */
	// //////////////////////////////////////////////////////////////////////
	private final static String TAG = MyTicketsActivity.class.getSimpleName();
	private IAssistantService assistantService;
	private List<Order> orderListFormDB;
	private IMasterService masterService;
	private List<Order> orderHistoryList;
	private List<Order> orderCanceledList;
	private OrderAsyncTask orderAsyncTask;

	private TicketsTitleAdapter ticketsTitleAdapter;
	private TicketsTitleAdapter ticketsHistoryTitleAdapter;
	private TicketsTitleAdapter canceledTitleAdapter;

	private LinearLayoutForListView assistantListView;
	private LinearLayoutForListView assistantHistoryListView;
	private LinearLayoutForListView canceledListView;
	private View upcommingLinearLayout, pastLinearLayout, noticketTextView,
			canceledLinearLayout;
	private SettingService settingService;

	private ProgressDialog progressDialog;
	public static final int FLAG_FIND_ORDER = 0;
	public static final int FLAG_FIND_ORDER_HISTORY = 1;
	public static final int FLAG_FIND_ORDER_CANCELED = 3;
	public static final int FLAG_FIND_ORDER_PASTTED = 4;
	private boolean isMultiple;

	private IMessageService messageService;
	private String responseErrorMessage;


	private ImageView refreshButton;
	private ImageView refreshButtonImageView;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		assistantService = ((NMBSApplication) getApplication()).getAssistantService();
		masterService = ((NMBSApplication) getApplication()).getMasterService();
		settingService = ((NMBSApplication) getApplication()).getSettingService();
		settingService.initLanguageSettings();
		messageService = ((NMBSApplication) getApplication()).getMessageService();
		setContentView(R.layout.assistant_view);
		loadData();
	}
	
	private void loadData() {
		Log.e(TAG, "loadData...");
		orderAsyncTask = new OrderAsyncTask(mHandler, FLAG_FIND_ORDER, masterService);
		orderAsyncTask.execute(assistantService);
		showWaitDialog();
	}

	// bind all view elements
	private void bindAllViewElements() {
		// historyView = (View)
		// findViewById(R.id.assistant_view_history_RelativeLayout);
		assistantListView = (LinearLayoutForListView) findViewById(R.id.assistant_list);
		assistantHistoryListView = (LinearLayoutForListView) findViewById(R.id.assistant_history_list);
		upcommingLinearLayout = findViewById(R.id.assistant_view_upcomming_LinearLayout);
		pastLinearLayout = findViewById(R.id.assistant_view_past_LinearLayout);
		noticketTextView = findViewById(R.id.assistant_view_noticket_TextView);
		canceledLinearLayout = findViewById(R.id.assistant_view_canceled_LinearLayout);
		canceledListView = (LinearLayoutForListView) findViewById(R.id.assistant_canceled_list);

		refreshButton = (ImageView) findViewById(R.id.refresh_Button);
		refreshButton.setOnClickListener(refreshButtonOnClickListener);
		refreshButtonImageView = (ImageView) findViewById(R.id.refresh_Button);

		if ((assistantService.getTickesHelpers() != null && assistantService.getTickesHelpers().size() > 0)
				|| (assistantService.getTickesHistoryHelpers() != null && assistantService.getTickesHistoryHelpers().size() > 0)
				||(assistantService.getCanceledHelpers() != null && assistantService.getCanceledHelpers().size() > 0)) {
			noticketTextView.setVisibility(View.GONE);
			refreshButton.setVisibility(View.VISIBLE);
			refreshButtonImageView.setVisibility(View.VISIBLE);
		} else {
			noticketTextView.setVisibility(View.VISIBLE);
			refreshButton.setVisibility(View.GONE);
			refreshButtonImageView.setVisibility(View.GONE);
		}
	}

	// Bind all Listeners
	public void bindAllListeners() {

		assistantListView.setOnclickLinstener(assistantListViewOnClickListener);
		assistantHistoryListView.setOnclickLinstener(assistantHistoryListViewOnClickListener);
		canceledListView.setOnclickLinstener(canceledListViewListViewOnClickListener);
	}
	
	private OnClickListener refreshButtonOnClickListener = new OnClickListener() {

		public void onClick(View v) {
			List<Order> orders = assistantService.uniteOrders(orderListFormDB, orderHistoryList, orderCanceledList);
			getDossierData(orders);
			//showAlertDialog();
		}
	};


	private OnClickListener assistantListViewOnClickListener = new OnClickListener() {

		public void onClick(View v) { // Called when a view has been clicked.
			int position = v.getId();
			ticketsTitleAdapter.controlAdapter(position);

		}
	};

	private OnClickListener assistantHistoryListViewOnClickListener = new OnClickListener() {

		public void onClick(View v) { // Called when a view has been clicked.
			int position = v.getId();
			ticketsHistoryTitleAdapter.controlAdapter(position);
		}
	};

	private OnClickListener canceledListViewListViewOnClickListener = new OnClickListener() {

		public void onClick(View v) { // Called when a view has been clicked.
			int position = v.getId();
			canceledTitleAdapter.controlAdapter(position);
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		LocaleChangedUtils.initLanguageSettings(NMBSApplication.getContext());
	}
	
	
	// When search order data finished, this function will do something for
	// Activity.
	private void asyncTaskFinished() {

		orderListFormDB = orderAsyncTask.getListOrders();
		orderHistoryList = orderAsyncTask.getListHistoryOrders();
		orderCanceledList = orderAsyncTask.getListCanceledOrders();
		bindAllViewElements();
		bindAllListeners();
		if (assistantService.getTickesHelpers() != null && assistantService.getTickesHelpers().size() > 0) {
			upcommingLinearLayout.setVisibility(View.VISIBLE);
			assistantListView.setVisibility(View.VISIBLE);
			// if (assistantService.isTimeToRefresh()) {
			// reEnableState();
			// }else {

			ticketsTitleAdapter = new TicketsTitleAdapter(this, R.layout.tickets_title_item, assistantService.getTickesHelpers(), assistantService, true, false);

			ticketsTitleAdapter.setRefreshCallback(new RefreshCallback() {

				public void refresh(List<Order> orders) {
					if (orders != null && orders.size() > 0) {
						Log.d(TAG, "refresh orders..." + orders.size());
						Log.d(TAG, "refresh orders DNR..."+ orders.get(0).getDNR());
						isMultiple = false;
						getDossierData(orders);
					}
				}
			});
			assistantListView.removeAllViews();
			assistantListView.setAdapter(ticketsTitleAdapter);
			// }

		} else {
			assistantListView.setVisibility(View.GONE);
			upcommingLinearLayout.setVisibility(View.GONE);
			
			//hideWaitDialog();
		}
		if (assistantService.getTickesHistoryHelpers() != null && assistantService.getTickesHistoryHelpers().size() > 0) {
			assistantHistoryListView.setVisibility(View.VISIBLE);
			pastLinearLayout.setVisibility(View.VISIBLE);

			ticketsHistoryTitleAdapter = new TicketsTitleAdapter(this, R.layout.tickets_title_item,
					assistantService.getTickesHistoryHelpers(), assistantService, false, false);

			ticketsHistoryTitleAdapter.setRefreshCallback(new RefreshCallback() {

				public void refresh(List<Order> orders) {
					Log.d(TAG, "refresh orders..." + orders.size());
					Log.d(TAG, "refresh orders DNR..."+ orders.get(0).getDNR());
					isMultiple = false;
					getDossierData(orders);
				}
			});

			assistantHistoryListView.removeAllViews();

			assistantHistoryListView.setAdapter(ticketsHistoryTitleAdapter);

		} else {
			
			assistantHistoryListView.setVisibility(View.GONE);
			pastLinearLayout.setVisibility(View.GONE);
		}
		if (assistantService.getCanceledHelpers() != null&& assistantService.getCanceledHelpers().size() > 0) {
			canceledListView.setVisibility(View.VISIBLE);
			canceledLinearLayout.setVisibility(View.VISIBLE);

			canceledTitleAdapter = new TicketsTitleAdapter(this, R.layout.tickets_title_item, assistantService.getCanceledHelpers(), assistantService, false, false);

			canceledTitleAdapter.setReloadCallback(new ReloadCallback() {
				public void reloadData() {
					loadData();
				}
			});
			canceledTitleAdapter.setRefreshCallback(new RefreshCallback() {

				public void refresh(List<Order> orders) {
					Log.d(TAG, "refresh orders..." + orders.size());
					Log.d(TAG, "refresh orders DNR..."+ orders.get(0).getDNR());
					isMultiple = false;
					getDossierData(orders);
				}
			});
			canceledListView.removeAllViews();
			canceledListView.setAdapter(canceledTitleAdapter);
		} else {
			canceledLinearLayout.setVisibility(View.GONE);
			canceledListView.setVisibility(View.GONE);
		}
		hideWaitDialog();
	}


	// refresh Data
	private void getDossierData(List<Order> orders) {
		MigrateDossierAsyncTask migrateDossierAsyncTask = new MigrateDossierAsyncTask(NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey(),
				getApplicationContext(), orders);
		migrateDossierAsyncTask.execute((Void) null);
		/*if (myState == null) {
			myState = new MyState();
		}
		if (orders != null && orders.size() > 0) {
			showWaitDialog();
			myState.asyncDossierAfterSaleResponse = assistantService.searchDossierAfterSale(null, orders, settingService, 
					hasConnection, isRefreshAllRealTime);
			myState.asyncDossierAfterSaleResponse.registerHandler(handler);
			myState.registerHandler(handler);
		}*/
		// showWaitDialog();
	}

	// receive right information, change myState status and hide progressDialog.

	
	private void startOrderTask(){
		orderAsyncTask = new OrderAsyncTask(mHandler, FLAG_FIND_ORDER, masterService);
		orderAsyncTask.execute(assistantService);
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			Log.e(TAG, "handler receive: handleMessage...!");
			switch (msg.what) {
			case ServiceConstant.MESSAGE_WHAT_OK:

				// reEnableState();
				asyncTaskFinished();
				
				new Thread() {
					public void run() {
						assistantService.deletePastTicket();
					}
				}.start();
				//Log.d(TAG, "handler receive: Success!");
				break;
			}
		};
	};

	// show progressDialog.
	private void showWaitDialog() {
		runOnUiThread(new Runnable() {
			public void run() {
				if (progressDialog == null) {
					//Log.e(TAG, "Show Wait Dialog....");
					progressDialog = ProgressDialog.show(MyTicketsActivity.this, getString(R.string.alert_data_sending_to_server),
							getString(R.string.alert_waiting), true);
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Check if the key event was the BACK key and if there's history
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			// webView.goBack();
			NMBSApplication.tabSpec = 0;
			//Log.d(TAG, "onKeyDown.. ");
			// return true;
			finish();
			
			return false;
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return false;
		}
		// If it wasn't the BACK key or there's no web page history, bubble up
		// to the default
		// system behavior (probably exit the activity)
		return true;
	}

	protected void onStart() {
		super.onStart();
		// Register to get ACTION_RELOAD_DATA broadcasts
	}
	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

	@Override
	protected void onRestart() {
		Log.d(TAG, "onRestart...");
		loadData();
		super.onRestart();
	}

	@Override
	public void stayHere() {
		loadData();
	}

	@Override
	public void viewRefreshedDossier() {
		startActivity(com.cfl.activities.MyTicketsActivity.createIntent(MyTicketsActivity.this));
		//loadData();
	}

/*	class ServiceStateReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "onReceive got REALTIME_SERVICE_ACTION broadcast");
			if (ServiceConstant.REALTIME_SERVICE_ACTION.equalsIgnoreCase(intent.getAction().toString())) {
				Log.d(TAG, "onReceive got ERROR_COUNT is::::" + isHasError);
				isHasError = intent.getBooleanExtra(RealTimeAsyncTask.ERROR_COUNT, false);
				loadData();
					}
				}
			}

			class DossierServiceStateReceiver extends BroadcastReceiver {
				@Override
				public void onReceive(Context context, Intent intent) {
					Log.d(TAG, "onReceive got DOSSIER_SERVICE_ACTION broadcast");
					if (ServiceConstant.DOSSIER_SERVICE_ACTION.equalsIgnoreCase(intent.getAction().toString())) {

						boolean isFinished = intent.getBooleanExtra(ExecuteDossierAftersalesTask.SERVICE_STATE_KEY, false);
						if (isFinished) {

							loadData();
							hideWaitDialog();
						}else {
							showWaitDialog();
						}

					}
				}
			}*/

	private class MigrateDossierAsyncTask extends AsyncTask<Void, Void, Void> {
		private String languageBeforSetting;
		private Context mContext;
		private int totalCount = 0;
		private int currentCount = 0;
		private List<Order> orders;
		private boolean hasError = false;
		public MigrateDossierAsyncTask(String languageBeforSetting, Context mContext, List<Order> listOrders) {
			this.languageBeforSetting = languageBeforSetting;
			this.mContext = mContext;
			this.orders = listOrders;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			showWaitDialog();
			migratingDossier();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (hasError){
				DialogError dialogError = new DialogError(MyTicketsActivity.this,
						getResources().getString(R.string.general_server_unavailable), responseErrorMessage);
				dialogError.show();
			}else{
				DialogSuccessful dialog = new DialogSuccessful(MyTicketsActivity.this, MyTicketsActivity.this);
				dialog.show();
			}
		}

		public void migratingDossier() {

			DossierDetailDataService dossierDetailDataService = new DossierDetailDataService();
			for(int i = 0; i < orders.size(); i++){
				Log.d(TAG, "migrate orders size is..." + orders.size());
				Log.d(TAG, "migrate DNR one by one...");
				try {
					String dnr = orders.get(i).getDNR();
					String email = orders.get(i).getEmail();
					DossierDetailsResponse dossierResponse = dossierDetailDataService.executeDossierDetail(mContext, email, dnr, languageBeforSetting, true, false, true);
					if (dossierResponse == null) {
						Log.e(TAG, "migrate...DNR...ERROR");
						hasError = true;
					}else{
						Log.e(TAG, "migrate...DNR...DONE..." + dnr);
						AssistantDatabaseService assistantDatabaseService = new AssistantDatabaseService(mContext);
						assistantDatabaseService.deleteOrder(dnr);
					}

				} catch (Exception e) {
					Log.e(TAG, "Exception...");
					hasError = true;
					continue;
				}
			}
			hideWaitDialog();
		}
	}




	public static Intent createIntent(Context context) {
		Intent intent = new Intent(context, MyTicketsActivity.class);
		//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return intent;
	}
	
}
