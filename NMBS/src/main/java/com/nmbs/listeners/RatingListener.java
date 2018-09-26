package com.nmbs.listeners;

import com.nmbs.services.impl.RatingService;

/**
 * Created by shig on 2015/12/17.
 */
public interface RatingListener {
    void changeRatingView(RatingService.RatingView whichView);
    void openAppStore();
    void sendEmail();
}
