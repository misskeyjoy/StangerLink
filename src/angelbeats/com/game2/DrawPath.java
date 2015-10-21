package angelbeats.com.game2;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

/**
 * 
 * @author AngelBeats
 *
 */
public class DrawPath extends DrawBS {

	private Path path = new Path();
	private float mX, mY;

	public DrawPath() {
		
	}
	
	//如果选择橡皮，则需要给画笔重新赋值
	public DrawPath(int i) {

		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setColor(Color.RED);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.SQUARE);
		paint.setStrokeWidth(15);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

	}
	

	public void onTouchDown(Point point) {
		path.moveTo(point.x, point.y);
		mX = point.x;
		mY = point.y;
	}

	public void onTouchMove(Point point) {
		float dx = Math.abs(point.x - mX);
		float dy = Math.abs(point.y - mY);
		if (dx > 0 || dy > 0) {
			path.quadTo(mX, mY, (point.x + mX) / 2, (point.y + mY) / 2);
			mX = point.x;
			mY = point.y;
		} else if (dx == 0 || dy == 0) {
			path.quadTo(mX, mY, (point.x + 1 + mX) / 2, (point.y + 1 + mY) / 2);
			mX = point.x + 1;
			mY = point.y + 1;
		}
	}

	
	public void onDraw(Canvas canvas,int color) {
		// TODO Auto-generated method stub
		paint.setColor(color);
		canvas.drawPath(path, paint);
	}

}


//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.Path;
//import android.util.Log;
//import android.view.MotionEvent;
//
///*
// * 涂鸦
// */
//public class DrawPath extends MyDraw {
//
//	private Path mPath;
//	private float mX, mY;
//
//	public DrawPath(Context context) {
//		super(context);
//		// TODO Auto-generated constructor stub
//		// 实例化
//		mPath = new Path();
//
//	}
//
//	@Override
//	protected void onDraw(Canvas canvas) {
//		// TODO Auto-generated method stub
//		super.onDraw(canvas);
//		canvas.drawPath(mPath, paint);
//	}
//
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		float x = event.getX();
//		float y = event.getY();
//		switch (event.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			onTouchDown(x, y);
//			invalidate();
//			break;
//		case MotionEvent.ACTION_MOVE:
//			onTouchMove(x, y);
//			invalidate();
//			break;
//		case MotionEvent.ACTION_UP:
//			onTouchUp(x, y);
//			invalidate();
//			break;
//		default:
//		}
//		return true;
//	}
//
//	private void onTouchDown(float x, float y) {
//		Log.e("paint----", "ontouch");
//		mPath.reset();
//		mPath.moveTo(x, y);
//		mX = x;
//		mY = y;
//	}
//
//	private void onTouchMove(float x, float y) {
//		Log.e("paint---", "onmove");
//		float dx = Math.abs(x - mX);
//		float dy = Math.abs(y - mY);
//		if (dx > 0 || dy > 0) {
//			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
//			mX = x;
//			mY = y;
//		} else if (dx == 0 || dy == 0) {
//			mPath.quadTo(mX, mY, (x + 1 + mX) / 2, (y + 1 + mY) / 2);
//			mX = x + 1;
//			mY = y + 1;
//		}
//	}
//
//	private void onTouchUp(float x, float y) {
//		Log.e("paint----.", "onmove");
//		// mPath.lineTo(mX, mY);
//		canvas.drawPath(mPath, paint);
//		mPath.reset();
//	}
//
//}

//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.Path;
//import android.util.Log;
//import android.view.MotionEvent;
//
///*
// * 涂鸦
// */
//public class DrawPath extends MyDraw {
//
//	private Path mPath;
//	private float mX, mY;
//
//	public DrawPath(Context context) {
//		super(context);
//		// TODO Auto-generated constructor stub
//		// 实例化
//		mPath = new Path();
//
//	}
//
//	@Override
//	protected void onDraw(Canvas canvas) {
//		// TODO Auto-generated method stub
//		super.onDraw(canvas);
//		canvas.drawPath(mPath, paint);
//	}
//
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		float x = event.getX();
//		float y = event.getY();
//		switch (event.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			onTouchDown(x, y);
//			invalidate();
//			break;
//		case MotionEvent.ACTION_MOVE:
//			onTouchMove(x, y);
//			invalidate();
//			break;
//		case MotionEvent.ACTION_UP:
//			onTouchUp(x, y);
//			invalidate();
//			break;
//		default:
//		}
//		return true;
//	}
//
//	private void onTouchDown(float x, float y) {
//		Log.e("paint----", "ontouch");
//		mPath.reset();
//		mPath.moveTo(x, y);
//		mX = x;
//		mY = y;
//	}
//
//	private void onTouchMove(float x, float y) {
//		Log.e("paint---", "onmove");
//		float dx = Math.abs(x - mX);
//		float dy = Math.abs(y - mY);
//		if (dx > 0 || dy > 0) {
//			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
//			mX = x;
//			mY = y;
//		} else if (dx == 0 || dy == 0) {
//			mPath.quadTo(mX, mY, (x + 1 + mX) / 2, (y + 1 + mY) / 2);
//			mX = x + 1;
//			mY = y + 1;
//		}
//	}
//
//	private void onTouchUp(float x, float y) {
//		Log.e("paint----.", "onmove");
//		// mPath.lineTo(mX, mY);
//		canvas.drawPath(mPath, paint);
//		mPath.reset();
//	}
//
//}
