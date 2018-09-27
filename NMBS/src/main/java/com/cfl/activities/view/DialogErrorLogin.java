package com.cfl.activities.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cfl.R;



public class DialogErrorLogin extends Dialog {

    private Button btnClose, btnResend;
    private ImageView closeImageButton;
    private Activity activity;
    private TextView tvTitle, tvDescribe;
    private String describe;
    private int flag;
    public DialogErrorLogin(Activity activity, String describe) {
        super(activity, R.style.Dialogheme);
        this.activity = activity;
        this.describe = describe;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Set dialog layout.
        setContentView(R.layout.dialog_error_login_view);

        bindAllViewsElement();
        bindAllListeners();
        setViewStateBasedOnValue();
    }

    private void bindAllViewsElement(){
        tvTitle = (TextView) findViewById(R.id.tv_dialog_alert_view_title);
        tvDescribe = (TextView) findViewById(R.id.tv_dialog_alert_view_describe);
        btnClose = (Button) findViewById(R.id.btn_dialog_alert_close);
        btnResend = (Button) findViewById(R.id.btn_dialog_alert_send);
        closeImageButton = (ImageView) findViewById(R.id.iv_dialog_alert_close);
    }

    private void bindAllListeners(){
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        closeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // activity.startActivity(CreateProfileActivity.createIntent(activity));
                dismiss();
                buttonCallback.callback();
            }
        });
    }

    private void setViewStateBasedOnValue(){
        //tvTitle.setText(title);
        tvDescribe.setText(describe);
    }

    private ButtonCallback buttonCallback;
    public void setRefreshCallback(ButtonCallback buttonCallback) {
        this.buttonCallback = buttonCallback;
    }
    public interface ButtonCallback {
        public void callback();
    }
}



