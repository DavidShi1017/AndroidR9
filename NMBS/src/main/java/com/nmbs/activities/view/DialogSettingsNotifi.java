package com.nmbs.activities.view;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nmbs.R;
import com.nmbs.activities.SettingsActivity;
import com.nmbs.application.NMBSApplication;
import com.nmbs.async.CheckUpdateAsyncTask;
import com.nmbs.async.UpdateSubscriptionUserAsyncTask;
import com.nmbs.exceptions.NetworkError;
import com.nmbs.listeners.SettingsListener;
import com.nmbs.log.LogUtils;
import com.nmbs.model.HafasUser;
import com.nmbs.services.impl.ServiceConstant;
import com.nmbs.services.impl.SettingService;

import org.w3c.dom.Text;

public class DialogSettingsNotifi extends Dialog {

    private Button btnCancel;
    private RelativeLayout btnOk;
    private Context context;
    private SettingsListener settingsListener;
    private Spinner spStartNotifi, spDelayNotifi;
    private SettingService settingService;
    private String selectStartTime, selectDelayTime, changedStartTime, changedDelayTime;
    private TextView tvError;
    private ProgressBar progressBar;
    private ImageView ivClose;

    public DialogSettingsNotifi(Context context, SettingsListener settingsListener, String selectStartTime, String selectDelayTime) {
        super(context, R.style.Dialogheme);
        this.settingsListener = settingsListener;
        this.context = context;
        this.selectStartTime = selectStartTime;
        this.selectDelayTime = selectDelayTime;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Set dialog layout.
        setContentView(R.layout.dialog_settings_notifi);
        settingService = new SettingService(context);
        bindAllViewsElement();
        bindAllListeners();
        setViewStateBasedOnValue();
    }

    private void bindAllViewsElement() {
        btnOk = (RelativeLayout) findViewById(R.id.btn_ok);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        spStartNotifi = (Spinner) findViewById(R.id.sp_start_notifi);
        spDelayNotifi = (Spinner) findViewById(R.id.sp_delay_notifi);
        tvError = (TextView) findViewById(R.id.tv_notification_error);
        progressBar = (ProgressBar) findViewById(R.id.pgb_update_bar);
        ivClose = (ImageView) findViewById(R.id.iv_close);
    }

    private void bindAllListeners() {
        btnOk.setOnClickListener(okButtonOnClickListener);
        ivClose.setOnClickListener(cancelButtonOnClickListener);
        btnCancel.setOnClickListener(cancelButtonOnClickListener);
        spStartNotifi.setOnItemSelectedListener(spStartNotifiOnItemClickListener);
        spDelayNotifi.setOnItemSelectedListener(spDelayNotifiOnItemClickListener);
    }

    private Spinner.OnItemSelectedListener spStartNotifiOnItemClickListener = new Spinner.OnItemSelectedListener(){

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String[] time = context.getResources().getStringArray(R.array.settings_start_time);
            changedStartTime = time[position];
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private Spinner.OnItemSelectedListener spDelayNotifiOnItemClickListener = new Spinner.OnItemSelectedListener(){

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String[] time = context.getResources().getStringArray(R.array.settings_time);
            changedDelayTime = time[position];

        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private View.OnClickListener okButtonOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {

            HafasUser hafasUser = NMBSApplication.getInstance().getPushService().getUser();

            if(hafasUser != null){
                upDateUser(hafasUser);

            }else{
                LogUtils.e("hafasUser", " hafasUser is null...");
                tvError.setVisibility(View.VISIBLE);
                tvError.setText(context.getString(R.string.alert_updateuser_failed));
                //dismiss();
            }


        }
    };

    private void upDateUser(HafasUser hafasUser){
        progressBar.setVisibility(View.VISIBLE);
        btnOk.setClickable(false);

        int delayTime = 5;
        int startTime = 5;
        int space = 0;
        if(changedDelayTime != null && changedDelayTime.length() > 0){
            space = changedDelayTime.indexOf(" ");
            delayTime = Integer.valueOf(changedDelayTime.substring(0, space));
        }
        if(changedStartTime != null && changedStartTime.length() > 0){
            space = changedStartTime.indexOf(" ");
            startTime = Integer.valueOf(changedStartTime.substring(0, space));
            if(startTime != 30){
                startTime = startTime * 60;
            }
        }
        LogUtils.e("delayTime", "delayTime===" + delayTime);
        LogUtils.e("startTime", "startTime===" + startTime);
        HafasUser newHafasUser = new HafasUser(hafasUser.getUserId(),
                NMBSApplication.getInstance().getPushService().getRegistrationId(), NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey(),
                delayTime, delayTime, startTime);

        UpdateSubscriptionUserAsyncTask updateSubscriptionUserAsyncTask = new UpdateSubscriptionUserAsyncTask(newHafasUser, context, updateUserHandler);
        updateSubscriptionUserAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        //updateSubscriptionUserAsyncTask.execute();
    }
    private View.OnClickListener cancelButtonOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            dismiss();
        }
    };

    private Handler updateUserHandler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case ServiceConstant.MESSAGE_WHAT_OK:
                    dismiss();
                    progressBar.setVisibility(View.GONE);
                    btnOk.setClickable(true);
                    settingService.setStartNotifiTime(changedStartTime);
                    settingsListener.setStartNotifi(changedStartTime);
                    settingService.setDelayNotifiTime(changedDelayTime);
                    settingsListener.setDelayNotifi(changedDelayTime);
                    break;
                case ServiceConstant.MESSAGE_WHAT_ERROR:
                    progressBar.setVisibility(View.GONE);
                    btnOk.setClickable(true);
                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText(context.getString(R.string.alert_updateuser_failed));
                    break;
            }
        };
    };

    private void setViewStateBasedOnValue(){
        int startTimePosition = settingService.selectedNotifiTime(selectStartTime);
        spStartNotifi.setSelection(startTimePosition);
        int delayTimePosition = settingService.selectedNotifiTime(selectDelayTime);
        spDelayNotifi.setSelection(delayTimePosition);
    }

}



