package angelbeats.com.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import angelbeats.com.activity.R;

/**
 * @˵���� �Զ����ɾ����ť��EditText
 * 
 */
public class ClearEditText extends EditText implements OnFocusChangeListener,
		TextWatcher {
	// EditText�Ҳ��ɾ����ť
	private Drawable mClearDrawable;
	private boolean hasFoucs;

	public ClearEditText(Context context) {
		this(context, null);
	}

	public ClearEditText(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.editTextStyle);
	}

	public ClearEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		// ��ȡEditText��DrawableRight,����û���������Ǿ�ʹ��Ĭ�ϵ�ͼƬ,��ȡͼƬ��˳�����������£�0,1,2,3,��
		mClearDrawable = getCompoundDrawables()[2];
		if (mClearDrawable == null) {
			mClearDrawable = getResources().getDrawable(R.drawable.edit_clear);
		}

		mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(),
				mClearDrawable.getIntrinsicHeight());
		// Ĭ����������ͼ��
		setClearIconVisible(false);
		// ���ý���ı�ļ���
		setOnFocusChangeListener(this);
		// ����������������ݷ����ı�ļ���
		addTextChangedListener(this);
	}

	/*
	 * @˵����isInnerWidth, isInnerHeightΪture����������ɾ��ͼ��֮�ڣ�����Ϊ�����ɾ��ͼ�� event.getX()
	 * ��ȡ���Ӧ�������Ͻǵ�X���� event.getY() ��ȡ���Ӧ�������Ͻǵ�Y���� getWidth() ��ȡ�ؼ��Ŀ�� getHeight()
	 * ��ȡ�ؼ��ĸ߶� getTotalPaddingRight() ��ȡɾ��ͼ�����Ե���ؼ��ұ�Ե�ľ��� getPaddingRight()
	 * ��ȡɾ��ͼ���ұ�Ե���ؼ��ұ�Ե�ľ��� isInnerWidth: getWidth() - getTotalPaddingRight()
	 * ����ɾ��ͼ�����Ե���ؼ����Ե�ľ��� getWidth() - getPaddingRight() ����ɾ��ͼ���ұ�Ե���ؼ����Ե�ľ���
	 * isInnerHeight: distance ɾ��ͼ�궥����Ե���ؼ�������Ե�ľ��� distance + height
	 * ɾ��ͼ��ײ���Ե���ؼ�������Ե�ľ���
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (getCompoundDrawables()[2] != null) {
				int x = (int) event.getX();
				int y = (int) event.getY();
				Rect rect = getCompoundDrawables()[2].getBounds();
				int height = rect.height();
				int distance = (getHeight() - height) / 2;
				boolean isInnerWidth = x > (getWidth() - getTotalPaddingRight())
						&& x < (getWidth() - getPaddingRight());
				boolean isInnerHeight = y > distance && y < (distance + height);
				if (isInnerWidth && isInnerHeight) {
					this.setText("");
				}
			}
		}
		return super.onTouchEvent(event);
	}

	/**
	 * ��ClearEditText���㷢���仯��ʱ�� ���볤��Ϊ�㣬����ɾ��ͼ�꣬������ʾɾ��ͼ��
	 */
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		this.hasFoucs = hasFocus;
		if (hasFocus) {
			setClearIconVisible(getText().length() > 0);
		} else {
			setClearIconVisible(false);
		}
	}

	protected void setClearIconVisible(boolean visible) {
		Drawable right = visible ? mClearDrawable : null;
		setCompoundDrawables(getCompoundDrawables()[0],
				getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int count, int after) {
		if (hasFoucs) {
			setClearIconVisible(s.length() > 0);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void afterTextChanged(Editable s) {

	}
}