package angelbeats.com.applcation;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.datatype.BmobGeoPoint;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;
import angelbeats.com.activity.R;
import angelbeats.com.util.CollectionUtils;
import angelbeats.com.util.SharePreferenceUtil;

/**
 * ��Application
 */
public class StrangerLinkApplication extends Application {
	public static StrangerLinkApplication mInstance;
	public LocationClient mLocationClient;
	public GeofenceClient mGeofenceClient;
	public MyLocationListener mMyLocationListener;
	public Vibrator mVibrator;
	public BmobGeoPoint geoPoint;
	private Map<String, BmobChatUser> contactList;
	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		contactList = new HashMap<String, BmobChatUser>();
		mLocationClient = new LocationClient(this.getApplicationContext());
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		mGeofenceClient = new GeofenceClient(getApplicationContext());

		Log.i("--BaiduLocationApiDem", "onCreate");
		mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
		
		if (BmobUserManager.getInstance(getApplicationContext())
				.getCurrentUser() != null) {
			// ��ȡ���غ���user list���ڴ�,�����Ժ��ȡ����list
			contactList = CollectionUtils.list2map(BmobDB.create(getApplicationContext()).getContactList());
		}
		
		initImageLoader(getApplicationContext());
		
	}

	public static StrangerLinkApplication getInstance() {
		return mInstance;
	}
	/**
	 * ʵ��ʵλ�ص�����
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
		
			double latitude = location.getLatitude();
			double longitude = location.getLongitude(); 
			geoPoint = new BmobGeoPoint(longitude, latitude);
			Log.i("--MyLocationListener", latitude + " " + longitude);
			
		}


	}
	//��ʼ��ImageLoader 
	public static void initImageLoader(Context context) {
		File cacheDir = StorageUtils.getOwnCacheDirectory(context,
				"/StrangerLink/avatar/");// ��ȡ�������Ŀ¼��ַ
		// ��������ImageLoader(���е�ѡ��ǿ�ѡ��,ֻʹ����Щ������붨��)����������趨��APPLACATION���棬����Ϊȫ�ֵ����ò���
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				// �̳߳��ڼ��ص�����
				.threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCache(new WeakMemoryCache())
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.discCache(new UnlimitedDiscCache(cacheDir))// �Զ��建��·��
				.writeDebugLogs() // Remove for release app
				.build();
		ImageLoader.getInstance().init(config);// ȫ�ֳ�ʼ��������
	}
	
	/**
	 * ��ȡ�ڴ��к���user list
	 * 
	 * @return
	 */
	public Map<String, BmobChatUser> getContactList() {
		return contactList;
	}

	/**
	 * ���ú���user list���ڴ���
	 * @param contactList
	 */
	public void setContactList(Map<String, BmobChatUser> contactList) {
		if (this.contactList != null) {
			this.contactList.clear();
		}
		this.contactList = contactList;
	}

	/**
	 * �˳���¼,��ջ�������
	 */
	public void logout() {
		BmobUserManager.getInstance(getApplicationContext()).logout();
		setContactList(null);
	}
	// ����ģʽ�����ܼ�ʱ��������
		SharePreferenceUtil mSpUtil;
		public static final String PREFERENCE_NAME = "_sharedinfo";

	public synchronized SharePreferenceUtil getSpUtil() {
		if (mSpUtil == null) {
			String currentId = BmobUserManager.getInstance(
					getApplicationContext()).getCurrentUserObjectId();
			String sharedName = currentId + PREFERENCE_NAME;
			mSpUtil = new SharePreferenceUtil(this, sharedName);
		}
		return mSpUtil;
	}
	MediaPlayer mMediaPlayer;

	public synchronized MediaPlayer getMediaPlayer() {
		if (mMediaPlayer == null)
			mMediaPlayer = MediaPlayer.create(this, R.raw.notify);
		return mMediaPlayer;
	}
		
}
