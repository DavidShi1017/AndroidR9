package com.cflint.activities.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cflint.R;

public class DialogError extends Dialog {

    private Button btnOk;
    private Context context;
    private TextView tvTitle, tvDescribe;
    private String title, describe;
    private ImageView ivClose;

    public DialogError(Context context, String title, String describe) {
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
        setContentView(R.layout.dialog_error);

        bindAllViewsElement();
        bindAllListeners();
        setViewStateBasedOnValue();
    }

    private void bindAllViewsElement(){
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvDescribe = (TextView) findViewById(R.id.tv_describe);
        ivClose = (ImageView) findViewById(R.id.iv_close);
        btnOk = (Button) findViewById(R.id.btn_close);
    }

    private void bindAllListeners(){
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
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



