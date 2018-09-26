package com.nmbs.activities.view;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.nmbs.R;


public class DialogMyOptions extends Dialog {

    private Button downloadBtn;
    private Context context;
    private ImageView ivClose;


    private final static String TAG = DialogMyOptions.class.getSimpleName();
    public DialogMyOptions(Context context) {
        super(context, R.style.Dialogheme);
        this.context = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Set dialog layout.
        setContentView(R.layout.dialog_myoptions);
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
            }
        });
    }

}



