package angelbeats.com.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import angelbeats.com.bean.User;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CountListener;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
/**
 * ע��-�ֻ�
 * @author AngelBeats
 *
 */
public class Register_phone extends BaseActivity implements OnClickListener {
	private Button nextBtn;
	private Button sendBtn;
	private EditText phoneEdit;
	private EditText checkEdit;
	public String phString;
	private String APPKEY = "76c8ff88bbe0";
	private String APPSECRET = "7236efd6a76b2f394dc26076afaef9bf";
	private boolean monce = false;
	
	private TextView titleText;
	private Button btnBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regist_phone);
		initView();
		SMSSDK.initSDK(this, APPKEY, APPSECRET);
		EventHandler eh = new EventHandler() {

			@Override
			public void afterEvent(int event, int result, Object data) {

				Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				handler.sendMessage(msg);
			}

		};
		SMSSDK.registerEventHandler(eh);

	}

	/**
	 * ��ʼ���ؼ�
	 */
	public void initView() {
		nextBtn = (Button) findViewById(R.id.register_phone_bt_next);
		sendBtn = (Button) findViewById(R.id.register_phone_bt_send);
		phoneEdit = (EditText) findViewById(R.id.register_phone_phone_edit);
		checkEdit = (EditText) findViewById(R.id.register_check_edit_text);
		sendBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		
		titleText = (TextView)findViewById(R.id.header_htv_subtitle);
		btnBack = (Button)findViewById(R.id.headr_back);
		titleText.setText("�ֻ���֤");
		btnBack.setVisibility(View.VISIBLE);
		btnBack.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intentBack = new Intent(Register_phone.this,Login.class);
				intentBack.putExtra("from", "Register_phone");
				startActivity(intentBack);
			}
			
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register_phone_bt_next:
			if (!TextUtils.isEmpty(checkEdit.getText().toString())) {
				SMSSDK.submitVerificationCode("86", phString, checkEdit
						.getText().toString());
			} else {
				Toast.makeText(this, "��֤�벻��Ϊ��", 1).show();
			}
			break;
		case R.id.register_phone_bt_send:
			phString = phoneEdit.getText().toString();
			if (!TextUtils.isEmpty(phString)) {
				String regEx = "[1][3578]\\d{9}";
				Pattern pattern = Pattern.compile(regEx);
				Matcher matcher = pattern.matcher(phString);
				boolean b = matcher.matches();
				if (!b) {
					ShowToast("���벻�Ϸ������������룡");
					return;
				}
		
				final ProgressDialog progress = new ProgressDialog(Register_phone.this);
				progress.setMessage("���ڷ�����֤...");
				progress.setCanceledOnTouchOutside(false);
				progress.show();
				
				
				// ������ݿ��Ƿ��и��ʺ�
				BmobQuery<User> bmobQuery = new BmobQuery<User>("User");
				bmobQuery.addWhereEqualTo("phone", phString);
				bmobQuery.count(Register_phone.this, User.class, new CountListener() {
					@Override
					public void onSuccess(int arg0) {
						if (arg0 == 0) {
							handler.post(wait_send);
							SMSSDK.getVerificationCode("86", phString);
							progress.dismiss();
						}else {
							ShowToast("�ֻ������Ѵ��ڣ�");
							progress.dismiss();
						}
					}
					@Override
					public void onFailure(int arg0, String arg1) {
					}
				});
			} else {
				Toast.makeText(this, "�绰����Ϊ��", 1).show();

			}

			break;
		}

	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);

			if (msg.what == 0x123) {
				//
				if (msg.arg1 > 0) {
					sendBtn.setText("");
					sendBtn.setText("��ȴ�" + msg.arg1 + "s");
					sendBtn.setEnabled(false);
					handler.post(wait_send);
				}
				if (msg.arg1 == 0) {
					sendBtn.setEnabled(true);
					sendBtn.setText("����");
					handler.removeCallbacks(wait_send);
				}
			} else {
				int event = msg.arg1;
				int result = msg.arg2;

				Object data = msg.obj;
				Log.e("event", "event=" + event);
				if (result == SMSSDK.RESULT_COMPLETE) {

					if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// �ύ��֤��ɹ�
						Toast.makeText(getApplicationContext(), "�ύ��֤��ɹ�",
								Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(Register_phone.this,
								Register_detail.class);
						intent.putExtra("phone",phString );
						startActivity(intent);

					} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
						Toast.makeText(getApplicationContext(), "��֤���Ѿ�����",
								Toast.LENGTH_SHORT).show();

					}
				} else {
					((Throwable) data).printStackTrace();
					// int resId = getStringRe(Register_phone.this,
					// "smssdk_network_error");
					// �ֻ����벻��ȷ,ȡ���ȴ�ʱ��
					handler.removeCallbacks(wait_send);
					sendBtn.setEnabled(true);
					sendBtn.setText("����");
					Toast.makeText(Register_phone.this, "��֤�����",
							Toast.LENGTH_SHORT).show();

				}
			}
		}
	};

	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		SMSSDK.unregisterAllEventHandler();
	}

	Runnable wait_send = new Runnable() {
		int timer = 20;

		@Override
		public void run() {
			try {
				if (timer >= 0) {
					Thread.sleep(1000);
					Message msg = handler.obtainMessage();
					msg.what = 0x123;
					msg.arg1 = timer;
					handler.sendMessage(msg);
				} else {
					//�ָ�Ĭ��ֵ
					timer = 20;
					handler.removeCallbacks(wait_send);
				}
				timer--;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	};
}
