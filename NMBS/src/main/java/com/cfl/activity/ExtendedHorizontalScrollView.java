package com.cfl.activity;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;

public class ExtendedHorizontalScrollView extends HorizontalScrollView {
	private IScrollStateListener scrollStateListener;
	private boolean flag = true;
	Handler listenHandler;
	Handler endHandler;
	private boolean scrollAble = true;
	private boolean zeroFlag = true;
	private boolean isScrolled = false;
	Handler buttonContrlHandler = null;

	public ExtendedHorizontalScrollView(Context context) {
		super(context);
	}

	public ExtendedHorizontalScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public Handler getButtonContrlHandler() {
		return this.buttonContrlHandler;
	}

	public void setIsScrolled(boolean isScrolled) {
		this.isScrolled = isScrolled;
	}

	public ExtendedHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		prepare();
		initHandler();
	}

	public void setScrollAble(boolean scrollAble) {
		this.scrollAble = scrollAble;
	}

	public void initHandler() {
		buttonContrlHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 123:
					arrowScroll(View.FOCUS_RIGHT);
					updateLeftAndRightButton();
					break;

				case 321:
					arrowScroll(View.FOCUS_LEFT);
					updateLeftAndRightButton();
					break;
				default:
					break;
				}
			}
		};
	}

	private void prepare() {
		if (scrollStateListener != null && flag) {
			scrollStateListener.initScroll();
			endHandler = new Handler();
			listenHandler = new Handler();
			flag = false;
		}
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (scrollStateListener != null) {
			if (scrollAble == true) {
			if (l == 0) {
				scrollStateListener.onScrollMostLeft();
			}else{
				scrollStateListener.onScrollFromMostLeft();
			}
			
			int mostRightL = this.getChildAt(0).getWidth() - getWidth();
			if (l >= mostRightL) {
				scrollStateListener.onScrollMostRight();
			}

			if (oldl >= mostRightL && l < mostRightL) {
				scrollStateListener.onScrollFromMostRight();
			}
		  }else {
				scrollStateListener.noScroll();
			}
		}

	}

	public void updateLeftAndRightButton() {
		listenHandler.post(new Runnable() {
			int lastScrollX;

			public void run() {
				while (true) {
					lastScrollX = getScrollX();
					System.out.println(getScrollX() == lastScrollX);
					if (getScrollX() == lastScrollX) {
						endScroll();
						return;
					}
				}
			}

		});

	}

	private void endScroll() {
		endHandler.post(new Runnable() {

			public void run() {
				int x = getScrollX();
				if (scrollAble == true) {
					if (x == 0) {
						scrollStateListener.onScrollMostLeft();
					} else {
						scrollStateListener.onScrollFromMostLeft();
					}
					if (getChildAt(0).getWidth() - getWidth() != 0) {
						if (x == getChildAt(0).getWidth() - getWidth()) {
							scrollStateListener.onScrollMostRight();
						} else {
							scrollStateListener.onScrollFromMostRight();
						}
					} else {
						if (isScrolled != true) {
							if (zeroFlag == true) {
								scrollStateListener.onScrollMostRight();
								zeroFlag = false;
							} else {
								scrollStateListener.onScrollMostLeft();
								zeroFlag = true;
							}
						} else {
							isScrolled = false;
						}
					}
				} else {
					scrollStateListener.noScroll();
				}

			}
		});

	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return super.onTouchEvent(ev);
	}

	public interface IScrollStateListener {
		void onScrollMostLeft();

		void onScrollFromMostLeft();

		void onScrollMostRight();

		void onScrollFromMostRight();

		void noScroll();

		void initScroll();
	}

	public void setScrollStateListener(IScrollStateListener iScrollStateListener) {
		scrollStateListener = iScrollStateListener;
	}

}
