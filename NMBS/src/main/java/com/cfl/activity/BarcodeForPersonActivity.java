package com.cfl.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.cfl.R;
import com.cfl.adapter.PDFAdapter;
import com.cfl.application.NMBSApplication;
import com.cfl.dataaccess.database.AssistantDatabaseService;
import com.cfl.model.DossierAftersalesResponse;
import com.cfl.model.HomePrintTicket;
import com.cfl.model.Order;
import com.cfl.model.TravelSegment;
import com.cfl.services.IAssistantService;
import com.cfl.util.FileManager;
import com.cfl.util.LocaleChangedUtils;

/**
 * Activity used for displaying the UI element, user can do some interbehavior
 * with this.
 * 
 * @author: David
 */
public class BarcodeForPersonActivity extends BaseActivity implements
		OnGestureListener {

	private TouchImageView barcodeImageView;
	private static final String DOSSIER_AFTERSALES_RESPONSE_SERIALIZABLE_KEY = "Dossier_Aftersales_Response_key";
	private static final String TRAVEL_SEGMENT_SERIALIZABLE_KEY = "TravelSegment_key";
	private static final String ORDER_SERIALIZABLE_KEY = "Order_key";

	public static final String PASSENGERS_SERIALIZABLE_KEY = "passengers_key";
	private TextView titleTextView, notDownload;
	private Button pdfBtn;
	private DossierAftersalesResponse dossierAftersalesResponse;
	private TravelSegment travelSegment;
	private ViewFlipper flipper;
	Map<String, List<String>> passengerAndBarcod;
	private GestureDetector detector;
	private LinearLayout dotLayout;
	private Iterator<Entry<String, List<String>>> iter;
	private IAssistantService assistantService;
	private Order order;
	private ProgressDialog progressDialog;
	private boolean isDisplayPdf;
	private String pdfFileName;
	private int location;
	private int screenBrightness;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		LocaleChangedUtils.initLanguageSettings(getApplicationContext());// Setting
																			// application
																			// language
		setContentView(R.layout.barcodes_view);
		dossierAftersalesResponse = getIntentDataOfDossierAftersalesResponse(getIntent());
		travelSegment = getIntentDataOfTravelSegment(getIntent());
		order = getIntentDataOfOrder(getIntent());
		assistantService = ((NMBSApplication) getApplication()).getAssistantService();
		if(savedInstanceState != null){
			location = savedInstanceState.getInt("location");	
		}
		bindAllViewsElement();

		setViewStateBasedOnModel();

	}

	// Bind all view elements
	private void bindAllViewsElement(){
		titleTextView = (TextView) findViewById(R.id.barcode_detail_view_title);

		if (dossierAftersalesResponse != null) {
			titleTextView.setText(getString(R.string.ticket_detail_title) + " " + dossierAftersalesResponse.getDnrId());
			passengerAndBarcod = dossierAftersalesResponse.getPassengerAndBarcodOfCurrentTravelSegment(travelSegment);
		}

		dotLayout = (LinearLayout) findViewById(R.id.barcode_for_person_view_dot_LinearLayout);
		detector = new GestureDetector(this);
		flipper = (ViewFlipper) this.findViewById(R.id.ViewFlipper01);
		addDotToLinearLayout();
		setDotState();
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		iter = passengerAndBarcod.entrySet().iterator();

		while (iter.hasNext()) {

			Entry<String, List<String>> entry = iter.next();
			if (entry.getValue() != null) {

				for (int i = 0; i < entry.getValue().size(); i++) {
					View view = layoutInflater.inflate(R.layout.barcode_for_person_view, null);
					barcodeImageView = (TouchImageView) view.findViewById(R.id.barcode_for_person_view_image);
					notDownload = (TextView) view.findViewById(R.id.barcode_for_person_view_not_download);

					 Bitmap mBitmap = null;
					try {
						BitmapFactory.Options options = new BitmapFactory.Options();
						//options.inTempStorage = new byte[16 * 1024];
						options.inSampleSize = 2;
						mBitmap = BitmapFactory.decodeFile(FileManager.getInstance().getExternalStoragePrivateFilePath(this, dossierAftersalesResponse.getDnrId(), entry.getValue().get(i)), options);
						//mBitmap = assistantService.getStreamFromEncryptFile(dossierAftersalesResponse.getDnrId(), entry.getValue().get(i));
					} catch (Exception e) {
						Toast.makeText(
								BarcodeForPersonActivity.this,
								BarcodeForPersonActivity.this
										.getString(R.string.barcode_view_not_download),
								Toast.LENGTH_LONG).show();
					}
					 if (mBitmap != null) {
						 barcodeImageView.setImageBitmap(mBitmap);
						 barcodeImageView.setMaxZoom(8f);
						 barcodeImageView.setVisibility(View.VISIBLE);
						 notDownload.setVisibility(View.INVISIBLE);
					 }else {

						Toast.makeText(
								BarcodeForPersonActivity.this,
								BarcodeForPersonActivity.this
										.getString(R.string.barcode_view_not_download),
								Toast.LENGTH_LONG).show();
						barcodeImageView.setVisibility(View.INVISIBLE);
						notDownload.setVisibility(View.VISIBLE);
					 }
					pdfBtn = (Button) view.findViewById(R.id.barcode_for_person_view_pdf_btn);
					if (dossierAftersalesResponse.getHomePrintTickets() != null
							&& dossierAftersalesResponse.getHomePrintTickets().size() > 0) {
						pdfBtn.setVisibility(View.VISIBLE);
					} else {
						pdfBtn.setVisibility(View.INVISIBLE);
					}
					TextView textView = (TextView) view.findViewById(R.id.barcode_for_person_text);

					textView.setText(getString(R.string.barcode_view_qrcode_of) + " " + entry.getKey());
					TextView seeDetailTextView = (TextView) view.findViewById(R.id.barcode_for_person_view_see_details);

					if (dotLayout.getChildCount() == 2) {
						seeDetailTextView.setText(getString(R.string.barcode_view_swipefor_other_passenger));
					} else if (dotLayout.getChildCount() > 2) {
						seeDetailTextView.setText(getString(R.string.barcode_view_swipefor_other_passengers));
					}
					flipper.addView(view);
					bindAllListeners(entry.getValue().get(i));
				}
			}
		} 
	}
	

	// Bind all view Listeners
	private void bindAllListeners(final String barCode) {
		pdfBtn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				openPDF(barCode);
			}
		});
	}

	private void openPDF(String barCode) {
		
		if (dossierAftersalesResponse != null
				&& dossierAftersalesResponse.getHomePrintTickets() != null) {
			List<HomePrintTicket> homePrintTickets = dossierAftersalesResponse
					.getHomePrintTicketsByBarcode(barCode);
			if (homePrintTickets != null) {
				if (homePrintTickets.size() == 1) {
					// String fileName = "";
					// fileName = dossierAftersalesResponse.getDnrId() + "-" +
					// homePrintTickets.get(0).getPdfId() + ".pdf";
					// boolean has =
					// FileManager.getInstance().hasExternalStoragePrivateFile(this.getApplicationContext(),
					// dossierAftersalesResponse.getDnrId(), fileName);
					// if (has) {
					this.pdfFileName = dossierAftersalesResponse.getDnrId() + "-" + homePrintTickets.get(0).getPdfId() + "-" +"decrypt"+ ".pdf";
					String fileName = dossierAftersalesResponse.getDnrId() + "-" + homePrintTickets.get(0).getPdfId() + ".pdf";	
					File file = FileManager.getInstance().getExternalStoragePrivateFile(this, dossierAftersalesResponse.getDnrId(), fileName);
				    if(file.exists()){
				    	showWaitDialog();
						isDisplayPdf = true;
					assistantService.openPDF(
							dossierAftersalesResponse.getDnrId(),
							homePrintTickets.get(0).getPdfId());
				    }else{
				    	Toast.makeText(
								this,
								this.getString(R.string.alert_ticket_detail_pdf_not_downloaded),
								Toast.LENGTH_LONG).show();
				    }
				} else {
					if (order != null && order.isCorrupted()) {
						Toast.makeText(
								BarcodeForPersonActivity.this,
								BarcodeForPersonActivity.this
										.getString(R.string.alert_ticket_detail_notall_pdf_downloaded),
								Toast.LENGTH_LONG).show();
					}
					showDialog(1);
				}
			}
		}
	}

	
	@Override
	protected void onResume() {
		//Log.d(TAG, "onResume");
		super.onResume();
		if(isDisplayPdf){
			hideWaitDialog();
			isDisplayPdf = false;
			FileManager.getInstance().deleteExternalStoragePrivateFile(this, dossierAftersalesResponse.getDnrId(), pdfFileName);
		}
		screenBrightness = getScreenBrightness();
		setScreenBrightness(255);
	}
	
	
	@Override
	protected void onStop() {
		setScreenBrightness(screenBrightness);
		super.onStop();
	}

	private int getScreenBrightness(){  
        int screenBrightness=255;  
        try{  
            screenBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);  
        }  
        catch (Exception localException){  
            
        }  
        return screenBrightness;  
      }  
	
    private void setScreenBrightness(int paramInt){  
        Window localWindow = getWindow();  
        WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();  
        float f = paramInt / 255.0F;  
        localLayoutParams.screenBrightness = f;  
        localWindow.setAttributes(localLayoutParams);  
      }  
	// show progressDialog.

	private void showWaitDialog() {

		if (progressDialog == null) {
			progressDialog = ProgressDialog.show(this,
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
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;

		Builder builder = new Builder(BarcodeForPersonActivity.this);

		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(getString(R.string.ticket_detail_pdf_files));

		switch (id) {
		case 1:
			List<HomePrintTicket> homePrintTickets = new ArrayList<HomePrintTicket>();
			String DNR = "";
			if (dossierAftersalesResponse != null) {
				homePrintTickets = dossierAftersalesResponse.getHomePrintTickets();
				DNR = dossierAftersalesResponse.getDnrId();
			}

			PDFAdapter pdfAdapter = new PDFAdapter(getApplicationContext(),
					R.layout.alert_dialog_data_adapter, homePrintTickets, DNR);
			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialogInterface, int which) {
					location = which;
					if (dossierAftersalesResponse != null
							&& dossierAftersalesResponse.getHomePrintTickets().size() > 0) {
						assistantService.openPDF(
								dossierAftersalesResponse.getDnrId(),
								dossierAftersalesResponse.getHomePrintTickets().get(location).getPdfId());
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
	 * Utility method for every Activity who needs to start this Activity.
	 * 
	 * @param context
	 * @param
	 * @param order
	 */
	public static Intent createIntent(Context context,
			DossierAftersalesResponse dossierAftersalesResponse,
			TravelSegment travelSegment, Order order) {
		Intent intent = new Intent(context, BarcodeForPersonActivity.class);
		intent.putExtra(DOSSIER_AFTERSALES_RESPONSE_SERIALIZABLE_KEY, dossierAftersalesResponse);
		intent.putExtra(TRAVEL_SEGMENT_SERIALIZABLE_KEY, travelSegment);
		intent.putExtra(ORDER_SERIALIZABLE_KEY, order);
		return intent;
	}

	// Get the intent data by special SerializableKey
	private DossierAftersalesResponse getIntentDataOfDossierAftersalesResponse(
			Intent intent) {
		return intent != null ? (DossierAftersalesResponse) intent
				.getSerializableExtra(DOSSIER_AFTERSALES_RESPONSE_SERIALIZABLE_KEY)
				: null;
	}

	// Get the intent data by special SerializableKey
	private TravelSegment getIntentDataOfTravelSegment(Intent intent) {
		return intent != null ? (TravelSegment) intent
				.getSerializableExtra(TRAVEL_SEGMENT_SERIALIZABLE_KEY) : null;
	}

	// Get the intent data by special SerializableKey
	private Order getIntentDataOfOrder(Intent intent) {
		return intent != null ? (Order) intent.getSerializableExtra(ORDER_SERIALIZABLE_KEY) : null;
	}

	// Set the base text
	private void setViewStateBasedOnModel() {

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return this.detector.onTouchEvent(event);
	}

	public boolean onDown(MotionEvent e) {

		return false;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (e1.getX() - e2.getX() > 120) {
			this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_left_in));
			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_left_out));

			if (this.flipper.getDisplayedChild() + 1 != this.flipper
					.getChildCount()) {

				this.flipper.showNext();
			}
			setDotState();

			return true;
		} else if (e1.getX() - e2.getX() < -120) {
			this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_right_in));
			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_right_out));
			if (this.flipper.getDisplayedChild() != 0) {
				this.flipper.showPrevious();
			}
			setDotState();

			return true;
		}
		return false;
	}

	private void addDotToLinearLayout() {
		iter = passengerAndBarcod.entrySet().iterator();
		while (iter.hasNext()) {

			Entry<String, List<String>> entry = iter.next();
			if (entry.getValue() != null) {

				for (int j = 0; j < entry.getValue().size(); j++) {

					TextView textView1 = new TextView(this);
					textView1.setText(getString(R.string.group_list_item_tag_middle_dot));

					textView1.setTextSize(16);
					if (j == 0) {
						textView1.setTextColor(Color.GRAY);
					} else {
						textView1.setTextColor(Color.BLACK);
					}

					textView1.setLayoutParams(new LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));

					textView1.setGravity(Gravity.CENTER_HORIZONTAL);
					dotLayout.addView(textView1);
				}
			}
		}

	}

	private void setDotState() {
		for (int i = 0; i < dotLayout.getChildCount(); i++) {
			TextView textView = (TextView) dotLayout.getChildAt(i);
			if (i == this.flipper.getDisplayedChild()) {
				textView.setTextColor(Color.GRAY);
			} else {
				textView.setTextColor(Color.BLACK);
			}
		}
	}

	public void onLongPress(MotionEvent e) {

	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {

		return false;
	}

	public void onShowPress(MotionEvent e) {

	}

	public boolean onSingleTapUp(MotionEvent e) {

		return false;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		this.detector.onTouchEvent(ev);

		return super.dispatchTouchEvent(ev);
	}

	public class AsyncLoadBarcodes extends AsyncTask<String, Void, Void> {
		private TouchImageView barcodeImageView;
		private TextView notDownload;
		
		public AsyncLoadBarcodes(TouchImageView barcodeImageView,
				TextView notDownload) {
			super();
			this.barcodeImageView = barcodeImageView;
			this.notDownload = notDownload;
		}

		Bitmap mBitmap = null;
		String barcodesId;

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			AssistantDatabaseService databaseService = new AssistantDatabaseService(
					BarcodeForPersonActivity.this);
			barcodesId = dossierAftersalesResponse.getDnrId() + "-" + params[0];
			Log.i("TAG", databaseService.readBarCodes(barcodesId).toString()+"");
			mBitmap = BitmapFactory.decodeByteArray(
					databaseService.readBarCodes(barcodesId), 0,
					databaseService.readBarCodes(barcodesId).length);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (mBitmap != null) {

				barcodeImageView.setImageBitmap(mBitmap);
				barcodeImageView.setMaxZoom(8f);
				barcodeImageView.setVisibility(View.VISIBLE);
				notDownload.setVisibility(View.INVISIBLE);
			} else {
				barcodeImageView.setVisibility(View.INVISIBLE);
				notDownload.setVisibility(View.VISIBLE);
			}
			super.onPostExecute(result);
		}

	}
	public void onSaveInstanceState(Bundle outState) {
    	
    	outState.putInt("location", location);
    	//outState.putSerializable("OfferService", offerService.getOfferResponse());
    	super.onSaveInstanceState(outState);
	}
}
