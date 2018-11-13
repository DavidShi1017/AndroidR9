package com.cflint.model;

import java.io.Serializable;

/**
 * Created by Richard on 2/22/16.
 */
public class CallCenterSubscriberNumber implements Serializable {
    private static final long serialVersionUID = 1L;
    private int drawableId;
    private String label;
    public CallCenterSubscriberNumber(int drawableId, String label){
        this.drawableId = drawableId;
        this.label = label;
    }

    public int getDrawableId(){
        return this.drawableId;
    }

    public String getLabel(){
        return this.label;
    }
}
