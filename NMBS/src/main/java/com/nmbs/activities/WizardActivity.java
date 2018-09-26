package com.nmbs.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nmbs.R;
import com.nmbs.activity.BaseActivity;
import com.nmbs.adapter.PagerViewAdapter;
import com.nmbs.application.NMBSApplication;
import com.nmbs.async.AsyncImageLoader;
import com.nmbs.async.AsyncImageLoader.ImageCallback;
import com.nmbs.model.MobileMessage;
import com.nmbs.model.MobileMessageResponse;
import com.nmbs.model.WizardItem;
import com.nmbs.model.WizardResponse;
import com.nmbs.services.IMasterService;
import com.nmbs.services.IMessageService;
import com.nmbs.services.impl.SettingService;
import com.nmbs.util.GoogleAnalyticsUtil;
import com.nmbs.util.ImageUtil;
import com.nmbs.util.TrackerConstant;
import com.nmbs.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class WizardActivity extends BaseActivity implements OnGestureListener {

	private GestureDetector detector;
	//private ViewFlipper flipper;
	private LinearLayout dotLayout, llTemp;
	private List<WizardItem> wizardItems;
	private static final String Intent_Key_WhichContext = "WhichContext";
	public static final String Wizard_Home = "Home";
	public static final String Wizard_MyTickets = "MyTickets";
	private static final String TAG = WizardActivity.class.getSimpleName();
	private IMasterService masterService;
	private SettingService settingService = null;
	private WizardResponse wizardResponse;
	private List<MobileMessage> showMessages = null;
	private IMessageService messageService;
	private MobileMessageResponse messageResponse;
	private String whichContext;
	private List<View> viewList;
	private PagerViewAdapter pagerViewAdapter;
	private ViewPager viewPager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Utils.setToolBarStyle(this);
		settingService = ((NMBSApplication) getApplication()).getSettingService();
		//settingService.initLanguageSettings();
		messageService = ((NMBSApplication) getApplication()).getMessageService();
		setContentView(R.layout.wizard_group_view);
		masterService = ((NMBSApplication) getApplication()).getMasterService();
		whichContext = getIntent().getStringExtra(Intent_Key_WhichContext);
		wizardResponse = masterService.loadWizardResponse(whichContext, settingService.getCurrentLanguagesKey());
		viewList = new ArrayList<View>();
		bindAllViewsElement();
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				setDotState();
			}

			@Override
			public void onPageSelected(int position) {

			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
	}

	// Bind all view elements
	private void bindAllViewsElement() {

		dotLayout = (LinearLayout) findViewById(R.id.wizard_group_view_dot_LinearLayout);
		llTemp = (LinearLayout) findViewById(R.id.ll_temp);

		//closeBtn = (ImageView) findViewById(R.id.message_wizard_group_view_close_btn);
		detector = new GestureDetector(this);
		//flipper = (ViewFlipper) this.findViewById(R.id.ViewFlipper01);
		this.viewPager = (ViewPager)findViewById(R.id.vf_wizard_pager);
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if(Wizard_Home.equalsIgnoreCase(whichContext)){
			bindMessage(layoutInflater);
		}
		bindWizard(layoutInflater);
		if(viewList != null){
			//Log.d(TAG, "viewList===" + viewList.size());
			this.pagerViewAdapter = new PagerViewAdapter(viewList);
			viewPager.setAdapter(pagerViewAdapter);
		}
		addDotToLinearLayoutMessage();
		addDotToLinearLayoutWizard();
		setDotState();
	}

	private void bindMessage(LayoutInflater layoutInflater){

		if(messageService.getMessageResponse() != null){
			messageResponse = messageService.getMessageResponse();
		}

		if (messageResponse != null){
			showMessages = messageService.getShowMessage(messageResponse.getMobileMessages());
			//showMessages = messageResponse.getMobileMessages();
		}

		if (showMessages != null) {
			//mobileMessages = messageResponse.getMobileMessages();
			llTemp.setBackgroundColor(getResources().getColor(R.color.background_secondaryaction));
			for (int i = 0; i < showMessages.size(); i++) {
				final MobileMessage message = showMessages.get(i);
				View convertView = layoutInflater.inflate(R.layout.activity_messages_wizard, null);
				ImageView btnClose = (ImageView) convertView.findViewById(R.id.btn_close);
				ImageView ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
				bindAllListeners(btnClose);
				TextView tvMessageTitle = (TextView) convertView.findViewById(R.id.tv_message_title);
				TextView tvMessageDescription = (TextView) convertView.findViewById(R.id.tv_message_description);
				TextView tvMessageValidity = (TextView) convertView.findViewById(R.id.tv_validity);
				Button btnMessage = (Button) convertView.findViewById(R.id.btn_message);
				if(message != null){
					if (message.isIncludeActionButton()){
						btnMessage.setText(showMessages.get(i).getActionButtonText());
					}else{
						btnMessage.setVisibility(View.GONE);
					}
					tvMessageTitle.setText(message.getTitle());
					tvMessageDescription.setText(message.getDescription());
					tvMessageValidity.setText(message.getValidity());
					ImageView descImageView = (ImageView) convertView.findViewById(R.id.iv_message_image);

					if (message.getImage(Utils.getDeviceDensity(this)).trim().equals("") || message.getImage(Utils.getDeviceDensity(this)) == null) {
						descImageView.setVisibility(View.INVISIBLE);
					} else {
						descImageView.setVisibility(View.VISIBLE);
						String imageUrl = ImageUtil.convertImageExtension(message.getImage(Utils.getDeviceDensity(this)));
						String fullImageUrl = getString(R.string.server_url_host) + imageUrl;
						AsyncImageLoader.getInstance().loadDrawable(
								this, fullImageUrl, imageUrl,
								descImageView, null, new ImageCallback() {
									public void imageLoaded(Bitmap imageDrawable,
															String imageUrl, View view) {
										((ImageView) view).setImageBitmap(imageDrawable);
									}
								});
					}

					if (message.getIcon(Utils.getDeviceDensity(this)).trim().equals("") || message.getIcon(Utils.getDeviceDensity(this)) == null) {
						ivIcon.setVisibility(View.INVISIBLE);
					} else {
						ivIcon.setVisibility(View.VISIBLE);
						String imageUrl = ImageUtil.convertImageExtension(message.getIcon(Utils.getDeviceDensity(this)));
						String fullImageUrl = getString(R.string.server_url_host) + imageUrl;
						AsyncImageLoader.getInstance().loadDrawable(
								this, fullImageUrl, imageUrl,
								ivIcon, null, new ImageCallback() {
									public void imageLoaded(Bitmap imageDrawable,
															String imageUrl, View view) {
										((ImageView) view).setImageBitmap(imageDrawable);
									}
								});
					}
				}
				btnMessage.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String url = message.getHyperlink();
						if(url != null && !url.isEmpty()){
							//Utils.openProwser(WizardActivity.this, url, NMBSApplication.getInstance().getClickToCallService());
							Utils.openWebView(WizardActivity.this, url, WebViewActivity.NORMAL_FLOW, "", message.isNavigationInNormalWebView());
							/*if(NetworkUtils.isOnline(WizardActivity.this)) {
								startActivity(WebViewActivity.createIntent(getApplicationContext(), Utils.getUrl(getApplicationContext(), url, NMBSApplication.getInstance().getClickToCallService())));
							}*/


						}
						GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.MESSAGES_CATEGORY,TrackerConstant.MESSAGES_CALL_TO_BUTTON,TrackerConstant.MESSAGES_CALL_TO_BUTTON_LABEL);
					}
				});
				//flipper.addView(convertView);
				viewList.add(convertView);
			}
		}
	}

	private void bindWizard(LayoutInflater layoutInflater){
		if (wizardResponse != null && wizardResponse.getWizards() != null) {
			llTemp.setBackgroundColor(getResources().getColor(R.color.background_activity_title));
			wizardItems = wizardResponse.getWizards();
			for (int i = 0; i < wizardItems.size(); i++) {

				View convertView = layoutInflater.inflate(R.layout.activity_wizard, null);
				ImageView btnClose = (ImageView) convertView.findViewById(R.id.btn_close);
				bindAllListeners(btnClose);
				TextView tvWizardTitle = (TextView) convertView.findViewById(R.id.tv_wizard_title);
				TextView tvWizardDescription = (TextView) convertView.findViewById(R.id.tv_wizard_description);
				ImageView ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
				String title = wizardItems.get(i).getTitle();
				if(title != null){
					title = title.toUpperCase();
				}
				tvWizardTitle.setText(title);
				tvWizardDescription.setText(wizardItems.get(i).getDescription());
				String icon = wizardItems.get(i).getAreaIcon();
				if("icon-icoTrainTicket".equalsIgnoreCase(icon)){
					ivIcon.setImageResource(R.drawable.ic_navigation_mytickets);
				}else if("icon-ico-beurope-reduction".equalsIgnoreCase(icon)){
					ivIcon.setImageResource(R.drawable.ic_wizard_logo);
				}
				String image = wizardItems.get(i).getHighResolutionImage();
				//Log.e("image", "image===" + image);
				ImageView descImageView = (ImageView) convertView.findViewById(R.id.iv_wizard);
				if(image != null){
					if(image.equalsIgnoreCase("WizardWelcome")){
						descImageView.setImageResource(R.drawable.wizard_welcome);
					}else if(image.equalsIgnoreCase("wizardRealTime")){
						descImageView.setImageResource(R.drawable.wizard_realtime);
					}else if(image.equalsIgnoreCase("wizardNewBooking")){
						descImageView.setImageResource(R.drawable.wizardnewbooking);
					}else if(image.equalsIgnoreCase("wizardHelp")){
						descImageView.setImageResource(R.drawable.wizardhelp);
					}
				}else{
					descImageView.setImageResource(R.drawable.stationboard);
				}


/*				String imageUrl = ImageUtil.convertImageExtension(wizardItems.get(i).getImage(Utils.getDeviceDensity(this)));
				String fullImageUrl = getString(R.string.server_url_host) + imageUrl;
				Log.d(TAG,"fullImageUrl=====" + fullImageUrl);
				AsyncImageLoader.getInstance().loadDrawable(
						this.getApplicationContext(), fullImageUrl, imageUrl,
						descImageView, null, new ImageCallback() {

							public void imageLoaded(Bitmap imageDrawable,
													String imageUrl, View view) {
								((ImageView) view).setImageBitmap(imageDrawable);
							}
						});*/

				//flipper.addView(convertView);
				viewList.add(convertView);
			}
		}
	}


	// Bind all view Listeners
	private void bindAllListeners(ImageView btnClose) {
		btnClose.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				saveMessageTime();
				finish();
			}
		});
	}


	private void addDotToLinearLayoutWizard() {
		if (wizardItems != null) {
			for (int j = 0; j < wizardItems.size(); j++) {
				TextView textView1 = new TextView(this);
				textView1.setPadding(5, 0, 5, 0);
				textView1.setText(getString(R.string.group_list_item_tag_middle_dot));
				textView1.setTextSize(getResources().getDimension(R.dimen.textsize_dot));

				if (j == 0) {
					textView1.setTextColor(getResources().getColor(R.color.toggle_on));
				} else {
					textView1.setTextColor(getResources().getColor(R.color.textcolor_secondaction));
				}
				textView1.setLayoutParams(new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

				textView1.setGravity(Gravity.CENTER_HORIZONTAL);
				dotLayout.addView(textView1);
			}
		}			
	}

	private void addDotToLinearLayoutMessage() {
		if (showMessages != null) {
			for (int j = 0; j < showMessages.size(); j++) {
				TextView textView1 = new TextView(this);
				textView1.setPadding(5, 0, 5, 0);
				textView1.setText(getString(R.string.group_list_item_tag_middle_dot));
				textView1.setTextSize(getResources().getDimension(R.dimen.textsize_dot));
				if (j == 0) {
					textView1.setTextColor(getResources().getColor(R.color.dot_active));
				} else {
					textView1.setTextColor(getResources().getColor(R.color.textcolor_primaryaction));
				}
				textView1.setLayoutParams(new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

				textView1.setGravity(Gravity.CENTER_HORIZONTAL);
				dotLayout.addView(textView1);
			}
		}
	}

	private void setDotState() {
		for (int i = 0; i < dotLayout.getChildCount(); i++) {
			TextView textView = (TextView) dotLayout.getChildAt(i);
			if (i == this.viewPager.getCurrentItem()) {
				textView.setTextColor(getResources().getColor(R.color.textcolor_secondaction));
			} else {
				textView.setTextColor(getResources().getColor(R.color.toggle_on));
			}
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		super.dispatchTouchEvent(ev);
		return detector.onTouchEvent(ev);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			saveMessageTime();
			finish();
		}

		return true;
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
		/*if (e1.getX() - e2.getX() > 120) {
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
		}*/
		return false;
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

	/**
	 * Utility method for every Activity who needs to start this Activity.
	 * 
	 * @param context
	 */
	public static Intent createIntent(Context context, String whichContext) {
		Intent intent = new Intent(context, WizardActivity.class);
		intent.putExtra(Intent_Key_WhichContext, whichContext);
		return intent;
	}
	
	@Override
	protected void onDestroy() {
		//messageService.saveMessageStateInWichTab(ActivityConstant.MOBILEMESSAGE);
		super.onDestroy();
	}
	public void saveMessageTime(){
		if (showMessages != null && showMessages.size() > 0) {
			messageService.saveMessageNextDisplay(showMessages);
		}
	}
}
