package com.cflint.activities.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cflint.R;
import com.cflint.application.NMBSApplication;
import com.cflint.listeners.SettingsListener;
import com.cflint.services.impl.SettingService;
import com.cflint.util.Validation;

public class DialogSettingsEmail extends Dialog {

    private EditText edEmail;
    private Button btnOk, btnCancel;
    private String email;
    private Context context;
    private SettingsListener settingsListener;
    private TextView tvEmailError;

    public DialogSettingsEmail(Context context, SettingsListener settingsListener, String email) {
        super(context, R.style.Dialogheme);
        this.settingsListener = settingsListener;
        this.context = context;
        this.email = email;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Set dialog layout.
        setContentView(R.layout.dialog_settings_email);

        bindAllViewsElement();
        bindAllListeners();
        setViewStateBasedOnValue();
    }

    private void bindAllViewsElement(){
        btnOk = (Button) findViewById(R.id.btn_ok);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        edEmail = (EditText) findViewById(R.id.et_settings_email);
        tvEmailError = (TextView) findViewById(R.id.tv_email_error);
    }

    private void bindAllListeners(){
        btnOk.setOnClickListener(okButtonOnClickListener);
        btnCancel.setOnClickListener(cancelButtonOnClickListener);

    }

    private View.OnClickListener okButtonOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            String email = edEmail.getText().toString();
            boolean isValid = Validation.validateEmail(email);
            if (isValid){
                /*SettingService settingService = new SettingService(context);
                settingService.setEmail(email);*/
                NMBSApplication.getInstance().getLoginService().setEmail(email);
                settingsListener.setEmail(email);
                tvEmailError.setVisibility(View.GONE);
                dismiss();
            }else{
                tvEmailError.setVisibility(View.VISIBLE);
            }

        }
    };
    private View.OnClickListener cancelButtonOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            dismiss();
        }
    };

    private void setViewStateBasedOnValue(){
        edEmail.setText(this.email);
    }
}



