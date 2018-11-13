package com.cflint.services.impl;

import android.content.Context;


import com.cflint.listeners.RatingListener;

import com.cflint.util.GoogleAnalyticsUtil;
import com.cflint.util.RatingUtil;
import com.cflint.util.TrackerConstant;

/**
 * Control Login Data and View communicate.
 */ 
public class RatingService{

	private final static String TAG = RatingService.class.getSimpleName();
	private Context applicationContext;
	private RatingListener ratingListener;
	public enum RatingView {
		QuestionDoYouLikeApp, QuestionWantToSubmitReview, QuestionSendFeedback, NeverShowAgain
	}
	//private LoginInfo loginInfo;

	public RatingService(Context context){
		this.applicationContext = context;
	}

	public void setRatingListener(RatingListener ratingListener) {
		this.ratingListener = ratingListener;
	}


	public void changeRatingView(RatingService.RatingView whichView, boolean yes){
		if (whichView == RatingService.RatingView.QuestionDoYouLikeApp){
			if (yes){

				this.ratingListener.changeRatingView(RatingService.RatingView.QuestionWantToSubmitReview);
			}else {
				this.ratingListener.changeRatingView(RatingView.QuestionSendFeedback);
			}

		}else if(whichView == RatingService.RatingView.QuestionWantToSubmitReview){
			if (yes){

				this.ratingListener.openAppStore();
				//Log.d(TAG, "Open app store.....");
			}else{

			}
			RatingUtil.saveNeverShowAgain(applicationContext, true);
			this.ratingListener.changeRatingView(RatingView.NeverShowAgain);
		}else{
			GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.HOME,TrackerConstant.HOME_IMPRESSION_FEEDBACK,"");
			RatingUtil.saveNeverShowAgain(applicationContext, true);
			this.ratingListener.changeRatingView(RatingView.NeverShowAgain);
			if (yes){
				GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.HOME,TrackerConstant.HOME_SELECT_FEEDBACK_ACTION, TrackerConstant.HOME_SELECT_FEEDBACK_ACCEPT);
				this.ratingListener.sendEmail();
				//Log.d(TAG, "Send Email.....");
			}else{
				GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.HOME,TrackerConstant.HOME_SELECT_FEEDBACK_ACTION, TrackerConstant.HOME_SELECT_FEEDBACK_DECLINE);
			}
		}
	}
}
