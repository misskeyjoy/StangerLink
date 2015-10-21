package angelbeats.com.fragment;

import cn.bmob.im.BmobUserManager;
import android.os.Bundle;
import android.app.Fragment;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import angelbeats.com.activity.Login;
import angelbeats.com.activity.MyInfo;
import angelbeats.com.activity.R;
import angelbeats.com.activity.UserInfo;
import angelbeats.com.applcation.StrangerLinkApplication;
import angelbeats.com.util.SharePreferenceUtil;

/**
 * 设置
 * 
 * @author AngelBeats
 * 
 */
public class Set_fragment extends Fragment implements OnClickListener {
	private View viewlayout;
	private TextView titleText;
	Button btn_logout;
	TextView tv_set_name;
	RelativeLayout layout_info, rl_switch_notification, rl_switch_voice,
			rl_switch_vibrate;

	ImageView iv_open_notification, iv_close_notification, iv_open_voice,
			iv_close_voice, iv_open_vibrate, iv_close_vibrate;

	View view1, view2;
	SharePreferenceUtil mSharedUtil;
	public StrangerLinkApplication mApplication;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		viewlayout = inflater.inflate(R.layout.fragment_set, null); // 先解析布局，得到一个view

		init();
		initData();
		return viewlayout;

	}

	private void init() {
		mApplication = StrangerLinkApplication.getInstance();
		mSharedUtil = mApplication.getSpUtil();
		titleText = (TextView) viewlayout
				.findViewById(R.id.header_htv_subtitle);
		titleText.setText("设置");

		layout_info = (RelativeLayout) viewlayout
				.findViewById(R.id.layout_info);
		rl_switch_notification = (RelativeLayout) viewlayout
				.findViewById(R.id.rl_switch_notification);
		rl_switch_voice = (RelativeLayout) viewlayout
				.findViewById(R.id.rl_switch_voice);
		rl_switch_vibrate = (RelativeLayout) viewlayout
				.findViewById(R.id.rl_switch_vibrate);
		rl_switch_notification.setOnClickListener(this);
		rl_switch_voice.setOnClickListener(this);
		rl_switch_vibrate.setOnClickListener(this);

		iv_open_notification = (ImageView) viewlayout
				.findViewById(R.id.iv_open_notification);
		iv_close_notification = (ImageView) viewlayout
				.findViewById(R.id.iv_close_notification);
		iv_open_voice = (ImageView) viewlayout.findViewById(R.id.iv_open_voice);
		iv_close_voice = (ImageView) viewlayout
				.findViewById(R.id.iv_close_voice);
		iv_open_vibrate = (ImageView) viewlayout
				.findViewById(R.id.iv_open_vibrate);
		iv_close_vibrate = (ImageView) viewlayout
				.findViewById(R.id.iv_close_vibrate);
		view1 = (View) viewlayout.findViewById(R.id.view1);
		view2 = (View) viewlayout.findViewById(R.id.view2);
		btn_logout = (Button) viewlayout.findViewById(R.id.btn_logout);
		tv_set_name = (TextView) viewlayout.findViewById(R.id.tv_set_name);
		// 初始化
		boolean isAllowNotify = mSharedUtil.isAllowPushNotify();

		if (isAllowNotify) {
			iv_open_notification.setVisibility(View.VISIBLE);
			iv_close_notification.setVisibility(View.INVISIBLE);
		} else {
			iv_open_notification.setVisibility(View.INVISIBLE);
			iv_close_notification.setVisibility(View.VISIBLE);
		}
		boolean isAllowVoice = mSharedUtil.isAllowVoice();
		if (isAllowVoice) {
			iv_open_voice.setVisibility(View.VISIBLE);
			iv_close_voice.setVisibility(View.INVISIBLE);
		} else {
			iv_open_voice.setVisibility(View.INVISIBLE);
			iv_close_voice.setVisibility(View.VISIBLE);
		}
		boolean isAllowVibrate = mSharedUtil.isAllowVibrate();
		if (isAllowVibrate) {
			iv_open_vibrate.setVisibility(View.VISIBLE);
			iv_close_vibrate.setVisibility(View.INVISIBLE);
		} else {
			iv_open_vibrate.setVisibility(View.INVISIBLE);
			iv_close_vibrate.setVisibility(View.VISIBLE);
		}
		btn_logout.setOnClickListener(this);
		layout_info.setOnClickListener(this);
	}

	private void initData() {
		tv_set_name.setText(BmobUserManager.getInstance(getActivity())
				.getCurrentUser().getUsername());
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_info:// 启动到个人资料页面
			Intent intent = new Intent(getActivity(), MyInfo.class);
			intent.putExtra("from", "Set_fragment");
			startActivity(intent);
			break;
		case R.id.btn_logout:
			StrangerLinkApplication.getInstance().logout();
			getActivity().finish();
			startActivity(new Intent(getActivity(), Login.class));
			break;
		case R.id.rl_switch_notification:
			if (iv_open_notification.getVisibility() == View.VISIBLE) {
				iv_open_notification.setVisibility(View.INVISIBLE);
				iv_close_notification.setVisibility(View.VISIBLE);
				mSharedUtil.setPushNotifyEnable(false);
				rl_switch_vibrate.setVisibility(View.GONE);
				rl_switch_voice.setVisibility(View.GONE);
				view1.setVisibility(View.GONE);
				view2.setVisibility(View.GONE);
			} else {
				iv_open_notification.setVisibility(View.VISIBLE);
				iv_close_notification.setVisibility(View.INVISIBLE);
				mSharedUtil.setPushNotifyEnable(true);
				rl_switch_vibrate.setVisibility(View.VISIBLE);
				rl_switch_voice.setVisibility(View.VISIBLE);
				view1.setVisibility(View.VISIBLE);
				view2.setVisibility(View.VISIBLE);
			}

			break;
		case R.id.rl_switch_voice:
			if (iv_open_voice.getVisibility() == View.VISIBLE) {
				iv_open_voice.setVisibility(View.INVISIBLE);
				iv_close_voice.setVisibility(View.VISIBLE);
				mSharedUtil.setAllowVoiceEnable(false);
			} else {
				iv_open_voice.setVisibility(View.VISIBLE);
				iv_close_voice.setVisibility(View.INVISIBLE);
				mSharedUtil.setAllowVoiceEnable(true);
			}

			break;
		case R.id.rl_switch_vibrate:
			if (iv_open_vibrate.getVisibility() == View.VISIBLE) {
				iv_open_vibrate.setVisibility(View.INVISIBLE);
				iv_close_vibrate.setVisibility(View.VISIBLE);
				mSharedUtil.setAllowVibrateEnable(false);
			} else {
				iv_open_vibrate.setVisibility(View.VISIBLE);
				iv_close_vibrate.setVisibility(View.INVISIBLE);
				mSharedUtil.setAllowVibrateEnable(true);
			}
			break;

		}

	}

}
