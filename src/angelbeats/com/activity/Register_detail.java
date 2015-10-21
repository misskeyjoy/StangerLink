package angelbeats.com.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import angelbeats.com.bean.User;
import angelbeats.com.util.Constants;
import angelbeats.com.util.PhotoUtil;
import angelbeats.com.util.Utils;
import cn.bmob.im.BmobUserManager;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

import angelbeats.com.applcation.StrangerLinkApplication;
/**
 * 注册界面-填写头像，用户名，密码
 * 
 * @author AngelBeats 2015.5.9
 */
public class Register_detail extends BaseActivity implements OnClickListener {
	private Button registerBtn;
	private ImageView avatarImgV;
	private EditText nameEdit;
	private EditText passwdEdit;
	private EditText conpasswdEdit;
	private Intent intent;
	private BmobGeoPoint geoPointlocation;
	final User user = new User();
	private boolean isExist;
	private String[] items = new String[] { "选择本地图片", "拍照" };
	
	private TextView titleText;
	private Button btnBack;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_detail);
		userManager = BmobUserManager.getInstance(this);
		Init();
	}

	public void Init() {
		geoPointlocation = ((StrangerLinkApplication) getApplication()).geoPoint;
		avatarImgV = (ImageView) findViewById(R.id.head_pic);
		nameEdit = (EditText) findViewById(R.id.name_edit);
		passwdEdit = (EditText) findViewById(R.id.passwd_edit);
		conpasswdEdit = (EditText) findViewById(R.id.con_passwd_edit);
		registerBtn = (Button) findViewById(R.id.btnregister);
		avatarImgV.setOnClickListener(this);
		registerBtn.setOnClickListener(this);
		
		titleText = (TextView)findViewById(R.id.header_htv_subtitle);
		btnBack = (Button)findViewById(R.id.headr_back);
		titleText.setText("详细信息");
		btnBack.setVisibility(View.VISIBLE);
		btnBack.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intentBack = new Intent(Register_detail.this,Register_phone.class);
				intentBack.putExtra("from", "Register_detail");
				startActivity(intentBack);
			}
			
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_pic:
			setHead();
			break;
		case R.id.btnregister:
			login();
			break;
		}
	}

	Bitmap newBitmap;
	boolean isFromCamera = false;// 是否是拍照
	int degree = 0;// 拍照旋转
	String path;// 存图片路径
	boolean isSetAvater = false;

	/**
	 * 设置头像
	 */
	private void setHead() {
		new AlertDialog.Builder(Register_detail.this).setTitle("设置头像")
				.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							avatarFromLocal();
							break;
						case 1:
							avaterFromCamera();
							break;
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();

	}

	/**
	 * 选择本地图片
	 */
	private void avatarFromLocal() {
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				"image/*");
		startActivityForResult(intent,
				Constants.REQUESTCODE_UPLOADAVATAR_LOCATION);
	}

	/**
	 * 拍照
	 */
	@SuppressLint("SimpleDateFormat")
	private void avaterFromCamera() {
		File dir = new File(Constants.MyAvatarDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		// 原图
		File file = new File(dir,
				new SimpleDateFormat("yyMMddHHmmss").format(new Date())
						+ ".png");
		path = file.getAbsolutePath();// 获取相片的保存路径
		Uri imageUri = Uri.fromFile(file);

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(intent,
				Constants.REQUESTCODE_UPLOADAVATAR_CAMERA);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case Constants.REQUESTCODE_UPLOADAVATAR_CAMERA:// 拍照
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					ShowToast("SD不可用");
					return;
				}
				isFromCamera = true;
				File file = new File(path);
				degree = PhotoUtil.readPictureDegree(file.getAbsolutePath());
				Log.i("life", "拍照后的角度：" + degree);
				startImageAction(Uri.fromFile(file), 200, 200,
						Constants.REQUESTCODE_UPLOADAVATAR_CROP, true);
			}
			break;
		case Constants.REQUESTCODE_UPLOADAVATAR_LOCATION:// 本地
			Uri uri = null;
			if (data == null) {// 取消了
				return;
			}
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					ShowToast("SD不可用");
					return;
				}
				isFromCamera = false;
				uri = data.getData();
				startImageAction(uri, 200, 200,
						Constants.REQUESTCODE_UPLOADAVATAR_CROP, true);
			} else {
				ShowToast("照片获取失败");
			}
			break;
		case Constants.REQUESTCODE_UPLOADAVATAR_CROP:// 裁剪头像返回
			if (data == null) {
				return;
			} else {
				saveCropAvator(data);
			}
			// 初始化文件路径
			// filePath = "";
			// 上传头像
			uploadAvatar();
			break;
		default:
			break;

		}
	}

	/**
	 * 裁剪头像
	 * @param uri
	 * @param outputX
	 * @param outputY
	 * @param requestCode
	 * @param isCrop
	 */
	private void startImageAction(Uri uri, int outputX, int outputY,
			int requestCode, boolean isCrop) {
		Intent intent = null;
		if (isCrop) {
			intent = new Intent("com.android.camera.action.CROP");
		} else {
			intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		}
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", true);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, requestCode);
	}

	/**
	 * 保存裁剪的头像
	 * 
	 * @param data
	 */
	@SuppressLint("SimpleDateFormat")
	private void saveCropAvator(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap bitmap = extras.getParcelable("data");
			if (bitmap != null) {
				bitmap = PhotoUtil.toRoundBitmap(bitmap);
				if (isFromCamera && degree != 0) {
					bitmap = PhotoUtil.rotaingImageView(degree, bitmap);
				}
				avatarImgV.setImageBitmap(bitmap);
				isSetAvater = true;
				String filename = new SimpleDateFormat("yyMMddHHmmss")
						.format(new Date()) + ".png";
				path = Constants.MyAvatarDir + filename;// 上传头像时使用
				PhotoUtil.saveBitmap(Constants.MyAvatarDir, filename, bitmap,
						true);
			}
		}
	}

	/**
	 * 上传头像
	 */
	private void uploadAvatar() {
		Log.i("--头像地址：", path);
		final BmobFile bmobFile = new BmobFile(new File(path));
		bmobFile.upload(this, new UploadFileListener() {
			@Override
			public void onSuccess() {
				String url = bmobFile.getFileUrl(Register_detail.this);
				Log.i("--user.setAvatar(url)", url);
				// 更新User对象
				user.setAvatar(url);
				Log.i("--user.setAvatar(url)", user.getAvatar());
			}

			@Override
			public void onProgress(Integer arg0) {
			}

			@Override
			public void onFailure(int arg0, String msg) {
				ShowToast("头像上传失败：" + msg);
			}
		});
	}

	/**
	 * 登陆
	 */
	private void login() {
		String name = nameEdit.getText().toString();
		String passwd = passwdEdit.getText().toString();
		String conpasswd = conpasswdEdit.getText().toString();
		if (!isSetAvater) {
			ShowToast(R.string.toast_error_headpicture_null);
			return;
		}
		if (TextUtils.isEmpty(name)) {
			ShowToast(R.string.toast_error_username_null);
			return;
		}
		if (TextUtils.isEmpty(passwd)) {
			ShowToast(R.string.toast_error_password_null);
			return;
		}
		if (!passwd.equals(conpasswd)) {
			ShowToast(R.string.toast_error_comfirm_password_no_same);
			return;
		}
		String regEx = "^[a-zA-Z][a-zA-Z0-9_]{4,15}$";
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(name);
		boolean b = matcher.matches();
		if (!b) {
			ShowToast(R.string.toast_error_username_illegal);
			return;
		}
		if (passwd.length() < 6) {
			ShowToast(R.string.toast_error_password_too_short);
			return;
		}

		// 检查网络
		boolean isNetWorkConnected = Utils
				.isNetworkAvailable(Register_detail.this);
		if (!isNetWorkConnected) {
			ShowToast(R.string.network_error_not_connected);
			return;
		}

		// 进度条
		final ProgressDialog progress = new ProgressDialog(Register_detail.this);
		progress.setMessage("正在注册...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		//isExist = false;
		BmobQuery<User> bmobQuery = new BmobQuery<User>("User");
		bmobQuery.addWhereEqualTo("username", name);
		bmobQuery.count(Register_detail.this, User.class, new CountListener() {
			@Override
			public void onSuccess(int arg0) {
				if (arg0 != 0) {
					isExist = true;
				} 
			}
			@Override
			public void onFailure(int arg0, String arg1) {
			}
		});

		if (isExist == true) {
			ShowToast(R.string.toast_error_username_exit);
			progress.dismiss();
			return;
		}
		// 用户信息
		user.setUsername(name);
		user.setNick(name);
		user.setPassword(passwd);
		user.setSex(true);
		user.setLocation(geoPointlocation);
		Log.i("--geoPointlocation", geoPointlocation.getLatitude()+""+geoPointlocation.getLongitude());
		// user.setAvatar(arg0);
		user.setFriendAddPolicy("a");
		user.setCharacter("这家伙很懒，什么都没有留下！");
		intent = getIntent();
		String phstring = intent.getStringExtra("phone");
		user.setPhone(phstring);
		user.setDeviceType("android");
		user.setInstallId(BmobInstallation.getInstallationId(this));
		Log.i("--uploadAvatar", "user.getAvatar()");
		user.signUp(Register_detail.this, new SaveListener() {
			@Override
			public void onSuccess() {
				progress.dismiss();
				ShowToast("注册成功");
				// 将设备与username进行绑定
				userManager.bindInstallationForRegister(user.getUsername());
				// 启动登陆
				Intent intent = new Intent(Register_detail.this, Login.class);
				startActivity(intent);
				finish();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				ShowToast("注册失败:" + arg1);
				progress.dismiss();
			}
		});
	}
}
