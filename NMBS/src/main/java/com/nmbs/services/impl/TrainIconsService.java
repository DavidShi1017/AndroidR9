package com.nmbs.services.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.nmbs.R;
import com.nmbs.dataaccess.database.TrainIconsDatabaseService;
import com.nmbs.dataaccess.restservice.impl.MasterDataService;
import com.nmbs.model.TrainIcon;
import com.nmbs.util.FileManager;
import com.nmbs.util.ImageUtil;
import com.nmbs.util.Utils;

import java.util.List;

/**
 * Created by shig on 2016/7/7.
 */

public class TrainIconsService {
    private Context context;
    private final static String TAG = TrainIconsService.class.getSimpleName();


    private static final String PREFERENCES_TEST = "com.nmbs.test.app";

    public TrainIconsService(Context context){
        this.context = context;
    }

    public List<TrainIcon> getTrainIcons(){
        MasterDataService masterDataService = new MasterDataService();
        List<TrainIcon> trainIcons = masterDataService.getTrainIcons(context);
        return trainIcons;
    }

    public TrainIcon getTrainIcon(String trainType){
        TrainIconsDatabaseService trainIconsDatabaseService = new TrainIconsDatabaseService(context);
        List<TrainIcon> trainIcons = trainIconsDatabaseService.selectTrainIcons();
        for(TrainIcon trainIcon : trainIcons){
            if(trainIcon != null) {
                for (String icon : trainIcon.getLinkedTrainBrands()) {
                    if (icon != null) {
                        icon = icon.replace(" ", "");
                        if(icon.equalsIgnoreCase(trainType)){
                            return trainIcon;
                        }
                    }
                }
                for (String icon : trainIcon.getLinkedHafasCodes()) {
                    if (icon != null) {
                        icon = icon.replace(" ", "");
                        if(icon.equalsIgnoreCase(trainType)){
                            return trainIcon;
                        }
                    }
                }
                for (String icon : trainIcon.getLinkedTariffGroups()) {
                    if (icon != null) {
                        icon = icon.replace(" ", "");
                        if(icon.equalsIgnoreCase(trainType)){
                            return trainIcon;
                        }
                    }
                }
            }
        }
        return null;
    }

    public Bitmap getTrainIcon(TrainIcon trainIcon){
        String name = "";
        Bitmap mBitmap = null;
        String imageUrl = ImageUtil.convertImageExtension(trainIcon.getImageHighResolution());
        String fullImageUrl = context.getString(R.string.server_url_host) + imageUrl;
        if(imageUrl != null && imageUrl.length() > 1){
            name = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        }
        if (FileManager.getInstance().hasExternalStoragePrivateFile(context, null, name)) {
            //Log.d(TAG, "File is exist");
            BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inTempStorage = new byte[16 * 1024];
            options.inSampleSize = 1;
            mBitmap = BitmapFactory.decodeFile(FileManager.getInstance().getExternalStoragePrivateFilePath(context, null, name), options);

        }
        return mBitmap;
    }
}
