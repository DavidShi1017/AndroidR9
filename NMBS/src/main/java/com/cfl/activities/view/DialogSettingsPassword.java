package com.cfl.activities.view;

import com.cfl.R;
import com.cfl.listeners.SettingsListener;
import com.cfl.services.impl.SettingService;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;

import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

public class DialogSettingsPassword extends Dialog {
    private CheckBox cbPassword;
    private EditText edPassword;
    private Button btnOk, btnCancel;
    private Context context;
    private SettingsListener settingsListener;
    private String pwd;

    public DialogSettingsPassword(Context context, SettingsListener settingsListener, String pwd) {
        super(context, R.style.Dialogheme);
        this.settingsListener = settingsListener;
        this.context = context;
        this.pwd = pwd;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Set dialog layout.
        setContentView(R.layout.dialog_settings_password);

        bindAllViewsElement();
        bindAllListeners();
        setViewStateBasedOnValue();
    }

    private void bindAllViewsElement() {
        btnOk = (Button) findViewById(R.id.btn_ok);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        cbPassword = (CheckBox) findViewById(R.id.cb_settings_showpassword);
        edPassword = (EditText) findViewById(R.id.et_settings_password);
    }

    private void bindAllListeners() {
        btnOk.setOnClickListener(okButtonOnClickListener);
        btnCancel.setOnClickListener(cancelButtonOnClickListener);
        cbPassword.setOnCheckedChangeListener(checkedChangeListener);
    }

    private View.OnClickListener okButtonOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            String password = edPassword.getText().toString();
            SettingService settingService = new SettingService(context);
            settingService.setPwd(password);
            settingsListener.setPassword(password);
            dismiss();
        }
    };
    private View.OnClickListener cancelButtonOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            dismiss();
        }
    };
    protected CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // TODO Auto-generated method stub
            if (isChecked) {
                //Show password
                edPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                //Hide password
                edPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    };

    private void setViewStateBasedOnValue(){
        edPassword.setText(this.pwd);
    }

}



