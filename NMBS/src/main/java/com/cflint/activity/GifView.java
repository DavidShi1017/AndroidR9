package com.cflint.activity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;



public class GifView extends View {
	private long movieStart;
	private Movie movie;
	private int width ;
	private int height;
	public GifView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		//movie = Movie.decodeStream(getResources().openRawResource(
			//	R.drawable.ic_showseat));
		//Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_showseat);
		//width = bitmap.getWidth();
		//height = bitmap.getHeight();
	}
	
	public GifView(Context context) {
		super(context);
		//movie = Movie.decodeStream(getResources().openRawResource(
		//		R.drawable.ic_showseat));
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		long curTime = android.os.SystemClock.uptimeMillis();

		if (movieStart == 0) {
			movieStart = curTime;
		}
		if (movie != null) {
			int duraction = movie.duration();
			int relTime = (int) ((curTime - movieStart) % duraction);
			movie.setTime(relTime);
			movie.draw(canvas, 0, 0);
			invalidate();
		}
		super.onDraw(canvas);
	}

	@Override
	public void setLayoutParams(LayoutParams params) {
		LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(width, height);
		super.setLayoutParams(layout);
	}

	
}
