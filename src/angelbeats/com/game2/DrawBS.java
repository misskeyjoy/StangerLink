package angelbeats.com.game2;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;

/*
 * ��view�ࡣ
 * ����������һ���ӿڣ����඼�̳�ʵ�����������Щ����
 * @author AngelBeats
 */
public class DrawBS {
	
	public int downState;
	public int moveState;
	public Point downPoint = new Point();
	public Point movePoint = new Point();
	public Point eventPoint = new Point();
	public Paint paint;//��������
	public static int color = Color.RED;
	
	public DrawBS() {
		// ���û���
		paint = new Paint();
		paint.setStyle(Style.STROKE);// ���÷����
		paint.setStrokeWidth(5);// �ʿ�5����
		paint.setColor(color);// Ĭ������Ϊ���
		paint.setAntiAlias(true);// ��ݲ���ʾ
		
	}
	
	public void onTouchDown(Point point) {
		
	}
	 
	public void onTouchMove(Point point) {
		
	}
	
	public void onTouchUp(Point point) {
		
	}
	
	
	public void onDraw(Canvas canvas, int color) {
		// TODO Auto-generated method stub
	};
	
	/*
	 *  ������ɫ
	 * */
	public void setColor(int color){
		DrawBS.color=color;
	}
}