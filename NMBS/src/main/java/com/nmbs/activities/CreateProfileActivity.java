package com.nmbs.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.nmbs.R;
import com.nmbs.activity.BaseActivity;
import com.nmbs.application.NMBSApplication;
import com.nmbs.services.IClickToCallService;
import com.nmbs.services.IMasterService;
import com.nmbs.services.IMessageService;
import com.nmbs.services.impl.SettingService;
import com.nmbs.util.Utils;

public class CreateProfileActivity extends BaseActivity {

	private SettingService settingService = null;
	private IMessageService messageService;
	private IMasterService masterService;


	private IClickToCallService clickToCallService;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//setStatusBarColor(this, getResources().getColor(R.color.background_activity_title));
		Utils.setToolBarStyle(this);
		setContentView(R.layout.activity_create_profile);
		settingService = ((NMBSApplication) getApplication()).getSettingService();
		//settingService.initLanguageSettings();
		messageService = ((NMBSApplication) getApplication()).getMessageService();
		masterService = ((NMBSApplication) getApplication()).getMasterService();
		clickToCallService = ((NMBSApplication) getApplication()).getClickToCallService();
		bindAllViewElements();
	}
	private void bindAllViewElements() {

	}

	
	public static Intent createIntent(Context context) {
		Intent intent = new Intent(context, CreateProfileActivity.class);
		return intent;
	}
}
