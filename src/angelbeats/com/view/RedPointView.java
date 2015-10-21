package angelbeats.com.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.TextView;

public class RedPointView extends TextView {
	private static final int DEFAULT_MARGIN_DIP = 5;
	private static final int DEFAULT_PADDING_DIP = 5;
	private int pointMargin;
	private int paddingPixels;
	
	private ShapeDrawable pointBg;

	private int content = 0;
	private int colorBg = Color.RED;
	private int colorContent = Color.WHITE;
	private int left_right = Gravity.RIGHT;
	private int top_bottom = Gravity.TOP;
	private int sizeContent = 15;
	private int sizeBg = (int) (sizeContent * 1.5);
	private boolean isShown;

	private Context context;
	private View orginView;

	public RedPointView(Context context, View target) {
		super(context);
		this.context = context; 
		this.orginView = target;
		init();
	}


	public void setContent(int content) {
		this.content = content;
		setText(content + "");
	}


	public void setColorContent(int colorContent) {
		this.colorContent = colorContent;
		setTextColor(colorContent);
	}

	public void setColorBg(int colorBg) {
		this.colorBg = colorBg;
		pointBg = getDefaultBackground();
		setBackgroundDrawable(pointBg);
	}

	public void setPosition(int left_right, int top_bottom) {
		this.left_right = left_right;
		this.top_bottom = top_bottom;
		setPositionParams(left_right, top_bottom);
	}

	public void setSizeContent(int sizeContent) {
		this.sizeContent = sizeContent;
		setTextSize(sizeContent);
		this.sizeBg = (int) (sizeContent * 1.5);
	}

	public void show() {
		this.setVisibility(View.VISIBLE);
		isShown = true;
	}

	public void hide() {
		this.setVisibility(View.GONE);
		isShown = false;
	}

	private ShapeDrawable getDefaultBackground() {
		int r = sizeBg;
		float[] outerR = new float[] { r, r, r, r, r, r, r, r };
		RoundRectShape rectShape = new RoundRectShape(outerR, null, null);
		ShapeDrawable shap = new ShapeDrawable(rectShape);
		shap.getPaint().setColor(colorBg);

		return shap;
	}

	private void setPositionParams(int left_right, int top_bottom) {
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = left_right | top_bottom;

		switch (left_right) {
		case Gravity.LEFT:
			switch (top_bottom) {
			case Gravity.TOP:
				params.setMargins(pointMargin, pointMargin, 0, 0);
				break;
			case Gravity.BOTTOM:
				params.setMargins(pointMargin, 0, 0, pointMargin);
			default:
				break;
			}
		case Gravity.RIGHT:
			switch (top_bottom) {
			case Gravity.TOP:
				params.setMargins(0, pointMargin, pointMargin, 0);
				break;
			case Gravity.BOTTOM:
				params.setMargins(0, 0, pointMargin, pointMargin);
			default:
				break;
			}
			break;
		default:
			break;
		}

		setLayoutParams(params);
	}

	private void init() {
		pointMargin = dipToPixels(DEFAULT_MARGIN_DIP);

		setTypeface(Typeface.DEFAULT_BOLD);
		paddingPixels = dipToPixels(DEFAULT_PADDING_DIP);
		setPadding(paddingPixels, 0, paddingPixels, 0);

		setContent(content);
		setColorContent(colorContent);
		setSizeContent(sizeContent);
		setPosition(left_right, top_bottom);
		setColorBg(colorBg);

		isShown = false;

		if (this.orginView != null) {
			restartDraw(this.orginView);
		}
	}

	private void restartDraw(View target) {
		LayoutParams lp = target.getLayoutParams();
		ViewParent parent = target.getParent();
		FrameLayout framLayout = new FrameLayout(context);

		ViewGroup viewGroup = (ViewGroup) parent;
		int index = viewGroup.indexOfChild(target);

		viewGroup.removeView(target);
		viewGroup.addView(framLayout, index, lp);
		framLayout.addView(target);
		framLayout.addView(this);

		viewGroup.invalidate();
	}

	private int dipToPixels(int dip) {
		Resources r = getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip,
				r.getDisplayMetrics());
		return (int) px;
	}

}
