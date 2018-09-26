package com.nmbs.activities.view;

import android.app.Activity;
import android.app.ProgressDialog;

import com.nmbs.R;

/**
 * Created by shig on 2015/12/21.
 */
public class ProgressDialogCustom {
    private static ProgressDialogCustom instance;
    private ProgressDialog progressDialog;

    /**
     * @return an instance of this singleton
     */
    public static ProgressDialogCustom getInstance() {
        if (instance == null)
            instance = new ProgressDialogCustom();
        return instance;
    }
    public void showWaitDialog(final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                if (progressDialog == null) {
                    //Log.e(TAG, "Show Wait Dialog....");
                    progressDialog = android.app.ProgressDialog.show(activity,
                            "",
                            activity.getString(R.string.general_loading), true);
                }
            }
        });
    }
    public void hideWaitDialog(final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                //Log.e(TAG, "Hide Wait Dialog....");
                if (progressDialog != null) {
                    //Log.e(TAG, "progressDialog is.... " + progressDialog);
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }
        });

    }
}
