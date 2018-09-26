package com.nmbs.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nmbs.R;

import com.nmbs.adapter.PDFAdapter;
import com.nmbs.adapter.TicketDetailStationAdapter;
import com.nmbs.adapter.TicketsDetailSeatlocationAdapter;

import com.nmbs.application.NMBSApplication;
import com.nmbs.async.RealTimeAsyncTask;
import com.nmbs.model.DossierAftersalesResponse;
import com.nmbs.model.HomePrintTicket;
import com.nmbs.model.Order;
import com.nmbs.model.Passenger;
import com.nmbs.model.StationInformationResult;
import com.nmbs.model.TariffDetail;
import com.nmbs.model.TravelSegment;
import com.nmbs.services.IAssistantService;
import com.nmbs.services.ISettingService;
import com.nmbs.services.impl.ServiceConstant;
import com.nmbs.util.FileManager;
import com.nmbs.util.LocaleChangedUtils;

/**
 * Activity used for displaying the UI element, user can do some inner behavior
 * with this.
 * 
 * User can select a category to see the details.
 */
public class TicketActivity extends BaseActivity {
	private final static String TAG = TicketActivity.class.getSimpleName();

	private IAssistantService assistantService;
	private ISettingService settingService;
	
	private ProgressDialog progressDialog;
	private Order order;

	private LinearLayoutForListView seatingInfoList, stationList;
	private Button qrCodeButton, pdfButton, exchangeButton, cancelButton;

	private TextView dnrNumberValue, pnrNumberValue, 
			 totalPriceValue, insurance, insuranceValue,
			tariffValue, tariffDetail, deliveryMethodValue, deliveryMethodInfo,
			deliveryMethodInfoDetail1, deliveryMethodInfoDetail2, title;
	private TicketsDetailSeatlocationAdapter ticketsDetailSeatlocationAdapter;
	private TicketDetailStationAdapter ticketDetailStationAdapter;
	private List<String> stationsCode = new ArrayList<String>();
	private int clickToCallScenarioIdIs4 = 4;
	private int clickToCallScenarioIdIs5 = 5;
	private View rooView;
	private List<HomePrintTicket> homePrintTickets;
	private TravelSegment travelSegment;
	private List<StationInformationResult> stations = new ArrayList<StationInformationResult>();
	private boolean isDisplayPdf;
	private int location;
	private DossierAftersalesResponse dossierAftersalesResponse;
	private ServiceStateReceiver serviceStateReceiver;
	private boolean isHasError;
	private LinearLayout progressBar;
	private LinearLayout errorLayout;
	private TextView errorTextview;
	@Override
	public void onCreate(Bundle savedInstanceState) {  
		super.onCreate(savedInstanceState);	
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		LocaleChangedUtils.initLanguageSettings(getApplicationContext());// Setting
																			// application
																			// language
		setContentView(R.layout.ticket_detail);
		Log.e(TAG, "onCreate");
		
		
		
		bindService();
		order = getIntentData(getIntent());
		Log.e(TAG, "onCreate/===" + order);
		if(savedInstanceState != null){
			location = savedInstanceState.getInt("location");	
		}
		ReadDnrAsyncTask readDnrAsyncTask = new ReadDnrAsyncTask();
		readDnrAsyncTask.execute((Void)null);
		/*if (NMBSApplication.isTestVersion) {
			showAlertDialog();
		}*/
		
		//reEnableState();
	}
	
	private void showAlertDialog(){
		
		final Builder builder = new Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setMessage("Exchangeable is::: " + order.getExchangeable() + "\n" 
							+ "Refundable is::: " + order.getRefundable() + "\n" 
							+ "RulfillmentFailed is::: " + order.getRulfillmentFailed() + "\n");
         
        builder.setPositiveButton(getString(R.string.general_ok), new DialogInterface.OnClickListener() {						
			public void onClick(DialogInterface dialog, int which) {		
				
			}
		});
         builder.show();
	}
	
	public class ReadDnrAsyncTask extends AsyncTask<Void, Void, Void>{
    	 	

    	@Override
    	protected void onPreExecute() {
        	
    		super.onPreExecute();
    	}
    	
		@Override
		protected Void doInBackground(Void... params) {
			try {
				if (order != null) {
					dossierAftersalesResponse = assistantService.getDossierAftersalesResponseFromFile(order.getDNR());
					if (dossierAftersalesResponse != null) {
						travelSegment = dossierAftersalesResponse.getTravelSegmentById(order.getTravelSegmentID());
						stationsCode = dossierAftersalesResponse.getStaionsCode(travelSegment);

						stations = dossierAftersalesResponse.getStations(stationsCode);
						
					}
					
					
					/*TicketAsyncTask ticketAsyncTask = new TicketAsyncTask();
					ticketAsyncTask.execute((Void) null);*/
				}
				
			} catch (Exception e) {
				
				
			}
			return null;
		}
    	

		
		@Override
		protected void onPostExecute(Void result) {
			bindAllViewElements();
			setViewStateBasedOnModel();
			super.onPostExecute(result);
		}

	
    }

	// bind Service instance
	private void bindService() {
		assistantService = ((NMBSApplication) getApplication()).getAssistantService();
		settingService = ((NMBSApplication) getApplication()).getSettingService();
		
	}

	// Bind all view elements
	private void bindAllViewElements() {
		seatingInfoList = (LinearLayoutForListView) findViewById(R.id.ticket_detail_view_seating_info_list);
		qrCodeButton = (Button) findViewById(R.id.ticket_detail_view_view_qr_code_btn);
		pdfButton = (Button) findViewById(R.id.ticket_detail_view_view_pdf_btn);

		dnrNumberValue = (TextView) findViewById(R.id.ticket_detail_view_booking_detail_reference_number_value);
		pnrNumberValue = (TextView) findViewById(R.id.ticket_detail_view_booking_detail_resevation_code_value);
		//paymentMethod = (TextView) findViewById(R.id.ticket_detail_view_booking_detail_payment_method_TextView);
		//paymentMethodValue = (TextView) findViewById(R.id.ticket_detail_view_booking_detail_payment_method_value);
		totalPriceValue = (TextView) findViewById(R.id.ticket_detail_view_booking_detail_total_price_value);
		insurance = (TextView) findViewById(R.id.ticket_detail_view_booking_detail_insurance_TextView);
		insuranceValue = (TextView) findViewById(R.id.ticket_detail_view_booking_detail_insurance_value);
		tariffValue = (TextView) findViewById(R.id.ticket_detail_view_booking_detail_tariff_value);

		tariffDetail = (TextView) findViewById(R.id.ticket_detail_view_tariff_details_TextView);

		deliveryMethodValue = (TextView) findViewById(R.id.ticket_detail_view_delivery_method_value);
		deliveryMethodInfo = (TextView) findViewById(R.id.ticket_detail_view_delivery_method_info_TextView);
		deliveryMethodInfoDetail1 = (TextView) findViewById(R.id.ticket_detail_view_delivery_method_info_detail1_TextView);
		deliveryMethodInfoDetail2 = (TextView) findViewById(R.id.ticket_detail_view_delivery_method_info_detail2_TextView);

		exchangeButton = (Button) findViewById(R.id.ticket_detail_view_view_exchange_btn);
		cancelButton = (Button) findViewById(R.id.ticket_detail_view_view_cancel_btn);

		title = (TextView) findViewById(R.id.ticket_detail_view_title);

		stationList = (LinearLayoutForListView) findViewById(R.id.ticket_detail_view_station_list);
		rooView = findViewById(R.id.ticket_detail_root_view);
		rooView.setVisibility(View.VISIBLE);
		progressBar = (LinearLayout)findViewById(R.id.tickets_detail_view_progressBarLayout);
		errorLayout = (LinearLayout) findViewById(R.id.tickets_detail_view_service_error_Layout);
		
		errorTextview = (TextView) findViewById(R.id.tickets_detail_view_retrieving_real_time_service_error_textview);
		
		runProgressBar();
		
		bindAllListeners();
	}
	
	private void runProgressBar(){
		if (!RealTimeAsyncTask.isRealTimeFinished) {
			//progressBar.setVisibility(View.VISIBLE);
			if (serviceStateReceiver == null) {			
				serviceStateReceiver = new ServiceStateReceiver();
				registerReceiver(serviceStateReceiver, new IntentFilter(ServiceConstant.REALTIME_SERVICE_ACTION));
			}	
			if (RealTimeAsyncTask.isRefreshingOneDnr) {
				if (RealTimeAsyncTask.REFRESHING_DNR != null 
						&& StringUtils.equalsIgnoreCase(RealTimeAsyncTask.REFRESHING_DNR, order.getDNR())){
					progressBar.setVisibility(View.VISIBLE);
				}
			}else {
				progressBar.setVisibility(View.VISIBLE);
			}			
		}
	}

	// set the base order text
	private void setViewStateBasedOnModel() {

		//Log.e(TAG, "order = " + order);
		if (order != null && dossierAftersalesResponse != null) {

			List<TravelSegment> travelSegments = dossierAftersalesResponse.getReservationForCurrentTravelSegment(travelSegment);
			if (travelSegments.size() == 0) {
				travelSegments.add(travelSegment);
			}
			if (travelSegments != null) {
				seatingInfoList.removeAllViews();
				ticketsDetailSeatlocationAdapter = new TicketsDetailSeatlocationAdapter(
						this.getApplicationContext(),
						R.layout.tickets_detail_seatlocation_adapter,
						travelSegments, dossierAftersalesResponse, assistantService, order);
				seatingInfoList.setAdapter(ticketsDetailSeatlocationAdapter);
			}
			// Log.e(TAG, "title = "+title);
			// Log.e(TAG,
			// "myState.dossierAftersalesResponse.getDnrId() = "+myState.dossierAftersalesResponse.getDnrId());
			title.setText(getString(R.string.ticket_detail_title) + " " + dossierAftersalesResponse.getDnrId());
			setDataForBooingDetails();
			setDataForStationInfo();
			setDataForDeliveryMethod();
			List<String> barcodes = dossierAftersalesResponse.getBarcodeOfCurrentTravelSegment(travelSegment);
			if (barcodes != null && barcodes.size() > 0) {
				qrCodeButton.setVisibility(View.VISIBLE);
			} else {
				qrCodeButton.setVisibility(View.GONE);
			}
			// Log.d(TAG,
			// "myState.dossierAftersalesResponse.getPdfUrl()::::::::::" +
			// myState.dossierAftersalesResponse.getPdfUrl());
			if (dossierAftersalesResponse.getHomePrintTickets() != null) {
				
				homePrintTickets = dossierAftersalesResponse.getHomePrintTicketsByTravelSegment(travelSegment);
				if (homePrintTickets != null && homePrintTickets.size() > 0) {
					pdfButton.setVisibility(View.VISIBLE);
				}else {
					pdfButton.setVisibility(View.GONE);
				}				
			} else {
				pdfButton.setVisibility(View.GONE);
			}

/*			if (myState.dossierAftersalesResponse
					.getExchangeStateForCurrentTravelSegment(travelSegment)) {
				exchangeButton.setVisibility(View.VISIBLE);
			} else {
				exchangeButton.setVisibility(View.GONE);
			}
			if (myState.dossierAftersalesResponse
					.getCancellationStateForCurrentTravelSegment(travelSegment)) {
				cancelButton.setVisibility(View.VISIBLE);
			} else {
				cancelButton.setVisibility(View.GONE);
			}*/
			// Log.e(TAG,
			// myState.dossierAftersalesResponse.getSelectedDeliveryMethod());
		}
	}
	
	
	
/*	private void realTimeMonitor(List<TravelSegment> travelSegments){
		if (isHasError) {
			errorLayout.setVisibility(View.VISIBLE);
		}else {
			errorLayout.setVisibility(View.GONE);
		}
		int errorCount = 0;
		for (TravelSegment travelSegment : travelSegments) {
			StationBoard stationBoard = assistantService.getParentRealTimeForTravelSegments(getApplicationContext(), travelSegment.getId());
			if (stationBoard != null) {
				if (!stationBoard.isCallSuccessFul()) {
					errorCount++;
					
				}
			}
		}
		if (errorCount > 0) {
			errorLayout.setVisibility(View.VISIBLE);
			if (errorCount > 1) {
				errorTextview.setText(getResources().getString(R.string.alert_bulkquery_partially_failed));
			}
		}else {
			errorLayout.setVisibility(View.GONE);
		}
		
		if (progressBar.getVisibility() == View.VISIBLE) {
			errorLayout.setVisibility(View.GONE);
		}
	}*/

	private void setDataForBooingDetails() {
		dnrNumberValue.setText(dossierAftersalesResponse.getDnrId());
		String pnrString = "";
		if (travelSegment.getPnrIds() != null
				&& travelSegment.getPnrIds().size() > 0) {
			for (int i = 0; i < travelSegment.getPnrIds().size(); i++) {

				if (i == travelSegment.getPnrIds().size() - 1) {
					pnrString += travelSegment.getPnrIds().get(i);
				} else {
					pnrString += travelSegment.getPnrIds().get(i) + ", ";
				}
			}
		}
		if (StringUtils.equalsIgnoreCase("", pnrString)) {
			pnrString = "/";
		}
		pnrNumberValue.setText(pnrString);
		//paymentMethod.setText(getString(R.string.general_payment_method) + ":");
		//paymentMethodValue.setText(myState.dossierAftersalesResponse
		//		.getSelectedPaymentMethod());
		
		if(dossierAftersalesResponse.getTotalDossierValue() != null){
			totalPriceValue.setText("€" + dossierAftersalesResponse.getTotalDossierValue()
					.getAmount());
		}

		insurance.setText(getString(R.string.general_insurance) + ":");

		if (dossierAftersalesResponse.isHasInsurance()) {
			/*
			 * int passengerCount = 0; if (travelSegment.getPassengerTariffs()
			 * != null) { passengerCount =
			 * travelSegment.getPassengerTariffs().size(); }
			 * 
			 * String passengerText = "";
			 * 
			 * if (passengerCount == 1) { passengerText = "1 " +
			 * getString(R.string.general_passenger); }else { passengerText =
			 * "2 " + getString(R.string.general_passengers); }
			 */
			insuranceValue.setText(R.string.general_yes);
		} else {
			insuranceValue.setText(R.string.general_no);
		}
		List<TariffDetail> tariffDetails = dossierAftersalesResponse
				.getTariffDetailsByTravelSegment(travelSegment);

		String tariffsDisplayText = dossierAftersalesResponse
				.getTariffsDisplayText(travelSegment, tariffDetails);
		tariffValue.setText(tariffsDisplayText);
	}

	private void setDataForStationInfo() {

		if (stations != null && stations.size() > 0) {
			ticketDetailStationAdapter = new TicketDetailStationAdapter(
					this.getApplicationContext(),
					R.layout.ticket_detail_station_adapter, stations);
			stationList.setAdapter(ticketDetailStationAdapter);
		}

	}

	private void setDataForDeliveryMethod() {

		List<Passenger> passengers = dossierAftersalesResponse
				.getPassengers(travelSegment);
		//Log.e(TAG,"myState.dossierAftersalesResponse.getSelectedDeliveryMethodLabel() ======== "+ myState.dossierAftersalesResponse.getSelectedDeliveryMethodLabel());
		deliveryMethodValue.setText(dossierAftersalesResponse
				.getSelectedDeliveryMethodLabel());

		if (StringUtils.equalsIgnoreCase(
				dossierAftersalesResponse.getSelectedDeliveryMethod(), "STAU")) {

			deliveryMethodInfo.setText(getString(R.string.ticket_detail_view_stationpickup_info));
			if (dossierAftersalesResponse != null) {
				deliveryMethodInfoDetail1.setText(dossierAftersalesResponse.getStationForPickup());
				
				deliveryMethodInfoDetail1.setTypeface(Typeface.DEFAULT_BOLD);
			}
			if (dossierAftersalesResponse.isPinCodeRequired()) {
				deliveryMethodInfoDetail2
						.setText(getString(R.string.ticket_detail_view_pin_needed_info));
			} else {
				deliveryMethodInfoDetail2
						.setText(getString(R.string.ticket_detail_view_pin_notneeded_info));
			}

		} else if (StringUtils.equalsIgnoreCase(
				
				dossierAftersalesResponse.getSelectedDeliveryMethod(), "TL")) {
			// deliveryMethodValue.setText(myState.dossierAftersalesResponse.getSelectedDeliveryMethodLabel());
			deliveryMethodInfo.setText(getString(R.string.ticket_detail_view_ticketless_info));
			deliveryMethodInfoDetail1.setTypeface(Typeface.DEFAULT_BOLD);
			deliveryMethodInfoDetail2.setTypeface(Typeface.DEFAULT_BOLD);
			if (passengers != null && passengers.size() > 0) {
				String ftpCardString = getFtpCardString(passengers, 0);
				String nameAndCardString = "";
				for (int i = 0; i < passengers.size(); i++) {
					nameAndCardString += passengers.get(i).getName() + " "
							+ ftpCardString + "\n";
				}
				deliveryMethodInfoDetail1.setText(nameAndCardString);
				deliveryMethodInfoDetail2.setVisibility(View.GONE);
				/*
				 * if (passengers.size() == 2) {
				 * deliveryMethodInfoDetail2.setVisibility(View.VISIBLE);
				 * ftpCardString = getFtpCardString(passengers, 1);
				 * deliveryMethodInfoDetail2.setText(passengers.get(1).getName()
				 * + " " + ftpCardString); }
				 */
			}

		} else if (StringUtils.equalsIgnoreCase(
				dossierAftersalesResponse.getSelectedDeliveryMethod(), "DH_AB")) {
			// deliveryMethodValue.setText(getString(R.string.delivery_view_type_print));
			deliveryMethodInfo
					.setText(getString(R.string.ticket_detail_view_print_info));
			deliveryMethodInfoDetail1.setTypeface(Typeface.DEFAULT_BOLD);
			deliveryMethodInfoDetail2.setTypeface(Typeface.DEFAULT_BOLD);
			if (passengers != null && passengers.size() > 0) {

				String name = "";
				for (int i = 0; i < passengers.size(); i++) {
					name += passengers.get(i).getName() + "\n";
				}
				deliveryMethodInfoDetail1.setText(name);
				deliveryMethodInfoDetail2.setVisibility(View.GONE);
				/*
				 * if (passengers.size() == 2) {
				 * deliveryMethodInfoDetail2.setVisibility(View.VISIBLE);
				 * 
				 * deliveryMethodInfoDetail2.setText(passengers.get(1).getName())
				 * ; }
				 */
			}
		} else {
			deliveryMethodInfo.setVisibility(View.GONE);
			deliveryMethodInfoDetail1.setVisibility(View.GONE);
			deliveryMethodInfoDetail2.setVisibility(View.GONE);
		}

	}

	private String getFtpCardString(List<Passenger> passengers,
			int passengerIndex) {
		String ftpCardString = "";
		for (int i = 0; i < passengers.get(passengerIndex).getFtpCards().size(); i++) {
			if (passengers.get(passengerIndex).getFtpCards().size() == 1) {
				ftpCardString = "(";
			}
			if (i == passengers.get(passengerIndex).getFtpCards().size() - 1) {
				ftpCardString += passengers.get(passengerIndex).getFtpCards()
						.get(i).getNumber()
						+ ") ";
			} else {
				ftpCardString += "("
						+ passengers.get(passengerIndex).getFtpCards().get(i)
								.getNumber() + ", ";
			}

		}
		return ftpCardString;
	}

	// Bind all listeners
	private void bindAllListeners() {
		qrCodeButton.setOnClickListener(qrCodeButtonOnClickListener);
		pdfButton.setOnClickListener(pdfButtonOnClickListener);
		tariffDetail.setOnClickListener(tariffDetailOnClickListener);
		exchangeButton.setOnClickListener(exchangeButtonOnClickListener);
		cancelButton.setOnClickListener(cancelButtonOnClickListener);
		stationList.setOnclickLinstener(stationListViewOnClickListener);
	}

	private OnClickListener stationListViewOnClickListener = new OnClickListener() {

		public void onClick(View v) { // Called when a view has been clicked.
			int position = v.getId();
			if (stations != null && stations.size() > 0) {
				// track Click link to Station information
				/*String action = TrackerConstant.ACTION_MYTICKETS_SELECTSTATIONINFO;
				TrackerService.getTrackerService().createEventTracker(
						TrackerConstant.MYTICKETS, action);*/
				startActivity(StationInfoActivity.createIntent(
						TicketActivity.this.getApplicationContext(),
						stations.get(position)));
			}
		}
	};

	private OnClickListener qrCodeButtonOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			
			/*TrackerService.getTrackerService().createEventTracker(
					TrackerConstant.MYTICKETS,
					TrackerConstant.ACTION_MYTICKETS_SELECTUSEQR);*/
			
			// Called when a view has been clicked.
			startActivity(BarcodeForPersonActivity.createIntent(
					TicketActivity.this.getApplicationContext(),
					dossierAftersalesResponse, travelSegment, order));
		}
	};

	private OnClickListener pdfButtonOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			/*TrackerService.getTrackerService().createEventTracker(
					TrackerConstant.MYTICKETS,
					TrackerConstant.ACTION_MYTICKETS_SELECTPDFTICKETS);*/
			// Called when a view has been clicked.
			openPDF();
		}
	};
	private OnClickListener tariffDetailOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			/*TrackerService.getTrackerService().createEventTracker(
					TrackerConstant.MYTICKETS,
					TrackerConstant.ACTION_MYTICKETS_SELECTTARIFFDETAIL);*/
			// Called when a view has been clicked.
			startActivity(TarrifActivity.createIntent(
					TicketActivity.this.getApplicationContext(), order,
					dossierAftersalesResponse, travelSegment));
		}
	};
	private OnClickListener exchangeButtonOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			/*TrackerService.getTrackerService().createEventTracker(
					TrackerConstant.MYTICKETS,
					TrackerConstant.ACTION_MYTICKETS_SELECTEXCHANGETICKETS);*/
			// Called when a view has been clicked.
			startActivity(MorePassengerActivity.createIntent(
					TicketActivity.this.getApplicationContext(),
					clickToCallScenarioIdIs4,
					dossierAftersalesResponse, order));
		}
	};
	private OnClickListener cancelButtonOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			/*TrackerService.getTrackerService().createEventTracker(
					TrackerConstant.MYTICKETS,
					TrackerConstant.ACTION_MYTICKETS_SELECTCANCELTICKETS);*/
			// Called when a view has been clicked.
			startActivity(MorePassengerActivity.createIntent(
					TicketActivity.this.getApplicationContext(),
					clickToCallScenarioIdIs5,
					dossierAftersalesResponse, order));
		}
	};

	private void openPDF() {

		if (dossierAftersalesResponse != null
				&& dossierAftersalesResponse.getHomePrintTickets() != null) {
			homePrintTickets = dossierAftersalesResponse.getHomePrintTicketsByTravelSegment(travelSegment);

			if (homePrintTickets != null) {
				if (homePrintTickets.size() == 1) {
					String fileName = dossierAftersalesResponse.getDnrId()
							+ "-"
							+ homePrintTickets.get(0).getPdfId() + ".pdf";
					File file = FileManager.getInstance().getExternalStoragePrivateFile(this,
									dossierAftersalesResponse.getDnrId(), fileName);
					if (file.exists()) {
						showWaitDialog();
						isDisplayPdf = true;
						assistantService.openPDF(dossierAftersalesResponse.getDnrId(), homePrintTickets.get(0).getPdfId());
					} else {
						Toast.makeText(
								TicketActivity.this,
								TicketActivity.this
										.getString(R.string.alert_ticket_detail_pdf_not_downloaded),
								Toast.LENGTH_LONG).show();
					}
				} else {
					if (order != null && order.isCorrupted()) {
						Toast.makeText(
								TicketActivity.this,
								TicketActivity.this
										.getString(R.string.alert_ticket_detail_notall_pdf_downloaded),
								Toast.LENGTH_LONG).show();
					}
					showDialog(1);
				}
			}
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;

		Builder builder = new Builder(TicketActivity.this);

		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(getString(R.string.ticket_detail_pdf_files));

		switch (id) {
		case 1:

			String DNR = "";
			if (dossierAftersalesResponse != null) {

				DNR = dossierAftersalesResponse.getDnrId();
			}

			PDFAdapter pdfAdapter = new PDFAdapter(getApplicationContext(),
					R.layout.alert_dialog_data_adapter, homePrintTickets, DNR);
			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialogInterface, int which) {
					location = which;
					if (homePrintTickets != null && homePrintTickets.size() > 0) {
						assistantService.openPDF(
								dossierAftersalesResponse.getDnrId(),								
								homePrintTickets.get(location).getPdfId());
					}
				}
			};
			builder.setAdapter(pdfAdapter, listener);
			dialog = builder.create();
			break;
		}
		return dialog;
	}

	/**
	 * Utility method for every Activity who needs to start this Activity. *
	 * This keeps the construction of the Intent and the corresponding
	 * parameters also in this class.
	 * 
	 * @param context
	 * @param order
	 */
	public static Intent createIntent(Context context, Order order) {
		Intent intent = new Intent(context, TicketActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(TarrifActivity.TICKET_CONTIANS_ORDER_SERIALIZABLE_KEY,
				order);
		return intent;
	}

	// Get the intent data by special SerializableKey
	private Order getIntentData(Intent intent) {
		return intent != null ? (Order) intent
				.getSerializableExtra(TarrifActivity.TICKET_CONTIANS_ORDER_SERIALIZABLE_KEY)
				: null;
	}

	@Override
	protected void onPause() {
		//Log.d(TAG, "onPause");
		super.onPause();
	}

	@Override
	protected void onResume() {
		// Log.d(TAG, "onResume");
		super.onResume();
		if (isDisplayPdf) {
			hideWaitDialog();
			isDisplayPdf = false;
			//if (myState != null) {
				assistantService.deleteUndecryptPdfFile(dossierAftersalesResponse, homePrintTickets.get(location));				
			//}
		}
	}

	// show progressDialog.

	private void showWaitDialog() {

		if (progressDialog == null) {
			progressDialog = ProgressDialog.show(TicketActivity.this,
					getString(R.string.alert_loading),
					getString(R.string.alert_waiting), true);
		}
	}

	// hide progressDialog
	private void hideWaitDialog() {

		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	/**
	 * It's a Exception class, used for receiving exception.
	 */
	private class ExceptionReceiver {
		private Exception excp;
	}

	private class TicketAsyncTask extends AsyncTask<Void, Void, Void> {

		private ExceptionReceiver exceptionReceiver;

		@Override
		protected void onPostExecute(Void result) {
			hideWaitDialog();
			if (exceptionReceiver != null && exceptionReceiver.excp != null) {
				Toast.makeText(
						TicketActivity.this,
						TicketActivity.this
								.getString(R.string.general_server_unavailable),
						Toast.LENGTH_SHORT).show();
			} else {
				bindAllViewElements();
				setViewStateBasedOnModel();
			}
		}

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}

		private void loadData() {

			/*
			 * List<DeliveryMethod> deliveryMethods =
			 * masterService.loadDeliveryMethodsCollectionByDeliveryMethodName
			 * (myState.dossierAftersalesResponse.getSelectedDeliveryMethod());
			 * if (deliveryMethods != null && deliveryMethods.size() > 0) {
			 * selectDeliveryMethod = deliveryMethods.get(0); }
			 */

			travelSegment = dossierAftersalesResponse.getTravelSegmentById(order.getTravelSegmentID());
			stationsCode = dossierAftersalesResponse.getStaionsCode(travelSegment);

			stations = dossierAftersalesResponse.getStations(stationsCode);

			// stationForPickup =
			// masterService.loadStationByStationCode(myState.dossierAftersalesResponse.getStationForPickup());

		}

		@Override
		protected Void doInBackground(Void... params) {

			try {

				loadData();

			} catch (Exception e) {
				e.printStackTrace();
				exceptionReceiver = new ExceptionReceiver();
				exceptionReceiver.excp = e;
				//Log.e(TAG, "ExceptionReceiver when searchOrders.", e);
				throw new RuntimeException();
			}
			return null;
		}
	}
	
	public void onSaveInstanceState(Bundle outState) {
    	
    	outState.putInt("location", location);
    	//outState.putSerializable("OfferService", offerService.getOfferResponse());
    	super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onDestroy() {

		if(serviceStateReceiver != null){
			unregisterReceiver(serviceStateReceiver);
		}
		super.onDestroy();
	}
	
	class ServiceStateReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "onReceive got REALTIME_SERVICE_ACTION broadcast");
			if (ServiceConstant.REALTIME_SERVICE_ACTION.equalsIgnoreCase(intent.getAction().toString())) {
				Log.d(TAG, "onReceive got ERROR_COUNT is::::" + isHasError);
				isHasError = intent.getBooleanExtra(RealTimeAsyncTask.ERROR_COUNT, false);
				progressBar.setVisibility(View.GONE);
				setViewStateBasedOnModel();
			}
		}
	}

}
