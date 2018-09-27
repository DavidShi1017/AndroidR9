package com.cfl.async;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.cfl.R;
import com.cfl.application.NMBSApplication;
import com.cfl.log.LogUtils;
import com.cfl.model.ForgotPwdInfoResponse;
import com.cfl.model.LogonInfo;
import com.cfl.model.ResendInfoResponse;
import com.cfl.services.impl.LoginService;
import com.cfl.util.DecryptUtils;
import com.cfl.util.Utils;


public class ForgotPwdAsyncTask extends AsyncTask<Void, Void, Void> {


    private Context mContext;
    private Handler handler;
    private String email;
    public static final String Intent_Key_Error = "Error";
    public ForgotPwdAsyncTask(Context context, Handler handler, String email){
        this.mContext = context;
        this.handler = handler;
        this.email = email;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        LogUtils.d("ResendAsyncTask", "doInBackground------->");
        String errorMsg = null;
        try {
            LoginService loginService = NMBSApplication.getInstance().getLoginService();
            String customerId = "";

            ForgotPwdInfoResponse forgotPwdInfoResponse = loginService.forgotPwd(email,  NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey());

            if(forgotPwdInfoResponse != null){
                if(forgotPwdInfoResponse.getForgotPwdInfo() != null){
                    if(forgotPwdInfoResponse.getForgotPwdInfo().isSuccess()){
                        errorMsg = "";
                    }else {
                        errorMsg = forgotPwdInfoResponse.getForgotPwdInfo().getFailureMessage();
                    }
                }else {
                    if(forgotPwdInfoResponse.getMessages() != null && forgotPwdInfoResponse.getMessages().size() > 0){
                        com.cfl.model.Message message = forgotPwdInfoResponse.getMessages().get(0);
                        if(message != null && "400".equalsIgnoreCase(message.getStatusCode())){
                            errorMsg = message.getDescription();
                        }else{
                            errorMsg = mContext.getResources().getString(R.string.general_server_unavailable);
                        }
                    }
                }
            }
            LogUtils.d("ResendAsyncTask", "doInBackground---errorMsg---->" + errorMsg);
            sendMessageByWhat(errorMsg);
        } catch (Exception e) {
            e.printStackTrace();
            errorMsg = mContext.getResources().getString(R.string.general_server_unavailable);
            LogUtils.d("ResendAsyncTask", "doInBackground---Exception---->" + e.getMessage());
            sendMessageByWhat(errorMsg);
        }
        return null;
    }
    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }
    private void sendMessageByWhat(String errorMsg){
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Intent_Key_Error, errorMsg);
        if(errorMsg != null && !errorMsg.isEmpty()){
            message.what = 0;
        }else{
            message.what = 1;
        }
        message.setData(bundle);
        if (handler != null) {
            handler.sendMessage(message);
        }

    }
}
