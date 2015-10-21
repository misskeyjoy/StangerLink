package angelbeats.com.util;

import android.os.Environment;

public class Constants {

	// 这是Bmob的ApplicationId,用于初始化操作
	public static String applicationId = "c5decea8856cb5819f69adaf2764fd67";

	// 我的头像保存目录
	public static String MyAvatarDir = Environment
			.getExternalStorageDirectory().getPath() + "/StrangerLink/avatar/";
	//存放发送图片的目录
	public static String PICTURE_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/StrangerLink/image/";

	// 拍照回调
	public static final int REQUESTCODE_UPLOADAVATAR_BYO = 1;// 选择自带头像
	public static final int REQUESTCODE_UPLOADAVATAR_LOCATION = 2;// 本地相册修改头像
	public static final int REQUESTCODE_UPLOADAVATAR_CAMERA = 3;// 拍照修改头像

	public static final int REQUESTCODE_UPLOADAVATAR_CROP = 4;// 系统裁剪头像
	
	public static final int REQUESTCODE_TAKE_CAMERA = 0x000001;//拍照
	public static final int REQUESTCODE_TAKE_LOCAL = 0x000002;//本地图片

}
