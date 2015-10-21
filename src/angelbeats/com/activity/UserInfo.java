package angelbeats.com.activity;

import java.util.List;
import com.nostra13.universalimageloader.core.ImageLoader;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import angelbeats.com.bean.User;
import angelbeats.com.util.ImageLoadOptions;
/**
 * 用户信息
 * @author AngelBeats
 *
 */
public class UserInfo extends ActivityBase {
	private TextView tv_set_name, tv_set_nick, tv_set_gender;
	private RelativeLayout layout_gametype;
	private ImageView iv_set_avator;
	private Button btnAdd;
	private Button btnStartConson;
	private BmobChatUser bombBmobChatUser;
	private Intent intent_get;
	private String usernameString;
	private String objectId;
	private String from;// 从哪个页面跳过来的
	private TextView titleText;
	private Button btnBack;
	private ImageView avatarView, nickView, genderView;// 三个小角，不显现

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userinfo);
		userManager = BmobUserManager.getInstance(this);
		manager = BmobChatManager.getInstance(this);
		initView();
		init();

	}
   /**
    * 控件初始化
    */
	private void initView() {
		intent_get = getIntent();
		from = intent_get.getStringExtra("from").toString();
		usernameString = intent_get.getStringExtra("username").toString();

		avatarView = (ImageView) findViewById(R.id.iv_arraw);
		nickView = (ImageView) findViewById(R.id.iv_nickarraw);
		genderView = (ImageView) findViewById(R.id.iv_genderarraw);
		tv_set_gender = (TextView) findViewById(R.id.tv_set_gender);
		tv_set_name = (TextView) findViewById(R.id.tv_set_name);
		tv_set_nick = (TextView) findViewById(R.id.tv_set_nick);
		iv_set_avator = (ImageView) findViewById(R.id.iv_set_avator);
		layout_gametype = (RelativeLayout) findViewById(R.id.layout_gametype);
		btnAdd = (Button) findViewById(R.id.btn_add_friend);
		btnStartConson = (Button) findViewById(R.id.btn_start_consonvertion);
		titleText = (TextView) findViewById(R.id.header_htv_subtitle);
		btnBack = (Button) findViewById(R.id.headr_back);
		titleText.setText("个人信息");
		btnBack.setVisibility(View.VISIBLE);
		avatarView.setVisibility(View.GONE);
		nickView.setVisibility(View.GONE);
		genderView.setVisibility(View.GONE);
		layout_gametype.setVisibility(View.GONE);
		if (from.equals("Contacts_fragment")) {
			btnAdd.setVisibility(View.GONE);
			btnStartConson.setVisibility(View.VISIBLE);
		} else if (from.equals("Game1")) {
			btnAdd.setVisibility(View.VISIBLE);
			btnStartConson.setVisibility(View.GONE);
		}
		initEvent();
	}

	private void initEvent() {
		btnAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addFriend();
			}
		});
		btnStartConson.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent_startConson = new Intent(UserInfo.this,
						Chat.class);
				intent_startConson.putExtra("from", "UserInfo");
				intent_startConson.putExtra("user", bombBmobChatUser);
				startActivity(intent_startConson);
			}
		});
		btnBack.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (from.equals("Contacts_fragment") || from.equals("Chat")) {
					Intent intentBack = new Intent(UserInfo.this,
							MainActivity.class);
					intentBack.putExtra("from", "UserInfo");
					startActivity(intentBack);
				} else if (from.equals("Game1")) {
					Intent intentBack = new Intent(UserInfo.this,
							NearPeopleActivity.class);
					intentBack.putExtra("from", "UserInfo");
					startActivity(intentBack);
				} else if (from.equals("Chat_message")) {
					Intent intentBack = new Intent(UserInfo.this, Chat.class);
					intentBack.putExtra("from", "UserInfo");
					intentBack.putExtra("user", bombBmobChatUser);
					startActivity(intentBack);
				}else if (from.equals("Game2")) {
					Intent intentBack = new Intent(UserInfo.this, NearPeopleActivity.class);
					intentBack.putExtra("from", "UserInfo");
					startActivity(intentBack);
				}

			}

		});
	}

	private void init() {

		userManager = BmobUserManager.getInstance(this);
		userManager.queryUser(usernameString, new FindListener<User>() {
			@Override
			public void onError(int arg0, String arg1) {
				ShowToast("获取信息失败！");
			}

			@Override
			public void onSuccess(List<User> user) {
				bombBmobChatUser = user.get(0);
				objectId = user.get(0).getObjectId();
				ShowLog(objectId);
				if (user.get(0).getSex() == true) {
					tv_set_gender.setText("男");
				} else {
					tv_set_gender.setText("女");
				}
				tv_set_name.setText(user.get(0).getUsername().toString());
				if (user.get(0).getNick() == null) {
					tv_set_nick.setText(" ");
				} else {
					tv_set_nick.setText(user.get(0).getNick().toString());
				}

				String avatar = user.get(0).getAvatar();
				if (avatar != null && !avatar.equals("")) {
					ImageLoader.getInstance().displayImage(avatar,
							iv_set_avator, ImageLoadOptions.getOptions());
				} else {
					iv_set_avator.setImageResource(R.drawable.head_pic);
				}
				Log.i("--queryUser", user.get(0).getUsername());
			}
		});
	}

	private void addFriend() {
		final ProgressDialog progress = new ProgressDialog(this);
		progress.setMessage("正在添加...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();

		userManager.addContactAfterAgree(bombBmobChatUser.getUsername(),
				new FindListener<BmobChatUser>() {

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.i("--UserInfo", "onError");
						progress.dismiss();
					}

					@Override
					public void onSuccess(List<BmobChatUser> arg0) {
						// TODO Auto-generated method stub
						Log.i("--UserInfo", "onSuccess");
						progress.dismiss();
					}
				});
		BmobMsg message = BmobMsg.createTextSendMsg(this,
				bombBmobChatUser.getObjectId(), "我们已经互为联系人了，可以进行交流了！");
		manager.sendTextMessage(bombBmobChatUser, message);
		BmobChatManager.getInstance(UserInfo.this).sendTagMessage(
				BmobConfig.TAG_ADD_CONTACT, objectId, new PushListener() {

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						progress.dismiss();
					}

					@Override
					public void onFailure(int arg0, final String arg1) {
						// TODO Auto-generated method stub
						progress.dismiss();
					}
				});
	}
}
