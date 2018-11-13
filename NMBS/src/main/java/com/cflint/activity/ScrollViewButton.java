package com.cflint.activity;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class ScrollViewButton extends ImageView {
	private String deriction;
	private ExtendedHorizontalScrollView horizontalScrollView;
	private boolean flag = true;
	private Handler buttonContrlHandler;
	public ScrollViewButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public ScrollViewButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public void setHandler(Handler handler){
		this.buttonContrlHandler = handler;
	}
	public ScrollViewButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
	    
		}
	
	public void setHorizontalScrollView(ExtendedHorizontalScrollView horizontalScrollView){
		this.horizontalScrollView = horizontalScrollView;
	}

	public void setDeriction(String deriction){
		this.deriction = deriction;
	}

/*	@Override
	public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
        	setHandler(horizontalScrollView.getButtonContrlHandler());
        	   	final String buttonAttr = this.deriction; 
                flag = true;
               Thread thread = new Thread(new Runnable() {
                        
                        public void run() {
                                while(flag){
                                        try {
                                                Thread.sleep(100);
                                                if(buttonAttr.equals("left")){
                                                	buttonContrlHandler.sendEmptyMessage(321);
                                                }else{
                                                	 buttonContrlHandler.sendEmptyMessage(123);
                                                }
                                        } catch (InterruptedException e) {
                                                e.printStackTrace();
                                        }
                                }
                        }
                });
                thread.start();
                break;

        case MotionEvent.ACTION_UP:
                flag = false;
                break;
        }
        
        return true;
	}*/

	
}
