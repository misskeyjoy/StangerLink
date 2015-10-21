package angelbeats.com.activity;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
/**
 * ҡһҡ 
 * 
 * @author AngelBeats
 *
 */
public class ShakeActivity extends BaseActivity implements OnClickListener {
	private ShakeListenerUtils shakeUtils;
	private SensorManager mSensorManager; // ����sensor������, ע���������
	private SoundPool pool; // ������
	private ImageButton btn_setVoice; // ��������
	private ImageButton btn_setVibre; // ������
	int soundId;// ��������ĳ����Ƶid
	private long lasttime;
	public static String shake_setting = "shake_setting";
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private int setting_voice; // �������������������ر�����
	private int setting_vibre; // 1 �����𶯡������ر���
	Vibrator vibrator;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shake_activity_layout);
		shakeUtils = new ShakeListenerUtils(this);
		preferences = getSharedPreferences(shake_setting, MODE_WORLD_READABLE);
		editor = preferences.edit();
		// editor.putInt("vibre", 1);
		// editor.putInt("voice", 1);
		// editor.commit();
		btn_setVoice = (ImageButton) findViewById(R.id.shake_setting_music);
		btn_setVibre = (ImageButton) findViewById(R.id.shake_setting_vibre);
		btn_setVibre.setOnClickListener(this);
		btn_setVoice.setOnClickListener(this);
		setting_vibre = preferences.getInt("vibre", 1);
		setting_voice = preferences.getInt("voice", 1);
		setVibre(setting_vibre);
		setVolume(setting_voice);
		vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
		// ��������
		if (setting_vibre == 2) {
			// ������Ӧ��ͼƬ
			// btn_setVibre.setBackground(background);
		}
		// ������������
		if (setting_voice == 2) {
			// TODO
			// btn_setVoice.setBackground(background);
		}
		// ����������
		pool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
		soundId = pool.load(this, R.raw.yaoyiyao, 100);

	}

	@Override
	protected void onResume() {
		super.onResume();

		// ��ȡ�������������
		mSensorManager = (SensorManager) this
				.getSystemService(Service.SENSOR_SERVICE);
		// ���ٶȴ�����
		mSensorManager.registerListener(shakeUtils,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				// ����SENSOR_DELAY_UI��SENSOR_DELAY_FASTEST��SENSOR_DELAY_GAME�ȣ�
				// ���ݲ�ͬӦ�ã���Ҫ�ķ�Ӧ���ʲ�ͬ���������ʵ������趨
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {
		mSensorManager.unregisterListener(shakeUtils);
		this.overridePendingTransition(R.anim.fade, R.anim.hold);// ����ʱ��һ��
		super.onPause();
	}

	class ShakeListenerUtils implements SensorEventListener {
		private Activity context;
		private int currentVol;

		public ShakeListenerUtils(Activity context) {
			super();
			this.context = context;
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			int sensorType = event.sensor.getType();
			// values[0]:X�ᣬvalues[1]��Y�ᣬvalues[2]��Z��
			float[] values = event.values;

			if (sensorType == Sensor.TYPE_ACCELEROMETER) {

				/*
				 * ��������£���������ֵ������9.8~10֮�䣬ֻ����ͻȻҡ���ֻ� ��ʱ��˲ʱ���ٶȲŻ�ͻȻ�������١�
				 * ������һ��ļ��ٶȴ���17����
				 */
				if ((Math.abs(values[0]) > 17 || Math.abs(values[1]) > 17 || Math
						.abs(values[2]) > 17)) {
					long curTime = java.lang.System.currentTimeMillis();
					pool.stop(soundId);
					if (curTime - lasttime > 3) {
						editor.commit();
						context.overridePendingTransition(R.anim.fade,
								R.anim.hold);
						Intent intent = new Intent(context,
								NearPeopleActivity.class);
						context.startActivity(intent);

					}
					lasttime = curTime;
					// ����������
					if (setting_voice == 1) {
						pool.play(soundId, 12, 12, 1, 0, 1);
					}
					if (setting_vibre == 1) {
						vibrator.vibrate(1000);

					}

					// // ��⵽�ζ�������OpenDoorЧ��
					// Intent intent = new Intent(context,
					// NearPeopleActivity.class);
					// context.startActivity(intent);
					// context.finish();
				}
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// �����������ȸı�ʱ�ص��÷�����Do nothing.
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.shake_setting_music:
			// btn_setVoice.setBackgroundResource(R.drawable.back);
			if (setting_voice == 1) {
				btn_setVoice
						.setBackgroundResource(R.drawable.ic_no_volume);
				setting_voice = 2;
				editor.putInt("voice", 2);
			} else {
				setting_voice = 1;
				btn_setVoice.setBackgroundResource(R.drawable.ic_volume);
				editor.putInt("voice", 1);
			}

			break;

		case R.id.shake_setting_vibre:
			// btn_setVoice.setBackgroundResource(R.drawable.back);
			if (setting_vibre== 1) {
				btn_setVibre
						.setBackgroundResource(R.drawable.ic_no_vibrate);
				setting_vibre = 2;
				editor.putInt("vibre", 2);
			} else {
				setting_voice = 1;
				btn_setVibre.setBackgroundResource(R.drawable.ic_vibrate);
				editor.putInt("vibre", 1);
			}

			break;
		}
	}
	public void setVibre(int flag) {
		if (flag== 1) {
			btn_setVibre
					.setBackgroundResource(R.drawable.ic_vibrate);
		} else {
			btn_setVoice.setBackgroundResource(R.drawable.ic_no_volume);
		}
	}
	public void setVolume(int flag) {
		if (flag== 1) {
			btn_setVoice
					.setBackgroundResource(R.drawable.ic_volume);
		} else {
			btn_setVoice.setBackgroundResource(R.drawable.ic_no_volume);
		}
	}
}
