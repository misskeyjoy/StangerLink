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
 * 实现方式：
 * 采用了两层bitmap的方式：
 * 底层与表层bitmap：floorBitmap, surfaceBitmap;表层bitmap为透明色，否则会覆盖掉底层bitmap的图形
 * 当前画图东都在表层bitmap：surfaceBitmap。如果改变画笔，则将当前surfaceBitmap的内容绘制到底层bitmap：floorBitmap
 * 如果选择橡皮，则需要在底层bitmap上进行绘制，
 * 查看原图片，也是讲图片绘制到底层bitmap
 */
public class MyView extends View {
	private DrawBS drawBS = null;
	private Point evevtPoint;
	private Bitmap floorBitmap, surfaceBitmap;// 底层与表层bitmap
	private Canvas floorCanvas, surfaceCanvas;// bitmap对应的canvas
	private Context context;
	private boolean isEraser = false;
	private String filepath;
	private FileUtils fileUtils;
	Bitmap newbm;

	@SuppressLint("ParserError")
	public MyView(Context context) {
		super(context);
		this.context = context;
		// 初始化drawBS，即drawBS默认为DrawPath类
		drawBS = new DrawScrawl();
		evevtPoint = new Point();

		// 底层bitmap与canvas，
		floorBitmap = Bitmap.createBitmap(800,1200, Bitmap.Config.ARGB_8888);
		floorCanvas = new Canvas(floorBitmap);
		floorCanvas.drawColor(Color.WHITE);
		// 表面bitmap。置于底层bitmap之上，用于赋值绘制当前的所画的图形；需要设置为透明，否则覆盖底部bitmap
		surfaceBitmap = Bitmap.createBitmap(800, 1200, Bitmap.Config.ARGB_8888);
		surfaceCanvas = new Canvas(surfaceBitmap);
		surfaceCanvas.drawColor(Color.TRANSPARENT);
	}

	@SuppressLint("WrongCall")
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// 将底层bitmap的图形绘制到主画布

		canvas.drawBitmap(floorBitmap, 0, 0, null);
		int color = DrawBS.color;
		/*
		 * 传递表层Canvas参数。 调用相应画图工具类方法,在表层bitmap上使用surfaceCanvas进行绘图
		 */
		drawBS.onDraw(surfaceCanvas, color);
		canvas.drawBitmap(surfaceBitmap, 0, 0, null);

	}

	// 触摸事件。调用相应的画图工具类进行操作
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
			 * 拖动过程中不停的将bitmap的颜色设置为透明（清空表层bitmap） 否则整个拖动过程的轨迹都会画出来
			 */
			surfaceBitmap.eraseColor(Color.TRANSPARENT);
			invalidate();
			break;

		case MotionEvent.ACTION_UP:
			drawBS.onTouchUp(evevtPoint);
			// 如果重新选择了图形，则需要将表层bitmap上的图像绘制到底层bitmap上进行保存
			floorCanvas.drawBitmap(surfaceBitmap, 0, 0, null);
			break;
		default:
			break;
		}
		return true;
	}

	// 选择图形，实例化相应的类
	public void setDrawTool(int i) {	
		drawBS = new DrawScrawl();
		
	}

	
	// 将图片存入内存卡
	public void savePicture(String draw_name,MyView myview) {
	   fileUtils = new FileUtils(context);
		if (myview == null)
			return;
		myview.setDrawingCacheEnabled(true);
		Bitmap b = Bitmap.createBitmap(myview.getDrawingCache()); // 创建一个DrawingCache的拷贝，因为DrawingCache得到的位图在禁用后会被回收

		if (b != null) {
			try {
				fileUtils.savaBitmap(draw_name+".jpg", b);
				
				Toast.makeText(context, "保存图片成功", Toast.LENGTH_LONG).show();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(context, "保存图片失败", Toast.LENGTH_LONG).show();
			}
			if (!b.isRecycled())
				b.recycle();
			b = null;
			System.gc();
		}
		myview.setDrawingCacheEnabled(false); // 禁用DrawingCahce否则会影响性能
	}
	public void getPath () {
	  fileUtils.getSavaBitmapPath();
	}
}
