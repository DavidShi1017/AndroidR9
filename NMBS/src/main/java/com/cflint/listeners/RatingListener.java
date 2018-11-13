package com.cflint.listeners;

import com.cflint.services.impl.RatingService;

/**
 * Created by shig on 2015/12/17.
 */
public interface RatingListener {
    void changeRatingView(RatingService.RatingView whichView);
    void openAppStore();
    void sendEmail();
}
