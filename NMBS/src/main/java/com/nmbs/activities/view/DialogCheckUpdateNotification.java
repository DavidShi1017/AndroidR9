package com.nmbs.activities.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nmbs.R;
import com.nmbs.adapter.CheckUpdateVersioinDescAdapter;
import com.nmbs.model.CheckAppUpdate;
import com.nmbs.services.ICheckUpdateService;

public class DialogCheckUpdateNotification extends Dialog {

    private Button downloadBtn;
    private Context context;
    private TextView tvNotNow;
    private LinearLayout llNotNow, descLayout;
    private ImageView ivClose;
    private ICheckUpdateService checkUpdateService;
    private Activity activity;

    private CheckAppUpdate checkAppUpdate;
    private CheckUpdateVersioinDescAdapter checkUpdateVersioinDescAdapter;
    private final static String TAG = DialogCheckUpdateNotification.class.getSimpleName();
    public DialogCheckUpdateNotification(Context context, Activity activity,ICheckUpdateService checkUpdateService,CheckAppUpdate checkAppUpdate) {
        super(context, R.style.Dialogheme);
        this.context = context;
        this.activity = activity;
        this.checkUpdateService = checkUpdateService;
        this.checkAppUpdate = checkAppUpdate;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Set dialog layout.
        setContentView(R.layout.dialog_check_update_notification);
        bindAllViewsElement();
        bindAllListeners();
        setViewStateBasedOnValue();
    }

    private void bindAllViewsElement(){
        tvNotNow = (TextView) findViewById(R.id.tv_check_update_notnow_title);
        tvNotNow.setText(context.getResources().getString(R.string.general_notnow).toUpperCase());
        llNotNow = (LinearLayout) findViewById(R.id.ll_notnow);
        descLayout = (LinearLayout) findViewById(R.id.ll_dialog_check_update_desc);
        downloadBtn = (Button) findViewById(R.id.btn_check_update_download);
        ivClose = (ImageView) findViewById(R.id.iv__check_update_close);
    }

    private void bindAllListeners(){
        llNotNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                checkUpdateService.saveCheckStutes(activity.getApplicationContext());
            }
        });
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                checkUpdateService.saveCheckStutes(activity.getApplicationContext());
            }
        });
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=com.nmbs"));
                    activity.startActivity(intent);
                } catch (Exception e) {
                    // google play app is not installed
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.nmbs"));
                    activity.startActivity(intent);
                }
            }
        });
    }

    private void setViewStateBasedOnValue(){
        checkUpdateVersioinDescAdapter = new CheckUpdateVersioinDescAdapter(activity,checkAppUpdate.getReleaseNotes());
        descLayout.removeAllViews();
        for(int i=0;i<checkAppUpdate.getReleaseNotes().size();i++){
            checkUpdateVersioinDescAdapter.getDescItem(i,descLayout);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            dismiss();
            checkUpdateService.saveCheckStutes(activity.getApplicationContext());
        }
        return super.onKeyDown(keyCode, event);
    }
}



