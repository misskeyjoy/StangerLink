package angelbeats.com.util;

import android.os.Environment;

public class Constants {

	// ����Bmob��ApplicationId,���ڳ�ʼ������
	public static String applicationId = "c5decea8856cb5819f69adaf2764fd67";

	// �ҵ�ͷ�񱣴�Ŀ¼
	public static String MyAvatarDir = Environment
			.getExternalStorageDirectory().getPath() + "/StrangerLink/avatar/";
	//��ŷ���ͼƬ��Ŀ¼
	public static String PICTURE_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/StrangerLink/image/";

	// ���ջص�
	public static final int REQUESTCODE_UPLOADAVATAR_BYO = 1;// ѡ���Դ�ͷ��
	public static final int REQUESTCODE_UPLOADAVATAR_LOCATION = 2;// ��������޸�ͷ��
	public static final int REQUESTCODE_UPLOADAVATAR_CAMERA = 3;// �����޸�ͷ��

	public static final int REQUESTCODE_UPLOADAVATAR_CROP = 4;// ϵͳ�ü�ͷ��
	
	public static final int REQUESTCODE_TAKE_CAMERA = 0x000001;//����
	public static final int REQUESTCODE_TAKE_LOCAL = 0x000002;//����ͼƬ

}
