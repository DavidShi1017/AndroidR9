package com.nmbs.controllers;

import com.nmbs.async.MasterDataAsyncTask;
import com.nmbs.handler.RestMasterDataHandler;
import com.nmbs.listeners.ActivityPostExecuteListener;

/**
 * Created by shig on 2015/12/21.
 */
public class MasterController {
    private static MasterController instance;

    // tag for this class
    private static final String TAG = MasterController.class.getSimpleName();

    /**
     * @return an instance of this singleton
     */
    public static MasterController getInstance() {
        if (instance == null)
            instance = new MasterController();
        return instance;
    }


}
