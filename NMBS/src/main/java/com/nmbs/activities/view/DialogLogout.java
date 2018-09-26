package com.nmbs.activities.view;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.nmbs.R;
import com.nmbs.activities.MainActivity;
import com.nmbs.activities.SettingsActivity;
import com.nmbs.activities.WebViewCreateActivity;
import com.nmbs.application.NMBSApplication;
import com.nmbs.log.LogUtils;


public class DialogLogout extends Dialog {

    private Button downloadBtn;
    private Activity activity;
    private ImageView ivClose;
    private String name;

    private final static String TAG = DialogLogout.class.getSimpleName();
    public DialogLogout(Activity activity, String name) {
        super(activity, R.style.Dialogheme);
        this.activity = activity;
        this.name = name;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Set dialog layout.
        setContentView(R.layout.dialog_logout);
        bindAllViewsElement();
        bindAllListeners();

    }

    private void bindAllViewsElement(){
        downloadBtn = (Button) findViewById(R.id.btn_ok);
        ivClose = (ImageView) findViewById(R.id.iv_close);
    }

    private void bindAllListeners(){

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

            }
        });
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                NMBSApplication.getInstance().getLoginService().logOut();
                if(WebViewCreateActivity.TAG.equalsIgnoreCase(name)){
                    activity.finish();
                }else{
                    Intent myIntent = new Intent(activity.getApplicationContext(), MainActivity.class);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(myIntent);
                    //activity.recreate();
                }

            }
        });
    }

}



