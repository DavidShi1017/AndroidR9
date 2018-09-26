package com.nmbs.async;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.nmbs.R;
import com.nmbs.application.NMBSApplication;
import com.nmbs.exceptions.NetworkError;
import com.nmbs.log.LogUtils;
import com.nmbs.model.LoginResponse;
import com.nmbs.model.LogonInfo;
import com.nmbs.services.ISettingService;
import com.nmbs.services.impl.LoginService;
import com.nmbs.services.impl.ServiceConstant;


public class LoginAsyncTask extends AsyncTask<Void, Void, Void> {

    private ISettingService settingService;
    private Context mContext;
    private Handler handler;
    private String email;
    private String pwd;
    private String loginProvider;
    private String token;
    public static final String Intent_Key_Error = "Error";
    public static final String Intent_Key_Logoninfo = "Logoninfo";
    public LoginAsyncTask(Context context, Handler handler, String email, String pwd, String loginProvider, String token){
        this.mContext = context;
        this.handler = handler;
        this.email = email;
        this.pwd = pwd;
        this.loginProvider = loginProvider;
        this.token = token;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        LogUtils.d("LoginAsyncTask", "doInBackground------->");
        String errorMsg = null;
        LoginResponse loginResponse = null;
        try {
            LoginService loginService = NMBSApplication.getInstance().getLoginService();
            if(LoginService.LOGIN_PROVIDER_CRIS.equalsIgnoreCase(loginProvider)){
                loginResponse = loginService.login(email, pwd, loginProvider, NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey());
            }else{
                loginResponse = loginService.loginSocial(loginProvider, NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey(), token);
            }

            if(loginResponse != null){
                if(loginResponse.getLogonInfo() != null){
                    LogUtils.d("LoginAsyncTask", "doInBackground---LogonInfo is not null---->" + loginResponse.getLogonInfo().getState());
                    sendMessageByWhat("", loginResponse.getLogonInfo());
                }else {
                    LogUtils.d("LoginAsyncTask", "doInBackground---LogonInfo is null---->" + loginResponse.getLogonInfo());
                    errorMsg = mContext.getResources().getString(R.string.general_server_unavailable);
                    sendMessageByWhat(errorMsg, null);
                }
            }
           /* if(errorMsg != null && !errorMsg.isEmpty()){

            }*/

        } catch (Exception e) {
            e.printStackTrace();
            errorMsg = mContext.getResources().getString(R.string.general_server_unavailable);
            LogUtils.d("LoginAsyncTask", "doInBackground---Exception---->" + e.getMessage());
            sendMessageByWhat(errorMsg, null);
        }
        return null;
    }
    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }
    private void sendMessageByWhat(String errorMsg, LogonInfo logonInfo){
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Intent_Key_Error, errorMsg);
        bundle.putSerializable(Intent_Key_Logoninfo, logonInfo);

        if(logonInfo != null){
            message.what = 0;
        }else{
            message.what = 1;
            LogUtils.d("LoginAsyncTask", "sendMessageByWhat---errorMsg---->" + errorMsg);
        }
        message.setData(bundle);
        if (handler != null) {
            handler.sendMessage(message);
        }

    }
}
