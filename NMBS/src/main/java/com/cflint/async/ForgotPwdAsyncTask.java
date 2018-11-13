package com.cflint.async;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.cflint.R;
import com.cflint.application.NMBSApplication;
import com.cflint.log.LogUtils;
import com.cflint.model.ForgotPwdInfoResponse;
import com.cflint.model.LogonInfo;
import com.cflint.model.ResendInfoResponse;
import com.cflint.services.impl.LoginService;
import com.cflint.util.DecryptUtils;
import com.cflint.util.Utils;


public class ForgotPwdAsyncTask extends AsyncTask<Void, Void, Void> {


    private Context mContext;
    private Handler handler;
    private String email;
    public static final String Intent_Key_Error = "Error";
    public static final String Intent_Key_resetLogin = "resetLogin";

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
            boolean resetLogin = false;
            ForgotPwdInfoResponse forgotPwdInfoResponse = loginService.forgotPwd(email,  NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey());

            if(forgotPwdInfoResponse != null){
                if(forgotPwdInfoResponse.getForgotPwdInfo() != null){
                    if(forgotPwdInfoResponse.getForgotPwdInfo().isSuccess()){
                        errorMsg = "";
                        resetLogin = forgotPwdInfoResponse.getForgotPwdInfo().isResetLogin();
                    }else {
                        errorMsg = forgotPwdInfoResponse.getForgotPwdInfo().getFailureMessage();
                    }
                }else {
                    if(forgotPwdInfoResponse.getMessages() != null && forgotPwdInfoResponse.getMessages().size() > 0){
                        com.cflint.model.Message message = forgotPwdInfoResponse.getMessages().get(0);
                        if(message != null && "400".equalsIgnoreCase(message.getStatusCode())){
                            errorMsg = message.getDescription();
                        }else{
                            errorMsg = mContext.getResources().getString(R.string.general_server_unavailable);
                        }
                    }
                }
            }
            LogUtils.d("ResendAsyncTask", "doInBackground---errorMsg---->" + errorMsg);
            sendMessageByWhat(errorMsg, resetLogin);
        } catch (Exception e) {
            e.printStackTrace();
            errorMsg = mContext.getResources().getString(R.string.general_server_unavailable);
            LogUtils.d("ResendAsyncTask", "doInBackground---Exception---->" + e.getMessage());
            sendMessageByWhat(errorMsg, false);
        }
        return null;
    }
    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }
    private void sendMessageByWhat(String errorMsg, boolean resetLogin){
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Intent_Key_Error, errorMsg);
        bundle.putSerializable(Intent_Key_resetLogin, resetLogin);
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
