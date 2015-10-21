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
 * 启动页面                                                                   
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
        //定位
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
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			if (userManager.getCurrentUser() != null) {
				// 每次自动登陆的时候就需要更新下当前位置和好友的资料，因为好友的头像，昵称啥的是经常变动的
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
		option.setLocationMode(tempMode);// 设置定位模式
		option.setCoorType(tempcoor);// 返回的定位结果是百度经纬度
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
