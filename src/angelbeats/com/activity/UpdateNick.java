package angelbeats.com.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import angelbeats.com.bean.User;
import cn.bmob.im.BmobUserManager;
import cn.bmob.v3.listener.UpdateListener;
/**
 * 更改昵称
 * @author AngelBeats
 *
 */
public class UpdateNick extends ActivityBase {
	EditText edit_nick;
	private TextView titleText;
	private Button btnBack,btnSure;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_update_nick);
		userManager = BmobUserManager.getInstance(this);
		initView();
	}

	private void initView() {
		edit_nick = (EditText) findViewById(R.id.edit_nick);
		titleText = (TextView) findViewById(R.id.header_htv_subtitle);
		btnBack = (Button) findViewById(R.id.headr_back);
		btnSure = (Button)findViewById(R.id.headr_add);
		titleText.setText("我的信息");
		btnBack.setVisibility(View.VISIBLE);
		btnSure.setBackgroundResource(R.drawable.base_action_bar_true_bg_selector);
		btnSure.setVisibility(View.VISIBLE);
		btnBack.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intentBack = new Intent(UpdateNick.this, MyInfo.class);
				intentBack.putExtra("from", "UpdateNick");
				startActivity(intentBack);
			}

		});
		btnSure.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				updateInfo(edit_nick.getText().toString());
				Intent intentBack = new Intent(UpdateNick.this, MyInfo.class);
				intentBack.putExtra("from", "UpdateNick");
				startActivity(intentBack);
			}
		});
	}

	private void updateInfo(String nick) {
		final User user = userManager.getCurrentUser(User.class);
		User u = new User();
		u.setNick(nick);
		u.setObjectId(user.getObjectId());
		u.update(this, new UpdateListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				final User c = userManager.getCurrentUser(User.class);
				ShowToast("修改成功:"+c.getNick());
				finish();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast("onFailure:" + arg1);
			}
		});
	}
}
