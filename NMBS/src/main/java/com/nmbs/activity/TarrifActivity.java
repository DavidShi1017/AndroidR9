package com.nmbs.activity;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.nmbs.R;
import com.nmbs.adapter.TarrifAdapter;
import com.nmbs.model.DossierAftersalesResponse;
import com.nmbs.model.Order;
import com.nmbs.model.TariffDetail;
import com.nmbs.model.TravelSegment;
import com.nmbs.util.LocaleChangedUtils;

/**
 * Activity used for displaying the UI element, user can do some inner behavior
 * with this.
 * 
 * User can select a category to see the details.
 */
public class TarrifActivity extends BaseActivity implements OnGestureListener{
	//private final static String TAG = TarrifActivity.class.getSimpleName();

	
	
	private DossierAftersalesResponse dResponse;
	
	private TarrifAdapter tarrifAdapter;
	public static final String TICKET_CONTIANS_ORDER_SERIALIZABLE_KEY = "ticket_contians_order_key";
	public static final String DOSSIER_AFTERSALES_RESPONSE_SERIALIZABLE_KEY = "dossier_aftersales_response_key";
	public static final String TRAVELSEGMENT_KEY = "travelSegment_key";
	private ViewFlipper flipper;
	private TextView titleTextView;
	private GestureDetector detector;
	private LinearLayout dotLayout;
	private List <TariffDetail> tariffDetails;
	private TravelSegment travelSegment;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		LocaleChangedUtils.initLanguageSettings(getApplicationContext());// Setting application language
		setContentView(R.layout.tarrif_view_with_viewflipper);
		//Log.e(TAG, "onCreate");
		 
		
		dResponse = getIntentDataOfResponse(getIntent());
		
		travelSegment = getIntentDataOfTravelSegment(getIntent());
		bindAllViewElements();
		setViewStateBasedOnModel();		
		
		bindAdapter();
	}

	// Bind all view elements
	private void bindAllViewElements() {
		
		
		titleTextView = (TextView)findViewById(R.id.tarrif_view_title_TextView);
		dotLayout = (LinearLayout)findViewById(R.id.tarrif_view_dot_LinearLayout);
		
		detector = new GestureDetector(this);
		flipper = (ViewFlipper) this.findViewById(R.id.ViewFlipper01);
		
		
	}
	
	private void bindAdapter() {
		if (dResponse != null) {
			
		}
	}
	
	//set the base text of view
	private void setViewStateBasedOnModel(){
		LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (dResponse != null) {
			titleTextView.setText(getString(R.string.ticket_detail_title) + " " + dResponse.getDnrId());
			tariffDetails = dResponse.getTariffDetailsByTravelSegment(travelSegment);	
			
		}

		addDotToLinearLayout();
		if (tariffDetails != null) {
			for (int i = 0; i < tariffDetails.size(); i++) {
				View view = layoutInflater.inflate(R.layout.tarrif_view, null);
				
				TextView textView = (TextView)view.findViewById(R.id.tarrif_view_conditions_info_textview);
				textView.setText(tariffDetails.get(i).getDisplayText());
				
				
				LinearLayoutForListView layoutForListView = (LinearLayoutForListView)view.findViewById(R.id.tarrif_view_tarrif_detail_list);
				tarrifAdapter = new TarrifAdapter(TarrifActivity.this.getApplicationContext(), R.layout.tarrif_adapter, 
						tariffDetails.get(i).getInfoTexts());
				
				layoutForListView.setAdapter(tarrifAdapter);
				layoutForListView.setClickable(false);
				layoutForListView.setFocusable(false);
				layoutForListView.setFocusableInTouchMode(false);
				
				flipper.addView(view);
				//layoutForListView.removeAllViews();
			}
		}
	}
	

	
	//Get the intent data by special SerializableKey
	private DossierAftersalesResponse getIntentDataOfResponse(Intent intent){
		return intent != null ? (DossierAftersalesResponse) intent.getSerializableExtra(DOSSIER_AFTERSALES_RESPONSE_SERIALIZABLE_KEY) : null;
	}
	private TravelSegment getIntentDataOfTravelSegment(Intent intent){
		return intent != null ? (TravelSegment) intent.getSerializableExtra(TRAVELSEGMENT_KEY) : null;
	}
	/**
	 * Utility method for every Activity who needs to start this Activity.	  
	 * This keeps the construction of the Intent and the corresponding parameters also in this class.
	 * 
	 * @param context
	 * @param dAftersalesResponse DossierAftersalesResponse
	 */
	public static Intent createIntent(Context context, Order order, DossierAftersalesResponse dAftersalesResponse, TravelSegment travelSegment) {
		Intent intent = new Intent(context, TarrifActivity.class);
		intent.putExtra(DOSSIER_AFTERSALES_RESPONSE_SERIALIZABLE_KEY, dAftersalesResponse);
		intent.putExtra(TICKET_CONTIANS_ORDER_SERIALIZABLE_KEY, order);
		intent.putExtra(TRAVELSEGMENT_KEY, travelSegment);
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
	public boolean onTouchEvent(MotionEvent event) {
		return this.detector.onTouchEvent(event);
	}
	
	public boolean onDown(MotionEvent e) {
		
		return false;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (e1.getX() - e2.getX() > 120) {
			this.flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));		
			
			if (this.flipper.getDisplayedChild() + 1 != this.flipper.getChildCount()) {
				
				this.flipper.showNext();
			}
			setDotState();
			
			return true;
		} else if (e1.getX() - e2.getX() < -120) {
			this.flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
			if (this.flipper.getDisplayedChild() != 0) {
				this.flipper.showPrevious();
			}
			setDotState();
			
			return true;
		}
		return false;
	}
	
	private void addDotToLinearLayout(){
		if (tariffDetails != null) {
			for (int j = 0; j < tariffDetails.size(); j++) {
				TextView textView1 = new TextView(this);
				textView1.setText("●");
				
				textView1.setTextSize(16);
				if (j == 0) {
					textView1.setTextColor(Color.WHITE);
				}else {
					textView1.setTextColor(Color.GRAY);
				}
				
				textView1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				
				textView1.setGravity(Gravity.CENTER_HORIZONTAL);
				dotLayout.addView(textView1);
			}
		}		
	}
	private void setDotState(){
		for (int i = 0; i < dotLayout.getChildCount(); i++) {
			TextView textView = (TextView)dotLayout.getChildAt(i);
			if (i == this.flipper.getDisplayedChild()) {
				textView.setTextColor(Color.WHITE);				
			}else {
				textView.setTextColor(Color.GRAY);
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
    public boolean dispatchTouchEvent(MotionEvent ev){
    		this.detector.onTouchEvent(ev);
            
            return super.dispatchTouchEvent(ev);
    }
}
