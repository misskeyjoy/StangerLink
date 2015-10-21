package angelbeats.com.game2;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;
/**
 * Ϳѻ
 * @author AngelBeats
 *
 */
public class DrawScrawl extends DrawBS {

	private Path path = new Path();
	private float mX, mY;

	public DrawScrawl() {

	}

	public DrawScrawl(int i) {

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

	public void onDraw(Canvas canvas, int color) {
		paint.setColor(color);
		canvas.drawPath(path, paint);
	}

}
