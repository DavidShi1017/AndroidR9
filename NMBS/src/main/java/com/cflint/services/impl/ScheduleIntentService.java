package com.cflint.services.impl;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.cflint.dataaccess.restservice.IScheduleDataService;
import com.cflint.dataaccess.restservice.impl.ScheduleDataService;
import com.cflint.exceptions.CustomError;
import com.cflint.exceptions.NetworkError;
import com.cflint.model.ScheduleQuery;
import com.cflint.model.ScheduleResponse;

/**
 * Created by Richard on 3/8/16.
 */
public class ScheduleIntentService extends IntentService {
    private static Context mContext;
    private Intent broadcastIntent = new Intent();

    private ScheduleQuery scheduleQuery;

    private ScheduleResponse scheduleResponse = null;

    private IScheduleDataService scheduleDataService = new ScheduleDataService();

    public ScheduleIntentService() {
        super(".intentservices.ScheduleIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String languageBeforSetting = (String) intent.getSerializableExtra(ServiceConstant.PARAM_IN_LANGUAGE);
        scheduleQuery = (ScheduleQuery) intent.getSerializableExtra(ServiceConstant.PARAM_IN_MSG_SCHEDULE_QUERY_RCODE);
        try {
            scheduleResponse = scheduleDataService.executeScheduleQuery(scheduleQuery, languageBeforSetting, mContext);
            broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_SCHEDULE_QUERY_RESPONSE);
            broadcastIntent.addCategory(ServiceConstant.INTENT_CATEGORY_RESPONSE);
            broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_MSG, scheduleResponse);
            sendBroadcast(broadcastIntent);
        }catch (CustomError e) {
            catchError(NetworkError.CustomError, e);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            catchError(NetworkError.TIMEOUT, e);
        }
    }

    public static void startService(Context context, ScheduleQuery scheduleQuery, String languageBeforSetting){
        mContext = context;
        Intent msgIntent = new Intent(context, ScheduleIntentService.class);

        msgIntent.putExtra(ServiceConstant.PARAM_IN_MSG_SCHEDULE_QUERY_RCODE, scheduleQuery);

        msgIntent.putExtra(ServiceConstant.PARAM_IN_LANGUAGE, languageBeforSetting);
        context.startService(msgIntent);
    }

    /**
     * Set error broadcast action,and sent broadcast.
     */
    public void catchError(NetworkError error, Exception e){
        broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_SCHEDULE_QUERY_ERROR);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_ERROR, error);
        broadcastIntent.putExtra(ServiceConstant.PARAM_OUT_ERROR_MESSAGE, e.getMessage());
        sendBroadcast(broadcastIntent);
        e.printStackTrace();
    }

}
