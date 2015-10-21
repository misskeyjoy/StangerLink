package angelbeats.com.activity;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;
import angelbeats.com.applcation.StrangerLinkApplication;
import angelbeats.com.util.CollectionUtils;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.listener.FindListener;

/**
 * 基类
 * 
 * @author AngelBeats 2015.5.8
 */
public class BaseActivity extends Activity {
	protected BmobUserManager userManager;
	BmobChatManager manager;
	private IntentFilter intentFilter;
	private NetworkChangeReceiver networkChangeReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		intentFilter = new IntentFilter();
		intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		networkChangeReceiver = new NetworkChangeReceiver();
		registerReceiver(networkChangeReceiver, intentFilter);
	}

	Toast mToast;

	public void ShowToast(final String text) {
		if (!TextUtils.isEmpty(text)) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (mToast == null) {
						mToast = Toast.makeText(getApplicationContext(), text,
								Toast.LENGTH_LONG);
					} else {
						mToast.setText(text);
					}
					mToast.show();
				}
			});
		}
	}

	public void ShowToast(final int resId) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (mToast == null) {
					mToast = Toast.makeText(
							BaseActivity.this.getApplicationContext(), resId,
							Toast.LENGTH_LONG);
				} else {
					mToast.setText(resId);
				}
				mToast.show();
			}
		});
	}

	public void ShowLog(String msg) {
		Log.i("--life", msg);
	}
	/**
	 * 重写方法 主要是设置切换动画
	 */
    @Override
    public void startActivity(Intent intent) {
    	// TODO Auto-generated method stub
    	super.startActivity(intent);
    	overridePendingTransition(R.anim.fade,
				R.anim.my_alpha_action);
    }
	/**
	 * 用于登陆或者自动登陆情况下的好友资料的检测更新
	 */
	public void updateUserInfos() {
		userManager.queryCurrentContactList(new FindListener<BmobChatUser>() {

			@Override
			public void onError(int arg0, String arg1) {
				if (arg0 == BmobConfig.CODE_COMMON_NONE) {
					ShowLog(arg1);
				} else {
					ShowLog("查询好友列表失败：" + arg1);
				}
			}

			@Override
			public void onSuccess(List<BmobChatUser> arg0) {
				// 保存到application中方便比较
				StrangerLinkApplication.getInstance().setContactList(
						CollectionUtils.list2map(arg0));
				for (int i = 0; i < arg0.size(); i++) {
					Log.i("--userlist", arg0.get(i).getUsername());
				}
			}
		});
	}

	class NetworkChangeReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager connectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isAvailable()) {

			} else {
				ShowToast("网络不可用！请检查网络！");
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(networkChangeReceiver);
	}

}
