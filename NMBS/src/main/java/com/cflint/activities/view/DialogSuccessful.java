package com.cflint.activities.view;


import android.app.Dialog;
import android.content.Context;

import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cflint.R;


import com.cflint.listeners.RefreshOldDossierListener;


public class DialogSuccessful extends Dialog {

    private Button downloadBtn;
    private Context context;
    private TextView tvNotNow;
    private LinearLayout llNotNow, descLayout;
    private ImageView ivClose;
    private RefreshOldDossierListener listener;

    private final static String TAG = DialogSuccessful.class.getSimpleName();
    public DialogSuccessful(Context context, RefreshOldDossierListener listener) {
        super(context, R.style.Dialogheme);
        this.context = context;
        this.listener = listener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Set dialog layout.
        setContentView(R.layout.dialog_successful);
        bindAllViewsElement();
        bindAllListeners();

    }

    private void bindAllViewsElement(){
        tvNotNow = (TextView) findViewById(R.id.tv_check_update_notnow_title);
        tvNotNow.setText(context.getResources().getString(R.string.migrate_stay).toUpperCase());
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
                listener.stayHere();
            }
        });
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
                listener.viewRefreshedDossier();
            }
        });
    }

}



