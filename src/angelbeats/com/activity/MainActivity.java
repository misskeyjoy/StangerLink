package angelbeats.com.activity;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import angelbeats.com.applcation.StrangerLinkApplication;
import angelbeats.com.fragment.Consonvertion_fragment;
import angelbeats.com.fragment.Contacts_fragment;
import angelbeats.com.fragment.Set_fragment;
import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.inteface.EventListener;

/**
 * ������
 * 
 * @author AngelBeats
 * @class MainActivity
 */
public class MainActivity extends ActivityBase implements OnClickListener,
		EventListener {
	private Consonvertion_fragment consonvertion_fragment;
	private Contacts_fragment contacts_fragment;
	private Set_fragment set_fragment;
	private LinearLayout[] lineFragment;
	private ImageView consonvertionimageview, friend_list_imageview,
			own_setting_imageview;
	private TextView consonvertion_text, friend_list_textview,
			own_setting_textview;

	private int text_color;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO �Զ����ɵķ������
		super.onCreate(savedInstanceState);
		// �ڵ��ò���ǰִ��requestWindowFeature����
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main_layout);
		BmobChat.getInstance(this).startPollService(1);
		initNewMessageBroadCast();
		initTagMessageBroadCast();
		init();
		userManager = BmobUserManager.getInstance(this);
	}

	/**
	 * ��ʼ���ؼ�
	 */
	public void init() {
		lineFragment = new LinearLayout[3];
		consonvertionimageview = (ImageView) findViewById(R.id.consonvertionimageview);
		friend_list_imageview = (ImageView) findViewById(R.id.friend_list_imageview);
		own_setting_imageview = (ImageView) findViewById(R.id.own_setting_imageview);
		consonvertion_text = (TextView) findViewById(R.id.consonvertion_textview);
		friend_list_textview = (TextView) findViewById(R.id.friend_list_textview);
		own_setting_textview = (TextView) findViewById(R.id.own_setting_textview);
		lineFragment[0] = (LinearLayout) findViewById(R.id.consonvertion);
		lineFragment[1] = (LinearLayout) findViewById(R.id.friend_list);
		lineFragment[2] = (LinearLayout) findViewById(R.id.own_setting);
		lineFragment[0].setOnClickListener(this);
		lineFragment[1].setOnClickListener(this);
		lineFragment[2].setOnClickListener(this);
		text_color = getResources().getColor(R.color.main_text_color);

		consonvertion_fragment = new Consonvertion_fragment();
		contacts_fragment = new Contacts_fragment();
		set_fragment = new Set_fragment();

		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.add(R.id.framelayout, consonvertion_fragment)
				.add(R.id.framelayout, contacts_fragment)
				.add(R.id.framelayout, set_fragment);
		Intent fromIntent = getIntent();
		String from = fromIntent.getStringExtra("from");
		if (from.equals("login") || from.equals("FirstLoad")
				|| from.equals("Chat2Consonvertion")) {
			transaction.hide(contacts_fragment).hide(set_fragment)
					.show(consonvertion_fragment);
		} else if (from.equals("MyInfo")) {
			restBack();
			own_setting_imageview.setImageResource(R.drawable.shezhi2);
			own_setting_textview.setTextColor(text_color);
			transaction.hide(consonvertion_fragment).hide(contacts_fragment)
					.show(set_fragment);
		} else {
			restBack();
			friend_list_imageview.setImageResource(R.drawable.lianxiren2);
			friend_list_textview.setTextColor(text_color);
			transaction.hide(consonvertion_fragment).hide(set_fragment)
					.show(contacts_fragment);
		}

		transaction.commit();

	}

	/**
	 * Ϊ��Ӧ�Ŀؼ�����¼�
	 * 
	 * @param v
	 */
	@SuppressLint("CommitTransaction")
	@Override
	public void onClick(View v) {
		// TODO �Զ����ɵķ������
		restBack();
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		switch (v.getId()) {
		case R.id.consonvertion:
			if (consonvertion_fragment == null) {
				consonvertion_fragment = new Consonvertion_fragment();
				Log.i("--if", "consonvertion_fragment");
			}
			transaction.hide(contacts_fragment).hide(set_fragment)
					.show(consonvertion_fragment);
			consonvertionimageview.setImageResource(R.drawable.huihua2);
			consonvertion_text.setTextColor(text_color);
			Log.i("--consonvertion", "consonvertion");
			break;
		case R.id.friend_list:
			if (contacts_fragment == null) {
				contacts_fragment = new Contacts_fragment();
				Log.i("--if", "contacts_fragment");
			}
			transaction.hide(consonvertion_fragment).hide(set_fragment)
					.show(contacts_fragment);
			friend_list_imageview.setImageResource(R.drawable.lianxiren2);
			friend_list_textview.setTextColor(text_color);
			Log.i("--friend_list", "consonvertion");
			break;
		case R.id.own_setting:
			if (set_fragment == null) {
				set_fragment = new Set_fragment();
				Log.i("--if", "set_fragment");
			}
			transaction.hide(contacts_fragment).hide(consonvertion_fragment)
					.show(set_fragment);
			own_setting_imageview.setImageResource(R.drawable.shezhi2);
			own_setting_textview.setTextColor(text_color);
			Log.i("--own_setting", "consonvertion");
			break;

		}
		transaction.commit();
	}

	/**
	 * ����Ĭ�ϵı�����������ɫ
	 */
	public void restBack() {
		consonvertionimageview.setImageResource(R.drawable.huihua1);
		consonvertion_text.setTextColor(Color.BLACK);
		friend_list_imageview.setImageResource(R.drawable.lianxiren1);
		friend_list_textview.setTextColor(Color.BLACK);
		own_setting_imageview.setImageResource(R.drawable.shezhi1);
		own_setting_textview.setTextColor(Color.BLACK);
	}
	
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MessageReceiver.ehList.add(this);// �������͵���Ϣ
		// ���
		MessageReceiver.mNewNum = 0;

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MessageReceiver.ehList.remove(this);// ȡ���������͵���Ϣ
	}

	@Override
	public void onMessage(BmobMsg message) {
		// TODO Auto-generated method stub
		refreshNewMsg(message);
	}

	/**
	 * ˢ�½���
	 * 
	 * @param message
	 */
	private void refreshNewMsg(BmobMsg message) {
		// ������ʾ
		boolean isAllow = StrangerLinkApplication.getInstance().getSpUtil()
				.isAllowVoice();
		if (isAllow) {
			StrangerLinkApplication.getInstance().getMediaPlayer().start();
		}
		// ҲҪ�洢����
		if (message != null) {
			BmobChatManager.getInstance(MainActivity.this).saveReceiveMessage(
					true, message);
		}
		if (!consonvertion_fragment.isHidden()) {
			// ��ǰҳ�����Ϊ�Ựҳ�棬ˢ�´�ҳ��
			if (consonvertion_fragment != null) {
				consonvertion_fragment.refresh();
			}
		}
	}

	NewBroadcastReceiver newReceiver;

	private void initNewMessageBroadCast() {
		// ע�������Ϣ�㲥
		newReceiver = new NewBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(
				BmobConfig.BROADCAST_NEW_MESSAGE);
		// ���ȼ�Ҫ����ChatActivity
		intentFilter.setPriority(3);
		registerReceiver(newReceiver, intentFilter);
	}

	/**
	 * ����Ϣ�㲥������
	 * 
	 */
	private class NewBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// ˢ�½���
			refreshNewMsg(null);
		}
	}

	TagBroadcastReceiver userReceiver;

	private void initTagMessageBroadCast() {
		// ע�������Ϣ�㲥
		userReceiver = new TagBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(
				BmobConfig.BROADCAST_ADD_USER_MESSAGE);
		// ���ȼ�Ҫ����ChatActivity
		intentFilter.setPriority(3);
		registerReceiver(userReceiver, intentFilter);
	}

	/**
	 * ��ǩ��Ϣ�㲥������
	 */
	private class TagBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			BmobInvitation message = (BmobInvitation) intent
					.getSerializableExtra("invite");
			refreshInvite(message);
		}
	}

	@Override
	public void onNetChange(boolean isNetConnected) {
		// TODO Auto-generated method stub
		if (isNetConnected) {
			ShowToast(R.string.network_error_not_connected);
		}
	}

	@Override
	public void onAddUser(BmobInvitation message) {
		// TODO Auto-generated method stub
		refreshInvite(message);
	}

	/**
	 * ˢ�º�������
	 * 
	 * @param message
	 */
	private void refreshInvite(BmobInvitation message) {
		boolean isAllow = StrangerLinkApplication.getInstance().getSpUtil()
				.isAllowVoice();
		if (isAllow) {
			StrangerLinkApplication.getInstance().getMediaPlayer().start();
		}
		if (!contacts_fragment.isHidden()) {
			if (contacts_fragment != null) {
				contacts_fragment.refresh();
			}
		} else {
			// ͬʱ����֪ͨ
			String tickerText = message.getFromname() + "�������Ϊ��ϵ��";
			boolean isAllowVibrate = StrangerLinkApplication.getInstance()
					.getSpUtil().isAllowVibrate();
			BmobNotifyManager.getInstance(this).showNotify(isAllow,
					isAllowVibrate, R.drawable.ic_launcher, tickerText,
					message.getFromname(), tickerText.toString(),
					MainActivity.class);
			userManager.addContactAfterAgree(message.getFromname());
			BmobMsg.createAndSaveRecentAfterAgree(MainActivity.this,
					"�����Ѿ���Ϊ��ϵ���ˣ����Խ��н����ˣ�");
		}
	}

	@Override
	public void onOffline() {
		// TODO Auto-generated method stub
		ShowToast("�����˺����������豸�ϵ�¼!");
		startActivity(new Intent(this, Login.class));
		finish();
	}

	@Override
	public void onReaded(String conversionId, String msgTime) {
		// TODO Auto-generated method stub
	}

	private static long firstTime;

	/**
	 * ���������η��ؼ����˳�
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (firstTime + 2000 > System.currentTimeMillis()) {
			super.onBackPressed();
		} else {
			ShowToast("�ٰ�һ���˳�����");
		}
		firstTime = System.currentTimeMillis();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			unregisterReceiver(newReceiver);
		} catch (Exception e) {
		}
		try {
			unregisterReceiver(userReceiver);
		} catch (Exception e) {
		}
		BmobChat.getInstance(this).stopPollService();
	}
}
