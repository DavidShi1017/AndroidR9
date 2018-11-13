package com.cflint.async;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.cflint.R;
import com.cflint.application.NMBSApplication;
import com.cflint.log.LogUtils;
import com.cflint.model.LogonInfo;
import com.cflint.model.ResendInfoResponse;
import com.cflint.services.ISettingService;
import com.cflint.services.impl.LoginService;
import com.cflint.util.DecryptUtils;
import com.cflint.util.Utils;


public class ResendAsyncTask extends AsyncTask<Void, Void, Void> {


    private Context mContext;
    private Handler handler;
    private LogonInfo logonInfo;
    private String email;
    public static final String Intent_Key_Error = "Error";
    public ResendAsyncTask(Context context, Handler handler, LogonInfo logonInfo, String email){
        this.mContext = context;
        this.handler = handler;
        this.logonInfo = logonInfo;
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

            if(logonInfo != null){
                customerId = logonInfo.getCustomerId();
                //email = logonInfo.getEmail();
            }
            /*String salt = NMBSApplication.getInstance().getMasterService().loadGeneralSetting().getRestSalt();
            String decrypStr = DecryptUtils.decryptData(salt);*/
            String sha1 = loginService.getControl(logonInfo);
            ResendInfoResponse resendInfoResponse = loginService.resend(customerId, sha1,
                    NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey());

            if(resendInfoResponse != null){
                if(resendInfoResponse.getResendInfo() != null){
                    if(resendInfoResponse.getResendInfo().isSuccess()){
                        errorMsg = "";
                    }else {
                        errorMsg = resendInfoResponse.getResendInfo().getFailureMessage();
                    }
                }else {
                    if(resendInfoResponse.getMessages() != null && resendInfoResponse.getMessages().size() > 0){
                        com.cflint.model.Message message = resendInfoResponse.getMessages().get(0);
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
