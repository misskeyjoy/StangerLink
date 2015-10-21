package angelbeats.com.activity;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import angelbeats.com.util.Constants;
import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobUserManager;
import cn.bmob.v3.Bmob;

import angelbeats.com.applcation.StrangerLinkApplication;

/**
 * ����ҳ��                                                                   
 * @author AngelBeats
 *
 */
public class FirstLoad extends BaseActivity {
	private UIThread mthread=new UIThread();
	
	private LocationClient mLocationClient;
	private LocationMode tempMode = LocationMode.Hight_Accuracy;
	private String tempcoor = "bd09ll";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_first_load);
		Bmob.initialize(this, Constants.applicationId);
		BmobChat.DEBUG_MODE = true;
        mthread.start();
        BmobChat.getInstance(this).startPollService(30);
        //��λ
        mLocationClient = ((StrangerLinkApplication) getApplication()).mLocationClient;
        InitLocation();
		mLocationClient.start();
		userManager = BmobUserManager.getInstance(this);
	}

	public class UIThread extends Thread {
		@Override
		public void run() {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
			if (userManager.getCurrentUser() != null) {
				// ÿ���Զ���½��ʱ�����Ҫ�����µ�ǰλ�úͺ��ѵ����ϣ���Ϊ���ѵ�ͷ���ǳ�ɶ���Ǿ����䶯��
				updateUserInfos();
				Intent intent_home = new Intent(FirstLoad.this,MainActivity.class);
				intent_home.putExtra("from", "FirstLoad");
				startActivity(intent_home);
				finish();
			} else {
				Intent intent_login = new Intent(FirstLoad.this,Login.class);
				intent_login.putExtra("from", "FirstLoad");
				startActivity(intent_login);
				finish();
			}
		}

	}
	
	private void InitLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(tempMode);// ���ö�λģʽ
		option.setCoorType(tempcoor);// ���صĶ�λ����ǰٶȾ�γ��
		int span = 999;
		option.setScanSpan(span);
		mLocationClient.setLocOption(option);
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		mLocationClient.stop();
		super.onStop();
	}

}
