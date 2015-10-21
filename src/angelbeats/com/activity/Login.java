package angelbeats.com.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import angelbeats.com.bean.User;
import cn.bmob.im.BmobUserManager;
import cn.bmob.v3.listener.SaveListener;

/**
 * ��½����
 * @author AngelBeats
 * 2015.5.8
 */
public class Login extends BaseActivity implements OnClickListener {
	private EditText namEdit; // �û���
	private EditText passwdEdit; // ����
	private Button submitBtn; // ��¼��ť
	private Button registerBtn; // ע��
	private Button forgetBtn; // ��������
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_layout);
		// ��ʼ���ؼ�
		init();

	}

	/**
	 * ��ʼ���ؼ�
	 */
	private void init() {
		userManager = BmobUserManager.getInstance(this);
		namEdit = (EditText) findViewById(R.id.name_login_btn);
		passwdEdit = (EditText) findViewById(R.id.passwd_login_edit);
		submitBtn = (Button) findViewById(R.id.submit_login_btn);
		registerBtn = (Button) findViewById(R.id.register_login_btn);
		forgetBtn = (Button) findViewById(R.id.forgetPasswd_login_btn);
		submitBtn.setOnClickListener(this);
		registerBtn.setOnClickListener(this);
		forgetBtn.setOnClickListener(this);
	}
	
	/**
	 * ����¼�
	 */
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.submit_login_btn:
			/*Intent intent_near = new Intent(Login.this,MainActivity.class);
			intent_near.putExtra("from", "login");
			startActivity(intent_near);*/
			login();
			break;
		case R.id.register_login_btn:
			Intent intent_register = new Intent(Login.this,Register_phone.class);
			startActivity(intent_register);
			break;
		case R.id.forgetPasswd_login_btn:
			Intent intent_forgetpasswd = new Intent(Login.this,Register_phone.class);
			startActivity(intent_forgetpasswd);
			break;
		}
	}
	/**
	 * ��½
	 */
	private void login(){
		String name = namEdit.getText().toString();
		String password = passwdEdit.getText().toString();

		if (TextUtils.isEmpty(name)) {
			ShowToast(R.string.toast_error_username_null);
			return;
		}

		if (TextUtils.isEmpty(password)) {
			ShowToast(R.string.toast_error_password_null);
			return;
		}

		final ProgressDialog progress = new ProgressDialog(
				Login.this);
		progress.setMessage("���ڵ�½...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		User user = new User();
		user.setUsername(name);
		user.setPassword(password);
		userManager.login(user,new SaveListener() {
			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						progress.setMessage("���ڻ�ȡ�����б�...");
					}
				});
				//�����û��ĺ��ѵ�����
				updateUserInfos();
				progress.dismiss();
				
				Intent intent = new Intent(Login.this,MainActivity.class);
				intent.putExtra("from", "login");
				startActivity(intent);
				finish();
			}

			@Override
			public void onFailure(int errorcode, String arg0) {
				progress.dismiss();
				ShowToast(arg0);
			}
		});
		
	}
}
