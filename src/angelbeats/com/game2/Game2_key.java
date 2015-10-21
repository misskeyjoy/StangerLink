package angelbeats.com.game2;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import angelbeats.com.activity.ActivityBase;
import angelbeats.com.activity.R;
import angelbeats.com.activity.UserInfo;
import angelbeats.com.util.ImageLoadOptions;

public class Game2_key extends ActivityBase{
	private ImageView imageView_key;
	private TextView text_hint;
	private EditText input_key;
	private Button OkBtn;
	private Button cancelBtn;
	private Intent intent_get;
	private String usernameString;
	private String userhintString;
	private String userpictureString;
	private String userkeyString;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game2_picture_key);
		//userManager = BmobUserManager.getInstance(this);
		//me = userManager.getCurrentUser(User.class);
		
		intent_get = getIntent();
		usernameString = intent_get.getStringExtra("username").toString();
		userhintString = intent_get.getStringExtra("hint").toString();
		userpictureString = intent_get.getStringExtra("picture").toString();
		userkeyString = intent_get.getStringExtra("key").toString();
		
		imageView_key = (ImageView) findViewById(R.id.imageView_game2_key);
		text_hint = (TextView) findViewById(R.id.pictrue_key_hint);
		input_key = (EditText) findViewById(R.id.pictrue_key_picturename);
		OkBtn = (Button) findViewById(R.id.key_bt_ok);
		cancelBtn = (Button) findViewById(R.id.key_cancle);
		
		ImageLoader.getInstance().displayImage(userpictureString, imageView_key,
				ImageLoadOptions.getOptions());
		text_hint.setText(userhintString);
		
		cancelBtn.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// TODO
		OkBtn.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (input_key.getText().toString().equals(userkeyString)) {
					Intent intent_info = new Intent(Game2_key.this,
							UserInfo.class);
					intent_info.putExtra("from", "Game2");
					intent_info.putExtra("username", usernameString);
					startActivity(intent_info);
				} else {
					ShowToast(R.string.game2_key_error);
				}
			}
		});
	}
}
