package com.cfl.activities.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cfl.R;

public class DialogAlertError extends Dialog {

    private Button btnClose;
    private ImageView closeImageButton;
    private Context context;
    private TextView tvTitle, tvDescribe;
    private String title, describe;

    public DialogAlertError(Context context, String title, String describe) {
        super(context, R.style.Dialogheme);
        this.context = context;
        this.title = title;
        this.describe = describe;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Set dialog layout.
        setContentView(R.layout.dialog_alert_view);

        bindAllViewsElement();
        bindAllListeners();
        setViewStateBasedOnValue();
    }

    private void bindAllViewsElement(){
        tvTitle = (TextView) findViewById(R.id.tv_dialog_alert_view_title);
        tvDescribe = (TextView) findViewById(R.id.tv_dialog_alert_view_describe);
        btnClose = (Button) findViewById(R.id.btn_dialog_alert_close);
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
    }

    private void setViewStateBasedOnValue(){
        tvTitle.setText(title);
        tvDescribe.setText(describe);
    }
}



