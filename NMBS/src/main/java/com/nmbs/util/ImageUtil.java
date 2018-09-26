package com.nmbs.util;

import java.io.InputStream;

import org.apache.commons.lang.StringUtils;


import android.content.Context;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;



import com.nmbs.R;


public class ImageUtil {

	public static int getTrainTypeImageId(String trainType) {

		if (trainType != null) {
			trainType = trainType.trim();
		}

		if (TrainTypeConstant.TRAIN_TGD.equalsIgnoreCase(trainType) 
				|| TrainTypeConstant.TRAIN_TGV.equalsIgnoreCase(trainType) 
				|| TrainTypeConstant.TRAIN_RHT.equalsIgnoreCase(trainType)
				|| TrainTypeConstant.TRAIN_TGV_FR_DE.equalsIgnoreCase(trainType)
				|| TrainTypeConstant.TRAIN_TGV_DE_FR.equalsIgnoreCase(trainType)
				|| TrainTypeConstant.TRAIN_LYRIA.equalsIgnoreCase(trainType)) {
			
			return R.drawable.ic_train_type_tgv;
			
		} else if (TrainTypeConstant.TRAIN_IC.equalsIgnoreCase(trainType)
				|| TrainTypeConstant.TRAIN_BENELUX.equalsIgnoreCase(trainType)
				|| TrainTypeConstant.TRAIN_INTERCITY.equalsIgnoreCase(trainType)
				|| TrainTypeConstant.TRAIN_ICD.equalsIgnoreCase(trainType)) {
			
			return R.drawable.ic_train_type_ic;
			
		} else if (TrainTypeConstant.TRAIN_THA.equalsIgnoreCase(trainType)) {
			
			return R.drawable.ic_train_type_thalys;
			
		} else if (TrainTypeConstant.TRAIN_ICE.equalsIgnoreCase(trainType) 
				|| TrainTypeConstant.TRAIN_IXB.equalsIgnoreCase(trainType) 
				|| TrainTypeConstant.TRAIN_IXK.equalsIgnoreCase(trainType) 
				|| TrainTypeConstant.TRAIN_RHI.equalsIgnoreCase(trainType)) {
			
			return R.drawable.ic_train_type_ice;
			
		} else if(StringUtils.equalsIgnoreCase(trainType, TrainTypeConstant.TRAIN_EST)
				|| StringUtils.equalsIgnoreCase(trainType, TrainTypeConstant.TRAIN_EUROSTAR)
				|| StringUtils.equalsIgnoreCase(trainType, TrainTypeConstant.TRAIN_EUR)){
			
			return R.drawable.ic_train_type_eurostar;
			
		}/* else if (StringUtils.equalsIgnoreCase(trainType, TrainTypeConstant.TRAIN_IR)) {

			return R.drawable.ic_train_type_ir;

		}else if (StringUtils.equalsIgnoreCase(trainType, TrainTypeConstant.TRAIN_EC)
				|| StringUtils.equalsIgnoreCase(trainType, TrainTypeConstant.TRAIN_EUROCITY)) {

			return R.drawable.ic_train_type_ec;
		}else if (StringUtils.equalsIgnoreCase(trainType, TrainTypeConstant.TRAIN_BUS)
				|| StringUtils.equalsIgnoreCase(trainType, TrainTypeConstant.TRAIN_FB)
				|| StringUtils.equalsIgnoreCase(trainType, TrainTypeConstant.TRAIN_EXB)
				|| StringUtils.equalsIgnoreCase(trainType, TrainTypeConstant.TRAIN_Express_Bus)
				|| StringUtils.equalsIgnoreCase(trainType, TrainTypeConstant.TRAIN_BSV)) {

			return R.drawable.ic_train_type_bus;
		}*/
		
		return R.drawable.icon_train;
		
	}
	
	public static int getLegTrainTypeImageId(String trainType,boolean isTrainLeg,boolean hasWarning){
		if(isTrainLeg == false){
			return R.drawable.ic_event;
		}else{
			if(hasWarning == true){
				return R.drawable.ic_train_type_warn;
			}else{
				return getTrainTypeImageId(trainType);
			}
		}
		
	}
	

	// 合成带水印的图片
	public final static Bitmap createBitmap(Bitmap src, Bitmap watermark) {
		if (src == null) {
			return null;
		}

		int w = src.getWidth();
		int h = src.getHeight();
		int ww = watermark.getWidth();
		// int wh = watermark.getHeight();

		Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
		Canvas cv = new Canvas(newb);

		cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src

		cv.drawBitmap(watermark, w - ww - 2, 2, null);// 在src的右上角画入水印

		cv.save(Canvas.ALL_SAVE_FLAG);// 保存

		cv.restore();// 存储
		return newb;
	}

	// 创建一个带有自定义自符的水印
	public static final Bitmap getMarkWithText(Bitmap watermark, String text) {
		int ww = watermark.getWidth();
		int wh = watermark.getHeight();

		// 创建一个和水印文件相同尺寸的bitmap
		Bitmap newb = Bitmap.createBitmap(ww, wh, Config.ARGB_8888);

		Canvas cv = new Canvas(newb);

		cv.drawBitmap(watermark, 0, 0, null);

		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.WHITE);
		paint.setTextSize(15);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setAntiAlias(true);
		// 根据字符长度设置画字符的位置
		if (text.length() >= 3) {
			cv.drawText("N", ww / 2 - 4, wh / 2 + 4, paint);
		} else if (text.length() >= 2) {
			cv.drawText(text, ww / 2 - 6, wh / 2 + 4, paint);
		} else {
			cv.drawText(text, ww / 2 - 3, wh / 2 + 4, paint);
		}

		cv.save(Canvas.ALL_SAVE_FLAG);

		cv.restore();

		return newb;
	}

	
	public static String convertImageExtension(String imageUrl){
		String url = "";
		
		if (imageUrl != null) {
			url = imageUrl.replaceAll("ashx", "png");
			/*if(!url.contains("png")){
				url = url + ".png";
			}*/
/*			if(imageUrl.indexOf("?") != -1){
				url = imageUrl.substring(0, imageUrl.indexOf("?") - 1);
				url = url.replaceAll("ashx", "png");
			}else {
				
			}*/
		}
		if(url.indexOf("?") != -1){
			url = url.substring(0, url.indexOf("?"));			
		}
		return url; 
	}
	
	public static Bitmap compressImage(Context context, int resId) {

		InputStream is = context.getResources().openRawResource(resId);
		BitmapFactory.Options options = new BitmapFactory.Options();

		options.inJustDecodeBounds = false;

		options.inSampleSize = 1; // width，hight设为原来的十分一
		options.inPreferredConfig = Config.ARGB_8888;
		Bitmap btp = BitmapFactory.decodeStream(is, null, options);
		return btp;
	}
}
