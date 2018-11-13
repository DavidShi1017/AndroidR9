package com.cflint.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.cflint.R;
import com.cflint.activities.view.DialogCheckUpdateNotification;
import com.cflint.activity.BaseActivity;
import com.cflint.application.NMBSApplication;
import com.cflint.async.AsyncImageLoader;
import com.cflint.async.AsyncImageLoader.ImageCallback;
import com.cflint.model.CheckAppUpdate;
import com.cflint.model.MobileMessage;
import com.cflint.model.MobileMessageResponse;
import com.cflint.services.ICheckUpdateService;
import com.cflint.services.IClickToCallService;
import com.cflint.services.IMessageService;
import com.cflint.services.impl.SettingService;
import com.cflint.util.GoogleAnalyticsUtil;
import com.cflint.util.ImageUtil;
import com.cflint.util.TrackerConstant;
import com.cflint.util.Utils;

import java.util.List;

public class MessageWizardActivity extends BaseActivity implements OnGestureListener {

	private GestureDetector detector;
	private ViewFlipper flipper;
	private LinearLayout dotLayout;
	private MobileMessageResponse messageResponse;
	//private List<MobileMessage> mobileMessages;
	private static final String MESSAGE_RESPONSE_SERIALIZABLE_KEY = "Message_Response_key";
	private static final String TAG = MessageWizardActivity.class.getSimpleName();
	private IMessageService messageService;
	private SettingService settingService;
	private IClickToCallService clickToCallService;
	private ICheckUpdateService checkUpdateService;
	private DialogCheckUpdateNotification dialogCheckUpdateNotification;
	private List<MobileMessage> showMessages = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Utils.setToolBarStyle(this);
		settingService = ((NMBSApplication) getApplication()).getSettingService();
		//settingService.initLanguageSettings();
		setContentView(R.layout.message_wizard_group_view);
		messageService = ((NMBSApplication) getApplication()).getMessageService();
		messageResponse = (MobileMessageResponse) getIntent().getSerializableExtra(MESSAGE_RESPONSE_SERIALIZABLE_KEY);
		clickToCallService = ((NMBSApplication) getApplication()).getClickToCallService();
		checkUpdateService = ((NMBSApplication) getApplication()).getCheckUpdateService();
		//Log.d(TAG, "messageResponse size is=========" + messageResponse.getMessages());
		bindAllViewsElement();
		showCheckUpdateAppResponse();
		GoogleAnalyticsUtil.getInstance().sendScreen(MessageWizardActivity.this, TrackerConstant.MESSAGE_OVERLAY);
	}

	// Bind all view elements
	private void bindAllViewsElement() {

		dotLayout = (LinearLayout) findViewById(R.id.message_wizard_group_view_dot_LinearLayout);
		//closeBtn = (ImageView) findViewById(R.id.message_wizard_group_view_close_btn);
		detector = new GestureDetector(this);
		flipper = (ViewFlipper) this.findViewById(R.id.message_ViewFlipper01);
		
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if(messageService.getMessageResponse() != null){
			messageResponse = messageService.getMessageResponse();
		}
		if (messageResponse != null){
			showMessages = messageService.getShowMessage(messageResponse.getMobileMessages());
		}
		//showMessages = messageService.getShowMessage(messageService.getMessageResponse().getMobileMessages());

		if (showMessages != null) {
			//Log.d(TAG, "messageResponse size is=========" + showMessages);
			//mobileMessages = messageResponse.getMobileMessages();
			for (int i = 0; i < showMessages.size(); i++) {
				final MobileMessage message = showMessages.get(i);
				View convertView = layoutInflater.inflate(R.layout.activity_messages_wizard, null);
				ImageView btnClose = (ImageView) convertView.findViewById(R.id.btn_close);
				ImageView ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
				bindAllListeners(btnClose);
				TextView tvMessageTitle = (TextView) convertView.findViewById(R.id.tv_message_title);
				TextView tvMessageDescription = (TextView) convertView.findViewById(R.id.tv_message_description);
				TextView tvMessageValidity = (TextView) convertView.findViewById(R.id.tv_validity);
				final Button btnMessage = (Button) convertView.findViewById(R.id.btn_message);
				if(message != null){
					String btnText = message.getActionButtonText();
					if (message.isIncludeActionButton()){

						if(btnText != null){
							btnText = btnText.toUpperCase();
						}
						btnMessage.setText(btnText);
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
					btnMessage.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							String url = message.getHyperlink();
							if(url != null && !url.isEmpty()){
								//Utils.openProwser(MessageWizardActivity.this, url, clickToCallService);

								Utils.openWebView(MessageWizardActivity.this, url, WebViewActivity.NORMAL_FLOW, "", message.isNavigationInNormalWebView());
								/*if(NetworkUtils.isOnline(MessageWizardActivity.this)) {
									if (url.contains("Navigation=WebView")) {
										startActivity(WebViewActivity.createIntent(getApplicationContext(), Utils.getUrl(getApplicationContext(), url, clickToCallService)));
									} else {
										startActivity(WebViewOverlayActivity.createIntent(getApplicationContext(), Utils.getUrl(getApplicationContext(), url, clickToCallService)));
									}
								}*/
							}
							GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.MESSAGES_CATEGORY, TrackerConstant.MESSAGES_CALL_TO_BUTTON, btnMessage.getText().toString());
						}
					});
				}

				flipper.addView(convertView);
				}
			}

		addDotToLinearLayout();
		setDotState();
	}
	private void showCheckUpdateAppResponse(){
		final CheckAppUpdate checkAppUpdate = checkUpdateService
				.getCheckAppUpdate();
		if(checkAppUpdate!=null){
			if(checkAppUpdate.isUpToDate()){
				Toast.makeText(MessageWizardActivity.this, getString(R.string.checkappversion_alert_no_update), Toast.LENGTH_LONG).show();
			}else{
				dialogCheckUpdateNotification = new DialogCheckUpdateNotification(MessageWizardActivity.this,MessageWizardActivity.this,checkUpdateService,checkAppUpdate);
				dialogCheckUpdateNotification.setCanceledOnTouchOutside(false);
				dialogCheckUpdateNotification.show();
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

	private void addDotToLinearLayout() {
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
			if (i == this.flipper.getDisplayedChild()) {
				textView.setTextColor(getResources().getColor(R.color.textcolor_primaryaction));
			} else {
				textView.setTextColor(getResources().getColor(R.color.dot_active));
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
	public static Intent createIntent(Context context, MobileMessageResponse messageResponse) {
		Intent intent = new Intent(context, MessageWizardActivity.class);
		intent.putExtra(MESSAGE_RESPONSE_SERIALIZABLE_KEY, messageResponse);
		return intent;
	}
	
	@Override
	protected void onDestroy() {
		//messageService.saveMessageStateInWichTab(ActivityConstant.MOBILEMESSAGE);
		super.onDestroy();
	}
	
	public void saveMessageTime(){
		if (showMessages != null) {
			messageService.saveMessageNextDisplay(showMessages);
		}
	}

	public void btnAction(View view){

	}
}
