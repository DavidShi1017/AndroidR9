package com.nmbs.async;


import java.io.InputStream;
import java.lang.ref.SoftReference;


import java.util.HashMap;
import java.util.Map;

import com.nmbs.util.FileManager;
import com.nmbs.util.HttpRetriever;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.View;

/**
 * This class is used async download images. An alternative is to manually spawn
 * a new Thread for each image. In addition we should use Handlers to deliver
 * the downloaded images to the UI thread.
 * 
 * @author David
 * 
 */


public class AsyncImageLoader {
	private Map<String, SoftReference<Bitmap>> imageCache;
	// private TrustManager[] TRUST_MANAGER = { new NaiveTrustManager() };
	//private final static String TAG = AsyncImageLoader.class.getSimpleName();
	private static AsyncImageLoader instance;
	
	

	public static AsyncImageLoader getInstance() {
		if (instance == null) {
			instance = new AsyncImageLoader();

		}
		return instance;
	}

	private AsyncImageLoader() {
		imageCache = new HashMap<String, SoftReference<Bitmap>>();
	}

	/**
	 * Providing an anonymous implementation of the ImageCallback interface
	 * 
	 * @param imageUrl
	 * @param callback
	 * @return
	 */
	public Bitmap loadDrawable(final Context context,
			final String fullImageUrl, final String imageUrl, final View view, final String folder,
			final ImageCallback callback) {
		if (imageCache.containsKey(imageUrl)) {
			SoftReference<Bitmap> softReference = imageCache.get(imageUrl);
			Bitmap drawable = softReference.get();
			if (drawable != null) {
				callback.imageLoaded(drawable, imageUrl, view);

				return drawable;
			}
		}
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				callback.imageLoaded((Bitmap) msg.obj, imageUrl, view);
			}
		};
		new Thread() {
			public void run() {

				Bitmap drawable = loadImageFromUrl(context, fullImageUrl, imageUrl, folder);

				imageCache.put(imageUrl, new SoftReference<Bitmap>(drawable));
				Message message = handler.obtainMessage(0, drawable);
				handler.sendMessage(message);
			}
		}.start();
		return null;
	}

	private Bitmap loadImageFromUrl(Context context, String fullImageUrl, final String imageUrl, String folder) {
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			//options.inTempStorage = new byte[16 * 1024];
			options.inSampleSize = 1;
			//options.inJustDecodeBounds = true;
			Bitmap mBitmap = null;
			String name = "";
			if(imageUrl != null && imageUrl.length() > 1){
				name = imageUrl.substring(imageUrl.lastIndexOf("/") + 1) + ".png";
			}
			//Log.e("HOMEBANNER", "name..." + name);

			if (imageUrl != null && imageUrl.length() > 0) {
				if (FileManager.getInstance().hasExternalStoragePrivateFile(context, folder, name)) {
					//Log.e("HOMEBANNER", "File is exist");
					mBitmap = BitmapFactory.decodeFile(FileManager.getInstance().getExternalStoragePrivateFilePath(context, folder, name), options);
				} else {
					//Log.e("HOMEBANNER", "fullImageUrl......." + fullImageUrl);
					InputStream inputStream = HttpRetriever.getInstance().retrieveStream(fullImageUrl);

					FileManager.getInstance().createExternalStoragePrivateFile(context, inputStream, folder, name);
					mBitmap = BitmapFactory.decodeFile(FileManager.getInstance().getExternalStoragePrivateFilePath(context, folder, name), options);
				}
			}

			return mBitmap;

		} catch (Exception e) {
			//Log.e("loadImageFromUrl", "Load imag from url Error:"+imageUrl , e);
			return null;
		}
	}
	public interface ImageCallback {
		public void imageLoaded(Bitmap imageDrawable, String imageUrl, View view);
	}
}
