package angelbeats.com.util;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class ImageLoadOptions {

	public static DisplayImageOptions getOptions() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				// �������ص�ͼƬ�Ƿ񻺴����ڴ���
				.cacheOnDisc(true)
				// �������ص�ͼƬ�Ƿ񻺴���SD����
				.considerExifParams(true)
				.imageScaleType(ImageScaleType.EXACTLY)// ����ͼƬ����εı��뷽ʽ��ʾ
				.bitmapConfig(Bitmap.Config.RGB_565)// ����ͼƬ�Ľ�������
				.considerExifParams(true)
				.resetViewBeforeLoading(true)// ����ͼƬ������ǰ�Ƿ����ã���λ
				.displayer(new FadeInBitmapDisplayer(100))// ����
				.build();
		
		return options;
	}

}
