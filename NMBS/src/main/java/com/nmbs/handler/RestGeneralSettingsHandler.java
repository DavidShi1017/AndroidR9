package com.nmbs.handler;

import android.os.Handler;
import android.os.Message;

import com.nmbs.listeners.ActivityPostExecuteListener;

/**
 * Created by shig on 2015/12/21.
 */
public class RestGeneralSettingsHandler extends Handler {

    // tag for logger
    private static final String TAG = RestGeneralSettingsHandler.class.getSimpleName();
    // REST RESULT
    private static final String RestResult = "RestResult";

    // current listener for post execution
    private ActivityPostExecuteListener postExecuteListener;

    /**
     * Set an extra listener for the post execute of the AsyncTask.
     *
     * @param postExecuteListener
     */
    public void setPostExecuteListener(ActivityPostExecuteListener postExecuteListener) {
        this.postExecuteListener = postExecuteListener;
    }

    /*
 * (non-Javadoc)
 *
 * @see android.os.Handler#handleMessage(android.os.Message)
 */
    @Override
    public void handleMessage(Message msg) {
        boolean isSucceeded = false;
        String errorMsg = "";
        if (msg.what == 1){
            isSucceeded = true;
        }else {
            // Get error msg.
        }
        this.postExecuteListener.onPostExecute(isSucceeded, errorMsg);
    }

}
