package angelbeats.com.game2;

import java.io.File;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import angelbeats.com.activity.ActivityBase;
import angelbeats.com.activity.MyInfo;
import angelbeats.com.activity.R;
import angelbeats.com.bean.User;
import cn.bmob.im.BmobUserManager;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class Game2_next extends ActivityBase{
	private ImageView imageView;
	private EditText intputEt;
	private EditText inputEtans;
	private Button sumitBtn;
	private Button cancelBtn;
	private Intent intent_get;
	private String filepath;
	private String usernameString;
	private User me;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game2_picture_dialog);
		userManager = BmobUserManager.getInstance(this);
		me = userManager.getCurrentUser(User.class);

		intent_get = getIntent();
		filepath = intent_get.getStringExtra("filepath").toString();

		imageView = (ImageView) findViewById(R.id.imageView_game2);
		intputEt = (EditText) findViewById(R.id.pictrue_dialog_picturename);
		inputEtans = (EditText) findViewById(R.id.pictrue_dialog_pictureans);
		sumitBtn = (Button) findViewById(R.id.dialog_send_picture);
		cancelBtn = (Button) findViewById(R.id.dialog_cancle);
		Bitmap bitmap = BitmapFactory.decodeFile(filepath); 
		imageView.setImageBitmap(bitmap);//
		cancelBtn.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				File f = new File(filepath);
				f.delete();
				finish();
			}
		});
		// TODO
		sumitBtn.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (intputEt.getText().toString() != null) {
					updatapic(filepath);
					updategame2(intputEt.getText().toString(),inputEtans.getText().toString());					

					Intent intent_info = new Intent(Game2_next.this,
							MyInfo.class);
					intent_info.putExtra("from", "Game2");
					intent_info.putExtra("username", usernameString);
					startActivity(intent_info);
				} else {

				}
			}
		});
	}
	private void updategame2(String key,String hint){
		final User user = new User();
		user.setGame2_key(key);
		user.setGame2_hint(hint);
		user.setFriendAddPolicy("b");
		user.setObjectId(me.getObjectId());
		user.update(this, new UpdateListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub

				//finish();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast("上传失败:" + arg1);
			}

		});
	}

	/**
	 * 上传图画
	 */
	final private User user = new User();

	private void updatapic(String path) {
		Log.i("--图片地址：", path);
		final BmobFile bmobFile = new BmobFile(new File(path));
		bmobFile.upload(this, new UploadFileListener() {
			@Override
			public void onSuccess() {
				String url = bmobFile.getFileUrl(Game2_next.this);
				// 更新User对象
				user.setGame2_picture(url);
				user.setObjectId(me.getObjectId());
				user.update(Game2_next.this);
			}

			@Override
			public void onProgress(Integer arg0) {
			}

			@Override
			public void onFailure(int arg0, String msg) {
				ShowToast("图片上传失败：" + msg);
			}
		});
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
}