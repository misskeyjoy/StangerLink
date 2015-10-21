package angelbeats.com.game2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/*
 * ʵ�ַ�ʽ��
 * ����������bitmap�ķ�ʽ��
 * �ײ�����bitmap��floorBitmap, surfaceBitmap;���bitmapΪ͸��ɫ������Ḳ�ǵ��ײ�bitmap��ͼ��
 * ��ǰ��ͼ�����ڱ��bitmap��surfaceBitmap������ı仭�ʣ��򽫵�ǰsurfaceBitmap�����ݻ��Ƶ��ײ�bitmap��floorBitmap
 * ���ѡ����Ƥ������Ҫ�ڵײ�bitmap�Ͻ��л��ƣ�
 * �鿴ԭͼƬ��Ҳ�ǽ�ͼƬ���Ƶ��ײ�bitmap
 */
public class MyView extends View {
	private DrawBS drawBS = null;
	private Point evevtPoint;
	private Bitmap floorBitmap, surfaceBitmap;// �ײ�����bitmap
	private Canvas floorCanvas, surfaceCanvas;// bitmap��Ӧ��canvas
	private Context context;
	private boolean isEraser = false;
	private String filepath;
	private FileUtils fileUtils;
	Bitmap newbm;

	@SuppressLint("ParserError")
	public MyView(Context context) {
		super(context);
		this.context = context;
		// ��ʼ��drawBS����drawBSĬ��ΪDrawPath��
		drawBS = new DrawScrawl();
		evevtPoint = new Point();

		// �ײ�bitmap��canvas��
		floorBitmap = Bitmap.createBitmap(800,1200, Bitmap.Config.ARGB_8888);
		floorCanvas = new Canvas(floorBitmap);
		floorCanvas.drawColor(Color.WHITE);
		// ����bitmap�����ڵײ�bitmap֮�ϣ����ڸ�ֵ���Ƶ�ǰ��������ͼ�Σ���Ҫ����Ϊ͸�������򸲸ǵײ�bitmap
		surfaceBitmap = Bitmap.createBitmap(800, 1200, Bitmap.Config.ARGB_8888);
		surfaceCanvas = new Canvas(surfaceBitmap);
		surfaceCanvas.drawColor(Color.TRANSPARENT);
	}

	@SuppressLint("WrongCall")
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// ���ײ�bitmap��ͼ�λ��Ƶ�������

		canvas.drawBitmap(floorBitmap, 0, 0, null);
		int color = DrawBS.color;
		/*
		 * ���ݱ��Canvas������ ������Ӧ��ͼ�����෽��,�ڱ��bitmap��ʹ��surfaceCanvas���л�ͼ
		 */
		drawBS.onDraw(surfaceCanvas, color);
		canvas.drawBitmap(surfaceBitmap, 0, 0, null);

	}

	// �����¼���������Ӧ�Ļ�ͼ��������в���
	@SuppressLint("WrongCall")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		evevtPoint.set((int) event.getX(), (int) event.getY());

		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:
			drawBS.onTouchDown(evevtPoint);
			break;

		case MotionEvent.ACTION_MOVE:
			drawBS.onTouchMove(evevtPoint);
			/*
			 * �϶������в�ͣ�Ľ�bitmap����ɫ����Ϊ͸������ձ��bitmap�� ���������϶����̵Ĺ켣���ử����
			 */
			surfaceBitmap.eraseColor(Color.TRANSPARENT);
			invalidate();
			break;

		case MotionEvent.ACTION_UP:
			drawBS.onTouchUp(evevtPoint);
			// �������ѡ����ͼ�Σ�����Ҫ�����bitmap�ϵ�ͼ����Ƶ��ײ�bitmap�Ͻ��б���
			floorCanvas.drawBitmap(surfaceBitmap, 0, 0, null);
			break;
		default:
			break;
		}
		return true;
	}

	// ѡ��ͼ�Σ�ʵ������Ӧ����
	public void setDrawTool(int i) {	
		drawBS = new DrawScrawl();
		
	}

	
	// ��ͼƬ�����ڴ濨
	public void savePicture(String draw_name,MyView myview) {
	   fileUtils = new FileUtils(context);
		if (myview == null)
			return;
		myview.setDrawingCacheEnabled(true);
		Bitmap b = Bitmap.createBitmap(myview.getDrawingCache()); // ����һ��DrawingCache�Ŀ�������ΪDrawingCache�õ���λͼ�ڽ��ú�ᱻ����

		if (b != null) {
			try {
				fileUtils.savaBitmap(draw_name+".jpg", b);
				
				Toast.makeText(context, "����ͼƬ�ɹ�", Toast.LENGTH_LONG).show();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(context, "����ͼƬʧ��", Toast.LENGTH_LONG).show();
			}
			if (!b.isRecycled())
				b.recycle();
			b = null;
			System.gc();
		}
		myview.setDrawingCacheEnabled(false); // ����DrawingCahce�����Ӱ������
	}
	public void getPath () {
	  fileUtils.getSavaBitmapPath();
	}
}
